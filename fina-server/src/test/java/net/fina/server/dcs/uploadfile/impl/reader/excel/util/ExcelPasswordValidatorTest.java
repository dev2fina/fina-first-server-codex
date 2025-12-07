package net.fina.server.dcs.uploadfile.impl.reader.excel.util;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ExcelPasswordValidatorTest {

    private void check(Workbook m, Workbook p) {
        for (int i = 0; i < m.getNumberOfSheets(); i++) {
            String sheetName = m.getSheetName(i);
            Sheet mSheet = m.getSheet(sheetName);
            Sheet pSheet = p.getSheet(sheetName);
//            Assert.assertTrue(ExcelPasswordValidator.checkSheetProtectionPassword(mSheet, pSheet));
        }
    }

    private Workbook createWorkbook(String fileName) throws IOException, InvalidFormatException {
        return WorkbookFactory.create(new File(fileName));
    }

    @Test
    public void test_1() {
        try {
            check(
                    createWorkbook("./src/test/resources/net.fina.server.dcs.uploadfile.impl.reader.excel.util/test_1_m.xlsx"),
                    createWorkbook("./src/test/resources/net.fina.server.dcs.uploadfile.impl.reader.excel.util/test_1_p.xlsx")
            );
        } catch (IOException | InvalidFormatException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void test_2() {
        try {
            check(
                    createWorkbook("./src/test/resources/net.fina.server.dcs.uploadfile.impl.reader.excel.util/test_2_m.xlsx"),
                    createWorkbook("./src/test/resources/net.fina.server.dcs.uploadfile.impl.reader.excel.util/test_2_p.xls")
            );
        } catch (IOException | InvalidFormatException e) {
            System.err.println(e.getMessage());
        }
    }
}
