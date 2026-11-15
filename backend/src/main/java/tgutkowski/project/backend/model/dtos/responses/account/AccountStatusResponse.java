package tgutkowski.project.backend.model.dtos.responses.account;

import io.swagger.v3.oas.annotations.media.Schema;
import tgutkowski.project.backend.model.entities.enums.AccountStatus;

@Schema(description = "Informs about current status of an account")
public record AccountStatusResponse(
        @Schema(description = "ID of an account")
        String accountId,
        @Schema(description = "Current status of an account")
        AccountStatus accountStatus
) {}
