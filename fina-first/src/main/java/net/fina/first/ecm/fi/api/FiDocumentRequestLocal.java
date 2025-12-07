package net.fina.first.ecm.fi.api;

import net.fina.common.shared.PaginatedListWrapper;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.fi.model.FiDocumentRequestMetaModel;
import net.fina.first.ecm.node.model.NodeMetaModel;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.util.List;

public interface FiDocumentRequestLocal {

    PaginatedListWrapper<FiDocumentRequestMetaModel> loadFiDocumentRequests(String acceptLanguage, int page, int start, int limit) throws NodeException;

    FiDocumentRequestMetaModel createFiDocumentRequest(FiDocumentRequestMetaModel fiDocumentRequestMetaModel) throws NodeException;

    List<FiDocumentRequestMetaModel> createFiDocumentRequest(MultipartFormDataInput multipartForm) throws NodeException;

    FiDocumentRequestMetaModel updateFiDocumentRequest(String fiDocumentRequestNodeId, FiDocumentRequestMetaModel fiDocumentRequestMetaModel) throws NodeException;

    void deleteFiDocumentRequest(String fiDocumentRequestNodeId) throws NodeException;

    List<NodeMetaModel> loadFiDocumentRequestDocuments(String acceptLanguage, String fiDocumentRequestNodeId);

    PaginatedListWrapper<FiDocumentRequestMetaModel> loadFiDocumentRequestsByCode(String acceptLanguage, String fiRegistryNodeId, int page, int start, int limit);

    PaginatedListWrapper<NodeMetaModel> loadFiDocumentRequestObjects(String acceptLanguage, String fiRegistryNodeId);
}
