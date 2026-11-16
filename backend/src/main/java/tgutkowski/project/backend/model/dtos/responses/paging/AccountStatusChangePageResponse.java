package tgutkowski.project.backend.model.dtos.responses.paging;

import io.swagger.v3.oas.annotations.media.Schema;
import tgutkowski.project.backend.model.dtos.responses.account.AccountStatusChangeLogInfoDto;
import tgutkowski.project.backend.model.entities.AccountStatusChangeLog;

@Schema(description = "Page response of account status change logs")
public class AccountStatusChangePageResponse extends PageResponse<AccountStatusChangeLog, AccountStatusChangeLogInfoDto>{
}
