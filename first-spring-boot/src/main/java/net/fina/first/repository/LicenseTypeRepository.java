package net.fina.first.repository;

import net.fina.first.model.LicenseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for LicenseType entity operations.
 */
@Repository
public interface LicenseTypeRepository extends JpaRepository<LicenseType, Long> {

    Optional<LicenseType> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT l FROM LicenseType l WHERE l.active = true ORDER BY l.sequence")
    List<LicenseType> findAllActive();

    @Query("SELECT l FROM LicenseType l WHERE l.renewable = true AND l.active = true ORDER BY l.sequence")
    List<LicenseType> findRenewableTypes();
}
