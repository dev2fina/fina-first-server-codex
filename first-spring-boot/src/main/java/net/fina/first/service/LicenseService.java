package net.fina.first.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fina.first.dto.common.PageResponse;
import net.fina.first.dto.request.LicenseRequest;
import net.fina.first.dto.response.LicenseResponse;
import net.fina.first.exception.BusinessException;
import net.fina.first.exception.DuplicateResourceException;
import net.fina.first.exception.ResourceNotFoundException;
import net.fina.first.mapper.LicenseMapper;
import net.fina.first.model.FiRegistry;
import net.fina.first.model.License;
import net.fina.first.model.LicenseType;
import net.fina.first.model.enums.LicenseStatus;
import net.fina.first.repository.FiRegistryRepository;
import net.fina.first.repository.LicenseRepository;
import net.fina.first.repository.LicenseTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for managing FI License operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LicenseService {

    private final LicenseRepository licenseRepository;
    private final FiRegistryRepository fiRegistryRepository;
    private final LicenseTypeRepository licenseTypeRepository;
    private final LicenseMapper licenseMapper;

    /**
     * Retrieves all licenses for an FI registry.
     */
    public List<LicenseResponse> findByFiRegistryId(Long fiRegistryId) {
        log.debug("Finding licenses for FI registry ID: {}", fiRegistryId);
        List<License> licenses = licenseRepository.findActiveByFiRegistryId(fiRegistryId);
        return licenseMapper.toResponseList(licenses);
    }

    /**
     * Retrieves licenses with pagination.
     */
    public PageResponse<LicenseResponse> findByFiRegistryId(Long fiRegistryId, Pageable pageable) {
        log.debug("Finding licenses for FI registry ID: {} with pagination", fiRegistryId);
        Page<License> page = licenseRepository.findByFiRegistryId(fiRegistryId, pageable);
        return PageResponse.of(page, licenseMapper.toResponseList(page.getContent()));
    }

    /**
     * Retrieves a license by ID.
     */
    public LicenseResponse findById(Long id) {
        log.debug("Finding license by ID: {}", id);
        License license = findEntityById(id);
        return licenseMapper.toResponse(license);
    }

    /**
     * Creates a new license for an FI registry.
     */
    @Transactional
    public LicenseResponse create(Long fiRegistryId, LicenseRequest request) {
        log.info("Creating license for FI registry ID: {}", fiRegistryId);

        FiRegistry fiRegistry = findFiRegistry(fiRegistryId);

        // Validate FI type supports licenses
        if (!fiRegistry.getFiType().isHasLicenses()) {
            throw new BusinessException("FI type does not support licenses: " + fiRegistry.getFiType().getCode());
        }

        // Check license number uniqueness
        if (licenseRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateResourceException("License", "licenseNumber", request.getLicenseNumber());
        }

        License license = licenseMapper.toEntity(request);
        license.setFiRegistry(fiRegistry);

        // Set license type if provided
        if (request.getLicenseTypeId() != null) {
            LicenseType licenseType = licenseTypeRepository.findById(request.getLicenseTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("LicenseType", "id", request.getLicenseTypeId()));
            license.setLicenseType(licenseType);

            // Auto-calculate expiration date if license type has validity period
            if (licenseType.getValidityPeriodMonths() != null && license.getEffectiveDate() != null) {
                license.setExpirationDate(license.getEffectiveDate().plusMonths(licenseType.getValidityPeriodMonths()));
            }
        }

        // Set default status if not provided
        if (license.getStatus() == null) {
            license.setStatus(LicenseStatus.PENDING);
        }

        License saved = licenseRepository.save(license);
        log.info("Created license with ID: {} for FI registry: {}", saved.getId(), fiRegistry.getCode());

        return licenseMapper.toResponse(saved);
    }

    /**
     * Updates an existing license.
     */
    @Transactional
    public LicenseResponse update(Long id, LicenseRequest request) {
        log.info("Updating license with ID: {}", id);

        License license = findEntityById(id);

        // Check license number uniqueness if changed
        if (!license.getLicenseNumber().equals(request.getLicenseNumber()) &&
            licenseRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateResourceException("License", "licenseNumber", request.getLicenseNumber());
        }

        // Update license type if provided
        if (request.getLicenseTypeId() != null) {
            LicenseType licenseType = licenseTypeRepository.findById(request.getLicenseTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("LicenseType", "id", request.getLicenseTypeId()));
            license.setLicenseType(licenseType);
        }

        licenseMapper.updateEntity(request, license);
        License saved = licenseRepository.save(license);

        log.info("Updated license with ID: {}", saved.getId());
        return licenseMapper.toResponse(saved);
    }

    /**
     * Activates a license.
     */
    @Transactional
    public LicenseResponse activate(Long id) {
        log.info("Activating license with ID: {}", id);

        License license = findEntityById(id);

        if (license.getStatus() != LicenseStatus.PENDING) {
            throw new BusinessException("License can only be activated from PENDING status");
        }

        license.setStatus(LicenseStatus.ACTIVE);
        if (license.getEffectiveDate() == null) {
            license.setEffectiveDate(LocalDate.now());
        }

        License saved = licenseRepository.save(license);
        log.info("License {} activated", saved.getLicenseNumber());

        return licenseMapper.toResponse(saved);
    }

    /**
     * Suspends a license.
     */
    @Transactional
    public LicenseResponse suspend(Long id, String reason) {
        log.info("Suspending license with ID: {}", id);

        License license = findEntityById(id);

        if (license.getStatus() != LicenseStatus.ACTIVE) {
            throw new BusinessException("License can only be suspended from ACTIVE status");
        }

        license.setStatus(LicenseStatus.SUSPENDED);
        license.setSuspensionDate(LocalDate.now());
        license.setSuspensionReason(reason);

        License saved = licenseRepository.save(license);
        log.info("License {} suspended", saved.getLicenseNumber());

        return licenseMapper.toResponse(saved);
    }

    /**
     * Revokes a license.
     */
    @Transactional
    public LicenseResponse revoke(Long id, String reason) {
        log.info("Revoking license with ID: {}", id);

        License license = findEntityById(id);

        license.setStatus(LicenseStatus.REVOKED);
        license.setRevocationDate(LocalDate.now());
        license.setRevocationReason(reason);

        License saved = licenseRepository.save(license);
        log.info("License {} revoked", saved.getLicenseNumber());

        return licenseMapper.toResponse(saved);
    }

    /**
     * Reactivates a suspended license.
     */
    @Transactional
    public LicenseResponse reactivate(Long id) {
        log.info("Reactivating license with ID: {}", id);

        License license = findEntityById(id);

        if (license.getStatus() != LicenseStatus.SUSPENDED) {
            throw new BusinessException("License can only be reactivated from SUSPENDED status");
        }

        license.setStatus(LicenseStatus.ACTIVE);
        license.setSuspensionDate(null);
        license.setSuspensionReason(null);

        License saved = licenseRepository.save(license);
        log.info("License {} reactivated", saved.getLicenseNumber());

        return licenseMapper.toResponse(saved);
    }

    /**
     * Finds expiring licenses within a date range.
     */
    public List<LicenseResponse> findExpiringLicenses(LocalDate startDate, LocalDate endDate) {
        log.debug("Finding licenses expiring between {} and {}", startDate, endDate);
        List<License> licenses = licenseRepository.findExpiringLicenses(LicenseStatus.ACTIVE, startDate, endDate);
        return licenseMapper.toResponseList(licenses);
    }

    /**
     * Soft deletes a license.
     */
    @Transactional
    public void delete(Long id, String deletedBy) {
        log.info("Deleting license with ID: {}", id);

        License license = findEntityById(id);

        if (license.getStatus() == LicenseStatus.ACTIVE) {
            throw new BusinessException("Cannot delete an active license");
        }

        license.markAsDeleted(deletedBy);
        licenseRepository.save(license);

        log.info("License {} soft deleted", license.getLicenseNumber());
    }

    // === Private Helper Methods ===

    private License findEntityById(Long id) {
        return licenseRepository.findById(id)
                .filter(l -> !l.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("License", "id", id));
    }

    private FiRegistry findFiRegistry(Long id) {
        return fiRegistryRepository.findById(id)
                .filter(fi -> !fi.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("FiRegistry", "id", id));
    }
}
