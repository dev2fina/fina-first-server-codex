package net.fina.first.repository.registry;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import net.fina.first.model.registry.Manager;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
    List<Manager> findByFinancialInstitutionId(Long fiId);
}
