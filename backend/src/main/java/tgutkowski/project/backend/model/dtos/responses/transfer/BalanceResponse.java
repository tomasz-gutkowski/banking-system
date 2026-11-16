package tgutkowski.project.backend.model.dtos.responses.transfer;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Informs about current balance of selected account")
public record BalanceResponse(
        @Schema(description = "ID of an account that this balance belongs to")
        String accountId,
        @Schema(description = "Current account balance with 2 fraction digits")
        BigDecimal balance
) {}
