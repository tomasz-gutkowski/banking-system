package tgutkowski.project.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import tgutkowski.project.backend.model.entities.Account;
import tgutkowski.project.backend.model.entities.AppUser;
import tgutkowski.project.backend.model.entities.enums.AccountStatus;
import tgutkowski.project.backend.model.entities.enums.AccountType;
import tgutkowski.project.backend.model.entities.enums.BonusStatus;
import tgutkowski.project.backend.model.entities.enums.Roles;
import tgutkowski.project.backend.repository.AccountRepository;
import tgutkowski.project.backend.repository.AppUserRepository;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;
import java.util.Optional;

@Configuration
public class InitConfig {

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${system.account.id}")
    private String systemAccountId;

    @Bean
    CommandLineRunner initUsers(AppUserRepository userRepository, AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            addAdminUser(userRepository, passwordEncoder);
            addSystemUserAndAccount(userRepository, accountRepository, passwordEncoder);
        };
    }

    private void addAdminUser(AppUserRepository repository, PasswordEncoder passwordEncoder) {
        String username = adminUsername.toLowerCase(Locale.ROOT);
        if(repository.findByUsername(username).isEmpty()) {
            AppUser admin = new AppUser();
            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Roles.ADMIN);
            admin.setBonusStatus(BonusStatus.UNAVAILABLE);
            repository.save(admin);
        }
    }

    private void addSystemUserAndAccount(AppUserRepository userRepository, AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        Optional<AppUser> systemOptional = userRepository.findByUsername("system");
        AppUser system;
        if(systemOptional.isEmpty()) {
            system = new AppUser();
            system.setUsername("system");

            byte[] bytes = new byte[48];
            new SecureRandom().nextBytes(bytes);
            String random = Base64.getEncoder().encodeToString(bytes);
            system.setPassword(passwordEncoder.encode(random));

            system.setRole(Roles.SYSTEM);
            system.setEnabled(false);
            system.setBonusStatus(BonusStatus.UNAVAILABLE);

            userRepository.save(system);
        } else{
            system = systemOptional.get();
        }

        if(!accountRepository.existsByOwnerAndAccountId(system, systemAccountId)) {
            Account account = new Account();
            account.setAccountId(systemAccountId);
            account.setOwner(system);
            account.setStatus(AccountStatus.ACTIVE);
            account.setType(AccountType.SYSTEM);
            accountRepository.save(account);
        }
    }



}
