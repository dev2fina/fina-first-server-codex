package net.fina.server.fi.entity;

import net.fina.auditlog.api.Audited;

import java.io.Serializable;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity(name = "MM_BANK_GROUP")
@Table(name = "MM_BANK_GROUP")
@SuppressWarnings("serial")
public class MMBankGroup implements Serializable, Audited {
    @EmbeddedId
    private MMBankGroupPk bankGroupPK;

    public MMBankGroupPk getBankGroupPK() {
        return bankGroupPK;
    }

    public void setBankGroupPK(MMBankGroupPk bankGroupPK) {
        this.bankGroupPK = bankGroupPK;
    }

}
