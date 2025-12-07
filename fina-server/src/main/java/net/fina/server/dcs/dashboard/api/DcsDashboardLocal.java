package net.fina.server.dcs.dashboard.api;

import net.fina.server.returns.entity.Schedule;

import java.util.Date;
import java.util.List;

public interface DcsDashboardLocal {

    long countUserNotifications();

    long countUserReceivedNotifications();

    long countUserMessages();

    long countUserReceivedMessages();

    long countFiles(Date fromDate, Date toDate, List<Long> fiIds, boolean submitted);

    List<Schedule> loadNotSubmittedFiles(Date fromDate, Date toDate, List<Long> fiIds);
}
