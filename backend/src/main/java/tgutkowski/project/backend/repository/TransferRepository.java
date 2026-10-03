package tgutkowski.project.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tgutkowski.project.backend.model.entities.Transfer;

import java.math.BigDecimal;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transfer t WHERE t.receiver.accountNumber = :accountNumber AND (t.status = 'PENDING' OR t.status = 'COMPLETED')")
    BigDecimal sumReceived(@Param("accountNumber") String accountNumber);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transfer t WHERE t.sender.accountNumber = :accountNumber")
    BigDecimal sumSent(@Param("accountNumber") String accountNumber);

    @Query("SELECT t FROM Transfer t WHERE t.sender.accountNumber = :accountNumber OR t.receiver.accountNumber = :accountNumber")
    Page<Transfer> findAllTransfersForAccount(@Param("accountNumber") String accountNumber, Pageable pageable);
}
