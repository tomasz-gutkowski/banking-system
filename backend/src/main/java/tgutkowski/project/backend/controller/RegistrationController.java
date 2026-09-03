package tgutkowski.project.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tgutkowski.project.backend.model.dtos.RegistrationRequest;
import tgutkowski.project.backend.repository.AppUserRepository;
import tgutkowski.project.backend.service.RegistrationService;

@RestController
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody RegistrationRequest registrationRequest) {
        String username = registrationRequest.username();
        String password = registrationRequest.password();

        return registrationService.registerUser(username, password);
    }

}
