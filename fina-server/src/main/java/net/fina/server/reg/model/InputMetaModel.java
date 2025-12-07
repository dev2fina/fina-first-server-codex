package net.fina.server.reg.model;


import net.fina.server.classifier.model.MDTCatalogMetaModel;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.model.ComparisonItem;

import java.io.Serializable;
import java.util.*;

public class InputMetaModel implements Serializable {

    protected String code;

    protected String description;

    protected String column;

    protected String type;

    protected InputTypeEnum typeEnum;

    protected int length;

    protected Integer precision;

    protected boolean optional;

    protected String defaultValue;

    protected List<ComparisonItem> comparisons;

    private Set<String> listElementItems;
    private MDTCatalogMetaModel catalog;
    private MDTNode mdtNode;

    private boolean key;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public InputTypeEnum getTypeEnum() {
        return typeEnum;
    }

    public void setTypeEnum(InputTypeEnum typeEnum) {
        this.typeEnum = typeEnum;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }


    public List<ComparisonItem> getComparisons() {
        return comparisons == null ? new ArrayList<>() : comparisons;
    }

    public void setComparisons(List<ComparisonItem> comparisons) {
        this.comparisons = comparisons;
    }

    public Set<String> getListElementItems() {
        return listElementItems == null ? new HashSet<>() : listElementItems;
    }

    public void setListElementItems(Set<String> listElementItems) {
        this.listElementItems = listElementItems;
    }

    public MDTNode getMdtNode() {
        return mdtNode;
    }

    public void setMdtNode(MDTNode mdtNode) {
        this.mdtNode = mdtNode;
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }

    public MDTCatalogMetaModel getCatalog() {
        return catalog;
    }

    public void setCatalog(MDTCatalogMetaModel catalog) {
        this.catalog = catalog;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputMetaModel that = (InputMetaModel) o;
        return Objects.equals(getCode(), that.getCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode());
    }
}
