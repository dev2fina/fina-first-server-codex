package net.fina.common.shared.ecm.model;

import java.io.Serializable;

public class ECMCapabilitiesMetaModel implements Serializable {
    private boolean isGuest;
    private boolean isAdmin;
    private boolean isMutable;

    public ECMCapabilitiesMetaModel() {
    }

    public ECMCapabilitiesMetaModel(boolean isGuest, boolean isAdmin, boolean isMutable) {
        this.isGuest = isGuest;
        this.isAdmin = isAdmin;
        this.isMutable = isMutable;
    }

    public boolean isGuest() {
        return isGuest;
    }

    public void setGuest(boolean guest) {
        isGuest = guest;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isMutable() {
        return isMutable;
    }

    public void setMutable(boolean mutable) {
        isMutable = mutable;
    }
}
