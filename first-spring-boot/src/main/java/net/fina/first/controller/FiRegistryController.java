package net.fina.first.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.fina.first.dto.common.ApiResponse;
import net.fina.first.dto.common.PageResponse;
import net.fina.first.dto.request.FiRegistryCreateRequest;
import net.fina.first.dto.request.FiRegistryUpdateRequest;
import net.fina.first.dto.response.FiRegistryResponse;
import net.fina.first.model.enums.FiTypeCode;
import net.fina.first.model.enums.RegistrationStatus;
import net.fina.first.service.FiRegistryService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fi-registries")
@RequiredArgsConstructor
@Tag(name = "FI Registry", description = "Financial Institution Registry management endpoints")
public class FiRegistryController {

    private final FiRegistryService fiRegistryService;

    @GetMapping
    @Operation(summary = "Get all FI registries with filtering and pagination")
    @PreAuthorize("hasAuthority('FI_REGISTRY_READ')")
    public ResponseEntity<PageResponse<FiRegistryResponse>> findAll(
            @Parameter(description = "Search by name or code")
            @RequestParam(required = false) String search,
            @Parameter(description = "Filter by FI type")
            @RequestParam(required = false) FiTypeCode fiTypeCode,
            @Parameter(description = "Filter by status")
            @RequestParam(required = false) RegistrationStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        PageResponse<FiRegistryResponse> response = fiRegistryService.findAll(
                search, fiTypeCode, status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get FI registry by ID")
    @PreAuthorize("hasAuthority('FI_REGISTRY_READ')")
    public ResponseEntity<FiRegistryResponse> findById(@PathVariable Long id) {
        FiRegistryResponse response = fiRegistryService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get FI registry by code")
    @PreAuthorize("hasAuthority('FI_REGISTRY_READ')")
    public ResponseEntity<FiRegistryResponse> findByCode(@PathVariable String code) {
        FiRegistryResponse response = fiRegistryService.findByCode(code);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create new FI registry")
    @PreAuthorize("hasAuthority('FI_REGISTRY_CREATE')")
    public ResponseEntity<FiRegistryResponse> create(
            @Valid @RequestBody FiRegistryCreateRequest request) {
        FiRegistryResponse response = fiRegistryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update FI registry")
    @PreAuthorize("hasAuthority('FI_REGISTRY_UPDATE')")
    public ResponseEntity<FiRegistryResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody FiRegistryUpdateRequest request) {
        FiRegistryResponse response = fiRegistryService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/submit")
    @Operation(summary = "Submit FI registry for approval")
    @PreAuthorize("hasAuthority('FI_REGISTRY_SUBMIT')")
    public ResponseEntity<FiRegistryResponse> submit(@PathVariable Long id) {
        FiRegistryResponse response = fiRegistryService.submit(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve FI registry")
    @PreAuthorize("hasAuthority('FI_REGISTRY_APPROVE')")
    public ResponseEntity<FiRegistryResponse> approve(
            @PathVariable Long id,
            @RequestParam(required = false) String comment) {
        FiRegistryResponse response = fiRegistryService.approve(id, comment);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject FI registry")
    @PreAuthorize("hasAuthority('FI_REGISTRY_REJECT')")
    public ResponseEntity<FiRegistryResponse> reject(
            @PathVariable Long id,
            @RequestParam String reason) {
        FiRegistryResponse response = fiRegistryService.reject(id, reason);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete FI registry (soft delete)")
    @PreAuthorize("hasAuthority('FI_REGISTRY_DELETE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        fiRegistryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("FI Registry deleted successfully"));
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Get FI registry action history")
    @PreAuthorize("hasAuthority('FI_REGISTRY_READ')")
    public ResponseEntity<PageResponse<FiRegistryResponse>> getHistory(
            @PathVariable Long id,
            @PageableDefault(size = 20) Pageable pageable) {
        // This would return the action history - simplified for now
        return ResponseEntity.ok(PageResponse.empty());
    }
}
