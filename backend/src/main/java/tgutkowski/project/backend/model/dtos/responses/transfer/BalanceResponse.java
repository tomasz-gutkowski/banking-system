package tgutkowski.project.backend.model.dtos.responses.transfer;

import java.math.BigDecimal;

public record BalanceResponse(
        String accountId,
        BigDecimal balance
) {}
