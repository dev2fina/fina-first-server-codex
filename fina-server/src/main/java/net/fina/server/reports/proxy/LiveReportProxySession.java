package net.fina.server.reports.proxy;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.filter.FiFilter;
import net.fina.report.core.ReportDataProcessor;
import net.fina.report.model.AgregateFunction;
import net.fina.report.model.PeriodFunction;
import net.fina.reporting.impl.LiveReportGenerator;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.entity.Fi;
import net.fina.server.i18n.model.DescriptionModelHelper;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.reports.model.DynamicReportMultiDimensionalModel;
import net.fina.server.reports.model.DynamicReportMultiDimensionalResultModel;
import net.fina.server.reports.model.LiveReportMultiDimensionResultMetaModel;
import net.fina.server.reports.model.LiveReportResultMetaModel;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Stateless
public class LiveReportProxySession {

    @Inject
    private ReportDataProcessor reportDataProcessor;

    @Inject
    private MDTNodeLocal mdtNodeLocal;

    @Inject
    private FiLocal fiLocal;

    public List<Double> nodeValueByPeriods(List<Integer> periodIds, String nodeCode) {
        LiveReportGenerator reportGenerator = new LiveReportGenerator(reportDataProcessor);
        List<Double> result = new ArrayList<>();

        periodIds.forEach(periodId -> {
            result.add(reportGenerator.allbanksvalue(reportGenerator.getReportSheetId(), nodeCode, "sum", periodId, "last", 0));
        });

        return result;
    }

    public Map<Integer, List<LiveReportResultMetaModel>> nodeValueByPeriodAndParentCode(String nodeCode, List<Integer> periodIds) {
        LiveReportGenerator reportGenerator = new LiveReportGenerator(reportDataProcessor);

        Map<Integer, List<LiveReportResultMetaModel>> result = new HashMap<>();

        if (nodeCode != null) {
            MDTNode mdtNode = mdtNodeLocal.getMdtNodesByCode().get(nodeCode);
            periodIds.parallelStream().forEach(periodId -> {
                List<LiveReportResultMetaModel> resultMetaModels = new ArrayList<>();
                if (mdtNode != null) {

                    List<MDTNode> nodes = getAndSortChildNodes(mdtNode.getId());

                    for (MDTNode node : nodes) {

                        double value = reportGenerator.allbanksvalue(reportGenerator.getReportSheetId(), node.getCode(), "sum", periodId, "last", 0);

                        LiveReportResultMetaModel model = new LiveReportResultMetaModel();
                        model.setCode(node.getCode());
                        model.setDescriptions(DescriptionModelHelper.toModel(node.getDescription()));
                        model.setValue(value);

                        resultMetaModels.add(model);
                    }
                }
                result.put(periodId, resultMetaModels);
            });

        }

        return result;
    }

    public Map<Integer, List<LiveReportResultMetaModel>> nodeValueByPeriodAndFis(List<Integer> periodIds, List<Long> fiIds, String nodeCode) {
        LiveReportGenerator reportGenerator = new LiveReportGenerator(reportDataProcessor);

        Map<Integer, List<LiveReportResultMetaModel>> result = new HashMap<>();

        Map<FiFilter, Object> filter = new HashMap<>();
        filter.put(FiFilter.FI_IDS, fiIds);

        if (nodeCode != null) {

            List<Fi> fis = fiLocal.load(filter);
            periodIds.parallelStream().forEach(periodId -> {
                List<LiveReportResultMetaModel> resultMetaModels = new ArrayList<>();
                if (fis != null) {
                    for (Fi fi : fis) {

                        double value = reportGenerator.bankvalue(reportGenerator.getReportSheetId(), nodeCode, fi.getCode(), periodId, "last", 0);

                        LiveReportResultMetaModel model = new LiveReportResultMetaModel();
                        model.setCode(fi.getCode());
                        model.setDescriptions(DescriptionModelHelper.toModel(fi.getDescription()));
                        model.setValue(value);

                        resultMetaModels.add(model);
                    }
                }
                result.put(periodId, resultMetaModels);
            });


        }

        return result;
    }

    public LiveReportMultiDimensionResultMetaModel nodeValueByPeriodAndParentCodeFiType(int periodId, String nodeCode, String fiTypeCode) {
        LiveReportGenerator reportGenerator = new LiveReportGenerator(reportDataProcessor);

        LiveReportMultiDimensionResultMetaModel result = new LiveReportMultiDimensionResultMetaModel();

        Map<FiFilter, Object> filter = new HashMap<>();
        filter.put(FiFilter.TYPE_CODE, fiTypeCode);

        if (nodeCode != null) {

            MDTNode mdtNode = mdtNodeLocal.getMdtNodesByCode().get(nodeCode);

            if (mdtNode != null) {
                List<MDTNode> nodes = getAndSortChildNodes(mdtNode.getId());

                List<Fi> fis = fiLocal.load(filter);

                if (fis != null) {
                    for (Fi fi : fis) {

                        List<LiveReportResultMetaModel> fisColumn = result.getColumns().get("fis");
                        if (fisColumn == null) {
                            fisColumn = new ArrayList<>();
                            result.getColumns().put("fis", fisColumn);
                        }

                        LiveReportResultMetaModel model = new LiveReportResultMetaModel();
                        model.setCode(fi.getCode());
                        model.setDescriptions(DescriptionModelHelper.toModel(fi.getDescription()));
                        fisColumn.add(model);

                        for (MDTNode node : nodes) {
                            double value = reportGenerator.bankvalue(reportGenerator.getReportSheetId(), node.getCode(), fi.getCode(), periodId, "last", 0);

                            LiveReportResultMetaModel nodeModel = new LiveReportResultMetaModel();
                            nodeModel.setValue(value);

                            List<LiveReportResultMetaModel> nodesColumn = result.getColumns().get(node.getCode());
                            if (nodesColumn == null) {
                                nodesColumn = new ArrayList<>();
                                nodeModel.setDescriptions(DescriptionModelHelper.toModel(node.getDescription()));
                                result.getColumns().put(node.getCode(), nodesColumn);
                            }
                            nodesColumn.add(nodeModel);

                        }
                    }
                }
            }
        }

        return result;
    }

    public Map<Integer, LiveReportMultiDimensionResultMetaModel> nodeValueByPeriodAndParentCode(String parameterCode,
                                                                                                List<Long> fiIds,
                                                                                                List<Integer> periodIds) {
        LiveReportGenerator reportGenerator = new LiveReportGenerator(reportDataProcessor);

        Map<Integer, LiveReportMultiDimensionResultMetaModel> result = new HashMap<>();

        Map<FiFilter, Object> filter = new HashMap<>();
        filter.put(FiFilter.FI_IDS, fiIds);

        if (parameterCode != null) {

            MDTNode mdtNode = mdtNodeLocal.getMdtNodesByCode().get(parameterCode);

            if (mdtNode != null) {
                List<MDTNode> nodes = getAndSortChildNodes(mdtNode.getId());

                List<Fi> fis = fiLocal.load(filter);

                if (fis != null) {
                    List<Fi> finalFis = fis;
                    periodIds.parallelStream().forEach(periodId -> {

                        LiveReportMultiDimensionResultMetaModel resultMetaModel = new LiveReportMultiDimensionResultMetaModel();
                        result.put(periodId, resultMetaModel);
                        for (Fi fi : finalFis) {

                            List<LiveReportResultMetaModel> fisColumn = result.get(periodId).getColumns().computeIfAbsent("fis", k -> new ArrayList<>());

                            LiveReportResultMetaModel model = new LiveReportResultMetaModel();
                            model.setCode(fi.getCode());
                            model.setDescriptions(DescriptionModelHelper.toModel(fi.getDescription()));
                            fisColumn.add(model);

                            for (MDTNode node : nodes) {
                                double value = reportGenerator.bankvalue(reportGenerator.getReportSheetId(),
                                        node.getCode(), fi.getCode(), periodId, "last", 0);

                                LiveReportResultMetaModel nodeModel = new LiveReportResultMetaModel();
                                nodeModel.setValue(value);

                                List<LiveReportResultMetaModel> nodesColumn = result.get(periodId).getColumns().get(node.getCode());
                                if (nodesColumn == null) {
                                    nodesColumn = new ArrayList<>();
                                    nodeModel.setDescriptions(DescriptionModelHelper.toModel(node.getDescription()));
                                    result.get(periodId).getColumns().put(node.getCode(), nodesColumn);
                                }
                                nodesColumn.add(nodeModel);

                            }
                        }
                    });
                }

            }
        }

        return result;

    }

    public Map<Integer, List<LiveReportResultMetaModel>> nodeValueByPeriodAndParentCodeFi(String parameterCode, String fiCode, List<Integer> periodIds) {
        LiveReportGenerator reportGenerator = new LiveReportGenerator(reportDataProcessor);

        Map<Integer, List<LiveReportResultMetaModel>> result = new HashMap<>();

        if (parameterCode != null) {
            MDTNode mdtNode = mdtNodeLocal.getMdtNodesByCode().get(parameterCode);

            if (mdtNode != null) {

                List<MDTNode> nodes = getAndSortChildNodes(mdtNode.getId());


                periodIds.parallelStream().forEach(periodId -> {

                    List<LiveReportResultMetaModel> resultMetaModels = new ArrayList<>();
                    for (MDTNode node : nodes) {

                        double value = reportGenerator.bankvalue(reportGenerator.getReportSheetId(), node.getCode(), fiCode, periodId, "last", 0);

                        LiveReportResultMetaModel model = new LiveReportResultMetaModel();
                        model.setCode(node.getCode());
                        model.setDescriptions(DescriptionModelHelper.toModel(node.getDescription()));
                        model.setValue(value);

                        resultMetaModels.add(model);
                    }
                    result.put(periodId, resultMetaModels);
                });

            }
        }

        return result;
    }


    public Map<Integer, List<LiveReportResultMetaModel>> nodeValueByPeriodAndFi(String extraParameterCode, String fiCode, List<Integer> periodIds) {
        LiveReportGenerator reportGenerator = new LiveReportGenerator(reportDataProcessor);

        Map<Integer, List<LiveReportResultMetaModel>> result = new HashMap<>();

        if (extraParameterCode != null) {

            Fi fi = fiLocal.findFiByCode(fiCode);
            periodIds.parallelStream().forEach(periodId -> {
                List<LiveReportResultMetaModel> resultMetaModels = new ArrayList<>();
                if (fi != null) {

                    double value = reportGenerator.bankvalue(reportGenerator.getReportSheetId(), extraParameterCode, fi.getCode(), periodId, "last", 0);

                    LiveReportResultMetaModel model = new LiveReportResultMetaModel();
                    model.setCode(fi.getCode());
                    model.setDescriptions(DescriptionModelHelper.toModel(fi.getDescription()));
                    model.setValue(value);

                    resultMetaModels.add(model);
                }
                result.put(periodId, resultMetaModels);
            });
        }

        return result;
    }

    public Map<Integer, List<String>> nodeValueByPeriodAndFi(String fiCode, List<Integer> periodIds, List<String> formulas) {
        Map<Integer, List<String>> result = new HashMap<>();
        LiveReportGenerator reportGenerator = new LiveReportGenerator(reportDataProcessor);

        periodIds.parallelStream().forEach(periodId -> {
            List<String> replacedFormulaList = new ArrayList<>();
            formulas.forEach(formula -> {
                replacedFormulaList.add(getFormulaResultForFi(reportGenerator, formula, fiCode, periodId));
                result.put(periodId, replacedFormulaList);
            });
        });
        return result;
    }


    public List<DynamicReportMultiDimensionalResultModel> nodeValueByPeriodAndFis(List<Long> fiIds, List<Integer> periodIds, List<String> formulas) {
        LiveReportGenerator reportGenerator = new LiveReportGenerator(reportDataProcessor);

        List<DynamicReportMultiDimensionalResultModel> result = new ArrayList<>();
        if (!periodIds.isEmpty()) {

            Map<FiFilter, Object> filter = new HashMap<>();
            filter.put(FiFilter.FI_IDS, fiIds);

            List<Fi> fis = fiLocal.load(filter);
            formulas.forEach(formula -> {
                List<DynamicReportMultiDimensionalModel> dataModels = new ArrayList<>();
                fis.forEach(fi -> {
                    DynamicReportMultiDimensionalModel model = new DynamicReportMultiDimensionalModel();
                    model.setFiDescriptions(DescriptionModelHelper.toModel(fi.getDescription()));
                    model.setPeriodValues(new LinkedHashMap<>());
                    periodIds.forEach(periodId -> {
                        String fn = getFormulaResultForFi(reportGenerator, formula, fi.getCode(), periodId);
                        model.getPeriodValues().put(periodId, fn);
                    });
                    dataModels.add(model);
                });
                result.add(new DynamicReportMultiDimensionalResultModel(formula, dataModels));
            });
        }

        return result;
    }

    private String getFormulaResultForFi(LiveReportGenerator reportGenerator, String formula, String fiCode, int periodId) {
        formula = replaceFunctions(reportGenerator, formula, fiCode, periodId);

        for (String nodeCode : parseFormulaNodeCodes(formula)) {
            double value;
            if (fiCode.equals("CONSOLIDATED")) {
                value = reportGenerator.allbanksvalue(reportGenerator.getReportSheetId(), nodeCode, "sum", periodId, "last", 0);
            } else {
                value = reportGenerator.bankvalue(reportGenerator.getReportSheetId(), nodeCode, fiCode, periodId, "last", 0);
            }
            formula = formula.replace("{" + nodeCode + "}", String.valueOf(Double.isNaN(value) ? 0.0 : value));
        }

        return formula;
    }

    private List<String> parseFormulaNodeCodes(String formula) {
        List<String> nodeCodes = new ArrayList<>();
        Pattern p = Pattern.compile("\\{([^}]*)\\}");
        Matcher m = p.matcher(formula);
        while (m.find()) {
            nodeCodes.add(m.group(1));
        }

        return nodeCodes;
    }


    private String replaceFunctions(LiveReportGenerator reportGenerator, String formula, String fiCode, int periodId) {
        String formulaCopy = formula;
        Matcher m = Pattern.compile("\\$([A-Z_]*)\\(\\{([^}]*)}\\)").matcher(formula);

        while (m.find()) {
            String nodeCode = m.group(2);

            String periodFunction = "last";
            String bankFunction = "sum";

            switch (m.group(1)) {
                case "AVG":
                    bankFunction = AgregateFunction.F_AVG;
                    break;
                case "SUM":
                case "MIN":
                case "MAX":
                case "COUNT":
                    bankFunction = m.group(1).toLowerCase();
                    break;
                case "Y_AVG":
                    periodFunction = PeriodFunction.F_YAVERAGE;
                    break;
                case "YTD_AVG":
                    periodFunction = PeriodFunction.F_YDTAVERAGE;
                    break;
            }

            double value;
            if (fiCode.equals("CONSOLIDATED")) {
                value = reportGenerator.allbanksvalue(reportGenerator.getReportSheetId(), nodeCode, bankFunction, periodId, periodFunction, 0);
            } else {
                value = reportGenerator.bankvalue(reportGenerator.getReportSheetId(), nodeCode, fiCode, periodId, periodFunction, 0);
            }

            formulaCopy = formulaCopy.replace(m.group(0), String.valueOf(Double.isNaN(value) ? 0.0 : value));
        }

        return formulaCopy;
    }

    private List<MDTNode> getAndSortChildNodes(long nodeId) {
        List<MDTNode> nodes = mdtNodeLocal.loadAllNodesByParentId().get(nodeId);
        //sort nodes
        nodes.sort((o1, o2) -> {
            if (o1.getSequence() == o2.getSequence()) {
                return 0;
            }
            return o1.getSequence() > o2.getSequence() ? 1 : -1;
        });

        return nodes;
    }

}