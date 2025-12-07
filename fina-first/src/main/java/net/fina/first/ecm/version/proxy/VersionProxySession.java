package net.fina.first.ecm.version.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.first.ecm.version.api.VersionLocal;
import net.fina.first.ecm.version.model.VersionMetaModel;
import net.fina.first.ecm.version.model.VersionRevertBodyMetaModel;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Stateless
@RolesAllowed({PermissionIdNames.FIRST_REVIEW})
@SecurityDomain("FinASecurityDomain")
public class VersionProxySession {

    @Inject
    private VersionLocal versionLocal;

    public PaginatedListWrapper<VersionMetaModel> getNodeVersions(String acceptLanguage, String nodeId) {
        return versionLocal.getNodeVersions(acceptLanguage, nodeId);
    }

    public VersionMetaModel revertToVersion(String acceptLanguage, String nodeId, String versionId, VersionRevertBodyMetaModel revertBodyMetaModel) {
        return versionLocal.revertToVersion(acceptLanguage, nodeId, versionId, revertBodyMetaModel);
    }

    public VersionMetaModel getVersionBeforePropertyValueChange(List<VersionMetaModel> versionMetaModels, String propertyKey, String propertyValue) {
        return versionLocal.getVersionBeforePropertyValueChange(versionMetaModels, propertyKey, propertyValue);
    }

    public VersionMetaModel getNodeVersion(String acceptLanguage, String nodeId, String versionId) {
        return versionLocal.getNodeVersion(acceptLanguage, nodeId, versionId);
    }

    public Response getNodeVersionContent(String nodeId, String versionId, Boolean attachment) throws UnsupportedEncodingException {
        return versionLocal.getNodeVersionContent(nodeId, versionId, attachment);
    }

    public NodeRepresentation purgeNodeVersion(String nodeId, String versionId) {
        return versionLocal.purgeNodeVersion(nodeId, versionId);
    }
}
