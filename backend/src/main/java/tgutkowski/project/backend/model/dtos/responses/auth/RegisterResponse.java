package tgutkowski.project.backend.model.dtos.responses.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informs of a successful registration")
public record RegisterResponse(
        @Schema(allowableValues = {
                "Account created successfully"
        })
        String message
) {}
