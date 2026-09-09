package tgutkowski.project.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tgutkowski.project.backend.model.entities.Account;
import tgutkowski.project.backend.model.entities.AppUser;

import javax.swing.plaf.synth.SynthTreeUI;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByOwner(AppUser owner);
    Optional<Account> findByAccountNumber(String accountNumber);
    boolean existsByOwnerUsername(String username);
    boolean existsByAccountNumber(String accountNumber);
}
