package tgutkowski.project.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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

@RestController
@EnableMethodSecurity
@RequestMapping("/transfer")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

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

    @PostMapping("/transfer/{accountId}")
    public ResponseEntity<TransferResponse> transferTo(@AuthenticationPrincipal AppUser appUser,
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


    @GetMapping("/transfers/{accountId}")
    public ResponseEntity<PageResponse<Transfer, TransferInfoDto>> getTransfers(@AuthenticationPrincipal AppUser appUser,
                                                                @NotBlank(message = "Sender is required")
                                                                @Pattern(regexp = "^ACC-\\d{8}$",
                                                                    message = "Not an eligible account id")
                                                                @PathVariable String accountId,
                                                                @Size(min = 1)
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
