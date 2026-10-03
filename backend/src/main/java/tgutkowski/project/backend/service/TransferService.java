package tgutkowski.project.backend.service;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tgutkowski.project.backend.exceptions.AccountNotEligibleException;
import tgutkowski.project.backend.exceptions.InsufficientBalanceException;
import tgutkowski.project.backend.exceptions.ResourceNotFoundException;
import tgutkowski.project.backend.model.dtos.responses.TransferInfoDto;
import tgutkowski.project.backend.model.entities.Account;
import tgutkowski.project.backend.model.entities.AppUser;
import tgutkowski.project.backend.model.entities.Transfer;
import tgutkowski.project.backend.model.entities.enums.AccountStatus;
import tgutkowski.project.backend.model.entities.enums.AccountType;
import tgutkowski.project.backend.model.entities.enums.TransferStatus;
import tgutkowski.project.backend.repository.AccountRepository;
import tgutkowski.project.backend.repository.TransferRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;

    public TransferService(TransferRepository transferRepository, AccountRepository accountRepository) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public BigDecimal getBalance(AppUser appUser) {
        Account acc = accountRepository.findByOwnerLocking(appUser)
                .orElseThrow(() -> new ResourceNotFoundException("No account found for the user"));

        String accNum = acc.getAccountNumber();

        return balanceFromAccNum(accNum);
    }

    @Transactional
    public String transferMoney(AppUser sender, String senderAccNum, String receiverAccNum, BigDecimal amount) {
        Account senderAcc = accountRepository.findByOwnerLocking(sender)
                .orElseThrow(() -> new ResourceNotFoundException("No account found for the sender number"));
        Account receiverAcc = accountRepository.findByAccountNumberLocking(receiverAccNum)
                .orElseThrow(() -> new ResourceNotFoundException("No account found for the receiver number"));

        if(!senderAcc.getAccountNumber().equals(senderAccNum)) throw new ResourceNotFoundException("No such account exists for an user");

        BigDecimal senderBalance =  balanceFromAccNum(senderAccNum);

        verifyTransfer(senderAcc, receiverAcc, senderBalance, amount);

        Transfer transfer = new Transfer();
        transfer.setAmount(amount);
        transfer.setSender(senderAcc);
        transfer.setReceiver(receiverAcc);
        transfer.setDate(LocalDateTime.now());
        transfer.setStatus(TransferStatus.COMPLETED);
        transferRepository.save(transfer);

        //TODO: currently returning transaction ID from database, but independent transaction identifier would work better
        return transfer.getId().toString();
    }

    private BigDecimal balanceFromAccNum(String accNum){
        BigDecimal sent = transferRepository.sumSent(accNum);
        BigDecimal received = transferRepository.sumReceived(accNum);

        return received.subtract(sent);
    }

    private void verifyTransfer(Account sender, Account receiver, BigDecimal balance, BigDecimal amount) throws AccountNotEligibleException, InsufficientBalanceException {
        if(sender.equals(receiver))
            throw new AccountNotEligibleException("Sender and receiver are identical");

        if(receiver.getAccountType() == AccountType.SYSTEM)
            throw new AccountNotEligibleException("Receiver account is not eligible");

        if(sender.getAccountType() !=  AccountType.SYSTEM) {
            if (balance.compareTo(amount) < 0)
                throw new InsufficientBalanceException("Insufficient funds for a transfer");
            if (!sender.getAccountStatus().equals(AccountStatus.ACTIVE))
                throw new AccountNotEligibleException("Sender account is not active");
        }

        if(!receiver.getAccountStatus().equals(AccountStatus.ACTIVE))
            throw new AccountNotEligibleException("Sender account is not active");

    }

    @Transactional
    public Page<TransferInfoDto> getTransfers(String username, Pageable pageable) {
        Account account = accountRepository.findByOwnerUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("No account found for the user"));
        if(account.getAccountStatus().equals(AccountStatus.CLOSED)) throw new AccountNotEligibleException("Account is closed");

        String accNum = account.getAccountNumber();
        Page<Transfer> transfers = transferRepository.findAllTransfersForAccount(accNum, pageable);

        return transfers.map(TransferInfoDto::from);
    }

}
