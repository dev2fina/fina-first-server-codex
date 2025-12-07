package net.fina.ecm.alfresco.api.core.model.body;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteMembershipBodyUpdate implements BaseRepresentation {
    public final String role;

    /**
     * Update only
     *
     * @param role
     */
    public SiteMembershipBodyUpdate(String role) {
        this.role = role;
    }

}
