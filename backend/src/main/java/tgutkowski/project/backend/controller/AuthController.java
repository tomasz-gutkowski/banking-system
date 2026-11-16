package tgutkowski.project.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tgutkowski.project.backend.config.OpenApiConfig;
import tgutkowski.project.backend.config.annotations.ApiValidationErrors;
import tgutkowski.project.backend.exceptions.dtos.ExceptionResponse;
import tgutkowski.project.backend.exceptions.InvalidTokenException;
import tgutkowski.project.backend.model.dtos.requests.auth.LoginRequest;
import tgutkowski.project.backend.model.dtos.requests.auth.RegisterRequest;
import tgutkowski.project.backend.model.dtos.responses.auth.LoginResponse;
import tgutkowski.project.backend.model.dtos.responses.auth.LogoutResponse;
import tgutkowski.project.backend.model.dtos.responses.auth.RefreshResponse;
import tgutkowski.project.backend.model.dtos.responses.auth.RegisterResponse;
import tgutkowski.project.backend.service.AuthService;

import java.time.Duration;

@Tag(name = OpenApiConfig.TagNames.AUTH)

@RestController
@SecurityRequirements()
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    @Value("${refresh.token.expiration.milliseconds}")
    private Long refreshTokenExpiration;

    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }

        @Operation(summary = "Logs in an existing user",
            description = "Attempts to log in user with credentials provided in request body, on success returns JWT and attaches \"refresh_token\" for further session authentication")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Sends initial JWT and sets \"refresh_token\" cookie",
                        content = @Content(schema = @Schema(implementation = LoginResponse.class))),
                @ApiResponse(responseCode = "401", description = "Invalid credentials",
                        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        })
        @ApiValidationErrors
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
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200" , description = "Successful registration",
                        content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
                @ApiResponse(responseCode = "409", description = "Username taken",
                        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        })
        @ApiValidationErrors
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
                description = "Sends new JWT in response if \"refresh_token\" cookie is present in request and is currently valid. \"refresh_token\" value is read from HTTP Cookies")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200" , description = "Successful JWT refresh",
                        content = @Content(schema = @Schema(implementation = RefreshResponse.class))),
                @ApiResponse(responseCode = "401", description = "Invalid or expired token",
                        content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
        })
    @GetMapping("/refresh")
    public ResponseEntity<RefreshResponse> getRefresh(@Parameter(hidden = true)
                                                          @CookieValue(name = "refresh_token", required = false)
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
                description = "Invalidates sent \"refresh_token\" cookie and removes it from browser.  \"refresh_token\" value is read from HTTP Cookies")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Previous \"refresh_token\" cookie removed",
                        content = @Content(schema = @Schema(implementation = LogoutResponse.class))),
                @ApiResponse(responseCode = "401", description = "Invalid or expired token",
                        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        })
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
                .maxAge(Duration.ofMillis(refreshTokenExpiration))
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
