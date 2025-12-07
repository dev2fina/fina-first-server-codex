package net.fina.server.survey.proxy;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.server.util.PagingUtil;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.server.dcs.uploadfile.entity.SortInfo;
import net.fina.server.survey.api.SurveyLocal;
import net.fina.server.survey.model.SurveyMetaModel;
import net.fina.server.survey.model.SurveyMetaModelHelper;
import net.fina.server.survey.model.SurveyResponseMetaModel;
import org.jboss.ejb3.annotation.SecurityDomain;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
@SecurityDomain("FinASecurityDomain")
@PermitAll
public class SurveyProxySession {
    @Inject
    private SurveyLocal surveyLocal;

    public List<String> loadPublicSurvey() {
        return surveyLocal.loadPublicSurvey();
    }

    public List<SurveyResponseMetaModel> loadPrivateSurvey() {
        return surveyLocal.loadPrivateSurvey().entrySet().stream()
                .map(e -> new SurveyResponseMetaModel(
                        e.getValue() != null ? e.getValue().getId() : 0,
                        e.getKey(),
                        e.getValue() != null ? e.getValue().getProgression() : "0"
                ))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getSurvey(String surveyName, boolean isPublic) throws IOException {
        return surveyLocal.getSurvey(surveyName, isPublic);
    }

    @RolesAllowed(PermissionIdNames.SURVEY_REVIEW)
    public PaginatedListWrapper<SurveyMetaModel> loadSurveys(int start, int limit, SortInfo sortInfo) {
        PaginatedListWrapper<SurveyMetaModel> result = new PaginatedListWrapper<>();

        List<SurveyMetaModel> surveysModels = SurveyMetaModelHelper.toModel(surveyLocal.loadSurveys(PagingUtil.getOffsetFromPage(start, limit), limit, sortInfo));
        result.setTotalResults(surveyLocal.getTotal());
        result.setList(surveysModels);

        return result;
    }

    public void submitSurvey(SurveyMetaModel survey) {
        surveyLocal.submitSurvey(SurveyMetaModelHelper.toEntity(survey));
    }

    public void publicSubmitSurvey(SurveyMetaModel survey) {
        surveyLocal.publicSubmitSurvey(SurveyMetaModelHelper.toEntity(survey));
    }

    public String getFiledData(String surveyName) {
        return surveyLocal.getFiledData(surveyName);
    }

    public void saveSurvey(SurveyMetaModel survey) {
        surveyLocal.saveSurvey(SurveyMetaModelHelper.toEntity(survey));
    }

    @RolesAllowed(PermissionIdNames.SURVEY_PUBLIC_UPLOAD)
    public void uploadFilePublic(InputStream inputStream, String fileName) {
        surveyLocal.uploadFile(inputStream, fileName, true);
    }

    @RolesAllowed(PermissionIdNames.SURVEY_PRIVATE_UPLOAD)
    public void uploadFilePrivate(InputStream inputStream, String fileName) {
        surveyLocal.uploadFile(inputStream, fileName, false);
    }
}
