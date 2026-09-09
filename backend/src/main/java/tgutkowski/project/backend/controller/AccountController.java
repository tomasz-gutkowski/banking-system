package tgutkowski.project.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tgutkowski.project.backend.model.entities.AppUser;
import tgutkowski.project.backend.repository.AccountRepository;
import tgutkowski.project.backend.service.AccountService;

@RestController
@RequestMapping("/acc")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public ResponseEntity<String> createAccount(@AuthenticationPrincipal UserDetails userDetails) {
        String message = accountService.createAccount(userDetails);
        return ResponseEntity.ok(message);
    }

}
