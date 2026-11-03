package tgutkowski.project.backend.model.dtos.responses.account;

public record AccountStatusChangeResponse(
        String accountId,
        String message
) {}
