package tgutkowski.project.backend.repository;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tgutkowski.project.backend.model.entities.Account;
import tgutkowski.project.backend.model.entities.AccountStatusChangeLog;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountStatusChangeLogRepository extends JpaRepository<AccountStatusChangeLog, Long> {

    @Query("SELECT COUNT(*)>0 FROM AccountStatusChangeLog r WHERE r.account = :account AND r.requestType = 'UNFREEZE' AND r.resolutionStatus = 'PENDING'")
    boolean existsActiveUnfreezeRequests(@Param("account") Account account);


    @Query("SELECT ascl.id FROM AccountStatusChangeLog ascl WHERE ascl.account = :account AND ascl.uuid = :cursor")
    Optional<Long> getIdByCursor(@Param("account") Account account, @Param("cursor") UUID cursor);

    @Query("""
            SELECT ascl
            FROM AccountStatusChangeLog ascl
            WHERE ascl.account = :account AND 
            (:cursorId IS NULL OR ascl.id <= :cursorId)
            ORDER BY ascl.id DESC
            """)
    List<AccountStatusChangeLog> getLogsPage(@Param("account") Account account, @Param("cursorId") Long cursorId, Limit limit);
}
