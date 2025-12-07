package net.fina.server.returns.impl;

import net.fina.common.client.returns.ProcessReturnInfo;
import net.fina.fsop.view.model.FsopItemMetaModel;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Collection;

public class FsopJSTreeAggregate {

    private ProcessReturnInfo returnInfo;

    public FsopJSTreeAggregate(ProcessReturnInfo returnInfo) {
        this.returnInfo = returnInfo;
    }

    /**
     * Evaluates Sum
     *
     * @param list of ReturnProcessItem
     * @return Sum of value properties of ReturnProcessItem
     */
    public double evalSum(Collection<FsopItemMetaModel> list) {
        double sum = 0.0d;
        String numberFormat = returnInfo.getNumberFormat();
        DecimalFormat format = new DecimalFormat(numberFormat);
        for (FsopItemMetaModel item : list) {
            try {
                String value = item.getValue();
                if (value != null && !value.trim().isEmpty()) {
                    sum += format.parse(item.getValue()).doubleValue();
                }
            } catch (Exception ignored) {
            }
        }
        return sum;
    }

    /**
     * Evaluates Average
     *
     * @param list of ReturnProcessItem
     * @return Average of value properties of ReturnProcessItem
     */
    public double evalAverage(Collection<FsopItemMetaModel> list) {
        if (list == null || list.size() == 0)
            return 0;
        else
            return evalSum(list) / list.size();
    }

    /**
     * Evaluates Min
     *
     * @param list of ReturnProcessItem
     * @return Minimum value of ReturnProcessItem's value propery
     */
    public double evalMin(Collection<FsopItemMetaModel> list) {
        double min = 0.0;
        try {
            if (list != null && list.size() > 0) {
                String numberFormat = returnInfo.getNumberFormat();
                DecimalFormat format = new DecimalFormat(numberFormat);
                FsopItemMetaModel[] array = new FsopItemMetaModel[list.size()];
                list.toArray(array);
                min = format.parse(array[0].getValue()).doubleValue();
                for (int i = 1; i < array.length; i++) {
                    if (format.parse(array[i].getValue()).doubleValue() < min) {
                        min = format.parse(array[i].getValue()).doubleValue();
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return min;
    }

    /**
     * Evaluates Max
     *
     * @param list of ProcessItems
     * @return Max of ReturnProcessItem's value properties
     */
    public double evalMax(Collection<FsopItemMetaModel> list) {
        double max = 0.0;
        try {
            if (list != null && list.size() > 0) {
                String numberFormat = returnInfo.getNumberFormat();
                DecimalFormat format = new DecimalFormat(numberFormat);

                FsopItemMetaModel[] array = new FsopItemMetaModel[list.size()];
                list.toArray(array);

                max = format.parse(array[0].getValue()).doubleValue();
                for (int i = 1; i < array.length; i++) {
                    if (format.parse(array[i].getValue()).doubleValue() > max) {
                        max = format.parse(array[i].getValue()).doubleValue();
                    }
                }
            }
        } catch (ParseException | NumberFormatException ignored) {
        }
        return max;
    }
}
