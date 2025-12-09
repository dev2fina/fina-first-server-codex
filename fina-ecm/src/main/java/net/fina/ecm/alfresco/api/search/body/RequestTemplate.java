package net.fina.ecm.alfresco.api.search.body;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.util.Objects;

/**
 * RequestTemplatesInner
 */
public class RequestTemplate implements BaseRepresentation {
    @JsonProperty("name")
    private String name = null;

    @JsonProperty("template")
    private String template = null;

    public RequestTemplate name(String name) {
        this.name = name;
        return this;
    }

    /**
     * The template name
     *
     * @return name
     **/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RequestTemplate template(String template) {
        this.template = template;
        return this;
    }

    /**
     * The template
     *
     * @return template
     **/
    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestTemplate requestTemplate = (RequestTemplate) o;
        return Objects.equals(this.name, requestTemplate.name)
                && Objects.equals(this.template, requestTemplate.template);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, template);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RequestTemplate {, ");

        sb.append("    name: ").append(toIndentedString(name)).append(", ");
        sb.append("    template: ").append(toIndentedString(template)).append(", ");
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
