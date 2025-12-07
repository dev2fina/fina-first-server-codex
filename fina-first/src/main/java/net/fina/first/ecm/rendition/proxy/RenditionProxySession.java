package net.fina.first.ecm.rendition.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.common.representation.UnexpectedErrorRepresentation;
import net.fina.ecm.alfresco.api.core.model.body.RenditionBodyCreate;
import net.fina.ecm.alfresco.api.core.model.representation.RenditionRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.RenditionStatusEnum;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.first.common.exception.RenditionException;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.rendition.model.RenditionMetaModel;
import net.fina.first.ecm.rendition.model.RenditionModelHelper;
import net.fina.first.interceptors.FirstRecordingAuditor;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Stateless
@RolesAllowed({PermissionIdNames.FIRST_REVIEW})
@SecurityDomain("FinASecurityDomain")
@Interceptors(FirstRecordingAuditor.class)
public class RenditionProxySession {

    private final Logger log = Logger.getLogger(getClass());

    @Inject
    private EcmClientProxySession ecmClientProxySession;

    public PaginatedListWrapper<RenditionMetaModel> getRenditions(String nodeId) {
        ResultPaging<RenditionRepresentation> renditionRepresentations = ecmClientProxySession.getAlfrescoClient().getRenditionsAPI().listRenditionsCall(nodeId);
        List<RenditionMetaModel> renditionMetaModels = RenditionModelHelper.getMetaModels(renditionRepresentations.getObjects());

        PaginatedListWrapper<RenditionMetaModel> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(renditionMetaModels);
        listWrapper.setTotalResults(renditionRepresentations.getPagination().getTotalItems());

        return listWrapper;
    }

    public RenditionMetaModel getRendition(String nodeId, String renditionId) {
        RenditionRepresentation representation = ecmClientProxySession.getAlfrescoClient().getRenditionsAPI().getRenditionCall(nodeId, renditionId);
        return RenditionModelHelper.getMetaModel(representation);
    }

    public Response getRenditionContent(String nodeId, String renditionId, Boolean attachment) {
        return ecmClientProxySession.getAlfrescoClient().getRenditionsAPI().getRenditionContentCall(nodeId, renditionId, attachment);
    }

    public Response createRendition(String nodeId, String renditionId) {
        return ecmClientProxySession.getAlfrescoClient().getRenditionsAPI().createRenditionCall(nodeId, new RenditionBodyCreate(renditionId, null));
    }

    public Response previewRenditionContent(String nodeId) throws RenditionException {
        try {
            List<RenditionMetaModel> renditions = getRenditions(nodeId).getList();
            if (renditions != null && !renditions.isEmpty()) {
                for (RenditionMetaModel rendition : renditions) {
                    if (rendition.getStatus() == RenditionStatusEnum.CREATED) {
                        Response response = getRenditionContent(nodeId, rendition.getId(), false);
                        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                            throw new RenditionException(response.readEntity(UnexpectedErrorRepresentation.class));
                        }
                        return response;
                    }
                }
            }

            throw new RenditionException("Rendition is not supported for selected node: " + nodeId);

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            throw new RenditionException("Preview is not available.");
        }
    }

    public void prepareRenditionContentForReview(String nodeId) throws RenditionException {
        try {
            List<RenditionMetaModel> renditions = getRenditions(nodeId).getList();
            if (renditions != null && !renditions.isEmpty()) {
                final String defaultRenditionId = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.RENDITION_DEFAULT_ID_KEY);
                RenditionMetaModel defaultNodeRendition = null;

                for (RenditionMetaModel rendition : renditions) {
                    if (rendition.getStatus() == RenditionStatusEnum.CREATED) {
                        return;
                    }

                    if (rendition.getId().equalsIgnoreCase(defaultRenditionId)) {
                        defaultNodeRendition = rendition;
                    }
                }

                defaultNodeRendition = (defaultNodeRendition != null ? defaultNodeRendition : renditions.get(0));
                if (defaultNodeRendition != null) {
                    Response response = createRendition(nodeId, defaultNodeRendition.getId());
                    if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                        throw new RenditionException(response.readEntity(UnexpectedErrorRepresentation.class));
                    }
                    return;
                }
            }

            throw new RenditionException("Rendition is not supported for selected node: " + nodeId);

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            throw new RenditionException("Preview is not available.");
        }
    }

}
