package net.fina.first.ecm.sites.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.representation.AbstractRepresentation;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.SiteBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.SiteMembershipBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.SiteMembershipBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.SiteMemberAuthorityRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.SiteMemberAuthorityType;
import net.fina.ecm.alfresco.api.core.model.representation.SiteMemberGroup;
import net.fina.ecm.alfresco.api.core.model.representation.SiteMemberRole;
import net.fina.ecm.alfresco.api.core.model.representation.SiteRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.SiteRepresentationEntry;
import net.fina.ecm.alfresco.api.core.model.representation.SiteRoleRepresentation;
import net.fina.first.EcmSitesUtil;
import net.fina.first.FirstUtil;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.node.model.NodeMetaModel;
import net.fina.first.ecm.node.model.NodeModelHelper;
import net.fina.first.interceptors.FirstRecordingAuditor;
import org.apache.http.HttpStatus;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.ClientErrorException;
import java.util.ArrayList;
import java.util.List;

@Stateless
@RolesAllowed({PermissionIdNames.FIRST_REVIEW})
@SecurityDomain("FinASecurityDomain")
@Interceptors(FirstRecordingAuditor.class)
public class SitesProxySession {

    @Inject
    private EcmClientProxySession ecmClientProxySession;

    public PaginatedListWrapper<NodeMetaModel> loadUserSites(Integer start, Integer limit, String orderBy) {
        return loadSites(true, start, limit, orderBy);
    }

    public PaginatedListWrapper<NodeMetaModel> loadPublicSites(Integer start, Integer limit, String orderBy) {
        return loadSites(false, start, limit, orderBy);
    }

    public SiteRepresentation createSite(SiteBodyCreate siteBodyCreate) {
        return ecmClientProxySession.getAlfrescoClient().getSitesAPI().createSite(siteBodyCreate);
    }

    public List<SiteMemberAuthorityRepresentation> loadSiteMembers(String siteId) {
        List<SiteMemberAuthorityRepresentation> siteMembers = ecmClientProxySession.getAlfrescoClient()
                .getWebScriptApi()
                .listSiteMembersByType(siteId, null);
        siteMembers.sort((s1, s2) -> {
            if (s1.getAuthority().getAuthorityType().equals(SiteMemberAuthorityType.USER)) {
                return 1;
            } else if (s1.getAuthority().getAuthorityType().equals(SiteMemberAuthorityType.GROUP)) {
                return -1;
            }
            return 0;
        });

        return siteMembers;
    }

    public void createSiteMembers(String siteId, List<SiteMemberAuthorityRepresentation> members) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        members.forEach(member -> {
            createSiteMember(client, siteId, member);
        });
    }

    private void createSiteMember(AlfrescoClient client, String siteId, SiteMemberAuthorityRepresentation membershipBodyCreate) {
        try {
            switch (membershipBodyCreate.getAuthority().getAuthorityType()) {
                case GROUP:
                    client.getWebScriptApi().createSiteMemberGroup(siteId,
                            new SiteMemberRole(new SiteMemberGroup(membershipBodyCreate.getId()), membershipBodyCreate.getRole()));
                    break;
                case USER:
                    client.getSitesAPI().createSiteMembershipCall(siteId,
                            new SiteMembershipBodyCreate(membershipBodyCreate.getId(), membershipBodyCreate.getRole()), null);
                    break;
            }
        } catch (ClientErrorException ex) {
            if (ex.getResponse().getStatus() == HttpStatus.SC_CONFLICT) {
                updateSiteMember(client, siteId, membershipBodyCreate.getId(), membershipBodyCreate.getRole());
            }
        }
    }

    private void updateSiteMember(AlfrescoClient client, String siteId, String personId, String role) {
        client.getSitesAPI().updateSiteMembershipCall(siteId,
                personId,
                new SiteMembershipBodyUpdate(role),
                null);
    }

    private PaginatedListWrapper<NodeMetaModel> loadSites(boolean privateSites, Integer start, Integer limit, String orderBy) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        ResultPaging<? extends AbstractRepresentation> resultPageBag = privateSites ?
                client.getSitesAPI().listPersonSites("-me-", null, null) :
                client.getSitesAPI().listSites(null, null, "(visibility='PUBLIC')");

        List<NodeMetaModel> siteRepresentationTotalList = new ArrayList<>();

        if (resultPageBag.getObjects() != null) {
            resultPageBag.getObjects().forEach(o -> {

                SiteRepresentation sr = privateSites ? ((SiteRoleRepresentation) o).getSite() : ((SiteRepresentationEntry) o);
                NodeMetaModel nodeMetaModel = NodeModelHelper.getMetaModel(client.getNodesAPI().getNodeCall(sr.getGuid(), null, "documentLibrary", null));
                nodeMetaModel.setSite(sr);
                siteRepresentationTotalList.add(nodeMetaModel);
            });
        }
        List<NodeMetaModel> siteRepresentationSortedPaginatedList = FirstUtil
                .getPaginatedList(EcmSitesUtil.getOrderedList(siteRepresentationTotalList, orderBy), start, limit);

        PaginatedListWrapper<NodeMetaModel> result = new PaginatedListWrapper<>();
        result.setList(siteRepresentationSortedPaginatedList);
        result.setTotalResults(resultPageBag.getPagination().getTotalItems());

        return result;
    }

    public void leaveSite(String siteId) {
        ecmClientProxySession.getAlfrescoClient().getSitesAPI().deleteSiteMembershipCall(siteId, "-me-");
    }

    public void deleteSite(String siteId) {
        ecmClientProxySession.getAlfrescoClient().getSitesAPI().deleteSite(siteId);
    }

    public void removePersonFromSite(String siteId, SiteMemberAuthorityRepresentation siteMember) {
        switch (siteMember.getAuthority().getAuthorityType()) {
            case USER:
                ecmClientProxySession.getAlfrescoClient().getSitesAPI().deleteSiteMembershipCall(siteId, siteMember.getId());
                break;
            case GROUP:
                ecmClientProxySession.getAlfrescoClient().getWebScriptApi().removeSiteGroup(siteId, siteMember.getId());
                break;
        }
    }

    public SiteRepresentation getSite(String siteId) {
        return ecmClientProxySession.getAlfrescoClient().getSitesAPI().getSite(siteId);
    }
}
