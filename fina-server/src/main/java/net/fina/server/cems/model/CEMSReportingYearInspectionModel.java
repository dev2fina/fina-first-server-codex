package net.fina.server.cems.model;

import net.fina.common.shared.user.UserModel;
import net.fina.common.shared.user.UserModelSimple;

import java.util.Date;

public class CEMSReportingYearInspectionModel {
    private long id;
    private Date startDate;
    private Date endDate;
    private UserModelSimple manager;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public UserModelSimple getManager() {
        return manager;
    }

    public void setManager(UserModelSimple manager) {
        this.manager = manager;
    }
}
