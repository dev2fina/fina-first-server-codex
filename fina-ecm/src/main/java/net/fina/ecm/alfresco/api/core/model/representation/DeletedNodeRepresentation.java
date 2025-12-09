/*
 *   Copyright (C) 2005-2016 Alfresco Software Limited.
 *
 *   This file is part of Alfresco Java Client.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package net.fina.ecm.alfresco.api.core.model.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeletedNodeRepresentation extends NodeRepresentation {
    @JsonProperty("archivedByUser")
    private UserInfoRepresentation archivedByUser = null;

    @JsonProperty("archivedAt")
    private Date archivedAt = null;

    // ///////////////////////////////////////////////////////////////////////////
    // GETTERS & SETTERS
    // ///////////////////////////////////////////////////////////////////////////

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeletedNodeRepresentation deletedNodeMinimal = (DeletedNodeRepresentation) o;
        return Objects.equals(this.id, deletedNodeMinimal.id)
                && Objects.equals(this.parentId, deletedNodeMinimal.parentId)
                && Objects.equals(this.name, deletedNodeMinimal.name)
                && Objects.equals(this.nodeType, deletedNodeMinimal.nodeType)
                && Objects.equals(this.isFolder, deletedNodeMinimal.isFolder)
                && Objects.equals(this.isFile, deletedNodeMinimal.isFile)
                && Objects.equals(this.modifiedAt, deletedNodeMinimal.modifiedAt)
                && Objects.equals(this.modifiedBy, deletedNodeMinimal.modifiedBy)
                && Objects.equals(this.createdAt, deletedNodeMinimal.createdAt)
                && Objects.equals(this.createdBy, deletedNodeMinimal.createdBy)
                && Objects.equals(this.content, deletedNodeMinimal.content)
                && Objects.equals(this.archivedByUser, deletedNodeMinimal.archivedByUser)
                && Objects.equals(this.archivedAt, deletedNodeMinimal.archivedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, parentId, name, nodeType, isFolder, isFile, modifiedAt, modifiedBy, createdAt,
                createdBy, content, archivedByUser, archivedAt);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DeletedNodeMinimal { ");

        sb.append("    id: ").append(toIndentedString(id)).append(", ");
        sb.append("    parentId: ").append(toIndentedString(parentId)).append(", ");
        sb.append("    name: ").append(toIndentedString(name)).append(", ");
        sb.append("    nodeType: ").append(toIndentedString(nodeType)).append(", ");
        sb.append("    isFolder: ").append(toIndentedString(isFolder)).append(", ");
        sb.append("    isFile: ").append(toIndentedString(isFile)).append(", ");
        sb.append("    modifiedAt: ").append(toIndentedString(modifiedAt)).append(", ");
        sb.append("    modifiedByUser: ").append(toIndentedString(modifiedBy)).append(", ");
        sb.append("    createdAt: ").append(toIndentedString(createdAt)).append(", ");
        sb.append("    createdByUser: ").append(toIndentedString(createdBy)).append(", ");
        sb.append("    content: ").append(toIndentedString(content)).append(", ");
        sb.append("    archivedByUser: ").append(toIndentedString(archivedByUser)).append(", ");
        sb.append("    archivedAt: ").append(toIndentedString(archivedAt));
        sb.append("}");
        return sb.toString();
    }

}
