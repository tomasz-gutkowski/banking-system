package tgutkowski.project.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tgutkowski.project.backend.model.entities.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transfer t WHERE t.receiver.accountNumber = :accountNumber")
    BigDecimal sumReceived(@Param("accountNumber") String accountNumber);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transfer t WHERE t.sender.accountNumber = :accountNumber")
    BigDecimal sumSent(@Param("accountNumber") String accountNumber);
}
