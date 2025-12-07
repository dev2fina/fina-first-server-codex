package net.fina.server.legalperson.model.connection;

import net.fina.server.legalperson.model.LegalPersonMetaModel;

import java.util.Collection;
import java.util.List;

public class ConnectedCompanyModel {
    private LegalPersonMetaModel source;
    private Collection<CompanyDependencyModel> dependencies;

    public ConnectedCompanyModel() {
    }

    public ConnectedCompanyModel(LegalPersonMetaModel source, Collection<CompanyDependencyModel> dependencies) {
        this.source = source;
        this.dependencies = dependencies;
    }

    public LegalPersonMetaModel getSource() {
        return source;
    }

    public void setSource(LegalPersonMetaModel source) {
        this.source = source;
    }

    public Collection<CompanyDependencyModel> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<CompanyDependencyModel> dependencies) {
        this.dependencies = dependencies;
    }
}
