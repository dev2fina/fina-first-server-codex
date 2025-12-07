package net.fina.server.returns.proxy;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.constants.CalendarPeriodType;
import net.fina.common.client.filter.OverdueReturnFilter;
import net.fina.common.client.returns.OverdueReturnModel;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.dcs.uploadfile.entity.SortInfo;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.api.RegionLocal;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fi.entity.Region;
import net.fina.server.returns.api.ReturnLocal;
import net.fina.server.returns.api.ScheduleLocal;
import net.fina.server.util.RegionUtil;
import net.fina.common.server.StatisticsLogger;
import org.jboss.ejb3.annotation.SecurityDomain;

import java.text.SimpleDateFormat;
import java.util.*;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class OverdueReturnProxySession {

    @Inject
    private ReturnLocal returnLocal;
    @Inject
    private ScheduleLocal scheduleLocal;
    @Inject
    private RegionLocal regionLocal;
    @Inject
    private FiLocal fiLocal;

    public PaginatedListWrapper<OverdueReturnModel> load(int offset, int pageSize, CalendarPeriodType periodType, Date fromDate, Date toDate, long fiType, String fiCode, Integer dueDate, String sortField, String sortDir) {
        try (StatisticsLogger statLog = new StatisticsLogger("Load Overdue Returns")) {

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            long langId = ThreadLocalHolder.getLanguage().getId();

            sortField = sortField == null || sortField.trim().isEmpty() ? "toDate" : sortField;
            sortDir = sortDir == null || sortDir.trim().isEmpty() ? "DESC" : sortDir.toUpperCase();

            if (offset < 0) {
                offset = 0;
            }
            // filter map
            Map<OverdueReturnFilter, Object> filterObjectMap = new HashMap<>();
            filterObjectMap.put(OverdueReturnFilter.from, fromDate);
            filterObjectMap.put(OverdueReturnFilter.to, toDate);
            filterObjectMap.put(OverdueReturnFilter.periodType, periodType);

            if (fiType > 0) {
                filterObjectMap.put(OverdueReturnFilter.fiType, fiType);
            }
            if (fiCode != null && !fiCode.trim().isEmpty()) {
                filterObjectMap.put(OverdueReturnFilter.fiCode, fiCode);
            }
            if (dueDate != null && dueDate >= 0) {
                filterObjectMap.put(OverdueReturnFilter.dueDate, dueDate);
            }

            // overdue returns
            statLog.logStage("Load Overdue Submitted Returns");
            SortInfo sortInfo = new SortInfo(sortField, sortDir);
            List<OverdueReturnModel> overdueReturnModels = returnLocal.loadOverdueReturnModels(filterObjectMap, langId, offset, pageSize, sortInfo);


            statLog.logStage("Load Overdue Schedules");
            List<OverdueReturnModel> schedules = scheduleLocal.loadNotSubmittedReturnModels(fromDate, toDate != null ? toDate : new Date(), periodType, fiType, fiCode, sortInfo);

            if (offset + pageSize >= overdueReturnModels.size() || (offset == 0 && pageSize <= 0)) {
                RegionUtil regionUtil = new RegionUtil();
                Map<Long, Region> regionIds = regionLocal.loadRegionsIdMap();

                int length = pageSize <= 0 ? schedules.size() : offset + pageSize - overdueReturnModels.size();
                int start = offset == 0 ? 0 : Math.max(length - pageSize, 0);
                for (int i = start; i < length && i < schedules.size(); i++) {
                    OverdueReturnModel model = schedules.get(i);
                    model.setDelay(scheduleLocal.getOverdueNumberOfDays(model.getDueDate(), model.getDueDateHour(), model.getDueDateMinute(), model.getToDate(), new Date()));
                    Fi fi = fiLocal.findFiByCode(model.getFiCode());
                    if (fi != null && fi.getId() > 0) {
                        model.setName(model.getFiCode() + " | " + df.format(model.getFromDate()) + " - " + df.format(model.getToDate()));
                        model.setAddress(fi.getAddressDescription().getDescription(langId));
                        model.setFiName(fi.getDescription().getDescription(langId));
                        model.setFiType(fi.getFiType().getCode());
                        model.setRegion(regionUtil.getFullRegionName(regionIds, fi.getRegionId(), langId, null));
                        model.setFiLegalForm(fi.getLegalForm());
                        model.setFiIdentificationCode(fi.getIdentificationCode());
                    }
                }
            }
            overdueReturnModels.addAll(schedules);

            if (!sortField.trim().isEmpty()) {
                sortOverdueReturnModels(overdueReturnModels, sortField, sortDir);
            }
            int total = overdueReturnModels.size();
            int limit = total;

            if (pageSize > 0) {
                limit = Math.min(offset + pageSize, limit);
            }

            List<OverdueReturnModel> result = new ArrayList<>();
            if (offset < overdueReturnModels.size()) {
                result.addAll(overdueReturnModels.subList(offset, limit));
            }


            return new PaginatedListWrapper<>(result, pageSize, total);
        }

    }

    private void sortOverdueReturnModels(List<OverdueReturnModel> overdueReturnModels, String sortField, String sortDir) {
        Comparator<OverdueReturnModel> comparator = (o1, o2) -> switch (sortField) {
            case "fiCode" -> o1.getFiCode().compareTo(o2.getFiCode());
            case "fiType" -> o1.getFiType().compareTo(o2.getFiType());
            case "fromDate" -> o1.getFromDate().compareTo(o2.getFromDate());
            case "toDate" -> o1.getToDate().compareTo(o2.getToDate());
            case "dueDate" -> Long.compare(o1.getDueDate(), o2.getDueDate());
            case "periodType" -> o1.getPeriodType().compareTo(o2.getPeriodType());
            case "delay" -> Integer.compare(o1.getDelay(), o2.getDelay());
            default -> 0;
        };

        if ("DESC".equalsIgnoreCase(sortDir)) {
            overdueReturnModels.sort(comparator.reversed());
        } else if ("ASC".equalsIgnoreCase(sortDir)) {
            overdueReturnModels.sort(comparator);
        }
    }

}
