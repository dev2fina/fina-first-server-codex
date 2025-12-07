package net.fina.server.reports.util;

import fina2.reportoo.ReportInfo;
import fina2.ui.sheet.openoffice.OOIterator;
import net.fina.common.client.exception.FinATypeException;
import net.fina.report.model.Iterator;
import net.fina.reporting.model.ParameterMetaModel;

import java.util.Map;

public class ReportDimensionHelper {

    public static void validateParameter(ParameterMetaModel parameter, ReportInfo reportInfo) throws FinATypeException {
        String paramName = parameter.getName();
        if (paramName == null || paramName.isBlank()) {
            throw new FinATypeException("Parameter Name  is null or empty");
        }
        if (reportInfo.parameters.containsKey(paramName)) {
            throw new FinATypeException("Parameter with same name already exists");
        }

    }


    public static void updateDependentDimensionParameter(String existingParamName, String newParamName, ReportInfo reportInfo) {
        for (Map.Entry<String, Iterator> entry : reportInfo.iterators.entrySet()) {
            OOIterator iter = (OOIterator) entry.getValue();


            switch (iter.getType()) {
                case Iterator.VCT_ITERATOR, Iterator.PLAIN_VCT_ITERATOR -> {
                    if (iter.getAggergateParameter() != null && iter.getAggergateParameter().trim().equals(existingParamName.trim())) {
                        iter.setAggregateParameter(newParamName.trim());
                    }
                    if (iter.getPeriodParameter() != null && iter.getPeriodParameter().trim().equals(existingParamName.trim())) {
                        iter.setPeriodParameter(newParamName.trim());
                    }
                }
                default -> {
                    if (iter.getParameter() != null && iter.getParameter().trim().equals(existingParamName.trim())) {
                        iter.setParameter(newParamName.trim());
                    }
                }
            }
        }

    }

}
