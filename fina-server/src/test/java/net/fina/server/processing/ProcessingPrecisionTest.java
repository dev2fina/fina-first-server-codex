package net.fina.server.processing;

import org.junit.Assert;
import org.junit.Test;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;

// JIRA: NBG-521 (https://fina2net.atlassian.net/browse/NBG-521)
public class ProcessingPrecisionTest {

    private final String PRECISION_NUMBER_FORMAT = "#.########";
    private final String DECIMAL_FORMAT = "##0.0";
    private final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private final DecimalFormat precisionFormat = new DecimalFormat(PRECISION_NUMBER_FORMAT);

    @Test
    public void precision() throws ParseException {

        // val 1 (Simulated formula evaluation value)
        double result = getSimulatedTreeLookupValue();
        String number = precisionFormat.format(result);
        result = precisionFormat.parse(number).doubleValue();
        System.out.println("double val1: " + result);

        // val 2 (Simulated input value)
        String value2 = "6502051.85";
        double result2 = Double.parseDouble(value2);
        String number2 = precisionFormat.format(result2);
        result2 = precisionFormat.parse(number2).doubleValue();
        System.out.println("double val2: " + result2);

        // compare
        DecimalFormat decimalFormat = new DecimalFormat(DECIMAL_FORMAT);
        decimalFormat.setRoundingMode(ROUNDING_MODE);

        Number comparison = decimalFormat.parse(decimalFormat.format(result));
        Number input = decimalFormat.parse(decimalFormat.format(result2));

        System.out.println("Comparison: " + comparison);
        System.out.println("Input: " + input);
        Assert.assertEquals("comparison does not equal to input", comparison, input);
    }

    private double getSimulatedTreeLookupValue() throws ParseException {
        String x1 = "51593.0034913708";
        String x2 = "9013.456488505868";
        String x3 = "13260.558205119256";
        String x4 = "34039.32892195943";
        String x5 = "71850.57558079924";
        String x6 = "174587.71673035028";
        String x7 = "6147707.210581895";

        double sum = getFormattedValue(x1) + getFormattedValue(x2) + getFormattedValue(x3) + getFormattedValue(x4) + getFormattedValue(x5) + getFormattedValue(x6) + getFormattedValue(x7);
        System.out.println("simulated sum: " + sum);

        return sum;
    }

    private double getFormattedValue(String value) throws ParseException {
        double x1d = Double.parseDouble(value);
        String number1 = precisionFormat.format(x1d);
        return precisionFormat.parse(number1).doubleValue();
    }
}
