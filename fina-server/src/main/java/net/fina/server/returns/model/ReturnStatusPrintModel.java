package net.fina.server.returns.model;

import net.fina.common.client.returns.ProcessStatus;
import net.fina.messages.MessagesUtil;
import net.fina.server.fi.util.RegionLevelPrintHelper;

import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReturnStatusPrintModel extends AbstractTableModel {
    private Map<Long, Map<String, Long>> counts;
    private final Map<Long, Map<Long, ProcessStatusDateWrapper>> returnStatuses;
    private final Map<Long, Long> fisAndRegionsMap;
    private final List<Long> fiIds;
    private final List<Long> returnIds;
    private final List<String> fiCodes;
    private final List<String> returnCodes;
    private final List<String> fiNames;

    private final int addedColumns;
    private final int emptyRows = 2;
    private final int legendRows = 12;

    private final RegionLevelPrintHelper regionLevelPrintHelper;

    private final String title;
    private final String langCode;

    public ReturnStatusPrintModel(Map<Long, Map<Long, ProcessStatusDateWrapper>> returnStatuses, List<Long> fiIds, List<Long> returnIds,
                                  List<String> fiCodes, List<String> returnCodes, List<String> fiNames, Map<Long, Long> fisAndRegionsMap,
                                  RegionLevelPrintHelper regionLevelPrintHelper, String title, String langCode) {
        this.returnStatuses = returnStatuses;
        this.fiIds = fiIds;
        this.returnIds = returnIds;
        this.fiCodes = fiCodes;
        this.returnCodes = returnCodes;
        this.fiNames = fiNames;
        this.fisAndRegionsMap = fisAndRegionsMap;
        this.regionLevelPrintHelper = regionLevelPrintHelper;
        this.addedColumns = regionLevelPrintHelper.getLastColumnIndex() + 1;
        this.title = title;
        this.langCode = langCode;
        doCount();
    }

    @Override
    public int getRowCount() {
        return fiIds.size() + emptyRows + legendRows;
    }

    @Override
    public int getColumnCount() {
        return returnIds.size() + regionLevelPrintHelper.getLastColumnIndex() + 1;
    }

    @Override
    public String getColumnName(int column) {
        if (column >= 0 && column < (returnIds.size() + addedColumns)) {
            if (column == 0) {
                return MessagesUtil.getString("net.fina.fiCode", langCode);
            }

            if (column == 1) {
                return MessagesUtil.getString("net.fina.description", langCode);
            }

            if (regionLevelPrintHelper.getHeaderAt(column) != null) {
                return regionLevelPrintHelper.getHeaderAt(column);
            }

            return returnCodes.get(column - addedColumns);
        } else
            return "";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= 0 && rowIndex < fiIds.size() && columnIndex >= 0 && columnIndex < returnIds.size() + addedColumns) {
            if (columnIndex == 0) {
                return fiCodes.get(rowIndex);
            } else if (columnIndex == 1) {
                return fiNames.get(rowIndex);
            } else if (regionLevelPrintHelper.getHeaderAt(columnIndex) != null) {
                Long regionId = fisAndRegionsMap.get(fiIds.get(rowIndex));
                return regionId != null ? regionLevelPrintHelper.getValueAt(columnIndex, regionId) : "N/A";
            } else {
                if (returnStatuses.get(returnIds.get(columnIndex - addedColumns)) != null && returnStatuses.get(returnIds.get(columnIndex - addedColumns)).get(fiIds.get(rowIndex)) != null) {
                    return getStatus(returnStatuses.get(returnIds.get(columnIndex - addedColumns)).get(fiIds.get(rowIndex)));
                } else {
                    return getStatus(null);
                }
            }
        } else if (rowIndex >= fiIds.size() && rowIndex < (fiIds.size() + emptyRows + legendRows) && columnIndex >= 0 && columnIndex < returnIds.size() + addedColumns + 1) {
            if (rowIndex == fiIds.size()) {
                if (columnIndex == (returnIds.size() + addedColumns)) {
                    return MessagesUtil.getString("net.fina.web.returnStatusesPrintServlet.value.total", langCode);
                }
                return "====";
            } else {
                if (columnIndex == 0) {
                    if (rowIndex == fiIds.size() + 1) {
                        return getStatus(null);
                    }
                    ProcessStatusDateWrapper wrapper = new ProcessStatusDateWrapper();
                    wrapper.setProcessStatus(ProcessStatus.values()[(rowIndex - fiIds.size()) - emptyRows]);
                    return getStatus(wrapper);
                } else if (columnIndex == 1) {
                    if (rowIndex == fiIds.size() + 1) {
                        return getDescription(null);
                    }
                    return getDescription(ProcessStatus.values()[(rowIndex - fiIds.size()) - emptyRows]);
                } else if (regionLevelPrintHelper.getHeaderAt(columnIndex) != null) {
                    return "0";
                } else if (columnIndex == (returnIds.size() + addedColumns)) {
                    int sum = 0;
                    for (int i = 2; i < (returnIds.size() + addedColumns); i++) {
                        sum += Integer.parseInt(String.valueOf(getValueAt(rowIndex, i)));
                    }
                    return String.valueOf(sum);
                } else {
                    Map<String, Long> statusCounts = counts.get(returnIds.get(columnIndex - addedColumns));
                    String status = String.valueOf(getValueAt(rowIndex, 0));
                    if (statusCounts == null) {
                        if (status.equals("x")) {
                            return String.valueOf(fiIds.size());
                        } else {
                            return "0";
                        }
                    }

                    long totalCount = 0;
                    for (String key : statusCounts.keySet()) {
                        if (key.startsWith(status + " - [") || key.equals(status)) {
                            totalCount += statusCounts.get(key);
                        }
                    }

                    if (totalCount > 0) {
                        return String.valueOf(totalCount);
                    } else {
                        return getCount(statusCounts, status);
                    }
                }
            }
        }
        return null;
    }

    private String getCount(Map<String, Long> statusCounts, String status) {
        long all = fiIds.size();
        long othersCount = 0;
        for (Long aLong : statusCounts.values()) {
            othersCount += aLong;
        }
        statusCounts.put(status, all - othersCount);
        return String.valueOf(all - othersCount);
    }

    private void doCount() {
        counts = new HashMap<>();

        for (Long definitionId : returnStatuses.keySet()) {
            Map<String, Long> statusCounts = counts.computeIfAbsent(definitionId, k -> new HashMap<>());
            Map<Long, ProcessStatusDateWrapper> fiStatuses = returnStatuses.get(definitionId);
            for (ProcessStatusDateWrapper processStatus : fiStatuses.values()) {
                String st = getStatus(processStatus);
                Long count = statusCounts.get(st);
                statusCounts.put(st, count == null ? 1 : (count + 1));
            }
        }
    }

    private String getDescription(ProcessStatus processStatus) {
        if (processStatus == null) {
            return MessagesUtil.getString("net.fina.web.returnStatusesPrintServlet.value.noData", langCode);
        }
        return MessagesUtil.getString(processStatus.getCode(), langCode);
    }

    private String getStatus(ProcessStatusDateWrapper wrapper) {
        if (wrapper == null) {
            return "x";
        }
        ProcessStatus processStatus = wrapper.getProcessStatus();

        if (processStatus == null) {
            return "x";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        return switch (processStatus) {
            case STATUS_SCHEDULED -> "S";
            case STATUS_CREATED -> "C";
            case STATUS_AMENDED -> "A";
            case STATUS_IMPORTED -> "I";
            case STATUS_PROCESSED -> {
                String status = "P";
                if (wrapper.getStatusDate() != null) {
                    status += " - [" + formatter.format(wrapper.getStatusDate()) + "]";
                }
                yield status;
            }
            case STATUS_VALIDATED -> "VAL";
            case STATUS_RESETED -> "R";
            case STATUS_ACCEPTED -> {
                String status = "V";
                if (wrapper.getStatusDate() != null) {
                    status += " - [" + formatter.format(wrapper.getStatusDate()) + "]";
                }
                yield status;
            }
            case STATUS_REJECTED -> "-";
            case STATUS_ERRORS -> "?";
            case STATUS_LOADED -> "L";
            case STATUS_QUEUED -> "Q";
        };
    }


    public String getTitle() {
        return title;
    }
}