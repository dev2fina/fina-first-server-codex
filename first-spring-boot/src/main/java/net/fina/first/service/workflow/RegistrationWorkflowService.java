package net.fina.first.service.workflow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fina.first.dto.workflow.*;
import net.fina.first.dto.workflow.WorkflowStatusResponse.ValidationError;
import net.fina.first.exception.BusinessException;
import net.fina.first.exception.ResourceNotFoundException;
import net.fina.first.model.*;
import net.fina.first.model.enums.*;
import net.fina.first.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service orchestrating the FIRST + VASP + PSP Registration Workflow.
 * Implements the complete workflow as per the registration diagram.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RegistrationWorkflowService {

    private final FiRegistryRepository fiRegistryRepository;
    private final FiTypeRepository fiTypeRepository;
    private final LegalFormRepository legalFormRepository;
    private final RegionRepository regionRepository;
    private final QuestionnaireRepository questionnaireRepository;
    private final WorkflowValidationService validationService;

    // ==========================================
    // PHASE 1: Initial Fields
    // ==========================================

    /**
     * Start a new registration with Phase 1 data.
     */
    public FiRegistry startRegistration(Phase1Request request) {
        log.info("Starting new registration for firm: {}", request.getFirmName());

        // Validate FI Type
        FiType fiType = fiTypeRepository.findByCode(request.getFiTypeCode())
                .orElseThrow(() -> new ResourceNotFoundException("FiType", "code", request.getFiTypeCode()));

        // Validate Legal Form
        LegalForm legalForm = legalFormRepository.findById(request.getLegalFormId())
                .orElseThrow(() -> new ResourceNotFoundException("LegalForm", "id", request.getLegalFormId()));

        // Generate unique code
        String code = generateFiCode(fiType);

        // Create registry
        FiRegistry registry = FiRegistry.builder()
                .code(code)
                .fiType(fiType)
                .firmName(request.getFirmName())
                .tradeName(request.getTradeName())
                .legalForm(legalForm)
                .identificationNumber(request.getIdentificationNumber())
                .applicationNumber(request.getApplicationNumber())
                .applicationReceivedDate(request.getApplicationReceivedDate())
                .status(RegistrationStatus.DRAFT)
                .currentPhase(WorkflowPhase.PHASE1_INITIAL)
                .build();

        // Create registration action
        FiRegistryAction action = FiRegistryAction.builder()
                .type(ActionType.REGISTRATION)
                .status(ActionStatus.INITIATED)
                .step(1)
                .author(getCurrentUsername())
                .build();
        registry.addAction(action);

        // Initialize questionnaires for this FI type
        initializeQuestionnaires(action, fiType);

        FiRegistry saved = fiRegistryRepository.save(registry);
        log.info("Created registration with ID: {} and code: {}", saved.getId(), saved.getCode());

        return saved;
    }

    /**
     * Complete Phase 1 and move to Phase 2.
     */
    public FiRegistry completePhase1(Long registryId) {
        log.info("Completing Phase 1 for registry ID: {}", registryId);

        FiRegistry registry = findById(registryId);
        validateCurrentPhase(registry, WorkflowPhase.PHASE1_INITIAL);

        // Validate Phase 1 requirements
        List<ValidationError> errors = validationService.validatePhase1(registry);
        if (!errors.isEmpty()) {
            throw new BusinessException("Phase 1 validation failed: " + formatErrors(errors));
        }

        registry.setPhase1CompletedAt(LocalDateTime.now());
        registry.setCurrentPhase(WorkflowPhase.PHASE2_GENERAL_INFO);

        return fiRegistryRepository.save(registry);
    }

    // ==========================================
    // PHASE 2: General Info
    // ==========================================

    /**
     * Update Phase 2 data (General Info).
     */
    public FiRegistry updatePhase2(Long registryId, Phase2Request request) {
        log.info("Updating Phase 2 data for registry ID: {}", registryId);

        FiRegistry registry = findById(registryId);

        if (registry.getCurrentPhase() != WorkflowPhase.PHASE2_GENERAL_INFO &&
            registry.getCurrentPhase() != WorkflowPhase.PHASE7_CORRECTION) {
            throw new BusinessException("Cannot update Phase 2 data in current phase: " + registry.getCurrentPhase());
        }

        // Validate and get region
        Region region = regionRepository.findById(request.getLegalAddressRegionId())
                .orElseThrow(() -> new ResourceNotFoundException("Region", "id", request.getLegalAddressRegionId()));

        // Update fields
        registry.setLegalAddressRegion(region);
        registry.setLegalAddressCity(request.getLegalAddressCity());
        registry.setLegalAddress(request.getLegalAddress());
        registry.setActualAddressSameAsLegal(request.isActualAddressSameAsLegal());
        registry.setActualOrHeadOfficeAddress(request.getActualOrHeadOfficeAddress());
        registry.setPhone(request.getPhone());
        registry.setEmail(request.getEmail());
        registry.setWebsite(request.getWebsite());
        registry.setContactPersonFullName(request.getContactPersonFullName());
        registry.setContactPersonIdentificationNumber(request.getContactPersonIdentificationNumber());
        registry.setContactPersonPhone(request.getContactPersonPhone());
        registry.setContactPersonEmail(request.getContactPersonEmail());
        registry.setCapital(request.getCapital());

        // Type-specific fields
        registry.setForeignBankBranchOrSubsidiary(request.getForeignBankBranchOrSubsidiary());
        registry.setForeignReliableBankBranchOrSubsidiary(request.getForeignReliableBankBranchOrSubsidiary());
        registry.setSignificantServiceProvider(request.getSignificantServiceProvider());
        registry.setReferenceExchangeRate(request.getReferenceExchangeRate());
        registry.setCommissionFee(request.getCommissionFee());
        registry.setTypeOfActivity(request.getTypeOfActivity());
        registry.setServicingCommercialBanks(request.getServicingCommercialBanks());

        return fiRegistryRepository.save(registry);
    }

    /**
     * Complete Phase 2 and move to Phase 3 (Data Entry).
     */
    public FiRegistry completePhase2(Long registryId) {
        log.info("Completing Phase 2 for registry ID: {}", registryId);

        FiRegistry registry = findById(registryId);
        validateCurrentPhase(registry, WorkflowPhase.PHASE2_GENERAL_INFO);

        // Validate Phase 2 requirements
        List<ValidationError> errors = validationService.validatePhase2(registry);
        if (!errors.isEmpty()) {
            throw new BusinessException("Phase 2 validation failed: " + formatErrors(errors));
        }

        registry.setPhase2CompletedAt(LocalDateTime.now());
        registry.setCurrentPhase(WorkflowPhase.PHASE3_DATA_ENTRY);

        return fiRegistryRepository.save(registry);
    }

    // ==========================================
    // PHASE 3: Data Entry (Managers, Beneficiaries, Branches, License)
    // ==========================================

    /**
     * Complete Data Entry phase and move to Questionnaire phase.
     */
    public FiRegistry completeDataEntry(Long registryId) {
        log.info("Completing Data Entry for registry ID: {}", registryId);

        FiRegistry registry = findById(registryId);
        validateCurrentPhase(registry, WorkflowPhase.PHASE3_DATA_ENTRY);

        // Validate Data Entry requirements
        List<ValidationError> errors = validationService.validateDataEntry(registry);
        if (!errors.isEmpty()) {
            throw new BusinessException("Data Entry validation failed: " + formatErrors(errors));
        }

        registry.setDataEntryCompletedAt(LocalDateTime.now());
        registry.setCurrentPhase(WorkflowPhase.PHASE4_QUESTIONNAIRE);

        return fiRegistryRepository.save(registry);
    }

    // ==========================================
    // PHASE 4: Questionnaire
    // ==========================================

    /**
     * Submit questionnaire answers.
     */
    public FiRegistry submitQuestionnaire(Long registryId, QuestionnaireAnswerRequest request) {
        log.info("Submitting questionnaire answers for registry ID: {}", registryId);

        FiRegistry registry = findById(registryId);

        if (registry.getCurrentPhase() != WorkflowPhase.PHASE4_QUESTIONNAIRE &&
            registry.getCurrentPhase() != WorkflowPhase.PHASE7_CORRECTION) {
            throw new BusinessException("Cannot submit questionnaire in current phase: " + registry.getCurrentPhase());
        }

        // Get current action
        FiRegistryAction action = getCurrentAction(registry);

        // Update questionnaire answers
        for (var answerRequest : request.getAnswers()) {
            action.getQuestionnaires().stream()
                    .filter(q -> q.getId().equals(answerRequest.getQuestionnaireId()))
                    .findFirst()
                    .ifPresent(q -> {
                        q.setAnswer(answerRequest.getAnswer());
                        q.setNote(answerRequest.getNote());
                        q.setStatus(QuestionnaireStatus.COMPLETED);
                    });
        }

        return fiRegistryRepository.save(registry);
    }

    /**
     * Complete Questionnaire phase and move to Submitted phase.
     */
    public FiRegistry completeQuestionnaire(Long registryId) {
        log.info("Completing Questionnaire for registry ID: {}", registryId);

        FiRegistry registry = findById(registryId);
        validateCurrentPhase(registry, WorkflowPhase.PHASE4_QUESTIONNAIRE);

        // Validate Questionnaire requirements
        List<ValidationError> errors = validationService.validateQuestionnaire(registry);
        if (!errors.isEmpty()) {
            throw new BusinessException("Questionnaire validation failed: " + formatErrors(errors));
        }

        registry.setQuestionnaireCompletedAt(LocalDateTime.now());
        registry.setCurrentPhase(WorkflowPhase.PHASE5_SUBMITTED);

        return fiRegistryRepository.save(registry);
    }

    // ==========================================
    // PHASE 5: Submit to Controller
    // ==========================================

    /**
     * Submit registration to Controller for review.
     */
    public FiRegistry submitToController(Long registryId) {
        log.info("Submitting registration to Controller for registry ID: {}", registryId);

        FiRegistry registry = findById(registryId);
        validateCurrentPhase(registry, WorkflowPhase.PHASE5_SUBMITTED);

        // Final validation before submission
        List<ValidationError> errors = validationService.validateForSubmission(registry);
        if (!errors.isEmpty()) {
            throw new BusinessException("Submission validation failed: " + formatErrors(errors));
        }

        String username = getCurrentUsername();

        registry.setSubmittedToControllerAt(LocalDateTime.now());
        registry.setSubmittedToControllerBy(username);
        registry.setCurrentPhase(WorkflowPhase.PHASE6_CONTROLLER_REVIEW);
        registry.setStatus(RegistrationStatus.PENDING_REVIEW);

        // Update action status
        FiRegistryAction action = getCurrentAction(registry);
        action.setStatus(ActionStatus.PENDING_APPROVAL);

        return fiRegistryRepository.save(registry);
    }

    // ==========================================
    // PHASE 6: Controller Review
    // ==========================================

    /**
     * Controller makes a decision on the registration.
     */
    public FiRegistry controllerDecision(Long registryId, ControllerDecisionRequest request) {
        log.info("Controller decision for registry ID: {}, decision: {}", registryId, request.getDecision());

        FiRegistry registry = findById(registryId);
        validateCurrentPhase(registry, WorkflowPhase.PHASE6_CONTROLLER_REVIEW);

        String username = getCurrentUsername();

        registry.setControllerReviewedAt(LocalDateTime.now());
        registry.setControllerReviewedBy(username);
        registry.setControllerDecision(request.getDecision());
        registry.setControllerDecisionComment(request.getComment());

        switch (request.getDecision()) {
            case ACCEPT -> handleAccept(registry, username);
            case DECLINE -> handleDecline(registry, request);
            case REQUEST_INFO -> handleRequestInfo(registry, request);
        }

        return fiRegistryRepository.save(registry);
    }

    private void handleAccept(FiRegistry registry, String username) {
        log.info("Registration {} accepted by {}", registry.getCode(), username);

        registry.setCurrentPhase(WorkflowPhase.PHASE8_DOCUMENT_GENERATION);
        registry.setStatus(RegistrationStatus.APPROVED);

        FiRegistryAction action = getCurrentAction(registry);
        action.setStatus(ActionStatus.APPROVED);
        action.setControlStatus("ACCEPTED");

        // Trigger document generation
        generateRegistrationDocuments(registry);
    }

    private void handleDecline(FiRegistry registry, ControllerDecisionRequest request) {
        log.info("Registration {} declined", registry.getCode());

        registry.setCurrentPhase(WorkflowPhase.PHASE7_CORRECTION);
        registry.setStatus(RegistrationStatus.PENDING_DOCUMENTS);
        registry.setCorrectionRequestedAt(LocalDateTime.now());
        registry.setCorrectionReason(request.getDeclineReason());
        registry.setCorrectionDeadline(request.getCorrectionDeadline());
        registry.setCorrectionCount(registry.getCorrectionCount() + 1);

        FiRegistryAction action = getCurrentAction(registry);
        action.setStatus(ActionStatus.IN_PROGRESS);
        action.setControlStatus("DECLINED");
        action.setRejectionReason(request.getDeclineReason());
    }

    private void handleRequestInfo(FiRegistry registry, ControllerDecisionRequest request) {
        log.info("Additional info requested for registration {}", registry.getCode());

        registry.setCurrentPhase(WorkflowPhase.PHASE7_CORRECTION);
        registry.setStatus(RegistrationStatus.PENDING_DOCUMENTS);
        registry.setCorrectionRequestedAt(LocalDateTime.now());
        registry.setCorrectionReason(request.getDeclineReason());
        registry.setCorrectionDeadline(request.getCorrectionDeadline());

        FiRegistryAction action = getCurrentAction(registry);
        action.setControlStatus("INFO_REQUESTED");
    }

    // ==========================================
    // PHASE 7: Correction
    // ==========================================

    /**
     * Resubmit after corrections.
     */
    public FiRegistry resubmitAfterCorrection(Long registryId) {
        log.info("Resubmitting after correction for registry ID: {}", registryId);

        FiRegistry registry = findById(registryId);
        validateCurrentPhase(registry, WorkflowPhase.PHASE7_CORRECTION);

        // Validate all requirements again
        List<ValidationError> errors = validationService.validateForSubmission(registry);
        if (!errors.isEmpty()) {
            throw new BusinessException("Resubmission validation failed: " + formatErrors(errors));
        }

        String username = getCurrentUsername();

        registry.setSubmittedToControllerAt(LocalDateTime.now());
        registry.setSubmittedToControllerBy(username);
        registry.setCurrentPhase(WorkflowPhase.PHASE6_CONTROLLER_REVIEW);
        registry.setStatus(RegistrationStatus.UNDER_REVIEW);
        registry.setControllerDecision(null);
        registry.setControllerDecisionComment(null);

        return fiRegistryRepository.save(registry);
    }

    // ==========================================
    // PHASE 8 & 9: Document Generation & Final Registration
    // ==========================================

    /**
     * Finalize registration after document generation.
     */
    public FiRegistry finalizeRegistration(Long registryId) {
        log.info("Finalizing registration for registry ID: {}", registryId);

        FiRegistry registry = findById(registryId);
        validateCurrentPhase(registry, WorkflowPhase.PHASE8_DOCUMENT_GENERATION);

        String username = getCurrentUsername();

        registry.setCurrentPhase(WorkflowPhase.PHASE9_REGISTERED);
        registry.setStatus(RegistrationStatus.ACTIVE);
        registry.setRegisteredAt(LocalDateTime.now());
        registry.setRegisteredBy(username);
        registry.setRegistrationDate(LocalDate.now());
        registry.setRegistrationNumber(generateRegistrationNumber());

        FiRegistryAction action = getCurrentAction(registry);
        action.setStatus(ActionStatus.COMPLETED);
        action.setFinalProgressStatus("REGISTERED");

        log.info("Registration {} completed with registration number: {}",
                registry.getCode(), registry.getRegistrationNumber());

        return fiRegistryRepository.save(registry);
    }

    // ==========================================
    // Workflow Status
    // ==========================================

    /**
     * Get detailed workflow status for a registration.
     */
    @Transactional(readOnly = true)
    public WorkflowStatusResponse getWorkflowStatus(Long registryId) {
        FiRegistry registry = findById(registryId);
        return buildWorkflowStatusResponse(registry);
    }

    private WorkflowStatusResponse buildWorkflowStatusResponse(FiRegistry registry) {
        List<ValidationError> errors = new ArrayList<>();
        List<String> availableActions = new ArrayList<>();

        // Determine available actions based on current phase
        switch (registry.getCurrentPhase()) {
            case PHASE1_INITIAL -> {
                errors = validationService.validatePhase1(registry);
                availableActions.add("COMPLETE_PHASE1");
            }
            case PHASE2_GENERAL_INFO -> {
                errors = validationService.validatePhase2(registry);
                availableActions.add("UPDATE_PHASE2");
                availableActions.add("COMPLETE_PHASE2");
            }
            case PHASE3_DATA_ENTRY -> {
                errors = validationService.validateDataEntry(registry);
                availableActions.add("ADD_ADMINISTRATOR");
                availableActions.add("ADD_BENEFICIARY");
                availableActions.add("ADD_BRANCH");
                if (registry.requiresLicense()) {
                    availableActions.add("ADD_LICENSE");
                }
                availableActions.add("COMPLETE_DATA_ENTRY");
            }
            case PHASE4_QUESTIONNAIRE -> {
                errors = validationService.validateQuestionnaire(registry);
                availableActions.add("SUBMIT_QUESTIONNAIRE");
                availableActions.add("COMPLETE_QUESTIONNAIRE");
            }
            case PHASE5_SUBMITTED -> {
                availableActions.add("SUBMIT_TO_CONTROLLER");
            }
            case PHASE6_CONTROLLER_REVIEW -> {
                availableActions.add("CONTROLLER_ACCEPT");
                availableActions.add("CONTROLLER_DECLINE");
            }
            case PHASE7_CORRECTION -> {
                errors = validationService.validateForSubmission(registry);
                availableActions.add("UPDATE_DATA");
                availableActions.add("RESUBMIT");
            }
            case PHASE8_DOCUMENT_GENERATION -> {
                availableActions.add("FINALIZE_REGISTRATION");
            }
            case PHASE9_REGISTERED -> {
                // No actions available - registration complete
            }
        }

        return WorkflowStatusResponse.builder()
                .registrationId(registry.getId())
                .code(registry.getCode())
                .firmName(registry.getFirmName())
                .fiTypeCode(registry.getFiType() != null ? registry.getFiType().getCode().name() : null)
                .fiTypeName(registry.getFiType() != null ? registry.getFiType().getName() : null)
                .currentPhase(registry.getCurrentPhase())
                .currentPhaseDisplayName(registry.getCurrentPhase().getDisplayName())
                .registrationStatus(registry.getStatus())
                .isEditableByFi(registry.isEditableByFi())
                .requiresControllerAction(registry.requiresControllerAction())
                .isRegistered(registry.isRegistered())
                .phase1Completed(registry.getPhase1CompletedAt() != null)
                .phase2Completed(registry.getPhase2CompletedAt() != null)
                .dataEntryCompleted(registry.getDataEntryCompletedAt() != null)
                .questionnaireCompleted(registry.getQuestionnaireCompletedAt() != null)
                .phase1CompletedAt(registry.getPhase1CompletedAt())
                .phase2CompletedAt(registry.getPhase2CompletedAt())
                .dataEntryCompletedAt(registry.getDataEntryCompletedAt())
                .questionnaireCompletedAt(registry.getQuestionnaireCompletedAt())
                .administratorCount(registry.getActiveAdministratorCount())
                .beneficiaryCount(registry.getActiveBeneficiaryCount())
                .branchCount(registry.getBranches() != null ?
                        registry.getBranches().stream().filter(b -> !b.isDeleted()).count() : 0)
                .licenseCount(registry.getActiveLicenseCount())
                .hasHeadOfficeBranch(registry.hasHeadOfficeBranch())
                .requiresLicense(registry.requiresLicense())
                .submittedToControllerAt(registry.getSubmittedToControllerAt())
                .submittedToControllerBy(registry.getSubmittedToControllerBy())
                .assignedController(registry.getAssignedController())
                .controllerReviewedAt(registry.getControllerReviewedAt())
                .controllerReviewedBy(registry.getControllerReviewedBy())
                .controllerDecision(registry.getControllerDecision())
                .controllerDecisionComment(registry.getControllerDecisionComment())
                .correctionRequestedAt(registry.getCorrectionRequestedAt())
                .correctionReason(registry.getCorrectionReason())
                .correctionDeadline(registry.getCorrectionDeadline())
                .correctionCount(registry.getCorrectionCount())
                .registeredAt(registry.getRegisteredAt())
                .registeredBy(registry.getRegisteredBy())
                .registrationNumber(registry.getRegistrationNumber())
                .validationErrors(errors)
                .canProceedToNextPhase(errors.isEmpty())
                .availableActions(availableActions)
                .build();
    }

    // ==========================================
    // Helper Methods
    // ==========================================

    private FiRegistry findById(Long id) {
        return fiRegistryRepository.findById(id)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("FiRegistry", "id", id));
    }

    private void validateCurrentPhase(FiRegistry registry, WorkflowPhase expectedPhase) {
        if (registry.getCurrentPhase() != expectedPhase) {
            throw new BusinessException(
                    String.format("Invalid phase. Expected: %s, Current: %s",
                            expectedPhase, registry.getCurrentPhase()));
        }
    }

    private FiRegistryAction getCurrentAction(FiRegistry registry) {
        return registry.getActions().stream()
                .filter(a -> !a.isFinalState())
                .findFirst()
                .orElseThrow(() -> new BusinessException("No active action found"));
    }

    private void initializeQuestionnaires(FiRegistryAction action, FiType fiType) {
        List<Questionnaire> templates = questionnaireRepository.findByFiTypeAndActiveTrue(fiType);

        for (Questionnaire template : templates) {
            ActionQuestionnaire aq = ActionQuestionnaire.builder()
                    .questionnaire(template)
                    .question(template.getQuestion())
                    .code(template.getCode())
                    .groupCode(template.getGroupCode())
                    .groupName(template.getGroupName())
                    .obligatory(template.isObligatory())
                    .predefined(true)
                    .defaultValue(template.getDefaultValue())
                    .sequence(template.getSequence())
                    .status(QuestionnaireStatus.PENDING)
                    .build();
            action.addQuestionnaire(aq);
        }
    }

    private void generateRegistrationDocuments(FiRegistry registry) {
        log.info("Generating registration documents for: {}", registry.getCode());
        // Document generation logic will be implemented in DocumentGenerationService
        // This is a placeholder for the integration
    }

    private String generateFiCode(FiType fiType) {
        long count = fiRegistryRepository.countByFiTypeCode(fiType.getCode()) + 1;
        return String.format("%s-%06d", fiType.getCode().getCode(), count);
    }

    private String generateRegistrationNumber() {
        return "REG-" + LocalDate.now().getYear() + "-" +
               UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String getCurrentUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }

    private String formatErrors(List<ValidationError> errors) {
        return errors.stream()
                .map(e -> e.getField() + ": " + e.getMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Unknown error");
    }
}
