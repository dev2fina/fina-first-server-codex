package net.fina.server.reports.proxy;

import fina2.period.PeriodPK;
import fina2.reportoo.ReportInfo;
import fina2.returns.ReturnDefinitionTablePK;
import fina2.ui.sheet.openoffice.OOIterator;
import fina2.ui.sheet.openoffice.OOParameter;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJBException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.exception.OfficeTypeException;
import net.fina.common.client.filter.PeriodFilter;
import net.fina.common.client.filter.ReportFilter;
import net.fina.common.client.filter.ReturnDefinitionFilter;
import net.fina.common.client.reports.ReportConstants;
import net.fina.common.client.reports.ReportType;
import net.fina.common.client.reports.ScheduledReportInfo;
import net.fina.common.server.util.ObjectSerializer;
import net.fina.common.shared.ContentModel;
import net.fina.common.shared.fi.CriterionMetaModel;
import net.fina.common.shared.report.*;
import net.fina.odstoolkit.AbstractFactory;
import net.fina.odstoolkit.FactoryProducer;
import net.fina.odstoolkit.writer.AooWriterBase;
import net.fina.report.core.ReportUtil;
import net.fina.report.model.Parameter;
import net.fina.reporting.impl.ExcelOoIteratorHelper;
import net.fina.reporting.model.*;
import net.fina.reporting.util.ReportingUtil;
import net.fina.security.api.AuthorizationLocal;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.aoo.AOOServiceManager;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.api.PeerGroupLocal;
import net.fina.server.fi.entity.Criterion;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fi.entity.PeerGroup;
import net.fina.server.fi.model.FiModelHelper;
import net.fina.server.fi.model.PeerGroupModelHelper;
import net.fina.server.i18n.helper.Description;
import net.fina.server.i18n.model.DescriptionModelHelper;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.mdt.model.helper.MdtNodeModelHelper;
import net.fina.server.reports.api.ReportLocal;
import net.fina.server.reports.api.StoredReportLocal;
import net.fina.server.reports.entity.Report;
import net.fina.server.reports.entity.ReportTemplate;
import net.fina.server.reports.entity.StoredReport;
import net.fina.server.reports.entity.StoredReportPk;
import net.fina.server.reports.impl.ReportGeneratorProcessor;
import net.fina.server.reports.impl.ReportSession;
import net.fina.server.reports.model.ReportGeneratorResult;
import net.fina.server.reports.model.ReportPairModel;
import net.fina.server.reports.model.ReportingMetaModelHelper;
import net.fina.server.reports.util.ReportDimensionHelper;
import net.fina.server.reports.util.ReportPrintUtil;
import net.fina.server.returns.api.PeriodLocal;
import net.fina.server.returns.api.ReturnDefinitionLocal;
import net.fina.server.returns.api.ReturnTypeLocal;
import net.fina.server.returns.api.ReturnVersionLocal;
import net.fina.server.returns.entity.DefinitionTable;
import net.fina.server.returns.entity.Period;
import net.fina.server.returns.entity.ReturnVersion;
import net.fina.server.returns.model.ReturnDefinitionMetaModel;
import net.fina.server.returns.model.ReturnTypeMetaModel;
import net.fina.server.returns.model.helper.PeriodModelHelper;
import net.fina.server.returns.model.helper.ReturnVersionModelHelper;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.User;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.fina.common.client.exception.FinATypeException.Type.DEPENDENCY_ERROR;
import static net.fina.common.client.exception.FinATypeException.Type.GENERAL_ERROR;
import static net.fina.report.model.Iterator.PLAIN_VCT_ITERATOR;
import static net.fina.report.model.Iterator.VCT_ITERATOR;

@Stateless
@PermitAll
public class ReportProxySession {

    private final Logger log = Logger.getLogger(getClass().getName());
    private final String reportGenerationSessionAttributePrefix = "REPORT_GENERATION_";
    @Inject
    private ReportLocal reportLocal;
    @Inject
    private StoredReportLocal storedReportLocal;
    @Inject
    private PeerGroupLocal peerGroupLocal;
    @Inject
    private ReturnDefinitionLocal returnDefinitionLocal;
    @Inject
    private ReturnTypeLocal returnTypeLocal;
    @Inject
    private UserLocal userLocal;
    @Inject
    private AuthorizationLocal authorizationLocal;
    @Inject
    private FiLocal fiLocal;
    @Inject
    private PeriodLocal periodLocal;
    @Inject
    private ReportGeneratorProcessor reportGeneratorProcessor;
    @Inject
    private AOOServiceManager aooServiceManager;
    @Inject
    private MDTNodeLocal mdtNodeLocal;
    @Inject
    private ReturnVersionLocal returnVersionLocal;

    public ReportMetaModel saveReportWithTemplate(ReportMetaModel reportMetaModel, byte[] fileContent) throws Throwable {
        Report report = new Report();
        report.setId(reportMetaModel.getId());
        report.setParentId(reportMetaModel.getParentId());
        report.setVersion(reportMetaModel.getVersion());

        report.setCode(reportMetaModel.getCode());

        report.setReportType(reportMetaModel.getReportType());

        report.setType(reportMetaModel.getType());
        report.setTemplate(fileContent);
        report.setInfo(ObjectSerializer.serialize(ReportInfoModelHelper.get(reportMetaModel)));
        report.setSequence(reportMetaModel.getSequence());

        Description description = new Description();
        description.setNameStrId(reportMetaModel.getNameStrId());
        description.getDescriptions().put(reportMetaModel.getLangId(), reportMetaModel.getDescription());
        report.setDescription(description);

        if (reportMetaModel.getId() > 0) {
            Report currentReport = reportLocal.findById(reportMetaModel.getId());
            report.setDescription(currentReport.getDescription());
            report.getDescription().addDescription(reportMetaModel.getLangId(), reportMetaModel.getDescription());
        }

        reportLocal.save(report, reportMetaModel.getLangId());

        reportMetaModel.setId(report.getId());
        reportMetaModel.setNameStrId(report.getDescription().getNameStrId());

        return reportMetaModel;
    }

    @Transactional(rollbackOn = FinATypeException.class)
    public ReportMetaModel saveReport(ReportMetaModel model) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();
        if (model.getId() == 0) {
            Description description = new Description(langId, model.getNameStrId(), model.getDescription());
            Report report = reportLocal.createEmptyReport(model.getParentId(), model.getCode(), description, model.getSequence(), model.getType(), model.getReportType());
            model = ReportingMetaModelHelper.toModel(report, langId);
        } else {
            Report report = reportLocal.findById(model.getId());
            report.setCode(model.getCode());
            report.setDescription(new Description(langId, model.getNameStrId(), model.getDescription()));
            report.setSequence(model.getSequence());
            model = ReportingMetaModelHelper.toModel(reportLocal.save(report, langId), langId);
        }
        return model;
    }

    public ReportMetaModel getReport(int reportId, long langId) {
        return ReportingMetaModelHelper.toModel(reportLocal.findById(reportId), langId);
    }

    public ReportMetaModel saveEmptyReport(ReportMetaModel reportMetaModel) throws FinATypeException {
        Description description = new Description();
        description.setNameStrId(reportMetaModel.getNameStrId());
        description.getDescriptions().put(reportMetaModel.getLangId(), reportMetaModel.getDescription());

        Report report = reportLocal.createEmptyReport(reportMetaModel.getParentId(), reportMetaModel.getCode(), description, reportMetaModel.getSequence(), reportMetaModel.getType(), reportMetaModel.getReportType());
        reportMetaModel.setId(report.getId());
        reportMetaModel.setNameStrId(report.getDescription().getNameStrId());

        return reportMetaModel;
    }

    public List<ReportMetaModel> loadReports(int parentId, ReportType reportType, boolean skipEmptyFolders) {
        long langId = ThreadLocalHolder.getLanguage().getId();

        Map<ReportFilter, Object> filter = new HashMap<>();
        filter.put(ReportFilter.REPORT_TYPE, reportType);

        if (reportType == null) {
            filter.put(ReportFilter.REPORT_TYPE, Arrays.asList(ReportType.DEFAULT, ReportType.EXCEL));
        }

        if (skipEmptyFolders) {
            return loadNonemptyNodesByParentId(parentId, filter, langId);
        }

        filter.put(ReportFilter.FOLDER_ID, parentId);

        List<Report> reports = reportLocal.load(filter);
        return ReportingMetaModelHelper.toModels(reports, langId);

    }

    public List<ReportMetaModel> loadReports(long parentId, long langId, ReportType reportType, boolean skipEmptyFolders) {
        Map<ReportFilter, Object> filter = new HashMap<>();
        filter.put(ReportFilter.REPORT_TYPE, reportType);

        if (skipEmptyFolders) {
            return loadNonemptyNodesByParentId((int) parentId, filter, langId);
        }

        filter.put(ReportFilter.FOLDER_ID, parentId);
        return ReportingMetaModelHelper.toModels(reportLocal.load(filter), langId);
    }


    public ReportTemplate loadReportTemplate(int reportId, long langId) {
        return reportLocal.loadReportTemplates(reportId, langId);

    }

    public ReportInfoMetaModel getReportInfoMetaModel(int reportId) {
        ReportInfoMetaModel result = new ReportInfoMetaModel();
        try {
            Report report = reportLocal.findById(reportId);
            ReportInfo reportInfo = ReportUtil.getReportInfo(report.getInfo());
            result = ReportInfoModelHelper.get(reportInfo);
        } catch (Exception t) {
            log.error(t.getMessage(), t);
        }
        return result;
    }

    public CriterionMetaModel loadCriterionByid(long id) {
        Criterion criterion = peerGroupLocal.loadCriterionById(id);
        CriterionMetaModel criterionMetaModel = new CriterionMetaModel();
        criterionMetaModel.setId(criterion.getId());
        criterionMetaModel.setCode(criterion.getCode());
        criterionMetaModel.setDescriptions(DescriptionModelHelper.toModel(criterion.getDescription()));
        criterionMetaModel.setVersion(criterion.getVersion());
        criterionMetaModel.setDefaultGroup(criterion.getIsDefault());
        return criterionMetaModel;
    }

    public String getReportPath(int reportId) {
        return reportLocal.getReportPath(reportId);
    }

    public List<ReportMetaModel> searchReports(String text, long langId, ReportType reportType) {
        if (text == null) {
            return new ArrayList<>();
        }
        Map<ReportFilter, Object> filter = new HashMap<>();
        filter.put(ReportFilter.REPORT_TYPE, reportType);
        filter.put(ReportFilter.LOAD_ALL, true);


        List<ReportMetaModel> reports = ReportingMetaModelHelper.toModels(reportLocal.load(filter), langId);

        return reports.stream().filter(r -> (r.getCode() != null && r.getCode().toLowerCase().contains(text.toLowerCase())) || (r.getDescription() != null && r.getDescription().toLowerCase().contains(text.toLowerCase()))).collect(Collectors.toList());

    }

    public ReportMetaModel updateReport(ReportMetaModel metaModel) throws FinATypeException {

        Report tmp = new Report();
        tmp.setId(metaModel.getId());
        tmp.setCode(metaModel.getCode());

        if (!reportLocal.checkIsCodeUnique(tmp)) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE);
        }

        Report report = reportLocal.findById(metaModel.getId());
        report.setCode(metaModel.getCode());
        report.getDescription().addDescription(metaModel.getLangId(), metaModel.getDescription());
        reportLocal.save(report, metaModel.getLangId());

        return metaModel;
    }

    public void deleteReports(List<Integer> reportIds) throws FinATypeException {
        for (Integer reportId : reportIds) {
            Report report = reportLocal.findById(reportId);
            if (report.getType() == 1) {
                if (!reportLocal.loadChildReportId(reportId).isEmpty()) {
                    throw new FinATypeException(DEPENDENCY_ERROR, new String[]{"Report Folder Has Children"});
                }
            }
        }

        reportLocal.delete(reportIds.toArray(new Integer[0]));
    }

    public void deleteExcelReport(int reportId) throws FinATypeException {
        Report report = reportLocal.findById(reportId);

        if (report.getType() == 1) {
            reportLocal.deleteEmptyReportFolder(reportId);
            return;
        } else if (report.getReportType() != ReportType.EXCEL) {
            throw new FinATypeException(GENERAL_ERROR);
        }
        reportLocal.delete(reportId);
    }

    public List<ReturnTypeMetaModel> loadReturnTypes() {
        return returnTypeLocal.loadReturnTypes().stream().map(item -> new ReturnTypeMetaModel().setEntity(item)).collect(Collectors.toList());
    }

    public byte[] getReturnTypeFormat(long returnTypeId) throws Exception {
        byte[] format = returnTypeLocal.getFormat(returnTypeId);
        return format == null ? ReportingUtil.createEmptySpreadsheetDocument() : format;
    }

    public void saveReturnTypeFormat(long returnTypeId, byte[] format) {
        returnTypeLocal.saveFormat(returnTypeId, format);
    }

    public List<ReturnDefinitionMetaModel> loadReturnDefinitions(Long returnTypeId) {
        Map<ReturnDefinitionFilter, Object> filterObjectMap = new HashMap<>();
        filterObjectMap.put(ReturnDefinitionFilter.LOAD_All, true);
        if (returnTypeId != null) {
            filterObjectMap.put(ReturnDefinitionFilter.RETURN_TYPE_ID, returnTypeId);
        }
        return returnDefinitionLocal.load(filterObjectMap).stream().map(item -> new ReturnDefinitionMetaModel().setEntity(item)).collect(Collectors.toList());
    }


    public List<ReportPairModel> loadUserReportsTreeMap(long userId, boolean foldersOnly, boolean loadAll) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        Map<ReportFilter, Object> filter = new HashMap<>();
        if (foldersOnly) {
            filter.put(ReportFilter.TYPE, ReportConstants.NODETYPE_FOLDER);
        } else {
            filter.put(ReportFilter.LOAD_ALL, loadAll);
        }

        List<ReportMetaModel> reportModels = ReportingMetaModelHelper.toModels(reportLocal.load(filter), langId);
        List<ReportPairModel> reportPairList = new ArrayList<>();

        List<Integer> userRoleReportIds = userLocal.loadUserRoleReportIds(userId);
        List<Integer> userReportIds = userLocal.loadUserReportIds(userId);

        for (ReportMetaModel reportModel : reportModels) {

            //Check root nodes
            if (reportModel.getParentId() == 0) {
                processReportTreeAndSetUserPermissionsRecursive(userRoleReportIds, userReportIds, reportModels, reportPairList, reportModel);
            }

        }

        return reportPairList;
    }


    public List<ReportPairModel> loadRoleReportsTreeMap(long roleId, boolean foldersOnly) {
        Map<ReportFilter, Object> filter = new HashMap<>();
        if (foldersOnly) {
            filter.put(ReportFilter.TYPE, ReportConstants.NODETYPE_FOLDER);
        } else {
            filter.put(ReportFilter.LOAD_ALL, Boolean.TRUE);
        }
        long langId = ThreadLocalHolder.getLanguage().getId();

        List<ReportMetaModel> reportModels = ReportingMetaModelHelper.toModels(reportLocal.load(filter), langId);
        List<ReportPairModel> reportPairList = new ArrayList<>();

        List<Integer> roleReportIds = userLocal.loadRoleReportIds(roleId);


        for (ReportMetaModel reportModel : reportModels) {

            //Check root nodes
            if (reportModel.getParentId() == 0) {
                processReportTreeAndSetUserPermissionsRecursive(null, roleReportIds, reportModels, reportPairList, reportModel);
            }

        }

        return reportPairList;
    }


    public List<GeneratedReportMetaModel> loadStatistics(int reportId, String reportName) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        List<ScheduledReportInfo> result = new ArrayList<>(storedReportLocal.loadStatistics(reportId, langId, reportName));
        final List<GeneratedReportMetaModel> models = new ArrayList<>();
        for (ScheduledReportInfo s : result) {
            models.add(new GeneratedReportMetaModel(s.getReportId(), s.getName(), s.getCreatorUser(), s.getCount(), s.getScheduleTime(), s.getReportInfoHashCode(), langId));
        }
        return models;
    }

    public ReportInfoDetailModel loadReportInfo(int reportId) {
        Report r = reportLocal.findById(reportId);
        return getReportIteratorsModel(r.getId(), r.getInfo());
    }

    public ReportParameterModel getReportParameter(int reportId, String paramName) throws FinATypeException {
        Report r = reportLocal.findById(reportId);
        ReportInfo reportInfo = deserializeReportInfo(reportId, r.getInfo());
        if (reportInfo == null) {
            return null;
        }

        ReportParameterModel result = new ReportParameterModel();
        result.setName(paramName);


        Parameter param = reportInfo.parameters.values().stream().filter(p -> p.getName().equals(paramName)).findFirst().orElseGet(null);
        result.setType(ReportParameterType.fromId(param.getType()));

        if (param.getValues() != null && !param.getValues().isEmpty()) {
            result.setValues(loadParameterOriginalValues(param.getType(), param.getValues()));
        }

        return result;
    }


    public ReportDimensionModel getReportDimension(int reportId, String paramName) throws FinATypeException {
        Report r = reportLocal.findById(reportId);
        ReportInfo reportInfo = deserializeReportInfo(reportId, r.getInfo());
        if (reportInfo == null) {
            return null;
        }

        ReportDimensionModel result = new ReportDimensionModel();
        result.setName(paramName);

        net.fina.report.model.Iterator dimension = reportInfo.iterators.values().stream().filter(p -> p.getName().equals(paramName)).findFirst().orElseGet(null);
        result.setType(ReportParameterType.fromId(dimension.getType()));

        if (dimension.getValues() != null && !dimension.getValues().isEmpty()) {
            result.setValues(loadParameterOriginalValues(dimension.getType(), dimension.getValues()));
        }

        if (isVCTIterator(dimension)) {
            result.setVctIteratorInfo(constructVCTTableIteratorParameter((OOIterator) dimension, true));
        }

        if (dimension.getType() == VCT_ITERATOR || dimension.getType() == PLAIN_VCT_ITERATOR) {
            result.setValues(new ArrayList<>());
        }

        return result;
    }

    public ReportInfoDetailModel getReportIteratorsModel(int reportId, byte[] reportInfoBytes) {
        List<ReportInfoModel> iterators = new ArrayList<>();
        List<ReportInfoModel> parameters = new ArrayList<>();
        long langId = ThreadLocalHolder.getLanguage().getId();
        ReportInfo reportInfo = null;
        try {
            reportInfo = ReportUtil.getReportInfo(reportInfoBytes);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        if (reportInfo == null) {
            return null;
        }
        Iterator<net.fina.report.model.Iterator> iterator = reportInfo.iterators.values().iterator();
        while (iterator.hasNext()) {
            net.fina.report.model.Iterator ooIterator = iterator.next();

            ReportInfoModel model = new ReportInfoModel();
            model.setName(ooIterator.getName());
            model.setType(ReportParameterType.fromId(ooIterator.getType()));

            if (isVCTIterator(ooIterator)) {
                model.setVctIteratorInfo(constructVCTTableIteratorParameter((OOIterator) ooIterator, false));
            }
            final List<String>[] values = getValues(ooIterator.getValues().iterator(), langId, ooIterator.getType());
            model.setValues(values[0]);
            model.setInvalidValues(values[1]);
            iterators.add(model);
        }

        Iterator<Parameter> parIterator = reportInfo.parameters.values().iterator();
        while (parIterator.hasNext()) {
            Parameter ooParameter = parIterator.next();

            ReportInfoModel model = new ReportInfoModel();
            model.setName(ooParameter.getName());
            model.setType(ReportParameterType.fromId(ooParameter.getType()));

            final List<String>[] values = getValues(ooParameter.getValues().iterator(), langId, ooParameter.getType());
            model.setValues(values[0]);
            model.setInvalidValues(values[1]);
            parameters.add(model);
        }

        return new ReportInfoDetailModel(reportId, iterators, parameters);
    }

    public ReportMetaModel moveReport(int reportId, int folderId) throws FinATypeException {
        Report report = reportLocal.findById(reportId);
        Report destinationReport = reportLocal.findById(folderId);

        if (isLeaf(report) && isLeaf(destinationReport)) {
            throw new FinATypeException("Report can't be pasted in report");
        }

        int sequence = reportLocal.findMaxChildSequence(Math.toIntExact(folderId)) + 1;
        long langId = ThreadLocalHolder.getLanguage().getId();
        report.setSequence(sequence);
        report.setParentId(folderId);
        return ReportingMetaModelHelper.toModel(reportLocal.save(report, langId), langId);
    }

    public boolean moveReportUpDown(ReportMetaModel selectedReport, boolean upDown) {

        return reportLocal.reorderReports(selectedReport.getId(), selectedReport.getParentId(), upDown);
    }

    List<String>[] getValues(Iterator valueIterator, long langId, int type) {
        List<String>[] values = new ArrayList[2];
        values[0] = new ArrayList<>();
        values[1] = new ArrayList<>();

        while (valueIterator.hasNext()) {
            Object next = valueIterator.next();
            String value = "";
            boolean invalidValue = false;

            switch (type) {
                case 1:
                    try {
                        value = "[" + next.toString().trim() + "] " + fiLocal.getFiShortNameByCode(next.toString().trim()).getDescription(langId);
                    } catch (EJBException e) {
                        value = next.toString().trim();
                        invalidValue = true;
                    }
                    break;
                case 4:
                    try {
                        List<Long> PIds = new ArrayList<Long>();

                        Object periodIdObj = next;

                        long periodId;
                        if (periodIdObj instanceof PeriodPK) {
                            periodId = ((PeriodPK) periodIdObj).getId();
                        } else {
                            periodId = (Integer) next;
                        }

                        PIds.add(periodId);

                        List<Period> periods = periodLocal.loadPeriodByIds(PIds);
                        if (!(periods == null || periods.isEmpty())) {
                            value = periods.get(0).getFromDate() + " - " + periods.get(0).getToDate();
                        }
                    } catch (EJBException e) {
                        value = ((PeriodPK) next).getId() + "";
                        invalidValue = true;
                    }
                    break;
                case 2:
                case 3:
                case 5:
                case 6:
                case 7:
                default:
                    value = next.toString();
            }

            values[invalidValue ? 1 : 0].add(value);
        }
        return values;
    }

    private void processReportTreeAndSetUserPermissionsRecursive(List<Integer> userRoleReportIds, List<Integer> userReportIds, List<ReportMetaModel> reportModels, List<ReportPairModel> result, ReportMetaModel parent) {
        List<ReportMetaModel> children = getChildReports(parent, reportModels);
        result.add(new ReportPairModel(parent, children));

        //Check real report
        if (parent.getType() == 2) {
            if (userRoleReportIds != null) {
                parent.setRolePermission(userRoleReportIds.contains(parent.getId()));
            }
            parent.setUserPermission(userReportIds.contains(parent.getId()));
        } else {
            boolean parentRolePermission = true;
            boolean parentUserPermission = true;
            boolean hasSomeChildrenSelected = false;

            for (ReportMetaModel child : children) {

                //Check if it's folder
                if (child.getType() == 1) {
                    processReportTreeAndSetUserPermissionsRecursive(userRoleReportIds, userReportIds, reportModels, result, child);
                }

                if (userRoleReportIds != null && !userRoleReportIds.isEmpty()) {
                    child.setOnlyRolePermission(child.isRolePermission() || userRoleReportIds.contains(child.getId()));
                }

                child.setUserPermission(child.isUserPermission() || userReportIds.contains(child.getId()));

                parentRolePermission &= child.isRolePermission();
                parentUserPermission &= child.isUserPermission();
                hasSomeChildrenSelected |= child.isUserPermission() || child.isHasSomeChildrenSelected();
            }

            hasSomeChildrenSelected &= !(parentRolePermission || parentUserPermission);
            parent.setRolePermission(!children.isEmpty() && parentRolePermission);
            parent.setUserPermission(!children.isEmpty() && parentUserPermission);
            parent.setHasSomeChildrenSelected(hasSomeChildrenSelected);
        }
    }


    private List<ReportMetaModel> getChildReports(ReportMetaModel parent, List<ReportMetaModel> reportModels) {
        List<ReportMetaModel> child = new ArrayList<>();
        long parentId = parent.getId();

        for (ReportMetaModel model : reportModels) {
            if (model.getParentId() == parentId) {
                child.add(model);
            }
        }
        return child;
    }

    private boolean isLeaf(Report report) {
        return report != null && report.getType() == ReportConstants.NODETYPE_REPORT;
    }

    private List<ReportMetaModel> loadNonemptyNodesByParentId(int parentId, Map<ReportFilter, Object> filter, long langId) {
        List<Report> reports = reportLocal.load(filter);
        List<ReportMetaModel> filteredList = new ArrayList<>();
        List<ReportMetaModel> modelList = ReportingMetaModelHelper.toModels(reports, langId);
        for (Report r : reports) {
            ReportMetaModel model = ReportingMetaModelHelper.toModel(r, langId);
            if (r.getParentId() == parentId && (r.getType() == ReportConstants.NODETYPE_REPORT || containsReportDescendant(model, modelList))) {
                filteredList.add(model);
            }
        }
        return filteredList;
    }

    private boolean containsReportDescendant(ReportMetaModel node, List<ReportMetaModel> allNodes) {
        if (node.getType() == ReportConstants.NODETYPE_REPORT) {
            return true;
        }

        List<ReportMetaModel> children = getChildReports(node, allNodes);

        for (ReportMetaModel child : children) {
            if (containsReportDescendant(child, allNodes)) {
                return true;
            }
        }

        return false;
    }

    public List<ReportInfoDetailModel> getReportParameters(List<Integer> reportIds) {
        List<ReportInfoDetailModel> allReports = new ArrayList<>();

        for (Integer id : reportIds) {
            Report report = reportLocal.findById(id);
            if (report.getType() == ReportConstants.NODETYPE_FOLDER) {
                reportLocal.loadChildReportId(id).forEach(rid -> {
                    if (report.getReportType().equals(ReportType.DEFAULT)) {
                        allReports.add(new ReportInfoDetailModel(rid));
                    }
                });
            } else {
                if (reportIds.size() == 1 || report.getReportType().equals(ReportType.DEFAULT)) {
                    allReports.add(new ReportInfoDetailModel(id));
                }
            }
        }
        return selectReportParams(allReports);
    }

    public List<ReportInfoDetailModel> selectReportParams(List<ReportInfoDetailModel> list) {
        for (ReportInfoDetailModel reportInfoDetailModel : list) {

            Report r = reportLocal.findById(reportInfoDetailModel.getReportId());
            if (r != null) {
                try {
                    ReportInfo reportInfo = ReportUtil.getReportInfo(r.getInfo());

                    for (java.util.Iterator<Parameter> iter = reportInfo.parameters.values().iterator(); iter.hasNext(); ) {
                        net.fina.report.model.Parameter parameter = iter.next();
                        if (parameter.getValues().isEmpty()) {
                            reportInfoDetailModel.getParameters().add(new ReportInfoModel(parameter.getName(), parameter.getType()));
                        }
                    }

                    for (java.util.Iterator<net.fina.report.model.Iterator> iter = reportInfo.iterators.values().iterator(); iter.hasNext(); ) {
                        net.fina.report.model.Iterator it = iter.next();
                        if (it.getType() == VCT_ITERATOR || it.getType() == PLAIN_VCT_ITERATOR) {
                            if ((it.getAggregateValues().isEmpty()) && (it.getAggergateParameter() == null) || (it.getPeriodValues().isEmpty() && it.getPeriodParameter() == null)) {
                                ReportInfoModel vctModel = new ReportInfoModel(it.getName(), it.getType());
                                reportInfoDetailModel.getIterators().add(vctModel);
//                                setVctAggregateParameterName(reportInfo, it);
                                vctModel.setVctIteratorInfo(constructVCTTableIteratorParameter((OOIterator) it, false));
                            }

//                            setVctPeriodParameterName(reportInfo, it);
                        } else {
                            if ((it.getValues().isEmpty()) && (it.getParameter() == null)) {
                                reportInfoDetailModel.getIterators().add(new ReportInfoModel(it.getName(), it.getType()));
                            }
                        }
                    }

                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        return list;
    }

    public ReportKeyModel setReportGenerationSessionReportInfo(List<ReportInfoDetailModel> reportInfoDetailModels) {
        boolean useGeneratedReports = true;
        long langId = ThreadLocalHolder.getLanguage().getId();

        List<StoredReportMetaModel> storedReports = new ArrayList<>();
        //pattern UUID+ generated time in milliseconds
        String reportGenerationKey = reportGenerationSessionAttributePrefix + UUID.randomUUID() + "_" + new Date().getTime();

        try {

            Map<String, net.fina.report.model.ReportModel> parameterReportsMap = new HashMap<>();

            for (ReportInfoDetailModel reportInfoDetalModel : reportInfoDetailModels) {
                Report report = reportLocal.findById(reportInfoDetalModel.getReportId());
                ReportInfo reportInfo = (ReportInfo) ObjectSerializer.deSerialize(report.getInfo());

                if (!reportInfoDetalModel.getParameters().isEmpty()) {

                    for (ReportInfoModel parameter : reportInfoDetalModel.getParameters()) {
                        int type = parameter.getType().getTypeID();
                        String parameterName = parameter.getName();
                        (reportInfo.parameters.get(parameterName)).setValues(getPeriodPKValues(type, parameter.getValues()));
                    }


                    for (java.util.Iterator<net.fina.report.model.Iterator> iter = reportInfo.iterators.values().iterator(); iter.hasNext(); ) {
                        net.fina.report.model.Iterator it = iter.next();
                        if (reportInfo.parameters != null && it.getParameter() != null) {
                            net.fina.report.model.Parameter paramObject = reportInfo.parameters.get(it.getParameter());
                            if (paramObject != null) {
                                it.setValues(paramObject.getValues());
                            }
                        }
                    }

                }
                if (!reportInfoDetalModel.getIterators().isEmpty()) {

                    for (ReportInfoModel iterator : reportInfoDetalModel.getIterators()) {
                        int type = iterator.getType().getTypeID();
                        String iteratorName = iterator.getName();
                        OOIterator it = (OOIterator) reportInfo.iterators.get(iteratorName);
                        if (isVCTIterator(it)) {

                            if (it.getPeriodParameter() == null && it.getPeriodValues().isEmpty()) {
                                it.setPeriodValues(getPeriodPKValues(ReportParameterType.PERIOD.getTypeID(), iterator.getVctIteratorInfo().getPeriodParameterValues()));
                            }
                            if (it.getAggregateValues() != null && it.getAggregateValues().isEmpty()) {
                                it.setAggregateValues(iterator.getValues());
                            }
                        } else {
                            it.setValues(getPeriodPKValues(type, iterator.getValues()));
                        }

                    }

                }

                net.fina.report.model.ReportModel legacyReportModel = new net.fina.report.model.ReportModel();
                legacyReportModel.setId(report.getId());
                legacyReportModel.setVersion(report.getVersion());
                legacyReportModel.setParentId(report.getParentId());
                legacyReportModel.setType(report.getType());
                legacyReportModel.setReportType(report.getReportType());
                legacyReportModel.setTemplate(report.getTemplate());
                legacyReportModel.setInfo(ObjectSerializer.serialize(reportInfo));
                legacyReportModel.setSequence(report.getSequence());
                legacyReportModel.setNameStrId(report.getDescription().getNameStrId());
                legacyReportModel.setName(report.getDescription().getDescription(langId));
                legacyReportModel.setUserId(userLocal.getCurrentUserId());

                //Set session report Info
                parameterReportsMap.put(Integer.toString(reportInfoDetalModel.getReportId()), legacyReportModel);
                ThreadLocalHolder.getThreadLocalRequest().getSession().setAttribute(reportGenerationKey, parameterReportsMap);

                StoredReportPk storedReportPk = new StoredReportPk();
                storedReportPk.setReportId(legacyReportModel.getId());
                storedReportPk.setHashCode(reportInfo.hashCode());
                storedReportPk.setLangId((int) langId);

                StoredReport storedReport = storedReportLocal.loadGeneratedReportLite(storedReportPk);

                if (storedReport != null) {

                    StoredReportMetaModel storedReportModel = new StoredReportMetaModel();

                    StoredReportPkMetaModel pk = new StoredReportPkMetaModel();
                    pk.setReportId(storedReportPk.getReportId());
                    pk.setHashCode(storedReportPk.getHashCode());
                    pk.setLangId(storedReportPk.getLangId());

                    storedReportModel.setReportPkModel(pk);

                    storedReportModel.setReportCode(report.getCode());
                    storedReportModel.setReportName(report.getDescription().getDescription(storedReportPk.getLangId()));
                    storedReportModel.setStoreDate(storedReport.getStoreDate());
                    storedReportModel.setUserId((int) storedReport.getUserId());

                    if (storedReport.getUserId() > 0) {
                        User user = userLocal.findUserbyId(storedReport.getUserId());
                        storedReportModel.setUserLogin(user.getDescription().getDescription(storedReportPk.getLangId()) + " [" + user.getLogin() + "]");
                    }

                    storedReports.add(storedReportModel);

                    if (reportInfoDetailModels.size() == 1) {
                        return new ReportKeyModel(reportGenerationKey, storedReports);
                    }
                } else {
                    useGeneratedReports = false;
                }
            }

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        if (!useGeneratedReports) {
            storedReports.clear();
        }

        return new ReportKeyModel(reportGenerationKey, storedReports);
    }


    public List<StoredReportMetaModel> loadPreGeneratedReports(List<Integer> reportIds) {
        List<StoredReportMetaModel> result = new ArrayList<>();

        int langId = (int) ThreadLocalHolder.getLanguage().getId();

        for (Integer reportId : reportIds) {
            Report report = reportLocal.findById(reportId);
            StoredReport storedReport = storedReportLocal.findGeneratedReport(report.getId(), langId);

            if (storedReport != null) {
                StoredReportMetaModel storedReportModel = new StoredReportMetaModel();

                StoredReportPkMetaModel pk = new StoredReportPkMetaModel();
                pk.setReportId(storedReport.getReportPk().getReportId());
                pk.setHashCode(storedReport.getReportPk().getHashCode());
                pk.setLangId(langId);

                storedReportModel.setReportPkModel(pk);

                storedReportModel.setReportCode(report.getCode());
                storedReportModel.setReportName(report.getDescription().getDescription(langId));
                storedReportModel.setStoreDate(storedReport.getStoreDate());
                storedReportModel.setUserId((int) storedReport.getUserId());

                if (storedReport.getUserId() > 0) {
                    User user = userLocal.findUserbyId(storedReport.getUserId());
                    storedReportModel.setUserLogin(user.getDescription().getDescription(langId) + " [" + user.getLogin() + "]");
                }
                result.add(storedReportModel);
            } else {
                result.clear();
                return result;
            }
        }

        return result;
    }

    @RolesAllowed(PermissionIdNames.FINA_REPORT_GENERATE)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = ReportSession.REPORT_GENERATE_TRANSACTION_TIMEOUT_HOURS)
    public ContentModel generateReport(String reportGenerationKey, List<Integer> reportIds, int folderId, boolean regenerate, boolean replaceAll, String fileType, String contextPath) throws FinATypeException, OfficeTypeException {
        try {
            //clear old session reports
            clearOutDatedReportSessionData();
            if (reportIds.size() > 1) {
                checkUserFolderGenerationPermission();
            }

            long langId = ThreadLocalHolder.getLanguage().getId();
            String[] ids = new String[reportIds.size()];
            for (int i = 0; i < reportIds.size(); i++) {
                ids[i] = String.valueOf(reportIds.get(i));
            }

            Map<String, Object> parameterReportsMap = getParametersMap(ThreadLocalHolder.getThreadLocalRequest(), ids, reportGenerationKey);

            ReportGeneratorResult result = reportGeneratorProcessor.compileReport(parameterReportsMap, ids, regenerate ? 1 : 0, langId, fileType, folderId, replaceAll);
            if (result.getReportType() == ReportType.EXCEL) {
                return ReportPrintUtil.print(null, result.getContent(), fileType, result.getFileName(), contextPath);
            }

            if (fileType.equalsIgnoreCase("html")) {
                return ReportPrintUtil.printHtml(aooServiceManager.getOfficeManager(), result.getContent(), result.getFileName(), contextPath);
            } else {
                return ReportPrintUtil.print(aooServiceManager.getOfficeManager(), result.getContent(), fileType, result.getFileName(), contextPath);
            }

        } catch (OfficeTypeException e) {
            throw e;
        } catch (Exception t) {
            log.error(t.getMessage(), t);
            throw new OfficeTypeException(t.getMessage());
        } finally {
            try {
                //clear report info from session
                ThreadLocalHolder.getThreadLocalRequest().getSession().removeAttribute(reportGenerationKey);
            } catch (Exception ignore) {
            }
        }

    }

    public List<ReportPairModel> loadCurrentUserReportsTree() {
        return loadUserReportsTreeMap(userLocal.getCurrentUserId(), false, false);
    }

    public ContentModel downloadTemplates(List<Integer> reportIds, String fileType, String contextPath) throws OfficeTypeException {
        try {
            long langId = ThreadLocalHolder.getLanguage().getId();
            DateFormat fileNameDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

            byte[] content = null;
            String fileName = "report_templates_" + fileNameDateFormat.format(new Date());

            String[] ids = new String[reportIds.size()];
            for (int i = 0; i < reportIds.size(); i++) {
                ids[i] = String.valueOf(reportIds.get(i));
            }

            boolean one = ids.length == 1;

            if (one) {
                int reportId = Integer.parseInt(ids[0]);
                Report report = reportLocal.findById(reportId);
                if (report != null && report.getReportType() == ReportType.EXCEL && reportLocal.hasUserAccess(reportId)) {

                    if (fileType.equalsIgnoreCase("html")) {
                        return ReportPrintUtil.printHtml(null, report.getTemplate(), fileName, contextPath);
                    } else {
                        return ReportPrintUtil.print(null, report.getTemplate(), fileType, fileName, contextPath);
                    }
                }
            }

            AooWriterBase workBookOdfToolkitReader = null;

            List<String> reportNames = new ArrayList<>();

            AbstractFactory factory = FactoryProducer.getFactory("odftoolkit");

            for (String idString : ids) {
                int reportId = Integer.parseInt(idString);

                if (workBookOdfToolkitReader == null) {
                    workBookOdfToolkitReader = factory.getAooWriter();
                    workBookOdfToolkitReader.init(workBookOdfToolkitReader.createEmptySpreadsheetDocument());
                    workBookOdfToolkitReader.removeSheetByIndex(0);
                }

                Report report = reportLocal.findById(reportId);

                if (report.getType() == ReportConstants.NODETYPE_FOLDER) {
                    one = false;

                    Map<ReportFilter, Object> filter = new HashMap<>();
                    filter.put(ReportFilter.TYPE, ReportConstants.NODETYPE_REPORT);
                    filter.put(ReportFilter.FOLDER_ID, report.getId());
                    List<Report> reports = reportLocal.load(filter);

                    for (Report r : reports) {
                        appendReportTemplate(factory, workBookOdfToolkitReader, reportLocal.findById(r.getId()), langId, reportNames);
                    }

                } else {
                    if (reportLocal.hasUserAccess(reportId)) {
                        if (one) {
                            fileName = report.getDescription().getDescription(langId);
                            AooWriterBase odfToolkitReader = factory.getAooWriter(report.getTemplate());
                            content = odfToolkitReader.getSpreadsheetDocument();
                        } else {
                            appendReportTemplate(factory, workBookOdfToolkitReader, report, langId, reportNames);
                        }
                    }
                }
            }

            if ((!one) && workBookOdfToolkitReader != null) {
                content = workBookOdfToolkitReader.getSpreadsheetDocument();
            }

            if (fileType.equalsIgnoreCase("html")) {
                return ReportPrintUtil.printHtml(aooServiceManager.getOfficeManager(), content, fileName, contextPath);
            } else {
                return ReportPrintUtil.print(aooServiceManager.getOfficeManager(), content, fileType, fileName, contextPath);
            }
        } catch (OfficeTypeException e) {
            throw e;
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return null;
    }

    private void appendReportTemplate(AbstractFactory factory, AooWriterBase workBookOdfToolkitReader, Report report, long langId, List<String> reportNames) throws Exception {
        String reportName = report.getDescription().getDescription(langId);
        reportName = getUniqueReportName(reportNames, reportName, reportName, 1);
        AooWriterBase odfToolkitReader = factory.getAooWriter(report.getTemplate());
        workBookOdfToolkitReader.appendSheet(odfToolkitReader.getCurrentSheet(), reportName);
    }

    private String getUniqueReportName(List<String> reportNames, String reportName, String tmpReportName, int index) {
        if (reportNames.contains(tmpReportName)) {
            tmpReportName = reportName + "_" + index;
            index++;
            return getUniqueReportName(reportNames, reportName, tmpReportName, index);
        } else {
            reportNames.add(tmpReportName);
            return tmpReportName;
        }
    }


    private void setVctAggregateParameterName(ReportInfo reportInfo, net.fina.report.model.Iterator it) {
        Iterator iterator = reportInfo.parameters.values().iterator();
        while (iterator.hasNext()) {
            OOParameter p = (OOParameter) iterator.next();
            if (it.getAggregateType() == 1 && p.getType() == 1 || it.getAggregateType() == 2 && p.getType() == 2) {
                it.setAggregateParameter(it.getName());
                break;
            }
        }
    }

    private void setVctPeriodParameterName(ReportInfo reportInfo, net.fina.report.model.Iterator it) {
        Iterator iterator = reportInfo.parameters.values().iterator();
        while (iterator.hasNext()) {
            OOParameter p = (OOParameter) iterator.next();
            if (p.getType() == 4) {
                it.setAggregateParameter(it.getName());
                break;
            }
        }
    }

    private void clearOutDatedReportSessionData() {
        String attrKey = "";
        try {
            HttpSession session = ThreadLocalHolder.getThreadLocalRequest().getSession();
            Enumeration<String> attributes = (session.getAttributeNames());

            while (attributes.hasMoreElements()) {
                attrKey = attributes.nextElement();
                if (attrKey.startsWith(reportGenerationSessionAttributePrefix)) {
                    String dateCreatedInMillis = attrKey.substring(attrKey.lastIndexOf("_") + 1);
                    long timePassed = new Date().getTime() - Long.parseLong(dateCreatedInMillis.trim());
                    if (timePassed > TimeUnit.HOURS.toMillis(6)) {
                        session.removeAttribute(attrKey);
                    }
                }
            }
        } catch (Throwable t) {
            log.warn("Cannot create report generation parameters session data with key : " + attrKey);
        }

    }

    private Map<String, Object> getParametersMap(HttpServletRequest request, String[] ids, String reportGenerationKey) {
        Map<String, Object> result = new java.util.HashMap<>();
        try {
            reportGenerationKey = reportGenerationKey == null ? "" : reportGenerationKey;
            Map<String, Object> paramMap = (Map<String, Object>) request.getSession().getAttribute(reportGenerationKey);
            if (paramMap != null) {
                return paramMap;
            } else {
                for (String idString : ids) {
                    result.put(idString, null);
                }
            }
        } catch (Throwable th) {
            log.error(th.getMessage(), th);
        }
        return result;
    }

    private void checkUserFolderGenerationPermission() throws FinATypeException {
        String userLogin = userLocal.getCurrentUserLogin();
        List<String> userPermissions = authorizationLocal.loadUserPermission(userLogin);
        if (!userPermissions.contains(PermissionIdNames.FINA_REPORT_FOLDER_GENERATE)) {
            throw new FinATypeException("User doesn't have permission to generate more than one report.");
        }
    }


    private List<? extends Object> getPeriodPKValues(int type, List<? extends Object> array) {
        List<Object> values = new ArrayList<>();
        if (type == 4) {
            for (int i = 0; i < array.size(); i++) {
                values.add(new PeriodPK(Integer.parseInt((String) array.get(i))));
            }
        }

        return !values.isEmpty() ? values : array;
    }

    private VctIteratorInfo constructVCTTableIteratorParameter(OOIterator ooIterator, boolean originalValues) {

        VctIteratorInfo iteratorInfoModel = new VctIteratorInfo();

        iteratorInfoModel.setSkipRowCondition(ooIterator.getSkipRowCondition());
        iteratorInfoModel.setGroupByDefinitionCode(ooIterator.getGroupCode());
        iteratorInfoModel.setVersionCode(ooIterator.getVersionCode());
        iteratorInfoModel.setAggregateParameter(ooIterator.getAggergateParameter());
        iteratorInfoModel.setPeriodParameter(ooIterator.getPeriodParameter());

        if (ooIterator.getPeriodValues() != null && !ooIterator.getPeriodValues().isEmpty()) {
            Collection<PeriodPK> periodValues = ooIterator.getPeriodValues();
            List<Long> periodIds = periodValues.stream().map(ppK -> (long) ppK.getId()).toList();
            if (!originalValues) {
                List<Period> periods = periodLocal.loadPeriodByIds(periodIds);
                iteratorInfoModel.setPeriodParameterValues(periods.stream().map(p -> p.getFromDate() + "-" + p.getToDate()).toList());
            } else {
                iteratorInfoModel.setPeriodParameterValues(periodIds.stream().map(Object::toString).toList());
            }
        }

        if (ooIterator.getAggregateValues() != null && !ooIterator.getAggregateValues().isEmpty()) {
            iteratorInfoModel.setAggregateValues((List<String>) ooIterator.getAggregateValues());
        }

        if (ooIterator.getAggregateType() == 1) {
            iteratorInfoModel.setAggregateBy("Bank");
        } else {
            iteratorInfoModel.setAggregateBy("Peer");
        }

        ReturnDefinitionTablePK tablePK = ooIterator.getTable();
        if (tablePK != null) {
            DefinitionTable definitionTable = returnDefinitionLocal.loadReturnDefinitionTableById(tablePK.getDefinitionID(), tablePK.getTableID());
            if (definitionTable != null) {
                iteratorInfoModel.setTableName(definitionTable.getCode());
            }
        }

        return iteratorInfoModel;
    }

    private boolean isVCTIterator(net.fina.report.model.Iterator iterator) {
        return iterator.getType() == VCT_ITERATOR || iterator.getType() == PLAIN_VCT_ITERATOR;
    }

    @RolesAllowed(PermissionIdNames.FINA_REPORT_AMEND)
    public void updateReportParameter(int reportId, String existingParameterName, ParameterMetaModel parameter) throws FinATypeException {

        Report r = reportLocal.findById(reportId);
        ReportInfo reportInfo = deserializeReportInfo(r.getId(), r.getInfo());

        Parameter existingParameter = reportInfo.parameters.get(existingParameterName);
        if (existingParameter == null) {
            throw new FinATypeException("Parameter not found");
        }
        //remove form parameters
        reportInfo.parameters.remove(existingParameterName);

        //validate parameter
        ReportDimensionHelper.validateParameter(parameter, reportInfo);

        existingParameter.setName(parameter.getName().trim());
        if (parameter.getValues() != null) {
            existingParameter.setValues(parameter.getValues());
        }

        //check and update parameter used by dimensions
        ReportDimensionHelper.updateDependentDimensionParameter(existingParameterName, parameter.getName(), reportInfo);

        reportInfo.parameters.put(parameter.getName().trim(), existingParameter);

        reportLocal.updateReportInfo(reportId, reportInfo);

    }

    @RolesAllowed(PermissionIdNames.FINA_REPORT_AMEND)
    public void updateReportDimension(@NotNull Integer reportId, @NotNull String existingDimensionName, DimensionMetaModel dimensionModel) throws FinATypeException {
        Report r = reportLocal.findById(reportId);
        ReportInfo reportInfo = deserializeReportInfo(r.getId(), r.getInfo());

        net.fina.report.model.Iterator existingDimension = reportInfo.iterators.get(existingDimensionName);

        if (existingDimension == null) {
            throw new FinATypeException("Dimension not found");
        }

        //remove form dimensions
        reportInfo.iterators.remove(existingDimensionName);

        existingDimension.setName(dimensionModel.getName().trim());

        if (dimensionModel.getValues() != null) {
            existingDimension.setValues(dimensionModel.getValues());
        }

        //update vct iterator values
        if (dimensionModel.getType().getTypeID() == PLAIN_VCT_ITERATOR || dimensionModel.getType().getTypeID() == VCT_ITERATOR) {
            if (dimensionModel.getAggregateParameterName() != null && !dimensionModel.getAggregateParameterName().isBlank()) {
                existingDimension.setAggregateParameter(dimensionModel.getAggregateParameterName());
            }
            if (dimensionModel.getPeriodParameterName() != null && !dimensionModel.getPeriodParameterName().isBlank()) {
                existingDimension.setPeriodParameter(dimensionModel.getPeriodParameterName());
            }

            if (dimensionModel.getAggregateValues() != null) {
                existingDimension.setAggregateValues(dimensionModel.getAggregateValues());
            }
            if (dimensionModel.getPeriodValues() != null) {
                existingDimension.setPeriodValues(dimensionModel.getPeriodValues());
            }
        }

        reportInfo.iterators.put(dimensionModel.getName().trim(), existingDimension);
        if (!dimensionModel.getName().trim().equalsIgnoreCase(existingDimensionName.trim())) {
            updateReportDimensionNamedRange(r, existingDimensionName, dimensionModel.getName());
        }
        reportLocal.updateReportInfo(reportId, reportInfo);

    }

    private void updateReportDimensionNamedRange(Report report, String oldName, String newName) throws FinATypeException {
        try {
            if (report.getReportType() == ReportType.EXCEL) {
                newName = ExcelOoIteratorHelper.getParsedName(newName);
            }

            byte[] result = reportLocal.renameNamedRange(report.getReportType(), report.getTemplate(), oldName, newName);
            reportLocal.updateReportTemplate(report.getId(), result);

        } catch (Exception ex) {
            throw new FinATypeException(GENERAL_ERROR, ex.getMessage());
        }
    }

    private ReportInfo deserializeReportInfo(long reportId, byte[] info) throws FinATypeException {
        try {
            return ReportUtil.getReportInfo(info);
        } catch (Exception e) {
            log.error("Cannot load report info for report id : " + reportId, e);
            throw new FinATypeException(GENERAL_ERROR);
        }
    }


    private List<Object> loadParameterOriginalValues(int parameterType, Collection<Object> values) {
        List<String> parameterValues = values.stream().map(Object::toString).toList();
        List<Object> result = new ArrayList<>();
        switch (parameterType) {
            case net.fina.report.model.Iterator.PERIOD_ITERATOR:
                Map<PeriodFilter, Object> pFilter = new HashMap<>();
                pFilter.put(PeriodFilter.ids, parameterValues.stream().map(Long::parseLong).toList());
                result.addAll(PeriodModelHelper.toModel(periodLocal.load(pFilter), ThreadLocalHolder.getLanguage().getId()));
                break;
            case net.fina.report.model.Iterator.BANK_ITERATOR:
                List<Fi> fis = fiLocal.loadFiByCodes(parameterValues);
                result.addAll(FiModelHelper.toModels(fis, ThreadLocalHolder.getLanguage().getId()));
                break;
            case net.fina.report.model.Iterator.PEER_ITERATOR:
                List<PeerGroup> peerGroups = peerGroupLocal.loadPeerGroupByCodes(parameterValues);
                result.addAll(PeerGroupModelHelper.toModels(peerGroups));
                break;
            case net.fina.report.model.Iterator.NODE_ITERATOR:
                List<MDTNode> nodes = mdtNodeLocal.getMDTNodeByCodes(parameterValues);
                result.addAll(MdtNodeModelHelper.toModels(nodes, ThreadLocalHolder.getLanguage().getId()));
                break;
            case net.fina.report.model.Iterator.VERSION_ITERATOR:
                List<ReturnVersion> versions = returnVersionLocal.loadReturnVersionsByCodes(parameterValues);
                result.addAll(ReturnVersionModelHelper.toModels(versions, ThreadLocalHolder.getLanguage().getId()));
                break;
        }

        return result;
    }
}
