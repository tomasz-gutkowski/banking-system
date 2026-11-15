package tgutkowski.project.backend.model.dtos.requests.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login form to an existing account. Account can be registered under \"auth/register\" endpoint")
public record LoginRequest(
        @Schema(example = "Example_Username123")
        @NotBlank(message = "Username cannot be empty")
        String username,

        @Schema(example = "Example_Password456")
        @NotBlank(message = "Password cannot be empty")
        String password
) {
}
