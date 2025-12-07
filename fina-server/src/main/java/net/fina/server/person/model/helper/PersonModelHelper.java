package net.fina.server.person.model.helper;

import net.fina.server.fi.entity.FiPersonConnection;
import net.fina.server.fi.model.RegionMetaModelHelper;
import net.fina.server.i18n.helper.Description;
import net.fina.server.person.entity.Person;
import net.fina.server.person.model.PersonMetaModel;
import net.fina.server.person.model.PersonResidentStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PersonModelHelper {
    public static PersonMetaModel toModel(Person entity, long langId) {
        PersonMetaModel model = new PersonMetaModel();
        model.setId(entity.getId());
        model.setIdentificationNumber(entity.getIdentificationNumber());
        model.setPassportNumber(entity.getPassportNumber());
        model.setName(entity.getName().getDescription(langId));
        model.setNameStrId(entity.getName().getNameStrId());
        model.setResidentStatus(entity.getResidentStatus());
        model.setStatus(entity.getStatus());
        if (entity.getCitizenship() != null) {
            model.setCitizenship(RegionMetaModelHelper.toModel(entity.getCitizenship(), langId));
        }
        model.setEducation(PersonEducationModelHelper.toModels(entity.getEducation(), langId));
        model.setPositions(PersonPositionModelHelper.toModels(entity.getPositions(), langId));
        model.setRecommendations(RecommendationModelHelper.toModels(entity.getRecommendations(), langId));
        model.setEducation(PersonEducationModelHelper.toModels(entity.getEducation(), langId));
        model.setCriminalRecords(CriminalRecordModelHelper.toModels(entity.getCriminalRecords(), langId));
        model.setFiPersonId(entity.getFiPersonId());
        model.setConnectionTypes(entity.getFiPersonConnections().stream().map(FiPersonConnection::getConnectionType).collect(Collectors.toSet()));
        return model;
    }

    public static PersonMetaModel toModelSimple(Person entity, long langId) {
        PersonMetaModel model = new PersonMetaModel();
        model.setId(entity.getId());
        model.setIdentificationNumber(entity.getIdentificationNumber());
        model.setPassportNumber(entity.getPassportNumber());
        model.setName(entity.getName().getDescription(langId));
        model.setNameStrId(entity.getName().getNameStrId());
        model.setResidentStatus(entity.getResidentStatus());

        return model;
    }

    public static Person toEntity(PersonMetaModel model, long langId) {
        Person entity = new Person();
        entity.setId(model.getId());
        entity.setIdentificationNumber(model.getIdentificationNumber());
        entity.setPassportNumber(model.getPassportNumber());
        entity.setName(new Description(langId, model.getNameStrId(), model.getName()));
        entity.setResidentStatus(model.getResidentStatus() != null ? model.getResidentStatus() : PersonResidentStatus.PHYSICAL_PERSONS_RESIDENT);
        entity.setStatus(model.getStatus());
        if (model.getCitizenship() != null) {
            entity.setCitizenship(RegionMetaModelHelper.toEntity(model.getCitizenship()));
        }
        entity.setEducation(PersonEducationModelHelper.toEntities(model.getEducation(), langId));
        entity.setPositions(PersonPositionModelHelper.toEntities(model.getPositions(), langId));
        entity.setRecommendations(RecommendationModelHelper.toEntities(model.getRecommendations(), langId));
        entity.setEducation(PersonEducationModelHelper.toEntities(model.getEducation(), langId));
        entity.setCriminalRecords(CriminalRecordModelHelper.toEntities(model.getCriminalRecords(), langId));
        return entity;
    }

    public static List<Person> toEntities(List<PersonMetaModel> models, long langId) {
        List<Person> result = new ArrayList<>();
        models.forEach(m -> result.add(toEntity(m, langId)));
        return result;
    }

    public static List<PersonMetaModel> toModels(List<Person> entities, long langId) {
        List<PersonMetaModel> result = new ArrayList<>();
        entities.forEach(e -> result.add(toModel(e, langId)));
        return result;
    }

}
