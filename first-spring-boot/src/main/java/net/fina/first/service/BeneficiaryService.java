package net.fina.first.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fina.first.dto.common.PageResponse;
import net.fina.first.dto.request.BeneficiaryRequest;
import net.fina.first.dto.response.BeneficiaryResponse;
import net.fina.first.exception.BusinessException;
import net.fina.first.exception.ResourceNotFoundException;
import net.fina.first.mapper.BeneficiaryMapper;
import net.fina.first.model.Beneficiary;
import net.fina.first.model.FiRegistry;
import net.fina.first.model.enums.BeneficiaryType;
import net.fina.first.repository.BeneficiaryRepository;
import net.fina.first.repository.FiRegistryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service for managing FI Beneficiary (Complex Structure) operations.
 * Handles hierarchical ownership structures and capital percentage calculations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BeneficiaryService {

    private static final BigDecimal SIGNIFICANT_OWNERSHIP_THRESHOLD = new BigDecimal("10.0000");

    private final BeneficiaryRepository beneficiaryRepository;
    private final FiRegistryRepository fiRegistryRepository;
    private final BeneficiaryMapper beneficiaryMapper;

    /**
     * Retrieves all beneficiaries for an FI registry.
     */
    public List<BeneficiaryResponse> findByFiRegistryId(Long fiRegistryId) {
        log.debug("Finding beneficiaries for FI registry ID: {}", fiRegistryId);
        List<Beneficiary> beneficiaries = beneficiaryRepository.findActiveByFiRegistryId(fiRegistryId);
        return beneficiaryMapper.toResponseList(beneficiaries);
    }

    /**
     * Retrieves root beneficiaries (top-level ownership structure).
     */
    public List<BeneficiaryResponse> findRootBeneficiaries(Long fiRegistryId) {
        log.debug("Finding root beneficiaries for FI registry ID: {}", fiRegistryId);
        List<Beneficiary> beneficiaries = beneficiaryRepository.findRootBeneficiariesByFiRegistryId(fiRegistryId);
        return beneficiaryMapper.toResponseList(beneficiaries);
    }

    /**
     * Retrieves ultimate beneficiaries (natural persons with significant ownership).
     */
    public List<BeneficiaryResponse> findUltimateBeneficiaries(Long fiRegistryId) {
        log.debug("Finding ultimate beneficiaries for FI registry ID: {}", fiRegistryId);
        List<Beneficiary> beneficiaries = beneficiaryRepository.findUltimateBeneficiariesByFiRegistryId(fiRegistryId);
        return beneficiaryMapper.toResponseList(beneficiaries);
    }

    /**
     * Retrieves beneficiaries with significant ownership (>= 10%).
     */
    public List<BeneficiaryResponse> findSignificantOwners(Long fiRegistryId) {
        log.debug("Finding significant owners for FI registry ID: {}", fiRegistryId);
        List<Beneficiary> beneficiaries = beneficiaryRepository.findByFiRegistryIdAndMinCapitalPercentage(
                fiRegistryId, SIGNIFICANT_OWNERSHIP_THRESHOLD);
        return beneficiaryMapper.toResponseList(beneficiaries);
    }

    /**
     * Retrieves beneficiaries with pagination.
     */
    public PageResponse<BeneficiaryResponse> findByFiRegistryId(Long fiRegistryId, Pageable pageable) {
        log.debug("Finding beneficiaries for FI registry ID: {} with pagination", fiRegistryId);
        Page<Beneficiary> page = beneficiaryRepository.findByFiRegistryId(fiRegistryId, pageable);
        return PageResponse.of(page, beneficiaryMapper.toResponseList(page.getContent()));
    }

    /**
     * Retrieves a beneficiary by ID.
     */
    public BeneficiaryResponse findById(Long id) {
        log.debug("Finding beneficiary by ID: {}", id);
        Beneficiary beneficiary = findEntityById(id);
        return beneficiaryMapper.toResponse(beneficiary);
    }

    /**
     * Creates a new beneficiary for an FI registry.
     */
    @Transactional
    public BeneficiaryResponse create(Long fiRegistryId, BeneficiaryRequest request) {
        log.info("Creating beneficiary for FI registry ID: {}", fiRegistryId);

        FiRegistry fiRegistry = findFiRegistry(fiRegistryId);

        // Validate FI type supports beneficiaries
        if (!fiRegistry.getFiType().isHasBeneficiaries()) {
            throw new BusinessException("FI type does not support beneficiaries: " + fiRegistry.getFiType().getCode());
        }

        // Validate request based on type
        validateBeneficiaryRequest(request);

        // Validate capital percentage
        validateCapitalPercentage(fiRegistryId, request.getParentId(), request.getCapitalPercentage());

        Beneficiary beneficiary = beneficiaryMapper.toEntity(request);
        beneficiary.setFiRegistry(fiRegistry);

        // Set parent if provided
        if (request.getParentId() != null) {
            Beneficiary parent = findEntityById(request.getParentId());
            if (!parent.getFiRegistry().getId().equals(fiRegistryId)) {
                throw new BusinessException("Parent beneficiary belongs to a different FI registry");
            }
            beneficiary.setParent(parent);
            beneficiary.setHierarchyLevel(parent.getHierarchyLevel() != null ? parent.getHierarchyLevel() + 1 : 1);
        } else {
            beneficiary.setHierarchyLevel(0);
        }

        // Auto-detect ultimate beneficiary
        if (request.getType() == BeneficiaryType.PHYSICAL && request.isUltimateBeneficiary()) {
            beneficiary.setUltimateBeneficiary(true);
        }

        Beneficiary saved = beneficiaryRepository.save(beneficiary);
        log.info("Created beneficiary with ID: {} for FI registry: {}", saved.getId(), fiRegistry.getCode());

        return beneficiaryMapper.toResponse(saved);
    }

    /**
     * Updates an existing beneficiary.
     */
    @Transactional
    public BeneficiaryResponse update(Long id, BeneficiaryRequest request) {
        log.info("Updating beneficiary with ID: {}", id);

        Beneficiary beneficiary = findEntityById(id);

        // Validate request based on type
        validateBeneficiaryRequest(request);

        // Validate capital percentage change
        if (request.getCapitalPercentage() != null &&
            !request.getCapitalPercentage().equals(beneficiary.getCapitalPercentage())) {
            validateCapitalPercentageUpdate(beneficiary, request.getCapitalPercentage());
        }

        // Handle parent change
        if (request.getParentId() != null && !request.getParentId().equals(
                beneficiary.getParent() != null ? beneficiary.getParent().getId() : null)) {
            Beneficiary newParent = findEntityById(request.getParentId());
            if (!newParent.getFiRegistry().getId().equals(beneficiary.getFiRegistry().getId())) {
                throw new BusinessException("Parent beneficiary belongs to a different FI registry");
            }
            // Prevent circular reference
            if (isDescendant(newParent, beneficiary)) {
                throw new BusinessException("Cannot set a descendant as parent (circular reference)");
            }
            beneficiary.setParent(newParent);
            beneficiary.setHierarchyLevel(newParent.getHierarchyLevel() + 1);
        }

        beneficiaryMapper.updateEntity(request, beneficiary);
        Beneficiary saved = beneficiaryRepository.save(beneficiary);

        log.info("Updated beneficiary with ID: {}", saved.getId());
        return beneficiaryMapper.toResponse(saved);
    }

    /**
     * Soft deletes a beneficiary and all its children.
     */
    @Transactional
    public void delete(Long id, String deletedBy) {
        log.info("Deleting beneficiary with ID: {}", id);

        Beneficiary beneficiary = findEntityById(id);

        // Recursively delete children
        deleteWithChildren(beneficiary, deletedBy);

        log.info("Beneficiary {} soft deleted with children", beneficiary.getDisplayName());
    }

    /**
     * Calculates total ownership percentage for root beneficiaries.
     */
    public BigDecimal calculateTotalOwnership(Long fiRegistryId) {
        return beneficiaryRepository.sumRootCapitalPercentageByFiRegistryId(fiRegistryId);
    }

    // === Private Helper Methods ===

    private Beneficiary findEntityById(Long id) {
        return beneficiaryRepository.findById(id)
                .filter(b -> !b.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary", "id", id));
    }

    private FiRegistry findFiRegistry(Long id) {
        return fiRegistryRepository.findById(id)
                .filter(fi -> !fi.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("FiRegistry", "id", id));
    }

    private void validateBeneficiaryRequest(BeneficiaryRequest request) {
        if (request.getType() == BeneficiaryType.PHYSICAL) {
            if (request.getFirstName() == null || request.getFirstName().isBlank()) {
                throw new BusinessException("First name is required for physical person");
            }
            if (request.getLastName() == null || request.getLastName().isBlank()) {
                throw new BusinessException("Last name is required for physical person");
            }
        } else if (request.getType() == BeneficiaryType.LEGAL) {
            if (request.getLegalEntityName() == null || request.getLegalEntityName().isBlank()) {
                throw new BusinessException("Legal entity name is required for legal entity");
            }
        }
    }

    private void validateCapitalPercentage(Long fiRegistryId, Long parentId, BigDecimal newPercentage) {
        if (newPercentage == null) return;

        BigDecimal currentTotal;
        if (parentId == null) {
            currentTotal = beneficiaryRepository.sumRootCapitalPercentageByFiRegistryId(fiRegistryId);
        } else {
            // For children, sum siblings' percentages
            List<Beneficiary> siblings = beneficiaryRepository.findByParentId(parentId);
            currentTotal = siblings.stream()
                    .map(Beneficiary::getCapitalPercentage)
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        if (currentTotal.add(newPercentage).compareTo(new BigDecimal("100")) > 0) {
            throw new BusinessException("Total capital percentage cannot exceed 100%");
        }
    }

    private void validateCapitalPercentageUpdate(Beneficiary beneficiary, BigDecimal newPercentage) {
        BigDecimal currentTotal;
        if (beneficiary.getParent() == null) {
            currentTotal = beneficiaryRepository.sumRootCapitalPercentageByFiRegistryId(beneficiary.getFiRegistry().getId());
        } else {
            List<Beneficiary> siblings = beneficiaryRepository.findByParentId(beneficiary.getParent().getId());
            currentTotal = siblings.stream()
                    .filter(s -> !s.getId().equals(beneficiary.getId()))
                    .map(Beneficiary::getCapitalPercentage)
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        // Subtract current beneficiary's percentage and add new
        BigDecimal adjustedTotal = currentTotal
                .subtract(beneficiary.getCapitalPercentage() != null ? beneficiary.getCapitalPercentage() : BigDecimal.ZERO)
                .add(newPercentage);

        if (adjustedTotal.compareTo(new BigDecimal("100")) > 0) {
            throw new BusinessException("Total capital percentage cannot exceed 100%");
        }
    }

    private boolean isDescendant(Beneficiary potential, Beneficiary ancestor) {
        if (potential.getId().equals(ancestor.getId())) {
            return true;
        }
        for (Beneficiary child : ancestor.getChildren()) {
            if (isDescendant(potential, child)) {
                return true;
            }
        }
        return false;
    }

    private void deleteWithChildren(Beneficiary beneficiary, String deletedBy) {
        // First delete all children recursively
        for (Beneficiary child : beneficiary.getChildren()) {
            deleteWithChildren(child, deletedBy);
        }
        // Then delete the beneficiary itself
        beneficiary.markAsDeleted(deletedBy);
        beneficiaryRepository.save(beneficiary);
    }
}
