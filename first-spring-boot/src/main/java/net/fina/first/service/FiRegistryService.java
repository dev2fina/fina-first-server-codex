package net.fina.first.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fina.first.dto.common.PageResponse;
import net.fina.first.dto.request.FiRegistryCreateRequest;
import net.fina.first.dto.request.FiRegistryUpdateRequest;
import net.fina.first.dto.response.FiRegistryResponse;
import net.fina.first.exception.BusinessException;
import net.fina.first.exception.DuplicateResourceException;
import net.fina.first.exception.ResourceNotFoundException;
import net.fina.first.mapper.FiRegistryMapper;
import net.fina.first.model.FiRegistry;
import net.fina.first.model.FiType;
import net.fina.first.model.LegalForm;
import net.fina.first.model.Region;
import net.fina.first.model.enums.FiTypeCode;
import net.fina.first.model.enums.RegistrationStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import net.fina.first.repository.FiRegistryRepository;
import net.fina.first.repository.FiTypeRepository;
import net.fina.first.repository.LegalFormRepository;
import net.fina.first.repository.RegionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for managing Financial Institution Registry operations.
 * Contains all business logic for FI registration and management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FiRegistryService {

    private final FiRegistryRepository fiRegistryRepository;
    private final FiTypeRepository fiTypeRepository;
    private final LegalFormRepository legalFormRepository;
    private final RegionRepository regionRepository;
    private final FiRegistryMapper fiRegistryMapper;

    /**
     * Retrieves all FI registries with pagination.
     */
    public PageResponse<FiRegistryResponse> findAll(Pageable pageable) {
        log.debug("Finding all FI registries with pagination: {}", pageable);
        Page<FiRegistry> page = fiRegistryRepository.findAllActive(pageable);
        return PageResponse.of(page, fiRegistryMapper.toResponseList(page.getContent()));
    }

    /**
     * Retrieves all FI registries with filtering and pagination.
     */
    public PageResponse<FiRegistryResponse> findAll(String search, FiTypeCode fiTypeCode,
                                                     RegistrationStatus status, Pageable pageable) {
        log.debug("Finding FI registries with filters - search: {}, fiTypeCode: {}, status: {}",
                search, fiTypeCode, status);

        Page<FiRegistry> page;

        if (search != null && !search.isBlank()) {
            page = fiRegistryRepository.searchByTerm(search, pageable);
        } else if (fiTypeCode != null && status != null) {
            page = fiRegistryRepository.findByFiTypeCodeAndStatus(fiTypeCode, status, pageable);
        } else if (fiTypeCode != null) {
            page = fiRegistryRepository.findByFiTypeCode(fiTypeCode, pageable);
        } else if (status != null) {
            page = fiRegistryRepository.findByStatus(status, pageable);
        } else {
            page = fiRegistryRepository.findAllActive(pageable);
        }

        return PageResponse.of(page, fiRegistryMapper.toResponseList(page.getContent()));
    }

    /**
     * Retrieves an FI registry by ID.
     */
    public FiRegistryResponse findById(Long id) {
        log.debug("Finding FI registry by ID: {}", id);
        FiRegistry fiRegistry = findEntityById(id);
        return fiRegistryMapper.toResponse(fiRegistry);
    }

    /**
     * Retrieves an FI registry by code.
     */
    public FiRegistryResponse findByCode(String code) {
        log.debug("Finding FI registry by code: {}", code);
        FiRegistry fiRegistry = fiRegistryRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("FiRegistry", "code", code));
        return fiRegistryMapper.toResponse(fiRegistry);
    }

    /**
     * Creates a new FI registry (Phase 1 - Initial Registration).
     */
    @Transactional
    public FiRegistryResponse create(FiRegistryCreateRequest request) {
        log.info("Creating new FI registry with application number: {}", request.getApplicationNumber());

        // Validate FI type
        FiType fiType = fiTypeRepository.findByCode(request.getFiTypeCode())
                .orElseThrow(() -> new ResourceNotFoundException("FiType", "code", request.getFiTypeCode()));

        // Validate legal form
        LegalForm legalForm = legalFormRepository.findById(request.getLegalFormId())
                .orElseThrow(() -> new ResourceNotFoundException("LegalForm", "id", request.getLegalFormId()));

        // Validate region
        Region region = regionRepository.findById(request.getLegalAddressRegionId())
                .orElseThrow(() -> new ResourceNotFoundException("Region", "id", request.getLegalAddressRegionId()));

        // Generate unique code and identification number
        String code = generateFiCode(fiType);
        String identificationNumber = generateIdentificationNumber();

        // Check uniqueness
        if (fiRegistryRepository.existsByCode(code)) {
            throw new DuplicateResourceException("FiRegistry", "code", code);
        }

        // Create entity
        FiRegistry fiRegistry = fiRegistryMapper.toEntity(request);
        fiRegistry.setCode(code);
        fiRegistry.setIdentificationNumber(identificationNumber);
        fiRegistry.setFiType(fiType);
        fiRegistry.setLegalForm(legalForm);
        fiRegistry.setLegalAddressRegion(region);
        fiRegistry.setStatus(RegistrationStatus.DRAFT);

        FiRegistry saved = fiRegistryRepository.save(fiRegistry);
        log.info("Created FI registry with ID: {} and code: {}", saved.getId(), saved.getCode());

        return fiRegistryMapper.toResponse(saved);
    }

    /**
     * Updates an existing FI registry (Phase 2 - Additional Details).
     */
    @Transactional
    public FiRegistryResponse update(Long id, FiRegistryUpdateRequest request) {
        log.info("Updating FI registry with ID: {}", id);

        FiRegistry fiRegistry = findEntityById(id);

        // Validate status allows updates
        validateCanUpdate(fiRegistry);

        // Update legal form if provided
        if (request.getLegalFormId() != null) {
            LegalForm legalForm = legalFormRepository.findById(request.getLegalFormId())
                    .orElseThrow(() -> new ResourceNotFoundException("LegalForm", "id", request.getLegalFormId()));
            fiRegistry.setLegalForm(legalForm);
        }

        // Update region if provided
        if (request.getLegalAddressRegionId() != null) {
            Region region = regionRepository.findById(request.getLegalAddressRegionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Region", "id", request.getLegalAddressRegionId()));
            fiRegistry.setLegalAddressRegion(region);
        }

        // Apply other updates
        fiRegistryMapper.updateEntity(request, fiRegistry);

        FiRegistry saved = fiRegistryRepository.save(fiRegistry);
        log.info("Updated FI registry with ID: {}", saved.getId());

        return fiRegistryMapper.toResponse(saved);
    }

    /**
     * Submits an FI registry for review.
     */
    @Transactional
    public FiRegistryResponse submit(Long id) {
        log.info("Submitting FI registry with ID: {} for review", id);

        FiRegistry fiRegistry = findEntityById(id);

        if (fiRegistry.getStatus() != RegistrationStatus.DRAFT) {
            throw new BusinessException("FI Registry can only be submitted from DRAFT status");
        }

        // Validate required fields for submission
        validateForSubmission(fiRegistry);

        fiRegistry.setStatus(RegistrationStatus.PENDING_REVIEW);
        FiRegistry saved = fiRegistryRepository.save(fiRegistry);

        log.info("FI registry {} submitted for review", saved.getCode());
        return fiRegistryMapper.toResponse(saved);
    }

    /**
     * Approves an FI registry.
     */
    @Transactional
    public FiRegistryResponse approve(Long id) {
        return approve(id, null);
    }

    /**
     * Approves an FI registry with optional comment.
     */
    @Transactional
    public FiRegistryResponse approve(Long id, String comment) {
        log.info("Approving FI registry with ID: {}, comment: {}", id, comment);

        FiRegistry fiRegistry = findEntityById(id);

        if (fiRegistry.getStatus() != RegistrationStatus.PENDING_REVIEW &&
            fiRegistry.getStatus() != RegistrationStatus.UNDER_REVIEW) {
            throw new BusinessException("FI Registry can only be approved from PENDING_REVIEW or UNDER_REVIEW status");
        }

        fiRegistry.setStatus(RegistrationStatus.APPROVED);
        FiRegistry saved = fiRegistryRepository.save(fiRegistry);

        log.info("FI registry {} approved", saved.getCode());
        return fiRegistryMapper.toResponse(saved);
    }

    /**
     * Rejects an FI registry.
     */
    @Transactional
    public FiRegistryResponse reject(Long id, String reason) {
        log.info("Rejecting FI registry with ID: {}", id);

        FiRegistry fiRegistry = findEntityById(id);

        if (fiRegistry.getStatus() != RegistrationStatus.PENDING_REVIEW &&
            fiRegistry.getStatus() != RegistrationStatus.UNDER_REVIEW) {
            throw new BusinessException("FI Registry can only be rejected from PENDING_REVIEW or UNDER_REVIEW status");
        }

        fiRegistry.setStatus(RegistrationStatus.REJECTED);
        fiRegistry.setCancellationReason(reason);
        FiRegistry saved = fiRegistryRepository.save(fiRegistry);

        log.info("FI registry {} rejected", saved.getCode());
        return fiRegistryMapper.toResponse(saved);
    }

    /**
     * Soft deletes an FI registry using current user.
     */
    @Transactional
    public void delete(Long id) {
        String deletedBy = getCurrentUsername();
        delete(id, deletedBy);
    }

    /**
     * Soft deletes an FI registry.
     */
    @Transactional
    public void delete(Long id, String deletedBy) {
        log.info("Deleting FI registry with ID: {}", id);

        FiRegistry fiRegistry = findEntityById(id);

        if (fiRegistry.getStatus() == RegistrationStatus.ACTIVE) {
            throw new BusinessException("Cannot delete an active FI Registry");
        }

        fiRegistry.markAsDeleted(deletedBy);
        fiRegistryRepository.save(fiRegistry);

        log.info("FI registry {} soft deleted", fiRegistry.getCode());
    }

    /**
     * Searches FI registries by search term.
     */
    public PageResponse<FiRegistryResponse> search(String searchTerm, Pageable pageable) {
        log.debug("Searching FI registries with term: {}", searchTerm);
        Page<FiRegistry> page = fiRegistryRepository.searchByTerm(searchTerm, pageable);
        return PageResponse.of(page, fiRegistryMapper.toResponseList(page.getContent()));
    }

    /**
     * Retrieves FI registry with full details.
     */
    public FiRegistryResponse findByIdWithDetails(Long id) {
        log.debug("Finding FI registry with details by ID: {}", id);
        FiRegistry fiRegistry = fiRegistryRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("FiRegistry", "id", id));
        return fiRegistryMapper.toResponse(fiRegistry);
    }

    // === Private Helper Methods ===

    private FiRegistry findEntityById(Long id) {
        return fiRegistryRepository.findById(id)
                .filter(fi -> !fi.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("FiRegistry", "id", id));
    }

    private String generateFiCode(FiType fiType) {
        long count = fiRegistryRepository.countByFiTypeCode(fiType.getCode()) + 1;
        return String.format("%s-%06d", fiType.getCode().getCode(), count);
    }

    private String generateIdentificationNumber() {
        return "FI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void validateCanUpdate(FiRegistry fiRegistry) {
        if (fiRegistry.getStatus() == RegistrationStatus.APPROVED ||
            fiRegistry.getStatus() == RegistrationStatus.ACTIVE) {
            throw new BusinessException("Cannot update FI Registry in current status: " + fiRegistry.getStatus());
        }
    }

    private void validateForSubmission(FiRegistry fiRegistry) {
        if (fiRegistry.getFirmName() == null || fiRegistry.getFirmName().isBlank()) {
            throw new BusinessException("Firm name is required for submission");
        }
        if (fiRegistry.getLegalForm() == null) {
            throw new BusinessException("Legal form is required for submission");
        }
        if (fiRegistry.getLegalAddress() == null || fiRegistry.getLegalAddress().isBlank()) {
            throw new BusinessException("Legal address is required for submission");
        }
        if (fiRegistry.getEmail() == null || fiRegistry.getEmail().isBlank()) {
            throw new BusinessException("Email is required for submission");
        }
    }

    private String getCurrentUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "system";
    }
}
