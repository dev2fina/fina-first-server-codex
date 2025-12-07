package net.fina.server.person.model.helper;

import net.fina.server.i18n.helper.Description;
import net.fina.server.legalperson.entity.LegalPerson;
import net.fina.server.legalperson.model.LegalPersonMetaModel;
import net.fina.server.legalperson.model.helper.LegalPersonModelHelper;
import net.fina.server.person.entity.Person;
import net.fina.server.person.entity.PersonPosition;
import net.fina.server.person.model.PersonMetaModel;
import net.fina.server.person.model.PersonPositionMetaModel;

import java.util.ArrayList;
import java.util.List;

public class PersonPositionModelHelper {

    public static PersonPositionMetaModel toModel(PersonPosition entity, long langId) {
        PersonPositionMetaModel model = new PersonPositionMetaModel();
        model.setId(entity.getId());
        model.setPosition(entity.getPosition().getDescription(langId));
        model.setPositionStrId(entity.getPosition().getNameStrId());
        if (entity.getCompany() != null) {
            LegalPersonMetaModel company = new LegalPersonMetaModel();
            company.setId(entity.getCompany().getId());
            company.setBank(entity.getCompany().getFiId()>0);
            company.setFiId(entity.getCompany().getFiId());
            company.setName(entity.getCompany().getName().getDescription(langId));
            company.setNameStrId(entity.getCompany().getName().getNameStrId());
            company.setIdentificationNumber(entity.getCompany().getIdentificationNumber());
            model.setCompany(company);
        }
        if (entity.getPerson() != null) {
            PersonMetaModel personMetaModel = new PersonMetaModel();
            personMetaModel.setId(entity.getPerson().getId());
            personMetaModel.setName(entity.getPerson().getName().getDescription(langId));
            personMetaModel.setIdentificationNumber(entity.getPerson().getIdentificationNumber());
            model.setPerson(personMetaModel);
        }
        model.setElectionDate(entity.getElectionDate());

        return model;
    }

    public static PersonPosition toEntity(PersonPositionMetaModel model, long langId) {
        PersonPosition entity = new PersonPosition();
        entity.setId(model.getId());
        entity.setPosition(new Description(langId, model.getPositionStrId(), model.getPosition()));
        if (model.getCompany() != null) {
            LegalPerson company = new LegalPerson();
            company.setId(model.getCompany().getId());
            company.setFiId(model.getCompany().getFiId());
            company.setName(new Description(langId, model.getCompany().getNameStrId(), model.getCompany().getName()));
            entity.setCompany(company);
        }
        if (model.getPerson() != null) {
            Person person = new Person();
            person.setId(model.getPerson().getId());
            person.setIdentificationNumber(model.getPerson().getIdentificationNumber());
            person.setName(new Description(langId, model.getPerson().getNameStrId(), model.getPerson().getName()));
            entity.setPerson(person);
        }
        entity.setElectionDate(model.getElectionDate());
        return entity;
    }

    public static List<PersonPosition> toEntities(List<PersonPositionMetaModel> models, long langId) {
        List<PersonPosition> result = new ArrayList<>();

        models.forEach(m -> result.add(toEntity(m, langId)));

        return result;
    }

    public static List<PersonPositionMetaModel> toModels(List<PersonPosition> persons, long langId) {
        List<PersonPositionMetaModel> result = new ArrayList<>();
        persons.forEach(p -> result.add(toModel(p, langId)));
        return result;
    }
}
