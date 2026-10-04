package tgutkowski.project.backend.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tgutkowski.project.backend.model.entities.Account;
import tgutkowski.project.backend.model.entities.AppUser;
import tgutkowski.project.backend.model.entities.enums.AccountStatus;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByOwner(AppUser owner);
    Optional<Account> findByOwnerUsername(String username);
    Optional<Account> findByOwnerAndAccountId(AppUser owner, String accountId);

    boolean existsByAccountId(String accountId);

    List<Account> findAllByOwner(AppUser owner);

    boolean existsByOwnerAndAccountId(AppUser owner, String accountId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.owner = :owner")
    Optional<Account> findByOwnerLocking(@Param("owner") AppUser owner);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountId = :accountId")
    Optional<Account> findByAccountNumberLocking(@Param("accountId") String accountId);

    @Query("SELECT COUNT(a) > 0 FROM Account a WHERE a.owner = :owner AND a.status IN :statuses")
    boolean existsByOwner(@Param("owner") AppUser owner,
                          @Param("statuses") List<AccountStatus> statuses);
}
