package tgutkowski.project.backend.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Limit;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tgutkowski.project.backend.exceptions.ConflictException;
import tgutkowski.project.backend.exceptions.InternalException;
import tgutkowski.project.backend.exceptions.InvalidArgumentException;
import tgutkowski.project.backend.exceptions.ResourceNotFoundException;
import tgutkowski.project.backend.model.dtos.responses.account.*;
import tgutkowski.project.backend.model.dtos.responses.paging.PageResponse;
import tgutkowski.project.backend.model.entities.Account;
import tgutkowski.project.backend.model.entities.AccountStatusChangeLog;
import tgutkowski.project.backend.model.entities.AppUser;
import tgutkowski.project.backend.model.entities.enums.*;
import tgutkowski.project.backend.repository.AccountRepository;
import tgutkowski.project.backend.repository.AccountStatusChangeLogRepository;
import tgutkowski.project.backend.repository.AppUserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class AccountService {
    private final AccountStatusChangeLogRepository accountStatusChangeLogRepository;
    @Value("${system.account.id}")
    private String systemAccountId;

    private final AccountRepository accountRepository;
    private final AppUserRepository appUserRepository;
    private final AccountStatusChangeLogRepository asclRepository;
    private final TransferService transferService;

    public AccountService(AccountRepository accountRepository, AppUserRepository appUserRepository, AccountStatusChangeLogRepository asclRepository, TransferService transferService, AccountStatusChangeLogRepository accountStatusChangeLogRepository) {
        this.accountRepository = accountRepository;
        this.appUserRepository = appUserRepository;
        this.asclRepository = asclRepository;
        this.transferService = transferService;
        this.accountStatusChangeLogRepository = accountStatusChangeLogRepository;
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
            Account userAccount = findAccountBelongingToUserLocking(user, accountId);
            Account sysAccount = accountRepository.findByAccountId(systemAccountId)
                    .orElseThrow(() -> new InternalException("There was a problem with your request"));

            if(userAccount.getAccountStatus().equals(AccountStatus.CLOSED)){
                throw new ConflictException("Your account is closed");
            }

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
    public AccountStatusChangeResponse changeAccountStatus(AppUser user, String accountId, StatusChange statusChange, String reason) {
        Account account;

        if(user.getRole() == Roles.ADMIN) account = findAccountByAccountIdLocking(accountId);
        else account = findAccountBelongingToUserLocking(user, accountId);

        switch(statusChange) {
            case CLOSE:{
                if(account.getAccountStatus().equals(AccountStatus.CLOSED)) throw new ConflictException("Account is already closed");
                break;
            }
            case FREEZE:{
                if(account.getAccountStatus().equals(AccountStatus.CLOSED)) throw new ConflictException("Cannot freeze closed account");
                if(account.getAccountStatus().equals(AccountStatus.FROZEN)) throw new ConflictException("Account is already frozen");
                break;
            }
            case UNFREEZE:{
                if(!account.getAccountStatus().equals(AccountStatus.FROZEN)) throw new ConflictException("Account is not frozen");
                if(asclRepository.existsActiveUnfreezeRequests(account)) throw new ConflictException("Unfreeze request is already pending");
                break;
            }
        }

        AccountStatusChangeLog ascl = new AccountStatusChangeLog();
        String message = "";
        LocalDateTime now = LocalDateTime.now();
        ascl.setAccount(account);
        ascl.setRequestedBy(user);
        ascl.setRequestDate(now);
        ascl.setReason(reason);

        switch(statusChange) {
            case CLOSE, FREEZE:{
                if(user.getRole() == Roles.ADMIN){
                    ascl.setResolutionType(ResolutionType.MANUAL);
                    ascl.setResolutionStatus(ResolutionStatus.ACCEPTED);
                    ascl.setResolvedBy(user);
                }
                else{
                    ascl.setResolutionType(ResolutionType.AUTO);
                    ascl.setResolutionStatus(ResolutionStatus.ACCEPTED);
                }
                ascl.setResolutionDate(now);
                ascl.setRequestType(statusChange);
                if(statusChange.equals(StatusChange.CLOSE)){
                    account.setStatus(AccountStatus.CLOSED);
                    message = "Account closed successfully";
                }
                if(statusChange.equals(StatusChange.FREEZE)){
                    account.setStatus(AccountStatus.FROZEN);
                    message = "Account frozen successfully";
                }
                break;
            }
            case UNFREEZE:{
                ascl.setRequestedBy(user);
                ascl.setRequestDate(now);
                ascl.setRequestType(StatusChange.UNFREEZE);
                ascl.setResolutionType(ResolutionType.MANUAL);
                if(user.getRole() == Roles.ADMIN){
                    ascl.setResolvedBy(user);
                    ascl.setResolutionStatus(ResolutionStatus.ACCEPTED);
                    ascl.setResolutionDate(now);

                    message = "Account unfrozen successfully";

                    account.setStatus(AccountStatus.ACTIVE);
                }
                else{
                    ascl.setResolutionStatus(ResolutionStatus.PENDING);

                    message = "Account unfreeze request created";
                }
                break;
            }
        }
        asclRepository.save(ascl);

        return new AccountStatusChangeResponse(account.getAccountId(), message);
    }

    @Transactional
    public PageResponse<AccountStatusChangeLog, AccountStatusChangeLogInfoDto> getChangeLogs(AppUser user,
                                                                                             String accountId,
                                                                                             int size,
                                                                                             UUID cursor) {
        Account account;
        if(user.getRole() == Roles.ADMIN) account = findAccountByAccountId(accountId);
        else account = findAccountBelongingToUser(user, accountId);

        Limit limit = Limit.of(size+1);


        Long cursorId = null;
        if(cursor != null){
            cursorId = accountStatusChangeLogRepository.getIdByAccountAndCursor(account, cursor)
                    .orElseThrow(() -> new InvalidArgumentException("Invalid cursor"));
        }

        List<AccountStatusChangeLog> accountStatusChangeLogs = accountStatusChangeLogRepository.getLogsPage(account, cursorId, limit);


        PageResponse<AccountStatusChangeLog, AccountStatusChangeLogInfoDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(accountStatusChangeLogs,size);

        return pageResponse;
    }

    @Transactional
    public PageResponse<AccountStatusChangeLog, AccountStatusChangeLogInfoDto> getChangeTickets(StatusChange requestType,
                                                                                                ResolutionStatus resolutionStatus,
                                                                                                int size,
                                                                                                UUID cursor) {
        Limit limit = Limit.of(size+1);

        Long cursorId = null;
        if(cursor != null){
            cursorId = accountStatusChangeLogRepository.getIdByCursor(cursor)
                    .orElseThrow(() -> new InvalidArgumentException("Invalid cursor"));
        }

        List<AccountStatusChangeLog> accountStatusChangeLogs = accountStatusChangeLogRepository.getLogsTicketPage(requestType, resolutionStatus, cursorId, limit);

        PageResponse<AccountStatusChangeLog, AccountStatusChangeLogInfoDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(accountStatusChangeLogs,size);

        return pageResponse;
    }

    @Transactional
    public AccountStatusChangeLogInfoDto resolveTicket(AppUser user, UUID uuid, ResolutionStatus resolutionStatus) {
        AccountStatusChangeLog ascl = accountStatusChangeLogRepository.findByUuidLocking(uuid)
                .orElseThrow(() -> new InvalidArgumentException("No ticket with selected UUID"));

        String accountId = ascl.getAccount().getAccountId();

        Account account = accountRepository.findByAccountIdLocking(accountId)
                .orElseThrow(() -> new InternalException("No account assigned to a ticket"));

        if(!ascl.getResolutionStatus().equals(ResolutionStatus.PENDING)) throw new ConflictException("Selected ticket already resolved");

        if(!resolutionStatus.equals(ResolutionStatus.ACCEPTED) && !resolutionStatus.equals(ResolutionStatus.DECLINED))
            throw new InvalidArgumentException("Invalid resolution");

        if(!ascl.getResolutionType().equals(ResolutionType.MANUAL)) throw new InvalidArgumentException("Invalid resolution type");

        switch(ascl.getRequestType()){
            case CLOSE:{
                if(account.getAccountStatus().equals(AccountStatus.CLOSED)) throw new ConflictException("Account status not matching request");
                if(resolutionStatus.equals(ResolutionStatus.ACCEPTED)) account.setStatus(AccountStatus.CLOSED);
                break;
            }
            case FREEZE:{
                if(!account.getAccountStatus().equals(AccountStatus.ACTIVE)) throw new ConflictException("Account status not matching request");
                if(resolutionStatus.equals(ResolutionStatus.ACCEPTED)) account.setStatus(AccountStatus.FROZEN);
                break;
            }
            case UNFREEZE:{
                if(!account.getAccountStatus().equals(AccountStatus.FROZEN)) throw new ConflictException("Account status not matching request");
                if(resolutionStatus.equals(ResolutionStatus.ACCEPTED)) account.setStatus(AccountStatus.ACTIVE);
                break;
            }
        }
        LocalDateTime now = LocalDateTime.now();
        ascl.setResolutionDate(now);
        ascl.setResolvedBy(user);
        ascl.setResolutionStatus(resolutionStatus);

        return ascl.convertToDTO();
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
