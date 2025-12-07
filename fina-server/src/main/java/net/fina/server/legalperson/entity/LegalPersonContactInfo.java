package net.fina.server.legalperson.entity;

import net.fina.server.fi.entity.Region;

import jakarta.persistence.*;
import java.util.Objects;

@Entity(name = "IN_LEGAL_PERSON_CONTACT_INFO")
@Table(name = "IN_LEGAL_PERSON_CONTACT_INFO")
public class LegalPersonContactInfo {
    @Id
    @SequenceGenerator(name = "legal_person_contact_info_sequence", sequenceName = "legal_person_contact_info_sequence", allocationSize = 1)
    @GeneratedValue(generator = "legal_person_contact_info_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @OneToOne
    @JoinColumn(name = "REGION_ID")
    private Region region;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "WEB_SITE")
    private String webSite;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LegalPersonContactInfo that = (LegalPersonContactInfo) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
