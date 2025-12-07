package net.fina.ecm.alfresco.api.core.model.body;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RenditionBodyCreate implements BaseRepresentation {

    @JsonProperty("id")
    public final String id;

    public RenditionBodyCreate(String id, String myRating) {
        this.id = id;
    }
}
