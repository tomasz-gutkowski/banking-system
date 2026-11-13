package tgutkowski.project.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tgutkowski.project.backend.config.OpenApiConfig;
import tgutkowski.project.backend.config.annotations.ApiAuthenticationErrors;
import tgutkowski.project.backend.config.annotations.ApiForbiddenErrors;
import tgutkowski.project.backend.config.annotations.ApiValidationErrors;
import tgutkowski.project.backend.exceptions.dtos.ExceptionResponse;
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

@Tag(name = OpenApiConfig.TagNames.ACCOUNT)

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

        @Operation(summary = "Creates an account for user",
            description = "REQUIRED ROLE [USER] - Creates new account for authenticated user, if said user doesn't currently have other non-closed account")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "409", description = "Account couldn't be created, because user already has an active or frozen account",
                        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        })
        @ApiAuthenticationErrors
        @ApiForbiddenErrors
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
                description = "REQUIRED ROLE [USER] - Returns complete list of all accounts assigned to user with their current status")
        @ApiAuthenticationErrors
        @ApiForbiddenErrors
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
        @ApiResponses(value = {
                @ApiResponse(responseCode = "404", description = "ROLE [USER]: No account with given ID found for the user. ROLE [ADMIN]: No account with given ID found",
                        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        })
        @ApiAuthenticationErrors
        @ApiValidationErrors
    @GetMapping("/status/{accountId}")
    public ResponseEntity<AccountStatusResponse> getStatus(@AuthenticationPrincipal AppUser appUser,
                                                               @NotBlank(message = "Account ID is required")
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
        @ApiResponses(value = {
                @ApiResponse(responseCode = "404", description = "ROLE [USER]: No account with given ID found for the user. ROLE [ADMIN]: No account with given ID found",
                        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                @ApiResponse(responseCode = "409", description = "Illegal state shift requested for account (ex. CLOSE -> FROZEN), or attempt at closing account with funds",
                        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        })
        @ApiAuthenticationErrors
        @ApiValidationErrors
    @PostMapping("/change-status/{accountId}")
    public ResponseEntity<AccountStatusChangeResponse> postChangeStatus(@AuthenticationPrincipal AppUser appUser,
                                                                           @NotBlank(message = "Account ID is required")
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
        @ApiResponses(value = {
                @ApiResponse(responseCode = "404", description = "ROLE [USER]: No account with given ID found for the user or no record found for given cursor. ROLE [ADMIN]: No account with given ID found or no record found for given cursor",
                        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        })
        @ApiAuthenticationErrors
        @ApiValidationErrors
    @GetMapping("/status-change-logs/{accountId}")
    public ResponseEntity<PageResponse<AccountStatusChangeLog, AccountStatusChangeLogInfoDto>> getStatusChangeLogs(@AuthenticationPrincipal AppUser appUser,
                                                                                                                  @NotBlank(message = "Account ID is required")
                                                                                                                 @Pattern(regexp = "^ACC-\\d{8}$",
                                                                                                                    message = "Not an eligible account id")
                                                                                                                 @PathVariable String accountId,
                                                                                                                  @Min(1)
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
                description = "REQUIRED ROLE [USER] - Claims signing bonus on selected account, if it belongs to the user and user haven't already claimed the bonus")
        @ApiResponses(value = {

                @ApiResponse(responseCode = "404", description = "No account with given ID found for the user.",
                        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                @ApiResponse(responseCode = "409", description = "Cannot claim bonus for frozen or closed accounts",
                        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        })
        @ApiAuthenticationErrors
        @ApiForbiddenErrors
        @ApiValidationErrors
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/claim-bonus/{accountId}")
    public ResponseEntity<ClaimBonusResponse> postClaimBonus(@AuthenticationPrincipal AppUser appUser,
                                                         @NotBlank(message = "Account ID is required")
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
