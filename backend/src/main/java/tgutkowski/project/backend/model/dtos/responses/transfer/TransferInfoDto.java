package tgutkowski.project.backend.model.dtos.responses.transfer;

import tgutkowski.project.backend.model.entities.Transfer;
import tgutkowski.project.backend.model.entities.enums.TransferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferInfoDto(
        Long id,
        String senderAccountId,
        String receiverAccountId,
        BigDecimal amount,
        LocalDateTime date,
        TransferStatus status
)
{
    public static TransferInfoDto from(Transfer transfer){
        return new TransferInfoDto(
                transfer.getId(),
                transfer.getSender().getAccountId(),
                transfer.getReceiver().getAccountId(),
                transfer.getAmount(),
                transfer.getDate(),
                transfer.getStatus()
        );
    }
}

