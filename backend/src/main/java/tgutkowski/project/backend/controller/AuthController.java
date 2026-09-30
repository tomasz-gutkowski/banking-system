package tgutkowski.project.backend.controller;

import jakarta.validation.Valid;
import org.springframework.boot.jackson.autoconfigure.JacksonProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tgutkowski.project.backend.model.dtos.requests.LoginRequest;
import tgutkowski.project.backend.model.dtos.requests.RegisterRequest;
import tgutkowski.project.backend.model.dtos.responses.LoginResponse;
import tgutkowski.project.backend.model.dtos.responses.RegisterResponse;
import tgutkowski.project.backend.service.AuthService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();

        String token = authService.authenticate(username, password);
        String message = "Logged in successfully";

        return ResponseEntity.ok(new LoginResponse(message, token));
    }


    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> postUserRegistration(@Valid @RequestBody RegisterRequest registerRequest) {
        String username = registerRequest.username();
        String password = registerRequest.password();

        authService.register(username, password);

        String message = "Account created successfully";

        return ResponseEntity.ok(new RegisterResponse(message));
    }


}
