package tgutkowski.project.backend.model.dtos.requests.transfer;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TransferRequest(
    @NotNull(message = "Transfer amount required")
    @Positive(message = "Transfer amount has to be positive")
    @Digits(integer = 12 , fraction = 2, message = "Amount format is invalid")
    BigDecimal amount,

    @NotBlank(message = "Receiver is required")
    @Pattern(regexp = "^ACC-\\d{8}$", message = "Not an eligible account number")
    String receiver
) {}
