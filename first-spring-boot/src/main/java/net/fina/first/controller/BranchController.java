package net.fina.first.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.fina.first.dto.common.ApiResponse;
import net.fina.first.dto.common.PageResponse;
import net.fina.first.dto.request.BranchRequest;
import net.fina.first.dto.response.BranchResponse;
import net.fina.first.model.enums.BranchStatus;
import net.fina.first.service.BranchService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/fi-registries/{fiRegistryId}/branches")
@RequiredArgsConstructor
@Tag(name = "Branches", description = "FI Registry Branch management endpoints")
public class BranchController {

    private final BranchService branchService;

    @GetMapping
    @Operation(summary = "Get all branches for an FI registry")
    @PreAuthorize("hasAuthority('BRANCH_READ')")
    public ResponseEntity<PageResponse<BranchResponse>> findByFiRegistry(
            @PathVariable Long fiRegistryId,
            @RequestParam(required = false) BranchStatus status,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable) {

        PageResponse<BranchResponse> response = branchService.findByFiRegistry(
                fiRegistryId, status, search, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get branch by ID")
    @PreAuthorize("hasAuthority('BRANCH_READ')")
    public ResponseEntity<BranchResponse> findById(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id) {
        BranchResponse response = branchService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/head-office")
    @Operation(summary = "Get head office branch")
    @PreAuthorize("hasAuthority('BRANCH_READ')")
    public ResponseEntity<BranchResponse> getHeadOffice(@PathVariable Long fiRegistryId) {
        BranchResponse response = branchService.getHeadOffice(fiRegistryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active branches")
    @PreAuthorize("hasAuthority('BRANCH_READ')")
    public ResponseEntity<List<BranchResponse>> findActiveBranches(
            @PathVariable Long fiRegistryId) {
        List<BranchResponse> response = branchService.findActiveBranches(fiRegistryId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create new branch")
    @PreAuthorize("hasAuthority('BRANCH_CREATE')")
    public ResponseEntity<BranchResponse> create(
            @PathVariable Long fiRegistryId,
            @Valid @RequestBody BranchRequest request) {
        BranchResponse response = branchService.create(fiRegistryId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update branch")
    @PreAuthorize("hasAuthority('BRANCH_UPDATE')")
    public ResponseEntity<BranchResponse> update(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id,
            @Valid @RequestBody BranchRequest request) {
        BranchResponse response = branchService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/activate")
    @Operation(summary = "Activate branch")
    @PreAuthorize("hasAuthority('BRANCH_ACTIVATE')")
    public ResponseEntity<BranchResponse> activate(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id) {
        BranchResponse response = branchService.activate(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "Close branch")
    @PreAuthorize("hasAuthority('BRANCH_CLOSE')")
    public ResponseEntity<BranchResponse> close(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        BranchResponse response = branchService.close(id, reason);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete branch (soft delete)")
    @PreAuthorize("hasAuthority('BRANCH_DELETE')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id) {
        branchService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Branch deleted successfully"));
    }
}
