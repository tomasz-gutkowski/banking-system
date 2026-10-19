package tgutkowski.project.backend.model.dtos.responses.account;

public record AccountUnfreezeResponse(
        String accountId,
        String message
){
}
