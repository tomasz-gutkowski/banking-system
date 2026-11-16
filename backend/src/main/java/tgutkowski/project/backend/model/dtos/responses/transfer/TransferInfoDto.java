package tgutkowski.project.backend.model.dtos.responses.transfer;

import io.swagger.v3.oas.annotations.media.Schema;
import tgutkowski.project.backend.model.entities.Transfer;
import tgutkowski.project.backend.model.entities.enums.TransferStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Information about a single transaction")
public record TransferInfoDto(
        @Schema(description = "UUIDv4 identifying transfer")
        UUID uuid,
        @Schema(description = "ID of an account sending funds")
        String senderAccountId,
        @Schema(description = "ID of an account receiving funds")
        String receiverAccountId,
        @Schema(description = "Amount of funds sent in a transfer")
        BigDecimal amount,
        @Schema(description = "Date when transfer was created (UTC time)")
        Instant date,
        @Schema(description = "Current status of a transfer")
        TransferStatus status
)
{
    public static TransferInfoDto from(Transfer transfer){
        return new TransferInfoDto(
                transfer.getUUID(),
                transfer.getSender().getAccountId(),
                transfer.getReceiver().getAccountId(),
                transfer.getAmount(),
                transfer.getDate(),
                transfer.getStatus()
        );
    }
}

