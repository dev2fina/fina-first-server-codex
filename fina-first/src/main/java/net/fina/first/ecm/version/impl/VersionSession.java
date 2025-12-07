package net.fina.first.ecm.version.impl;

import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.RevertBody;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.VersionRepresentation;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.version.api.VersionLocal;
import net.fina.first.ecm.version.model.VersionMetaModel;
import net.fina.first.ecm.version.model.VersionModelHelper;
import net.fina.first.ecm.version.model.VersionRevertBodyMetaModel;
import net.fina.first.ecm.version.model.VersionRevertBodyModelHelper;
import net.fina.first.interceptors.FirstRecordingAuditor;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

@Stateless
@Local(VersionLocal.class)
@Interceptors(FirstRecordingAuditor.class)
public class VersionSession implements VersionLocal {

    @Inject
    private EcmClientProxySession ecmClientProxySession;

    @Override
    public PaginatedListWrapper<VersionMetaModel> getNodeVersions(String acceptLanguage, String nodeId) {
        ResultPaging<VersionRepresentation> resultPaging = ecmClientProxySession.getAlfrescoClient(acceptLanguage).getVersionAPI().listVersionHistoryCall(nodeId, null, null, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), null);
        List<VersionMetaModel> resultModels = VersionModelHelper.getMetaModels(resultPaging.getObjects());

        PaginatedListWrapper<VersionMetaModel> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(resultModels);
        listWrapper.setTotalResults(resultPaging.getCount());

        return listWrapper;
    }

    @Override
    public List<VersionRepresentation> getNodeVersionsRepresentation(String acceptLanguage, String nodeId) {
        ResultPaging<VersionRepresentation> resultPaging = ecmClientProxySession.getAlfrescoClient(acceptLanguage).getVersionAPI().listVersionHistoryCall(nodeId, null, null, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), null);
        return resultPaging.getObjects();
    }

    @Override
    public VersionMetaModel revertToVersion(String acceptLanguage, String nodeId, String versionId, VersionRevertBodyMetaModel revertBodyMetaModel) {
        RevertBody revertBody = VersionRevertBodyModelHelper.getRepresentation(revertBodyMetaModel);
        VersionRepresentation result = ecmClientProxySession.getAlfrescoClient(acceptLanguage).getVersionAPI().revertVersionCall(nodeId, versionId, revertBody);
        return VersionModelHelper.getMetaModel(result);
    }

    @Override
    public VersionMetaModel getVersionBeforePropertyValueChange(List<VersionMetaModel> versionMetaModels, String propertyKey, String propertyValue) {
        VersionMetaModel result = null;
        for (VersionMetaModel versionMetaModel : versionMetaModels) {
            String value = (String) versionMetaModel.getProperties().get(propertyKey);
            if (value != null && !value.equalsIgnoreCase(propertyValue)) {
                result = versionMetaModel;
                break;
            }
        }
        return result;
    }

    @Override
    public VersionMetaModel getNodeVersion(String acceptLanguage, String nodeId, String versionId) {
        VersionRepresentation versionRepresentation = ecmClientProxySession.getAlfrescoClient(acceptLanguage).getVersionAPI().getVersionCall(nodeId, versionId);
        return VersionModelHelper.getMetaModel(versionRepresentation);
    }

    @Override
    public Response getNodeVersionContent(String nodeId, String version, Boolean attachment) throws UnsupportedEncodingException {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        Response response = client.getVersionAPI().getNodeVersionContent(nodeId, version, attachment);
        VersionRepresentation versionRepresentation = client.getVersionAPI().getVersionCall(nodeId, version);
        String displayName = URLEncoder.encode(versionRepresentation.getName(), "UTF-8").replace("+", "%20");

        return Response
                .status(response.getStatus())
                .type(MediaType.APPLICATION_OCTET_STREAM)
                .entity(response.getEntity())
                .header("Content-Disposition", "attachment; filename=\"" + displayName + "\"")
                .build();
    }

    @Override
    public NodeRepresentation purgeNodeVersion(String nodeId, String versionId) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        client.getVersionAPI().deleteVersionCall(nodeId, versionId);
        return client.getNodesAPI().getNodeCall(nodeId);
    }
}
