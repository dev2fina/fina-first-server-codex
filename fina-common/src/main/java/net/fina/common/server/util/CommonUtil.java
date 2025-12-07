package net.fina.common.server.util;

import jakarta.security.jacc.PolicyContext;
import jakarta.servlet.http.HttpServletRequest;
import net.fina.common.client.constants.HeaderNames;
import net.fina.common.shared.dashboard.ChartContentMetaModel;
import org.apache.commons.lang.StringUtils;
import org.jboss.logging.Logger;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

    private static final Logger log = Logger.getLogger(CommonUtil.class);
    private static Long defaultServerMaxPostSize;
    private CommonUtil() {
    }

    public static double formatNumber(double d, String pattern) {
        double res = 0.0;
        try {
            DecimalFormat df = new DecimalFormat(pattern);
            res = Double.valueOf(df.format(d));
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("Could not format " + d + " with pattern " + pattern);
        }
        return res;
    }


    public static String extractStackTrace(Throwable t) {
        StringWriter me = new StringWriter();
        PrintWriter pw = new PrintWriter(me);
        t.printStackTrace(pw);
        pw.flush();
        return me.toString();
    }


    public static String getCurrentClientIpAddress() {
        try {
            HttpServletRequest request = (HttpServletRequest) PolicyContext.getContext(HttpServletRequest.class.getName());
            if (request != null) {
                String remoteAddress = request.getHeader("X-FORWARDED-FOR");
                if (remoteAddress == null || remoteAddress.isEmpty()) {
                    remoteAddress = request.getRemoteAddr();
                }
                return remoteAddress;
            }
        } catch (Exception e) {
            Logger.getLogger("CommonUtil").error(e.getMessage(), e);
        }

        String regex = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})";
        String remoteClient = "";
        String currentThreadName = Thread.currentThread().getName();
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(currentThreadName);
        if (m.find()) {
            remoteClient = m.group(0);
        }
        return remoteClient;
    }

    public static List<List<Long>> partitions(List<Long> originalList) {
        int partitionSize = 1000;
        List<List<Long>> partitions = new LinkedList<>();
        for (int i = 0; i < originalList.size(); i += partitionSize) {
            partitions.add(originalList.subList(i, i + Math.min(partitionSize, originalList.size() - i)));
        }
        return partitions;
    }

    public static boolean isClusterMode() {
        String enableClusterMode = ConfigurationUtil.get().get("ENABLE_CLUSTER");
        return (enableClusterMode != null) && (!enableClusterMode.isEmpty()) && (Integer.parseInt(enableClusterMode) > 0);
    }

    public static boolean enableReportScheduler() {
        String enableScheduler = ConfigurationUtil.get().get("ENABLE_REPORT_SCHEDULER");
        return (enableScheduler != null) && (!enableScheduler.isEmpty()) && (Integer.parseInt(enableScheduler) > 0);
    }

    public static Map<String, String> parseMap(final String input, String firstToken, String secondToken) {
        final Map<String, String> map = new HashMap<>();
        for (String pair : input.split(firstToken)) {
            String[] kv = pair.split(secondToken);
            map.put(kv[0].trim(), kv[1].trim());
        }
        return map;
    }

    public static String left(String str, int len) {
        if (str != null && str.length() > len) {
            String temp = ". This message is too long(>" + len + ")";
            str = StringUtils.left(str, len - temp.length());
            str += temp;
        }
        return str;
    }

    public static String compileMessageWithParams(String message, String... params) {
        if (params != null && params.length > 0) {
            message = String.format(message, params);
        }
        return message;
    }


    public static long getServerMaxPostSizeProperty(MBeanServer server) {
        if (defaultServerMaxPostSize == null) {

            try {
                ObjectName undertow = new ObjectName("jboss.as.expr:subsystem=undertow,server=default-server,http-listener=default");
                String postSize = (String) server.getAttribute(undertow, "max-post-size");

                if (postSize != null && !postSize.trim().isEmpty()) {
                    defaultServerMaxPostSize = Long.parseLong(postSize);
                }

            } catch (Throwable t) {
                log.error("Cannot get undertow server property 'max-post-size' ");
                log.error(t.getMessage(), t);
                defaultServerMaxPostSize = 10 * 1024 * 1024L;
            }
        }


        return defaultServerMaxPostSize;
    }

    public static boolean isEcmEnable() {
        String enableEcm = ConfigurationUtil.get().get("ECM.enable");
        return enableEcm != null && (!enableEcm.isEmpty()) && Integer.parseInt(enableEcm) > 0;
    }

    public static boolean isEcmFileImportEnabled() {
        String isEcmFileImportEnabled = ConfigurationUtil.get().get("ECM.fileImport.enable");
        return ((isEcmFileImportEnabled != null) && (!isEcmFileImportEnabled.isEmpty()) && (Integer.parseInt(isEcmFileImportEnabled) > 0));
    }

    public static ChartContentMetaModel getChartContent(String fileName, String base64ChartContent) {
        String encodingPrefix = "base64,";
        fileName = (fileName != null && !fileName.trim().isEmpty() ? fileName : "chart") + ".png";

        int contentStartIndex = base64ChartContent.indexOf(encodingPrefix) + encodingPrefix.length();
        byte[] chartContent = Base64.getDecoder().decode(base64ChartContent.substring(contentStartIndex));

        return new ChartContentMetaModel(fileName, chartContent);
    }

    public static String getClientName() {
        try {
            HttpServletRequest request = (HttpServletRequest) PolicyContext.getContext(HttpServletRequest.class.getName());
            if (request != null) {
                String clientName = request.getHeader(HeaderNames.FINA_APP_NAME_HEADER);
                if (clientName == null) {
                    clientName = request.getContextPath().equals("/fina") ? HeaderNames.FINA_APP_NAME_HEADER_VALUE : "API";
                }
                return clientName;
            }
        } catch (Exception e) {
            Logger.getLogger("CommonUtil").error(e.getMessage(), e);
        }

        return "";
    }

    private static String truncateUtf8ToBytes(String s, final int MAX_CHARS) {
        int bytes = 0, i = 0, len = s.length();

        while (i < len) {
            char ch = s.charAt(i);
            int add;
            if (ch < 0x80) {
                add = 1;
                i++;
            } else if (ch < 0x800) {
                add = 2;
                i++;
            } else if (Character.isHighSurrogate(ch)) {
                add = 4;
                i += 2;
            } else {
                add = 3;
                i++;
            }
            if (bytes + add >= MAX_CHARS) break;
            bytes += add;
        }
        return (i >= len) ? s : s.substring(0, i);

    }


}
