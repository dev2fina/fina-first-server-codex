package net.fina.first.repository;

import net.fina.first.model.FiRegistry;
import net.fina.first.model.enums.FiTypeCode;
import net.fina.first.model.enums.LicenseStatus;
import net.fina.first.model.enums.RegistrationStatus;
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
 * Repository for FiRegistry entity operations.
 */
@Repository
public interface FiRegistryRepository extends JpaRepository<FiRegistry, Long>, JpaSpecificationExecutor<FiRegistry> {

    Optional<FiRegistry> findByCode(String code);

    Optional<FiRegistry> findByIdentificationNumber(String identificationNumber);

    boolean existsByCode(String code);

    boolean existsByIdentificationNumber(String identificationNumber);

    boolean existsByEmail(String email);

    @Query("SELECT f FROM FiRegistry f WHERE f.deleted = false")
    Page<FiRegistry> findAllActive(Pageable pageable);

    @Query("SELECT f FROM FiRegistry f WHERE f.fiType.code = :fiTypeCode AND f.deleted = false")
    Page<FiRegistry> findByFiTypeCode(@Param("fiTypeCode") FiTypeCode fiTypeCode, Pageable pageable);

    @Query("SELECT f FROM FiRegistry f WHERE f.status = :status AND f.deleted = false")
    Page<FiRegistry> findByStatus(@Param("status") RegistrationStatus status, Pageable pageable);

    @Query("SELECT f FROM FiRegistry f WHERE f.fiType.code = :fiTypeCode AND f.status = :status AND f.deleted = false")
    Page<FiRegistry> findByFiTypeCodeAndStatus(
            @Param("fiTypeCode") FiTypeCode fiTypeCode,
            @Param("status") RegistrationStatus status,
            Pageable pageable);

    @Query("SELECT f FROM FiRegistry f WHERE f.licenseStatus = :licenseStatus AND f.deleted = false")
    Page<FiRegistry> findByLicenseStatus(@Param("licenseStatus") LicenseStatus licenseStatus, Pageable pageable);

    @Query("SELECT f FROM FiRegistry f WHERE f.legalAddressRegion.id = :regionId AND f.deleted = false")
    Page<FiRegistry> findByRegionId(@Param("regionId") Long regionId, Pageable pageable);

    @Query("SELECT f FROM FiRegistry f WHERE f.registrationDate BETWEEN :startDate AND :endDate AND f.deleted = false")
    List<FiRegistry> findByRegistrationDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT f FROM FiRegistry f WHERE " +
           "LOWER(f.firmName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(f.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(f.identificationNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<FiRegistry> searchByTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT COUNT(f) FROM FiRegistry f WHERE f.fiType.code = :fiTypeCode AND f.deleted = false")
    long countByFiTypeCode(@Param("fiTypeCode") FiTypeCode fiTypeCode);

    @Query("SELECT COUNT(f) FROM FiRegistry f WHERE f.status = :status AND f.deleted = false")
    long countByStatus(@Param("status") RegistrationStatus status);

    @Query("SELECT f FROM FiRegistry f LEFT JOIN FETCH f.branches WHERE f.id = :id")
    Optional<FiRegistry> findByIdWithBranches(@Param("id") Long id);

    @Query("SELECT f FROM FiRegistry f LEFT JOIN FETCH f.administrators WHERE f.id = :id")
    Optional<FiRegistry> findByIdWithAdministrators(@Param("id") Long id);

    @Query("SELECT f FROM FiRegistry f LEFT JOIN FETCH f.beneficiaries WHERE f.id = :id")
    Optional<FiRegistry> findByIdWithBeneficiaries(@Param("id") Long id);

    @Query("SELECT f FROM FiRegistry f LEFT JOIN FETCH f.licenses WHERE f.id = :id")
    Optional<FiRegistry> findByIdWithLicenses(@Param("id") Long id);

    @Query("SELECT DISTINCT f FROM FiRegistry f " +
           "LEFT JOIN FETCH f.fiType " +
           "LEFT JOIN FETCH f.legalForm " +
           "LEFT JOIN FETCH f.legalAddressRegion " +
           "WHERE f.id = :id")
    Optional<FiRegistry> findByIdWithDetails(@Param("id") Long id);
}
