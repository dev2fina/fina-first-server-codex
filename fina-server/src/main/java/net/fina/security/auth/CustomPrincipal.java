package net.fina.security.auth;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomPrincipal implements Principal, Serializable {
    protected long id;
    protected Collection<Long> fis;
    protected Collection<Long> returnDefinitions;
    protected Map<Long, Boolean> returnVersions;
    protected Collection<Integer> reports;
    protected List<Long> roles;
    protected String name;


    public CustomPrincipal(long id, String name, Collection<Long> fis, Collection<Long> returnDefinitions, Map<Long, Boolean> returnVersions, Collection<Integer> reports, List<Long> roles) {
        this.id = id;
        this.name = name;
        this.fis = fis;
        this.returnDefinitions = returnDefinitions;
        this.returnVersions = returnVersions;
        this.reports = reports;
        this.roles = roles;
    }

    public Collection<Long> getFis() {
        return fis;
    }

    public Collection<Long> getReturnDefinitions() {
        return returnDefinitions;
    }

    public Map<Long, Boolean> getReturnVersions() {
        return returnVersions;
    }

    public Collection<Integer> getReports() {
        return reports;
    }

    public List<Long> getRoles() {
        return roles;
    }


    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object another) {
        if (!(another instanceof Principal))
            return false;
        String anotherName = ((Principal) another).getName();
        boolean equals = false;
        if (name == null)
            equals = anotherName == null;
        else
            equals = name.equals(anotherName);
        return equals;
    }

    @Override
    public int hashCode() {
        return (name == null ? 0 : name.hashCode());
    }

    @Override
    public String toString() {
        return name;
    }
}
