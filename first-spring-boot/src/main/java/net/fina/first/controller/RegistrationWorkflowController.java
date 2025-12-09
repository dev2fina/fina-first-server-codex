package net.fina.first.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fina.first.dto.common.ApiResponse;
import net.fina.first.dto.workflow.*;
import net.fina.first.mapper.FiRegistryMapper;
import net.fina.first.model.FiRegistry;
import net.fina.first.service.workflow.RegistrationWorkflowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for the FIRST + VASP + PSP Registration Workflow.
 * Provides endpoints for each phase of the registration process.
 */
@RestController
@RequestMapping("/v1/registrations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Registration Workflow", description = "FI Registration workflow management endpoints")
public class RegistrationWorkflowController {

    private final RegistrationWorkflowService workflowService;
    private final FiRegistryMapper fiRegistryMapper;

    // ==========================================
    // PHASE 1: Start Registration
    // ==========================================

    @PostMapping("/start")
    @Operation(summary = "Start new registration (Phase 1)",
            description = "Creates a new FI registration with Phase 1 initial data")
    @PreAuthorize("hasAuthority('FI_REGISTRY_CREATE')")
    public ResponseEntity<ApiResponse<?>> startRegistration(@Valid @RequestBody Phase1Request request) {
        log.info("Starting new registration for firm: {}", request.getFirmName());

        FiRegistry registry = workflowService.startRegistration(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(fiRegistryMapper.toResponse(registry),
                        "Registration started successfully"));
    }

    @PostMapping("/{id}/phase1/complete")
    @Operation(summary = "Complete Phase 1",
            description = "Validates and completes Phase 1, moves to Phase 2")
    @PreAuthorize("hasAuthority('FI_REGISTRY_UPDATE')")
    public ResponseEntity<ApiResponse<?>> completePhase1(@PathVariable Long id) {
        log.info("Completing Phase 1 for registration ID: {}", id);

        FiRegistry registry = workflowService.completePhase1(id);

        return ResponseEntity.ok(ApiResponse.success(fiRegistryMapper.toResponse(registry),
                "Phase 1 completed successfully"));
    }

    // ==========================================
    // PHASE 2: General Info
    // ==========================================

    @PutMapping("/{id}/phase2")
    @Operation(summary = "Update Phase 2 data",
            description = "Updates general info (address, contact, company details)")
    @PreAuthorize("hasAuthority('FI_REGISTRY_UPDATE')")
    public ResponseEntity<ApiResponse<?>> updatePhase2(
            @PathVariable Long id,
            @Valid @RequestBody Phase2Request request) {
        log.info("Updating Phase 2 for registration ID: {}", id);

        FiRegistry registry = workflowService.updatePhase2(id, request);

        return ResponseEntity.ok(ApiResponse.success(fiRegistryMapper.toResponse(registry),
                "Phase 2 data updated successfully"));
    }

    @PostMapping("/{id}/phase2/complete")
    @Operation(summary = "Complete Phase 2",
            description = "Validates and completes Phase 2, moves to Data Entry phase")
    @PreAuthorize("hasAuthority('FI_REGISTRY_UPDATE')")
    public ResponseEntity<ApiResponse<?>> completePhase2(@PathVariable Long id) {
        log.info("Completing Phase 2 for registration ID: {}", id);

        FiRegistry registry = workflowService.completePhase2(id);

        return ResponseEntity.ok(ApiResponse.success(fiRegistryMapper.toResponse(registry),
                "Phase 2 completed successfully"));
    }

    // ==========================================
    // PHASE 3: Data Entry
    // ==========================================

    @PostMapping("/{id}/data-entry/complete")
    @Operation(summary = "Complete Data Entry",
            description = "Validates managers, beneficiaries, branches, license (PSP/VASP) and moves to Questionnaire phase")
    @PreAuthorize("hasAuthority('FI_REGISTRY_UPDATE')")
    public ResponseEntity<ApiResponse<?>> completeDataEntry(@PathVariable Long id) {
        log.info("Completing Data Entry for registration ID: {}", id);

        FiRegistry registry = workflowService.completeDataEntry(id);

        return ResponseEntity.ok(ApiResponse.success(fiRegistryMapper.toResponse(registry),
                "Data entry completed successfully"));
    }

    // ==========================================
    // PHASE 4: Questionnaire
    // ==========================================

    @PutMapping("/{id}/questionnaire")
    @Operation(summary = "Submit questionnaire answers",
            description = "Submit answers for the required questionnaires")
    @PreAuthorize("hasAuthority('FI_REGISTRY_UPDATE')")
    public ResponseEntity<ApiResponse<?>> submitQuestionnaire(
            @PathVariable Long id,
            @Valid @RequestBody QuestionnaireAnswerRequest request) {
        log.info("Submitting questionnaire for registration ID: {}", id);

        FiRegistry registry = workflowService.submitQuestionnaire(id, request);

        return ResponseEntity.ok(ApiResponse.success(fiRegistryMapper.toResponse(registry),
                "Questionnaire answers submitted successfully"));
    }

    @PostMapping("/{id}/questionnaire/complete")
    @Operation(summary = "Complete Questionnaire",
            description = "Validates all obligatory questions are answered and moves to Submitted phase")
    @PreAuthorize("hasAuthority('FI_REGISTRY_UPDATE')")
    public ResponseEntity<ApiResponse<?>> completeQuestionnaire(@PathVariable Long id) {
        log.info("Completing Questionnaire for registration ID: {}", id);

        FiRegistry registry = workflowService.completeQuestionnaire(id);

        return ResponseEntity.ok(ApiResponse.success(fiRegistryMapper.toResponse(registry),
                "Questionnaire completed successfully"));
    }

    // ==========================================
    // PHASE 5: Submit to Controller
    // ==========================================

    @PostMapping("/{id}/submit-to-controller")
    @Operation(summary = "Submit to Controller",
            description = "Submit registration for controller review")
    @PreAuthorize("hasAuthority('FI_REGISTRY_SUBMIT')")
    public ResponseEntity<ApiResponse<?>> submitToController(@PathVariable Long id) {
        log.info("Submitting registration to Controller for ID: {}", id);

        FiRegistry registry = workflowService.submitToController(id);

        return ResponseEntity.ok(ApiResponse.success(fiRegistryMapper.toResponse(registry),
                "Registration submitted to controller successfully"));
    }

    // ==========================================
    // PHASE 6: Controller Review
    // ==========================================

    @PostMapping("/{id}/controller/decision")
    @Operation(summary = "Controller decision",
            description = "Controller accepts or declines the registration")
    @PreAuthorize("hasAuthority('FI_REGISTRY_APPROVE')")
    public ResponseEntity<ApiResponse<?>> controllerDecision(
            @PathVariable Long id,
            @Valid @RequestBody ControllerDecisionRequest request) {
        log.info("Controller decision for registration ID: {}, decision: {}", id, request.getDecision());

        FiRegistry registry = workflowService.controllerDecision(id, request);

        String message = switch (request.getDecision()) {
            case ACCEPT -> "Registration approved successfully";
            case DECLINE -> "Registration declined, sent for correction";
            case REQUEST_INFO -> "Additional information requested from FI";
        };

        return ResponseEntity.ok(ApiResponse.success(fiRegistryMapper.toResponse(registry), message));
    }

    // ==========================================
    // PHASE 7: Correction
    // ==========================================

    @PostMapping("/{id}/resubmit")
    @Operation(summary = "Resubmit after correction",
            description = "Resubmit registration after making corrections")
    @PreAuthorize("hasAuthority('FI_REGISTRY_SUBMIT')")
    public ResponseEntity<ApiResponse<?>> resubmitAfterCorrection(@PathVariable Long id) {
        log.info("Resubmitting registration after correction for ID: {}", id);

        FiRegistry registry = workflowService.resubmitAfterCorrection(id);

        return ResponseEntity.ok(ApiResponse.success(fiRegistryMapper.toResponse(registry),
                "Registration resubmitted successfully"));
    }

    // ==========================================
    // PHASE 8 & 9: Finalize Registration
    // ==========================================

    @PostMapping("/{id}/finalize")
    @Operation(summary = "Finalize registration",
            description = "Complete the registration process after document generation")
    @PreAuthorize("hasAuthority('FI_REGISTRY_APPROVE')")
    public ResponseEntity<ApiResponse<?>> finalizeRegistration(@PathVariable Long id) {
        log.info("Finalizing registration for ID: {}", id);

        FiRegistry registry = workflowService.finalizeRegistration(id);

        return ResponseEntity.ok(ApiResponse.success(fiRegistryMapper.toResponse(registry),
                "Registration finalized successfully. Registration Number: " + registry.getRegistrationNumber()));
    }

    // ==========================================
    // Workflow Status
    // ==========================================

    @GetMapping("/{id}/workflow-status")
    @Operation(summary = "Get workflow status",
            description = "Get detailed workflow status including current phase, validation errors, and available actions")
    @PreAuthorize("hasAuthority('FI_REGISTRY_READ')")
    public ResponseEntity<WorkflowStatusResponse> getWorkflowStatus(@PathVariable Long id) {
        log.info("Getting workflow status for registration ID: {}", id);

        WorkflowStatusResponse status = workflowService.getWorkflowStatus(id);

        return ResponseEntity.ok(status);
    }
}
