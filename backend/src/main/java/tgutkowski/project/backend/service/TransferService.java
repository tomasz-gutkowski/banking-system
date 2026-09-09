package tgutkowski.project.backend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tgutkowski.project.backend.exceptions.ResourceNotFoundException;
import tgutkowski.project.backend.model.entities.Account;
import tgutkowski.project.backend.model.entities.AppUser;
import tgutkowski.project.backend.model.entities.Transfer;
import tgutkowski.project.backend.repository.AccountRepository;
import tgutkowski.project.backend.repository.TransferRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;

    public TransferService(TransferRepository transferRepository, AccountRepository accountRepository) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
    }

    public BigDecimal getBalance(AppUser appUser) {
        Account account = accountRepository.findByOwner(appUser)
                .orElseThrow(() -> new ResourceNotFoundException("No account found for the user"));

        String accNum = account.getAccountNumber();

        BigDecimal sent = transferRepository.sumSent(accNum);
        BigDecimal received = transferRepository.sumReceived(accNum);


        return received.subtract(sent);
    }

}
