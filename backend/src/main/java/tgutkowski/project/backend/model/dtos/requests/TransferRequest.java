package tgutkowski.project.backend.model.dtos.requests;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TransferRequest(
    @NotNull
    @DecimalMin("0")
    BigDecimal amount,

    @NotBlank
    @Pattern(regexp = "^ACC-\\d{8}$")
    String receiver
) {}
