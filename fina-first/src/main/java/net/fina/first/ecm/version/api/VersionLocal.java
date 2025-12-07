package net.fina.first.ecm.version.api;

import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.VersionRepresentation;
import net.fina.first.ecm.version.model.VersionMetaModel;
import net.fina.first.ecm.version.model.VersionRevertBodyMetaModel;

import jakarta.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface VersionLocal {
    PaginatedListWrapper<VersionMetaModel> getNodeVersions(String acceptLanguage, String nodeId);

    List<VersionRepresentation> getNodeVersionsRepresentation(String acceptLanguage, String nodeId);

    VersionMetaModel revertToVersion(String acceptLanguage, String nodeId, String versionId, VersionRevertBodyMetaModel revertBodyMetaModel);

    VersionMetaModel getVersionBeforePropertyValueChange(List<VersionMetaModel> versionMetaModels, String propertyKey, String propertyValue);

    VersionMetaModel getNodeVersion(String acceptLanguage, String nodeId, String versionId);

    Response getNodeVersionContent(String nodeId, String version, Boolean attachment) throws UnsupportedEncodingException;

    NodeRepresentation purgeNodeVersion(String nodeId, String versionId);
}
