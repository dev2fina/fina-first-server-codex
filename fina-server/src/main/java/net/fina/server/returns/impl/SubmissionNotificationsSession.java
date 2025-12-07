package net.fina.server.returns.impl;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import net.fina.common.client.constants.CommunicatorNotificationStatus;
import net.fina.common.client.constants.CommunicatorReadStatus;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.messages.MessagesUtil;
import net.fina.server.communicator.api.CommunicatorLocal;
import net.fina.server.communicator.entity.CommunicatorNotification;
import net.fina.server.communicator.entity.CommunicatorNotificationUser;
import net.fina.server.communicator.entity.UserNotificationId;
import net.fina.server.communicator.event.notification.CommunicatorNotificationEvent;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.notification.proxy.SysNotificationProxySession;
import net.fina.server.returns.api.ReturnSubmissionNotificationLocal;
import net.fina.server.returns.api.ReturnSubmissionNotificationType;
import net.fina.server.returns.entity.ReturnSubmissionNotification;
import net.fina.server.returns.entity.Schedule;
import net.fina.server.security.api.PropertyLocal;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.Permission;
import net.fina.server.security.entity.Role;
import net.fina.server.security.entity.User;
import net.fina.server.sms.api.SmsLocal;
import net.fina.server.sms.entity.Sms;
import net.fina.common.server.StatisticsLogger;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;
import org.stringtemplate.v4.ST;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Stateless
@Interceptors(RecordingAuditor.class)
public class SubmissionNotificationsSession {
    @Inject
    private Event<CommunicatorNotificationEvent> communicatorNotificationEvent;

    @EJB
    private ReturnSubmissionNotificationLocal returnSubmissionNotificationLocalSession;
    @EJB
    private CommunicatorLocal communicatorLocalSession;
    @EJB
    private UserLocal userLocal;
    @EJB
    private PropertyLocal propertyLocal;
    @EJB
    private SmsLocal smsLocal;

    @Inject
    private Logger log;

    @Inject
    private SysNotificationProxySession sysNotificationProxySession;


    @TransactionTimeout(unit = TimeUnit.MINUTES, value = 10)
    public void sendOverdueReturnNotifications(int daysBeforeDueDate) throws FinATypeException {
        try (StatisticsLogger logger = new StatisticsLogger("SUBMISSION_NOTIFICATION_TASK")) {

            List<Schedule> schedules = returnSubmissionNotificationLocalSession.getOverdueReturnSchedules(new Date(), daysBeforeDueDate);

            logger.logMessage("Start generating notifications, Schedules size (" + schedules.size() + ")");

            User sa = getUser();

            Date currentDate = new Date();
            ResourceBundle rb = MessagesUtil.loadMessageBundle(
                    propertyLocal.getSystemProperty(PropertyKeys.DEFAULT_LANGUAGE)
            );

            logger.logStage("Sort Schedules");
            sortSchedules(schedules);

            Schedule lastSchedule = null;
            Map<Long, List<User>> fiUsersMap = new HashMap<>();
            Permission externalUserPermission = new Permission();
            externalUserPermission.setIdName("fina2.web.external.user");
            ST stringTemplate = new ST(rb.getString("net.fina.server.return.overdueSubmissionNotificationContent"));
            String content = stringTemplate.add("due", String.valueOf(daysBeforeDueDate)).render();

            CommunicatorNotification notification = createOverdueSubmissionNotification(content, currentDate, sa, rb.getString("net.fina.server.return.overdueSubmissionNotificationTitle"));
            Set<CommunicatorNotificationUser> notificationUsers = new HashSet<>();
            logger.logStage("Start  persist " + ReturnSubmissionNotificationType.OVERDUE.name() + " notifications");

            for (Schedule schedule : schedules) {
                List<User> fiExternalUsers = getUsersByPermission(externalUserPermission, schedule, fiUsersMap);

                if (!fiExternalUsers.isEmpty()) {
                    if (lastSchedule == null || schedule.getReturnDefinition().getReturnType().getId() != lastSchedule.getReturnDefinition().getReturnType().getId()
                            || !lastSchedule.getFi().equals(schedule.getFi()) || (lastSchedule.getDelay() + lastSchedule.getDelayHour() + lastSchedule.getDelayMinute()) != (schedule.getDelay() + schedule.getDelayHour() + schedule.getDelayMinute())
                            || !lastSchedule.getPeriod().equals(schedule.getPeriod())) {

                        notificationUsers.addAll(notificationUsers(fiExternalUsers, notification));
                        saveSubmissionSms(content, currentDate, fiExternalUsers);

                    }
                    lastSchedule = schedule;
                    // remember the fact that notification belongs to schedule
                    returnSubmissionNotificationLocalSession.save(new ReturnSubmissionNotification(schedule, ReturnSubmissionNotificationType.OVERDUE));
                }

            }

            if (!notificationUsers.isEmpty()) {
                notification.setNotificationUsers(notificationUsers);
                communicatorLocalSession.saveNotification(notification);
            }

            if (lastSchedule != null) {
                // now notify the clients about that:
                communicatorNotificationEvent.fire(new CommunicatorNotificationEvent(null));
            }
        }

    }

    @TransactionTimeout(unit = TimeUnit.MINUTES, value = 10)
    public void sendSubmissionPeriodStartedNotifications() throws FinATypeException {
        try (StatisticsLogger logger = new StatisticsLogger("SUBMISSION_NOTIFICATION_TASK")) {

            List<Schedule> schedules = returnSubmissionNotificationLocalSession.getActiveSubmissionSchedules(new Date());
            logger.logMessage("Start generating notifications, Schedules size (" + schedules.size() + ")");

            User sa = getUser();

            Date currentDate = new Date();
            ResourceBundle rb = MessagesUtil.loadMessageBundle(
                    propertyLocal.getSystemProperty(PropertyKeys.DEFAULT_LANGUAGE)
            );

            logger.logStage("Sort Schedules");
            sortSchedules(schedules);

            Schedule lastSchedule = null;
            CommunicatorNotification notification = createOverdueSubmissionNotification("", currentDate, sa, rb.getString("net.fina.server.return.activeSubmissionNotificationTitle"));
            Set<CommunicatorNotificationUser> notificationUsers = new HashSet<>();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            Map<Long, List<User>> fiUsersMap = new HashMap<>();
            Permission externalUserPermission = new Permission();
            externalUserPermission.setIdName("fina2.web.external.user");

            logger.logStage("Start  persist " + ReturnSubmissionNotificationType.SUBMISSION_PERIOD.name() + " notifications");
            for (Schedule schedule : schedules) {
                List<User> fiExternalUsers = getUsersByPermission(externalUserPermission, schedule, fiUsersMap);

                if (!fiExternalUsers.isEmpty()) {
                    if (lastSchedule == null || schedule.getReturnDefinition().getReturnType().getId() != lastSchedule.getReturnDefinition().getReturnType().getId()
                            || !lastSchedule.getFi().equals(schedule.getFi()) || (lastSchedule.getDelay() + lastSchedule.getDelayHour() + lastSchedule.getDelayMinute()) != (schedule.getDelay() + schedule.getDelayHour() + schedule.getDelayMinute())
                            || !lastSchedule.getPeriod().equals(schedule.getPeriod())) {

                        ST stringTemplate = new ST(rb.getString("net.fina.server.return.activeSubmissionNotificationContent"));
                        String content = stringTemplate.add("fromDate", dateFormatter.format(schedule.getPeriod().getFromDate()))
                                .add("toDate", dateFormatter.format(schedule.getPeriod().getToDate()))
                                .add("due", String.valueOf(schedule.getDelay()))
                                .render();
                        notification.setContent(content);


                        notificationUsers.addAll(notificationUsers(fiExternalUsers, notification));
                        saveSubmissionSms(content, currentDate, fiExternalUsers);

                    }
                    lastSchedule = schedule;
                    // remember the fact that notification belongs to schedule
                    returnSubmissionNotificationLocalSession.save(new ReturnSubmissionNotification(schedule, ReturnSubmissionNotificationType.SUBMISSION_PERIOD));
                }

            }

            if (!notificationUsers.isEmpty()) {
                notification.setNotificationUsers(notificationUsers);
                communicatorLocalSession.saveNotification(notification);
            }
            if (lastSchedule != null) {
                // now notify the clients about that:
                communicatorNotificationEvent.fire(new CommunicatorNotificationEvent(null));
            }
        }

    }

    @TransactionTimeout(unit = TimeUnit.MINUTES, value = 10)
    public void sendNotSubmittedReturnNotifications() throws FinATypeException {
        try (StatisticsLogger logger = new StatisticsLogger("SUBMISSION_NOTIFICATION_TASK")) {

            List<Schedule> schedules = returnSubmissionNotificationLocalSession.getNotSubmittedReturnSchedules();

            logger.logMessage("Start generating notifications, Schedules size (" + schedules.size() + ")");

            User sa = getUser();

            Date currentDate = new Date();
            ResourceBundle rb = MessagesUtil.loadMessageBundle(
                    propertyLocal.getSystemProperty(PropertyKeys.DEFAULT_LANGUAGE)
            );

            logger.logStage("Sort Schedules");
            sortSchedules(schedules);

            Schedule lastSchedule = null;
            Map<Long, List<User>> fiUsersMap = new HashMap<>();
            Permission externalUserPermission = new Permission();
            externalUserPermission.setIdName("fina2.web.external.user");

            Permission internalUserPermission = new Permission();
            internalUserPermission.setIdName("fina2.web.internal.user");

            CommunicatorNotification notification = createOverdueSubmissionNotification("", currentDate, sa, rb.getString("net.fina.server.return.notSubmittedNotificationTitle"));
            Set<CommunicatorNotificationUser> notificationUsers = new HashSet<>();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

            logger.logStage("Start  persist " + ReturnSubmissionNotificationType.NOT_SUBMITTED.name() + " notifications");
            for (Schedule schedule : schedules) {


                List<User> fiUsers;
                List<User> fiExternalUsers = new ArrayList<>();
                List<User> fiInternalUsers = new ArrayList<>();

                if (fiUsersMap.get(schedule.getFi().getId()) == null) {
                    fiUsers = userLocal.loadFiUsers(schedule.getFi().getId());
                    fiUsersMap.put(schedule.getFi().getId(), fiUsers);
                } else {
                    fiUsers = fiUsersMap.get(schedule.getFi().getId());
                }

                // filter external users
                for (User user : fiUsers) {
                    if (user.getPermissions().contains(internalUserPermission)) {
                        fiInternalUsers.add(user);
                    } else if (user.getPermissions().contains(externalUserPermission)) {
                        fiExternalUsers.add(user);
                    } else {
                        for (Role role : user.getRoles()) {
                            if (role.getPermissions().contains(internalUserPermission)) {
                                fiInternalUsers.add(user);
                                break;
                            } else if (role.getPermissions().contains(externalUserPermission)) {
                                fiExternalUsers.add(user);
                                break;
                            }
                        }
                    }
                }

                logger.logStage("External Users : " + fiExternalUsers.size());
                logger.logStage("Internal Users : " + fiInternalUsers.size());

                if (!fiExternalUsers.isEmpty() || !fiInternalUsers.isEmpty()) {
                    if (lastSchedule == null || schedule.getReturnDefinition().getReturnType().getId() != lastSchedule.getReturnDefinition().getReturnType().getId()
                            || !lastSchedule.getFi().equals(schedule.getFi()) || (lastSchedule.getDelay() + lastSchedule.getDelayHour() + lastSchedule.getDelayMinute()) != (schedule.getDelay() + schedule.getDelayHour() + schedule.getDelayMinute())
                            || !lastSchedule.getPeriod().equals(schedule.getPeriod())) {

                        ST stringTemplate = new ST(rb.getString("net.fina.server.return.notSubmittedNotificationContent"));
                        String content = stringTemplate
                                .add("fromDate", dateFormatter.format(schedule.getPeriod().getFromDate()))
                                .add("toDate", dateFormatter.format(schedule.getPeriod().getToDate())).render();


                        if (!fiExternalUsers.isEmpty()) {
                            logger.logStage("Saving Notifications");
                            notification.setContent(content);

                            notificationUsers.addAll(notificationUsers(fiExternalUsers, notification));
                            saveSubmissionSms(content, currentDate, fiExternalUsers);
                        }

                        sysNotificationProxySession.notifyUsers(fiInternalUsers, content);
                    }
                    lastSchedule = schedule;
                    // remember the fact that notification belongs to schedule
                    returnSubmissionNotificationLocalSession.save(new ReturnSubmissionNotification(schedule, ReturnSubmissionNotificationType.NOT_SUBMITTED));
                }

            }
            if (!notificationUsers.isEmpty()) {
                notification.setNotificationUsers(notificationUsers);
                communicatorLocalSession.saveNotification(notification);
            }

            if (lastSchedule != null) {
                // now notify the clients about that:
                communicatorNotificationEvent.fire(new CommunicatorNotificationEvent(null));
            }
        }

    }

    private CommunicatorNotification createOverdueSubmissionNotification(String content, Date currentDate, User sa, String title) {
        CommunicatorNotification notification = new CommunicatorNotification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setCreationDate(currentDate);
        notification.setPublishDate(currentDate);
        notification.setUser(sa);
        notification.setStatus(CommunicatorNotificationStatus.PUBLISHED);
        notification.setAutomatic(true);

        return notification;
    }

    private Collection<CommunicatorNotificationUser> notificationUsers(List<User> users, CommunicatorNotification notification) {
        List<CommunicatorNotificationUser> userList = new ArrayList<>();
        for (User user : users) {
            UserNotificationId userNotificationId = new UserNotificationId();

            userNotificationId.setUser(user);
            userNotificationId.setNotification(notification);

            CommunicatorNotificationUser communicatorNotificationUser = new CommunicatorNotificationUser();
            communicatorNotificationUser.setUserNotificationId(userNotificationId);
            communicatorNotificationUser.setStatus(CommunicatorReadStatus.SENT);

            userList.add(communicatorNotificationUser);
        }
        return userList;
    }

    private void sortSchedules(List<Schedule> schedules) {
        schedules.sort((o1, o2) -> {
            if (o1.getFi().getId() != o2.getFi().getId()) {
                return ((Long) o1.getFi().getId()).compareTo(o2.getFi().getId());
            }
            if (o1.getPeriod().getId() != o2.getPeriod().getId()) {
                return ((Long) o1.getPeriod().getId()).compareTo(o2.getPeriod().getId());
            }

            if ((o1.getDelay() + o1.getDelayHour() + o1.getDelayMinute()) != (o2.getDelay() + o2.getDelayHour() + o2.getDelayMinute())) {
                return (o1.getDelay() + o1.getDelayHour() + o1.getDelayMinute()) - (o2.getDelay() + o2.getDelayHour() + o2.getDelayHour());
            }

            return ((Long) o1.getReturnDefinition().getReturnType().getId()).compareTo(o2.getReturnDefinition().getReturnType().getId());
        });
    }

    private void saveSubmissionSms(String content, Date currentDate, List<User> users) {
        String serviceEnable = ConfigurationUtil.get().get("SMS.enable");
        if (serviceEnable == null ||
                serviceEnable.isEmpty() ||
                Integer.parseInt(serviceEnable) <= 0
        ) {
            return;
        }

        for (User u : users) {
            Sms sms = new Sms();
            sms.setTitle("Return");
            sms.setContent(content);
            sms.setCreationDate(currentDate);
            sms.setAddress(u.getPhone());
            sms.setRecipient("");

            smsLocal.saveSms(sms);
        }
    }


    private User getUser() {
        try {
            return userLocal.findUserbyLogin(ConfigurationUtil.get().get("SUBMISSION_NOTIFICATION.user"));
        } catch (Throwable t) {
            log.error("Invalid user provided!");
            return null;
        }
    }

    private List<User> getUsersByPermission(Permission permission, Schedule schedule, Map<Long, List<User>> fiUsersMap) {
        List<User> fiUsers;

        if (fiUsersMap.get(schedule.getFi().getId()) == null) {
            fiUsers = userLocal.loadFiUsers(schedule.getFi().getId());
            fiUsersMap.put(schedule.getFi().getId(), fiUsers);
        } else {
            fiUsers = fiUsersMap.get(schedule.getFi().getId());
        }

        List<User> fiUsersResult = new ArrayList<>();

        //filter users by permission
        for (User user : fiUsers) {
            if (user.getPermissions().contains(permission)) {
                fiUsersResult.add(user);
            } else {
                for (Role role : user.getRoles()) {
                    if (role.getPermissions().contains(permission)) {
                        fiUsersResult.add(user);
                        break;
                    }
                }
            }
        }

        return fiUsersResult;
    }
}
