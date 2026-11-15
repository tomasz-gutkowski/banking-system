package tgutkowski.project.backend.model.dtos.responses.account;

import io.swagger.v3.oas.annotations.media.Schema;
import tgutkowski.project.backend.model.entities.AccountStatusChangeLog;
import tgutkowski.project.backend.model.entities.enums.ResolutionStatus;
import tgutkowski.project.backend.model.entities.enums.ResolutionType;
import tgutkowski.project.backend.model.entities.enums.StatusChange;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Single log of account status change")
public record AccountStatusChangeLogInfoDto(
        @Schema(description = "UUIDv4 identifying log. Best way to extract valid UUID for \"/admin/status-change-logs/ticket/resolve\" endpoint")
        UUID uuid,
        @Schema(description = "ID of an account that status change was requested on")
        String accountId,
        @Schema(description = "Status change requested on an account")
        StatusChange requestType,
        @Schema(description = "Tells whether request was done by account owner (USER) or an admin (ADMIN)",
            allowableValues = {"USER", "ADMIN"})
        String requestedBy,
        @Schema(description = "Reason given for status change by requester. Can be null if none was given")
        String reason,
        @Schema(description = "Date when change was requested (UTC time)")
        Instant requestDate,
        @Schema(description = "Tells whether request was made for automatic (AUTO) resolution or manual (MANUAL)")
        ResolutionType resolutionType,
        @Schema(description = "Tells whether request was accepted, declined or is still pending resolution")
        ResolutionStatus resolutionStatus,
        @Schema(description = "Date when request was resolved (UTC time). Null while pending")
        Instant resolutionDate
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
