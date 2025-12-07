package net.fina.server.returns.converter;

import net.fina.server.returns.converter.api.PoiConverter;
import net.fina.server.returns.converter.impl.PoiCsvConverter;
import net.fina.server.returns.converter.impl.PoiHtmlConverter;

public class PoiConverterFactory {
    public static PoiConverter create(ConvertOptions options) {
        switch (options) {
            case HTML:
                return new PoiHtmlConverter();
            case CSV:
                return new PoiCsvConverter();
        }

        return null;
    }
}
