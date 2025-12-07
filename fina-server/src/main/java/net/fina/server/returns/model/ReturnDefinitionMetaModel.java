package net.fina.server.returns.model;

import net.fina.common.shared.i18n.DescriptionMetaModel;
import net.fina.server.i18n.model.DescriptionModelHelper;
import net.fina.server.returns.entity.DefinitionTable;
import net.fina.server.returns.entity.ReturnDefinition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ReturnDefinitionMetaModel implements Serializable {
    private long id;
    private Integer version;
    private String code;
    private List<DescriptionMetaModel> descriptions;
    private Collection<DefinitionTableMetaModel> definitionTables;

    public ReturnDefinitionMetaModel setEntity(ReturnDefinition definition) {
        this.id = definition.getId();
        this.version = definition.getVersion();
        this.code = definition.getCode();

        this.descriptions = DescriptionModelHelper.toModel(definition.getDescription());

        this.definitionTables = new ArrayList<>();
        this.definitionTables.addAll(definition.getDefinitionTables().stream().sorted(Comparator.comparingLong(DefinitionTable::getSequence)).map(dt -> new DefinitionTableMetaModel().setEntity(dt)).collect(Collectors.toList()));

        return this;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<DescriptionMetaModel> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<DescriptionMetaModel> descriptions) {
        this.descriptions = descriptions;
    }

    public Collection<DefinitionTableMetaModel> getDefinitionTables() {
        return definitionTables;
    }

    public void setDefinitionTables(Collection<DefinitionTableMetaModel> definitionTables) {
        this.definitionTables = definitionTables;
    }
}
