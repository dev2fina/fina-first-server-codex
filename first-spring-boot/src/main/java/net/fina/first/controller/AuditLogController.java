package net.fina.first.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.fina.first.dto.common.PageResponse;
import net.fina.first.model.AuditLog;
import net.fina.first.service.AuditLogService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Audit Logs", description = "Audit log viewing endpoints")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @Operation(summary = "Search audit logs")
    @PreAuthorize("hasAuthority('AUDIT_LOG_READ')")
    public ResponseEntity<PageResponse<AuditLog>> search(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String username,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @PageableDefault(size = 50, sort = "timestamp", direction = Sort.Direction.DESC)
            Pageable pageable) {

        PageResponse<AuditLog> response = auditLogService.search(
                entityType, action, username, from, to, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    @Operation(summary = "Get audit logs for a specific entity")
    @PreAuthorize("hasAuthority('AUDIT_LOG_READ')")
    public ResponseEntity<PageResponse<AuditLog>> findByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId,
            @PageableDefault(size = 50, sort = "timestamp", direction = Sort.Direction.DESC)
            Pageable pageable) {

        PageResponse<AuditLog> response = auditLogService.findByEntityTypeAndEntityId(
                entityType, entityId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{username}")
    @Operation(summary = "Get audit logs by username")
    @PreAuthorize("hasAuthority('AUDIT_LOG_READ')")
    public ResponseEntity<PageResponse<AuditLog>> findByUsername(
            @PathVariable String username,
            @PageableDefault(size = 50, sort = "timestamp", direction = Sort.Direction.DESC)
            Pageable pageable) {

        PageResponse<AuditLog> response = auditLogService.findByUsername(username, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/action/{action}")
    @Operation(summary = "Get audit logs by action type")
    @PreAuthorize("hasAuthority('AUDIT_LOG_READ')")
    public ResponseEntity<PageResponse<AuditLog>> findByAction(
            @PathVariable String action,
            @PageableDefault(size = 50, sort = "timestamp", direction = Sort.Direction.DESC)
            Pageable pageable) {

        PageResponse<AuditLog> response = auditLogService.findByAction(action, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get audit logs by date range")
    @PreAuthorize("hasAuthority('AUDIT_LOG_READ')")
    public ResponseEntity<PageResponse<AuditLog>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @PageableDefault(size = 50, sort = "timestamp", direction = Sort.Direction.DESC)
            Pageable pageable) {

        PageResponse<AuditLog> response = auditLogService.findByDateRange(from, to, pageable);
        return ResponseEntity.ok(response);
    }
}
