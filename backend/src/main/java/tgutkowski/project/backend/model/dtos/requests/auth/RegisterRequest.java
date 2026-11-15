package tgutkowski.project.backend.model.dtos.requests.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.nio.charset.StandardCharsets;

@Schema(description = "Registration form. After success you can authenticate with your credentials under \"auth/login\" endpoint")
public record RegisterRequest(

        @Schema(description = "Unique username consisting only of letters, numbers, dots, underscores and hyphens",
        example = "Example_Username123")
        @NotBlank(message = "Username cannot be empty")
        @Size(min = 3, max=30, message = "Username must be 3-30 characters")
        @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "Username can only consist of letters, numbers, dots, underscores and hyphens")
        String username,

        @Schema(description = "Password that does not exceed 72 bytes",
                example = "Example_Password456")
        @NotBlank(message = "Password cannot be empty")
        @Size(min = 8, max=72, message = "Password must be 8-72 characters")
        String password
){
        @Schema(hidden = true)
        @AssertTrue(message = "Password cannot exceed 72 bytes")
        public boolean isPasswordValid(){
                if(password == null) return true;
                return this.password.getBytes(StandardCharsets.UTF_8).length <= 72;
        }
}
