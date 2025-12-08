package net.fina.first.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fina.first.dto.response.RegionResponse;
import net.fina.first.exception.ResourceNotFoundException;
import net.fina.first.mapper.RegionMapper;
import net.fina.first.model.Region;
import net.fina.first.repository.RegionRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static net.fina.first.config.CacheConfig.REGIONS_CACHE;

/**
 * Service for managing Region operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RegionService {

    private final RegionRepository regionRepository;
    private final RegionMapper regionMapper;

    /**
     * Retrieves all regions (alias for findAllActive).
     */
    @Cacheable(value = REGIONS_CACHE, key = "'all'")
    public List<RegionResponse> findAll() {
        log.debug("Finding all regions");
        List<Region> regions = regionRepository.findAllActive();
        return regionMapper.toResponseList(regions);
    }

    /**
     * Retrieves all active regions.
     */
    @Cacheable(value = REGIONS_CACHE, key = "'active'")
    public List<RegionResponse> findAllActive() {
        log.debug("Finding all active regions");
        List<Region> regions = regionRepository.findAllActive();
        return regionMapper.toResponseList(regions);
    }

    /**
     * Retrieves root regions (alias for findRootRegions).
     */
    @Cacheable(value = REGIONS_CACHE, key = "'roots'")
    public List<RegionResponse> findRoots() {
        log.debug("Finding root regions");
        List<Region> regions = regionRepository.findRootRegions();
        return regionMapper.toResponseList(regions);
    }

    /**
     * Retrieves root regions (top-level).
     */
    public List<RegionResponse> findRootRegions() {
        log.debug("Finding root regions");
        List<Region> regions = regionRepository.findRootRegions();
        return regionMapper.toResponseList(regions);
    }

    /**
     * Retrieves child regions of a parent (alias for findByParentId).
     */
    public List<RegionResponse> findChildren(Long parentId) {
        log.debug("Finding children of region ID: {}", parentId);
        List<Region> regions = regionRepository.findByParentId(parentId);
        return regionMapper.toResponseList(regions);
    }

    /**
     * Retrieves a region by ID.
     */
    public RegionResponse findById(Long id) {
        log.debug("Finding region by ID: {}", id);
        Region region = regionRepository.findById(id)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Region", "id", id));
        return regionMapper.toResponse(region);
    }

    /**
     * Retrieves a region by code.
     */
    public RegionResponse findByCode(String code) {
        log.debug("Finding region by code: {}", code);
        Region region = regionRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Region", "code", code));
        return regionMapper.toResponse(region);
    }

    /**
     * Retrieves child regions of a parent.
     */
    public List<RegionResponse> findByParentId(Long parentId) {
        log.debug("Finding regions by parent ID: {}", parentId);
        List<Region> regions = regionRepository.findByParentId(parentId);
        return regionMapper.toResponseList(regions);
    }

    /**
     * Retrieves regions by hierarchy level.
     */
    public List<RegionResponse> findByLevel(Integer level) {
        log.debug("Finding regions by level: {}", level);
        List<Region> regions = regionRepository.findByLevel(level);
        return regionMapper.toResponseList(regions);
    }

    /**
     * Searches regions by name.
     */
    public List<RegionResponse> searchByName(String name) {
        log.debug("Searching regions by name: {}", name);
        List<Region> regions = regionRepository.findByNameContaining(name);
        return regionMapper.toResponseList(regions);
    }
}
