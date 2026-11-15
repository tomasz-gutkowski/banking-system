package tgutkowski.project.backend.model.dtos.requests.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tgutkowski.project.backend.model.entities.enums.StatusChange;

@Schema(description = "Request for performing status change on an account")
public record AccountStatusChangeRequest(
        @Schema(description = "Optional reason for requesting a change",
                example = "Security concern")
        @Nullable
        @Size(max = 200, message = "Reason cannot exceed 200 characters")
        String reason,

        @Schema(description = "Status transition desired for an account. FREEZE and CLOSE require ACTIVE account and are executed automatically with CLOSE additionally requiring a balance to be 0, UNFREEZE can be only done on FROZEN account and requires manual admin verification",
                example="FREEZE")
        @NotNull(message = "Invalid or empty status change")
        StatusChange statusChange
) {}
