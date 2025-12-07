package net.fina.server.survey.api;

import net.fina.server.dcs.uploadfile.entity.SortInfo;
import net.fina.server.survey.entity.Survey;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface SurveyLocal {
    List<String> loadPublicSurvey();
    String getFiledData(String surveyName);
    Map<String, Survey> loadPrivateSurvey();
    Map<String, Object> getSurvey(String surveyName, boolean isPublic) throws IOException;
    List<Survey> loadSurveys(int start, int limit, SortInfo sortInfo);
    void submitSurvey(Survey survey);
    void publicSubmitSurvey(Survey survey);
    void saveSurvey(Survey survey);
    int getTotal();
    void uploadFile(InputStream inputStream, String fileName, boolean isPublicUpload);
}
