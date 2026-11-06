package tgutkowski.project.backend.model.dtos.requests.account;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import tgutkowski.project.backend.model.entities.enums.StatusChange;

public record AccountStatusChangeRequest(
        @Nullable
        String reason,

        @NotNull(message = "Invalid or empty status change")
        StatusChange statusChange
) {}
