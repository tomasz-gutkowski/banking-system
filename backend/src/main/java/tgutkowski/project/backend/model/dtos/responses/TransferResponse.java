package tgutkowski.project.backend.model.dtos.responses;

import java.math.BigDecimal;

public record TransferResponse(
        String id,
        String senderAccNum,
        String receiverAccNum,
        BigDecimal amount,
        String message
) {

}
