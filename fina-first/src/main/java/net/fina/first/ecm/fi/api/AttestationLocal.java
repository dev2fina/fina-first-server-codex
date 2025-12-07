package net.fina.first.ecm.fi.api;

import net.fina.common.shared.PaginatedListWrapper;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.fi.model.AttestationDocumentType;
import net.fina.first.ecm.node.model.NodeMetaModel;

import java.util.Map;

public interface AttestationLocal {

    PaginatedListWrapper<NodeMetaModel> load(String acceptLanguage, String query, String filter,
                                             String sort, String group, int page, int start, int limit);

    NodeMetaModel generateDocument(String acceptLanguage, AttestationDocumentType documentType, Map<String, Object> filter)
            throws NodeException;

    NodeMetaModel generateDocument(String acceptLanguage, String nodeId, AttestationDocumentType documentType)
            throws NodeException;
}
