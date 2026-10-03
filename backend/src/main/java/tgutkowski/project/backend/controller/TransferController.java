package tgutkowski.project.backend.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tgutkowski.project.backend.model.dtos.requests.TransferRequest;
import tgutkowski.project.backend.model.dtos.responses.TransferInfoDto;
import tgutkowski.project.backend.model.dtos.responses.TransferResponse;
import tgutkowski.project.backend.model.entities.AppUser;
import tgutkowski.project.backend.service.TransferService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/transfer")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance(@AuthenticationPrincipal AppUser appUser) {
        BigDecimal balance = transferService.getBalance(appUser);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transferTo(@AuthenticationPrincipal AppUser appUser,
                                             @Valid @RequestBody TransferRequest request) {
        String senderAccNum = request.sender();
        String receiverAccNum = request.receiver();
        BigDecimal amount = request.amount();
        String id = transferService.transferMoney(appUser, senderAccNum, receiverAccNum, amount);
        String message = "Transfer completed successfully";

        return ResponseEntity.ok(new TransferResponse(id, senderAccNum, receiverAccNum, amount, message));
    }


    @GetMapping("/transfers")
    public Page<TransferInfoDto> getTransfers(@AuthenticationPrincipal AppUser appUser,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10")int size) {
        int sizeCapped = Math.min(size, 20);

        String username = appUser.getUsername();
        Pageable pageable = PageRequest.of(page, sizeCapped, Sort.by("date").descending());

        return transferService.getTransfers(username, pageable);
    }
}
