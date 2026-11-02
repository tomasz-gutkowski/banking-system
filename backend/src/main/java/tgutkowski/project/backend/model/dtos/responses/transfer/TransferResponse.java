package tgutkowski.project.backend.model.dtos.responses.transfer;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferResponse(
        UUID uuid,
        String senderAccountId,
        String receiverAccountId,
        BigDecimal amount,
        String message
) {

}
