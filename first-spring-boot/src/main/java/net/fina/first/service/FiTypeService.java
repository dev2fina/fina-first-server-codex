package net.fina.first.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fina.first.dto.response.FiTypeResponse;
import net.fina.first.exception.ResourceNotFoundException;
import net.fina.first.mapper.FiTypeMapper;
import net.fina.first.model.FiType;
import net.fina.first.model.enums.FiTypeCode;
import net.fina.first.repository.FiTypeRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static net.fina.first.config.CacheConfig.FI_TYPES_CACHE;

/**
 * Service for managing FI Type operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FiTypeService {

    private final FiTypeRepository fiTypeRepository;
    private final FiTypeMapper fiTypeMapper;

    /**
     * Retrieves all FI types (alias for findAllActive).
     */
    @Cacheable(value = FI_TYPES_CACHE, key = "'all'")
    public List<FiTypeResponse> findAll() {
        log.debug("Finding all FI types");
        List<FiType> fiTypes = fiTypeRepository.findAllActive();
        return fiTypeMapper.toResponseList(fiTypes);
    }

    /**
     * Retrieves all active FI types.
     */
    @Cacheable(value = FI_TYPES_CACHE, key = "'active'")
    public List<FiTypeResponse> findAllActive() {
        log.debug("Finding all active FI types");
        List<FiType> fiTypes = fiTypeRepository.findAllActive();
        return fiTypeMapper.toResponseList(fiTypes);
    }

    /**
     * Retrieves an FI type by ID.
     */
    public FiTypeResponse findById(Long id) {
        log.debug("Finding FI type by ID: {}", id);
        FiType fiType = fiTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FiType", "id", id));
        return fiTypeMapper.toResponse(fiType);
    }

    /**
     * Retrieves an FI type by code.
     */
    @Cacheable(value = FI_TYPES_CACHE, key = "#code")
    public FiTypeResponse findByCode(FiTypeCode code) {
        log.debug("Finding FI type by code: {}", code);
        FiType fiType = fiTypeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("FiType", "code", code));
        return fiTypeMapper.toResponse(fiType);
    }

    /**
     * Retrieves FI types that support branches.
     */
    public List<FiTypeResponse> findAllWithBranchesEnabled() {
        log.debug("Finding FI types with branches enabled");
        List<FiType> fiTypes = fiTypeRepository.findAllWithBranchesEnabled();
        return fiTypeMapper.toResponseList(fiTypes);
    }

    /**
     * Retrieves FI types that support licenses.
     */
    public List<FiTypeResponse> findAllWithLicensesEnabled() {
        log.debug("Finding FI types with licenses enabled");
        List<FiType> fiTypes = fiTypeRepository.findAllWithLicensesEnabled();
        return fiTypeMapper.toResponseList(fiTypes);
    }
}
