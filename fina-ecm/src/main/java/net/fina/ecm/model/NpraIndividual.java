package net.fina.ecm.model;

import java.io.Serializable;

/**
 * User: Baaka
 * Date: 5/9/2015
 * Time: 2:49 PM
 */
public class NpraIndividual implements Serializable {
    private String npraId;
    private String firstName;
    private String lastName;
    private String recordLink;
    private String folderLink;

    public NpraIndividual() {
    }

    public NpraIndividual(String npraId, String firstName, String lastName, String recordLink, String folderLink) {
        this.npraId = npraId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.recordLink = recordLink;
        this.folderLink = folderLink;
    }

    public String getNpraId() {
        return npraId;
    }

    public void setNpraId(String npraId) {
        this.npraId = npraId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRecordLink() {
        return recordLink;
    }

    public void setRecordLink(String recordLink) {
        this.recordLink = recordLink;
    }

    public String getFolderLink() {
        return folderLink;
    }

    public void setFolderLink(String folderLink) {
        this.folderLink = folderLink;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        NpraIndividual individual = (NpraIndividual) object;
        return npraId == individual.npraId;
    }
}
