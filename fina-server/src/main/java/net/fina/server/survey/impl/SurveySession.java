package net.fina.server.survey.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.server.dcs.uploadfile.entity.SortInfo;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.User;
import net.fina.server.survey.api.SurveyLocal;
import net.fina.server.survey.entity.Survey;
import org.apache.commons.io.FileUtils;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;


@Stateless
@Local(SurveyLocal.class)
@Interceptors(RecordingAuditor.class)
public class SurveySession implements SurveyLocal {
    private final Logger log = Logger.getLogger(SurveySession.class.getName());
    @Inject
    private EntityManager em;
    @Inject
    private UserLocal userLocal;

    @Override
    public List<String> loadPublicSurvey() {
        return loadFileNames(ConfigurationUtil.get().get("PUBLIC_SURVEYS_LOCATION"));
    }

    @Override
    public String getFiledData(String surveyName) {
        User u = userLocal.getCurrentUser();
        String result = null;

        try {
            Survey survey = em.createQuery("select s from IN_SURVEY s where s.user.id=:userId and s.name=:surveyName", Survey.class)
                    .setParameter("userId", u.getId())
                    .setParameter("surveyName", surveyName)
                    .getSingleResult();
            result = survey.getSurvey();
        } catch (NoResultException ex) {
            log.error(ex.getMessage(), ex);
        }

        return result;
    }

    @Override
    public Map<String, Survey> loadPrivateSurvey() {
        User u = userLocal.getCurrentUser();
        List<String> surveys = loadFileNames(ConfigurationUtil.get().get("PRIVATE_SURVEYS_LOCATION"));
        Map<String, Survey> res = new HashMap<>();

        if (u != null) {
            List<Survey> filled = em.createQuery("select s from IN_SURVEY s where s.user.id=:userId", Survey.class)
                    .setParameter("userId", u.getId())
                    .getResultList();

            List<String> completedNames = filled.stream()
                    .filter(Survey::isCompleted)
                    .map(Survey::getName)
                    .collect(Collectors.toList());


            for (String name : surveys) {
                if (!completedNames.contains(name)) {
                    res.put(name, null);
                }
            }

            for (Survey sur : filled) {
                if (!sur.isCompleted()) {
                    res.put(sur.getName(), sur);
                }
            }
        }

        return res;
    }

    private List<String> loadFileNames(String location) {
        List<String> result = new ArrayList<>();

        File folder = new File(location);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < Objects.requireNonNull(listOfFiles).length; i++) {
            if (listOfFiles[i].isFile()) {
                result.add(listOfFiles[i].getName().replaceFirst("[.][^.]+$", ""));
            }
        }

        return result;
    }

    @Override
    public Map<String, Object> getSurvey(String surveyName, boolean isPublic) throws IOException {
        String surveyPath = ConfigurationUtil.get().get(isPublic ? "PUBLIC_SURVEYS_LOCATION" : "PRIVATE_SURVEYS_LOCATION") + surveyName + ".json";
        return new ObjectMapper().readValue(new File(surveyPath), new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public List<Survey> loadSurveys(int start, int limit, SortInfo sortInfo) {
        String baseQuery = "select s from IN_SURVEY s where s.isCompleted = true";
        String orderClause = "";

        if (sortInfo != null && sortInfo.getSortField() != null) {
            String sortField = switch (sortInfo.getSortField()) {
                case "userName" -> "s.user.description";
                case "name" -> "s.name";
                default -> null;
            };

            if (sortField != null) {
                String direction = (sortInfo.getSortDir() == null || sortInfo.getSortDir().isBlank())
                        ? "desc"
                        : sortInfo.getSortDir().toLowerCase();
                orderClause = " order by " + sortField + " " + direction;
            }
        }

        TypedQuery<Survey> query = em.createQuery(baseQuery + orderClause, Survey.class);

        if (start >= 0 && limit > 0) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }


    @Override
    public void submitSurvey(Survey survey) {
        survey.setUser(userLocal.getCurrentUser());
        survey.setCompleted(true);
        if (survey.getId() > 0) {
            em.merge(survey);
        } else {
            em.persist(survey);
        }
    }

    @Override
    public void publicSubmitSurvey(Survey survey) {
        survey.setCompleted(true);
        if (survey.getId() > 0) {
            em.merge(survey);
        } else {
            em.persist(survey);
        }
    }

    @Override
    public void saveSurvey(Survey survey) {
        survey.setUser(userLocal.getCurrentUser());
        survey.setCompleted(false);
        if (survey.getId() > 0) {
            em.merge(survey);
        } else {
            em.persist(survey);
        }
    }

    @Override
    public int getTotal() {
        return em.createQuery("select count(s) from IN_SURVEY s where s.isCompleted=true", Long.class)
                .getSingleResult()
                .intValue();
    }

    @Override
    public void uploadFile(InputStream inputStream, String fileName, boolean isPublicUpload) {
        try {
            FileUtils.copyInputStreamToFile(inputStream, new File(ConfigurationUtil.get().get(isPublicUpload ? "PUBLIC_SURVEYS_LOCATION" : "PRIVATE_SURVEYS_LOCATION") + fileName));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
