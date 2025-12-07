package net.fina.first.ecm.dictionary.model;

public class ClassAssociationMetaModel {
    private String name;
    private String title;
    private String url;
    private AssociationSourceTargetMetaModel source;
    private AssociationSourceTargetMetaModel target;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public AssociationSourceTargetMetaModel getSource() {
        return source;
    }

    public void setSource(AssociationSourceTargetMetaModel source) {
        this.source = source;
    }

    public AssociationSourceTargetMetaModel getTarget() {
        return target;
    }

    public void setTarget(AssociationSourceTargetMetaModel target) {
        this.target = target;
    }
}
