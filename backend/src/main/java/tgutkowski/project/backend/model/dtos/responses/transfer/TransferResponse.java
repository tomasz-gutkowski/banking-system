package tgutkowski.project.backend.model.dtos.responses.transfer;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Description of successfully created transfer")
public record TransferResponse(
        @Schema(description = "UUIDv4 identifying transfer")
        UUID uuid,
        @Schema(description = "ID of an account sending funds")
        String senderAccountId,
        @Schema(description = "ID of an account receiving funds")
        String receiverAccountId,
        @Schema(description = "Amount of funds sent in a transfer")
        BigDecimal amount,
        @Schema(allowableValues = {"Transfer completed successfully"})
        String message
) {

}
