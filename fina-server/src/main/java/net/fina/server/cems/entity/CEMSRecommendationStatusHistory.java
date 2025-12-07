package net.fina.server.cems.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity(name = "CEMS_RECOMMENDATIONS_STATUS_HISTORY")
@Table(name = "CEMS_RECOMMENDATIONS_STATUS_HISTORY")
public class CEMSRecommendationStatusHistory {
    @Id
    @SequenceGenerator(name = "cems_recomm_history_sequence", sequenceName = "cems_recomm_history_sequence", allocationSize = 1)
    @GeneratedValue(generator = "cems_recomm_history_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Embedded
    private CEMSRecommendationStatusInfo statusInfo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "RECORD_DATE")
    private Date recordDate;

    @Column(name = "VERSION")
    private int version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECOMMENDATION_ID")
    private CEMSRecommendation recommendation;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CEMSRecommendationStatusInfo getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(CEMSRecommendationStatusInfo statusInfo) {
        this.statusInfo = statusInfo;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public CEMSRecommendation getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(CEMSRecommendation recommendation) {
        this.recommendation = recommendation;
    }
}
