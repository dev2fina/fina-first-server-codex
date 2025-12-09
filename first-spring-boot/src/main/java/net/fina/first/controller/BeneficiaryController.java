package net.fina.first.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.fina.first.dto.common.ApiResponse;
import net.fina.first.dto.common.PageResponse;
import net.fina.first.dto.request.BeneficiaryRequest;
import net.fina.first.dto.response.BeneficiaryResponse;
import net.fina.first.model.enums.BeneficiaryType;
import net.fina.first.service.BeneficiaryService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/fi-registries/{fiRegistryId}/beneficiaries")
@RequiredArgsConstructor
@Tag(name = "Beneficiaries", description = "FI Registry Beneficiary management endpoints")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    @GetMapping
    @Operation(summary = "Get all beneficiaries for an FI registry")
    @PreAuthorize("hasAuthority('BENEFICIARY_READ')")
    public ResponseEntity<PageResponse<BeneficiaryResponse>> findByFiRegistry(
            @PathVariable Long fiRegistryId,
            @RequestParam(required = false) BeneficiaryType type,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable) {

        PageResponse<BeneficiaryResponse> response = beneficiaryService.findByFiRegistry(
                fiRegistryId, type, search, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get beneficiary by ID")
    @PreAuthorize("hasAuthority('BENEFICIARY_READ')")
    public ResponseEntity<BeneficiaryResponse> findById(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id) {
        BeneficiaryResponse response = beneficiaryService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ultimate")
    @Operation(summary = "Get ultimate beneficial owners")
    @PreAuthorize("hasAuthority('BENEFICIARY_READ')")
    public ResponseEntity<List<BeneficiaryResponse>> findUltimateBeneficiaries(
            @PathVariable Long fiRegistryId) {
        List<BeneficiaryResponse> response = beneficiaryService.findUltimateBeneficiaries(fiRegistryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hierarchy")
    @Operation(summary = "Get beneficiary hierarchy (tree structure)")
    @PreAuthorize("hasAuthority('BENEFICIARY_READ')")
    public ResponseEntity<List<BeneficiaryResponse>> findBeneficiaryHierarchy(
            @PathVariable Long fiRegistryId) {
        List<BeneficiaryResponse> response = beneficiaryService.findBeneficiaryHierarchy(fiRegistryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/children")
    @Operation(summary = "Get child beneficiaries")
    @PreAuthorize("hasAuthority('BENEFICIARY_READ')")
    public ResponseEntity<List<BeneficiaryResponse>> findChildBeneficiaries(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id) {
        List<BeneficiaryResponse> response = beneficiaryService.findChildBeneficiaries(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create new beneficiary")
    @PreAuthorize("hasAuthority('BENEFICIARY_CREATE')")
    public ResponseEntity<BeneficiaryResponse> create(
            @PathVariable Long fiRegistryId,
            @Valid @RequestBody BeneficiaryRequest request) {
        BeneficiaryResponse response = beneficiaryService.create(fiRegistryId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update beneficiary")
    @PreAuthorize("hasAuthority('BENEFICIARY_UPDATE')")
    public ResponseEntity<BeneficiaryResponse> update(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id,
            @Valid @RequestBody BeneficiaryRequest request) {
        BeneficiaryResponse response = beneficiaryService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete beneficiary (soft delete)")
    @PreAuthorize("hasAuthority('BENEFICIARY_DELETE')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id) {
        beneficiaryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Beneficiary deleted successfully"));
    }

    @GetMapping("/{id}/calculate-ownership")
    @Operation(summary = "Calculate total ownership percentage")
    @PreAuthorize("hasAuthority('BENEFICIARY_READ')")
    public ResponseEntity<ApiResponse<Double>> calculateOwnership(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id) {
        double percentage = beneficiaryService.calculateTotalOwnership(id);
        return ResponseEntity.ok(ApiResponse.success(percentage));
    }
}
