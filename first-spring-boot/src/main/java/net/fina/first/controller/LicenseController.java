package net.fina.first.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.fina.first.dto.common.ApiResponse;
import net.fina.first.dto.common.PageResponse;
import net.fina.first.dto.request.LicenseRequest;
import net.fina.first.dto.response.LicenseResponse;
import net.fina.first.model.enums.LicenseStatus;
import net.fina.first.service.LicenseService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/fi-registries/{fiRegistryId}/licenses")
@RequiredArgsConstructor
@Tag(name = "Licenses", description = "FI Registry License management endpoints")
public class LicenseController {

    private final LicenseService licenseService;

    @GetMapping
    @Operation(summary = "Get all licenses for an FI registry")
    @PreAuthorize("hasAuthority('LICENSE_READ')")
    public ResponseEntity<PageResponse<LicenseResponse>> findByFiRegistry(
            @PathVariable Long fiRegistryId,
            @RequestParam(required = false) LicenseStatus status,
            @PageableDefault(size = 20, sort = "issueDate", direction = Sort.Direction.DESC)
            Pageable pageable) {

        PageResponse<LicenseResponse> response = licenseService.findByFiRegistry(
                fiRegistryId, status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get license by ID")
    @PreAuthorize("hasAuthority('LICENSE_READ')")
    public ResponseEntity<LicenseResponse> findById(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id) {
        LicenseResponse response = licenseService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active licenses")
    @PreAuthorize("hasAuthority('LICENSE_READ')")
    public ResponseEntity<List<LicenseResponse>> findActiveLicenses(
            @PathVariable Long fiRegistryId) {
        List<LicenseResponse> response = licenseService.findActiveLicenses(fiRegistryId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create new license")
    @PreAuthorize("hasAuthority('LICENSE_CREATE')")
    public ResponseEntity<LicenseResponse> create(
            @PathVariable Long fiRegistryId,
            @Valid @RequestBody LicenseRequest request) {
        LicenseResponse response = licenseService.create(fiRegistryId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update license")
    @PreAuthorize("hasAuthority('LICENSE_UPDATE')")
    public ResponseEntity<LicenseResponse> update(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id,
            @Valid @RequestBody LicenseRequest request) {
        LicenseResponse response = licenseService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/activate")
    @Operation(summary = "Activate license")
    @PreAuthorize("hasAuthority('LICENSE_ACTIVATE')")
    public ResponseEntity<LicenseResponse> activate(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id) {
        LicenseResponse response = licenseService.activate(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/suspend")
    @Operation(summary = "Suspend license")
    @PreAuthorize("hasAuthority('LICENSE_SUSPEND')")
    public ResponseEntity<LicenseResponse> suspend(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id,
            @RequestParam String reason) {
        LicenseResponse response = licenseService.suspend(id, reason);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/revoke")
    @Operation(summary = "Revoke license")
    @PreAuthorize("hasAuthority('LICENSE_REVOKE')")
    public ResponseEntity<LicenseResponse> revoke(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id,
            @RequestParam String reason) {
        LicenseResponse response = licenseService.revoke(id, reason);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reactivate")
    @Operation(summary = "Reactivate suspended license")
    @PreAuthorize("hasAuthority('LICENSE_REACTIVATE')")
    public ResponseEntity<LicenseResponse> reactivate(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id) {
        LicenseResponse response = licenseService.reactivate(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete license (soft delete)")
    @PreAuthorize("hasAuthority('LICENSE_DELETE')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id) {
        licenseService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("License deleted successfully"));
    }
}
