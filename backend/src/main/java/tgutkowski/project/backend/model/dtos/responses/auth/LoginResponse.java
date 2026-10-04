package tgutkowski.project.backend.model.dtos.responses.auth;

public record LoginResponse(
        String message,
        String jwt
) {}
