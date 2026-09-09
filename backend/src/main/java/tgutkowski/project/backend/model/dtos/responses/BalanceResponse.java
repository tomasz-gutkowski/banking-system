package tgutkowski.project.backend.model.dtos.responses;

import java.math.BigDecimal;

public record BalanceResponse(
        String username,
        BigDecimal balance
) {}
