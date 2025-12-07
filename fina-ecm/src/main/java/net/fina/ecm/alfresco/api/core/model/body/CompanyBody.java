package net.fina.ecm.alfresco.api.core.model.body;

import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

public class CompanyBody implements BaseRepresentation {
    public final String organization;
    public final String address1;
    public final String address2;
    public final String address3;
    public final String postcode;
    public final String telephone;
    public final String fax;
    public final String email;

    public CompanyBody(String organization, String address1, String address2, String address3, String postcode,
                       String telephone, String fax, String email)
    {
        this.organization = organization;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.postcode = postcode;
        this.telephone = telephone;
        this.fax = fax;
        this.email = email;
    }
}
