package tgutkowski.project.backend.model.dtos.responses.account;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Confirms successful request of account status change. Does not mean that account change was necessarily done")
public record AccountStatusChangeResponse(
        @Schema(description = "ID of an account that change was requested on")
        String accountId,
        @Schema(allowableValues = {
                "Account closed successfully",
                "Account frozen successfully",
                "Account unfrozen successfully",
                "Account unfreeze request created"
        })
        String message
) {}
