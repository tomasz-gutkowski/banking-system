package tgutkowski.project.backend.model.dtos.responses;

import tgutkowski.project.backend.model.entities.Transfer;
import tgutkowski.project.backend.model.entities.enums.TransferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferDto(
        Long id,
        String senderNumber,
        String receiverNumber,
        BigDecimal amount,
        LocalDateTime date,
        TransferStatus status
)
{
    public static TransferDto from(Transfer transfer){
        return new TransferDto(
                transfer.getId(),
                transfer.getSender().getAccountNumber(),
                transfer.getReceiver().getAccountNumber(),
                transfer.getAmount(),
                transfer.getDate(),
                transfer.getStatus()
        );
    }
}

