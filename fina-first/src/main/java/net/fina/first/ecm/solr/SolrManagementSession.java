package net.fina.first.ecm.solr;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.first.interceptors.FirstRecordingAuditor;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.text.MessageFormat;

@Stateless
@RolesAllowed({PermissionIdNames.FIRST_REVIEW})
@SecurityDomain("FinASecurityDomain")
@Interceptors(FirstRecordingAuditor.class)
public class SolrManagementSession {
    private Logger log = Logger.getLogger(getClass().getName());

    public Response reindexSolr() {
        String solrUrl = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.SOLR_SERVER_URL);
        String query = "{\"delete\":{\"query\":\"*:*\" }}";
        if (solrUrl != null) {
            String reindexUrlTemplate = solrUrl + "/{0}/update";
            Client client = ClientBuilder.newClient();
            log.info("Call Solr Reindex at  : " + solrUrl);
            log.info("Solr Call Reindex [Alfresco] Core");
            client.target(MessageFormat.format(reindexUrlTemplate, "alfresco")).request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(query, MediaType.APPLICATION_JSON));
            log.info("Solr Call Reindex [Archive] Core");
            return client.target(MessageFormat.format(reindexUrlTemplate, "archive")).request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(query, MediaType.APPLICATION_JSON));
        }

        return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
    }
}
