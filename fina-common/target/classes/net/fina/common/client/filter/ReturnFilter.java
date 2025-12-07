package net.fina.common.client.filter;

public class ReturnFilter {
    private Criteria criteria;
    private Object value;

    public ReturnFilter(Criteria criteria, Object value) {
        this.criteria = criteria;
        this.value = value;
    }

    public enum Criteria {
        FROM,
        TO,
        TYPE,
        LIMIT,
        OFFSET,
        LANG_ID
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
