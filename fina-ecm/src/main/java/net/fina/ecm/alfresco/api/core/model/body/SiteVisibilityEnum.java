package net.fina.ecm.alfresco.api.core.model.body;


import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;


public enum SiteVisibilityEnum implements BaseRepresentation
{
    @JsonProperty("PUBLIC") PUBLIC("PUBLIC"),

    @JsonProperty("PRIVATE") PRIVATE("PRIVATE"),

    @JsonProperty("MODERATED") MODERATED("MODERATED");

    private String value;

    SiteVisibilityEnum(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return String.valueOf(value);
    }

    public String value()
    {
        return value;
    }

}
