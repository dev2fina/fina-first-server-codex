package net.fina.first.repository.registry;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import net.fina.first.model.registry.FinancialInstitution;
import net.fina.first.model.registry.RegistrationStatus;

public interface FinancialInstitutionRepository extends JpaRepository<FinancialInstitution, Long> {
    Optional<FinancialInstitution> findByRegistryCode(String registryCode);

    long countByStatus(RegistrationStatus status);
}
