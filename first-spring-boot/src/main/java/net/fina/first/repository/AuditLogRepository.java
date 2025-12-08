package net.fina.first.repository;

import net.fina.first.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for AuditLog entity operations.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long>, JpaSpecificationExecutor<AuditLog> {

    @Query("SELECT a FROM AuditLog a WHERE a.entityId = :entityId AND a.entityName = :entityName ORDER BY a.relevanceTime DESC")
    List<AuditLog> findByEntityIdAndEntityName(
            @Param("entityId") Long entityId,
            @Param("entityName") String entityName);

    @Query("SELECT a FROM AuditLog a WHERE a.entityName = :entityName AND a.entityId = :entityId ORDER BY a.relevanceTime DESC")
    Page<AuditLog> findByEntityNameAndEntityIdOrderByRelevanceTimeDesc(
            @Param("entityName") String entityName,
            @Param("entityId") Long entityId,
            Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.actorLogin = :actorLogin ORDER BY a.relevanceTime DESC")
    Page<AuditLog> findByActorLogin(@Param("actorLogin") String actorLogin, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.relevanceTime BETWEEN :startDate AND :endDate ORDER BY a.relevanceTime DESC")
    Page<AuditLog> findByRelevanceTimeBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.operationType = :operationType ORDER BY a.relevanceTime DESC")
    Page<AuditLog> findByOperationType(
            @Param("operationType") AuditLog.OperationType operationType,
            Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:entityName IS NULL OR a.entityName = :entityName) AND " +
           "(:operationType IS NULL OR a.operationType = :operationType) AND " +
           "(:actorLogin IS NULL OR a.actorLogin = :actorLogin) AND " +
           "(:fromDate IS NULL OR a.relevanceTime >= :fromDate) AND " +
           "(:toDate IS NULL OR a.relevanceTime <= :toDate) " +
           "ORDER BY a.relevanceTime DESC")
    Page<AuditLog> searchAuditLogs(
            @Param("entityName") String entityName,
            @Param("operationType") AuditLog.OperationType operationType,
            @Param("actorLogin") String actorLogin,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
}
