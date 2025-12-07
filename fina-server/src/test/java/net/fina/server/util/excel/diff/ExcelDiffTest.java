package net.fina.server.util.excel.diff;


import net.fina.server.tools.util.ExcelComparator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ExcelDiffTest {

    @Test
    public void poiDiffTest() throws IOException, InvalidFormatException {
        final String rootPath = "./src/test/resources/net/fina/server/util/excel/diff/";

        Workbook wb1 = WorkbookFactory.create(new File(rootPath + "Book2_v1.xlsx"), null, true);
        Workbook wb2 = WorkbookFactory.create(new File(rootPath + "Book2_v2.xlsx"), null, true);

        List<String> listOfDifferences = ExcelComparator.compare(wb1, wb2);
        if (listOfDifferences != null && !listOfDifferences.isEmpty()) {
            for (int i = 0; i < listOfDifferences.size(); i++) {
                System.out.println((i + 1) + ". " + listOfDifferences.get(i));
            }
        }

        wb1.close();
        wb2.close();
        assert listOfDifferences != null;
        Assert.assertEquals(3, listOfDifferences.size());
    }

}
