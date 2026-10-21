package tgutkowski.project.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tgutkowski.project.backend.model.dtos.requests.auth.LoginRequest;
import tgutkowski.project.backend.model.dtos.requests.auth.RegisterRequest;
import tgutkowski.project.backend.model.dtos.responses.auth.LoginResponse;
import tgutkowski.project.backend.model.dtos.responses.auth.RegisterResponse;
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
        LoginResponse response = new LoginResponse(token, message);

        return ResponseEntity
                .ok()
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


}
