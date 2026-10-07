package tgutkowski.project.backend.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.context.annotation.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tgutkowski.project.backend.model.dtos.responses.account.AccountCreateResponse;
import tgutkowski.project.backend.model.dtos.responses.account.AccountStatusResponse;
import tgutkowski.project.backend.model.entities.AppUser;
import tgutkowski.project.backend.service.AccountService;

import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    public ResponseEntity<AccountCreateResponse> createAccount(@AuthenticationPrincipal AppUser appUser) {

        String accountNumber = accountService.createAccount(appUser);
        String message = "Account created successfully";
        return ResponseEntity.ok(new AccountCreateResponse(accountNumber, message));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/statuses")
    public ResponseEntity<List<AccountStatusResponse>> accountStatuses(@AuthenticationPrincipal AppUser appUser) {
        List<AccountStatusResponse> accountStatusList = accountService.getAccountsStatusList(appUser);
        return ResponseEntity.ok(accountStatusList);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/status/{accountId}")
    public ResponseEntity<AccountStatusResponse> accountStatus(@AuthenticationPrincipal AppUser appUser,
                                                               @NotBlank(message = "Sender is required")
                                                               @Pattern(regexp = "^ACC-\\d{8}$",
                                                                   message = "Not an eligible account id")
                                                               @PathVariable String accountId) {
        AccountStatusResponse accountStatusResponse = accountService.getAccountStatus(appUser, accountId);
        return ResponseEntity.ok(accountStatusResponse);
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/close/{accountId}")
    public ResponseEntity<Boolean> closeAccount(@AuthenticationPrincipal AppUser appUser,
                                                @NotBlank(message = "Sender is required")
                                                @Pattern(regexp = "^ACC-\\d{8}$",
                                                        message = "Not an eligible account id")
                                                @PathVariable String accountId){
        return ResponseEntity.ok(false);
    }





}
