package tgutkowski.project.backend.model.dtos.responses.auth;

public record RefreshResponse(
    String jwt,
    String message
) {}
