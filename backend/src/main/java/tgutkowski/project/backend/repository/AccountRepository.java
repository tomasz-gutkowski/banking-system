package tgutkowski.project.backend.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tgutkowski.project.backend.model.entities.Account;
import tgutkowski.project.backend.model.entities.AppUser;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.owner = :username")
    Optional<Account> findByOwnerUsernameLocking(@Param("username") String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberLocking(@Param("accountNumber") String accountNumber);

    Optional<Account> findByOwner(AppUser owner);
    Optional<Account> findByAccountNumber(String accountNumber);
    Optional<Account> findByOwnerUsername(String username);
    boolean existsByOwnerUsername(String username);
    boolean existsByAccountNumber(String accountNumber);
}
