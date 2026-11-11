package tgutkowski.project.backend.service;



import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tgutkowski.project.backend.exceptions.ConflictException;
import tgutkowski.project.backend.exceptions.InvalidArgumentException;
import tgutkowski.project.backend.exceptions.InvalidCredentialsException;
import tgutkowski.project.backend.exceptions.InvalidTokenException;
import tgutkowski.project.backend.model.dtos.responses.auth.LoginResponse;
import tgutkowski.project.backend.model.dtos.responses.auth.LogoutResponse;
import tgutkowski.project.backend.model.dtos.responses.auth.RefreshResponse;
import tgutkowski.project.backend.model.entities.AppUser;
import tgutkowski.project.backend.model.entities.enums.Roles;
import tgutkowski.project.backend.repository.AppUserRepository;
import tgutkowski.project.backend.service.authtokens.jwt.JwtUtil;
import tgutkowski.project.backend.service.authtokens.refresh.RefreshTokenStore;

import java.util.Optional;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenStore refreshTokenStore;

    public AuthService(AppUserRepository appUserRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       RefreshTokenStore refreshTokenStore) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenStore = refreshTokenStore;
    }

    private AppUser authenticate(String username, String password){
        boolean userExists = true;
        Optional<AppUser> optUser = appUserRepository.findByUsername(username);

        AppUser user;
        if(optUser.isPresent()){
            user = optUser.get();
        } else{
            userExists = false;
            user = new AppUser();
            user.setUsername("dummyUsername");
            user.setPassword("$2a$10$LmVnwADLutehl.3detBZruBbanaSzw96WyP.bohxWSFCT9FrEeS6C");
        }

        String expectedPassword = user.getPassword();

        if(!passwordEncoder.matches(password, expectedPassword)) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        if(!user.isEnabled() || !user.isAccountNonLocked() || !user.isAccountNonExpired()) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        if(!userExists) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        return user;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(String username, String password) {
        AppUser user = authenticate(username, password);

        String jwt = jwtUtil.generateToken(user);

        String message = "Login successful";
        return new LoginResponse(jwt, message);
    }

    @Transactional(readOnly = true)
    public String getRefreshToken(String username) {
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + username));

        return refreshTokenStore.issueToken(appUser.getId());
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
        try {
            appUserRepository.save(appUser);
        }catch(DataIntegrityViolationException e){
            throw new ConflictException("Username already taken");
        }
    }

    @Transactional
    public RefreshResponse refreshJwt(String refreshToken) {
        Long ownerId = refreshTokenStore.findTokenOwner(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired refresh token"));

        AppUser appUser = appUserRepository.findById(ownerId)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired refresh token"));

        String jwt = jwtUtil.generateToken(appUser);
        String message = "Refresh successful";

        return new RefreshResponse(jwt, message);
    }

    public LogoutResponse logout(String token) {
        refreshTokenStore.invalidateToken(token);
        String message = "Logout successful";
        return new LogoutResponse(message);
    }
}

