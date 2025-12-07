package net.fina.first.ecm.tag.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.TagBody;
import net.fina.ecm.alfresco.api.core.model.representation.TagRepresentation;
import net.fina.first.FirstUtil;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.tag.model.*;
import net.fina.first.interceptors.FirstRecordingAuditor;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import java.util.ArrayList;
import java.util.List;

@Stateless
@RolesAllowed({PermissionIdNames.FIRST_REVIEW})
@SecurityDomain("FinASecurityDomain")
@Interceptors(FirstRecordingAuditor.class)
public class TagProxySession {

    @Inject
    private EcmClientProxySession ecmClientProxySession;

    public PaginatedListWrapper<TagMetaModel> listTagsForNode(String nodeId) {
        ResultPaging<TagRepresentation> representationResultPaging = ecmClientProxySession.getAlfrescoClient().getTagsAPI().listTagsForNodeCall(nodeId);
        List<TagMetaModel> tagMetaModels = TagModelHelper.getMetaModels(representationResultPaging.getObjects());

        return getTagMetaModelPaginatedListWrapper(tagMetaModels, representationResultPaging.getPagination().getTotalItems());
    }

    public PaginatedListWrapper<TagMetaModel> listTagsForNode(String nodeId, Integer start, Integer limit, List<String> fieldValues) {
        ResultPaging<TagRepresentation> representationResultPaging = ecmClientProxySession.getAlfrescoClient().getTagsAPI().listTagsForNodeCall(nodeId, start, limit, FirstUtil.getFieldsParamFromValuesSafe(fieldValues));
        List<TagMetaModel> tagMetaModels = TagModelHelper.getMetaModels(representationResultPaging.getObjects());

        return getTagMetaModelPaginatedListWrapper(tagMetaModels, representationResultPaging.getPagination().getTotalItems());
    }

    public PaginatedListWrapper<TagMetaModel> listTags() {
        ResultPaging<TagRepresentation> representationResultPaging = ecmClientProxySession.getAlfrescoClient().getTagsAPI().listTagsCall();
        List<TagMetaModel> tagMetaModels = TagModelHelper.getMetaModels(representationResultPaging.getObjects());

        return getTagMetaModelPaginatedListWrapper(tagMetaModels, representationResultPaging.getPagination().getTotalItems());
    }

    public PaginatedListWrapper<TagMetaModel> listTags(Integer start, Integer limit, List<String> fieldValues) {
        ResultPaging<TagRepresentation> representationResultPaging = ecmClientProxySession.getAlfrescoClient().getTagsAPI().listTagsCall(start, limit, FirstUtil.getFieldsParamFromValuesSafe(fieldValues));
        List<TagMetaModel> tagMetaModels = TagModelHelper.getMetaModels(representationResultPaging.getObjects());

        return getTagMetaModelPaginatedListWrapper(tagMetaModels, representationResultPaging.getPagination().getTotalItems());
    }

    public TagMetaModel createTagForNode(String nodeId, TagBodyMetaModel tagBodyMetaModel) {
        TagBody tagBody = TagBodyModelHelper.getTagBody(tagBodyMetaModel);
        TagRepresentation tagRepresentation = ecmClientProxySession.getAlfrescoClient().getTagsAPI().createTagForNodeCall(nodeId, tagBody);
        return TagModelHelper.getMetaModel(tagRepresentation);
    }

    public PaginatedListWrapper<TagMetaModel> createTagsForNode(String nodeId, List<TagBodyMetaModel> tagBodyMetaModels) {
        TagBody[] tagBodies = TagBodyModelHelper.getTagBodies(tagBodyMetaModels);

        int totalResult = 0;
        List<TagMetaModel> tagMetaModels = new ArrayList<>();
        if (tagBodies.length == 1) {
            ecmClientProxySession.getAlfrescoClient().getTagsAPI().createTagForNodeCall(nodeId, tagBodies[0]);
            totalResult = tagBodies.length;
        } else {
            ResultPaging<TagRepresentation> representationResultPaging = ecmClientProxySession.getAlfrescoClient().getTagsAPI().createTagsForNodeCall(nodeId, tagBodies);
            tagMetaModels.addAll(TagModelHelper.getMetaModels(representationResultPaging.getObjects()));
            totalResult = representationResultPaging.getPagination().getTotalItems();
        }

        return getTagMetaModelPaginatedListWrapper(tagMetaModels, totalResult);
    }

    public TagMetaModel getTag(String tagId) {
        TagRepresentation tagRepresentation = ecmClientProxySession.getAlfrescoClient().getTagsAPI().getTagCall(tagId);
        return TagModelHelper.getMetaModel(tagRepresentation);
    }

    public TagMetaModel updateTag(String tagId, TagBodyMetaModel tagBodyMetaModel, List<String> fieldValues) {
        TagBody tagBody = TagBodyModelHelper.getTagBody(tagBodyMetaModel);
        TagRepresentation tagRepresentation = ecmClientProxySession.getAlfrescoClient().getTagsAPI().updateTagCall(tagId, tagBody, FirstUtil.getFieldsParamFromValuesSafe(fieldValues));
        return TagModelHelper.getMetaModel(tagRepresentation);
    }

    public void deleteTagFromNode(String nodeId, String tagId) {
        ecmClientProxySession.getAlfrescoClient().getTagsAPI().deleteTagFromNodeCall(nodeId, tagId);
    }

    public void updateNodeTags(String nodeId, TagUpdateMetaModel tagUpdateMetaModel) {
        List<TagMetaModel> removedTags = tagUpdateMetaModel.getRemovedTags();
        if (removedTags != null && !removedTags.isEmpty()) {
            for (TagMetaModel removedTag : removedTags) {
                if (!removedTag.getId().equalsIgnoreCase(removedTag.getTag())) {
                    deleteTagFromNode(nodeId, removedTag.getId());
                }
            }
        }

        List<TagMetaModel> addedTags = tagUpdateMetaModel.getAddedTags();
        if (addedTags != null && !addedTags.isEmpty()) {
            createTagsForNode(nodeId, TagBodyModelHelper.getTagBodiesMetaModel(addedTags));
        }
    }

    private PaginatedListWrapper<TagMetaModel> getTagMetaModelPaginatedListWrapper(List<TagMetaModel> tagMetaModels, Integer totalResult) {
        PaginatedListWrapper<TagMetaModel> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(tagMetaModels);
        listWrapper.setTotalResults(totalResult);

        return listWrapper;
    }

}
