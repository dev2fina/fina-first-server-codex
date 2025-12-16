package net.fina.first.dto.registry;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import net.fina.first.model.registry.FiType;
import net.fina.first.model.registry.RegistrationStatus;

@Data
public class FiRegistryDto {
    private Long id;
    private String registryCode;
    private String legalName;
    private String brandName;
    private FiType fiType;
    private RegistrationStatus status;
    private LocalDate phaseOneSubmittedAt;
    private LocalDate phaseTwoSubmittedAt;
    private LocalDate registrationCompletedAt;
    private String headquartersAddress;
    private String headquartersCity;
    private String headquartersCountry;
    private String registrationCountry;
    private String controllerComments;
    private List<ManagerDto> managers;
    private List<BeneficiaryDto> beneficiaries;
    private List<BranchDto> branches;
    private List<LicenseDto> licenses;
    private List<QuestionnaireResponseDto> questionnaireResponses;
    private List<GapDetailDto> gaps;
}
