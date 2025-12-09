package net.fina.ecm.alfresco.api.common.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonTypeName(value = "list")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultPaging<T> implements Serializable, BaseRepresentation {
    /**
     * List of all object present in a page.
     */
    @JsonProperty("entries")
    protected List<T> objects;

    @JsonProperty("pagination")
    protected PaginationRepresentation pagination;

    public ResultPaging() {

    }

    public ResultPaging(List<T> objects, PaginationRepresentation pagination) {
        this.objects = objects;
        this.pagination = pagination;
    }

    /**
     * {@inheritDoc}
     */
    public Boolean hasMoreItems() {
        return pagination.getHasMoreItems();
    }

    /**
     * {@inheritDoc}
     */
    public long getCount() {
        return pagination.getCount();
    }

    /**
     * {@inheritDoc}
     */
    public List<T> getObjects() {
        return objects != null ? objects : new ArrayList<>();
    }

    public void setObjects(List<T> objects) {
        this.objects = objects;
    }

    public PaginationRepresentation getPagination() {
        return pagination;
    }

    public void setPagination(PaginationRepresentation pagination) {
        this.pagination = pagination;
    }

}
