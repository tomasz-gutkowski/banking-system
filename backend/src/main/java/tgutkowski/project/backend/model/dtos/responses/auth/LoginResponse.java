package tgutkowski.project.backend.model.dtos.responses.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informs of a successful login")
public record LoginResponse(
        @Schema(description = "JWT allowing for authentication using \"Authorization\" : \"bearer {token}\" header")
        String jwt,
        @Schema(allowableValues = {
                "Login successful"
        })
        String message
) {}
