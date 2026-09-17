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
import tgutkowski.project.backend.model.dtos.responses.TransferDto;
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

    @PostMapping("/send")
    public ResponseEntity<String> sendTo(@AuthenticationPrincipal AppUser appUser, @Valid @RequestBody TransferRequest request) {
        String senderUsername = appUser.getUsername();
        String receiverAccNum = request.receiver();
        BigDecimal amount = request.amount();
        String message = transferService.transferMoney(senderUsername, receiverAccNum, amount);
        return ResponseEntity.ok(message);
    }


    @GetMapping("/transfers")
    public Page<TransferDto> getTransfers(@AuthenticationPrincipal AppUser appUser,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10")int size) {
        int sizeCapped = Math.min(size, 20);

        String username = appUser.getUsername();
        Pageable pageable = PageRequest.of(page, sizeCapped, Sort.by("date").descending());

        return transferService.getTransfers(username, pageable);
    }
}
