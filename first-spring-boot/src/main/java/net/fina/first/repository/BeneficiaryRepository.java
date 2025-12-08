package net.fina.first.repository;

import net.fina.first.model.Beneficiary;
import net.fina.first.model.enums.BeneficiaryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Beneficiary entity operations.
 */
@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long>, JpaSpecificationExecutor<Beneficiary> {

    List<Beneficiary> findByFiRegistryId(Long fiRegistryId);

    Page<Beneficiary> findByFiRegistryId(Long fiRegistryId, Pageable pageable);

    @Query("SELECT b FROM Beneficiary b WHERE b.fiRegistry.id = :fiRegistryId AND b.type = :type AND b.deleted = false")
    Page<Beneficiary> findByFiRegistryIdAndType(
            @Param("fiRegistryId") Long fiRegistryId,
            @Param("type") BeneficiaryType type,
            Pageable pageable);

    @Query("SELECT b FROM Beneficiary b WHERE b.fiRegistry.id = :fiRegistryId AND b.deleted = false")
    List<Beneficiary> findActiveByFiRegistryId(@Param("fiRegistryId") Long fiRegistryId);

    @Query("SELECT b FROM Beneficiary b WHERE b.fiRegistry.id = :fiRegistryId AND b.type = :type AND b.deleted = false")
    List<Beneficiary> findByFiRegistryIdAndType(
            @Param("fiRegistryId") Long fiRegistryId,
            @Param("type") BeneficiaryType type);

    @Query("SELECT b FROM Beneficiary b WHERE b.fiRegistry.id = :fiRegistryId AND b.parent IS NULL AND b.deleted = false ORDER BY b.sequence")
    List<Beneficiary> findRootBeneficiariesByFiRegistryId(@Param("fiRegistryId") Long fiRegistryId);

    @Query("SELECT b FROM Beneficiary b WHERE b.parent.id = :parentId AND b.deleted = false ORDER BY b.sequence")
    List<Beneficiary> findByParentId(@Param("parentId") Long parentId);

    @Query("SELECT b FROM Beneficiary b WHERE b.fiRegistry.id = :fiRegistryId AND b.ultimateBeneficiary = true AND b.deleted = false")
    List<Beneficiary> findUltimateBeneficiariesByFiRegistryId(@Param("fiRegistryId") Long fiRegistryId);

    @Query("SELECT b FROM Beneficiary b WHERE b.fiRegistry.id = :fiRegistryId AND b.capitalPercentage >= :minPercentage AND b.deleted = false")
    List<Beneficiary> findByFiRegistryIdAndMinCapitalPercentage(
            @Param("fiRegistryId") Long fiRegistryId,
            @Param("minPercentage") BigDecimal minPercentage);

    Optional<Beneficiary> findByFiRegistryIdAndIdentificationNumber(Long fiRegistryId, String identificationNumber);

    Optional<Beneficiary> findByFiRegistryIdAndLegalEntityCode(Long fiRegistryId, String legalEntityCode);

    @Query("SELECT COALESCE(SUM(b.capitalPercentage), 0) FROM Beneficiary b " +
           "WHERE b.fiRegistry.id = :fiRegistryId AND b.parent IS NULL AND b.deleted = false")
    BigDecimal sumRootCapitalPercentageByFiRegistryId(@Param("fiRegistryId") Long fiRegistryId);

    @Query("SELECT COUNT(b) FROM Beneficiary b WHERE b.fiRegistry.id = :fiRegistryId AND b.deleted = false")
    long countByFiRegistryId(@Param("fiRegistryId") Long fiRegistryId);
}
