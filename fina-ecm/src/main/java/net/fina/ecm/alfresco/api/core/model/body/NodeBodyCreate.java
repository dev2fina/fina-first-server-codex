package net.fina.ecm.alfresco.api.core.model.body;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeBodyCreate implements BaseRepresentation {
    @JsonProperty("name")
    public String name;

    @JsonProperty("nodeType")
    public String nodeType;

    @JsonProperty("properties")
    public Map<String, Object> properties;

    @JsonProperty("aspectNames")
    public List<String> aspectNames;

    @JsonProperty("relativePath")
    public String relativePath;

    @JsonProperty("association")
    public NodeBodyCreateAssociation association;

    @JsonProperty("secondaryChildren")
    public List<ChildAssociationBody> secondaryChildren;

    @JsonProperty("targets")
    public List<AssociationBody> targets;

    public NodeBodyCreate(){}

    public NodeBodyCreate(String name, String nodeType)
    {
        this.nodeType = nodeType;
        this.name = name;
        this.properties = null;
        this.aspectNames = null;
        this.relativePath = null;
        this.association = null;
        this.secondaryChildren = null;
        this.targets = null;
    }

    public NodeBodyCreate(String name, String nodeType, Map<String, Object> properties, List<String> aspectNames)
    {
        this.nodeType = nodeType;
        this.name = name;
        this.properties = properties;
        this.aspectNames = aspectNames;
        this.relativePath = null;
        this.association = null;
        this.secondaryChildren = null;
        this.targets = null;
    }

    public NodeBodyCreate(String name, String nodeType, Map<String, Object> properties, List<String> aspectNames,
                          String relativePath)
    {
        this.nodeType = nodeType;
        this.name = name;
        this.properties = properties;
        this.aspectNames = aspectNames;
        this.relativePath = relativePath;
        this.association = null;
        this.secondaryChildren = null;
        this.targets = null;
    }

    public NodeBodyCreate(String name, String nodeType, Map<String, Object> properties, List<String> aspectNames,
                          String relativePath, NodeBodyCreateAssociation association, List<ChildAssociationBody> secondaryChildren,
                          List<AssociationBody> targets)
    {
        this.nodeType = nodeType;
        this.name = name;
        this.properties = properties;
        this.aspectNames = aspectNames;
        this.relativePath = relativePath;
        this.association = association;
        this.secondaryChildren = secondaryChildren;
        this.targets = targets;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public List<String> getAspectNames() {
        return aspectNames;
    }

    public void setAspectNames(List<String> aspectNames) {
        this.aspectNames = aspectNames;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public NodeBodyCreateAssociation getAssociation() {
        return association;
    }

    public void setAssociation(NodeBodyCreateAssociation association) {
        this.association = association;
    }

    public List<ChildAssociationBody> getSecondaryChildren() {
        return secondaryChildren;
    }

    public void setSecondaryChildren(List<ChildAssociationBody> secondaryChildren) {
        this.secondaryChildren = secondaryChildren;
    }

    public List<AssociationBody> getTargets() {
        return targets;
    }

    public void setTargets(List<AssociationBody> targets) {
        this.targets = targets;
    }
}
