package tgutkowski.project.backend.service;


import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tgutkowski.project.backend.exceptions.ConflictException;
import tgutkowski.project.backend.exceptions.InvalidArgumentException;
import tgutkowski.project.backend.exceptions.InvalidCredentialsException;
import tgutkowski.project.backend.model.entities.AppUser;
import tgutkowski.project.backend.model.entities.enums.Roles;
import tgutkowski.project.backend.repository.AppUserRepository;
import tgutkowski.project.backend.service.jwt.JwtUtil;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AppUserDetailsService appUserDetailsService;
    private final JwtUtil jwtUtil;

    public AuthService(AppUserRepository appUserRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       AppUserDetailsService appUserDetailsService,
                       JwtUtil jwtUtil) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.appUserDetailsService = appUserDetailsService;
        this.jwtUtil = jwtUtil;
    }


    public String authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        UserDetails userDetails = appUserDetailsService.loadUserByUsername(username);
        return jwtUtil.generateToken(userDetails);
    }

    @Transactional
    public void register(String username, String password) throws InvalidArgumentException {
        AppUser appUser = new AppUser();
        if (appUserRepository.findByUsername(username).isPresent()) {
            throw new ConflictException("Username already taken");
        }
        appUser.setUsername(username);
        appUser.setPassword(passwordEncoder.encode(password));
        appUser.setRole(Roles.USER);
        appUserRepository.save(appUser);
    }
}
