package net.fina.server.inputs.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.filter.PeriodFilter;
import net.fina.common.client.returns.ImportModel;
import net.fina.common.client.returns.ReturnTypeModel;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fi.entity.FiType;
import net.fina.server.inputs.api.InputManagerLocal;
import net.fina.server.inputs.model.InputManagerFiMetaModel;
import net.fina.server.inputs.model.InputManagerFilterMetaModel;
import net.fina.server.inputs.model.InputManagerMainMetaModel;
import net.fina.server.inputs.model.InputManagerMainModelType;
import net.fina.server.inputs.util.ModelUtil;
import net.fina.server.inputs.util.UploadedFileStatusesExportUtil;
import net.fina.server.jcr.impl.FileContentManagementSession;
import net.fina.server.returns.api.ImportLocal;
import net.fina.server.returns.api.PeriodLocal;
import net.fina.server.returns.api.ReturnTypeLocal;
import net.fina.server.returns.entity.ImportedReturn;
import net.fina.server.returns.entity.Period;
import net.fina.server.returns.entity.Schedule;
import net.fina.server.returns.model.PeriodMetaModel;
import net.fina.server.returns.model.helper.PeriodModelHelper;
import net.fina.server.security.api.UserLocal;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@SecurityDomain("FinASecurityDomain")
@RolesAllowed(PermissionIdNames.INPUT_MANAGER_REVIEW)
public class InputManagerProxySession {

    @EJB
    private InputManagerLocal inputManagerLocal;

    @EJB
    private ImportLocal importLocal;

    @EJB
    private UserLocal userLocal;

    @EJB
    private FiLocal fiLocal;

    @EJB
    private PeriodLocal periodLocal;

    @EJB
    private ReturnTypeLocal returnTypeLocal;

    @EJB
    private FileContentManagementSession fileSession;

    @SuppressWarnings("unchecked")
    public PaginatedListWrapper<InputManagerMainMetaModel> load(String nodeId, InputManagerFilterMetaModel filter, long langId) {
        PaginatedListWrapper<InputManagerMainMetaModel> listWrapper = new PaginatedListWrapper<>();

        listWrapper.setPageSize(filter.getLimit());
        listWrapper.setCurrentPage(filter.getPage());
        listWrapper.setTotalResults(inputManagerLocal.countDistinctSchedules(
                (Map<String, Object>) filter.getFilterMap().get("schedule")));

        if (nodeId.equals("root")) {
            listWrapper.setList(loadDistinctSchedules(filter.getStart(), filter.getLimit(),
                    (Map<String, Object>) filter.getFilterMap().get("schedule"), langId));
        } else {
            long[] params = Arrays.stream(nodeId.split("-")).mapToLong(Long::parseLong).toArray();

            if (params.length == 1) {
                Map<String, Object> fiFilter = (Map<String, Object>) filter.getFilterMap().get("fi");

                List<Long> fiTypeIds = fiFilter != null && fiFilter.get("fiTypes") != null
                        ? ((List<Integer>) fiFilter.get("fiTypes")).stream().map(Long::valueOf).collect(Collectors.toList())
                        : null;

                listWrapper.setList(loadFiTypes(nodeId, params, fiTypeIds, langId));
            } else if (params.length == 2) {
                Map<String, Object> fiFilter = (Map<String, Object>) filter.getFilterMap().get("fi");

                List<Long> fiIds = fiFilter != null && fiFilter.get("fis") != null
                        ? ((List<Integer>) fiFilter.get("fis")).stream().map(Long::valueOf).collect(Collectors.toList())
                        : null;

                listWrapper.setList(loadFis(nodeId, params, fiIds, langId));
            } else if (params.length == 3) {
                listWrapper.setList(loadImportedFiles(nodeId, params, (Map<String, Object>) filter.getFilterMap().get("file"), langId));
            } else if (params.length == 4) {
                listWrapper.setList(loadImportedReturns(nodeId, params, (Map<String, Object>) filter.getFilterMap().get("xml")));
            }
        }

        return listWrapper;
    }

    public ImportModel getImportedXml(long xmlId) {
        return ModelUtil.importedReturnToImportModel(importLocal.findById((int) xmlId), null);
    }

    public byte[] getImportedXmlContent(long xmlId) {
        return fileSession.loadImportedReturnContent(importLocal.findById((int) xmlId));
    }

    public byte[] exportUploadFileStatuses(List<String> returnTypeCodeList, List<Long> fiIdList, Date periodFrom, Date periodTo, String periodTypeCode, long langId) throws IOException {

        // Load Periods
        Map<PeriodFilter, Object> periodFilter = new HashMap<>();
        periodFilter.put(PeriodFilter.from, periodFrom);
        periodFilter.put(PeriodFilter.to, periodTo);
        periodFilter.put(PeriodFilter.type, periodTypeCode);
        List<Period> periods = periodLocal.load(periodFilter);
        periods.sort(Comparator.comparing(Period::getFromDate));
        List<PeriodMetaModel> periodModels = PeriodModelHelper.toModel(periods);

        // Load FIs
        List<Fi> fis = fiLocal.load(new HashMap<>());
        if (fiIdList != null && !fiIdList.isEmpty()) {
            fis = fis.stream().filter(fi -> fiIdList.contains(fi.getId())).collect(Collectors.toList());
        }
        // Filter By Permissions
        fis = fis.stream().filter(fi -> userLocal.getCallerPrincipal().getFis().contains(fi.getId())).collect(Collectors.toList());
        List<InputManagerFiMetaModel> fiModels = ModelUtil.getFiModels(fis, langId);

        // Load Upload Data
        Map<ReturnTypeModel, List<Object[]>> dataMap = new HashMap<>();

        List<Object[]> data = inputManagerLocal.loadFileUploadData(returnTypeCodeList,
                periodFrom, periodTo, periodTypeCode, fiIdList);

        // Create Data Map
        for (String returnTypeCode : returnTypeCodeList) {
            ReturnTypeModel returnType = ModelUtil.getReturnTypeModel(returnTypeLocal.getReturnTypeByCode(returnTypeCode), langId);
            List<Object[]> returnTypeData = data.stream().filter(obj -> obj[4].equals(returnTypeCode)).collect(Collectors.toList());
            dataMap.put(returnType, returnTypeData);
        }

        return UploadedFileStatusesExportUtil.exportUploadedFileStatuses(dataMap, periodModels, fiModels);
    }

    private List<InputManagerMainMetaModel> loadDistinctSchedules(int start, int limit, Map<String, Object> filter, long langId) {
        List<InputManagerMainMetaModel> models = new ArrayList<>();

        for (Schedule schedule : inputManagerLocal.loadDistinctSchedules(start, limit, filter)) {
            models.add(new InputManagerMainMetaModel("" + schedule.getId(), InputManagerMainModelType.SCHEDULE,
                    ModelUtil.scheduleEntityToModel(schedule, langId), null, null, null, null));
        }

        return models;
    }

    private List<InputManagerMainMetaModel> loadFiTypes(String nodeId, long[] params, List<Long> fiTypeFilter, long langId) {
        List<InputManagerMainMetaModel> models = new ArrayList<>();
        Schedule schedule = inputManagerLocal.getScheduleById(params[0]);

        for (FiType fiType : inputManagerLocal.loadFiTypes(schedule.getPeriod().getId(), schedule.getDelay(), schedule.getDelayHour(), schedule.getDelayMinute(), fiTypeFilter)) {
            models.add(new InputManagerMainMetaModel(nodeId + "-" + fiType.getId(), InputManagerMainModelType.FI_TYPE, null,
                    ModelUtil.getFiTypeModel(fiType, langId), null, null, null));
        }

        return models;
    }

    private List<InputManagerMainMetaModel> loadFis(String nodeId, long[] params, List<Long> fiFilter, long langId) {
        List<InputManagerMainMetaModel> models = new ArrayList<>();
        Schedule schedule = inputManagerLocal.getScheduleById(params[0]);

        for (Fi fi : inputManagerLocal.loadFis(schedule.getPeriod().getId(), schedule.getDelay(), params[1], fiFilter, userLocal.getCurrentUserId())) {
            models.add(new InputManagerMainMetaModel(nodeId + "-" + fi.getId(), InputManagerMainModelType.FI, null,
                    null, ModelUtil.getFiModel(fi, langId), null, null));
        }

        return models;
    }

    private List<InputManagerMainMetaModel> loadImportedFiles(String nodeId, long[] params, Map<String, Object> fileFilter, long langId) {
        Period period = inputManagerLocal.getScheduleById(params[0]).getPeriod();
        Fi fi = inputManagerLocal.getFiById(params[2]);

        List<InputManagerMainMetaModel> models = new ArrayList<>();
        for (UploadFile uploadFile : inputManagerLocal.loadUploadedFiles(fi.getCode(), period, fileFilter)) {
            models.add(new InputManagerMainMetaModel(nodeId + "-" + uploadFile.getId(), InputManagerMainModelType.FILE,
                    null, null, null, ModelUtil.getFileUploadModel(uploadFile, langId), null));
        }

        return models;
    }

    private List<InputManagerMainMetaModel> loadImportedReturns(String nodeId, long[] params, Map<String, Object> filter) {
        List<InputManagerMainMetaModel> models = new ArrayList<>();
        UploadFile file = inputManagerLocal.getUploadedFileById(params[3]);

        for (ImportedReturn importedReturn : inputManagerLocal.loadImportedReturns(file.getId(), filter)) {
            ImportModel importModel = ModelUtil.importedReturnToImportModel(importedReturn, file);

            models.add(new InputManagerMainMetaModel(nodeId + "-" + importModel.getId(), InputManagerMainModelType.XML,
                    null, null, null, null, importModel));
        }

        return models;
    }

}
