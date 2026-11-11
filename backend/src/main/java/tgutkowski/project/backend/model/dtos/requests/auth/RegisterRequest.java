package tgutkowski.project.backend.model.dtos.requests.auth;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.nio.charset.StandardCharsets;

public record RegisterRequest(
        @NotBlank(message = "Username cannot be empty")
        @Size(min = 3, max=30, message = "Username must be 3-30 characters")
        // todo regex validation
        String username,

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 8, max=72, message = "Password must be 8-72 characters")
        String password
){
        @AssertTrue(message = "Password cannot exceed 72 bytes")
        public boolean isPasswordValid(){
                if(password == null) return false;
                return this.password.getBytes(StandardCharsets.UTF_8).length <= 72;
        }
}
