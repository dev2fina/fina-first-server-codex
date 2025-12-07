package net.fina.first.repository;

import net.fina.first.model.FiRegistryAction;
import net.fina.first.model.enums.ActionStatus;
import net.fina.first.model.enums.ActionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for FiRegistryAction entity operations.
 */
@Repository
public interface FiRegistryActionRepository extends JpaRepository<FiRegistryAction, Long>, JpaSpecificationExecutor<FiRegistryAction> {

    List<FiRegistryAction> findByFiRegistryId(Long fiRegistryId);

    Page<FiRegistryAction> findByFiRegistryId(Long fiRegistryId, Pageable pageable);

    @Query("SELECT a FROM FiRegistryAction a WHERE a.fiRegistry.id = :fiRegistryId AND a.deleted = false ORDER BY a.createdAt DESC")
    List<FiRegistryAction> findByFiRegistryIdOrderByCreatedAtDesc(@Param("fiRegistryId") Long fiRegistryId);

    @Query("SELECT a FROM FiRegistryAction a WHERE a.fiRegistry.id = :fiRegistryId AND a.type = :type AND a.deleted = false")
    List<FiRegistryAction> findByFiRegistryIdAndType(
            @Param("fiRegistryId") Long fiRegistryId,
            @Param("type") ActionType type);

    @Query("SELECT a FROM FiRegistryAction a WHERE a.fiRegistry.id = :fiRegistryId AND a.status = :status AND a.deleted = false")
    List<FiRegistryAction> findByFiRegistryIdAndStatus(
            @Param("fiRegistryId") Long fiRegistryId,
            @Param("status") ActionStatus status);

    @Query("SELECT a FROM FiRegistryAction a WHERE a.status = :status AND a.deleted = false ORDER BY a.createdAt DESC")
    Page<FiRegistryAction> findByStatus(@Param("status") ActionStatus status, Pageable pageable);

    @Query("SELECT a FROM FiRegistryAction a WHERE a.author = :author AND a.deleted = false ORDER BY a.createdAt DESC")
    Page<FiRegistryAction> findByAuthor(@Param("author") String author, Pageable pageable);

    Optional<FiRegistryAction> findByProcessId(String processId);

    @Query("SELECT a FROM FiRegistryAction a WHERE a.fiRegistry.id = :fiRegistryId AND a.status NOT IN ('COMPLETED', 'CANCELLED', 'REJECTED') AND a.deleted = false")
    List<FiRegistryAction> findPendingActionsByFiRegistryId(@Param("fiRegistryId") Long fiRegistryId);

    @Query("SELECT a FROM FiRegistryAction a WHERE a.createdAt BETWEEN :startDate AND :endDate AND a.deleted = false")
    List<FiRegistryAction> findByCreatedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(a) FROM FiRegistryAction a WHERE a.fiRegistry.id = :fiRegistryId AND a.deleted = false")
    long countByFiRegistryId(@Param("fiRegistryId") Long fiRegistryId);

    @Query("SELECT COUNT(a) FROM FiRegistryAction a WHERE a.status = :status AND a.deleted = false")
    long countByStatus(@Param("status") ActionStatus status);
}
