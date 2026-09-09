package tgutkowski.project.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
