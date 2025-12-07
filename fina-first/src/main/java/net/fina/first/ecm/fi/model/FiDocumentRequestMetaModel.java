package net.fina.first.ecm.fi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FiDocumentRequestMetaModel {
    private String id;
    private String name;
    private String description;
    private Date dueDate;
    private String assigneeFiCode;
    private String assigneeFiId;
    private String assigneeFiName;
    private boolean isSubmitted;
    private Date submissionDate;
    private Date createdAt;
    private Date modifiedAt;
    private String comment;
    private List<FiDocumentRequestFileModel> files;
    private List<String> fiObjectTypes;

    public FiDocumentRequestMetaModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getAssigneeFiCode() {
        return assigneeFiCode;
    }

    public void setAssigneeFiCode(String assigneeFiCode) {
        this.assigneeFiCode = assigneeFiCode;
    }

    public String getAssigneeFiName() {
        return assigneeFiName;
    }

    public void setAssigneeFiName(String assigneeFiName) {
        this.assigneeFiName = assigneeFiName;
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }

    public void setSubmitted(boolean submitted) {
        isSubmitted = submitted;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public List<FiDocumentRequestFileModel> getFiles() {
        return files;
    }

    public void setFiles(List<FiDocumentRequestFileModel> files) {
        this.files = files;
    }

    public String getAssigneeFiId() {
        return assigneeFiId;
    }

    public void setAssigneeFiId(String assigneeFiId) {
        this.assigneeFiId = assigneeFiId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getFiObjectTypes() {
        return fiObjectTypes;
    }

    public void setFiObjectTypes(List<String> fiObjectTypes) {
        this.fiObjectTypes = fiObjectTypes;
    }
}
