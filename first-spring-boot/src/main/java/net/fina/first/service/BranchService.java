package net.fina.first.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fina.first.dto.common.PageResponse;
import net.fina.first.dto.request.BranchRequest;
import net.fina.first.dto.response.BranchResponse;
import net.fina.first.exception.BusinessException;
import net.fina.first.exception.DuplicateResourceException;
import net.fina.first.exception.ResourceNotFoundException;
import net.fina.first.mapper.BranchMapper;
import net.fina.first.model.Branch;
import net.fina.first.model.FiRegistry;
import net.fina.first.model.Region;
import net.fina.first.model.enums.BranchStatus;
import net.fina.first.repository.BranchRepository;
import net.fina.first.repository.FiRegistryRepository;
import net.fina.first.repository.RegionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing FI Branch operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BranchService {

    private final BranchRepository branchRepository;
    private final FiRegistryRepository fiRegistryRepository;
    private final RegionRepository regionRepository;
    private final BranchMapper branchMapper;

    /**
     * Retrieves all branches for an FI registry.
     */
    public List<BranchResponse> findByFiRegistryId(Long fiRegistryId) {
        log.debug("Finding branches for FI registry ID: {}", fiRegistryId);
        List<Branch> branches = branchRepository.findActiveByFiRegistryId(fiRegistryId);
        return branchMapper.toResponseList(branches);
    }

    /**
     * Retrieves branches for an FI registry with pagination.
     */
    public PageResponse<BranchResponse> findByFiRegistryId(Long fiRegistryId, Pageable pageable) {
        log.debug("Finding branches for FI registry ID: {} with pagination", fiRegistryId);
        Page<Branch> page = branchRepository.findByFiRegistryId(fiRegistryId, pageable);
        return PageResponse.of(page, branchMapper.toResponseList(page.getContent()));
    }

    /**
     * Retrieves a branch by ID.
     */
    public BranchResponse findById(Long id) {
        log.debug("Finding branch by ID: {}", id);
        Branch branch = findEntityById(id);
        return branchMapper.toResponse(branch);
    }

    /**
     * Creates a new branch for an FI registry.
     */
    @Transactional
    public BranchResponse create(Long fiRegistryId, BranchRequest request) {
        log.info("Creating branch for FI registry ID: {}", fiRegistryId);

        FiRegistry fiRegistry = findFiRegistry(fiRegistryId);

        // Validate FI type supports branches
        if (!fiRegistry.getFiType().isHasBranches()) {
            throw new BusinessException("FI type does not support branches: " + fiRegistry.getFiType().getCode());
        }

        // Check code uniqueness within FI
        if (branchRepository.existsByFiRegistryIdAndCode(fiRegistryId, request.getCode())) {
            throw new DuplicateResourceException("Branch", "code", request.getCode());
        }

        Branch branch = branchMapper.toEntity(request);
        branch.setFiRegistry(fiRegistry);

        // Set region if provided
        if (request.getRegionId() != null) {
            Region region = regionRepository.findById(request.getRegionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Region", "id", request.getRegionId()));
            branch.setRegion(region);
        }

        // Set default status if not provided
        if (branch.getStatus() == null) {
            branch.setStatus(BranchStatus.PENDING);
        }

        Branch saved = branchRepository.save(branch);
        log.info("Created branch with ID: {} for FI registry: {}", saved.getId(), fiRegistry.getCode());

        return branchMapper.toResponse(saved);
    }

    /**
     * Updates an existing branch.
     */
    @Transactional
    public BranchResponse update(Long id, BranchRequest request) {
        log.info("Updating branch with ID: {}", id);

        Branch branch = findEntityById(id);

        // Check code uniqueness if changed
        if (!branch.getCode().equals(request.getCode()) &&
            branchRepository.existsByFiRegistryIdAndCode(branch.getFiRegistry().getId(), request.getCode())) {
            throw new DuplicateResourceException("Branch", "code", request.getCode());
        }

        // Update region if provided
        if (request.getRegionId() != null) {
            Region region = regionRepository.findById(request.getRegionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Region", "id", request.getRegionId()));
            branch.setRegion(region);
        }

        branchMapper.updateEntity(request, branch);
        Branch saved = branchRepository.save(branch);

        log.info("Updated branch with ID: {}", saved.getId());
        return branchMapper.toResponse(saved);
    }

    /**
     * Soft deletes a branch.
     */
    @Transactional
    public void delete(Long id, String deletedBy) {
        log.info("Deleting branch with ID: {}", id);

        Branch branch = findEntityById(id);

        if (branch.getStatus() == BranchStatus.ACTIVE) {
            throw new BusinessException("Cannot delete an active branch");
        }

        branch.markAsDeleted(deletedBy);
        branchRepository.save(branch);

        log.info("Branch {} soft deleted", branch.getCode());
    }

    /**
     * Activates a branch.
     */
    @Transactional
    public BranchResponse activate(Long id) {
        log.info("Activating branch with ID: {}", id);

        Branch branch = findEntityById(id);
        branch.setStatus(BranchStatus.ACTIVE);
        Branch saved = branchRepository.save(branch);

        log.info("Branch {} activated", saved.getCode());
        return branchMapper.toResponse(saved);
    }

    /**
     * Closes a branch.
     */
    @Transactional
    public BranchResponse close(Long id) {
        log.info("Closing branch with ID: {}", id);

        Branch branch = findEntityById(id);
        branch.setStatus(BranchStatus.CLOSED);
        branch.setCancellationDate(java.time.LocalDate.now());
        Branch saved = branchRepository.save(branch);

        log.info("Branch {} closed", saved.getCode());
        return branchMapper.toResponse(saved);
    }

    // === Private Helper Methods ===

    private Branch findEntityById(Long id) {
        return branchRepository.findById(id)
                .filter(b -> !b.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", id));
    }

    private FiRegistry findFiRegistry(Long id) {
        return fiRegistryRepository.findById(id)
                .filter(fi -> !fi.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("FiRegistry", "id", id));
    }
}
