package net.fina.first.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.fina.first.dto.response.FiTypeResponse;
import net.fina.first.dto.response.LegalFormResponse;
import net.fina.first.dto.response.LicenseTypeResponse;
import net.fina.first.dto.response.RegionResponse;
import net.fina.first.model.enums.FiTypeCode;
import net.fina.first.service.FiTypeService;
import net.fina.first.service.LegalFormService;
import net.fina.first.service.LicenseTypeService;
import net.fina.first.service.RegionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/reference-data")
@RequiredArgsConstructor
@Tag(name = "Reference Data", description = "Reference data lookup endpoints")
public class ReferenceDataController {

    private final FiTypeService fiTypeService;
    private final RegionService regionService;
    private final LegalFormService legalFormService;
    private final LicenseTypeService licenseTypeService;

    @GetMapping("/fi-types")
    @Operation(summary = "Get all FI types")
    public ResponseEntity<List<FiTypeResponse>> getAllFiTypes() {
        return ResponseEntity.ok(fiTypeService.findAll());
    }

    @GetMapping("/fi-types/{code}")
    @Operation(summary = "Get FI type by code")
    public ResponseEntity<FiTypeResponse> getFiTypeByCode(@PathVariable FiTypeCode code) {
        return ResponseEntity.ok(fiTypeService.findByCode(code));
    }

    @GetMapping("/regions")
    @Operation(summary = "Get all regions")
    public ResponseEntity<List<RegionResponse>> getAllRegions() {
        return ResponseEntity.ok(regionService.findAll());
    }

    @GetMapping("/regions/{id}")
    @Operation(summary = "Get region by ID")
    public ResponseEntity<RegionResponse> getRegionById(@PathVariable Long id) {
        return ResponseEntity.ok(regionService.findById(id));
    }

    @GetMapping("/regions/{parentId}/children")
    @Operation(summary = "Get child regions")
    public ResponseEntity<List<RegionResponse>> getChildRegions(@PathVariable Long parentId) {
        return ResponseEntity.ok(regionService.findChildren(parentId));
    }

    @GetMapping("/regions/roots")
    @Operation(summary = "Get root regions (no parent)")
    public ResponseEntity<List<RegionResponse>> getRootRegions() {
        return ResponseEntity.ok(regionService.findRoots());
    }

    @GetMapping("/legal-forms")
    @Operation(summary = "Get all legal forms")
    public ResponseEntity<List<LegalFormResponse>> getAllLegalForms() {
        return ResponseEntity.ok(legalFormService.findAll());
    }

    @GetMapping("/legal-forms/active")
    @Operation(summary = "Get active legal forms")
    public ResponseEntity<List<LegalFormResponse>> getActiveLegalForms() {
        return ResponseEntity.ok(legalFormService.findActive());
    }

    @GetMapping("/legal-forms/{id}")
    @Operation(summary = "Get legal form by ID")
    public ResponseEntity<LegalFormResponse> getLegalFormById(@PathVariable Long id) {
        return ResponseEntity.ok(legalFormService.findById(id));
    }

    @GetMapping("/legal-forms/code/{code}")
    @Operation(summary = "Get legal form by code")
    public ResponseEntity<LegalFormResponse> getLegalFormByCode(@PathVariable String code) {
        return ResponseEntity.ok(legalFormService.findByCode(code));
    }

    @GetMapping("/license-types")
    @Operation(summary = "Get all license types")
    public ResponseEntity<List<LicenseTypeResponse>> getAllLicenseTypes() {
        return ResponseEntity.ok(licenseTypeService.findAll());
    }

    @GetMapping("/license-types/fi-type/{fiTypeCode}")
    @Operation(summary = "Get license types by FI type")
    public ResponseEntity<List<LicenseTypeResponse>> getLicenseTypesByFiType(
            @PathVariable FiTypeCode fiTypeCode) {
        return ResponseEntity.ok(licenseTypeService.findByFiType(fiTypeCode));
    }

    @GetMapping("/license-types/{id}")
    @Operation(summary = "Get license type by ID")
    public ResponseEntity<LicenseTypeResponse> getLicenseTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(licenseTypeService.findById(id));
    }
}
