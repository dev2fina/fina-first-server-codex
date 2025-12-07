package net.fina.server.fi.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@SuppressWarnings("serial")
public class MMBankGroupPk implements Serializable {
    @Column(name = "BANKID")
    private long bankId;
    @Column(name = "BANKGROUPID")
    private long bankGroupId;

    public long getBankId() {
        return bankId;
    }

    public void setBankId(long bankId) {
        this.bankId = bankId;
    }

    public long getBankGroupId() {
        return bankGroupId;
    }

    public void setBankGroupId(long bankGroupId) {
        this.bankGroupId = bankGroupId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (bankGroupId ^ (bankGroupId >>> 32));
        result = prime * result + (int) (bankId ^ (bankId >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MMBankGroupPk other = (MMBankGroupPk) obj;
        if (bankGroupId != other.bankGroupId)
            return false;
        if (bankId != other.bankId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "MMBankGroupPk{" +
                "bankId=" + bankId +
                ", bankGroupId=" + bankGroupId +
                '}';
    }
}
