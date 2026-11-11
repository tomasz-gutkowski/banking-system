package tgutkowski.project.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tgutkowski.project.backend.model.dtos.requests.admin.AccountStatusChangeResolutionRequest;
import tgutkowski.project.backend.model.dtos.responses.account.AccountStatusChangeLogInfoDto;
import tgutkowski.project.backend.model.dtos.responses.paging.PageResponse;
import tgutkowski.project.backend.model.entities.AccountStatusChangeLog;
import tgutkowski.project.backend.model.entities.AppUser;
import tgutkowski.project.backend.model.entities.enums.ResolutionStatus;
import tgutkowski.project.backend.model.entities.enums.StatusChange;
import tgutkowski.project.backend.service.AdminService;

import java.util.UUID;

@Tag(name = "Admin panel",
        description = "Methods exclusive to admin accounts")

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "Returns list of all tickets requesting manually resolved status change",
            description = "Returns a part of paginated response with given size (1-10, higher values will be reduced to the max of a range) consisting of status change requests, that require manual confirmation. If next page exists, field \"cursor\" in response has a value that can be used as a starting point for next page if passed as \"cursor\" request parameter")
    @GetMapping("/status-change-logs/tickets")
    public ResponseEntity<PageResponse<AccountStatusChangeLog, AccountStatusChangeLogInfoDto>> getStatusChangeLogsTickets(@RequestParam(required = false) StatusChange requestType,
                                                                                                                      @RequestParam(required = false) ResolutionStatus resolutionStatus,
                                                                                                                      @Size(min = 1)
                                                                                                                      @RequestParam(defaultValue = "10") int size,
                                                                                                                      @RequestParam(required = false) UUID cursor){
        int sizeCapped = Math.min(size, 10);
        PageResponse<AccountStatusChangeLog, AccountStatusChangeLogInfoDto> response = adminService.getChangeTickets(requestType, resolutionStatus, sizeCapped, cursor);

        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

    @Operation(summary = "Resolves ticket with a valid resolution type",
            description = "Resolves ticket giving it either ACCEPTED or DECLINED status. If ticket gets accepted, then account requesting change gets it's status changed accordingly")
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
