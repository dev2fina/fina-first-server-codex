package net.fina.server.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Created by Oto Iantbelidze on 10/21/16.
 */
public class MappingUtil {
    private static final String MDT_CODE_REGEX = "(?<=\\\")([a-zA-Z0-9_\\-\\.]+)(?=\\\")|(?<=\\')([a-zA-Z0-9_\\-\\.]+)(?=\\')";

    public static Set<String> extractMDTCodes(String value) {
        return compileAndExtract(value);
    }

    public static Set<String> extractMDTCodes(String value, Pattern pattern) {
        return extract(value, pattern);
    }


    private static Set<String> compileAndExtract(String value) {
        Set<String> codes = new HashSet<>();
        String reg="(?<=\\\")([a-zA-Z0-9_\\-\\.]+)(?=\\\")|(?<=\\')([a-zA-Z0-9_\\-\\.]+)(?=\\')|(?<=\\:)([a-zA-Z0-9_\\-\\.]+)(?=\\,)";
        Pattern pattern = Pattern.compile(MDT_CODE_REGEX);
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            codes.add(matcher.group().trim());
        }
        return codes;
    }

    private static Set<String> extract(String value, Pattern pattern) {
        Set<String> codes = new HashSet<>();
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            codes.add(matcher.group().trim());
        }
        return codes;
    }


}
