package tgutkowski.project.backend.model.dtos.requests.admin;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import tgutkowski.project.backend.model.entities.enums.ResolutionStatus;

public record AccountStatusChangeResolutionRequest(

        @NotNull(message = "UUID cannot be empty")
        @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
        message = "Not a valid UUID")
        String uuid,

        @NotNull(message = "Resolution cannot be empty")
        ResolutionStatus resolutionStatus
) {}
