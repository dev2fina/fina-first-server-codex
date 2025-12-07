package net.fina.ecm.alfresco.api.core.model.representation;

import net.fina.ecm.alfresco.api.common.representation.AbstractRepresentation;

import java.util.Objects;

public class CompanyRepresentation extends AbstractRepresentation {
    private String organization = null;
    private String address1 = null;
    private String address2 = null;
    private String address3 = null;
    private String postcode = null;
    private String telephone = null;
    private String fax = null;
    private String email = null;

    // ///////////////////////////////////////////////////////////////////////////
    // GETTERS & SETTERS
    // ///////////////////////////////////////////////////////////////////////////
    /**
     * Get organization
     *
     * @return organization
     **/
    public String getOrganization()
    {
        return organization;
    }

    public void setOrganization(String organization)
    {
        this.organization = organization;
    }

    /**
     * Get address1
     *
     * @return address1
     **/
    public String getAddress1()
    {
        return address1;
    }

    public void setAddress1(String address1)
    {
        this.address1 = address1;
    }

    /**
     * Get address2
     *
     * @return address2
     **/
    public String getAddress2()
    {
        return address2;
    }

    public void setAddress2(String address2)
    {
        this.address2 = address2;
    }

    /**
     * Get address3
     *
     * @return address3
     **/
    public String getAddress3()
    {
        return address3;
    }

    public void setAddress3(String address3)
    {
        this.address3 = address3;
    }

    /**
     * Get postcode
     *
     * @return postcode
     **/
    public String getPostcode()
    {
        return postcode;
    }

    public void setPostcode(String postcode)
    {
        this.postcode = postcode;
    }

    /**
     * Get telephone
     *
     * @return telephone
     **/
    public String getTelephone()
    {
        return telephone;
    }

    public void setTelephone(String telephone)
    {
        this.telephone = telephone;
    }

    /**
     * Get fax
     *
     * @return fax
     **/
    public String getFax()
    {
        return fax;
    }

    public void setFax(String fax)
    {
        this.fax = fax;
    }

    /**
     * Get email
     *
     * @return email
     **/
    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    @Override
    public boolean equals(java.lang.Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        CompanyRepresentation company = (CompanyRepresentation) o;
        return Objects.equals(this.organization, company.organization)
                && Objects.equals(this.address1, company.address1) && Objects.equals(this.address2, company.address2)
                && Objects.equals(this.address3, company.address3) && Objects.equals(this.postcode, company.postcode)
                && Objects.equals(this.telephone, company.telephone) && Objects.equals(this.fax, company.fax)
                && Objects.equals(this.email, company.email);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(organization, address1, address2, address3, postcode, telephone, fax, email);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("class Company {");

        sb.append("    organization: ").append(toIndentedString(organization)).append(", ");
        sb.append("    address1: ").append(toIndentedString(address1)).append(", ");
        sb.append("    address2: ").append(toIndentedString(address2)).append(", ");
        sb.append("    address3: ").append(toIndentedString(address3)).append(", ");
        sb.append("    postcode: ").append(toIndentedString(postcode)).append(", ");
        sb.append("    telephone: ").append(toIndentedString(telephone)).append(", ");
        sb.append("    fax: ").append(toIndentedString(fax)).append(", ");
        sb.append("    email: ").append(toIndentedString(email));
        sb.append("}");
        return sb.toString();
    }
}
