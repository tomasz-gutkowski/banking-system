package tgutkowski.project.backend.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tgutkowski.project.backend.exceptions.InvalidTokenException;
import tgutkowski.project.backend.model.dtos.requests.auth.LoginRequest;
import tgutkowski.project.backend.model.dtos.requests.auth.RegisterRequest;
import tgutkowski.project.backend.model.dtos.responses.auth.LoginResponse;
import tgutkowski.project.backend.model.dtos.responses.auth.RefreshResponse;
import tgutkowski.project.backend.model.dtos.responses.auth.RegisterResponse;
import tgutkowski.project.backend.service.AuthService;

import java.time.Duration;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${refresh.token.expiration}")
    private Long refreshTokenExpiration;

    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();

        LoginResponse response = authService.login(username, password);

        String refreshToken = authService.getRefreshToken(username);

        ResponseCookie cookie = getRefreshTokenCookie(refreshToken);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header("Content-Type", "application/json")
                .body(response);
    }


    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> postUserRegistration(@Valid @RequestBody RegisterRequest registerRequest) {
        String username = registerRequest.username();
        String password = registerRequest.password();

        authService.register(username, password);

        String message = "Account created successfully";

        RegisterResponse response = new RegisterResponse(message);

        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

    @GetMapping("/refresh")
    public ResponseEntity<RefreshResponse> refreshToken(@CookieValue(name = "refresh_token", required = false)
                                                            String refreshToken) {
        if (refreshToken == null) {
            throw new InvalidTokenException("Invalid or expired token");
        }

        RefreshResponse refreshResponse = authService.refreshJwt(refreshToken);

        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(refreshResponse);
    }

    private ResponseCookie getRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/auth")
                .maxAge(Duration.ofSeconds(refreshTokenExpiration))
                .build();
    }

}
