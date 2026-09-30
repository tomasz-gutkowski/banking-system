package tgutkowski.project.backend.model.dtos.responses;

public record LoginResponse(
        String message,
        String jwt
) {}
