package net.fina.server.reg.util;

import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.server.processing.model.ComparisonItem;
import net.fina.server.reg.model.InputTypeEnum;
import net.fina.server.reg.model.RegProcessConfig;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RegUtil {
    private static final Logger log = Logger.getLogger(RegUtil.class.getName());

    public static InputTypeEnum getTypeEnum(MDTNodeDataTypes dataType) {
        switch (dataType) {
            case NUMERIC:
                return InputTypeEnum.NUMBER;
            case DATE:
                return InputTypeEnum.TIMESTAMP;
            case DATE_TIME:
                return InputTypeEnum.DATETIME;
            case TEXT:
                return InputTypeEnum.STRING;
        }
        return InputTypeEnum.STRING;
    }

    public static Map<String, List<String>> getRegReturnErrorMap(List<String> fileErrors) {
        Map<String, List<String>> result = new HashMap<>();

        for (String error : fileErrors) {
            String returnCode = error.split(":")[0].trim();
            String errorMessage = error.substring(error.indexOf(":") + 1);
            if (result.containsKey(returnCode)) {
                result.get(returnCode).add(errorMessage);
            } else {
                result.put(returnCode, Stream.of(errorMessage).collect(Collectors.toList()));
            }
        }

        return result;
    }


    public static double convertAndRoundNumber(ComparisonItem comp, String value, RegProcessConfig config) {
        double result = .0;
        if ((value != null) && (value.trim().length() != 0) && (!value.equals("undefined"))) {
            try {
                result = Double.parseDouble(value);

                //Find number pattern
                DecimalFormat decimalFormat = new DecimalFormat(config.getNumberFormat());
                if (comp.numberPattern != null && !comp.numberPattern.trim().isEmpty()) {
                    decimalFormat = new DecimalFormat(comp.numberPattern);
                    decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
                }

                result = decimalFormat.parse(decimalFormat.format(result)).doubleValue();
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        } else {
            result = Double.NaN;
        }

        return result;
    }

    public static int compare(String compValue, String itemValue, InputTypeEnum dataType, RegProcessConfig config) {
        switch (dataType) {
            case NUMBER:
            case BIGNUMBER:
            case INTEGER:
                double cv = Double.parseDouble(compValue);
                double iv = Double.parseDouble(itemValue);
                return Double.compare(iv, cv);
            case STRING:
                if (itemValue != null && compValue != null) {
                    return itemValue.compareTo(compValue);
                } else {
                    return Objects.equals(compValue, itemValue) ? 0 : 1;
                }
            case DATE:
            case TIMESTAMP:
                try {
                    Date id = new Date(new BigDecimal(itemValue).longValue());
                    Date cd = new Date(new BigDecimal(compValue).longValue());
                    LocalDate localDate1 = id.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate localDate2 = cd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    return localDate1.compareTo(localDate2);
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                }
                break;
            case DATETIME:
                try {
                    Date id = new Date(new BigDecimal(itemValue).longValue());
                    Date cd = new Date(new BigDecimal(compValue).longValue());
                    return id.compareTo(cd);
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                }
                break;
        }
        return 0;
    }


}
