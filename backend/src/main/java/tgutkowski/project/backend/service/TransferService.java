package tgutkowski.project.backend.service;

import jakarta.transaction.Transactional;

import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tgutkowski.project.backend.exceptions.AccountNotEligibleException;
import tgutkowski.project.backend.exceptions.InsufficientBalanceException;
import tgutkowski.project.backend.exceptions.ResourceNotFoundException;
import tgutkowski.project.backend.model.dtos.responses.transfer.TransferInfoDto;
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


    @Value("${starting.bonus.value}")
    @PositiveOrZero
    private BigDecimal startingBonusValue;

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;

    public TransferService(TransferRepository transferRepository, AccountRepository accountRepository) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
    }

    private Account findAccountBelongingToUserLocking(AppUser user, String accountId) {
        return accountRepository.findByOwnerAndAccountIdLocking(user, accountId)
                .orElseThrow(() -> new ResourceNotFoundException("No account found for the user and ID"));
    }

    @Transactional
    public void claimBonusTransfer(Account account, Account systemAccount) {
        Transfer transfer = new Transfer();
        transfer.setAmount(startingBonusValue);
        transfer.setSender(systemAccount);
        transfer.setReceiver(account);
        transfer.setStatus(TransferStatus.COMPLETED);
        transfer.setDate(LocalDateTime.now());

        transferRepository.save(transfer);
    }

    @Transactional
    public BigDecimal getBalance(AppUser appUser, String accountId) {
        Account account = findAccountBelongingToUserLocking(appUser, accountId);

        return balanceForAccount(account);
    }

    @Transactional
    public String transferMoney(AppUser sender, String senderAccountId, String receiverAccountId, BigDecimal amount) {
        Account senderAcc;
        Account receiverAcc;
        if (senderAccountId.compareTo(receiverAccountId) < 0) {
            senderAcc = findAccountBelongingToUserLocking(sender, receiverAccountId);
            receiverAcc = accountRepository.findByAccountIdLocking(receiverAccountId)
                    .orElseThrow(() -> new ResourceNotFoundException("No account found for the receiver ID"));
        } else {
            receiverAcc = accountRepository.findByAccountIdLocking(receiverAccountId)
                    .orElseThrow(() -> new ResourceNotFoundException("No account found for the receiver ID"));
            senderAcc = findAccountBelongingToUserLocking(sender, receiverAccountId);
        }

        BigDecimal senderBalance =  balanceForAccount(senderAcc);

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

    @Transactional
    public Page<TransferInfoDto> getTransfers(AppUser user, String accountId, Pageable pageable){
        Account acc = findAccountBelongingToUserLocking(user, accountId);

        Page<Transfer> transfers = transferRepository.findAllTransfersForAccount(acc, pageable);

        return transfers.map(TransferInfoDto::from);
    }

    private BigDecimal balanceForAccount(Account acc){
        BigDecimal sent = transferRepository.sumSent(acc);
        BigDecimal received = transferRepository.sumReceived(acc);

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
            throw new AccountNotEligibleException("Receiver account is not active");

    }
}
