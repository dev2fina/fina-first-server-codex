package net.fina.server.security.entity;

import net.fina.server.dashboard.dynamic.entity.Dashboard;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserDashboardPK implements Serializable {

    @Column(name = "USER_ID")
    private long userId;

    @OneToOne
    @JoinColumn(name = "DASHBOARD_ID")
    private Dashboard dashboard;

    public UserDashboardPK() {
    }

    public UserDashboardPK(long userId, long dashboardId) {
        this.dashboard = new Dashboard(dashboardId);
        this.userId = userId;
    }


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDashboardPK that = (UserDashboardPK) o;
        return getUserId() == that.getUserId() && (dashboard.equals(that.dashboard));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), dashboard);
    }
}
