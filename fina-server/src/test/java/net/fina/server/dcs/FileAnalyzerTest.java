package net.fina.server.dcs;

import net.fina.common.client.exception.ConverterDcsTypeException;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.server.dcs.uploadfile.impl.reader.excel.ExcelMatrixReader;
import net.fina.server.dcs.uploadfile.impl.reader.excel.MatrixOptionBase;
import net.fina.server.dcs.uploadfile.impl.util.FileAnalyzer;
import net.fina.server.i18n.entity.Language;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * nikoloz on 10/3/14.
 *
 * @version 0.1 parse patterns
 */
public class FileAnalyzerTest {

    @Test
    public void testCase1() {
        String daily[] = new String[]{"(SCC)([0-9]{7})(d)(01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31)(01|02|03|04|05|06|07|08|09|10|11|12)([0-9]{4})(\\.xls)", "SCC1234567d29021992.xls"};
        String weekly[] = new String[]{"(SCC)([0-9]{2})(w)(01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31)(01|02|03|04|05|06|07|08|09|10|11|12)([0-9]{4})(\\.xls)", "SCC52w05092012.xls"};

        String decade[] = new String[]{"(SCC)([0-9]{7})(dec)(01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31)(01|02|03|04|05|06|07|08|09|10|11|12)([0-9]{4})(\\.xls)", "SCC1234567dec10092019.xls"};

        String monthly[] = new String[]{"(SBS)([0-9]{5})(m)(01|02|03|04|05|06|07|08|09|10|11|12)([0-9]{4})(\\.xls)", "SBS12345m021992.xls"};
        String quarterly[] = new String[]{"(SBS)([0-9]{5})(t)(01|02|03|04)([0-9]{4})(\\.xls)", "SBS20002t022012.xls"};
        String semiAnnual[] = new String[]{"(SBS)([0-9]{5})(s)(01|02|03|04)([0-9]{4})(\\.xls)", "SBS20002s022012.xls"};
        String annual[] = new String[]{"(SBS)([0-9]{5})(y)([0-9]{4})(\\.xls)", "SBS20002y2012.xls"};
        String any[] = new String[]{"(SBS)([0-9]{5})(any)(01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31)(01|02|03|04|05|06|07|08|09|10|11|12)([0-9]{4})-(01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31)(01|02|03|04|05|06|07|08|09|10|11|12)([0-9]{4})(\\.xls)", "SBS19922any22021992-29021992.xls"};

        Language language = new Language();
        language.setCode("en_US");
        language.setDateFormat("dd/MM/yyyy");

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("dcs.language", language);
        properties.put("dcs.primary.matrix.option", null);

        ExcelMatrixReader excelMatrixReader = new ExcelMatrixReader("./src/test/resources/net/fina/server/dcs/FileAnalyzerTestMatrix.xls", properties);

        List<MatrixOptionBase> options = excelMatrixReader.getOptions();

        String period[][] = new String[][]{daily, weekly, decade, monthly, quarterly, semiAnnual, annual,any};

        for (int i = 0; i < period.length; i++) {
            String fileName = period[i][1];

            MatrixOptionBase option = null;

            for (MatrixOptionBase o : options) {
                if (o.getPattern() != null) {

                    String patterns = o.getPattern();
                    if (fileName.matches(patterns)) {
                        if (option == null) {
                            option = o;
                        } else {
                            DcsTypeException.Type type = DcsTypeException.Type.MATRIX_DUPLICATED_PATTERN;
                            throw new ConverterDcsTypeException(type, type.getReplaceableCode());
                        }
                    }
                }
            }

            if (option != null) {
                properties.put("dcs.primary.matrix.option", option);

                String testPattern = option.getPattern();
                FileAnalyzer fileAnalyzer = new FileAnalyzer(testPattern, fileName, option, language);
                fileAnalyzer.getGeneratedHeader();
                System.out.println(fileAnalyzer.getExtension());
                System.out.println("");
                System.out.println("FileName   = " + fileName);
                System.out.println("Start date = " + fileAnalyzer.getPeriod(true));
                System.out.println("End date   = " + fileAnalyzer.getPeriod(false));

                System.out.println(fileName.matches(testPattern));
                System.out.println("______________________________________");
                System.out.println("");
                System.out.println("");

                switch (fileName) {
                    case "SCC1234567d29021992.xls": {
                        Assert.assertEquals("Start Date", "29/02/1992", fileAnalyzer.getPeriod(true));
                        Assert.assertEquals("End Date", "29/02/1992", fileAnalyzer.getPeriod(false));
                        break;
                    }
                    case "SCC52w05092012.xls": {
                        Assert.assertEquals("Start Date", "30/08/2012", fileAnalyzer.getPeriod(true));
                        Assert.assertEquals("End Date", "05/09/2012", fileAnalyzer.getPeriod(false));
                        break;
                    }

                    case "SCC1234567dec10092019.xls": {
                        Assert.assertEquals("Start Date", "01/09/2019", fileAnalyzer.getPeriod(true));
                        Assert.assertEquals("End Date", "10/09/2019", fileAnalyzer.getPeriod(false));
                        break;
                    }

                    case "SBS12345m021992.xls": {
                        Assert.assertEquals("Start Date", "01/02/1992", fileAnalyzer.getPeriod(true));
                        Assert.assertEquals("End Date", "29/02/1992", fileAnalyzer.getPeriod(false));
                        break;
                    }

                    case "SBS20002t022012.xls": {
                        Assert.assertEquals("Start Date", "01/04/2012", fileAnalyzer.getPeriod(true));
                        Assert.assertEquals("End Date", "30/06/2012", fileAnalyzer.getPeriod(false));
                        break;
                    }

                    case "SBS20002s022012.xls": {
                        Assert.assertEquals("Start Date", "01/07/2012", fileAnalyzer.getPeriod(true));
                        Assert.assertEquals("End Date", "31/12/2012", fileAnalyzer.getPeriod(false));
                        break;
                    }

                    case "SBS20002y2012.xls": {
                        Assert.assertEquals("Start Date", "01/01/2012", fileAnalyzer.getPeriod(true));
                        Assert.assertEquals("End Date", "31/12/2012", fileAnalyzer.getPeriod(false));
                        break;
                    }
                    case "SBS19922any22021992-29021992.xls": {
                        Assert.assertEquals("Start Date", "22/02/1992", fileAnalyzer.getPeriod(true));
                        Assert.assertEquals("End Date", "29/02/1992", fileAnalyzer.getPeriod(false));
                        break;
                    }
                    default: {
                        System.err.println(fileName + " not defined");
                    }
                }

            } else {
                System.err.println(i + " Matrix options not found");
            }

        }
    }
}
