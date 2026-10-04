package tgutkowski.project.backend.service;

import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tgutkowski.project.backend.exceptions.ConflictException;
import tgutkowski.project.backend.exceptions.ResourceNotFoundException;
import tgutkowski.project.backend.model.dtos.responses.account.AccountStatusResponse;
import tgutkowski.project.backend.model.entities.Account;
import tgutkowski.project.backend.model.entities.AppUser;
import tgutkowski.project.backend.model.entities.enums.AccountStatus;
import tgutkowski.project.backend.model.entities.enums.AccountType;
import tgutkowski.project.backend.repository.AccountRepository;
import tgutkowski.project.backend.repository.AppUserRepository;

import java.util.List;
import java.util.Random;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AppUserRepository appUserRepository;

    public AccountService(AccountRepository accountRepository, AppUserRepository appUserRepository) {
        this.accountRepository = accountRepository;
        this.appUserRepository = appUserRepository;
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
}
