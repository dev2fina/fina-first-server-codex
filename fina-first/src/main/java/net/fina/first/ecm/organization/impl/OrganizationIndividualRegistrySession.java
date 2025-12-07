package net.fina.first.ecm.organization.impl;

import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.parameters.OrderByParam;
import net.fina.ecm.alfresco.api.common.representation.UnexpectedErrorRepresentation;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultNodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultSetRepresentation;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.node.api.NodeLocal;
import net.fina.first.ecm.node.model.NodeMetaModel;
import net.fina.first.ecm.node.model.NodeModelHelper;
import net.fina.first.ecm.organization.api.OrganizationIndividualRegistryLocal;
import net.fina.first.ecm.organization.model.*;
import net.fina.first.ecm.search.api.SearchLocal;
import net.fina.first.interceptors.FirstRecordingAuditor;
import org.apache.http.HttpStatus;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import java.text.MessageFormat;
import java.util.*;

@Stateless
@Local(OrganizationIndividualRegistryLocal.class)
@Interceptors(FirstRecordingAuditor.class)
public class OrganizationIndividualRegistrySession implements OrganizationIndividualRegistryLocal {

    @Inject
    private NodeLocal nodeLocal;
    @Inject
    private SearchLocal searchLocal;

    @Override
    public PaginatedListWrapper<OrganizationIndividualRegistryMetaModel> loadOrganizationIndividualRegistry(
            String acceptLanguage, int page, int start, int limit, String query, String filter) {
        List<OrganizationIndividualRegistryMetaModel> resultModels = new ArrayList<>();

        PaginatedListWrapper<OrganizationIndividualRegistryMetaModel> result = new PaginatedListWrapper<>();
        result.setList(resultModels);
        result.setCurrentPage(page);

        NodeRepresentation organizationIndividualRegistryRootNode = getOrganizationIndividualRegistryRootNode();
        if (organizationIndividualRegistryRootNode != null) {
            if (query != null && !query.trim().isEmpty()) {
                return searchOrganizationAndIndividuals(query, filter, start, limit);
            }

            if (filter != null && !filter.isEmpty()) {
                String filterType = "TYPE:'" + EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_TYPE + "'";
                PaginatedListWrapper<NodeMetaModel> res = searchLocal.searchNodes(
                        acceptLanguage, filterType, start, limit, null, searchLocal.getAFTSFilterString(filter));
                resultModels.addAll(OrganizationIndividualRegistryModelHelper.getModels(res.getList()));
                result.setTotalResults(res.getTotalResults());
                return result;
            }

            PaginatedListWrapper<NodeMetaModel> licenseCertificateRegistryNodes = nodeLocal.getNodeChildren(
                    acceptLanguage,
                    organizationIndividualRegistryRootNode.getId(),
                    start,
                    limit,
                    null,
                    new OrderByParam(Collections.singletonList("createdAt desc")),
                    "(nodeType=" + EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_TYPE + ")",
                    new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)),
                    null,
                    null,
                    null);
            result.setTotalResults(licenseCertificateRegistryNodes.getTotalResults());
            resultModels.addAll(OrganizationIndividualRegistryModelHelper.getModels(licenseCertificateRegistryNodes.getList()));
        }

        return result;
    }

    @Override
    public OrganizationIndividualRegistryMetaModel saveOrganizationIndividualRegistry(OrganizationIndividualRegistryMetaModel model) throws NodeException {
        NodeRepresentation organizationIndividualRegistryRootNode = getOrganizationIndividualRegistryRootNode();
        if (organizationIndividualRegistryRootNode != null) {
            NodeBodyCreate nodeBodyCreate = new NodeBodyCreate(UUID.randomUUID().toString(), EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_TYPE, OrganizationIndividualRegistryModelHelper.convertModelToMap(model), null);
            NodeMetaModel newNode = nodeLocal.createChildNode(organizationIndividualRegistryRootNode.getId(), nodeBodyCreate);
            model.setId(newNode.getId());
            model.setCreatedAt(newNode.getCreatedAt());
            model.setModifiedAt(newNode.getModifiedAt());
        }

        return model;
    }

    @Override
    public OrganizationIndividualRegistryMetaModel updateOrganizationIndividualRegistry(String nodeId, OrganizationIndividualRegistryMetaModel model) throws NodeException {
        NodeBodyUpdate nodeBodyUpdate = new NodeBodyUpdate(OrganizationIndividualRegistryModelHelper.convertModelToMap(model));
        NodeMetaModel updatedNode = nodeLocal.updateNode(nodeId, nodeBodyUpdate);
        model.setModifiedAt(updatedNode.getModifiedAt());
        return model;
    }

    @Override
    public PaginatedListWrapper<OrganizationIndividualLicenseCertificateMetaModel> loadOrganizationIndividualLicenseCertificates(String acceptLanguage, String parentId, int page, int start, int limit, String query) {
        List<OrganizationIndividualLicenseCertificateMetaModel> resultModels = new ArrayList<>();

        PaginatedListWrapper<OrganizationIndividualLicenseCertificateMetaModel> result = new PaginatedListWrapper<>();
        result.setList(resultModels);
        result.setCurrentPage(page);

        PaginatedListWrapper<NodeMetaModel> licenseCertificateRegistryNodes = new PaginatedListWrapper<>();
        if (query != null && !query.trim().isEmpty()) {
            String queryTemplate = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.ORGANIZATION_INDIVIDUALS_LICENSE_QUERY_TEMPLATE);
            queryTemplate = MessageFormat.format(queryTemplate, query);
            String filterType = "TYPE:'" + EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_TYPE + "'";
            ResultSetRepresentation<ResultNodeRepresentation> searchResult = searchLocal.searchAFTS(acceptLanguage, queryTemplate, start, limit, null, filterType);
            licenseCertificateRegistryNodes.setList(NodeModelHelper.getMetaModels(searchResult.getObjects()));
            licenseCertificateRegistryNodes.setTotalResults(searchResult.getPagination().getTotalItems());
        } else {
            licenseCertificateRegistryNodes = nodeLocal.getNodeChildren(acceptLanguage, parentId, start, limit, null, new OrderByParam(Collections.singletonList("createdAt desc")), "(nodeType=" + EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_TYPE + ")", new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), null, null, null);
        }

        List<OrganizationIndividualLicenseCertificateMetaModel> licenseModels = OrganizationIndividualLicenseCertificateModelHelper.getModels(licenseCertificateRegistryNodes.getList());
        licenseModels.forEach(licenseModel -> {
            OrganizationIndividualLicenseCertificateTypeMetaModel licenseType = OrganizationIndividualLicenseCertificateModelHelper.getTypeModel(
                    nodeLocal.getNodeById(licenseModel.getType().getId(), new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)).toString(), null));
            licenseModel.setType(licenseType);
        });

        result.setTotalResults(licenseCertificateRegistryNodes.getTotalResults());
        resultModels.addAll(licenseModels);

        return result;
    }

    @Override
    public OrganizationIndividualLicenseCertificateMetaModel saveOrganizationIndividualLicenseCertificate(String parentId, OrganizationIndividualLicenseCertificateMetaModel model) throws NodeException {
        NodeBodyCreate nodeBodyCreate = new NodeBodyCreate(UUID.randomUUID().toString(), EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_TYPE, OrganizationIndividualLicenseCertificateModelHelper.convertModelToMap(model), null);
        NodeMetaModel newNode = nodeLocal.createChildNode(parentId, nodeBodyCreate);
        model.setId(newNode.getId());
        model.setCreatedAt(newNode.getCreatedAt());
        model.setModifiedAt(newNode.getModifiedAt());

        return model;
    }

    @Override
    public OrganizationIndividualLicenseCertificateMetaModel updateOrganizationIndividualLicenseCertificate(String nodeId, OrganizationIndividualLicenseCertificateMetaModel model) throws NodeException {
        NodeBodyUpdate nodeBodyUpdate = new NodeBodyUpdate(OrganizationIndividualLicenseCertificateModelHelper.convertModelToMap(model));
        NodeMetaModel updatedNode = nodeLocal.updateNode(nodeId, nodeBodyUpdate);
        model.setModifiedAt(updatedNode.getModifiedAt());
        return model;
    }

    @Override
    public void delete(String nodeId) throws NodeException {
        nodeLocal.deleteNodeById(nodeId);
    }

    @Override
    public PaginatedListWrapper<OrganizationIndividualLicenseCertificateTypeMetaModel> loadOrganizationIndividualLicenseCertificateTypes(String acceptLanguage, int page, int start, int limit, String query) {
        if (query != null && !query.trim().isEmpty()) {
            return searchLicenseTypes(acceptLanguage, query, page, start, limit);
        }

        String typesFolderRelativePath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.ORGANIZATION_INDIVIDUAL_LICENSE_TYPES_FOLDER_PATH_KEY);
        PaginatedListWrapper<NodeMetaModel> nodes = nodeLocal.getNodeChildren(acceptLanguage,
                APIConstants.FOLDER_ROOT,
                start,
                limit,
                null,
                new OrderByParam(Collections.singletonList("createdAt desc")),
                null, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)),
                typesFolderRelativePath,
                null,
                null);
        PaginatedListWrapper<OrganizationIndividualLicenseCertificateTypeMetaModel> result = new PaginatedListWrapper<>();
        result.setPageSize(nodes.getPageSize());
        result.setCurrentPage(nodes.getCurrentPage());
        result.setTotalResults(nodes.getTotalResults());
        result.setList(OrganizationIndividualLicenseCertificateModelHelper.getTypeModels(nodes.getList()));

        return result;
    }

    @Override
    public OrganizationIndividualLicenseCertificateTypeMetaModel saveLicenseType(OrganizationIndividualLicenseCertificateTypeMetaModel model) throws NodeException {
        String typesFolderRelativePath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.ORGANIZATION_INDIVIDUAL_LICENSE_TYPES_FOLDER_PATH_KEY);
        TreeMap<String, Object> properties = OrganizationIndividualLicenseCertificateModelHelper.convertModelToMap(model);

        NodeBodyCreate nodeBodyCreate = new NodeBodyCreate(UUID.randomUUID().toString(),
                EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATETYPE_TYPE,
                properties,
                null,
                typesFolderRelativePath);

        NodeMetaModel saved = nodeLocal.createChildNode(APIConstants.FOLDER_ROOT, nodeBodyCreate);

        return OrganizationIndividualLicenseCertificateModelHelper.getTypeModel(saved);
    }

    @Override
    public OrganizationIndividualLicenseCertificateTypeMetaModel updateLicenseType(OrganizationIndividualLicenseCertificateTypeMetaModel model) throws NodeException {
        TreeMap<String, Object> properties = OrganizationIndividualLicenseCertificateModelHelper.convertModelToMap(model);
        NodeBodyUpdate nbu = new NodeBodyUpdate(model.getName(), null, properties, null);

        return OrganizationIndividualLicenseCertificateModelHelper.getTypeModel(nodeLocal.updateNode(model.getId(), nbu));
    }

    @Override
    public void deleteLicense(String nodeId) throws NodeException {
        //check if type has dependency
        String query = "select * from fina:organizationIndividualLicenseCertificate where fina:organizationIndividualLicenseCertificateTypeId='" + nodeId + "'";
        PaginatedListWrapper<ResultNodeRepresentation> licensies = searchLocal.searchByCMIS(query);
        if (!licensies.getList().isEmpty()) {
            throw new NodeException(new UnexpectedErrorRepresentation(HttpStatus.SC_CONFLICT, "License Type Has Dependent Objects"));
        }

        nodeLocal.deleteNode(nodeId, true);
    }

    @Override
    public PaginatedListWrapper<OrganizationIndividualLicenseCertificateTypeMetaModel> searchLicenseTypes(String acceptLanguage, String query, int page, int start, int limit) {
        String queryTpl = "(" + EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATETYPE_PROP_NAME + ":\"*" + query + "*\")";

        PaginatedListWrapper<NodeMetaModel> searchResult = searchLocal.searchNodes(acceptLanguage, queryTpl, start, limit);


        PaginatedListWrapper<OrganizationIndividualLicenseCertificateTypeMetaModel> result = new PaginatedListWrapper<>();
        result.setPageSize(limit);
        result.setTotalResults(searchResult.getTotalResults());
        result.setList(OrganizationIndividualLicenseCertificateModelHelper.getTypeModels(searchResult.getList()));

        return result;
    }

    private NodeRepresentation getOrganizationIndividualRegistryRootNode() {
        String organizationIndividualRegistryRootFolderPath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_ROOT_FOLDER_PATH_KEY);
        return nodeLocal.getNodeById(APIConstants.FOLDER_ROOT, null, organizationIndividualRegistryRootFolderPath, null);
    }


    private PaginatedListWrapper<OrganizationIndividualRegistryMetaModel> searchOrganizationAndIndividuals(String queryString, String filter, int start, int limit) {
        PaginatedListWrapper<OrganizationIndividualRegistryMetaModel> result = new PaginatedListWrapper<>();
        String queryTemplate = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.ORGANIZATION_INDIVIDUALS_QUERY_TEMPLATE);
        if (queryTemplate != null) {
            queryTemplate = MessageFormat.format(queryTemplate, queryString);
            String filterType = "TYPE:'" + EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_TYPE + "'";
            if (filter != null && !filter.isEmpty()) {
                filterType += " and " + searchLocal.getAFTSFilterString(filter);
            }
            ResultSetRepresentation<ResultNodeRepresentation> searchResult = searchLocal.searchAFTS("*", queryTemplate, start, limit, null, filterType);
            result.setList(OrganizationIndividualRegistryModelHelper.getModelsFromRepresentations(searchResult.getObjects()));
            result.setTotalResults(searchResult.getPagination().getTotalItems());
        }


        return result;
    }


}
