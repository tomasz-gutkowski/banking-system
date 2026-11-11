package tgutkowski.project.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tgutkowski.project.backend.model.dtos.requests.transfer.TransferRequest;
import tgutkowski.project.backend.model.dtos.responses.paging.PageResponse;
import tgutkowski.project.backend.model.dtos.responses.transfer.BalanceResponse;
import tgutkowski.project.backend.model.dtos.responses.transfer.TransferInfoDto;
import tgutkowski.project.backend.model.dtos.responses.transfer.TransferResponse;
import tgutkowski.project.backend.model.entities.AppUser;
import tgutkowski.project.backend.model.entities.Transfer;
import tgutkowski.project.backend.service.TransferService;

import java.math.BigDecimal;
import java.util.UUID;

@Tag(name = "Transfer management",
        description = "Transfers and funds management")

@RestController
@RequestMapping("/transfer")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }


    @Operation(summary = "Allows user to check current balance for one of their accounts",
            description = "Returns balance for an existing account if it belongs to currently authenticated user")
    @GetMapping("/balance/{accountId}")
    public ResponseEntity<BalanceResponse> getBalance(@AuthenticationPrincipal AppUser appUser,
                                                 @NotBlank(message = "Sender is required")
                                                 @Pattern(regexp = "^ACC-\\d{8}$",
                                                         message = "Not an eligible account id")
                                                 @PathVariable String accountId) {
        BigDecimal balance = transferService.getBalance(appUser, accountId);

        BalanceResponse response = new BalanceResponse(accountId, balance);

        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

    @Operation(summary = "Allows user to create new funds transfer to destined account in a system",
            description = "Attempts to create new transfer from an account belonging to authenticated user, with destination and amount of funds specified in the body")
    @PostMapping("/transfer-funds/{accountId}")
    public ResponseEntity<TransferResponse> postTransfer(@AuthenticationPrincipal AppUser appUser,
                                             @Valid @RequestBody TransferRequest request,
                                             @NotBlank(message = "Sender is required")
                                             @Pattern(regexp = "^ACC-\\d{8}$",
                                                     message = "Not an eligible account id")
                                             @PathVariable String accountId)

    {
        String receiverAccNum = request.receiver();
        BigDecimal amount = request.amount();
        UUID uuid = transferService.transferMoney(appUser, accountId, receiverAccNum, amount);
        String message = "Transfer completed successfully";

        TransferResponse response = new TransferResponse(uuid, accountId, receiverAccNum, amount, message);

        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

    @Operation(summary = "Allows user to get list of transfers connected to their account",
            description = "Returns a part of paginated response with given size (1-20, higher values will be reduced to the max of a range) consisting of transfers information for account belonging to authenticated user. If next page exists, field \"cursor\" in response has a value that can be used as a starting point for next page if passed as \"cursor\" request parameter")
    @GetMapping("/transfers/{accountId}")
    public ResponseEntity<PageResponse<Transfer, TransferInfoDto>> getTransfers(@AuthenticationPrincipal AppUser appUser,
                                                                @NotBlank(message = "Sender is required")
                                                                @Pattern(regexp = "^ACC-\\d{8}$",
                                                                    message = "Not an eligible account id")
                                                                @PathVariable String accountId,
                                                                @Min(1)
                                                                @RequestParam(defaultValue = "10") int size,
                                                                @RequestParam(required = false) UUID cursor
    ) {
        int sizeCapped = Math.min(size, 20);

        PageResponse<Transfer, TransferInfoDto> response = transferService.getTransfers(appUser, accountId, sizeCapped, cursor);


        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(response);
    }
}
