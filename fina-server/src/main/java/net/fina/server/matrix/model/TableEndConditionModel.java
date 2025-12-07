package net.fina.server.matrix.model;

public class TableEndConditionModel {
    private long id;

    private String column;
    private String condition;

    public TableEndConditionModel() {
    }

    public TableEndConditionModel(long id, String column, String condition) {
        this.id = id;
        this.column = column;
        this.condition = condition;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
