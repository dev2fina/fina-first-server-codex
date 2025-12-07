package net.fina.first.ecm.node.proxy;

import net.fina.common.server.util.ConfigurationUtil;
import net.fina.common.shared.event.ReportContentManagementEvent;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.plugins.providers.multipart.OutputPart;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.StreamingOutput;
import java.io.ByteArrayInputStream;
import java.util.List;

@Stateless
public class NodeProxySessionAllowed {
    private final Logger log = Logger.getLogger(getClass().getName());

    @Inject
    private EcmClientProxySession ecmClientProxySession;


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void uploadFile(@Observes(during = TransactionPhase.AFTER_SUCCESS) ReportContentManagementEvent event) {
        String enableEcm = ConfigurationUtil.get().get("ECM.enable");
        try {

            if (enableEcm != null && (!enableEcm.isEmpty()) && Integer.parseInt(enableEcm) > 0 && event.getRepositoryNodeId() != null && !event.getRepositoryNodeId().trim().isEmpty()) {
                log.info("Saving Report [" + event.getFileName() + "] into DMS folder ");
                AlfrescoClient client = ecmClientProxySession.getAlfrescoClientByUser(event.getUserLogin());
                ResultPaging<NodeRepresentation> savedReports = client.getNodesAPI().listNodeChildrenCall(event.getRepositoryNodeId(), 0, Integer.MAX_VALUE, null);
                String fileName = event.getFileName();

                MultipartFormDataOutput mdo = new MultipartFormDataOutput();
                OutputPart objPart = mdo.addFormData("filedata", new ByteArrayInputStream(event.getContent()), MediaType.MULTIPART_FORM_DATA_TYPE);
                objPart.getHeaders().putSingle("Content-Disposition", "form-data; name=filedata; filename=" + fileName);
                mdo.addFormData("name", fileName, MediaType.TEXT_PLAIN_TYPE);

                mdo.addFormData("nodeType", AlfrescoPropConstants.DATA_TYPE_REPORT_DOCUMENT, MediaType.TEXT_PLAIN_TYPE);
                mdo.addFormData(AlfrescoPropConstants.REPORT_DOCUMENT_PROP_NOTIFICATION_MAILS, event.getNotificationMails(), MediaType.TEXT_PLAIN_TYPE);

                NodeRepresentation existing = findNodeById(fileName, savedReports.getObjects());

                if (existing == null) {
                    client.getNodesAPI().createUploadNodeCall(event.getRepositoryNodeId(), mdo, true, null, null);
                } else {
                    StreamingOutput out = null;
                    out = outputStream -> outputStream.write(IOUtils.toByteArray(new ByteArrayInputStream(event.getContent())));

                    client.getNodesAPI().updateUploadNodeCall(existing.getId(), out, false, null, null);
                }
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

    private NodeRepresentation findNodeById(String name, List<NodeRepresentation> nodes) {
        for (NodeRepresentation n : nodes) {
            if (n.getName().equals(name)) {
                return n;
            }
        }

        return null;
    }
}
