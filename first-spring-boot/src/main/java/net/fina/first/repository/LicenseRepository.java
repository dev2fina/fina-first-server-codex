package net.fina.first.repository;

import net.fina.first.model.License;
import net.fina.first.model.enums.LicenseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for License entity operations.
 */
@Repository
public interface LicenseRepository extends JpaRepository<License, Long>, JpaSpecificationExecutor<License> {

    List<License> findByFiRegistryId(Long fiRegistryId);

    Page<License> findByFiRegistryId(Long fiRegistryId, Pageable pageable);

    Optional<License> findByLicenseNumber(String licenseNumber);

    @Query("SELECT l FROM License l WHERE l.fiRegistry.id = :fiRegistryId AND l.deleted = false")
    List<License> findActiveByFiRegistryId(@Param("fiRegistryId") Long fiRegistryId);

    @Query("SELECT l FROM License l WHERE l.fiRegistry.id = :fiRegistryId AND l.status = :status AND l.deleted = false")
    List<License> findByFiRegistryIdAndStatus(
            @Param("fiRegistryId") Long fiRegistryId,
            @Param("status") LicenseStatus status);

    @Query("SELECT l FROM License l WHERE l.fiRegistry.id = :fiRegistryId AND l.status = :status AND l.deleted = false")
    Page<License> findByFiRegistryIdAndStatus(
            @Param("fiRegistryId") Long fiRegistryId,
            @Param("status") LicenseStatus status,
            Pageable pageable);

    @Query("SELECT l FROM License l WHERE l.fiRegistry.id = :fiRegistryId AND l.deleted = false")
    Page<License> findByFiRegistryIdActive(@Param("fiRegistryId") Long fiRegistryId, Pageable pageable);

    @Query("SELECT l FROM License l WHERE l.status = :status AND l.expirationDate BETWEEN :startDate AND :endDate AND l.deleted = false")
    List<License> findExpiringLicenses(
            @Param("status") LicenseStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT l FROM License l WHERE l.status = 'ACTIVE' AND l.expirationDate < :date AND l.deleted = false")
    List<License> findExpiredLicenses(@Param("date") LocalDate date);

    @Query("SELECT l FROM License l WHERE l.licenseType.id = :licenseTypeId AND l.deleted = false")
    List<License> findByLicenseTypeId(@Param("licenseTypeId") Long licenseTypeId);

    @Query("SELECT COUNT(l) FROM License l WHERE l.fiRegistry.id = :fiRegistryId AND l.deleted = false")
    long countByFiRegistryId(@Param("fiRegistryId") Long fiRegistryId);

    @Query("SELECT COUNT(l) FROM License l WHERE l.fiRegistry.id = :fiRegistryId AND l.status = 'ACTIVE' AND l.deleted = false")
    long countActiveByFiRegistryId(@Param("fiRegistryId") Long fiRegistryId);

    boolean existsByLicenseNumber(String licenseNumber);
}
