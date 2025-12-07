package net.fina.server.cems.model.helper;

import net.fina.common.client.fis.FiModelSimple;
import net.fina.common.shared.KeyValuePair;
import net.fina.messages.MessagesUtil;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.cems.entity.sanction.*;
import net.fina.server.cems.model.CEMSInspectionModel;
import net.fina.server.cems.model.CEMSSanctionModel;
import net.fina.server.cems.model.CEMSSanctionRegulationModel;
import net.fina.server.cems.model.CEMSSanctionedEmployeeInfoModel;
import net.fina.server.misc.ObjectUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CEMSSanctionModelHelper {


    public static CEMSSanction toEntity(CEMSSanctionModel sanction) {
        CEMSSanction result = new CEMSSanction();
        ObjectUtil.copyProperties(sanction, result);

        if (sanction.getMeasureOfInfluence() != null) {
            result.setMeasureOfInfluence(sanction.getMeasureOfInfluence().stream().map(i -> new CEMSSanctionMeasureInfluence(i.getKey())).collect(Collectors.toList()));
        }
        if (sanction.getDecisionMakingBodyCatalog() != null) {
            result.setDecisionMakingBodyCatalog(CEMSDecisionMakingBodyCatalog.valueOf(sanction.getDecisionMakingBodyCatalog().getKey()));
        }
        if (sanction.getMeasureReasonCatalog() != null && !sanction.getMeasureReasonCatalog().isEmpty()) {
            result.setMeasureReasonCatalog(sanction.getMeasureReasonCatalog().stream().map(r -> new CEMSSanctionMeasureReasons(r.getKey())).collect(Collectors.toList()));

        }
        if (sanction.getStatus() != null) {
            result.setStatus(new CEMSSanctionStatus(sanction.getStatus()));
        }

        if (sanction.getSanctionedEmployees() != null) {
            List<CEMSSanctionedEmployeeInfo> employeeInfoList = new ArrayList<>();
            for (CEMSSanctionedEmployeeInfoModel emp : sanction.getSanctionedEmployees()) {
                employeeInfoList.add(new CEMSSanctionedEmployeeInfo(emp.getId(), emp.getEmployeeName(), emp.getEmployeeId(), emp.getEmployeePosition()));
            }
            result.setSanctionedEmployeeList(employeeInfoList);

        }

        List<CEMSSanctionRegulation> regulations = new ArrayList<>();

        for (CEMSSanctionRegulationModel regulation : sanction.getRegulations()) {
            CEMSSanctionRegulationTable regCatalog = null;
            if (regulation.getRegulationCatalog() != null) {
                regCatalog = new CEMSSanctionRegulationTable(regulation.getRegulationCatalog().getKey());
            }
            regulations.add(new CEMSSanctionRegulation(regulation.getId(), regCatalog, regulation.getValue(), regulation.getActualValue()));
        }

        result.setRegulationList(regulations);

        return result;
    }

    public static CEMSSanctionModel toModel(CEMSSanction sanction, String langCode) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        CEMSSanctionModel result = new CEMSSanctionModel();
        ObjectUtil.copyProperties(sanction, result);

        result.setMeasureOfInfluence(sanction.getMeasureOfInfluence().stream().map(i -> new KeyValuePair<>(i.getCode(), i.getDescription().getDescription(langId))).collect(Collectors.toList()));
        if (sanction.getDecisionMakingBodyCatalog() != null) {
            result.setDecisionMakingBodyCatalog(new KeyValuePair<>(sanction.getDecisionMakingBodyCatalog().name(), MessagesUtil.getString(sanction.getDecisionMakingBodyCatalog().getCode(), langCode)));
        }
        result.setMeasureReasonCatalog(sanction.getMeasureReasonCatalog().stream().map(i -> new KeyValuePair<>(i.getCode(), i.getDescription().getDescription(langId))).collect(Collectors.toList()));
        if (sanction.getStatus() != null) {
            result.setStatus(sanction.getStatus().getCode());
        }

        List<CEMSSanctionedEmployeeInfoModel> employeeInfoList = new ArrayList<>();
        for (CEMSSanctionedEmployeeInfo emp : sanction.getSanctionedEmployeeList()) {
            employeeInfoList.add(new CEMSSanctionedEmployeeInfoModel(emp.getId(), emp.getEmployeeName(), emp.getEmployeeId(), emp.getEmployeePosition()));
        }
        result.setSanctionedEmployees(employeeInfoList);

        List<CEMSSanctionRegulationModel> regulations = new ArrayList<>();

        for (CEMSSanctionRegulation regulation : sanction.getRegulationList()) {
            KeyValuePair<String, String> regCatalog = null;
            if (regulation.getRegulationCatalog() != null) {
                regCatalog = new KeyValuePair<>(regulation.getRegulationCatalog().getCode(), regulation.getRegulationCatalog().getDescription().getDescription(langId));
            }
            regulations.add(new CEMSSanctionRegulationModel(regulation.getId(), regCatalog, regulation.getValue(), regulation.getActualValue()));
        }

        result.setRegulations(regulations);

        if (sanction.getInspection() != null) {
            result.setInspection(new CEMSInspectionModel(sanction.getInspection().getId(), new FiModelSimple(sanction.getInspection().getFi().getId(), sanction.getInspection().getFi().getCode(), sanction.getInspection().getFi().getDescription().getDescription(langId)), sanction.getInspection().getType()));
        }

        return result;
    }

    public static List<CEMSSanctionModel> toModels(List<CEMSSanction> result, String langCode) {
        return result.stream().map(s -> toModel(s, langCode)).collect(Collectors.toList());
    }
}
