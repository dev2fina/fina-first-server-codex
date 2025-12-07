package net.fina.server.reports.entity;

import net.fina.auditlog.api.Audited;

import java.io.Serializable;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

@Entity(name = "RESULT_VIEW")
@Table(name = "RESULT_VIEW")
@Immutable
public class ResultView implements Serializable, Audited {
    @EmbeddedId
    private ResultViewPk viewPk;

    public ResultViewPk getViewPk() {
        return viewPk;
    }

    public void setViewPk(ResultViewPk viewPk) {
        this.viewPk = viewPk;
    }

    @Override
    public String toString() {
        return "ResultView [viewPk=" + viewPk + "]";
    }

}
