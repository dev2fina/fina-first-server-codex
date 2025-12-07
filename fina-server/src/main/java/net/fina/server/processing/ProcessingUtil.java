package net.fina.server.processing;

import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.server.processing.model.ProcessItem;
import org.jboss.logging.Logger;

import java.io.InputStream;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: nikoloz
 * Date: 8/22/13
 * Time: 3:35 PM
 */
public class ProcessingUtil {
    private DecimalFormat decimalFormat;
    private DecimalFormat precisionFormat;
    private DateFormat dateFormat;
    private DateFormat dateTimeFormat;
    private String datePattern;
    private String dateTimePattern;
    private ErrorHandler errorHandler;
    private Calendar simpleCalendar = Calendar.getInstance();
    private long langId;

    public ProcessingUtil(ErrorHandler errorHandler, String numberPattern, String datePattern, String dateTimePattern, long langId) {
        this.errorHandler = errorHandler;
        this.datePattern = datePattern;
        this.dateTimePattern = dateTimePattern;
        decimalFormat = new DecimalFormat(numberPattern);
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        dateFormat = new SimpleDateFormat(datePattern);
        dateFormat.setLenient(false);
        dateTimeFormat = new SimpleDateFormat(dateTimePattern);
        dateTimeFormat.setLenient(false);
        this.langId = langId;
        String precisionNumberFormat = loadProcessProperties().getProperty("PrecisionNumberFormat");
        if (precisionNumberFormat != null && (!precisionNumberFormat.isEmpty())) {
            precisionFormat = new DecimalFormat(precisionNumberFormat.trim());
        }
    }

    /**
     * @param val
     * @return double
     */
    public static double stringTodouble(String val) {
        double result = .0;
        try {
            if (val != null) {
                result = Double.parseDouble(val);
            }
        } catch (Exception ignored) {
        }
        return result;
    }

    public double round(double val) {
        double result = .0;
        try {
            result = decimalFormat.parse(decimalFormat.format(val)).doubleValue();
        } catch (Exception ignored) {
        }
        return result;
    }

    public void validateDataType(ProcessItem item, String value) {
        if (value != null) {
            value = value.trim();
            if ((!value.isEmpty())) {
                switch (item.dataType) {
                    case NUMERIC: {
                        try {
                            Double.parseDouble(value);
                        } catch (Exception ex) {
                            errorHandler.onError(ex, item, ErrorHandler.MessageId.INVALID_NUMBER_FORMAT, item.description, item.code, value);
                        }
                        break;
                    }
                    case DATE: {
                        try {
                            Date date = dateFormat.parse(value);
                            simpleCalendar.clear();
                            simpleCalendar.setTime(date);
                            String year = Integer.toString(simpleCalendar.get(Calendar.YEAR));
                            if (year.length() != 4) {
                                throw new Exception("Invalid date format");
                            }
                        } catch (Exception ex) {
                            errorHandler.onError(ex, item, ErrorHandler.MessageId.INVALID_DATE_FORMAT, item.code, item.description, value, datePattern);
                        }
                        break;
                    }
                    case DATE_TIME: {
                        try {
                            Date date = dateTimeFormat.parse(value);
                            simpleCalendar.clear();
                            simpleCalendar.setTime(date);
                            String year = Integer.toString(simpleCalendar.get(Calendar.YEAR));
                            if (year.length() != 4) {
                                throw new Exception("Invalid date time format");
                            }
                        } catch (Exception ex) {
                            errorHandler.onError(ex, item, ErrorHandler.MessageId.INVALID_DATE_FORMAT, item.code, item.description, value, dateTimePattern);
                        }
                        break;
                    }
                    case TEXT: {
                        if (value.length() > 4000) {
                            errorHandler.onError(null, item, ErrorHandler.MessageId.INVALID_TEXT_LENGTH, item.description, item.code, value);
                        }
                        break;
                    }
                    case UNKNOWN: {
                        errorHandler.onError(null, item, ErrorHandler.MessageId.UNKNOWN_NODE_DATA_TYPE);
                        break;
                    }
                }
            }
        }
    }

    public void validateProcessItem(ProcessItem pItem, int rowNumber) {
        String value = pItem.getValue();
        if (value != null && (!value.isEmpty())) {
            if (pItem.nodeType != MDTNodeTypes.LIST) {
                switch (pItem.dataType) {
                    case NUMERIC: {
                        try {
                            Double.parseDouble(value);
                        } catch (Exception ex) {
                            errorHandler.onError(ex, pItem, ErrorHandler.MessageId.INVALID_NUMBER_FORMAT, "Invalid Number Format. Code:" + pItem.code + ", value:" + value + ", row number:" + rowNumber);
                        }
                        break;
                    }
                    case DATE: {
                        try {
                            Date date = dateFormat.parse(value);
                            simpleCalendar.clear();
                            simpleCalendar.setTime(date);
                            String year = Integer.toString(simpleCalendar.get(Calendar.YEAR));
                            if (year.length() != 4) {
                                throw new Exception("Invalid date format");
                            }
                        } catch (Exception ex) {
                            errorHandler.onError(ex, pItem, ErrorHandler.MessageId.INVALID_DATE_FORMAT, "Invalid Date! Code: " + pItem.code + ", Value: " + value + ". Please use pattern: " + datePattern + ", row number:" + rowNumber);
                        }
                        break;
                    }
                    case DATE_TIME: {
                        try {
                            Date date = dateTimeFormat.parse(value);
                            simpleCalendar.clear();
                            simpleCalendar.setTime(date);
                            String year = Integer.toString(simpleCalendar.get(Calendar.YEAR));
                            if (year.length() != 4) {
                                throw new Exception("Invalid date time format");
                            }
                        } catch (Exception ex) {
                            errorHandler.onError(ex, pItem, ErrorHandler.MessageId.INVALID_DATE_FORMAT, "Invalid Date Time! Code: " + pItem.code + ", Value: " + value + ". Please use pattern: " + dateTimePattern + ", row number:" + rowNumber);
                        }
                        break;
                    }
                    case TEXT: {
                        if (value.length() > 4000) {
                            errorHandler.onError(null, pItem, ErrorHandler.MessageId.INVALID_TEXT_LENGTH, "Invalid Text size. Code: " + pItem.code + ", text length:" + value.length(), ", row number:" + rowNumber);
                        }
                        break;
                    }
                    case UNKNOWN: {
                        errorHandler.onError(null, pItem, ErrorHandler.MessageId.UNKNOWN_NODE_DATA_TYPE, "Unknown Data Type. Code:" + pItem.code + ", row number:" + rowNumber);
                        break;
                    }
                }
            }
        }
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public DateFormat getDateFormat() {
        return this.dateFormat;
    }

    public DateFormat getDateTimeFormat() {
        return this.dateTimeFormat;
    }

    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    public DecimalFormat getPrecisionFormat() {
        return precisionFormat;
    }

    public long getLangId() {
        return langId;
    }

    public Properties loadProcessProperties() {
        Properties properties = new Properties();
        String property = getClass().getPackage().getName().replace('.', '/') + "/process.properties";
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(property)) {
            properties.load(in);
        } catch (Throwable t) {
            Logger.getLogger(getClass()).error(t.getMessage(), t);
        }
        return properties;
    }

    public void validateListElement(ProcessItem item, String value) {
        Set<String> values = new HashSet<>();
        if (item.nodeType == MDTNodeTypes.LIST && item.listElementValues != null) {
            for (ProcessItem pi : item.listElementValues) {
                pi.dataElementValue = pi.dataElementValue != null ? pi.dataElementValue.trim() : null;
                values.add(pi.dataElementValue);
            }
        }
        value = value != null ? value.trim() : null;

        if ((!(value == null || value.isEmpty()) || item.required) && !(values.contains(value))) {
            String shortenedListElementMessage = item.getCatalog() != null ? item.getCatalog().toString() : ListElementUtil.truncateListElementMessage(values);
            errorHandler.onError(null, item, ErrorHandler.MessageId.INVALID_LIST_ELEMENT_VALUE, item.description, item.code, value, shortenedListElementMessage);
        }
    }

    public static class ProcessItemValueComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            if (o1 != null && o2 != null) {
                double b1 = stringTodouble(o1);
                double b2 = stringTodouble(o2);
                return Double.compare(b1, b2);
            }
            return 0;
        }
    }
}
