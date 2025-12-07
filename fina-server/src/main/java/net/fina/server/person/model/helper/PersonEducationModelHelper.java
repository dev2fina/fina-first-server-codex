package net.fina.server.person.model.helper;

import net.fina.server.i18n.helper.Description;
import net.fina.server.person.entity.PersonEducation;
import net.fina.server.person.model.PersonEducationMetaModel;

import java.util.ArrayList;
import java.util.List;

public class PersonEducationModelHelper {

    public static PersonEducationMetaModel toModel(PersonEducation entity, long langId) {
        PersonEducationMetaModel model = new PersonEducationMetaModel();
        model.setId(entity.getId());
        model.setCertificates(entity.isCertificates());
        model.setEducationLevel(entity.getEducationLevel());
        model.setAcademicDegreeLevel(entity.getAcademicDegreeLevel());
        model.setCompletionDate(entity.getCompletionDate());
        model.setCompleteCourseName(entity.getCompleteCourseName().getDescription(langId));
        model.setCompleteCourseNameStrId(entity.getCompleteCourseName().getNameStrId());
        model.setInstituteName(entity.getInstituteName().getDescription(langId));
        model.setInstituteNameStrId(entity.getInstituteName().getNameStrId());
        model.setSeminarOrganizer(entity.getSeminarOrganizer().getDescription(langId));
        model.setSeminarOrganizerStrId(entity.getId());
        model.setSpeciality(entity.getSpeciality().getDescription(langId));
        model.setSpecialityStrId(entity.getSpeciality().getNameStrId());
        model.setSupportDocuments(entity.getSupportDocuments().getDescription(langId));
        model.setSupportDocumentsStrId(entity.getSupportDocuments().getNameStrId());
        model.setTrainingPlace(entity.getTrainingPlace().getDescription(langId));
        model.setTrainingPlaceStrId(entity.getTrainingPlace().getNameStrId());

        return model;
    }

    public static PersonEducation toEntity(PersonEducationMetaModel model, long langId) {
        PersonEducation entity = new PersonEducation();
        entity.setId(model.getId());
        entity.setCertificates(model.isCertificates());
        entity.setEducationLevel(model.getEducationLevel());
        entity.setAcademicDegreeLevel(model.getAcademicDegreeLevel());
        entity.setCompletionDate(model.getCompletionDate());
        entity.setCompleteCourseName(new Description(langId, model.getCompleteCourseNameStrId(), model.getCompleteCourseName()));
        entity.setInstituteName(new Description(langId, model.getInstituteNameStrId(), model.getInstituteName()));
        entity.setSeminarOrganizer(new Description(langId, model.getSeminarOrganizerStrId(), model.getSeminarOrganizer()));
        entity.setSpeciality(new Description(langId, model.getSpecialityStrId(), model.getSpeciality()));
        entity.setSupportDocuments(new Description(langId, model.getSupportDocumentsStrId(), model.getSupportDocuments()));
        entity.setTrainingPlace(new Description(langId, model.getTrainingPlaceStrId(), model.getTrainingPlace()));

        return entity;
    }


    public static List<PersonEducation> toEntities(List<PersonEducationMetaModel> models, long langId) {
        List<PersonEducation> result = new ArrayList<>();
        models.forEach(m -> result.add(toEntity(m, langId)));
        return result;
    }

    public static List<PersonEducationMetaModel> toModels(List<PersonEducation> entities, long langId) {
        List<PersonEducationMetaModel> result = new ArrayList<>();
        entities.forEach(e -> result.add(toModel(e, langId)));
        return result;
    }


}
