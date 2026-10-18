package tgutkowski.project.backend.model.dtos.responses.account;

public record AccountFreezeResponse(
        String accountId,
        String message
){}
