package net.fina.server.cems.model;

public class CEMSSanctionedEmployeeInfoModel {
    private long id;
    private String employeeName;
    private String employeeId;
    private String employeePosition;

    public CEMSSanctionedEmployeeInfoModel() {
    }

    public CEMSSanctionedEmployeeInfoModel(long id, String employeeName, String employeeId, String employeePosition) {
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
