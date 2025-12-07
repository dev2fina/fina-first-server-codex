package net.fina.server.processing;

import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.returns.ProcessResult;
import net.fina.common.client.returns.ProcessStatus;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.common.shared.mdt.ProcessStage;
import net.fina.messages.MessagesUtil;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.model.ComparisonItem;
import net.fina.server.processing.model.Dependent;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.processing.model.ProcessReturnModel;
import net.fina.server.processing.script.ScriptEngineBase;
import net.fina.server.processing.script.ScriptEngineFactory;
import net.fina.server.processing.script.js.JSTree;
import org.jboss.logging.Logger;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author Nikoloz
 */
public class RecursionProcessing implements ProcessingBase {

    private final Logger log = Logger.getLogger(getClass());

    /**
     * < ProcessItem Id , ReturnProcessItem >
     */
    private final Map<Long, ProcessItem> itemsById;
    /**
     * < ProcessItem Code , ReturnProcessItem >
     */
    private final Map<String, ProcessItem> itemsByIdCode;
    /**
     * Process return model
     */
    private final ProcessReturnModel processReturnModel;
    /**
     * Date processor
     */
    private final JSTreeDateProcessor dateProcessor;
    private final Map<Long, MDTNode> allIdNodes;
    private final ProcessingUtil util;

    private final boolean reprocess;
    /**
     * < ProcessItem id, sub nodes >
     */
    private Map<Long, List<ProcessItem>> itemsByParentId;

    public RecursionProcessing(Map<Long, ProcessItem> itemsById, Map<String, ProcessItem> itemsByCode, Map<Long, MDTNode> allIdNodes, ProcessingUtil util, boolean reprocess, ProcessReturnModel processReturnModel, JSTreeDateProcessor dateProcessor) {
        this.itemsById = itemsById;
        this.itemsByIdCode = itemsByCode;
        this.allIdNodes = allIdNodes;
        this.util = util;
        this.reprocess = reprocess;
        this.processReturnModel = processReturnModel;
        this.dateProcessor = dateProcessor;
    }

    @Override
    public ProcessResult process(Collection<ProcessItem> nodes) {
        this.itemsByParentId = new HashMap<>();

        //Select Nodes to Aggregation
        setItemsByParentId(nodes);

        ProcessResult result = new ProcessResult();
        result.setStatus(ProcessStatus.STATUS_PROCESSED);
        result.setProcessNote("Process OK");

        //Eval Aggregation
        JSTree jsTree = new JSTree(null, null, -1, null, null);
        for (ProcessItem node : nodes) {
            if (node.nodeType == MDTNodeTypes.NODE
                    && node.nodeEvalMethod != null
                    && node.nodeEvalMethod != MDTNodeEvalMethods.UNKNOWN) {
                calculateSubNode(node, jsTree);
            }
        }

        // Evaluate Script
        for (ProcessItem node : nodes) {
            if (node.nodeType == MDTNodeTypes.VARIABLE) {
                evalScript(node);
            } else if (node.nodeType == MDTNodeTypes.INPUT) {
                for (String value : node.values.values()) {
                    util.validateDataType(node, value);
                }
            } else if (node.nodeType == MDTNodeTypes.LIST) {
                for (String value : node.values.values()) {
                    util.validateListElement(node, value);
                }
            }
        }

        if ((!nodes.isEmpty()) && (this.util.getErrorHandler().hasErrors(nodes.iterator().next().returnId))) {
            result.setStatus(ProcessStatus.STATUS_ERRORS);
            result.setProcessNote(this.util.getErrorHandler().getErrorMessage(nodes.iterator().next().returnId));
        }

        return result;
    }

    private void setItemsByParentId(Collection<ProcessItem> nodes) {
        for (ProcessItem pi : nodes) {
            if (pi.nodeType == MDTNodeTypes.NODE
                    && pi.nodeEvalMethod != null
                    && pi.nodeEvalMethod != MDTNodeEvalMethods.UNKNOWN) {
                for (ProcessItem item : nodes) {
                    if (item.parentId == pi.nodeId) {
                        List<ProcessItem> subNodes = this.itemsByParentId.get(pi.nodeId);
                        if (subNodes == null) {
                            subNodes = new ArrayList<>();
                            this.itemsByParentId.put(pi.nodeId, subNodes);
                        }
                        subNodes.add(item);
                    }
                }
            }
        }
    }

    private void evalScript(ProcessItem item) {
        for (int row : item.values.keySet()) {

            Dependent dependent = item.dependent;

            if ((dependent != null) && (!dependent.dependentIds.isEmpty()) && (dependent.status == Dependent.Status.UNKNOWN)) {

                dependent.status = Dependent.Status.IN_PROGRESS;

                for (long dependentId : dependent.dependentIds) {
                    ProcessItem dependentItem = itemsById.get(dependentId);
                    if (dependentItem != null) {
                        if (dependentItem.nodeType == MDTNodeTypes.VARIABLE) {
                            if (reprocess) {
                                evalScript(dependentItem);
                            } else if (item.returnId == dependentItem.returnId) {
                                evalScript(dependentItem);
                            }
                        }
                    } else {
                        MDTNode node = allIdNodes.get(dependentId);
                        if (node != null) {
                            String code = node.getCode();
                            String description = node.getDescription().getDescription(util.getLangId());
                            util.getErrorHandler().onError(null, item, ErrorHandler.MessageId.RETURN_REQUIERS_DEPENDENT_ON, code, description, item.code, item.description);
                        } else {
                            util.getErrorHandler().onError(null, item, ErrorHandler.MessageId.RETURN_REQUIERS_DEPENDENT, item.code, item.description);
                        }
                    }
                }

                dependent.status = Dependent.Status.FINISHED;
            }

            JSTree tree = new JSTree(itemsByIdCode, item, row, this.processReturnModel, this.dateProcessor);
            String value = ScriptEngineFactory.get().call(tree, ScriptEngineBase.createFunction(item.equation));

            item.values.put(row, value);
            item.nValues.put(row, ProcessingUtil.stringTodouble(value));
        }
    }

    @Override
    public ProcessResult ruleValidation(Collection<ProcessItem> nodes, Map<Long, List<ComparisonItem>> comparisons) {
        ProcessResult result = new ProcessResult();
        result.setStatus(ProcessStatus.STATUS_PROCESSED);
        result.setProcessNote("Process OK");

        boolean hasPostProcessComparisons = false;
        // Evaluate Comparisons
        for (ProcessItem node : nodes) {
            List<ComparisonItem> nodeComparisons = comparisons.getOrDefault(node.nodeId, new ArrayList<>());
            hasPostProcessComparisons = hasPostProcessComparisons || nodeComparisons.stream().anyMatch(c -> ProcessStage.POST_PROCESS.equals(c.processStage));

            //calculate
            evalCompScript(node, this.itemsByIdCode, comparisons);
        }

        result.setValidatePostProcessComparisons(hasPostProcessComparisons);

        if ((!nodes.isEmpty()) && (this.util.getErrorHandler().hasErrors(nodes.iterator().next().returnId))) {
            result.setStatus(ProcessStatus.STATUS_ERRORS);
            result.setProcessNote(this.util.getErrorHandler().getErrorMessage(nodes.iterator().next().returnId));
        }

        return result;
    }

    private void evalCompScript(ProcessItem item, Map<String, ProcessItem> values, Map<Long, List<ComparisonItem>> comparisons) {

        for (Integer row : item.values.keySet()) {
            String rowNumber = "";
            rowNumber = item.tableType == ReturnTableType.VCT ? " " + MessagesUtil.getString("net.fina.processing.rowNumber") + (row + 1) + " , " : rowNumber;

            List<ComparisonItem> comparisonItems = comparisons.get(item.nodeId);

            if (comparisonItems != null) {
                //filter DEFAULT process stage comparisons
                comparisonItems = comparisonItems.stream().filter(c -> c.processStage.equals(ProcessStage.DEFAULT)).toList();
                for (ComparisonItem comp : comparisonItems) {

                    try {

                        if (comp != null) {

                            String itemValue = item.values.get(row);
                            if (comp.leftEquation != null && !comp.leftEquation.trim().isEmpty()) {
                                itemValue = ScriptEngineFactory.get().call(new JSTree(values, item, row, this.processReturnModel, this.dateProcessor), ScriptEngineBase.createFunction(comp.leftEquation));
                            }

                            String compValue = ScriptEngineFactory.get().call(new JSTree(values, item, row, this.processReturnModel, this.dateProcessor), ScriptEngineBase.createFunction(comp.equation));

                            compValue = compValue != null ? compValue.trim() : null;
                            itemValue = itemValue != null ? itemValue.trim() : null;

                            MDTNodeDataTypes dataType = item.dataType;
                            if (item.nodeType == MDTNodeTypes.VARIABLE) {
                                dataType = MDTNodeDataTypes.NUMERIC;
                            }

                            switch (dataType) {
                                case NUMERIC:
                                    if (itemValue != null) {
                                        itemValue = Double.toString(convertAndRoundNumber(comp, itemValue));
                                    }
                                    if (compValue != null) {
                                        compValue = Double.toString(convertAndRoundNumber(comp, compValue));
                                    }
                                    break;
                            }

                            int compResult = compare(compValue, itemValue, dataType);
                            String compStr = (comp.leftEquation != null && !comp.leftEquation.trim().isEmpty() ? equationToUserFriendly(comp.leftEquation) : "");

                            switch (comp.condition) {
                                case EQUALS: {
                                    if (compResult != 0) {
                                        this.util.getErrorHandler().onComparisonError(comp, item, ErrorHandler.MessageId.COMPARISON_EQUALS, item.code, rowNumber + item.description, itemValue, compStr, compValue, equationToUserFriendly(comp.equation), row);
                                    }
                                    break;
                                }
                                case NOT_EQUALS: {
                                    if (compResult == 0) {
                                        this.util.getErrorHandler().onComparisonError(comp, item, ErrorHandler.MessageId.COMPARISON_NOT_EQUALS, item.code, rowNumber + item.description, itemValue, compStr, compValue, equationToUserFriendly(comp.equation), row);
                                    }
                                    break;

                                }
                                case GREATER: {
                                    if (compResult <= 0) {
                                        this.util.getErrorHandler().onComparisonError(comp, item, ErrorHandler.MessageId.COMPARISON_GREATER, item.code, rowNumber + item.description, itemValue, compStr, compValue, equationToUserFriendly(comp.equation), row);
                                    }
                                    break;
                                }
                                case GREATER_EQUALS: {
                                    if (compResult < 0) {
                                        this.util.getErrorHandler().onComparisonError(comp, item, ErrorHandler.MessageId.COMPARISON_GREATER_EQUALS, item.code, rowNumber + item.description, itemValue, compStr, compValue, equationToUserFriendly(comp.equation), row);
                                    }
                                    break;
                                }
                                case LESS: {
                                    if (compResult >= 0) {
                                        this.util.getErrorHandler().onComparisonError(comp, item, ErrorHandler.MessageId.COMPARISON_LESS, item.code, rowNumber + item.description, itemValue, compStr, compValue, equationToUserFriendly(comp.equation), row);
                                    }
                                    break;
                                }
                                case LESS_EQUALS: {
                                    if (compResult > 0) {
                                        this.util.getErrorHandler().onComparisonError(comp, item, ErrorHandler.MessageId.COMPARISON_LESS_EQUALS, item.code, rowNumber + item.description, itemValue, compStr, compValue, equationToUserFriendly(comp.equation), row);
                                    }
                                    break;
                                }

                                case UNKNOWN: {
                                    this.util.getErrorHandler().onComparisonError(comp, item, ErrorHandler.MessageId.UNKNOWN_COMPARISON, item.code, rowNumber + item.description);
                                }

                            }

                        }

                    } catch (Throwable t) {
                        this.util.getErrorHandler().onError(t, item, ErrorHandler.MessageId.INVALID_NUMBER_FORMAT, item.description + rowNumber, item.code, "Error evaluating comparison.");
                    }
                }
            }
        }
    }

    private int compare(String compValue, String itemValue, MDTNodeDataTypes dataType) {
        switch (dataType) {
            case NUMERIC:
                double cv = Double.parseDouble(compValue);
                double iv = Double.parseDouble(itemValue);
                return Double.compare(iv, cv);
            case TEXT:
                if (itemValue != null && compValue != null) {
                    return itemValue.compareTo(compValue);
                } else {
                    return Objects.equals(compValue, itemValue) ? 0 : 1;
                }
            case DATE:
                try {
                    Date id = util.getDateFormat().parse(itemValue);
                    Date cd = util.getDateFormat().parse(compValue);
                    if (id != null && cd != null) {
                        return id.compareTo(cd);
                    }
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                }
                break;
            case DATE_TIME:
                try {
                    Date id = util.getDateTimeFormat().parse(itemValue);
                    Date cd = util.getDateTimeFormat().parse(compValue);
                    if (id != null && cd != null) {
                        return id.compareTo(cd);
                    }
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                }
                break;
        }
        return 0;
    }

    private double convertAndRoundNumber(ComparisonItem comp, String value) {
        double result = .0;
        if ((value != null) && (value.trim().length() != 0) && (!value.equals("undefined"))) {
            try {
                result = Double.parseDouble(value);
                if (util.getPrecisionFormat() != null) {
                    String number2 = util.getPrecisionFormat().format(result);
                    result = util.getPrecisionFormat().parse(number2).doubleValue();
                }

                //Find number pattern
                DecimalFormat decimalFormat = util.getDecimalFormat();
                if (comp.numberPattern != null && !comp.numberPattern.trim().isEmpty()) {
                    decimalFormat = new DecimalFormat(comp.numberPattern);
                    decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
                }

                result = decimalFormat.parse(decimalFormat.format(result)).doubleValue();
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        } else {
            result = Double.NaN;
        }

        return result;
    }

    /**
     * calculate sub nodes
     *
     * @param item
     * @param jsTree
     */

    private void calculateSubNode(ProcessItem item, JSTree jsTree) {
        List<ProcessItem> subNodes = itemsByParentId.get(item.nodeId);
        if (subNodes != null) {
            for (ProcessItem i : subNodes) {
                if (i.nodeType == MDTNodeTypes.NODE
                        && i.nodeEvalMethod != null
                        && i.nodeEvalMethod != MDTNodeEvalMethods.UNKNOWN) {
                    calculateSubNode(i, jsTree);
                }
            }
        }
        evalAggregation(item, jsTree);
    }

    /**
     * Eval node aggregation
     *
     * @param item
     * @param jsTree
     */
    private void evalAggregation(ProcessItem item, JSTree jsTree) {
        List<ProcessItem> subNodes = itemsByParentId.get(item.nodeId);
        if (subNodes != null) {
            Collection<String> values = new ArrayList<>();

            for (ProcessItem i : subNodes) {
                values.addAll(i.values.values());
            }
            double value = Double.NaN;
            switch (item.nodeEvalMethod) {
                case SUM: {
                    value = jsTree.evalSum(values);
                    break;
                }
                case AVERAGE: {
                    value = jsTree.evalAverage(values);
                    break;
                }
                case MAX: {
                    value = jsTree.evalMax(values);
                    break;
                }
                case MIN: {
                    value = jsTree.evalMin(values);
                    break;
                }
            }
            for (Integer rowNumber : item.values.keySet()) {
                item.values.put(rowNumber, String.valueOf(value));
                item.nValues.put(rowNumber, value);
            }
        }
    }

    private String equationToUserFriendly(String equation) {
        if (equation != null) {
            equation = equation.replace("tree.lookup", "lookup");
        }
        return equation;
    }
}
