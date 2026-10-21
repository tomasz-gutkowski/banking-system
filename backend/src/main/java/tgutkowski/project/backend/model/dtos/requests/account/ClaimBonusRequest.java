package tgutkowski.project.backend.model.dtos.requests.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ClaimBonusRequest(
        @NotBlank(message = "Sender is required")
        @Pattern(regexp = "^ACC-\\d{8}$",
                message = "Not an eligible account id")
        String accountId
) {}
