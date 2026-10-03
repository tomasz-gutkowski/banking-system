package tgutkowski.project.backend.controller;

import org.springframework.context.annotation.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tgutkowski.project.backend.model.dtos.responses.AccountCreateResponse;
import tgutkowski.project.backend.model.dtos.responses.AccountStatusResponse;
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

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public ResponseEntity<AccountCreateResponse> createAccount(@AuthenticationPrincipal AppUser appUser) {

        String accountNumber = accountService.createAccount(appUser);
        String message = "Account created successfully";
        return ResponseEntity.ok(new AccountCreateResponse(accountNumber, message));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/statuses")
    public ResponseEntity<List<AccountStatusResponse>> accountStatuses(@AuthenticationPrincipal AppUser appUser) {
        List<AccountStatusResponse> accountStatusList = accountService.getAccountsStatusList(appUser);
        return ResponseEntity.ok(accountStatusList);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/status/{accountNum}")
    public ResponseEntity<AccountStatusResponse> accountStatus(@AuthenticationPrincipal AppUser appUser,
                                                               @PathVariable String accountNum) {
        AccountStatusResponse accountStatusResponse = accountService.getAccountStatus(appUser, accountNum);
        return ResponseEntity.ok(accountStatusResponse);
    }




}
