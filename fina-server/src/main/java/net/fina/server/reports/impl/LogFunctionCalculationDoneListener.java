package net.fina.server.reports.impl;

import com.sun.star.sheet.addin.InvokedFunction;
import net.fina.report.core.api.FunctionCalculationDoneListener;
import net.fina.report.model.FinaFunction;
import org.jboss.logging.Logger;


public class LogFunctionCalculationDoneListener implements FunctionCalculationDoneListener {
    private Logger log = Logger.getLogger(getClass());

    @Override
    public void done(InvokedFunction[] results) {
        log.info("--------------------------------");
        for (InvokedFunction function : results) {
            log.info(functionToString(function.functionName, function.arguments) + "|R:" + function.result);
        }
        log.info("--------------------------------");
    }

    private String functionToString(String functionName, Object[] arguments) {
        StringBuffer buff = new StringBuffer();

        buff.append("Function name: ");
        buff.append(functionName);
        buff.append(", arguments: ");

        buff.append("[");
        for (Object arg : arguments) {
            if (arg != null && arg.getClass().isArray()) {
                buff.append("[");
                for (Object innArg : (Object[]) arg) {
                    buff.append(innArg).append(", ");
                }
                if (((Object[]) arg).length > 0) {
                    buff.setLength(buff.length() - 2);
                }
                buff.append("]");
            } else {
                buff.append(arg);
            }
            buff.append(", ");
        }
        if (arguments.length > 0) {
            buff.setLength(buff.length() - 2);
        }
        buff.append("]");

        return buff.toString();

    }
}
