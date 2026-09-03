package tgutkowski.project.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tgutkowski.project.backend.model.entities.AppUser;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
}
