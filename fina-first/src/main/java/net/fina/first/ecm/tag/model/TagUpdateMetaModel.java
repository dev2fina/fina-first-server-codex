package net.fina.first.ecm.tag.model;

import java.util.List;

public class TagUpdateMetaModel {
    private List<TagMetaModel> addedTags;
    private List<TagMetaModel> removedTags;

    public List<TagMetaModel> getAddedTags() {
        return addedTags;
    }

    public void setAddedTags(List<TagMetaModel> addedTags) {
        this.addedTags = addedTags;
    }

    public List<TagMetaModel> getRemovedTags() {
        return removedTags;
    }

    public void setRemovedTags(List<TagMetaModel> removedTags) {
        this.removedTags = removedTags;
    }
}
