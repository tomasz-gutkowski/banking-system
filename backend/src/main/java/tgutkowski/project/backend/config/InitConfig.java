package tgutkowski.project.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import tgutkowski.project.backend.model.entities.AppUser;
import tgutkowski.project.backend.model.entities.Roles;
import tgutkowski.project.backend.repository.AppUserRepository;

@Configuration
public class InitConfig {

    @Bean
    CommandLineRunner initUsers(AppUserRepository repository,  PasswordEncoder passwordEncoder) {
        return args -> {
            if(repository.findByUsername("admin").isEmpty()) {
                AppUser admin = new AppUser();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("password"));
                admin.setRole(Roles.ADMIN);
                repository.save(admin);
            }
        };
    }
}
