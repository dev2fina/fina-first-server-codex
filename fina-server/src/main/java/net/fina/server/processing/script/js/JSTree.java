package net.fina.server.processing.script.js;

import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.processing.JSTreeDateProcessor;
import net.fina.server.processing.ProcessingUtil;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.processing.model.ProcessReturnModel;
import org.jboss.logging.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static net.fina.server.processing.ProcessingUtil.stringTodouble;

public class JSTree implements JSTreeBase {

    private final static String DEFAULT_DATA_PATTERN = "dd/MM/yyyy";
    private static final Logger log = Logger.getLogger(JSTree.class.getName());
    private final Map<String, ProcessItem> values;
    private final ProcessItem currentItem;
    private final int currentRow;
    private final ProcessReturnModel processReturnModel;
    private final JSTreeDateProcessor dateProcessor;

    public JSTree(Map<String, ProcessItem> values, ProcessItem currentItem, int currentRow, ProcessReturnModel processReturnModel, JSTreeDateProcessor dateProcessor) {
        this.values = values;
        this.currentItem = currentItem;
        this.currentRow = currentRow;
        this.processReturnModel = processReturnModel;
        this.dateProcessor = dateProcessor;
    }

    /**
     * lookup(String)
     */
    public double lookup(String code) {
        double result = .0;

        ProcessItem value = values.get(code);

        if (value != null) {

            String val = null;

            Iterator<String> valueIterator = value.values.values().iterator();
            if (valueIterator.hasNext()) {
                val = valueIterator.next();
            }

            if (value.tableType == ReturnTableType.VCT) {
                if ((currentItem.returnId == value.returnId) && (currentItem.tableId == value.tableId)) {
                    val = value.values.get(currentRow);
                    result = stringTodouble(val);
                } else {
                    switch (value.tableEvalType) {
                        case SUM: {
                            result = evalSum(value.values.values());
                            break;
                        }
                        case AVERAGE: {
                            result = evalAverage(value.values.values());
                            break;
                        }
                        case MIN: {
                            result = evalMin(value.values.values());
                            break;
                        }
                        case MAX: {
                            result = evalMax(value.values.values());
                            break;
                        }
                    }
                }
            } else {
                result = stringTodouble(val);
            }
        }
        return result;
    }

    /**
     * @param code
     * @param rowNumber
     * @return
     */
    public Double lookup(String code, int rowNumber) {
        Double result = null;

        ProcessItem value = values.get(code);

        if ((value != null)) {
            if (value.values.size() > 0) {
                String val = value.values.get(rowNumber);
                if (val != null) {
                    result = stringTodouble(val);
                }
            }

        }
        return result;
    }

    /**
     * @param code
     * @return
     */
    public String lookupString(String code) {
        String result = null;

        ProcessItem value = values.get(code);
        if (value != null) {
            Iterator<String> valueIterator = value.values.values().iterator();
            if (valueIterator.hasNext()) {
                result = valueIterator.next();
            }
            if (value.tableType == ReturnTableType.VCT) {
                if ((currentItem.returnId == value.returnId) && (currentItem.tableId == value.tableId)) {
                    result = value.values.get(currentRow);
                }
            }
        }
        return result;
    }

    /**
     * @param code
     * @param rowNumber
     * @return
     */
    public String lookupString(String code, int rowNumber) {
        ProcessItem value = values.get(code);
        if (value != null) {
            return value.values.get(rowNumber);
        }
        return null;
    }

    /**
     * @param code
     * @return
     */
    public double notrow(String code) {
        double result = .0;

        ProcessItem value = values.get(code);

        if ((value != null)) {

            switch (value.tableEvalType) {
                case SUM: {
                    result = evalSum(value.values.values());
                    break;
                }
                case MIN: {
                    result = evalMin(value.values.values());
                    break;
                }
                case MAX: {
                    result = evalMax(value.values.values());
                    break;
                }
                case AVERAGE: {
                    result = evalAverage(value.values.values());
                    break;
                }
                case UNKNOWN: {
                    // TODO Logger
                }
            }
        }
        return result;
    }

    /**
     * @param values
     * @return Sum
     */
    public double evalSum(Collection<String> values) {
        double result = .0;
        if (values != null) {
            for (String value : values) {
                if (value != null) {
                    result += stringTodouble(value);
                }
            }
        }
        return result;
    }

    /**
     * @param values
     * @return Average
     */
    public double evalAverage(Collection<String> values) {
        return evalSum(values) / values.size();
    }

    /**
     * @param values
     * @return Max
     */
    public double evalMax(Collection<String> values) {
        double result = .0;
        if (values != null) {
            String maxItem = Collections.max(values, new ProcessingUtil.ProcessItemValueComparator());
            if (maxItem != null) {
                result = stringTodouble(maxItem);
            }
        }
        return result;
    }

    /**
     * @param values
     * @return Min
     */
    public double evalMin(Collection<String> values) {
        double result = 0.;
        if (values != null) {
            String minItem = Collections.min(values, new ProcessingUtil.ProcessItemValueComparator());
            if (minItem != null) {
                result = stringTodouble(minItem);
            }
        }
        return result;
    }

    /**
     * @param nodeCode    MDT node code
     * @param criterion   Criterion: >, <, = and ''
     * @param sumNodeCode Sum vct column node code
     * @return sum
     */
    public double sumif(String nodeCode, String criterion, String sumNodeCode) {
        double result = .0;

        ProcessItem cItem = values.get(nodeCode);
        ProcessItem sItem = values.get(sumNodeCode);

        if (cItem != null && sItem != null) {

            String val = null;

            Iterator<String> valueIterator = sItem.values.values().iterator();
            if (valueIterator.hasNext()) {
                val = valueIterator.next();
            }

            if (cItem.tableType == ReturnTableType.VCT) {
                if ((currentItem.returnId == cItem.returnId) && (currentItem.tableId == cItem.tableId)) {
                    val = sItem.values.get(currentRow);
                    result = stringTodouble(val);
                } else {
                    char operation = getCriterionOperation(criterion);
                    String criterionValue = getCriterionValue(criterion, operation);

                    double criterionDoubleValue = Double.NaN;
                    if (cItem.dataType == MDTNodeDataTypes.NUMERIC) {
                        criterionDoubleValue = stringTodouble(criterionValue);
                    }

                    for (Map.Entry<Integer, String> e : cItem.values.entrySet()) {

                        String tempValue = e.getValue();

                        if (match(cItem, tempValue, operation, criterionValue, criterionDoubleValue)) {
                            String temp = sItem.values.get(e.getKey());
                            result += stringTodouble(temp);
                        }
                    }
                }
            } else {
                result = stringTodouble(val);
            }
        }
        return result;
    }

    /**
     * rowCount(String)
     */
    public int rowCount(String code) {
        int result = 0;

        ProcessItem value = values.get(code);

        if (value != null) {
            result = value.values.size();
        }

        return result;
    }

    /**
     * @param sumNodeCode Sum vct column node code
     * @return sum
     */
    public double sumifs(String sumNodeCode, String... criteria) {

        double result = .0;

        ProcessItem sumNodeItem = values.get(sumNodeCode);

        if (sumNodeItem != null) {

            String val = null;

            Iterator<String> valueIterator = sumNodeItem.values.values().iterator();
            if (valueIterator.hasNext()) {
                val = valueIterator.next();
            }

            if (sumNodeItem.tableType == ReturnTableType.VCT) {

                if ((currentItem.returnId == sumNodeItem.returnId) && (currentItem.tableId == sumNodeItem.tableId)) {
                    val = sumNodeItem.values.get(currentRow);
                    result = stringTodouble(val);
                } else if (criteria != null && criteria.length != 0) {

                    //Sum values iteration
                    for (Map.Entry<Integer, String> e : sumNodeItem.values.entrySet()) {

                        boolean match = false;

                        for (int i = 0; i < criteria.length; i=i+2) {

                            if ((i == 0 || match)) {

                                String nodeCode = criteria[i];

                                String nodeCriteria = null;
                                if (i + 1 < criteria.length) {
                                    nodeCriteria = criteria[i + 1];
                                }

                                if (nodeCriteria != null && nodeCode != null) {

                                    char operation = getCriterionOperation(nodeCriteria);
                                    String criterionValue = getCriterionValue(nodeCriteria, operation);

                                    ProcessItem criteriaNodeItem = values.get(nodeCode);

                                    if (criteriaNodeItem != null) {

                                        double criterionDoubleValue = Double.NaN;
                                        if (criteriaNodeItem.dataType == MDTNodeDataTypes.NUMERIC) {
                                            criterionDoubleValue = stringTodouble(criterionValue);
                                        }

                                        String tempValue = criteriaNodeItem.values.get(e.getKey());

                                        match = match(criteriaNodeItem, tempValue, operation, criterionValue, criterionDoubleValue);
                                    }
                                }
                            }
                        }
                        if (match) {
                            result += stringTodouble(e.getValue());
                        }
                    }
                } else {
                    return evalSum(sumNodeItem.values.values());
                }

            } else {
                result = stringTodouble(val);
            }

        }

        return result;
    }

    /**
     * @return current period from date
     */
    public String curPeriodFrom() {
        if (processReturnModel != null && processReturnModel.getFromDate() != null) {
            return this.convertPeriodToString(processReturnModel.getFromDate());
        }
        return null;
    }

    /**
     * @return current period to date
     */
    public String curPeriodTo() {
        if (processReturnModel != null && processReturnModel.getToDate() != null) {
            return this.convertPeriodToString(processReturnModel.getToDate());
        }
        return null;
    }

    /**
     * @param code     node code
     * @param fromDate period from date
     * @param toDate   period to date
     * @return period value
     */
    public double lookupPeriod(String code, String fromDate, String toDate) {
        double result = .0;

        if (this.dateProcessor != null && this.processReturnModel != null) {
            Date from = convertPeriodToDate(fromDate);
            Date to = convertPeriodToDate(toDate);

            if (from != null && to != null) {
                return this.dateProcessor.periodValue(code, processReturnModel.getFiId(), processReturnModel.getVersionId(), from, to);
            }
        }

        return result;
    }

    public String curPeriodType() {
        if (this.processReturnModel != null) {
            return this.processReturnModel.getPeriodTypeCode();
        }
        return null;
    }

    public int curRowNum() {
        return currentRow;
    }

    /**
     * @return count of digits after . (i.e. 123.123 returns 3)
     */
    public int decimalCount() {
        if (currentItem == null) {
            return 0;
        }

        String nValue = currentItem.values.get(currentRow);

        String[] parts = nValue.split("\\.");
        if (parts.length == 2) {
            return parts[1].length();
        }

        return 0;
    }

    private String convertPeriodToString(Date date) {
        DateFormat df = new SimpleDateFormat(DEFAULT_DATA_PATTERN);
        return df.format(date);
    }

    private Date convertPeriodToDate(String dateString) {
        DateFormat df = new SimpleDateFormat(DEFAULT_DATA_PATTERN);
        try {
            return df.parse(dateString);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private boolean match(ProcessItem criteriaItem, String value, char operation, String criterionValue, double criterionDoubleValue) {
        boolean match = false;

        switch (criteriaItem.dataType) {
            case NUMERIC:
                switch (operation) {
                    case '>':
                        match = stringTodouble(value) > criterionDoubleValue;
                        break;
                    case '<':
                        match = stringTodouble(value) < criterionDoubleValue;
                        break;
                    default:
                        match = stringTodouble(value) == criterionDoubleValue;
                }
                break;
            case DATE:
            case DATE_TIME:
            case TEXT:
                match = Objects.equals(criterionValue, value);
                break;
        }

        return match;
    }

    private char getCriterionOperation(String criterion) {
        if (criterion != null && criterion.length() > 0) {
            return criterion.charAt(0);
        }
        return ' ';
    }

    private String getCriterionValue(String criterion, char operation) {
        if (criterion != null && criterion.length() > 0) {
            switch (operation) {
                case '>':
                case '<':
                case '=':
                    return criterion.substring(1);
            }
        }
        return criterion;
    }
}
