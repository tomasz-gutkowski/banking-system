package tgutkowski.project.backend.model.dtos.responses.paging;

import io.swagger.v3.oas.annotations.media.Schema;
import tgutkowski.project.backend.model.dtos.responses.transfer.TransferInfoDto;
import tgutkowski.project.backend.model.entities.Transfer;

@Schema(description = "Page response of transfers")
public class TransferPageResponse extends PageResponse<Transfer, TransferInfoDto> {
}
