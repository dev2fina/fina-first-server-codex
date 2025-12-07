package net.fina.server.dcs;

import net.fina.server.dcs.uploadfile.impl.reader.excel.util.ExcelPasswordValidator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExcelSheetProtectionValidatorTest {

    @Test
    public void validate() throws Exception {
        FileInputStream file = new FileInputStream("./src/test/resources/net/fina/server/dcs/ProtectedWorkbook.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(file);

        Sheet sheet1 = workbook.getSheet("Sheet1");
        Sheet sheet2 = workbook.getSheet("Sheet2");
        Sheet sheet3 = workbook.getSheet("NotProtected");
        assertTrue(isValid(sheet1, "fina2demo"));
        assertTrue(isValid(sheet2, "fina1demo"));
        assertFalse(isValid(sheet3, ""));
    }

    private boolean isValid(Sheet sheet, String password) {
        return ExcelPasswordValidator.checkPasswordCorrection(sheet, password);
    }
}
