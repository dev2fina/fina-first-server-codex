package net.fina.server.dcs.dashboard;

import net.fina.common.client.fis.FiModel;
import net.fina.common.shared.dashboard.CommunicationStatisticsMetaModel;
import net.fina.common.shared.dashboard.ReturnStatisticsMetaModel;
import net.fina.common.shared.dashboard.NotSubmittedFileMetaModel;
import net.fina.common.shared.dashboard.UserDataMetaModel;
import net.fina.server.dcs.dashboard.api.DcsDashboardLocal;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.entity.Fi;
import net.fina.server.returns.entity.Schedule;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.Permission;
import net.fina.server.security.entity.User;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class DcsDashboardProxySession {

    @EJB
    private DcsDashboardLocal dcsDashboardLocal;

    @EJB
    private UserLocal userLocal;

    @EJB
    private FiLocal fiLocal;


    public CommunicationStatisticsMetaModel getCommunicationStatistics() {
        CommunicationStatisticsMetaModel model = new CommunicationStatisticsMetaModel();

        model.setIncomingNotifications(dcsDashboardLocal.countUserReceivedNotifications());
        model.setOutgoingNotifications(dcsDashboardLocal.countUserNotifications());
        model.setIncomingMessages(dcsDashboardLocal.countUserReceivedMessages());
        model.setOutgoingMessages(dcsDashboardLocal.countUserMessages());

        return model;
    }

    public UserDataMetaModel getUserData(long langId) {
        UserDataMetaModel model = new UserDataMetaModel();

        User user = userLocal.getCurrentUser();
        model.setLogin(user.getLogin());
        model.setName(user.getDescription().getDescription(langId));

        Permission externalUserPermission = new Permission();
        externalUserPermission.setIdName("fina2.web.external.user");

        if (!user.getPermissions().contains(externalUserPermission))
            return model;

        List<String> fis = fiLocal.loadFiCodes(user.getLogin());
        if (!fis.isEmpty()) {
            FiModel fiModel = new FiModel();
            Fi fi = fiLocal.findFiByCode(fis.get(0));

            fiModel.setId(fi.getId());
            fiModel.setCode(fi.getCode());
            fiModel.setName(fi.getDescription().getDescription(langId));
            fiModel.setShortNameString(fi.getShortName().getDescription(langId));

            model.setFi(fiModel);
        }

        return model;
    }

    public List<NotSubmittedFileMetaModel> loadNotSubmittedFiles(Long periodFrom, Long periodTo, long langId) {
        List<Long> userFis = userLocal.getUserFis(userLocal.getCurrentUserId());
        List<Schedule> schedules = dcsDashboardLocal.loadNotSubmittedFiles(
                periodFrom != null && periodFrom > 0 ? new Date(periodFrom) : null,
                periodTo != null && periodTo > 0 ? new Date(periodTo) : null, userFis);

        List<NotSubmittedFileMetaModel> result = new ArrayList<>();
        for (Schedule s : schedules) {
            NotSubmittedFileMetaModel model = new NotSubmittedFileMetaModel();

            model.setBankCode(s.getFi().getCode());
            model.setBankName(s.getFi().getDescription().getDescription(langId));
            model.setDelay(s.getDelay());
            model.setDelayHour(s.getDelayHour());
            model.setDelayMinute(s.getDelayMinute());
            model.setPeriodFrom(s.getPeriod().getFromDate());
            model.setPeriodTo(s.getPeriod().getToDate());
            model.setPeriodType(s.getPeriod().getPeriodType().getPeriodType());

            result.add(model);
        }

        return result;
    }

    public ReturnStatisticsMetaModel getReturnStatistics(Long periodFrom, Long periodTo) {
        ReturnStatisticsMetaModel model = new ReturnStatisticsMetaModel();

        List<Long> userFis = userLocal.getUserFis(userLocal.getCurrentUserId());
        Date fromDate = periodFrom != null && periodFrom > 0 ? new Date(periodFrom) : null;
        Date toDate = periodTo != null && periodTo > 0 ? new Date(periodTo) : null;

        model.setNotSubmittedFiles(dcsDashboardLocal.countFiles(fromDate, toDate, userFis, false));
        model.setSubmittedFiles(dcsDashboardLocal.countFiles(fromDate, toDate, userFis, true));

        return model;
    }

    public Long getSubmittedFilesCount() {
        return getReturnStatistics(null, null).getSubmittedFiles();
    }

}
