package tgutkowski.project.backend.service;

import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tgutkowski.project.backend.exceptions.ConflictException;
import tgutkowski.project.backend.exceptions.ResourceNotFoundException;
import tgutkowski.project.backend.model.dtos.responses.account.AccountClosureResponse;
import tgutkowski.project.backend.model.dtos.responses.account.AccountFreezeResponse;
import tgutkowski.project.backend.model.dtos.responses.account.AccountStatusResponse;
import tgutkowski.project.backend.model.dtos.responses.account.AccountUnfreezeResponse;
import tgutkowski.project.backend.model.entities.Account;
import tgutkowski.project.backend.model.entities.AccountStatusChangeRequest;
import tgutkowski.project.backend.model.entities.AppUser;
import tgutkowski.project.backend.model.entities.enums.*;
import tgutkowski.project.backend.repository.AccountRepository;
import tgutkowski.project.backend.repository.AccountStatusChangeRequestRepository;
import tgutkowski.project.backend.repository.AppUserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AppUserRepository appUserRepository;
    private final AccountStatusChangeRequestRepository ascrRepository;

    public AccountService(AccountRepository accountRepository, AppUserRepository appUserRepository,  AccountStatusChangeRequestRepository ascrRepository) {
        this.accountRepository = accountRepository;
        this.appUserRepository = appUserRepository;
        this.ascrRepository = ascrRepository;
    }

    @Transactional
    public String createAccount(AppUser user) throws ConflictException {
        String username = user.getUsername();

        //LOCK QUERY
        AppUser owner = appUserRepository.findByUsernameLocking(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        List<AccountStatus> accountStatuses = List.of(AccountStatus.ACTIVE, AccountStatus.FROZEN);
        if(accountRepository.existsByOwner(owner, accountStatuses))
            throw new ConflictException("Account already exists");

        Account account = buildAccount(owner);

        accountRepository.save(account);

        return account.getAccountId();
    }

    private Account buildAccount(AppUser owner) {
        Account account = new Account();
        account.setOwner(owner);
        account.setAccountId(generateUniqueAccountId());
        account.setStatus(AccountStatus.ACTIVE);
        account.setType(AccountType.USER);
        return account;
    }

    private String generateUniqueAccountId(){
        String accId = generateAccountId();
        while(accountRepository.existsByAccountId(accId)){
            accId = generateAccountId();
        }
        return accId;
    }

    private String generateAccountId(){
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append("ACC-");
        for(int i = 0; i < 8; i++){
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    @Transactional
    public List<AccountStatusResponse> getAccountsStatusList(AppUser user){
        List<Account> accounts = accountRepository.findAllByOwner(user);
        return accounts.stream()
                .map(a -> new AccountStatusResponse(a.getAccountId(), a.getAccountStatus()))
                .toList();
    }

    @Transactional
    public AccountStatusResponse getAccountStatus(AppUser user, String accountId){
        Account account = accountRepository.findByOwnerAndAccountId(user, accountId)
                .orElseThrow(() -> new ResourceNotFoundException("No account found for the user and number"));

        return new AccountStatusResponse(account.getAccountId(), account.getAccountStatus());
    }

    @Transactional
    public AccountClosureResponse closeAccount(AppUser user, String accountId, String reason) {
        Account account;

        if(user.getRole() == Roles.ADMIN){
            account = accountRepository.findByAccountIdLocking(accountId)
                    .orElseThrow(() -> new ResourceNotFoundException("No account found for the number"));

        } else {
            account = accountRepository.findByOwnerAndAccountIdLocking(user, accountId)
                    .orElseThrow(() -> new ResourceNotFoundException("No account found for the user and number"));
        }

        if(account.getAccountStatus().equals(AccountStatus.CLOSED)) throw new ConflictException("Account is already closed");

        AccountStatusChangeRequest ascr = new AccountStatusChangeRequest();
        if(user.getRole() == Roles.ADMIN){
            LocalDateTime now = LocalDateTime.now();
            ascr.setAccount(account);
            ascr.setRequestType(StatusChange.CLOSE);
            ascr.setRequestedBy(user);
            ascr.setReason(reason);
            ascr.setRequestDate(now);
            ascr.setResolutionType(ResolutionType.MANUAL);
            ascr.setResolvedBy(user);
            ascr.setResolutionStatus(ResolutionStatus.ACCEPTED);
            ascr.setResolutionDate(now);
        } else {
            LocalDateTime now = LocalDateTime.now();
            ascr.setAccount(account);
            ascr.setRequestType(StatusChange.CLOSE);
            ascr.setRequestedBy(user);
            ascr.setReason(reason);
            ascr.setRequestDate(now);
            ascr.setResolutionType(ResolutionType.AUTO);
            ascr.setResolutionStatus(ResolutionStatus.ACCEPTED);
            ascr.setResolutionDate(now);
        }

        ascrRepository.save(ascr);

        String message = "Account closed successfully";

        account.setStatus(AccountStatus.CLOSED);

        return new AccountClosureResponse(account.getAccountId(), message);
    }

    @Transactional
    public AccountFreezeResponse freezeAccount(AppUser user, String accountId, String reason) {
        Account account;
        if(user.getRole() == Roles.ADMIN){
            account = accountRepository.findByAccountIdLocking(accountId)
                    .orElseThrow(() -> new ResourceNotFoundException("No account found for the number"));
        } else {
            account = accountRepository.findByOwnerAndAccountIdLocking(user, accountId)
                    .orElseThrow(() -> new ResourceNotFoundException("No account found for the user and number"));
        }
        if(account.getAccountStatus().equals(AccountStatus.CLOSED)) throw new ConflictException("Cannot freeze closed account");
        if(account.getAccountStatus().equals(AccountStatus.FROZEN)) throw new ConflictException("Account is already frozen");

        AccountStatusChangeRequest ascr = new AccountStatusChangeRequest();
        if(user.getRole() == Roles.ADMIN){
            LocalDateTime now = LocalDateTime.now();
            ascr.setAccount(account);
            ascr.setRequestType(StatusChange.FREEZE);
            ascr.setRequestedBy(user);
            ascr.setReason(reason);
            ascr.setRequestDate(now);
            ascr.setResolutionType(ResolutionType.MANUAL);
            ascr.setResolvedBy(user);
            ascr.setResolutionStatus(ResolutionStatus.ACCEPTED);
            ascr.setResolutionDate(now);
        } else {
            LocalDateTime now = LocalDateTime.now();
            ascr.setAccount(account);
            ascr.setRequestType(StatusChange.FREEZE);
            ascr.setRequestedBy(user);
            ascr.setReason(reason);
            ascr.setRequestDate(now);
            ascr.setResolutionType(ResolutionType.AUTO);
            ascr.setResolutionStatus(ResolutionStatus.ACCEPTED);
            ascr.setResolutionDate(now);
        }

        ascrRepository.save(ascr);

        String message = "Account frozen successfully";

        account.setStatus(AccountStatus.FROZEN);

        return new AccountFreezeResponse(account.getAccountId(), message);
    }

    public AccountUnfreezeResponse unfreezeAccount(AppUser user, String accountId) {
        Account account;
        if(user.getRole() == Roles.ADMIN){
            account = accountRepository.findByAccountIdLocking(accountId)
                    .orElseThrow(() -> new ResourceNotFoundException("No account found for the number"));
        } else {
            account = accountRepository.findByOwnerAndAccountIdLocking(user, accountId)
                    .orElseThrow(() -> new ResourceNotFoundException("No account found for the user and number"));
        }
        if(!account.getAccountStatus().equals(AccountStatus.FROZEN)) throw new ConflictException("Account is not frozen");
        if(!ascrRepository.existsActiveUnfreezeRequests(account)) throw new ConflictException("Unfreeze request is already pending");

        AccountStatusChangeRequest ascr = new AccountStatusChangeRequest();
        String message;
        if(user.getRole() == Roles.ADMIN){
            LocalDateTime now = LocalDateTime.now();
            ascr.setAccount(account);
            ascr.setRequestType(StatusChange.UNFREEZE);
            ascr.setRequestedBy(user);
            ascr.setRequestDate(now);
            ascr.setResolutionType(ResolutionType.MANUAL);
            ascr.setResolvedBy(user);
            ascr.setResolutionStatus(ResolutionStatus.ACCEPTED);
            ascr.setResolutionDate(now);

            message = "Account unfrozen successfully";

            account.setStatus(AccountStatus.ACTIVE);
        } else {
            LocalDateTime now = LocalDateTime.now();
            ascr.setAccount(account);
            ascr.setRequestType(StatusChange.UNFREEZE);
            ascr.setRequestedBy(user);
            ascr.setRequestDate(now);
            ascr.setResolutionType(ResolutionType.MANUAL);
            ascr.setResolutionStatus(ResolutionStatus.PENDING);

            message = "Account unfreeze request created";
        }

        ascrRepository.save(ascr);

        return new AccountUnfreezeResponse(account.getAccountId(), message);
    }
}
