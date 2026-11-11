package tgutkowski.project.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tgutkowski.project.backend.model.dtos.requests.account.*;
import tgutkowski.project.backend.model.dtos.responses.account.*;
import tgutkowski.project.backend.model.dtos.responses.account.AccountStatusChangeLogInfoDto;
import tgutkowski.project.backend.model.dtos.responses.paging.PageResponse;
import tgutkowski.project.backend.model.entities.AccountStatusChangeLog;
import tgutkowski.project.backend.model.entities.AppUser;
import tgutkowski.project.backend.model.entities.enums.StatusChange;
import tgutkowski.project.backend.service.AccountService;

import java.util.List;
import java.util.UUID;

@RestController
@EnableMethodSecurity
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    public ResponseEntity<AccountCreateResponse> postCreate(@AuthenticationPrincipal AppUser appUser) {
        AccountCreateResponse response = accountService.createAccount(appUser);
        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/statuses")
    public ResponseEntity<List<AccountStatusResponse>> getStatuses(@AuthenticationPrincipal AppUser appUser) {
        List<AccountStatusResponse> response = accountService.getAccountsStatusList(appUser);
        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

    @GetMapping("/status/{accountId}")
    public ResponseEntity<AccountStatusResponse> getStatus(@AuthenticationPrincipal AppUser appUser,
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

    @PostMapping("/change-status/{accountId}")
    public ResponseEntity<AccountStatusChangeResponse> postChangeStatus(@AuthenticationPrincipal AppUser appUser,
                                                                           @NotBlank(message = "Sender is required")
                                                                           @Pattern(regexp = "^ACC-\\d{8}$",
                                                                                   message = "Not an eligible account id")
                                                                           @PathVariable String accountId,
                                                                           @Valid @RequestBody AccountStatusChangeRequest request){
        StatusChange statusChange = request.statusChange();
        String reason = request.reason();

        AccountStatusChangeResponse response = accountService.changeAccountStatus(appUser, accountId, statusChange, reason);

        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

    @GetMapping("/status-change-logs/{accountId}")
    public ResponseEntity<PageResponse<AccountStatusChangeLog, AccountStatusChangeLogInfoDto>> getStatusChangeLogs(@AuthenticationPrincipal AppUser appUser,
                                                                                                                  @NotBlank(message = "Sender is required")
                                                                                                                 @Pattern(regexp = "^ACC-\\d{8}$",
                                                                                                                    message = "Not an eligible account id")
                                                                                                                 @PathVariable String accountId,
                                                                                                                  @RequestParam(defaultValue = "10") int size,
                                                                                                                  @RequestParam(required = false) UUID cursor){
        int sizeCapped = Math.min(size, 10);
        PageResponse<AccountStatusChangeLog, AccountStatusChangeLogInfoDto> response = accountService.getChangeLogs(appUser, accountId, sizeCapped, cursor);

        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/claim-bonus/{accountId}")
    public ResponseEntity<ClaimBonusResponse> postClaimBonus(@AuthenticationPrincipal AppUser appUser,
                                                         @NotBlank(message = "Sender is required")
                                                         @Pattern(regexp = "^ACC-\\d{8}$",
                                                                 message = "Not an eligible account id")
                                                         @PathVariable String accountId){
        ClaimBonusResponse response = accountService.claimBonus(appUser, accountId);


        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

}
