package tgutkowski.project.backend.model.dtos.requests.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username cannot be empty")
        @Size(min = 3, max=30, message = "Username must be 3-30 characters")
        // todo regex validation
        String username,

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 4, max=72, message = "Password must be 8-72 characters")
        String password
){}
