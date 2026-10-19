package tgutkowski.project.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tgutkowski.project.backend.model.entities.AccountStatusChangeRequest;

public interface AccountStatusChangeRequestRepository extends JpaRepository<AccountStatusChangeRequest, Long> {


}
