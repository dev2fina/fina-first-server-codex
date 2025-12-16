package net.fina.first.repository.registry;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import net.fina.first.model.registry.License;
import net.fina.first.model.registry.LicenseType;

public interface LicenseRepository extends JpaRepository<License, Long> {
    List<License> findByFinancialInstitutionId(Long fiId);

    boolean existsByFinancialInstitutionIdAndLicenseType(Long fiId, LicenseType type);
}
