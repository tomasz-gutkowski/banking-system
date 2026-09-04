package tgutkowski.project.backend.service;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tgutkowski.project.backend.exceptions.InvalidArgumentException;
import tgutkowski.project.backend.model.entities.AppUser;
import tgutkowski.project.backend.model.entities.Roles;
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
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        UserDetails userDetails = appUserDetailsService.loadUserByUsername(username);
        return jwtUtil.generateToken(userDetails);
    }

    public String register(String username, String password) throws InvalidArgumentException {
        AppUser appUser = new AppUser();
        if (appUserRepository.findByUsername(username).isPresent()) {
            throw new InvalidArgumentException("Username already taken");
        }
        appUser.setUsername(username);
        appUser.setPassword(passwordEncoder.encode(password));
        appUser.setRole(Roles.USER);
        appUserRepository.save(appUser);
        return "Account created successfully";
    }
}
