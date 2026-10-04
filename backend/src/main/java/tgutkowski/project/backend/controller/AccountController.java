package tgutkowski.project.backend.controller;

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
    @GetMapping("/status/{accountId}")
    public ResponseEntity<AccountStatusResponse> accountStatus(@AuthenticationPrincipal AppUser appUser,
                                                               @PathVariable String accountId) {
        AccountStatusResponse accountStatusResponse = accountService.getAccountStatus(appUser, accountId);
        return ResponseEntity.ok(accountStatusResponse);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/close/{accountId}")
    public ResponseEntity<Boolean> closeAccount(@AuthenticationPrincipal AppUser appUser,
                                          @PathVariable String accountId){
        return ResponseEntity.ok(false);
    }



}
