package tgutkowski.project.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tgutkowski.project.backend.model.dtos.requests.LoginRequest;
import tgutkowski.project.backend.model.dtos.requests.RegistrationRequest;
import tgutkowski.project.backend.service.AuthService;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();

        String token = authService.authenticate(username, password);

        return ResponseEntity.ok(token);
    }


    @PostMapping("/register")
    public ResponseEntity<String> postUserRegistration(@Valid @RequestBody RegistrationRequest registrationRequest) {
        String username = registrationRequest.username();
        String password = registrationRequest.password();

        String message = authService.register(username, password);

        return ResponseEntity.ok(message);
    }


}
