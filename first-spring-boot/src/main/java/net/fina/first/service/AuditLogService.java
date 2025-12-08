package net.fina.first.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fina.first.dto.common.PageResponse;
import net.fina.first.model.AuditLog;
import net.fina.first.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String action, String entityType, Long entityId,
                    String oldValue, String newValue) {
        try {
            AuditLog auditLog = createAuditLog(action, entityType, entityId, oldValue, newValue);
            auditLogRepository.save(auditLog);
            log.debug("Audit log created: {} on {} #{}", action, entityType, entityId);
        } catch (Exception e) {
            log.error("Failed to create audit log: {}", e.getMessage(), e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logSync(String action, String entityType, Long entityId,
                        String oldValue, String newValue) {
        AuditLog auditLog = createAuditLog(action, entityType, entityId, oldValue, newValue);
        auditLogRepository.save(auditLog);
    }

    private AuditLog createAuditLog(String action, String entityType, Long entityId,
                                     String oldValue, String newValue) {
        AuditLog auditLog = new AuditLog();
        auditLog.setOperationType(mapActionToOperationType(action));
        auditLog.setEntityName(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setOldValue(truncateIfNeeded(oldValue));
        auditLog.setNewValue(truncateIfNeeded(newValue));
        auditLog.setRelevanceTime(LocalDateTime.now());
        auditLog.setActorLogin(getCurrentUsername());
        auditLog.setClientIp(getCurrentIpAddress());
        auditLog.setUserAgent(getCurrentUserAgent());
        auditLog.setDescription(action);
        return auditLog;
    }

    private AuditLog.OperationType mapActionToOperationType(String action) {
        if (action == null) return AuditLog.OperationType.UPDATE;

        String upperAction = action.toUpperCase();
        if (upperAction.contains("CREATE") || upperAction.contains("UPLOAD")) {
            return AuditLog.OperationType.CREATE;
        } else if (upperAction.contains("UPDATE") || upperAction.contains("CHANGE") || upperAction.contains("RESET")) {
            return AuditLog.OperationType.UPDATE;
        } else if (upperAction.contains("DELETE") || upperAction.contains("DEACTIVATE")) {
            return AuditLog.OperationType.DELETE;
        } else if (upperAction.contains("LOGIN")) {
            return AuditLog.OperationType.LOGIN;
        } else if (upperAction.contains("LOGOUT")) {
            return AuditLog.OperationType.LOGOUT;
        } else if (upperAction.contains("PASSWORD")) {
            return AuditLog.OperationType.PASSWORD_CHANGE;
        } else if (upperAction.contains("GENERATE")) {
            return AuditLog.OperationType.GENERATE;
        } else if (upperAction.contains("EXPORT")) {
            return AuditLog.OperationType.EXPORT;
        } else if (upperAction.contains("IMPORT")) {
            return AuditLog.OperationType.IMPORT;
        }
        return AuditLog.OperationType.UPDATE;
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId,
                                                               Pageable pageable) {
        Page<AuditLog> page = auditLogRepository.findByEntityNameAndEntityIdOrderByRelevanceTimeDesc(
                entityType, entityId, pageable);
        return PageResponse.of(page, page.getContent());
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditLog> findByUsername(String username, Pageable pageable) {
        Page<AuditLog> page = auditLogRepository.findByActorLogin(username, pageable);
        return PageResponse.of(page, page.getContent());
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditLog> findByAction(String action, Pageable pageable) {
        AuditLog.OperationType operationType = mapActionToOperationType(action);
        Page<AuditLog> page = auditLogRepository.findByOperationType(operationType, pageable);
        return PageResponse.of(page, page.getContent());
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditLog> findByDateRange(LocalDateTime from, LocalDateTime to,
                                                   Pageable pageable) {
        Page<AuditLog> page = auditLogRepository.findByRelevanceTimeBetween(from, to, pageable);
        return PageResponse.of(page, page.getContent());
    }

    @Transactional(readOnly = true)
    public List<AuditLog> findRecentByEntity(String entityType, Long entityId, int limit) {
        return auditLogRepository.findByEntityIdAndEntityName(entityId, entityType)
                .stream()
                .limit(limit)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditLog> search(String entityType, String action, String username,
                                         LocalDateTime from, LocalDateTime to, Pageable pageable) {
        AuditLog.OperationType operationType = action != null ? mapActionToOperationType(action) : null;
        Page<AuditLog> page = auditLogRepository.searchAuditLogs(
                entityType, operationType, username, from, to, pageable);
        return PageResponse.of(page, page.getContent());
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "system";
    }

    private String getCurrentIpAddress() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.debug("Could not determine IP address: {}", e.getMessage());
        }
        return null;
    }

    private String getCurrentUserAgent() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                return attributes.getRequest().getHeader("User-Agent");
            }
        } catch (Exception e) {
            log.debug("Could not determine user agent: {}", e.getMessage());
        }
        return null;
    }

    private String truncateIfNeeded(String value) {
        if (value != null && value.length() > 4000) {
            return value.substring(0, 4000);
        }
        return value;
    }
}
