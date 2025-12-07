package net.fina.server.mdt.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MDTNodeUtil {

    public static final String MDT_CODE_REGEX = "(?<=\\\")([a-zA-Z0-9_\\-\\.]+)(?=\\\")|(?<=\\')([a-zA-Z0-9_\\-\\.]+)(?=\\')";

    public static Set<String> extractCodes(String string, Pattern pattern) {
        HashSet<String> codes = new HashSet<>();
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            String code = matcher.group();
            if (!skipDependency(string, code)) {
                codes.add(code);
            }
        }
        return codes;
    }

    private static boolean skipDependency(String text, String code) {
        return text.contains("lookupPeriod(\"" + code + "\"") || text.contains("lookupPeriod('" + code + "'");
    }

}
