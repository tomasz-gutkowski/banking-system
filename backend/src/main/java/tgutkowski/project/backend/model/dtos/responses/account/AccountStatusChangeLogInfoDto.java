package tgutkowski.project.backend.model.dtos.responses.account;

import tgutkowski.project.backend.model.entities.AccountStatusChangeLog;
import tgutkowski.project.backend.model.entities.enums.ResolutionStatus;
import tgutkowski.project.backend.model.entities.enums.ResolutionType;
import tgutkowski.project.backend.model.entities.enums.StatusChange;

import java.time.LocalDateTime;
import java.util.UUID;

public record AccountStatusChangeLogInfoDto(
        UUID uuid,
        String accountId,
        StatusChange requestType,
        String requestedBy,
        String reason,
        LocalDateTime requestDate,
        ResolutionType resolutionType,
        ResolutionStatus resolutionStatus,
        LocalDateTime resolutionDate
) {
    public static AccountStatusChangeLogInfoDto from(AccountStatusChangeLog ascl) {
        return new AccountStatusChangeLogInfoDto(
                ascl.getUuid(),
                ascl.getAccount().getAccountId(),
                ascl.getRequestType(),
                ascl.getRequestedBy().getRole().toString(),
                ascl.getReason(),
                ascl.getRequestDate(),
                ascl.getResolutionType(),
                ascl.getResolutionStatus(),
                ascl.getResolutionDate()
        );
    }
}
