package net.fina.ecm.alfresco.api.workflow.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

@JsonTypeName(value = "entry")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkflowProcessDefinitionRepresentation implements BaseRepresentation {

    @JsonProperty("id")
    private String id;

    @JsonProperty("key")
    private String key;

    @JsonProperty("name")
    private String name;

    @JsonProperty("category")
    private String category;

    @JsonProperty("deploymentId")
    private String deploymentId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("startFormResourceKey")
    private String startFormResourceKey;

    @JsonProperty("graphicNotationDefined")
    private boolean graphicNotationDefined;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartFormResourceKey() {
        return startFormResourceKey;
    }

    public void setStartFormResourceKey(String startFormResourceKey) {
        this.startFormResourceKey = startFormResourceKey;
    }

    public boolean isGraphicNotationDefined() {
        return graphicNotationDefined;
    }

    public void setGraphicNotationDefined(boolean graphicNotationDefined) {
        this.graphicNotationDefined = graphicNotationDefined;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class WorkflowProcessDefinitionRepresentation {, ");

        sb.append("    id: ").append(toIndentedString(id)).append(", ");
        sb.append("    key: ").append(toIndentedString(key)).append(", ");
        sb.append("    name: ").append(toIndentedString(name)).append(", ");
        sb.append("    category: ").append(toIndentedString(category)).append(", ");
        sb.append("    deploymentId: ").append(toIndentedString(deploymentId)).append(", ");
        sb.append("    title: ").append(toIndentedString(title)).append(", ");
        sb.append("    description: ").append(toIndentedString(description)).append(", ");
        sb.append("    startFormResourceKey: ").append(toIndentedString(startFormResourceKey)).append(", ");
        sb.append("    graphicNotationDefined: ").append(toIndentedString(graphicNotationDefined)).append(", ");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace(", ", ",     ");
    }
}
