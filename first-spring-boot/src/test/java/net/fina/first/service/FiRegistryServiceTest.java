package net.fina.first.service;

import net.fina.first.dto.common.PageResponse;
import net.fina.first.dto.request.FiRegistryCreateRequest;
import net.fina.first.dto.response.FiRegistryResponse;
import net.fina.first.exception.ResourceNotFoundException;
import net.fina.first.mapper.FiRegistryMapper;
import net.fina.first.model.FiRegistry;
import net.fina.first.model.FiType;
import net.fina.first.model.enums.FiTypeCode;
import net.fina.first.model.enums.RegistrationStatus;
import net.fina.first.repository.FiRegistryRepository;
import net.fina.first.repository.FiTypeRepository;
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
    private FiRegistryMapper fiRegistryMapper;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private FiRegistryService fiRegistryService;

    private FiRegistry fiRegistry;
    private FiRegistryResponse fiRegistryResponse;
    private FiType fiType;

    @BeforeEach
    void setUp() {
        fiType = new FiType();
        fiType.setId(1L);
        fiType.setCode(FiTypeCode.BANK);
        fiType.setName("Bank");

        fiRegistry = new FiRegistry();
        fiRegistry.setId(1L);
        fiRegistry.setCode("FI-001");
        fiRegistry.setName("Test Bank");
        fiRegistry.setFiType(fiType);
        fiRegistry.setStatus(RegistrationStatus.DRAFT);

        fiRegistryResponse = new FiRegistryResponse();
        fiRegistryResponse.setId(1L);
        fiRegistryResponse.setCode("FI-001");
        fiRegistryResponse.setName("Test Bank");
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

            when(fiRegistryRepository.findAll(any(Pageable.class))).thenReturn(page);
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
            request.setName("New Bank");
            request.setFiTypeId(1L);

            when(fiTypeRepository.findById(1L)).thenReturn(Optional.of(fiType));
            when(fiRegistryRepository.existsByCode(anyString())).thenReturn(false);
            when(fiRegistryRepository.save(any(FiRegistry.class))).thenReturn(fiRegistry);
            when(fiRegistryMapper.toResponse(any(FiRegistry.class))).thenReturn(fiRegistryResponse);

            FiRegistryResponse result = fiRegistryService.create(request);

            assertThat(result).isNotNull();
            verify(fiRegistryRepository).save(any(FiRegistry.class));
            verify(auditLogService).log(eq("FI_REGISTRY_CREATE"), anyString(), anyLong(), any(), any());
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
                    registry.getStatus() == RegistrationStatus.PENDING_APPROVAL));
        }

        @Test
        @DisplayName("Should approve FI registry successfully")
        void shouldApproveSuccessfully() {
            fiRegistry.setStatus(RegistrationStatus.PENDING_APPROVAL);
            when(fiRegistryRepository.findById(1L)).thenReturn(Optional.of(fiRegistry));
            when(fiRegistryRepository.save(any(FiRegistry.class))).thenReturn(fiRegistry);
            when(fiRegistryMapper.toResponse(any(FiRegistry.class))).thenReturn(fiRegistryResponse);

            FiRegistryResponse result = fiRegistryService.approve(1L, "Approved");

            assertThat(result).isNotNull();
            verify(fiRegistryRepository).save(argThat(registry ->
                    registry.getStatus() == RegistrationStatus.ACTIVE));
        }
    }
}
