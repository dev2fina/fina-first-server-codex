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
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.fina.ecm.alfresco.api.common.representation.AbstractRepresentation;

import java.util.Objects;

@JsonTypeName(value = "entry")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteRoleRepresentation extends AbstractRepresentation {
    @JsonProperty("site")
    private SiteRepresentation site = null;

    @JsonProperty("id")
    private String id = null;

    @JsonProperty("guid")
    private String guid = null;

    // TODO RoleEnum ?
    @JsonProperty("role")
    private String role = null;

    public SiteRoleRepresentation() {
    }

    // ///////////////////////////////////////////////////////////////////////////
    // GETTERS & SETTERS
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * Get site
     *
     * @return site
     **/
    public SiteRepresentation getSite() {
        return site;
    }

    public void setSite(SiteRepresentation site) {
        this.site = site;
    }

    /**
     * Get id
     *
     * @return id
     **/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get guid
     *
     * @return guid
     **/
    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    /**
     * Get role
     *
     * @return role
     **/
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public SiteRoleEnum getRoleEnum() {
        return role != null ? SiteRoleEnum.fromString(role) : null;
    }

    public void setRoleEnum(SiteRoleEnum siteRoleEnum) {
        this.role = siteRoleEnum.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SiteRoleRepresentation siteRole = (SiteRoleRepresentation) o;
        return Objects.equals(this.site, siteRole.site) && Objects.equals(this.id, siteRole.id)
                && Objects.equals(this.guid, siteRole.guid) && Objects.equals(this.role, siteRole.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(site, id, guid, role);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SiteRole {, ");

        sb.append("    site: ").append(toIndentedString(site)).append(", ");
        sb.append("    id: ").append(toIndentedString(id)).append(", ");
        sb.append("    guid: ").append(toIndentedString(guid)).append(", ");
        sb.append("    role: ").append(toIndentedString(role)).append(", ");
        sb.append("}");
        return sb.toString();
    }

}
