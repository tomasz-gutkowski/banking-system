package tgutkowski.project.backend.repository;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tgutkowski.project.backend.model.entities.Account;
import tgutkowski.project.backend.model.entities.Transfer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transfer t WHERE t.receiver = :account AND (t.status = 'PENDING' OR t.status = 'COMPLETED')")
    BigDecimal sumReceived(@Param("account") Account account);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transfer t WHERE t.sender = :account")
    BigDecimal sumSent(@Param("account") Account account);

    @Query("SELECT t.id FROM Transfer t WHERE (t.sender = :account OR t.receiver = :account) AND t.uuid = :cursor")
    Optional<Long> getIdByCursor(@Param("account") Account account, @Param("cursor") UUID cursor);

    @Query("""
                SELECT t
                FROM Transfer t
                WHERE (t.sender = :account OR t.receiver = :account)
                AND (:cursorId IS NULL OR t.id <= :cursorId)
                ORDER BY t.id DESC
                """)
    List<Transfer> getTransfersPage(@Param("account") Account account, @Param("cursorId") Long cursorId, Limit limit);
}
