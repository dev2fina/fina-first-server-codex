package net.fina.first.ecm.fi.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.fi.api.AttestationLocal;
import net.fina.first.ecm.fi.model.AttestationDocumentType;
import net.fina.first.ecm.node.model.NodeMetaModel;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.Map;

@Stateless
@SecurityDomain("FinASecurityDomain")
@RolesAllowed({PermissionIdNames.FIRST_ATTESTATION_REVIEW})
public class AttestationProxySession {

    @Inject
    private AttestationLocal attestationLocal;

    public PaginatedListWrapper<NodeMetaModel> load(String acceptLanguage, String query, String filter, String sort, String group, int page, int start, int limit) {
        return attestationLocal.load(acceptLanguage, query, filter, sort, group, page, start, limit);
    }

    public NodeMetaModel generateDocument(String acceptLanguage, AttestationDocumentType documentType, Map<String, Object> filter) throws NodeException {
        return attestationLocal.generateDocument(acceptLanguage, documentType, filter);
    }

    public NodeMetaModel generateDocument(String acceptLanguage, String nodeId, AttestationDocumentType documentType) throws NodeException {
        return attestationLocal.generateDocument(acceptLanguage, nodeId, documentType);
    }

}
