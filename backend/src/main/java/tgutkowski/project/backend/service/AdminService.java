package tgutkowski.project.backend.service;

import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tgutkowski.project.backend.exceptions.ConflictException;
import tgutkowski.project.backend.exceptions.InternalException;
import tgutkowski.project.backend.exceptions.InvalidArgumentException;
import tgutkowski.project.backend.model.dtos.responses.account.AccountStatusChangeLogInfoDto;
import tgutkowski.project.backend.model.dtos.responses.paging.PageResponse;
import tgutkowski.project.backend.model.entities.Account;
import tgutkowski.project.backend.model.entities.AccountStatusChangeLog;
import tgutkowski.project.backend.model.entities.AppUser;
import tgutkowski.project.backend.model.entities.enums.AccountStatus;
import tgutkowski.project.backend.model.entities.enums.ResolutionStatus;
import tgutkowski.project.backend.model.entities.enums.ResolutionType;
import tgutkowski.project.backend.model.entities.enums.StatusChange;
import tgutkowski.project.backend.repository.AccountRepository;
import tgutkowski.project.backend.repository.AccountStatusChangeLogRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AdminService {

    private final AccountRepository accountRepository;
    private final AccountStatusChangeLogRepository accountStatusChangeLogRepository;

    public AdminService(AccountRepository accountRepository, AccountStatusChangeLogRepository accountStatusChangeLogRepository) {
        this.accountRepository = accountRepository;
        this.accountStatusChangeLogRepository = accountStatusChangeLogRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<AccountStatusChangeLog, AccountStatusChangeLogInfoDto> getChangeTickets(StatusChange requestType,
                                                                                                ResolutionStatus resolutionStatus,
                                                                                                int size,
                                                                                                UUID cursor) {
        Limit limit = Limit.of(size+1);

        Long cursorId = null;
        if(cursor != null){
            cursorId = accountStatusChangeLogRepository.getIdByCursor(cursor)
                    .orElseThrow(() -> new InvalidArgumentException("Invalid cursor"));
        }

        List<AccountStatusChangeLog> accountStatusChangeLogs = accountStatusChangeLogRepository.getLogsTicketPage(requestType, resolutionStatus, cursorId, limit);

        PageResponse<AccountStatusChangeLog, AccountStatusChangeLogInfoDto> pageResponse = new PageResponse<>();
        pageResponse.setPage(accountStatusChangeLogs,size);

        return pageResponse;
    }

    @Transactional
    public AccountStatusChangeLogInfoDto resolveTicket(AppUser user, UUID uuid, ResolutionStatus resolutionStatus) {
        AccountStatusChangeLog ascl = accountStatusChangeLogRepository.findByUuidLocking(uuid)
                .orElseThrow(() -> new InvalidArgumentException("No ticket with selected UUID"));

        String accountId = ascl.getAccount().getAccountId();

        Account account = accountRepository.findByAccountIdLocking(accountId)
                .orElseThrow(() -> new InternalException("No account assigned to a ticket"));

        if(!ascl.getResolutionStatus().equals(ResolutionStatus.PENDING)) throw new ConflictException("Selected ticket already resolved");

        if(!resolutionStatus.equals(ResolutionStatus.ACCEPTED) && !resolutionStatus.equals(ResolutionStatus.DECLINED))
            throw new InvalidArgumentException("Invalid resolution");

        if(!ascl.getResolutionType().equals(ResolutionType.MANUAL)) throw new InvalidArgumentException("Invalid resolution type");

        switch(ascl.getRequestType()){
            case CLOSE:{
                if(account.getAccountStatus().equals(AccountStatus.CLOSED)) throw new ConflictException("Account status not matching request");
                if(resolutionStatus.equals(ResolutionStatus.ACCEPTED)) account.setStatus(AccountStatus.CLOSED);
                break;
            }
            case FREEZE:{
                if(!account.getAccountStatus().equals(AccountStatus.ACTIVE)) throw new ConflictException("Account status not matching request");
                if(resolutionStatus.equals(ResolutionStatus.ACCEPTED)) account.setStatus(AccountStatus.FROZEN);
                break;
            }
            case UNFREEZE:{
                if(!account.getAccountStatus().equals(AccountStatus.FROZEN)) throw new ConflictException("Account status not matching request");
                if(resolutionStatus.equals(ResolutionStatus.ACCEPTED)) account.setStatus(AccountStatus.ACTIVE);
                break;
            }
        }
        LocalDateTime now = LocalDateTime.now();
        ascl.setResolutionDate(now);
        ascl.setResolvedBy(user);
        ascl.setResolutionStatus(resolutionStatus);

        return ascl.convertToDTO();
    }
}
