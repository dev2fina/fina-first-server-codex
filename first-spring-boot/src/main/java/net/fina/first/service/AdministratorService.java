package net.fina.first.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fina.first.dto.common.PageResponse;
import net.fina.first.dto.request.AdministratorRequest;
import net.fina.first.dto.response.AdministratorResponse;
import net.fina.first.exception.BusinessException;
import net.fina.first.exception.DuplicateResourceException;
import net.fina.first.exception.ResourceNotFoundException;
import net.fina.first.mapper.AdministratorMapper;
import net.fina.first.model.Administrator;
import net.fina.first.model.FiRegistry;
import net.fina.first.repository.AdministratorRepository;
import net.fina.first.repository.FiRegistryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for managing FI Administrator operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdministratorService {

    private final AdministratorRepository administratorRepository;
    private final FiRegistryRepository fiRegistryRepository;
    private final AdministratorMapper administratorMapper;

    /**
     * Retrieves all administrators for an FI registry.
     */
    public List<AdministratorResponse> findByFiRegistryId(Long fiRegistryId) {
        log.debug("Finding administrators for FI registry ID: {}", fiRegistryId);
        List<Administrator> administrators = administratorRepository.findActiveByFiRegistryId(fiRegistryId);
        return administratorMapper.toResponseList(administrators);
    }

    /**
     * Retrieves administrators with filters and pagination - for controller use.
     */
    public PageResponse<AdministratorResponse> findByFiRegistry(Long fiRegistryId, Boolean activeOnly,
                                                                  String search, Pageable pageable) {
        log.debug("Finding administrators for FI registry ID: {} with activeOnly: {} and search: {}",
                fiRegistryId, activeOnly, search);

        Page<Administrator> page;
        if (Boolean.TRUE.equals(activeOnly)) {
            page = administratorRepository.findCurrentByFiRegistryId(fiRegistryId, pageable);
        } else {
            page = administratorRepository.findByFiRegistryId(fiRegistryId, pageable);
        }

        return PageResponse.of(page, administratorMapper.toResponseList(page.getContent()));
    }

    /**
     * Retrieves active administrators for an FI registry.
     */
    public List<AdministratorResponse> findActiveAdministrators(Long fiRegistryId) {
        log.debug("Finding active administrators for FI registry ID: {}", fiRegistryId);
        List<Administrator> administrators = administratorRepository.findCurrentByFiRegistryId(fiRegistryId);
        return administratorMapper.toResponseList(administrators);
    }

    /**
     * Retrieves current (active) administrators for an FI registry.
     */
    public List<AdministratorResponse> findCurrentByFiRegistryId(Long fiRegistryId) {
        log.debug("Finding current administrators for FI registry ID: {}", fiRegistryId);
        List<Administrator> administrators = administratorRepository.findCurrentByFiRegistryId(fiRegistryId);
        return administratorMapper.toResponseList(administrators);
    }

    /**
     * Retrieves administrators with pagination.
     */
    public PageResponse<AdministratorResponse> findByFiRegistryId(Long fiRegistryId, Pageable pageable) {
        log.debug("Finding administrators for FI registry ID: {} with pagination", fiRegistryId);
        Page<Administrator> page = administratorRepository.findByFiRegistryId(fiRegistryId, pageable);
        return PageResponse.of(page, administratorMapper.toResponseList(page.getContent()));
    }

    /**
     * Retrieves an administrator by ID.
     */
    public AdministratorResponse findById(Long id) {
        log.debug("Finding administrator by ID: {}", id);
        Administrator administrator = findEntityById(id);
        return administratorMapper.toResponse(administrator);
    }

    /**
     * Creates a new administrator for an FI registry.
     */
    @Transactional
    public AdministratorResponse create(Long fiRegistryId, AdministratorRequest request) {
        log.info("Creating administrator for FI registry ID: {}", fiRegistryId);

        FiRegistry fiRegistry = findFiRegistry(fiRegistryId);

        // Validate FI type supports administrators
        if (!fiRegistry.getFiType().isHasAdministrators()) {
            throw new BusinessException("FI type does not support administrators: " + fiRegistry.getFiType().getCode());
        }

        // Check for duplicate identification number within FI
        if (request.getIdentificationNumber() != null &&
            administratorRepository.existsByFiRegistryIdAndIdentificationNumber(
                    fiRegistryId, request.getIdentificationNumber())) {
            throw new DuplicateResourceException("Administrator", "identificationNumber", request.getIdentificationNumber());
        }

        Administrator administrator = administratorMapper.toEntity(request);
        administrator.setFiRegistry(fiRegistry);

        Administrator saved = administratorRepository.save(administrator);
        log.info("Created administrator with ID: {} for FI registry: {}", saved.getId(), fiRegistry.getCode());

        return administratorMapper.toResponse(saved);
    }

    /**
     * Updates an existing administrator.
     */
    @Transactional
    public AdministratorResponse update(Long id, AdministratorRequest request) {
        log.info("Updating administrator with ID: {}", id);

        Administrator administrator = findEntityById(id);

        // Check for duplicate identification number if changed
        if (request.getIdentificationNumber() != null &&
            !request.getIdentificationNumber().equals(administrator.getIdentificationNumber()) &&
            administratorRepository.existsByFiRegistryIdAndIdentificationNumber(
                    administrator.getFiRegistry().getId(), request.getIdentificationNumber())) {
            throw new DuplicateResourceException("Administrator", "identificationNumber", request.getIdentificationNumber());
        }

        administratorMapper.updateEntity(request, administrator);
        Administrator saved = administratorRepository.save(administrator);

        log.info("Updated administrator with ID: {}", saved.getId());
        return administratorMapper.toResponse(saved);
    }

    /**
     * Terminates an administrator with optional reason.
     */
    @Transactional
    public AdministratorResponse terminate(Long id, String reason) {
        log.info("Terminating administrator with ID: {}, reason: {}", id, reason);

        Administrator administrator = findEntityById(id);
        administrator.setActive(false);
        administrator.setTerminationDate(LocalDate.now());
        if (reason != null) {
            administrator.setTerminationReason(reason);
        }

        Administrator saved = administratorRepository.save(administrator);
        log.info("Administrator {} terminated", saved.getFullName());

        return administratorMapper.toResponse(saved);
    }

    /**
     * Soft deletes an administrator.
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deleting administrator with ID: {}", id);

        Administrator administrator = findEntityById(id);
        administrator.markAsDeleted(getCurrentUsername());
        administratorRepository.save(administrator);

        log.info("Administrator {} soft deleted", administrator.getFullName());
    }

    // === Private Helper Methods ===

    private Administrator findEntityById(Long id) {
        return administratorRepository.findById(id)
                .filter(a -> !a.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Administrator", "id", id));
    }

    private FiRegistry findFiRegistry(Long id) {
        return fiRegistryRepository.findById(id)
                .filter(fi -> !fi.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("FiRegistry", "id", id));
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "system";
    }
}
