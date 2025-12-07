package net.fina.first.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fina.first.dto.response.LegalFormResponse;
import net.fina.first.exception.ResourceNotFoundException;
import net.fina.first.mapper.LegalFormMapper;
import net.fina.first.model.LegalForm;
import net.fina.first.repository.LegalFormRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LegalFormService {

    private final LegalFormRepository legalFormRepository;
    private final LegalFormMapper legalFormMapper;

    @Cacheable(value = "legalForms")
    public List<LegalFormResponse> findAll() {
        log.debug("Fetching all legal forms");
        List<LegalForm> legalForms = legalFormRepository.findAllByOrderBySortOrderAsc();
        return legalFormMapper.toResponseList(legalForms);
    }

    @Cacheable(value = "legalForms", key = "#id")
    public LegalFormResponse findById(Long id) {
        log.debug("Fetching legal form by id: {}", id);
        LegalForm legalForm = legalFormRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LegalForm", "id", id));
        return legalFormMapper.toResponse(legalForm);
    }

    @Cacheable(value = "legalForms", key = "#code")
    public LegalFormResponse findByCode(String code) {
        log.debug("Fetching legal form by code: {}", code);
        LegalForm legalForm = legalFormRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("LegalForm", "code", code));
        return legalFormMapper.toResponse(legalForm);
    }

    public List<LegalFormResponse> findActive() {
        log.debug("Fetching active legal forms");
        List<LegalForm> legalForms = legalFormRepository.findByActiveTrueOrderBySortOrderAsc();
        return legalFormMapper.toResponseList(legalForms);
    }
}
