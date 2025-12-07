package net.fina.server.matrix.entity;


import jakarta.persistence.*;

@Entity(name = "SYS_MATRIX_TABLE_CONDITION")
@Table(name = "SYS_MATRIX_TABLE_CONDITION")
public class TableEndCondition {

    @Id
    @SequenceGenerator(name = "matrix_table_condition_sequence", sequenceName = "matrix_table_condition_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "matrix_table_condition_sequence")
    private long id;

    @Column(name = "COLUMN_NAME", nullable = false)
    private String column;
    @Column(name = "CONDITION")
    private String condition;

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
        return condition == null ? "" : condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
