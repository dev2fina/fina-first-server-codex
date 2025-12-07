package net.fina.server.fi.model;

import net.fina.server.fi.entity.FiManagement;
import net.fina.server.fi.entity.FiPerson;
import net.fina.server.i18n.helper.Description;
import net.fina.server.person.model.helper.PersonModelHelper;

public class FiManagementMetaModelHelper {
    public static FiManagement toEntity(FiManagementMetaModel model, FiPerson fiPerson, long langId) {
        FiManagement entity = new FiManagement();
        entity.setId(model.getId());
        entity.setAppointmentDate(model.getAppointmentDate());
        entity.setCancelDate(model.getCancelDate());
        entity.setPhone(model.getPhone());
        entity.setFiId(model.getFiId());
        entity.setVersion(model.getVersion());

        entity.setDescription(new Description(langId, model.getFirstNameStrId(), model.getFirstName()));
        entity.setLastDescription(new Description(langId, model.getLastNameStrId(), model.getLastName()));
        entity.setPost(new Description(langId, model.getPostStrId(), model.getPostString()));
        entity.setResident(model.isResident());
        entity.setRegistrationId1(new Description(langId, model.getRegistrationId1StrId(), model.getRegistrationId1String()));
        entity.setRegistrationId2(new Description(langId, model.getRegistrationId2StrId(), model.getRegistrationId2String()));
        entity.setRegistrationId3(new Description(langId, model.getRegistrationId3StrId(), model.getRegistrationId3String()));
        entity.setCommectId1(new Description(langId, model.getCommentId1StrId(), model.getCommentId1String()));
        entity.setCommectId2(new Description(langId, model.getCommentId2StrId(), model.getCommentId2String()));
        entity.setDisable(model.isDisable());

        entity.setAddress(model.getAddress());
        entity.setMail(model.getMail());
        entity.setDependencyStatus(model.isDependencyStatus());
        entity.setFiPerson(fiPerson);
        entity.setFiManagementCommitteeList(FiManagementCommitteeModelHelper.toEntities(model.getCommitteeList(), langId));
        entity.setPosition(model.getPosition());
        entity.setNumberOfReportingEmployees(model.getNumberOfReportingEmployees());
        entity.setPortfolio(model.getPortfolio());
        entity.setDateOfApproval(model.getDateOfApproval());

        return entity;
    }

    public static FiManagementMetaModel toModel(FiManagement entity, long langId) {
        FiManagementMetaModel model = new FiManagementMetaModel();
        model.setId(entity.getId());
        model.setAppointmentDate(entity.getAppointmentDate());
        model.setCancelDate(entity.getCancelDate());
        model.setPhone(entity.getPhone());
        model.setFiId(entity.getFiId());
        if (entity.getVersion() != null) {
            model.setVersion(entity.getVersion());
        }
        model.setResident(entity.isResident());
        model.setDisable(entity.isDisable());

        model.setRegistrationId1StrId(entity.getRegistrationId1().getNameStrId());
        model.setRegistrationId2StrId(entity.getRegistrationId2().getNameStrId());
        model.setRegistrationId3StrId(entity.getRegistrationId1().getNameStrId());
        model.setCommentId1StrId(entity.getCommectId1().getNameStrId());
        model.setCommentId2StrId(entity.getCommectId2().getNameStrId());
        model.setPostStrId(entity.getPost().getNameStrId());
        model.setFirstNameStrId(entity.getDescription().getNameStrId());
        model.setLastNameStrId(entity.getLastDescription().getNameStrId());

        model.setCommentId1String(entity.getCommectId1().getDescription(langId));
        model.setCommentId2String(entity.getCommectId2().getDescription(langId));
        model.setRegistrationId1String(entity.getRegistrationId1().getDescription(langId));
        model.setRegistrationId2String(entity.getRegistrationId2().getDescription(langId));
        model.setRegistrationId3String(entity.getRegistrationId1().getDescription(langId));
        model.setPostString(entity.getPost().getDescription(langId));
        model.setFirstName(entity.getDescription().getDescription(langId));
        model.setLastName(entity.getLastDescription().getDescription(langId));

        model.setAddress(entity.getAddress());
        model.setMail(entity.getMail());
        model.setDependencyStatus(entity.isDependencyStatus());
        if (entity.getFiPerson() != null) {
            model.setPerson(PersonModelHelper.toModel(entity.getFiPerson().getPerson(), langId));
            model.setFiPersonId(entity.getFiPerson().getId());
        }

        model.setCommitteeList(FiManagementCommitteeModelHelper.toModels(entity.getFiManagementCommitteeList(), langId));
        model.setPosition(entity.getPosition());
        model.setNumberOfReportingEmployees(entity.getNumberOfReportingEmployees());
        model.setPortfolio(entity.getPortfolio());
        model.setDateOfApproval(entity.getDateOfApproval());

        if (entity.getManagement() != null) {
            ManagementMetaModel managementMetaModel = new ManagementMetaModel();
            managementMetaModel.setId(entity.getManagement().getId());
            managementMetaModel.setCode(entity.getManagement().getCode());
            managementMetaModel.setName(entity.getManagement().getDescription().getDescription(langId));
            model.setManagementModel(managementMetaModel);
        }
        return model;
    }
}
