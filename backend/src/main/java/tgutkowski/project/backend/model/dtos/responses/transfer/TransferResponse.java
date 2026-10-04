package tgutkowski.project.backend.model.dtos.responses.transfer;

import java.math.BigDecimal;

public record TransferResponse(
        String id,
        String senderAccountId,
        String receiverAccountId,
        BigDecimal amount,
        String message
) {

}
