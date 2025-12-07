package net.fina.server.reports.impl;

import com.sun.star.sheet.addin.InvokedFunction;
import net.fina.report.core.api.FunctionCalculationDoneListener;

public class DoNothingFunctionCalculationDoneListener implements FunctionCalculationDoneListener {
    @Override
    public void done(InvokedFunction[] results) {
        //Nothing
    }
}
