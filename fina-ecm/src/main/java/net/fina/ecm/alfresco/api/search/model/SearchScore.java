package net.fina.ecm.alfresco.api.search.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

/**
 * SearchEntry
 */
public class SearchScore implements BaseRepresentation {
    @JsonProperty("score")
    private Float score = null;

    /**
     * The score for this row
     *
     * @return score
     **/
    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }
}
