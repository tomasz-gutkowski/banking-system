package tgutkowski.project.backend.model.dtos.responses.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informs of a successful JWT refresh")
public record RefreshResponse(
        @Schema(description = "New JWT allowing for authentication using \"Authorization\" : \"bearer {token}\" header")
        String jwt,
        @Schema(allowableValues = {
                "Refresh successful"
        })
        String message
) {}
