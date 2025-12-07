package net.fina.server.returns.impl;

import org.jboss.logging.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ReturnFormatConstants {

    private static Logger log = Logger.getLogger(ReturnFormatConstants.class);

    /**
     * Format Constants
     */
    public static final int MAX_COLUMN = 10;
    public static final int MAX_ROW = 8;

    public static final String FI_NAME = "fina2.fi.name";
    public static final String FI_CODE = "fina2.fi.code";

    public static final String PERIOD_FROM = "fina2.period.from";
    public static final String PERIOD_TO = "fina2.period.to";
    public static final String PERIOD_TYPE_CODE = "fina2.period.type.code";
    public static final String PERIOD_TYPE_NAME = "fina2.period.type.name";

    public static final String RETURN_CODE = "fina2.return.code";
    public static final String RETURN_NAME = "fina2.return.name";
    public static final String RETURN_STATUS = "fina2.return.status";

    public static final String RETURN_TYPE_CODE = "fina2.return.type.code";

    public static final String RETURN_VERSION_CODE = "fina2.return.version.code";
    public static final String RETURN_VERSION_NAME = "fina2.return.version.name";

    public static final String USER_LOGIN = "fina2.user.login";
    public static final String USER_NAME = "fina2.user.name";

    public static List<String> getReturnFormatConstants() {
        List<String> result = new ArrayList<>();
        // get Current parameters
        Class<?> clazz = ReturnFormatConstants.class;
        Field[] fields = clazz.getFields();
        for (Field f : fields) {
            try {
                Object obj = f.get(clazz.getClass());
                if (obj instanceof String) {
                    String val = (String) obj;
                    result.add(val);
                }
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        return result;
    }

}
