package net.fina.server.cems.model.helper;

import net.fina.common.client.fis.FiModelSimple;
import net.fina.common.shared.KeyValuePair;
import net.fina.common.shared.LanguageSampleModel;
import net.fina.common.shared.user.UserModelSimple;
import net.fina.messages.MessagesUtil;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.cems.entity.*;
import net.fina.server.cems.entity.sanction.CEMSSanction;
import net.fina.server.cems.model.CEMSInspectionMetaInfoModel;
import net.fina.server.cems.model.CEMSInspectionModel;
import net.fina.server.cems.model.CEMSReportingYearInspectionModel;
import net.fina.server.fi.entity.Fi;
import net.fina.server.misc.ObjectUtil;
import net.fina.server.security.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public class CEMSInspectionModelHelper {

    public static CEMSInspectionModel toModel(CEMSInspection inspection, long langId) {
        CEMSInspectionModel result = new CEMSInspectionModel();

        ObjectUtil.copyProperties(inspection, result);

        result.setDecisions(getRecommendationByType(inspection.getRecommendations(), CEMSRecommendationType.DECISION));
        result.setRecommendationList(getRecommendationByType(inspection.getRecommendations(), CEMSRecommendationType.RECOMMENDATION));

        if (inspection.getFi() != null) {
            result.setFi(new FiModelSimple(inspection.getFi().getId(), inspection.getFi().getCode(), inspection.getFi().getDescription().getDescription(langId)));
        }

        if (inspection.getManager() != null) {
            User manager = inspection.getManager();
            result.setManager(new UserModelSimple(manager.getId(), manager.getLogin(), manager.getDescription().getDescription(langId)));
        }

        if (inspection.getReportingYearInspection() != null) {
            CEMSReportingYearInspectionModel cry = new CEMSReportingYearInspectionModel();
            ObjectUtil.copyProperties(inspection.getReportingYearInspection(), cry);

            if (inspection.getReportingYearInspection().getManager() != null) {
                User u = inspection.getReportingYearInspection().getManager();
                cry.setManager(new UserModelSimple(u.getId(), u.getLogin(), u.getDescription().getDescription(ThreadLocalHolder.getLanguage().getId())));
            }

            result.setReportingYearInspection(cry);
        }

        if (inspection.getMetaInfo() != null) {
            CEMSInspectionMetaInfoModel metaInfoModel = new CEMSInspectionMetaInfoModel();
            ObjectUtil.copyProperties(inspection.getMetaInfo(), metaInfoModel);

            result.setMetaInfo(metaInfoModel);
        }

        if (inspection.getSanctionList() != null) {
            result.setSanctionsStatuses(getSanctionsStatuses(inspection.getSanctionList()));
        }

        return result;
    }

    public static CEMSInspection toEntity(CEMSInspectionModel inspection) {
        CEMSInspection result = new CEMSInspection();

        ObjectUtil.copyProperties(inspection, result);

        if (inspection.getFi() != null) {
            result.setFi(new Fi(inspection.getFi().getId()));
        }

        if (inspection.getManager() != null) {
            UserModelSimple manager = inspection.getManager();
            result.setManager(new User(manager.getId()));
        }

        if (inspection.getReportingYearInspection() != null) {
            CEMSReportingYearInspection cry = new CEMSReportingYearInspection();
            ObjectUtil.copyProperties(inspection.getReportingYearInspection(), cry);

            if (inspection.getReportingYearInspection().getManager() != null) {
                cry.setManager(new User(inspection.getReportingYearInspection().getManager().getId()));
            }

            result.setReportingYearInspection(cry);
        }

        if (inspection.getMetaInfo() != null) {
            CEMSInspectionMetaInfo metaInfo = new CEMSInspectionMetaInfo();

            ObjectUtil.copyProperties(inspection.getMetaInfo(), metaInfo);

            result.setMetaInfo(metaInfo);
        }


        return result;
    }

    public static List<CEMSInspectionModel> toModels(List<CEMSInspection> entities, long langId) {
        return entities.stream().map(e -> toModel(e, langId)).collect(Collectors.toList());
    }

    private static List<KeyValuePair<String, String>> getRecommendationByType(List<CEMSRecommendation> recommendations, CEMSRecommendationType type) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        return recommendations.stream().filter(r -> r.getType().equals(type)).map(r -> {
            KeyValuePair<String, String> keyValuePair = new KeyValuePair<>(r.getId(), null);
            if (r.getStatusInfo() != null && r.getStatusInfo().getStatus() != null && r.getStatusInfo().getStatus().getDescription() != null) {
                keyValuePair.setValue(r.getStatusInfo().getStatus().getDescription().getDescription(langId));
            }
            return keyValuePair;
        }).collect(Collectors.toList());
    }

    private static List<KeyValuePair<Long, String>> getSanctionsStatuses(List<CEMSSanction> sanctions) {
        LanguageSampleModel language = ThreadLocalHolder.getLanguage();
        String langCode = language.getCode();
        long langId = language.getId();

        return sanctions.stream().map(s -> new KeyValuePair<>(s.getId(), MessagesUtil.getString(s.getStatus().getDescription().getDescription(langId), langCode))).collect(Collectors.toList());
    }
}
