package tgutkowski.project.backend.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tgutkowski.project.backend.model.entities.AppUser;
import tgutkowski.project.backend.model.entities.Roles;
import tgutkowski.project.backend.repository.AppUserRepository;

@Service
public class RegistrationService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String registerUser(String username, String password) {
        AppUser appUser = new AppUser();
        if (appUserRepository.findByUsername(username).isPresent()) {
            return "User already exists";
        }
        appUser.setUsername(username);
        appUser.setPassword(passwordEncoder.encode(password));
        appUser.setRole(Roles.USER);
        appUserRepository.save(appUser);
        return "Success!";
    }
}
