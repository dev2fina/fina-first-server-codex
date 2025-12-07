package net.fina.server.reg.entity;

import jakarta.persistence.*;

@Entity(name = "IN_REG_FILE_SCHEDULES")
@Table(name = "IN_REG_FILE_SCHEDULES")
public class RegFileSchedule {

    @Id
    @SequenceGenerator(name = "reg_file_schedules_sequence", sequenceName = "reg_file_schedules_sequence", allocationSize = 1)
    @GeneratedValue(generator = "reg_file_schedules_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "FILE_ID")
    private long fileId;
    @Column(name = "SCHEDULE_ID")
    private long scheduleId;

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
