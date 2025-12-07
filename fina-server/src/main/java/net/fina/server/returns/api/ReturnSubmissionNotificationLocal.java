package net.fina.server.returns.api;

import net.fina.server.returns.entity.ReturnSubmissionNotification;
import net.fina.server.returns.entity.Schedule;

import java.util.Date;
import java.util.List;

public interface ReturnSubmissionNotificationLocal {

    void save(ReturnSubmissionNotification returnNotification);

    List<Schedule> getOverdueReturnSchedules(Date currentDate, int daysBeforeDueDate);

    List<Schedule> getNotSubmittedReturnSchedules();

    List<Schedule> getActiveSubmissionSchedules(Date currentDate);

}
