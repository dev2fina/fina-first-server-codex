package net.fina.first.dto.registry;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import net.fina.first.model.registry.FiType;

@Data
public class FiRegistryRequest {
    @NotBlank
    @Size(max = 64)
    private String registryCode;

    @NotBlank
    @Size(max = 255)
    private String legalName;

    @Size(max = 255)
    private String brandName;

    @NotNull
    private FiType fiType;

    private LocalDate establishmentDate;

    @Size(max = 255)
    private String headquartersAddress;

    @Size(max = 100)
    private String headquartersCity;

    @Size(max = 100)
    private String headquartersCountry;

    @Size(max = 100)
    private String registrationCountry;

    @Valid
    private List<ManagerDto> managers;

    @Valid
    private List<BeneficiaryDto> beneficiaries;

    @Valid
    private List<BranchDto> branches;
}
