package net.fina.server.legalperson.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.server.fi.model.RegionMetaModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LegalPersonContactInfoMetaModel {
    private long id;
    private RegionMetaModel citizenship;
    private String address;
    private String phone;
    private String webSite;


    public LegalPersonContactInfoMetaModel() {
    }

    public LegalPersonContactInfoMetaModel(RegionMetaModel citizenship, String address, String phone, String webSite) {
        this.citizenship = citizenship;
        this.address = address;
        this.phone = phone;
        this.webSite = webSite;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RegionMetaModel getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(RegionMetaModel citizenship) {
        this.citizenship = citizenship;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }
}
