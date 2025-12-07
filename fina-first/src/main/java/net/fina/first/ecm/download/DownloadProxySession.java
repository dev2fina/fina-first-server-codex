package net.fina.first.ecm.download;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.ecm.alfresco.api.core.model.body.DownloadBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.DownloadRepresentation;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.node.proxy.NodeProxySession;
import net.fina.first.interceptors.FirstRecordingAuditor;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

@Stateless
@RolesAllowed({PermissionIdNames.FIRST_REVIEW})
@SecurityDomain("FinASecurityDomain")
@Interceptors(FirstRecordingAuditor.class)
public class DownloadProxySession {

    @Inject
    private EcmClientProxySession ecmClientProxySession;

    @Inject
    private NodeProxySession nodeProxySession;

    public DownloadRepresentation createDownload(DownloadBodyCreate downloadBodyCreate) {
        return ecmClientProxySession.getAlfrescoClient().getDownloadAPI().createDownloadRepresentation(downloadBodyCreate);
    }

    public DownloadRepresentation getDownloadInfo(String downloadId, String name) throws NodeException {
        if (name != null && !name.isEmpty()) {
            NodeBodyUpdate nbu = new NodeBodyUpdate();
            nbu.setName(name + ".zip");
            nodeProxySession.updateNode(downloadId, nbu);
        }
        return ecmClientProxySession.getAlfrescoClient().getDownloadAPI().getDownloadRepresentation(downloadId);
    }
}
