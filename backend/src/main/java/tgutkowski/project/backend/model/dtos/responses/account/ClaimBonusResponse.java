package tgutkowski.project.backend.model.dtos.responses.account;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informs if sign-in bonus was claimed just now, was claimed before or unavailable for user")
public record ClaimBonusResponse(
        @Schema(description = "ID of an account that bonus claim was attempted for")
        String accountId,
        @Schema(allowableValues = {
                "Bonus claimed successfully",
                "Your bonus was already claimed",
                "Bonus is unavailable for this user"
        })
        String message
) {
}
