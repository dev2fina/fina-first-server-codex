package net.fina.first.ecm.people.model;

import net.fina.common.shared.ecm.model.ECMCapabilitiesMetaModel;
import net.fina.common.shared.ecm.model.ECMCompanyMetaModel;
import net.fina.common.shared.ecm.model.ECMPersonMetaModel;
import net.fina.ecm.alfresco.api.core.model.representation.CapabilitiesRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.CompanyRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.PersonRepresentation;

import java.util.ArrayList;
import java.util.List;

public class ECMPersonModelHelper {

    public static ECMPersonMetaModel getPersonMetaModel(PersonRepresentation personRepresentation) {
        ECMPersonMetaModel metaModel = new ECMPersonMetaModel();
        metaModel.setAspectNames(personRepresentation.getAspectNames());
        metaModel.setAvatarId(personRepresentation.getAvatarId());
        metaModel.setCompany(getCompanyModel(personRepresentation.getCompany()));
        metaModel.setDescription(personRepresentation.getDescription());
        metaModel.setEmail(personRepresentation.getEmail());
        metaModel.setEmailNotificationsEnabled(personRepresentation.isEmailNotificationsEnabled());
        metaModel.setEnabled(personRepresentation.isEnabled());
        metaModel.setFirstName(personRepresentation.getFirstName());
        metaModel.setGoogleId(personRepresentation.getGoogleId());
        metaModel.setId(personRepresentation.getId());
        metaModel.setInstantMessageId(personRepresentation.getInstantMessageId());
        metaModel.setJobTitle(personRepresentation.getJobTitle());
        metaModel.setLastName(personRepresentation.getLastName());
        metaModel.setLocation(personRepresentation.getLocation());
        metaModel.setMobile(personRepresentation.getMobile());
        metaModel.setProperties(personRepresentation.getProperties());
        metaModel.setSkypeId(personRepresentation.getSkypeId());
        metaModel.setStatusUpdatedAt(personRepresentation.getStatusUpdatedAt());
        metaModel.setTelephone(personRepresentation.getTelephone());
        metaModel.setUserStatus(personRepresentation.getUserStatus());
        metaModel.setCapabilities(getCapabilitiesModel(personRepresentation.getCapabilities()));

        return metaModel;
    }

    public static ECMCompanyMetaModel getCompanyModel(CompanyRepresentation representation) {
        if (representation != null) {
            ECMCompanyMetaModel model = new ECMCompanyMetaModel();
            model.setAddress1(representation.getAddress1());
            model.setAddress2(representation.getAddress2());
            model.setAddress3(representation.getAddress3());
            model.setEmail(representation.getEmail());
            model.setFax(representation.getFax());
            model.setOrganization(representation.getOrganization());
            model.setPostcode(representation.getPostcode());
            model.setTelephone(representation.getTelephone());

            return model;
        }

        return null;
    }

    public static List<ECMPersonMetaModel> getPersonMetaModels(List<PersonRepresentation> persons) {
        List<ECMPersonMetaModel> result = new ArrayList<>();

        if (persons != null) {
            persons.forEach(p -> result.add(getPersonMetaModel(p)));
        }

        return result;
    }

    private static ECMCapabilitiesMetaModel getCapabilitiesModel(CapabilitiesRepresentation capabilitiesRepresentation) {
        if (capabilitiesRepresentation != null) {
            return new ECMCapabilitiesMetaModel(capabilitiesRepresentation.isGuest(), capabilitiesRepresentation.isAdmin(), capabilitiesRepresentation.isMutable());
        }
        return null;
    }

}
