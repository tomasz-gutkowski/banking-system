package tgutkowski.project.backend.model.dtos.requests.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import tgutkowski.project.backend.model.entities.enums.ResolutionStatus;

@Schema(description = "Resolution to a PENDING ticket requesting a status change on an account with MANUAL type resolution")
public record AccountStatusChangeResolutionRequest(

        @Schema(description = "UUIDv4 for pending request that is due for manual resolution. Valid UUID is required and one can be extracted from \"/admin/status-change-logs/tickets\" endpoint response.",
                example = "12345678-1234-4123-8123-123456789abc"
        )
        @NotNull(message = "UUID cannot be empty")
        @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
        message = "Not a valid UUID")
        String uuid,

        @Schema(description = "Decision whether ticket is resolved as ACCEPTED or DECLINED. ACCEPTED ticket will automatically assign proper status to requesting account, DECLINED will result in no changes to status",
        example = "ACCEPTED",
        allowableValues = {
                "ACCEPTED",
                "DECLINED"
        })
        @NotNull(message = "Resolution cannot be empty")
        ResolutionStatus resolutionStatus


) {
        @Schema(hidden = true)
        @AssertTrue(message = "Resolution has to be either ACCEPTED or DECLINED")
        public boolean isResolutionStatusValid(){
            return resolutionStatus == ResolutionStatus.ACCEPTED || resolutionStatus == ResolutionStatus.DECLINED;
        }
}
