package tgutkowski.project.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import tgutkowski.project.backend.model.dtos.responses.auth.LogoutResponse;
import tgutkowski.project.backend.model.dtos.responses.auth.RefreshResponse;
import tgutkowski.project.backend.model.dtos.responses.auth.RegisterResponse;
import tgutkowski.project.backend.service.AuthService;

import java.time.Duration;

@Tag(name = "Authentication",
        description = "User registration and authentication management with JWT + Refresh Token based authentication")

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${refresh.token.expiration}")
    private Long refreshTokenExpiration;

    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Logs in an existing user",
        description = "Attempts to log in user with credentials provided in request body, on success returns JWT and attaches \"refresh_token\" for further session authentication")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> postLogin(@Valid @RequestBody LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();

        LoginResponse response = authService.login(username, password);

        String refreshToken = authService.getRefreshToken(username);

        ResponseCookie cookie = refreshTokenCookie(refreshToken);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header("Content-Type", "application/json")
                .body(response);
    }

    @Operation(summary = "Registers new user to the system",
            description = "Registers new account using credentials present in request body")
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> postRegister(@Valid @RequestBody RegisterRequest registerRequest) {
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

    @Operation(summary = "Refreshes JWT based on \"refresh_token\" cookie",
            description = "Sends new JWT in response if \"refresh_token\" cookie is present in request and is currently valid")
    @GetMapping("/refresh")
    public ResponseEntity<RefreshResponse> getRefresh(@CookieValue(name = "refresh_token", required = false)
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

    @Operation(summary = "Logs out the user",
            description = "Invalidates sent \"refresh_token\" cookie and removes it from browser")
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> postLogout(@Parameter(hidden = true)
                                                         @CookieValue(name = "refresh_token", required = false) String refreshToken){
        if (refreshToken == null) {
            throw new InvalidTokenException("Invalid or expired token");
        }

        ResponseCookie cookie = logoutCookie();

        LogoutResponse response = authService.logout(refreshToken);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header("Content-Type", "application/json")
                .body(response);
    }

    private ResponseCookie refreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/auth")
                .maxAge(Duration.ofSeconds(refreshTokenExpiration))
                .build();
    }

    private ResponseCookie logoutCookie(){
        return ResponseCookie.from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/auth")
                .maxAge(0)
                .build();
    }

}
