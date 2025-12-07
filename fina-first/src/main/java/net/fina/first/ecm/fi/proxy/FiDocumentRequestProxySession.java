package net.fina.first.ecm.fi.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.fi.api.FiDocumentRequestLocal;
import net.fina.first.ecm.fi.model.FiDocumentRequestMetaModel;
import net.fina.first.ecm.node.model.NodeMetaModel;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;

@Stateless
@RolesAllowed({PermissionIdNames.FIRST_FI_DOCUMENT_REQUEST_REVIEW})
@SecurityDomain("FinASecurityDomain")
public class FiDocumentRequestProxySession {

    @Inject
    private FiDocumentRequestLocal fiDocumentRequestLocal;

    public PaginatedListWrapper<FiDocumentRequestMetaModel> loadFiDocumentRequests(String acceptLanguage, int page, int start, int limit) throws NodeException {
        return fiDocumentRequestLocal.loadFiDocumentRequests(acceptLanguage, page, start, limit);
    }

    @RolesAllowed(PermissionIdNames.FIRST_FI_DOCUMENT_REQUEST_AMEND)
    public FiDocumentRequestMetaModel createFiDocumentRequest(FiDocumentRequestMetaModel fiDocumentRequestMetaModel) throws NodeException {
        return fiDocumentRequestLocal.createFiDocumentRequest(fiDocumentRequestMetaModel);
    }

    @RolesAllowed(PermissionIdNames.FIRST_FI_DOCUMENT_REQUEST_AMEND)
    public List<FiDocumentRequestMetaModel> createFiDocumentRequest(MultipartFormDataInput multipartForm) throws NodeException {
        return fiDocumentRequestLocal.createFiDocumentRequest(multipartForm);
    }

    @RolesAllowed(PermissionIdNames.FIRST_FI_DOCUMENT_REQUEST_AMEND)
    public FiDocumentRequestMetaModel updateFiDocumentRequest(String fiDocumentRequestNodeId, FiDocumentRequestMetaModel fiDocumentRequestMetaModel) throws NodeException {
        return fiDocumentRequestLocal.updateFiDocumentRequest(fiDocumentRequestNodeId, fiDocumentRequestMetaModel);
    }

    @RolesAllowed(PermissionIdNames.FIRST_FI_DOCUMENT_REQUEST_DELETE)
    public void deleteFiDocumentRequest(String fiDocumentRequestNodeId) throws NodeException{
        fiDocumentRequestLocal.deleteFiDocumentRequest(fiDocumentRequestNodeId);
    }

    public List<NodeMetaModel> loadFiDocumentRequestDocuments(String acceptLanguage, String fiDocumentRequestNodeId) {
        return fiDocumentRequestLocal.loadFiDocumentRequestDocuments(acceptLanguage, fiDocumentRequestNodeId);
    }

    public PaginatedListWrapper<NodeMetaModel> loadFiDocumentRequestObjects(String acceptLanguage, String fiDocumentRequestNodeId) {
        return fiDocumentRequestLocal.loadFiDocumentRequestObjects(acceptLanguage, fiDocumentRequestNodeId);
    }
}
