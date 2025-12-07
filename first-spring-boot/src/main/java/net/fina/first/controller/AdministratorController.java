package net.fina.first.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.fina.first.dto.common.ApiResponse;
import net.fina.first.dto.common.PageResponse;
import net.fina.first.dto.request.AdministratorRequest;
import net.fina.first.dto.response.AdministratorResponse;
import net.fina.first.service.AdministratorService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fi-registries/{fiRegistryId}/administrators")
@RequiredArgsConstructor
@Tag(name = "Administrators", description = "FI Registry Administrator management endpoints")
public class AdministratorController {

    private final AdministratorService administratorService;

    @GetMapping
    @Operation(summary = "Get all administrators for an FI registry")
    @PreAuthorize("hasAuthority('ADMINISTRATOR_READ')")
    public ResponseEntity<PageResponse<AdministratorResponse>> findByFiRegistry(
            @PathVariable Long fiRegistryId,
            @RequestParam(required = false) Boolean activeOnly,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "lastName", direction = Sort.Direction.ASC)
            Pageable pageable) {

        PageResponse<AdministratorResponse> response = administratorService.findByFiRegistry(
                fiRegistryId, activeOnly, search, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get administrator by ID")
    @PreAuthorize("hasAuthority('ADMINISTRATOR_READ')")
    public ResponseEntity<AdministratorResponse> findById(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id) {
        AdministratorResponse response = administratorService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active administrators")
    @PreAuthorize("hasAuthority('ADMINISTRATOR_READ')")
    public ResponseEntity<List<AdministratorResponse>> findActiveAdministrators(
            @PathVariable Long fiRegistryId) {
        List<AdministratorResponse> response = administratorService.findActiveAdministrators(fiRegistryId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create new administrator")
    @PreAuthorize("hasAuthority('ADMINISTRATOR_CREATE')")
    public ResponseEntity<AdministratorResponse> create(
            @PathVariable Long fiRegistryId,
            @Valid @RequestBody AdministratorRequest request) {
        AdministratorResponse response = administratorService.create(fiRegistryId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update administrator")
    @PreAuthorize("hasAuthority('ADMINISTRATOR_UPDATE')")
    public ResponseEntity<AdministratorResponse> update(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id,
            @Valid @RequestBody AdministratorRequest request) {
        AdministratorResponse response = administratorService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/terminate")
    @Operation(summary = "Terminate administrator")
    @PreAuthorize("hasAuthority('ADMINISTRATOR_TERMINATE')")
    public ResponseEntity<AdministratorResponse> terminate(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        AdministratorResponse response = administratorService.terminate(id, reason);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete administrator (soft delete)")
    @PreAuthorize("hasAuthority('ADMINISTRATOR_DELETE')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id) {
        administratorService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Administrator deleted successfully"));
    }
}
