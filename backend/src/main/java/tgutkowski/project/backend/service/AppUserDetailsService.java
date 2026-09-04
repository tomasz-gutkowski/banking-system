package tgutkowski.project.backend.service;

import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tgutkowski.project.backend.model.entities.AppUser;
import tgutkowski.project.backend.repository.AppUserRepository;

import java.util.Optional;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public AppUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> user = appUserRepository.findByUsername(username);
        if (user.isEmpty()) throw new UsernameNotFoundException(username);

        AppUser appUser = user.get();

        return User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword())
                .roles(appUser.getRole().name())
                .build();
    }
}
