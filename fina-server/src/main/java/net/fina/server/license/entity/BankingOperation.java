package net.fina.server.license.entity;

import net.fina.server.i18n.helper.Description;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import java.util.Objects;

@Entity(name = "IN_BANKING_OPERATIONS")
@Table(name = "IN_BANKING_OPERATIONS")
public class BankingOperation {
    @Id
    @SequenceGenerator(name = "banking_operation_sequence", sequenceName = "banking_operation_sequence", allocationSize = 1)
    @GeneratedValue(generator = "banking_operation_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "PARENT_ID")
    private long parentId;
    @Column(name = "CODE")
    private String code;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LICENSE_TYPE_ID")
    private LicenceType licenceType;
    @Column(name = "NAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description description;
    @Column(name = "NATIONAL_CURRENCY")
    private boolean nationalCurrency;
    @Column(name = "FOREIGN_CURRENCY")
    private boolean foreignCurrency;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public boolean isNationalCurrency() {
        return nationalCurrency;
    }

    public void setNationalCurrency(boolean nationalCurrency) {
        this.nationalCurrency = nationalCurrency;
    }

    public boolean isForeignCurrency() {
        return foreignCurrency;
    }

    public void setForeignCurrency(boolean foreignCurrency) {
        this.foreignCurrency = foreignCurrency;
    }

    public LicenceType getLicenceType() {
        return licenceType;
    }

    public void setLicenceType(LicenceType licenceType) {
        this.licenceType = licenceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankingOperation that = (BankingOperation) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
