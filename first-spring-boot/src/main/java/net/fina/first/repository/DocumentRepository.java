package net.fina.first.repository;

import net.fina.first.model.Document;
import net.fina.first.model.enums.DocumentType;
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
 * Repository for Document entity operations.
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {

    List<Document> findByFiRegistryId(Long fiRegistryId);

    Page<Document> findByFiRegistryId(Long fiRegistryId, Pageable pageable);

    List<Document> findByActionId(Long actionId);

    Optional<Document> findByFileUuid(String fileUuid);

    @Query("SELECT d FROM Document d WHERE d.fiRegistry.id = :fiRegistryId AND d.deleted = false")
    List<Document> findActiveByFiRegistryId(@Param("fiRegistryId") Long fiRegistryId);

    @Query("SELECT d FROM Document d WHERE d.fiRegistry.id = :fiRegistryId AND d.documentType = :documentType AND d.deleted = false")
    List<Document> findByFiRegistryIdAndDocumentType(
            @Param("fiRegistryId") Long fiRegistryId,
            @Param("documentType") DocumentType documentType);

    @Query("SELECT d FROM Document d WHERE d.action.id = :actionId AND d.deleted = false")
    List<Document> findActiveByActionId(@Param("actionId") Long actionId);

    @Query("SELECT d FROM Document d WHERE d.branch.id = :branchId AND d.deleted = false")
    List<Document> findActiveByBranchId(@Param("branchId") Long branchId);

    @Query("SELECT COUNT(d) FROM Document d WHERE d.fiRegistry.id = :fiRegistryId AND d.deleted = false")
    long countByFiRegistryId(@Param("fiRegistryId") Long fiRegistryId);
}
