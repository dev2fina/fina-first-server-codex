package net.fina.first.ecm.organization.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.organization.api.OrganizationIndividualRegistryLocal;
import net.fina.first.ecm.organization.model.OrganizationIndividualLicenseCertificateMetaModel;
import net.fina.first.ecm.organization.model.OrganizationIndividualLicenseCertificateTypeMetaModel;
import net.fina.first.ecm.organization.model.OrganizationIndividualRegistryMetaModel;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
@SecurityDomain("FinASecurityDomain")
@RolesAllowed({PermissionIdNames.FIRST_ORGANIZATION_INDIVIDUAL_REGISTRY_REVIEW})
public class OrganizationIndividualRegistryProxySession {

    @Inject
    private OrganizationIndividualRegistryLocal organizationIndividualRegistryLocal;

    public PaginatedListWrapper<OrganizationIndividualRegistryMetaModel> loadOrganizationIndividualRegistry(String acceptLanguage,
                                                                                                            int page,
                                                                                                            int start,
                                                                                                            int limit,
                                                                                                            String query,
                                                                                                            String filter) {
        return organizationIndividualRegistryLocal.loadOrganizationIndividualRegistry(acceptLanguage, page, start, limit, query, filter);
    }

    @RolesAllowed(PermissionIdNames.FIRST_ORGANIZATION_INDIVIDUAL_REGISTRY_AMEND)
    public OrganizationIndividualRegistryMetaModel saveOrganizationIndividualRegistry(OrganizationIndividualRegistryMetaModel model) throws NodeException {
        return organizationIndividualRegistryLocal.saveOrganizationIndividualRegistry(model);
    }

    @RolesAllowed(PermissionIdNames.FIRST_ORGANIZATION_INDIVIDUAL_REGISTRY_AMEND)
    public OrganizationIndividualRegistryMetaModel updateOrganizationIndividualRegistry(String nodeId, OrganizationIndividualRegistryMetaModel model) throws NodeException {
        return organizationIndividualRegistryLocal.updateOrganizationIndividualRegistry(nodeId, model);
    }

    public PaginatedListWrapper<OrganizationIndividualLicenseCertificateMetaModel> loadOrganizationIndividualLicenseCertificates(String acceptLanguage, String parentId, int page, int start, int limit,String query) {
        return organizationIndividualRegistryLocal.loadOrganizationIndividualLicenseCertificates(acceptLanguage, parentId, page, start, limit,query);
    }

    @RolesAllowed(PermissionIdNames.FIRST_ORGANIZATION_INDIVIDUAL_REGISTRY_AMEND)
    public OrganizationIndividualLicenseCertificateMetaModel saveOrganizationIndividualLicenseCertificate(String parentId, OrganizationIndividualLicenseCertificateMetaModel model) throws NodeException {
        return organizationIndividualRegistryLocal.saveOrganizationIndividualLicenseCertificate(parentId, model);
    }

    @RolesAllowed(PermissionIdNames.FIRST_ORGANIZATION_INDIVIDUAL_REGISTRY_AMEND)
    public OrganizationIndividualLicenseCertificateMetaModel updateOrganizationIndividualLicenseCertificate(String nodeId, OrganizationIndividualLicenseCertificateMetaModel model) throws NodeException {
        return organizationIndividualRegistryLocal.updateOrganizationIndividualLicenseCertificate(nodeId, model);
    }

    @RolesAllowed(PermissionIdNames.FIRST_ORGANIZATION_INDIVIDUAL_REGISTRY_DELETE)
    public void delete(String nodeId) throws NodeException {
        organizationIndividualRegistryLocal.delete(nodeId);
    }

    public PaginatedListWrapper<OrganizationIndividualLicenseCertificateTypeMetaModel> loadOrganizationIndividualLicenseCertificateTypes(String acceptLanguage, int page, int start, int limit, String query) {
        return organizationIndividualRegistryLocal.loadOrganizationIndividualLicenseCertificateTypes(acceptLanguage, page, start, limit, query);
    }

    @RolesAllowed(PermissionIdNames.FIRST_ORGANIZATION_INDIVIDUAL_REGISTRY_AMEND)
    public OrganizationIndividualLicenseCertificateTypeMetaModel saveLicenseType(OrganizationIndividualLicenseCertificateTypeMetaModel model) throws NodeException {
        return organizationIndividualRegistryLocal.saveLicenseType(model);
    }

    @RolesAllowed(PermissionIdNames.FIRST_ORGANIZATION_INDIVIDUAL_REGISTRY_AMEND)
    public OrganizationIndividualLicenseCertificateTypeMetaModel updateLicenseType(OrganizationIndividualLicenseCertificateTypeMetaModel model) throws NodeException {
        return organizationIndividualRegistryLocal.updateLicenseType(model);
    }

    @RolesAllowed(PermissionIdNames.FIRST_ORGANIZATION_INDIVIDUAL_REGISTRY_DELETE)
    public void deleteLicense(String nodeId) throws NodeException {
        organizationIndividualRegistryLocal.deleteLicense(nodeId);
    }


    public PaginatedListWrapper<OrganizationIndividualLicenseCertificateTypeMetaModel> searchLicenseTypes(String acceptLanguage, String query, int page, int start, int limit) {
        return organizationIndividualRegistryLocal.searchLicenseTypes(acceptLanguage, query, page, start, limit);
    }
}
