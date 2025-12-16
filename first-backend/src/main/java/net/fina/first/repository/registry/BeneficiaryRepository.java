package net.fina.first.repository.registry;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import net.fina.first.model.registry.Beneficiary;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    List<Beneficiary> findByFinancialInstitutionId(Long fiId);
}
