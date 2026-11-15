package tgutkowski.project.backend.model.dtos.responses.account;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response informing of successful account creation")
public record AccountCreateResponse(
        @Schema(description = "Account ID assigned to created account")
        String accountId,

        @Schema(allowableValues = {
                "Account created successfully"
        })
        String message
)
{
}
