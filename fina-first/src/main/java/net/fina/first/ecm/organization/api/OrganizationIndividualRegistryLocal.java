package net.fina.first.ecm.organization.api;

import net.fina.common.shared.PaginatedListWrapper;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.organization.model.OrganizationIndividualLicenseCertificateMetaModel;
import net.fina.first.ecm.organization.model.OrganizationIndividualLicenseCertificateTypeMetaModel;
import net.fina.first.ecm.organization.model.OrganizationIndividualRegistryMetaModel;

public interface OrganizationIndividualRegistryLocal {
    PaginatedListWrapper<OrganizationIndividualRegistryMetaModel> loadOrganizationIndividualRegistry(String acceptLanguage, int page, int start, int limit, String query, String filter);

    OrganizationIndividualRegistryMetaModel saveOrganizationIndividualRegistry(OrganizationIndividualRegistryMetaModel model) throws NodeException;

    OrganizationIndividualRegistryMetaModel updateOrganizationIndividualRegistry(String nodeId, OrganizationIndividualRegistryMetaModel model) throws NodeException;

    PaginatedListWrapper<OrganizationIndividualLicenseCertificateMetaModel> loadOrganizationIndividualLicenseCertificates(String acceptLanguage, String parentId, int page, int start, int limit,String query);

    OrganizationIndividualLicenseCertificateMetaModel saveOrganizationIndividualLicenseCertificate(String parentId, OrganizationIndividualLicenseCertificateMetaModel model) throws NodeException;

    OrganizationIndividualLicenseCertificateMetaModel updateOrganizationIndividualLicenseCertificate(String nodeId, OrganizationIndividualLicenseCertificateMetaModel model) throws NodeException;

    void delete(String nodeId) throws NodeException;

    PaginatedListWrapper<OrganizationIndividualLicenseCertificateTypeMetaModel> loadOrganizationIndividualLicenseCertificateTypes(String acceptLanguage, int page, int start, int limit, String query);

    OrganizationIndividualLicenseCertificateTypeMetaModel saveLicenseType(OrganizationIndividualLicenseCertificateTypeMetaModel model) throws NodeException;

    OrganizationIndividualLicenseCertificateTypeMetaModel updateLicenseType(OrganizationIndividualLicenseCertificateTypeMetaModel model) throws NodeException;

    void deleteLicense(String nodeId) throws NodeException;

    PaginatedListWrapper<OrganizationIndividualLicenseCertificateTypeMetaModel> searchLicenseTypes(String acceptLanguage, String query, int page, int start, int limit);
}
