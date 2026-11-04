package tgutkowski.project.backend.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tgutkowski.project.backend.model.entities.Account;
import tgutkowski.project.backend.model.entities.AccountStatusChangeLog;
import tgutkowski.project.backend.model.entities.enums.ResolutionStatus;
import tgutkowski.project.backend.model.entities.enums.StatusChange;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountStatusChangeLogRepository extends JpaRepository<AccountStatusChangeLog, Long> {

    @Query("SELECT COUNT(*)>0 FROM AccountStatusChangeLog r WHERE r.account = :account AND r.requestType = 'UNFREEZE' AND r.resolutionStatus = 'PENDING'")
    boolean existsActiveUnfreezeRequests(@Param("account") Account account);


    @Query("SELECT ascl.id FROM AccountStatusChangeLog ascl WHERE ascl.account = :account AND ascl.uuid = :cursor")
    Optional<Long> getIdByAccountAndCursor(@Param("account") Account account, @Param("cursor") UUID cursor);

    @Query("SELECT ascl.id FROM AccountStatusChangeLog ascl WHERE ascl.uuid = :cursor")
    Optional<Long> getIdByCursor(@Param("cursor") UUID cursor);

    @Query("""
            SELECT ascl
            FROM AccountStatusChangeLog ascl
            WHERE ascl.account = :account AND
            (:cursorId IS NULL OR ascl.id <= :cursorId)
            ORDER BY ascl.id DESC
            """)
    List<AccountStatusChangeLog> getLogsPage(@Param("account") Account account, @Param("cursorId") Long cursorId, Limit limit);

    @Query("""
            SELECT ascl
            FROM AccountStatusChangeLog ascl
            WHERE (:cursorId IS NULL OR ascl.id <= :cursorId) AND
            (:requestType IS NULL or ascl.requestType = :requestType) AND
            (:resolutionStatus IS NULL or ascl.resolutionStatus = :resolutionStatus)
            ORDER BY ascl.id DESC
            """)
    List<AccountStatusChangeLog> getLogsTicketPage(@Param("requestType") StatusChange requestType,
                                                   @Param("resolutionStatus") ResolutionStatus resolutionStatus,
                                                   @Param("cursorId") Long cursorId,
                                                   Limit limit);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ascl FROM AccountStatusChangeLog ascl WHERE ascl.uuid = :uuid")
    Optional<AccountStatusChangeLog> findByUuidLocking(@Param("uuid") UUID uuid);
}
