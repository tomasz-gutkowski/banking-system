package tgutkowski.project.backend.model.dtos.requests.transfer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Creates new outgoing transfer in a system from an ACTIVE account")
public record TransferRequest(
    @Schema(description = "Amount of funds to be transferred. Requires sender to have balance greater or equal than given amount, for initial balance use \"/account/claim-bonus\". Max 12 integer digits and 2 fraction digits",
    example = "100.50")
    @NotNull(message = "Transfer amount required")
    @Positive(message = "Transfer amount has to be positive")
    @Digits(integer = 12 , fraction = 2, message = "Amount format is invalid")
    BigDecimal amount,

    @Schema(description = "Account ID of receiver. Account has to exist in a system, have an ACTIVE status and be different then sender",
    example = "ACC-12345678")
    @NotBlank(message = "Receiver is required")
    @Pattern(regexp = "^ACC-\\d{8}$", message = "Not an eligible account number")
    String receiver
) {}
