package tgutkowski.project.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tgutkowski.project.backend.config.OpenApiConfig;
import tgutkowski.project.backend.config.annotations.ApiAuthenticationErrors;
import tgutkowski.project.backend.config.annotations.ApiForbiddenErrors;
import tgutkowski.project.backend.config.annotations.ApiValidationErrors;
import tgutkowski.project.backend.exceptions.dtos.ExceptionResponse;
import tgutkowski.project.backend.model.dtos.requests.admin.AccountStatusChangeResolutionRequest;
import tgutkowski.project.backend.model.dtos.responses.account.AccountStatusChangeLogInfoDto;
import tgutkowski.project.backend.model.dtos.responses.paging.AccountStatusChangePageResponse;
import tgutkowski.project.backend.model.dtos.responses.paging.PageResponse;
import tgutkowski.project.backend.model.entities.AccountStatusChangeLog;
import tgutkowski.project.backend.model.entities.AppUser;
import tgutkowski.project.backend.model.entities.enums.ResolutionStatus;
import tgutkowski.project.backend.model.entities.enums.StatusChange;
import tgutkowski.project.backend.service.AdminService;

import java.util.UUID;

@Tag(name = OpenApiConfig.TagNames.ADMIN)

@RestController
@RequestMapping(value = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

        @Operation(summary = "Returns list of all tickets requesting manually resolved status change",
                description = "REQUIRED ROLE [ADMIN] - Returns a part of paginated response with given size (1-10, higher values will be reduced to the max of a range) consisting of status change requests, that require manual confirmation. If next page exists, field \"cursor\" in response has a value that can be used as a starting point for next page if passed as \"cursor\" request parameter")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200" , description = "Page for all status change logs with MANUAL resolution",
                        content = @Content(schema = @Schema(implementation = AccountStatusChangePageResponse.class))),
                @ApiResponse(responseCode = "404", description = "No record found for given cursor",
                        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        })
        @ApiAuthenticationErrors
        @ApiForbiddenErrors
        @ApiValidationErrors
    @GetMapping("/status-change-logs/tickets")
    public ResponseEntity<PageResponse<AccountStatusChangeLog, AccountStatusChangeLogInfoDto>> getStatusChangeLogsTickets(@Parameter(description = "Request type to filter for. If not specified, response will include all possible options")
                                                                                                                                  @RequestParam(required = false) StatusChange requestType,
                                                                                                                          @Parameter(description = "Resolution status to filter for. If not specified, response will include all possible options")
                                                                                                                      @RequestParam(required = false) ResolutionStatus resolutionStatus,
                                                                                                                      @Parameter(description = "Amount of records in a page (1-10, greater values are reduced to the top of the range)")
                                                                                                                      @Min(1)
                                                                                                                      @RequestParam(defaultValue = "10") int size,
                                                                                                                      @Parameter(description = "Cursor pointing at the start of selected page. Omit to start from first page. Cursor for next page is returned by response (null if no next page)")
                                                                                                                      @RequestParam(required = false) UUID cursor){
        int sizeCapped = Math.min(size, 10);
        PageResponse<AccountStatusChangeLog, AccountStatusChangeLogInfoDto> response = adminService.getChangeTickets(requestType, resolutionStatus, sizeCapped, cursor);

        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

        @Operation(summary = "Resolves ticket with a valid resolution type",
                description = "REQUIRED ROLE [ADMIN] - Resolves ticket giving it either ACCEPTED or DECLINED status. If ticket gets accepted, then account requesting change gets it's status changed accordingly")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200" , description = "Information of ticket resolution",
                        content = @Content(schema = @Schema(implementation = AccountStatusChangeLogInfoDto.class))),
                @ApiResponse(responseCode = "404", description = "No ticket found for given UUID",
                        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                @ApiResponse(responseCode = "409", description = "Ticket either not selected for manual resolution, ticket is already resolved or account status doesnt match a requested change",
                        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        })
        @ApiAuthenticationErrors
        @ApiForbiddenErrors
        @ApiValidationErrors
    @PostMapping("/status-change-logs/ticket/resolve")
    public ResponseEntity<AccountStatusChangeLogInfoDto> postStatusChangeLogsTicketResolve(@AuthenticationPrincipal AppUser appUser,
                                                                       @Valid @RequestBody AccountStatusChangeResolutionRequest request){
        UUID uuid = UUID.fromString(request.uuid());
        ResolutionStatus resolutionStatus = request.resolutionStatus();

        AccountStatusChangeLogInfoDto response = adminService.resolveTicket(appUser, uuid, resolutionStatus);
        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

}
