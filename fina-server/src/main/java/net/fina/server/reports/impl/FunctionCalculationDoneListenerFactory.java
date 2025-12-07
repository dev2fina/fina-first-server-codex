package net.fina.server.reports.impl;


import net.fina.report.core.api.FunctionCalculationDoneListener;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FunctionCalculationDoneListenerFactory {

    private Logger log = Logger.getLogger(getClass());

    private static FunctionCalculationDoneListener functionCalculationDoneListener;


    public FunctionCalculationDoneListener getFunctionCalculationDoneListener() {
        if (functionCalculationDoneListener == null) {
            try {
                Properties properties = loadProcessProperties();
                String listener = properties.getProperty("net.fina.server.reports.functionCalculationDoneListener");
                if (listener != null) {
                    Class<?> listenerClass = Class.forName(listener);
                    functionCalculationDoneListener = (FunctionCalculationDoneListener) listenerClass.newInstance();
                }
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        }
        return functionCalculationDoneListener;
    }

    public Properties loadProcessProperties() throws IOException {
        Properties properties = new Properties();
        String property = getClass().getPackage().getName().replace('.', '/') + "/reporting.properties";
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(property)) {
            properties.load(in);
        }
        return properties;
    }
}
