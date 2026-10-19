package tgutkowski.project.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tgutkowski.project.backend.model.entities.Account;
import tgutkowski.project.backend.model.entities.AccountStatusChangeRequest;

public interface AccountStatusChangeRequestRepository extends JpaRepository<AccountStatusChangeRequest, Long> {

    @Query("SELECT COUNT(*)>0 FROM AccountStatusChangeRequest r WHERE r.account = :account AND r.requestType = 'UNFREEZE' AND r.resolutionStatus = 'PENDING'")
    boolean existsActiveUnfreezeRequests(@Param("account") Account account);
}
