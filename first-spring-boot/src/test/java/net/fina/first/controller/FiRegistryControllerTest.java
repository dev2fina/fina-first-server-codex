package net.fina.first.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fina.first.dto.common.PageResponse;
import net.fina.first.dto.request.FiRegistryCreateRequest;
import net.fina.first.dto.response.FiRegistryResponse;
import net.fina.first.security.JwtTokenProvider;
import net.fina.first.service.FiRegistryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FiRegistryController.class)
class FiRegistryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FiRegistryService fiRegistryService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    private FiRegistryResponse fiRegistryResponse;

    @BeforeEach
    void setUp() {
        fiRegistryResponse = new FiRegistryResponse();
        fiRegistryResponse.setId(1L);
        fiRegistryResponse.setCode("FI-001");
        fiRegistryResponse.setName("Test Bank");
    }

    @Test
    @DisplayName("Should return 401 when not authenticated")
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/fi-registries"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return paginated FI registries when authenticated")
    @WithMockUser(authorities = "FI_REGISTRY_READ")
    void shouldReturnPaginatedFiRegistries() throws Exception {
        PageResponse<FiRegistryResponse> pageResponse = PageResponse.of(
                List.of(fiRegistryResponse), 1, 20, 0, 1);

        when(fiRegistryService.findAll(any(), any(), any(), any(Pageable.class)))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/fi-registries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].code").value("FI-001"));
    }

    @Test
    @DisplayName("Should return FI registry by ID")
    @WithMockUser(authorities = "FI_REGISTRY_READ")
    void shouldReturnFiRegistryById() throws Exception {
        when(fiRegistryService.findById(1L)).thenReturn(fiRegistryResponse);

        mockMvc.perform(get("/api/v1/fi-registries/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("FI-001"));
    }

    @Test
    @DisplayName("Should create FI registry")
    @WithMockUser(authorities = "FI_REGISTRY_CREATE")
    void shouldCreateFiRegistry() throws Exception {
        FiRegistryCreateRequest request = new FiRegistryCreateRequest();
        request.setName("New Bank");
        request.setFiTypeId(1L);

        when(fiRegistryService.create(any(FiRegistryCreateRequest.class)))
                .thenReturn(fiRegistryResponse);

        mockMvc.perform(post("/api/v1/fi-registries")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Should return 403 when user lacks permission")
    @WithMockUser(authorities = "SOME_OTHER_PERMISSION")
    void shouldReturn403WhenLacksPermission() throws Exception {
        mockMvc.perform(get("/api/v1/fi-registries"))
                .andExpect(status().isForbidden());
    }
}
