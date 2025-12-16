package net.fina.first.controller.registry;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import net.fina.first.dto.registry.DecisionRequest;
import net.fina.first.dto.registry.FiRegistryDto;
import net.fina.first.dto.registry.FiRegistryRequest;
import net.fina.first.dto.registry.LicenseDto;
import net.fina.first.dto.registry.QuestionnaireSubmissionRequest;
import net.fina.first.dto.registry.SubmissionRequest;
import net.fina.first.service.registry.FiRegistrationService;

@RestController
@RequestMapping("/api/registry")
@RequiredArgsConstructor
public class FiRegistrationController {

    private final FiRegistrationService fiRegistrationService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EDITOR')")
    public ResponseEntity<FiRegistryDto> create(@Valid @RequestBody FiRegistryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fiRegistrationService.createDraft(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EDITOR')")
    public ResponseEntity<FiRegistryDto> update(@PathVariable Long id, @Valid @RequestBody FiRegistryRequest request) {
        return ResponseEntity.ok(fiRegistrationService.updateGeneralInfo(id, request));
    }

    @PostMapping("/{id}/submit-phase1")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EDITOR')")
    public ResponseEntity<FiRegistryDto> submitPhaseOne(@PathVariable Long id, @Valid @RequestBody SubmissionRequest submissionRequest) {
        return ResponseEntity.ok(fiRegistrationService.submitPhaseOne(id, submissionRequest));
    }

    @PostMapping("/{id}/phase1/review")
    @PreAuthorize("hasAuthority('CONTROLLER')")
    public ResponseEntity<FiRegistryDto> reviewPhaseOne(@PathVariable Long id,
                                                        @RequestParam boolean accept,
                                                        @Valid @RequestBody DecisionRequest decisionRequest) {
        return ResponseEntity.ok(fiRegistrationService.controllerReviewPhaseOne(id, accept, decisionRequest.getPerformedBy(), decisionRequest.getComment()));
    }

    @PostMapping("/{id}/phase2/open")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EDITOR')")
    public ResponseEntity<FiRegistryDto> openPhaseTwo(@PathVariable Long id) {
        return ResponseEntity.ok(fiRegistrationService.openPhaseTwo(id));
    }

    @PostMapping("/{id}/licenses")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EDITOR')")
    public ResponseEntity<FiRegistryDto> saveLicenses(@PathVariable Long id, @Valid @RequestBody java.util.List<LicenseDto> licenses) {
        return ResponseEntity.ok(fiRegistrationService.saveLicenses(id, licenses));
    }

    @PostMapping("/{id}/questionnaire")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EDITOR')")
    public ResponseEntity<FiRegistryDto> saveQuestionnaire(@PathVariable Long id, @Valid @RequestBody QuestionnaireSubmissionRequest request) {
        return ResponseEntity.ok(fiRegistrationService.saveQuestionnaires(id, request.getResponses()));
    }

    @PostMapping("/{id}/submit-phase2")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EDITOR')")
    public ResponseEntity<FiRegistryDto> submitPhaseTwo(@PathVariable Long id, @Valid @RequestBody SubmissionRequest submissionRequest) {
        return ResponseEntity.ok(fiRegistrationService.submitPhaseTwo(id, submissionRequest));
    }

    @PostMapping("/{id}/decision")
    @PreAuthorize("hasAuthority('CONTROLLER')")
    public ResponseEntity<FiRegistryDto> decide(@PathVariable Long id,
                                                @RequestParam boolean approve,
                                                @Valid @RequestBody DecisionRequest decisionRequest) {
        return ResponseEntity.ok(fiRegistrationService.controllerDecision(id, approve, decisionRequest.getPerformedBy(), decisionRequest.getComment()));
    }

    @PostMapping("/{id}/decline")
    @PreAuthorize("hasAuthority('CONTROLLER')")
    public ResponseEntity<FiRegistryDto> decline(@PathVariable Long id, @Valid @RequestBody DecisionRequest decisionRequest) {
        return ResponseEntity.ok(fiRegistrationService.decline(id, decisionRequest.getPerformedBy(), decisionRequest.getComment()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EDITOR') or hasAuthority('CONTROLLER')")
    public ResponseEntity<FiRegistryDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(fiRegistrationService.get(id));
    }
}
