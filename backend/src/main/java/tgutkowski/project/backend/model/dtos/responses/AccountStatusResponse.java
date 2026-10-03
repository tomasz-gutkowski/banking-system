package tgutkowski.project.backend.model.dtos.responses;

import tgutkowski.project.backend.model.entities.enums.AccountStatus;

public record AccountStatusResponse(
        String accountNumber,
        AccountStatus accountStatus
) {}
