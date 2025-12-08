package net.fina.first.service;

import net.fina.first.dto.common.PageResponse;
import net.fina.first.dto.request.FiRegistryCreateRequest;
import net.fina.first.dto.response.FiRegistryResponse;
import net.fina.first.exception.ResourceNotFoundException;
import net.fina.first.mapper.FiRegistryMapper;
import net.fina.first.model.FiRegistry;
import net.fina.first.model.FiType;
import net.fina.first.model.LegalForm;
import net.fina.first.model.Region;
import net.fina.first.model.enums.FiTypeCode;
import net.fina.first.model.enums.RegistrationStatus;
import net.fina.first.repository.FiRegistryRepository;
import net.fina.first.repository.FiTypeRepository;
import net.fina.first.repository.LegalFormRepository;
import net.fina.first.repository.RegionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FiRegistryServiceTest {

    @Mock
    private FiRegistryRepository fiRegistryRepository;

    @Mock
    private FiTypeRepository fiTypeRepository;

    @Mock
    private LegalFormRepository legalFormRepository;

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private FiRegistryMapper fiRegistryMapper;

    @InjectMocks
    private FiRegistryService fiRegistryService;

    private FiRegistry fiRegistry;
    private FiRegistryResponse fiRegistryResponse;
    private FiType fiType;
    private LegalForm legalForm;
    private Region region;

    @BeforeEach
    void setUp() {
        fiType = new FiType();
        fiType.setId(1L);
        fiType.setCode(FiTypeCode.BANK);
        fiType.setName("Bank");

        legalForm = new LegalForm();
        legalForm.setId(1L);
        legalForm.setCode("LLC");
        legalForm.setName("Limited Liability Company");

        region = new Region();
        region.setId(1L);
        region.setCode("TBI");
        region.setName("Tbilisi");

        fiRegistry = new FiRegistry();
        fiRegistry.setId(1L);
        fiRegistry.setCode("FI-001");
        fiRegistry.setFirmName("Test Bank");
        fiRegistry.setFiType(fiType);
        fiRegistry.setLegalForm(legalForm);
        fiRegistry.setStatus(RegistrationStatus.DRAFT);
        fiRegistry.setEmail("test@bank.ge");
        fiRegistry.setLegalAddress("123 Main Street");

        fiRegistryResponse = new FiRegistryResponse();
        fiRegistryResponse.setId(1L);
        fiRegistryResponse.setCode("FI-001");
        fiRegistryResponse.setFirmName("Test Bank");
    }

    @Nested
    @DisplayName("findById tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return FI registry when found")
        void shouldReturnFiRegistryWhenFound() {
            when(fiRegistryRepository.findById(1L)).thenReturn(Optional.of(fiRegistry));
            when(fiRegistryMapper.toResponse(fiRegistry)).thenReturn(fiRegistryResponse);

            FiRegistryResponse result = fiRegistryService.findById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getCode()).isEqualTo("FI-001");
            verify(fiRegistryRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when FI registry not found")
        void shouldThrowExceptionWhenNotFound() {
            when(fiRegistryRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> fiRegistryService.findById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("FiRegistry");
        }
    }

    @Nested
    @DisplayName("findAll tests")
    class FindAllTests {

        @Test
        @DisplayName("Should return paginated results")
        void shouldReturnPaginatedResults() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<FiRegistry> page = new PageImpl<>(List.of(fiRegistry), pageable, 1);

            when(fiRegistryRepository.findAllActive(any(Pageable.class))).thenReturn(page);
            when(fiRegistryMapper.toResponseList(anyList())).thenReturn(List.of(fiRegistryResponse));

            PageResponse<FiRegistryResponse> result = fiRegistryService.findAll(null, null, null, pageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("create tests")
    class CreateTests {

        @Test
        @DisplayName("Should create FI registry successfully")
        void shouldCreateFiRegistrySuccessfully() {
            FiRegistryCreateRequest request = new FiRegistryCreateRequest();
            request.setFirmName("New Bank");
            request.setFiTypeCode(FiTypeCode.BANK);
            request.setApplicationNumber("APP-2024-001");
            request.setApplicationReceivedDate(LocalDate.now());
            request.setLegalFormId(1L);
            request.setLegalAddressRegionId(1L);
            request.setLegalAddressCity("Tbilisi");
            request.setLegalAddress("123 Main Street");
            request.setPhone("+995555123456");
            request.setEmail("info@newbank.ge");

            when(fiTypeRepository.findByCode(FiTypeCode.BANK)).thenReturn(Optional.of(fiType));
            when(legalFormRepository.findById(1L)).thenReturn(Optional.of(legalForm));
            when(regionRepository.findById(1L)).thenReturn(Optional.of(region));
            when(fiRegistryRepository.existsByCode(anyString())).thenReturn(false);
            when(fiRegistryRepository.countByFiTypeCode(FiTypeCode.BANK)).thenReturn(0L);
            when(fiRegistryRepository.save(any(FiRegistry.class))).thenReturn(fiRegistry);
            when(fiRegistryMapper.toEntity(any(FiRegistryCreateRequest.class))).thenReturn(fiRegistry);
            when(fiRegistryMapper.toResponse(any(FiRegistry.class))).thenReturn(fiRegistryResponse);

            FiRegistryResponse result = fiRegistryService.create(request);

            assertThat(result).isNotNull();
            verify(fiRegistryRepository).save(any(FiRegistry.class));
        }
    }

    @Nested
    @DisplayName("workflow tests")
    class WorkflowTests {

        @Test
        @DisplayName("Should submit FI registry successfully")
        void shouldSubmitSuccessfully() {
            fiRegistry.setStatus(RegistrationStatus.DRAFT);
            when(fiRegistryRepository.findById(1L)).thenReturn(Optional.of(fiRegistry));
            when(fiRegistryRepository.save(any(FiRegistry.class))).thenReturn(fiRegistry);
            when(fiRegistryMapper.toResponse(any(FiRegistry.class))).thenReturn(fiRegistryResponse);

            FiRegistryResponse result = fiRegistryService.submit(1L);

            assertThat(result).isNotNull();
            verify(fiRegistryRepository).save(argThat(registry ->
                    registry.getStatus() == RegistrationStatus.PENDING_REVIEW));
        }

        @Test
        @DisplayName("Should approve FI registry successfully")
        void shouldApproveSuccessfully() {
            fiRegistry.setStatus(RegistrationStatus.PENDING_REVIEW);
            when(fiRegistryRepository.findById(1L)).thenReturn(Optional.of(fiRegistry));
            when(fiRegistryRepository.save(any(FiRegistry.class))).thenReturn(fiRegistry);
            when(fiRegistryMapper.toResponse(any(FiRegistry.class))).thenReturn(fiRegistryResponse);

            FiRegistryResponse result = fiRegistryService.approve(1L, "Approved");

            assertThat(result).isNotNull();
            verify(fiRegistryRepository).save(argThat(registry ->
                    registry.getStatus() == RegistrationStatus.APPROVED));
        }
    }
}
