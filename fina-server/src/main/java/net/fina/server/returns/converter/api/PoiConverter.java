package net.fina.server.returns.converter.api;

import org.apache.poi.ss.usermodel.Workbook;

public interface PoiConverter {
    byte[] convert(Workbook wb);
}
