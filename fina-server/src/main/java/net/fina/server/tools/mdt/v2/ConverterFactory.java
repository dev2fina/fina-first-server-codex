package net.fina.server.tools.mdt.v2;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: nick Date: 1/28/13 Time: 12:53 AM To change
 * this template use File | Settings | File Templates.
 */
public class ConverterFactory {
    private ConverterFactory() {
    }

    public static Converter createConverter(InputStream inputStream, String optionSheetName, List<String> languageCodes) throws Exception {
        return new ExcelConverter(inputStream, optionSheetName, languageCodes);
    }

    public static Converter createSimpleConverter(InputStream inputStream) throws InvalidFormatException, IOException {
        return new ExcelConverter(inputStream);
    }

}
