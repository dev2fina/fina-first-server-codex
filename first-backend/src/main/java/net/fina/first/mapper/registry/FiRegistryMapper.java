package net.fina.first.mapper.registry;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import net.fina.first.dto.registry.BeneficiaryDto;
import net.fina.first.dto.registry.BranchDto;
import net.fina.first.dto.registry.FiRegistryDto;
import net.fina.first.dto.registry.FiRegistryRequest;
import net.fina.first.dto.registry.GapDetailDto;
import net.fina.first.dto.registry.LicenseDto;
import net.fina.first.dto.registry.ManagerDto;
import net.fina.first.dto.registry.QuestionnaireResponseDto;
import net.fina.first.model.registry.Beneficiary;
import net.fina.first.model.registry.Branch;
import net.fina.first.model.registry.FinancialInstitution;
import net.fina.first.model.registry.GapDetail;
import net.fina.first.model.registry.License;
import net.fina.first.model.registry.Manager;
import net.fina.first.model.registry.QuestionnaireResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FiRegistryMapper {

    FinancialInstitution toEntity(FiRegistryRequest request);

    FiRegistryDto toDto(FinancialInstitution entity);

    Manager toEntity(ManagerDto dto);

    ManagerDto toDto(Manager entity);

    Beneficiary toEntity(BeneficiaryDto dto);

    BeneficiaryDto toDto(Beneficiary entity);

    Branch toEntity(BranchDto dto);

    BranchDto toDto(Branch entity);

    License toEntity(LicenseDto dto);

    LicenseDto toDto(License entity);

    QuestionnaireResponse toEntity(QuestionnaireResponseDto dto);

    QuestionnaireResponseDto toDto(QuestionnaireResponse entity);

    GapDetail toEntity(GapDetailDto dto);

    GapDetailDto toDto(GapDetail entity);

    List<ManagerDto> toManagerDtos(Iterable<Manager> entities);

    List<BeneficiaryDto> toBeneficiaryDtos(Iterable<Beneficiary> entities);

    List<BranchDto> toBranchDtos(Iterable<Branch> entities);

    List<LicenseDto> toLicenseDtos(Iterable<License> entities);

    List<QuestionnaireResponseDto> toQuestionnaireDtos(Iterable<QuestionnaireResponse> entities);

    List<GapDetailDto> toGapDtos(Iterable<GapDetail> entities);
}
