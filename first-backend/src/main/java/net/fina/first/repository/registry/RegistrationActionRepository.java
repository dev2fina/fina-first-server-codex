package net.fina.first.repository.registry;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import net.fina.first.model.registry.RegistrationAction;

public interface RegistrationActionRepository extends JpaRepository<RegistrationAction, Long> {
    List<RegistrationAction> findByFinancialInstitutionIdOrderByPerformedAtDesc(Long fiId);
}
