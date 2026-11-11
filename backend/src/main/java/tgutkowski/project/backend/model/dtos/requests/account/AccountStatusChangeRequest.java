package tgutkowski.project.backend.model.dtos.requests.account;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import tgutkowski.project.backend.model.entities.enums.StatusChange;

public record AccountStatusChangeRequest(
        @Nullable
        String reason,

        @NotNull(message = "Invalid or empty status change")
        StatusChange statusChange
) {}
