package net.fina.ecm.alfresco.api.search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.UserInfoRepresentation;

import java.util.Date;
import java.util.Objects;

/**
 * ResultNodeRepresentation
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultNodeRepresentation extends NodeRepresentation implements BaseRepresentation {
    private SearchEntry search = null;

    private UserInfoRepresentation archivedByUser = null;

    private Date archivedAt = null;

    private String versionLabel = null;

    private String versionComment = null;

    public ResultNodeRepresentation() {
    }

    public ResultNodeRepresentation search(SearchEntry search) {
        this.search = search;
        return this;
    }

    /**
     * Get search
     *
     * @return search
     **/
    public SearchEntry getSearch() {
        return search;
    }

    public void setSearch(SearchEntry search) {
        this.search = search;
    }

    public ResultNodeRepresentation archivedByUser(UserInfoRepresentation archivedByUser) {
        this.archivedByUser = archivedByUser;
        return this;
    }

    /**
     * Get archivedByUser
     *
     * @return archivedByUser
     **/
    public UserInfoRepresentation getArchivedByUser() {
        return archivedByUser;
    }

    public void setArchivedByUser(UserInfoRepresentation archivedByUser) {
        this.archivedByUser = archivedByUser;
    }

    public ResultNodeRepresentation archivedAt(Date archivedAt) {
        this.archivedAt = archivedAt;
        return this;
    }

    /**
     * Get archivedAt
     *
     * @return archivedAt
     **/
    public Date getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(Date archivedAt) {
        this.archivedAt = archivedAt;
    }

    public ResultNodeRepresentation versionLabel(String versionLabel) {
        this.versionLabel = versionLabel;
        return this;
    }

    /**
     * Get versionLabel
     *
     * @return versionLabel
     **/
    public String getVersionLabel() {
        return versionLabel;
    }

    public void setVersionLabel(String versionLabel) {
        this.versionLabel = versionLabel;
    }

    public ResultNodeRepresentation versionComment(String versionComment) {
        this.versionComment = versionComment;
        return this;
    }

    /**
     * Get versionComment
     *
     * @return versionComment
     **/
    public String getVersionComment() {
        return versionComment;
    }

    public void setVersionComment(String versionComment) {
        this.versionComment = versionComment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResultNodeRepresentation resultNodeRepresentation = (ResultNodeRepresentation) o;
        return super.equals(resultNodeRepresentation)
                && Objects.equals(this.search, resultNodeRepresentation.search)
                && Objects.equals(this.archivedByUser, resultNodeRepresentation.archivedByUser)
                && Objects.equals(this.archivedAt, resultNodeRepresentation.archivedAt)
                && Objects.equals(this.versionLabel, resultNodeRepresentation.versionLabel)
                && Objects.equals(this.versionComment, resultNodeRepresentation.versionComment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(search, archivedByUser, archivedAt, versionLabel, versionComment);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ResultNodeRepresentation {, ");

        sb.append("    search: ").append(toIndentedString(search)).append(", ");
        sb.append("    archivedByUser: ").append(toIndentedString(archivedByUser)).append(", ");
        sb.append("    archivedAt: ").append(toIndentedString(archivedAt)).append(", ");
        sb.append("    versionLabel: ").append(toIndentedString(versionLabel)).append(", ");
        sb.append("    versionComment: ").append(toIndentedString(versionComment)).append(", ");
        sb.append("}");
        return sb.toString();
    }

}
