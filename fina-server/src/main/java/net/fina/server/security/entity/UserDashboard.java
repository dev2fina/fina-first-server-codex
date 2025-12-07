package net.fina.server.security.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity(name = "SYS_USER_DASHBOARDS")
@Table(name = "SYS_USER_DASHBOARDS")
public class UserDashboard {

    @EmbeddedId
    private UserDashboardPK userDashboardPK;

    public UserDashboard() {
    }

    public UserDashboard(UserDashboardPK userDashboardPK) {
        this.userDashboardPK = userDashboardPK;
    }

    public UserDashboardPK getUserDashboardPK() {
        return userDashboardPK;
    }

    public void setUserDashboardPK(UserDashboardPK userDashboardPK) {
        this.userDashboardPK = userDashboardPK;
    }
}
