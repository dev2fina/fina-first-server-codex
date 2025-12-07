package net.fina.first.repository;

import net.fina.first.model.Branch;
import net.fina.first.model.enums.BranchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Branch entity operations.
 */
@Repository
public interface BranchRepository extends JpaRepository<Branch, Long>, JpaSpecificationExecutor<Branch> {

    List<Branch> findByFiRegistryId(Long fiRegistryId);

    Page<Branch> findByFiRegistryId(Long fiRegistryId, Pageable pageable);

    Optional<Branch> findByFiRegistryIdAndCode(Long fiRegistryId, String code);

    @Query("SELECT b FROM Branch b WHERE b.fiRegistry.id = :fiRegistryId AND b.deleted = false")
    List<Branch> findActiveByFiRegistryId(@Param("fiRegistryId") Long fiRegistryId);

    @Query("SELECT b FROM Branch b WHERE b.fiRegistry.id = :fiRegistryId AND b.status = :status AND b.deleted = false")
    List<Branch> findByFiRegistryIdAndStatus(
            @Param("fiRegistryId") Long fiRegistryId,
            @Param("status") BranchStatus status);

    @Query("SELECT b FROM Branch b WHERE b.region.id = :regionId AND b.deleted = false")
    List<Branch> findByRegionId(@Param("regionId") Long regionId);

    @Query("SELECT COUNT(b) FROM Branch b WHERE b.fiRegistry.id = :fiRegistryId AND b.deleted = false")
    long countByFiRegistryId(@Param("fiRegistryId") Long fiRegistryId);

    @Query("SELECT COUNT(b) FROM Branch b WHERE b.fiRegistry.id = :fiRegistryId AND b.status = :status AND b.deleted = false")
    long countByFiRegistryIdAndStatus(
            @Param("fiRegistryId") Long fiRegistryId,
            @Param("status") BranchStatus status);

    boolean existsByFiRegistryIdAndCode(Long fiRegistryId, String code);
}
