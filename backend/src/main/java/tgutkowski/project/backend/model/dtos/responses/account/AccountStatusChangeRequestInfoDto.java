package tgutkowski.project.backend.model.dtos.responses.account;

import tgutkowski.project.backend.model.entities.AccountStatusChangeRequest;

public record AccountStatusChangeRequestInfoDto(

) {
    public static AccountStatusChangeRequestInfoDto from(AccountStatusChangeRequest ascr) {
        return new AccountStatusChangeRequestInfoDto();
    }
}
