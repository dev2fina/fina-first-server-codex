package net.fina.first.ecm.node.model;

import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.VersionRepresentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeModelHelper {

    public static List<NodeMetaModel> getMetaModels(List<? extends NodeRepresentation> nodeRepresentations) {
        List<NodeMetaModel> result = new ArrayList<>();
        if (nodeRepresentations != null && !nodeRepresentations.isEmpty()) {
            for (NodeRepresentation nodeRepresentation : nodeRepresentations) {
                result.add(getMetaModel(nodeRepresentation));
            }
        }
        return result;
    }

    public static NodeMetaModel getMetaModel(NodeRepresentation nodeRepresentation) {
        NodeMetaModel result = new NodeMetaModel();
        result.setAllowableOperations(nodeRepresentation.getAllowableOperations());
        result.setAspects(nodeRepresentation.getAspects());
        result.setAssociation(nodeRepresentation.getAssociation());
        result.setContent(nodeRepresentation.getContent());
        result.setCreatedAt(nodeRepresentation.getCreatedAt());
        result.setCreatedBy(nodeRepresentation.getCreatedByUser());
        result.setFile(nodeRepresentation.isFile());
        result.setFolder(nodeRepresentation.isFolder());
        result.setId(nodeRepresentation.getId());
        result.setLink(nodeRepresentation.isLink());
        result.setLocked(nodeRepresentation.isLocked());
        result.setModifiedAt(nodeRepresentation.getModifiedAt());
        result.setModifiedBy(nodeRepresentation.getModifiedByUser());
        result.setName(nodeRepresentation.getName());
        result.setNodeType(nodeRepresentation.getNodeType());
        result.setParentId(nodeRepresentation.getParentId());
        result.setPath(nodeRepresentation.getPath());
        result.setPermissions(nodeRepresentation.getPermissions());
        result.setProperties(nodeRepresentation.getProperties());

        return result;
    }

    public static NodeMetaModel getMetaModel(VersionRepresentation versionRepresentation) {
        NodeMetaModel result = new NodeMetaModel();
        result.setAspects(versionRepresentation.getAspectNames());
        result.setContent(versionRepresentation.getContent());
        result.setFile(versionRepresentation.getIsFile());
        result.setFolder(versionRepresentation.getIsFolder());
        result.setId(versionRepresentation.getId());
        result.setModifiedAt(versionRepresentation.getModifiedAt());
        result.setModifiedBy(versionRepresentation.getModifiedByUser());
        result.setName(versionRepresentation.getName());
        result.setNodeType(versionRepresentation.getNodeType());
        result.setVersionComment(versionRepresentation.getVersionComment());

        Map<String, Object> properties = new HashMap<>(versionRepresentation.getProperties());
        result.setProperties(properties);

        return result;
    }

}
