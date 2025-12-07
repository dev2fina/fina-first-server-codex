package net.fina.server.cems.model.helper;

import net.fina.common.client.fis.FiModelSimple;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.cems.entity.CEMSRecommendation;
import net.fina.server.cems.entity.CEMSRecommendationStatusHistory;
import net.fina.server.cems.entity.CEMSRecommendationStatusInfo;
import net.fina.server.cems.entity.CEMSResponsiblePerson;
import net.fina.server.cems.entity.sanction.CEMSSanctionStatus;
import net.fina.server.cems.model.CEMSInspectionModel;
import net.fina.server.cems.model.CEMSRecommendationModel;
import net.fina.server.cems.model.CEMSRecommendationStatusHistoryModel;
import net.fina.server.cems.model.CEMSResponsiblePersonModel;
import net.fina.server.fi.entity.Fi;
import net.fina.server.misc.ObjectUtil;

import java.util.List;
import java.util.stream.Collectors;

public class CEMSRecommendationModelHelper {

    public static CEMSRecommendationModel toModel(CEMSRecommendation recommendation) {
        CEMSRecommendationModel result = new CEMSRecommendationModel();
        ObjectUtil.copyProperties(recommendation, result);

        if (recommendation.getStatusInfo() != null) {
            result.setStatus(recommendation.getStatusInfo().getStatus().getCode());
            result.setFiActions(recommendation.getStatusInfo().getFiActions());
            result.setNote(recommendation.getStatusInfo().getNote());
        }

        if (recommendation.getFiResponsiblePersons() != null) {
            List<CEMSResponsiblePersonModel> persons = recommendation.getFiResponsiblePersons().stream().map(rp -> new CEMSResponsiblePersonModel(rp.getId(), rp.getFullName(), rp.getPosition())).collect(Collectors.toList());
            result.setFiResponsiblePersons(persons);
        }

        CEMSInspectionModel inspection = new CEMSInspectionModel();
        Fi fi = recommendation.getInspection().getFi();
        inspection.setFi(new FiModelSimple(fi.getId(), fi.getCode(), fi.getDescription().getDescription(ThreadLocalHolder.getLanguage().getId())));
        result.setInspection(inspection);

        return result;
    }

    public static CEMSRecommendation toEntity(CEMSRecommendationModel recommendation) {
        CEMSRecommendation result = new CEMSRecommendation();

        ObjectUtil.copyProperties(recommendation, result);

        CEMSRecommendationStatusInfo statusInfo = new CEMSRecommendationStatusInfo();
        statusInfo.setStatus(new CEMSSanctionStatus(recommendation.getStatus()));
        statusInfo.setFiActions(recommendation.getFiActions());
        statusInfo.setNote(recommendation.getNote());
        result.setStatusInfo(statusInfo);

        if (recommendation.getFiResponsiblePersons() != null) {
            List<CEMSResponsiblePerson> persons = recommendation.getFiResponsiblePersons().stream().map(rp -> new CEMSResponsiblePerson(rp.getId(), rp.getFullName(), rp.getPosition())).collect(Collectors.toList());
            result.setFiResponsiblePersons(persons);
        }

        return result;
    }

    public static List<CEMSRecommendationModel> toModels(List<CEMSRecommendation> entities) {
        return entities.stream().map(CEMSRecommendationModelHelper::toModel).collect(Collectors.toList());
    }

    public static CEMSRecommendationStatusHistoryModel toModel(CEMSRecommendationStatusHistory status) {
        long langId = ThreadLocalHolder.getLanguage().getId();

        CEMSRecommendationStatusHistoryModel result = new CEMSRecommendationStatusHistoryModel();
        ObjectUtil.copyProperties(status, result);

        if (status.getStatusInfo().getStatus() != null) {
            result.setStatus(status.getStatusInfo().getStatus().getDescription().getDescription(langId));
        }
        result.setFiActions(status.getStatusInfo().getFiActions());
        result.setNote(status.getStatusInfo().getNote());
        return result;
    }

    public static List<CEMSRecommendationStatusHistoryModel> toStatusModels(List<CEMSRecommendationStatusHistory> history) {
        return history.stream().map(CEMSRecommendationModelHelper::toModel).collect(Collectors.toList());
    }
}
