package tgutkowski.project.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tgutkowski.project.backend.model.dtos.requests.account.AccountClosureRequest;
import tgutkowski.project.backend.model.dtos.requests.account.AccountFreezeRequest;
import tgutkowski.project.backend.model.dtos.requests.account.AccountUnfreezeRequest;
import tgutkowski.project.backend.model.dtos.requests.account.ClaimBonusRequest;
import tgutkowski.project.backend.model.dtos.responses.account.*;
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
        AccountCreateResponse response = accountService.createAccount(appUser);
        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/statuses")
    public ResponseEntity<List<AccountStatusResponse>> accountStatuses(@AuthenticationPrincipal AppUser appUser) {
        List<AccountStatusResponse> response = accountService.getAccountsStatusList(appUser);
        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

    @GetMapping("/status/{accountId}")
    public ResponseEntity<AccountStatusResponse> accountStatus(@AuthenticationPrincipal AppUser appUser,
                                                               @NotBlank(message = "Sender is required")
                                                               @Pattern(regexp = "^ACC-\\d{8}$",
                                                                   message = "Not an eligible account id")
                                                               @PathVariable String accountId) {
        AccountStatusResponse response = accountService.getAccountStatus(appUser, accountId);
        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

    @PostMapping("/close")
    public ResponseEntity<AccountClosureResponse> closeAccount(@AuthenticationPrincipal AppUser appUser,
                                                @Valid @RequestBody AccountClosureRequest request){
        String accountId = request.accountId();
        String reason = request.reason();

        AccountClosureResponse response = accountService.closeAccount(appUser, accountId, reason);

        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

    @PostMapping("/freeze")
    public ResponseEntity<AccountFreezeResponse> freezeAccount(@AuthenticationPrincipal AppUser appUser,
                                          @Valid @RequestBody AccountFreezeRequest request){
        String accountId = request.accountId();
        String reason = request.reason();

        AccountFreezeResponse response = accountService.freezeAccount(appUser, accountId, reason);

        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

    @PostMapping("/unfreeze")
    public ResponseEntity<AccountUnfreezeResponse> unfreezeAccount(@AuthenticationPrincipal AppUser appUser,
                                            @Valid @RequestBody AccountUnfreezeRequest request){
        String accountId = request.accountId();

        AccountUnfreezeResponse response = accountService.unfreezeAccount(appUser, accountId);

        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/claim-bonus")
    public ResponseEntity<ClaimBonusResponse> claimBonus(@AuthenticationPrincipal AppUser appUser,
                                                         @Valid @RequestBody ClaimBonusRequest request){
        String accountId = request.accountId();

        ClaimBonusResponse response = accountService.claimBonus(appUser, accountId);


        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

}
