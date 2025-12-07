package net.fina.server.legislative.model;

import net.fina.server.legislative.entity.LegislativeDocumentCategory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LegislativeCategoryMetaModel implements Serializable {
    private long id;
    private String name;
    private long nameStrId;
    private int version;
    private Integer documentCount;

    private List<LegislativeDocumentMetaModel> documents = new ArrayList<>();

    public LegislativeCategoryMetaModel() {
    }

    public LegislativeCategoryMetaModel(long id, String name, long nameStrId,  Integer documentCount, int version) {
        this.id = id;
        this.name = name;
        this.nameStrId = nameStrId;
        this.documentCount = documentCount;
        this.version = version;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDocumentCount() {
        return documentCount;
    }

    public void setDocumentCount(Integer documentCount) {
        this.documentCount = documentCount;
    }

    public long getNameStrId() {
        return nameStrId;
    }

    public void setNameStrId(long nameStrId) {
        this.nameStrId = nameStrId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<LegislativeDocumentMetaModel> getDocuments() {
        return documents;
    }

    public void setDocuments(List<LegislativeDocumentMetaModel> documents) {
        this.documents = documents;
    }
}
