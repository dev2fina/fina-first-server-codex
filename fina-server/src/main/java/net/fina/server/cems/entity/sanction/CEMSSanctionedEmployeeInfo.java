package net.fina.server.cems.entity.sanction;

import jakarta.persistence.*;

@Entity(name = "CEMS_SANCTIONED_EMPLOYEES")
@Table(name = "CEMS_SANCTIONED_EMPLOYEES")
public class CEMSSanctionedEmployeeInfo {
    @Id
    @SequenceGenerator(name = "cems_sanctioned_empl_sequence", sequenceName = "cems_sanctioned_empl_sequence", allocationSize = 1)
    @GeneratedValue(generator = "cems_sanctioned_empl_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "EMPLOYEE_NAME")
    private String employeeName;

    @Column(name = "EMPLOYEE_ID")
    private String employeeId;

    @Column(name = "EMPLOYEE_POSITION")
    private String employeePosition;

    public CEMSSanctionedEmployeeInfo() {
    }

    public CEMSSanctionedEmployeeInfo(long id, String employeeName, String employeeId, String employeePosition) {
        this.id = id;
        this.employeeName = employeeName;
        this.employeeId = employeeId;
        this.employeePosition = employeePosition;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeePosition() {
        return employeePosition;
    }

    public void setEmployeePosition(String employeePosition) {
        this.employeePosition = employeePosition;
    }
}
