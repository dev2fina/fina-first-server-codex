package net.fina.first.service.registry;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.fina.first.dto.registry.FiRegistryDto;
import net.fina.first.dto.registry.FiRegistryRequest;
import net.fina.first.dto.registry.LicenseDto;
import net.fina.first.dto.registry.QuestionnaireResponseDto;
import net.fina.first.dto.registry.SubmissionRequest;
import net.fina.first.exception.NotFoundException;
import net.fina.first.exception.registry.InvalidTransitionException;
import net.fina.first.exception.registry.MissingDataException;
import net.fina.first.mapper.registry.FiRegistryMapper;
import net.fina.first.model.registry.FiType;
import net.fina.first.model.registry.FinancialInstitution;
import net.fina.first.model.registry.License;
import net.fina.first.model.registry.LicenseType;
import net.fina.first.model.registry.QuestionnaireResponse;
import net.fina.first.model.registry.RegistrationAction;
import net.fina.first.model.registry.RegistrationStatus;
import net.fina.first.repository.registry.BeneficiaryRepository;
import net.fina.first.repository.registry.BranchRepository;
import net.fina.first.repository.registry.FinancialInstitutionRepository;
import net.fina.first.repository.registry.LicenseRepository;
import net.fina.first.repository.registry.ManagerRepository;
import net.fina.first.repository.registry.QuestionnaireResponseRepository;
import net.fina.first.repository.registry.RegistrationActionRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class FiRegistrationService {

    private final FinancialInstitutionRepository financialInstitutionRepository;
    private final ManagerRepository managerRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final BranchRepository branchRepository;
    private final LicenseRepository licenseRepository;
    private final QuestionnaireResponseRepository questionnaireResponseRepository;
    private final RegistrationActionRepository registrationActionRepository;
    private final FiRegistryMapper mapper;
    private final QuestionnaireService questionnaireService;
    private final RegistryDocumentService registryDocumentService;

    public FiRegistryDto createDraft(FiRegistryRequest request) {
        FinancialInstitution entity = mapper.toEntity(request);
        FinancialInstitution saved = financialInstitutionRepository.save(entity);
        syncChildren(saved, request);
        registerAction(saved, RegistrationStatus.DRAFT_PHASE1, "Draft created", null);
        return mapper.toDto(loadWithChildren(saved.getId()));
    }

    public FiRegistryDto updateGeneralInfo(Long id, FiRegistryRequest request) {
        FinancialInstitution fi = loadWithChildren(id);
        fi.setRegistryCode(request.getRegistryCode());
        fi.setLegalName(request.getLegalName());
        fi.setBrandName(request.getBrandName());
        fi.setFiType(request.getFiType());
        fi.setEstablishmentDate(request.getEstablishmentDate());
        fi.setHeadquartersAddress(request.getHeadquartersAddress());
        fi.setHeadquartersCity(request.getHeadquartersCity());
        fi.setHeadquartersCountry(request.getHeadquartersCountry());
        fi.setRegistrationCountry(request.getRegistrationCountry());
        syncChildren(fi, request);
        return mapper.toDto(loadWithChildren(id));
    }

    public FiRegistryDto submitPhaseOne(Long id, SubmissionRequest submissionRequest) {
        FinancialInstitution fi = loadWithChildren(id);
        validatePhaseOne(fi);
        ensureTransition(fi.getStatus(), RegistrationStatus.SUBMITTED_PHASE1,
            "Phase 1 can only be submitted from draft or correction.");
        fi.setStatus(RegistrationStatus.SUBMITTED_PHASE1);
        fi.setPhaseOneSubmittedAt(LocalDate.now());
        registerAction(fi, RegistrationStatus.SUBMITTED_PHASE1, "Submitted phase 1", submissionRequest.getSubmittedBy());
        return mapper.toDto(fi);
    }

    public FiRegistryDto controllerReviewPhaseOne(Long id, boolean accepted, String performedBy, String comment) {
        FinancialInstitution fi = loadWithChildren(id);
        if (fi.getStatus() != RegistrationStatus.SUBMITTED_PHASE1) {
            throw new InvalidTransitionException("Phase 1 review allowed only when submitted");
        }
        if (accepted) {
            fi.setStatus(RegistrationStatus.PHASE1_APPROVED);
            registerAction(fi, RegistrationStatus.PHASE1_APPROVED, "Phase 1 approved", performedBy);
        } else {
            fi.setStatus(RegistrationStatus.PHASE1_CORRECTION_REQUIRED);
            fi.setControllerComments(comment);
            registerAction(fi, RegistrationStatus.PHASE1_CORRECTION_REQUIRED, "Phase 1 corrections required", performedBy);
        }
        return mapper.toDto(fi);
    }

    public FiRegistryDto openPhaseTwo(Long id) {
        FinancialInstitution fi = loadWithChildren(id);
        if (fi.getStatus() != RegistrationStatus.PHASE1_APPROVED) {
            throw new InvalidTransitionException("Phase 2 can start only after phase 1 approval");
        }
        fi.setStatus(RegistrationStatus.DRAFT_PHASE2);
        registerAction(fi, RegistrationStatus.DRAFT_PHASE2, "Phase 2 opened", null);
        return mapper.toDto(fi);
    }

    public FiRegistryDto saveLicenses(Long id, List<LicenseDto> licenses) {
        FinancialInstitution fi = loadWithChildren(id);
        licenseRepository.deleteAll(licenseRepository.findByFinancialInstitutionId(id));
        Set<License> mappedLicenses = licenses.stream()
            .map(mapper::toEntity)
            .peek(l -> l.setFinancialInstitution(fi))
            .collect(Collectors.toSet());
        licenseRepository.saveAll(mappedLicenses);
        return mapper.toDto(loadWithChildren(id));
    }

    public FiRegistryDto saveQuestionnaires(Long id, List<QuestionnaireResponseDto> responses) {
        FinancialInstitution fi = loadWithChildren(id);
        questionnaireResponseRepository.deleteAll(questionnaireResponseRepository.findByFinancialInstitutionId(id));
        questionnaireService.validateObligatory(responses);
        Set<QuestionnaireResponse> mappedResponses = responses.stream()
            .map(mapper::toEntity)
            .peek(r -> r.setFinancialInstitution(fi))
            .collect(Collectors.toSet());
        questionnaireResponseRepository.saveAll(mappedResponses);
        questionnaireService.syncGaps(fi, mappedResponses);
        return mapper.toDto(loadWithChildren(id));
    }

    public FiRegistryDto submitPhaseTwo(Long id, SubmissionRequest submissionRequest) {
        FinancialInstitution fi = loadWithChildren(id);
        validatePhaseTwo(fi);
        ensureTransition(fi.getStatus(), RegistrationStatus.SUBMITTED_PHASE2,
            "Phase 2 can only be submitted from draft or correction.");
        fi.setStatus(RegistrationStatus.SUBMITTED_PHASE2);
        fi.setPhaseTwoSubmittedAt(LocalDate.now());
        registerAction(fi, RegistrationStatus.SUBMITTED_PHASE2, "Submitted phase 2", submissionRequest.getSubmittedBy());
        return mapper.toDto(fi);
    }

    public FiRegistryDto controllerDecision(Long id, boolean approved, String performedBy, String comment) {
        FinancialInstitution fi = loadWithChildren(id);
        if (fi.getStatus() != RegistrationStatus.SUBMITTED_PHASE2) {
            throw new InvalidTransitionException("Controller decision only allowed when phase 2 is submitted");
        }
        if (approved) {
            fi.setStatus(RegistrationStatus.APPROVED);
            fi.setRegistrationCompletedAt(LocalDate.now());
            registerAction(fi, RegistrationStatus.APPROVED, "Registration approved", performedBy);
            registryDocumentService.generateApprovalDocuments(fi);
        } else {
            fi.setStatus(RegistrationStatus.CORRECTION_REQUIRED);
            fi.setControllerComments(comment);
            registerAction(fi, RegistrationStatus.CORRECTION_REQUIRED, "Corrections requested", performedBy);
        }
        return mapper.toDto(fi);
    }

    public FiRegistryDto decline(Long id, String performedBy, String comment) {
        FinancialInstitution fi = loadWithChildren(id);
        ensureTransition(fi.getStatus(), RegistrationStatus.DECLINED, "Decline allowed only for submitted actions");
        fi.setStatus(RegistrationStatus.DECLINED);
        fi.setControllerComments(comment);
        registerAction(fi, RegistrationStatus.DECLINED, "Registration declined", performedBy);
        return mapper.toDto(fi);
    }

    @Transactional(readOnly = true)
    public FiRegistryDto get(Long id) {
        return mapper.toDto(loadWithChildren(id));
    }

    private FinancialInstitution loadWithChildren(Long id) {
        FinancialInstitution fi = financialInstitutionRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Financial institution not found"));
        fi.getManagers().size();
        fi.getBeneficiaries().size();
        fi.getBranches().size();
        fi.getLicenses().size();
        fi.getQuestionnaireResponses().size();
        fi.getGapDetails().size();
        return fi;
    }

    private void syncChildren(FinancialInstitution fi, FiRegistryRequest request) {
        managerRepository.deleteAll(managerRepository.findByFinancialInstitutionId(fi.getId()));
        beneficiaryRepository.deleteAll(beneficiaryRepository.findByFinancialInstitutionId(fi.getId()));
        branchRepository.deleteAll(branchRepository.findByFinancialInstitutionId(fi.getId()));
        if (request.getManagers() != null) {
            request.getManagers().stream()
                .map(mapper::toEntity)
                .forEach(m -> {
                    m.setFinancialInstitution(fi);
                    managerRepository.save(m);
                });
        }
        if (request.getBeneficiaries() != null) {
            request.getBeneficiaries().stream()
                .map(mapper::toEntity)
                .forEach(b -> {
                    b.setFinancialInstitution(fi);
                    beneficiaryRepository.save(b);
                });
        }
        if (request.getBranches() != null) {
            request.getBranches().stream()
                .map(mapper::toEntity)
                .forEach(b -> {
                    b.setFinancialInstitution(fi);
                    branchRepository.save(b);
                });
        }
    }

    private void ensureTransition(RegistrationStatus current, RegistrationStatus target, String message) {
        if (current == RegistrationStatus.DECLINED || current == RegistrationStatus.APPROVED) {
            throw new InvalidTransitionException("Completed records cannot transition");
        }
        if (current == target) {
            return;
        }
        switch (target) {
            case SUBMITTED_PHASE1 -> {
                if (!(current == RegistrationStatus.DRAFT_PHASE1 || current == RegistrationStatus.PHASE1_CORRECTION_REQUIRED)) {
                    throw new InvalidTransitionException(message);
                }
            }
            case DRAFT_PHASE2 -> {
                if (current != RegistrationStatus.PHASE1_APPROVED) {
                    throw new InvalidTransitionException(message);
                }
            }
            case SUBMITTED_PHASE2 -> {
                if (!(current == RegistrationStatus.DRAFT_PHASE2 || current == RegistrationStatus.CORRECTION_REQUIRED)) {
                    throw new InvalidTransitionException(message);
                }
            }
            case DECLINED, APPROVED -> {
                if (current != RegistrationStatus.SUBMITTED_PHASE2) {
                    throw new InvalidTransitionException(message);
                }
            }
            default -> {
            }
        }
    }

    private void validatePhaseOne(FinancialInstitution fi) {
        if (hasBlank(fi.getRegistryCode(), fi.getLegalName(), fi.getHeadquartersAddress())) {
            throw new MissingDataException("Registry code, legal name and headquarters address are required in phase 1");
        }
        boolean hasChiefExecutive = fi.getManagers().stream().anyMatch(m -> Objects.equals(m.getRole(), net.fina.first.model.registry.ManagerRole.CHIEF_EXECUTIVE));
        if (!hasChiefExecutive) {
            throw new MissingDataException("At least one chief executive manager is required");
        }
    }

    private void validatePhaseTwo(FinancialInstitution fi) {
        if (fi.getFiType() == FiType.PSP || fi.getFiType() == FiType.VASP) {
            boolean licensePresent = licenseRepository.existsByFinancialInstitutionIdAndLicenseType(fi.getId(), LicenseType.valueOf(fi.getFiType().name()));
            if (!licensePresent) {
                throw new MissingDataException("License information is mandatory for PSP/VASP");
            }
        }
        questionnaireService.validateObligatory(mapper.toQuestionnaireDtos(fi.getQuestionnaireResponses()));
        questionnaireService.ensureGapsResolved(fi);
    }

    private void registerAction(FinancialInstitution fi, RegistrationStatus status, String action, String performedBy) {
        RegistrationAction history = new RegistrationAction();
        history.setFinancialInstitution(fi);
        history.setStatus(status);
        history.setAction(action);
        history.setPerformedAt(LocalDate.now());
        history.setPerformedBy(performedBy);
        registrationActionRepository.save(history);
    }

    private boolean hasBlank(String... values) {
        for (String value : values) {
            if (value == null || value.isBlank()) {
                return true;
            }
        }
        return false;
    }
}
