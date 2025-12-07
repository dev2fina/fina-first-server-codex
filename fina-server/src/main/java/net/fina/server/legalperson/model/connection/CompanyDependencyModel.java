package net.fina.server.legalperson.model.connection;

import net.fina.server.legalperson.entity.ConnectedCOmpanyConnectionType;
import net.fina.server.legalperson.model.LegalPersonMetaModel;

import java.util.Objects;

public class CompanyDependencyModel {
    LegalPersonMetaModel legalPerson;
    ConnectedCOmpanyConnectionType dependencyType;

    public CompanyDependencyModel(LegalPersonMetaModel legalPerson, ConnectedCOmpanyConnectionType dependencyType) {
        this.legalPerson = legalPerson;
        this.dependencyType = dependencyType;
    }


    public LegalPersonMetaModel getLegalPerson() {
        return legalPerson;
    }

    public void setLegalPerson(LegalPersonMetaModel legalPerson) {
        this.legalPerson = legalPerson;
    }

    public ConnectedCOmpanyConnectionType getDependencyType() {
        return dependencyType;
    }

    public void setDependencyType(ConnectedCOmpanyConnectionType dependencyType) {
        this.dependencyType = dependencyType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyDependencyModel that = (CompanyDependencyModel) o;
        return Objects.equals(getLegalPerson(), that.getLegalPerson());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLegalPerson());
    }
}
