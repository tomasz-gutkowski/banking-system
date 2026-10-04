package tgutkowski.project.backend.model.dtos.responses.account;

import tgutkowski.project.backend.model.entities.enums.AccountStatus;

public record AccountStatusResponse(
        String accountNumber,
        AccountStatus accountStatus
) {}
