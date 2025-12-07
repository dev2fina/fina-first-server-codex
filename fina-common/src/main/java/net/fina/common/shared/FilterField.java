package net.fina.common.shared;

public class FilterField {
    private String property;
    private Object value;
    private String operator;
    private FilterType type;

    public FilterField() {
    }

    public FilterField(String property, String value) {
        this.property = property;
        this.value = value;
    }

    public FilterField(String property, String value, String operator) {
        this.property = property;
        this.value = value;
        this.operator = operator;
    }

    public FilterField(String property, String value, String operator, FilterType type) {
        this.property = property;
        this.value = value;
        this.operator = operator;
        this.type = type;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public FilterType getType() {
        return type;
    }

    public void setType(FilterType type) {
        this.type = type;
    }
}
