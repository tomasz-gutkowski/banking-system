package tgutkowski.project.backend.service;

import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tgutkowski.project.backend.exceptions.ConflictException;
import tgutkowski.project.backend.model.entities.Account;
import tgutkowski.project.backend.model.entities.AppUser;
import tgutkowski.project.backend.model.entities.enums.AccountStatus;
import tgutkowski.project.backend.model.entities.enums.AccountType;
import tgutkowski.project.backend.repository.AccountRepository;
import tgutkowski.project.backend.repository.AppUserRepository;

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
    public String createAccount(UserDetails userDetails) throws ConflictException {
        String username = userDetails.getUsername();
        if(accountRepository.existsByOwnerUsername(username)) throw new ConflictException("Account already exists");
        AppUser user = appUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        buildAccount(user);
        return "Account created successfully";
    }

    private void buildAccount(AppUser owner) {
        Account account = new Account();
        account.setOwner(owner);
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setStatus(AccountStatus.ACTIVE);
        account.setType(AccountType.USER);
        accountRepository.save(account);
    }

    private String generateUniqueAccountNumber(){
        String number = generateAccountNumber();
        while(accountRepository.existsByAccountNumber(number)){
            number = generateAccountNumber();
        }
        return number;
    }

    private String generateAccountNumber(){
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append("ACC-");
        for(int i = 0; i < 8; i++){
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
