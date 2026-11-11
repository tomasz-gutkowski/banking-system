package tgutkowski.project.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

@Tag(name = "Account management",
        description = "Account creation and management")

@RestController
@EnableMethodSecurity
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Creates an account for user",
            description = "Creates new account for authenticated user, if said user doesn't currently have other non-closed account")
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    public ResponseEntity<AccountCreateResponse> postCreate(@AuthenticationPrincipal AppUser appUser) {
        AccountCreateResponse response = accountService.createAccount(appUser);
        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

    @Operation(summary = "Returns the list of all accounts belonging to user and their statuses",
            description = "Returns complete list of all accounts assigned to user with their current status")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/statuses")
    public ResponseEntity<List<AccountStatusResponse>> getStatuses(@AuthenticationPrincipal AppUser appUser) {
        List<AccountStatusResponse> response = accountService.getAccountsStatusList(appUser);
        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

    @Operation(summary = "Returns status for selected account",
            description = "Returns status for requested account, if it belongs to currently authenticated user or request comes from account with an ADMIN role")
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

    @Operation(summary = "Requests given status change on selected account",
            description = "Requests given status change for selected account, either done by account owner or account with ADMIN role")
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

    @Operation(summary = "Returns lists of all status changes requested for selected account",
            description = "Returns a part of paginated response with given size (1-10, higher values will be reduced to the max of a range) consisting of status change requests for selected account. If next page exists, field \"cursor\" in response has a value that can be used as a starting point for next page if passed as \"cursor\" request parameter")
    @GetMapping("/status-change-logs/{accountId}")
    public ResponseEntity<PageResponse<AccountStatusChangeLog, AccountStatusChangeLogInfoDto>> getStatusChangeLogs(@AuthenticationPrincipal AppUser appUser,
                                                                                                                  @NotBlank(message = "Sender is required")
                                                                                                                 @Pattern(regexp = "^ACC-\\d{8}$",
                                                                                                                    message = "Not an eligible account id")
                                                                                                                 @PathVariable String accountId,
                                                                                                                  @Size(min = 1)
                                                                                                                  @RequestParam(defaultValue = "10") int size,
                                                                                                                  @RequestParam(required = false) UUID cursor){
        int sizeCapped = Math.min(size, 10);
        PageResponse<AccountStatusChangeLog, AccountStatusChangeLogInfoDto> response = accountService.getChangeLogs(appUser, accountId, sizeCapped, cursor);

        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(response);
    }
    @Operation(summary = "Allows user to claim signing bonus for selected account",
            description = "Claims signing bonus on selected account, if it belongs to the user and user haven't already claimed the bonus")
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
