package net.fina.first.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fina.first.dto.response.LicenseTypeResponse;
import net.fina.first.exception.ResourceNotFoundException;
import net.fina.first.mapper.LicenseTypeMapper;
import net.fina.first.model.LicenseType;
import net.fina.first.model.enums.FiTypeCode;
import net.fina.first.repository.LicenseTypeRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LicenseTypeService {

    private final LicenseTypeRepository licenseTypeRepository;
    private final LicenseTypeMapper licenseTypeMapper;

    @Cacheable(value = "licenseTypes")
    public List<LicenseTypeResponse> findAll() {
        log.debug("Fetching all license types");
        List<LicenseType> licenseTypes = licenseTypeRepository.findAllOrderByNameAsc();
        return licenseTypeMapper.toResponseList(licenseTypes);
    }

    @Cacheable(value = "licenseTypes", key = "#id")
    public LicenseTypeResponse findById(Long id) {
        log.debug("Fetching license type by id: {}", id);
        LicenseType licenseType = licenseTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LicenseType", "id", id));
        return licenseTypeMapper.toResponse(licenseType);
    }

    @Cacheable(value = "licenseTypes", key = "#fiTypeCode")
    public List<LicenseTypeResponse> findByFiType(FiTypeCode fiTypeCode) {
        log.debug("Fetching license types by FI type: {}", fiTypeCode);
        List<LicenseType> licenseTypes = licenseTypeRepository.findByFiTypeCodeOrderByNameAsc(fiTypeCode);
        return licenseTypeMapper.toResponseList(licenseTypes);
    }
}
