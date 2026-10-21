package tgutkowski.project.backend.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tgutkowski.project.backend.exceptions.ConflictException;
import tgutkowski.project.backend.exceptions.InternalException;
import tgutkowski.project.backend.exceptions.ResourceNotFoundException;
import tgutkowski.project.backend.model.dtos.responses.account.*;
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
    @Value("${system.account.id}")
    private String systemAccountId;

    private final AccountRepository accountRepository;
    private final AppUserRepository appUserRepository;
    private final AccountStatusChangeRequestRepository ascrRepository;
    private final TransferService transferService;

    public AccountService(AccountRepository accountRepository, AppUserRepository appUserRepository,  AccountStatusChangeRequestRepository ascrRepository, TransferService transferService) {
        this.accountRepository = accountRepository;
        this.appUserRepository = appUserRepository;
        this.ascrRepository = ascrRepository;
        this.transferService = transferService;
    }

    private Account findAccountBelongingToUserLocking(AppUser user, String accountId) {
        return accountRepository.findByOwnerAndAccountIdLocking(user, accountId)
                .orElseThrow(() -> new ResourceNotFoundException("No account found for the user and ID"));
    }

    private Account findAccountBelongingToUser(AppUser user, String accountId) {
        return accountRepository.findByOwnerAndAccountId(user, accountId)
                .orElseThrow(() -> new ResourceNotFoundException("No account found for the user and ID"));
    }

    private Account findAccountByAccountIdLocking(String accountId) {
        return accountRepository.findByAccountIdLocking(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("No account found for the ID"));
    }

    private Account findAccountByAccountId(String accountId) {
        return accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("No account found for the ID"));
    }


    @Transactional
    public AccountCreateResponse createAccount(AppUser user) throws ConflictException {
        String username = user.getUsername();

        AppUser owner = appUserRepository.findByUsernameLocking(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        List<AccountStatus> accountStatuses = List.of(AccountStatus.ACTIVE, AccountStatus.FROZEN);
        if(accountRepository.existsByOwner(owner, accountStatuses))
            throw new ConflictException("Account already exists");

        Account account = buildAccount(owner);

        accountRepository.save(account);

        String message = "Account created successfully";

        return new AccountCreateResponse(account.getAccountId(), message);
    }

    @Transactional
    public ClaimBonusResponse claimBonus(AppUser user, String accountId){
        String username = user.getUsername();

        AppUser owner = appUserRepository.findByUsernameLocking(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        String message;

        if(owner.getBonusStatus().equals(BonusStatus.AVAILABLE)){
            Account userAccount = findAccountBelongingToUser(user, accountId);
            Account sysAccount = accountRepository.findByAccountId(systemAccountId)
                    .orElseThrow(() -> new InternalException("There was a problem with your request"));

            transferService.claimBonusTransfer(userAccount, sysAccount);

            owner.setBonusStatus(BonusStatus.CLAIMED);

            message = "Bonus claimed successfully";
        }
        else if(owner.getBonusStatus().equals(BonusStatus.CLAIMED)){
            message = "Your bonus was already claimed";
        }
        else{
            message = "Bonus is unavailable for this user";
        }

        return new ClaimBonusResponse(accountId, message);
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
        Account account;

        if(user.getRole() == Roles.ADMIN) account = findAccountByAccountId(accountId);
        else account = findAccountBelongingToUser(user, accountId);

        return new AccountStatusResponse(account.getAccountId(), account.getAccountStatus());
    }

    @Transactional
    public AccountClosureResponse closeAccount(AppUser user, String accountId, String reason) {
        Account account;

        if(user.getRole() == Roles.ADMIN) account = findAccountByAccountIdLocking(accountId);
        else account = findAccountBelongingToUserLocking(user, accountId);

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

        if(user.getRole() == Roles.ADMIN) account = findAccountByAccountIdLocking(accountId);
        else account = findAccountBelongingToUserLocking(user, accountId);

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

    @Transactional
    public AccountUnfreezeResponse unfreezeAccount(AppUser user, String accountId) {
        Account account;

        if(user.getRole() == Roles.ADMIN) account = findAccountByAccountIdLocking(accountId);
        else account = findAccountBelongingToUserLocking(user, accountId);

        if(!account.getAccountStatus().equals(AccountStatus.FROZEN)) throw new ConflictException("Account is not frozen");
        if(ascrRepository.existsActiveUnfreezeRequests(account)) throw new ConflictException("Unfreeze request is already pending");

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
}
