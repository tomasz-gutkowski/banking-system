package tgutkowski.project.backend.model.dtos.responses.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informs of a successful logout")
public record LogoutResponse(
        @Schema(allowableValues = {
                "Logout successful"
        })
        String message
) {}
