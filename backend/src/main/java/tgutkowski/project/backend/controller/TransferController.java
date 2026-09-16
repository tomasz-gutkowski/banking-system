package tgutkowski.project.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tgutkowski.project.backend.model.dtos.requests.TransferRequest;
import tgutkowski.project.backend.model.dtos.responses.BalanceResponse;
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
    public ResponseEntity<BalanceResponse> getBalance(@AuthenticationPrincipal AppUser appUser) {
        String username = appUser.getUsername();
        BigDecimal balance = transferService.getBalance(appUser);
        return ResponseEntity.ok(new BalanceResponse(username, balance));
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendTo(@AuthenticationPrincipal AppUser appUser, @Valid @RequestBody TransferRequest request) {
        String senderUsername = appUser.getUsername();
        String receiverAccNum = request.receiver();
        BigDecimal amount = request.amount();
        String message = transferService.transferMoney(senderUsername, receiverAccNum, amount);
        return ResponseEntity.ok(message);
    }
}
