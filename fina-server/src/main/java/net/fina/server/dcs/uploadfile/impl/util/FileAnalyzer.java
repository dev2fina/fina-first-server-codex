package net.fina.server.dcs.uploadfile.impl.util;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.fina.server.dcs.uploadfile.impl.reader.excel.MatrixOptionBase;
import net.fina.server.returns.xml.Header;
import org.jboss.logging.Logger;

import net.fina.common.client.exception.DcsTypeException;
import net.fina.server.i18n.entity.Language;


/**
 * @version 0.1 This class is used to analyze file's name and get header
 * information
 */
public class FileAnalyzer {

    private final Logger log = Logger.getLogger(getClass());

    public enum Block {
        FI_TYPE,
        FI_CODE,
        PERIOD_TYPE,
        PERIOD_MONTH,
        PERIOD_DAY,
        PERIOD_YEAR,
        VERSION_CODE

    }

    private MatrixOptionBase matrixOption;

    private final String dailyPeriodType = "(DLY)|[D]|[d]|[J]|[j]";
    private final String weeklyPeriodType = "(WKL)|[W]|[w]|[H]|[h]";
    private final String decadePeriodType = "(DEC)|(dec)|(10d)|(10D)|[c]|[C]";
    private final String monthlyPeriodType = "(MON)|[M]|[m]";
    private final String quarterlyPeriodType = "(QUART)|[Q]|[q]|[T]|[t]";
    private final String semiAnnual = "(SEM)|[S]|[s]";
    private final String yearlyPeriodType = "(YEAR)|[Y]|[y]";
    private final String anyPeriodType = "(ANY)|(any)";

    private final String regEx;
    private final String fileName;

    private final Matcher matcher;

    private final Language language;

    private final Map<Block, Integer> blocksMap;

    {
        blocksMap = new HashMap<Block, Integer>();

        blocksMap.put(Block.FI_TYPE, 1);
        blocksMap.put(Block.FI_CODE, 2);
        blocksMap.put(Block.PERIOD_TYPE, 3);
        blocksMap.put(Block.PERIOD_MONTH, 4);
        blocksMap.put(Block.PERIOD_YEAR, 5);
        // blocksMap.put(Block.VERSION_CODE, 6);
    }


    public FileAnalyzer(String regEx, String fileName, MatrixOptionBase matrixOption, Language language) {
        this.regEx = regEx.toUpperCase().trim();
        this.fileName = fileName.toUpperCase().trim();

        Pattern pattern = Pattern.compile(regEx);
        matcher = pattern.matcher(fileName);

        this.matrixOption = matrixOption;
        this.language = language;
    }

    public String getExtension() {
        if (matcher.matches())
            return matcher.group(matcher.groupCount());
        return null;
    }

    public String getRegEx() {
        return regEx;
    }


    public String getFileName() {
        return fileName;
    }


    public Header getGeneratedHeader() throws DcsTypeException {
        Header header = new Header();
        header.setBankCode(getFiCode());
        header.setBankName("Bank Name");
        header.setLng(language.getCode().trim());
        header.setPeriodEnd(getPeriod(false));
        header.setPeriodFrom(getPeriod(true));
        header.setReturnName("Return Name");
        header.setSigned("null");
        header.setVer(getVersionCode());
        return header;
    }


    public String getPeriod(boolean from) throws DcsTypeException {

        String period = null;

        if (matcher.matches()) {

            Integer periodTypeIndex = blocksMap.get(Block.PERIOD_TYPE);

            if (periodTypeIndex != null) {
                periodTypeIndex = 3;
            }

            Object endPeriodDay = "";
            Object endPeriodMonth = "";
            Object endPeriodYear = "";

            String periodTypeGroup = matcher.group(periodTypeIndex);

            String day = "01";
            String month = "01";
            String year;

            if (!periodTypeGroup.matches(yearlyPeriodType)) {
                month = matcher.group(blocksMap.get(Block.PERIOD_MONTH));
                year = matcher.group(blocksMap.get(Block.PERIOD_YEAR));
            } else {
                blocksMap.put(Block.PERIOD_YEAR, 4);
            }
            year = matcher.group(blocksMap.get(Block.PERIOD_YEAR));

            if (periodTypeGroup.matches(anyPeriodType)) {
                blocksMap.put(Block.PERIOD_DAY, 4);
                blocksMap.put(Block.PERIOD_MONTH, 5);
                blocksMap.put(Block.PERIOD_YEAR, 6);
                int offset=3;
                month = matcher.group(blocksMap.get(Block.PERIOD_MONTH) + offset);
                year = matcher.group(blocksMap.get(Block.PERIOD_YEAR) + offset);
                day = matcher.group(blocksMap.get(Block.PERIOD_DAY) + offset);
            }

            if (periodTypeGroup.matches(dailyPeriodType) || periodTypeGroup.matches(weeklyPeriodType) || periodTypeGroup.matches(decadePeriodType)) {

                // TODO
                blocksMap.put(Block.PERIOD_DAY, 4);
                blocksMap.put(Block.PERIOD_MONTH, 5);
                blocksMap.put(Block.PERIOD_YEAR, 6);
                // blocksMap.put(Block.VERSION_CODE, 7);

                day = matcher.group(blocksMap.get(Block.PERIOD_DAY));
                month = matcher.group(blocksMap.get(Block.PERIOD_MONTH));
                year = matcher.group(blocksMap.get(Block.PERIOD_YEAR));

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
                calendar.set(Calendar.YEAR, Integer.parseInt(year));

                int maxDayOfTheMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                if (maxDayOfTheMonth < Integer.parseInt(day)) {
                    throw new DcsTypeException("maximum day of month " + month + " is " + maxDayOfTheMonth + " at " + year + "year");
                }

                if (periodTypeGroup.matches(weeklyPeriodType)) {
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
                    if (from) {
                        calendar.add(Calendar.DAY_OF_MONTH, -6);
                        year = (calendar.get(Calendar.YEAR) + "");

                        month = ((calendar.get(Calendar.MONTH) > 8 ? "" : "0") + (calendar.get(Calendar.MONTH) + 1));
                        day = ((calendar.get(Calendar.DAY_OF_MONTH) > 9 ? "" : "0") + calendar.get(Calendar.DAY_OF_MONTH));
                    }
                }

                if (periodTypeGroup.matches(decadePeriodType)) {
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
                    if (from) {
                        calendar.add(Calendar.DAY_OF_MONTH, -9);
                        year = (calendar.get(Calendar.YEAR) + "");
                        month = ((calendar.get(Calendar.MONTH) > 8 ? "" : "0") + (calendar.get(Calendar.MONTH) + 1));
                        day = ((calendar.get(Calendar.DAY_OF_MONTH) > 9 ? "" : "0") + calendar.get(Calendar.DAY_OF_MONTH));
                    }
                }

                period = day + "/" + month + "/" + year;

            } else if (from) {
                if (periodTypeGroup.matches(quarterlyPeriodType)) {
                    if (month.equals("01")) {
                        month = "01";
                    } else if (month.equals("02")) {
                        month = "04";
                    } else if (month.equals("03")) {
                        month = "07";
                    } else if (month.equals("04")) {
                        month = "10";
                    } else {
                        throw new DcsTypeException("Illegal number of quarter must be [01-04] but is " + month);
                    }
                } else if (periodTypeGroup.matches(semiAnnual)) {
                    if (month.equals("01")) {
                        month = "01";
                    } else if (month.equals("02")) {
                        month = "07";
                    } else {
                        throw new DcsTypeException("Illegal number of semi-annual must be 01,06 but is " + month);
                    }
                } else if (periodTypeGroup.matches(anyPeriodType)) {
                    month = matcher.group(blocksMap.get(Block.PERIOD_MONTH));
                    year = matcher.group(blocksMap.get(Block.PERIOD_YEAR));
                    day = matcher.group(blocksMap.get(Block.PERIOD_DAY));
                }

                period = day + "/" + month + "/" + year;

            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
                calendar.set(Calendar.YEAR, Integer.parseInt(year));

                if (periodTypeGroup.matches(monthlyPeriodType)) {

                    calendar.add(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - 1);

                } else if (periodTypeGroup.matches(quarterlyPeriodType)) {
                    calendar.add(Calendar.MONTH, 2);
                    if (month.equals("01")) {
                        endPeriodDay = "31";
                        endPeriodMonth = "03";
                    } else if (month.equals("04")) {
                        endPeriodDay = "31";
                        endPeriodMonth = "12";
                    } else if (month.equals("02")) {
                        endPeriodDay = "30";
                        endPeriodMonth = "06";
                    } else if (month.equals("03")) {
                        endPeriodDay = "30";
                        endPeriodMonth = "09";
                    } else {
                        throw new DcsTypeException("Illegal number of quarter must be [01-04] but is" + month);
                    }

                } else if (periodTypeGroup.matches(semiAnnual)) {
                    if (month.equals("01")) {
                        endPeriodDay = "30";
                        endPeriodMonth = "06";
                    } else if (month.equals("02")) {
                        endPeriodDay = "31";
                        endPeriodMonth = "12";
                    } else {
                        throw new DcsTypeException("Illegal number of semi-annual must be 01,06 but is " + month);
                    }
                } else if (periodTypeGroup.matches(yearlyPeriodType)) {
                    calendar.set(calendar.get(Calendar.YEAR), Calendar.DECEMBER, 31);
                } else if (periodTypeGroup.matches(anyPeriodType)) {
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
                }
                endPeriodDay = endPeriodDay.equals("") ? calendar.get(Calendar.DAY_OF_MONTH) : endPeriodDay;
                endPeriodMonth = endPeriodMonth.equals("") ? calendar.get(Calendar.MONTH) + 1 : endPeriodMonth;
                endPeriodYear = calendar.get(Calendar.YEAR);
                if (endPeriodDay.toString().length() == 1) {
                    endPeriodDay = "0" + endPeriodDay;
                }
                if (endPeriodMonth.toString().length() == 1) {
                    endPeriodMonth = "0" + endPeriodMonth;
                }
                period = endPeriodDay + "/" + endPeriodMonth + "/" + endPeriodYear;
            }
        }

        // Format Date
        try {
            DateFormat tmpDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            DateFormat languageDateFormat = new SimpleDateFormat(language.getDateFormat().trim());
            period = languageDateFormat.format(tmpDateFormat.parse(period));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return period;
    }


    private String getFiCode() {
        String fiCode = null;
        if (matcher.matches()) {
            Integer fiCodeIndex = blocksMap.get(Block.FI_CODE);
            if (fiCodeIndex == null) {
                fiCodeIndex = 2;
            }
            fiCode = matcher.group(fiCodeIndex);
        }
        return fiCode;
    }


    private String getVersionCode() {
        String version = null;

        if (matcher.matches()) {
            Integer versionIndex = blocksMap.get(Block.VERSION_CODE);
            if (versionIndex != null) {
                version = matcher.group(versionIndex);
            }
        }

        if (version == null) {
            version = matrixOption.getVersion();
        }

        // FIXME
        if (version.equals("")) {
            version = "ORIG";
        }

        return version;
    }

/*
    public static void main(String[] args) throws Exception {
        String daily[] = new String[]{
                "(SCC)([0-9]{7})(d)(01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31)(01|02|03|04|05|06|07|08|09|10|11|12)([0-9]{4})(\\.xls)",
                "SCC1234567d29021992.xls"};
        String weekly[] = new String[]{
                "(SCC)([0-9]{2})(w)(01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31)(01|02|03|04|05|06|07|08|09|10|11|12)([0-9]{4})(\\.xls)",
                "SCC52w05092012.xls"};
        String monthly[] = new String[]{"(SBS)([0-9]{5})(m)(01|02|03|04|05|06|07|08|09|10|11|12)([0-9]{4})(\\.xls)", "SBS12345m021992.xls"};
        String quartly[] = new String[]{"(SBS)([0-9]{5})(t)(01|02|03|04)([0-9]{4})(\\.xls)", "SBS20002t022012.xls"};
        String annual[] = new String[]{"(SBS)([0-9]{5})(y)([0-9]{4})(\\.xls)", "SBS20002y2012.xls"};
        String any[] = new String[]{"(SBS)([0-9]{5})(any)(01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31)(01|02|03|04|05|06|07|08|09|10|11|12)([0-9]{4})-(01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31)(01|02|03|04|05|06|07|08|09|10|11|12)([0-9]{4})(\\.xls)", "SBS19922any22021992-29021992.xls"};

        Map<String, Object> properties = new HashMap<String, Object>();

        Language language = new Language();
        language.setCode("en_US");
        language.setDateFormat("dd/MM/yyyy");

        properties.put("dcs.language", language);

        ExcelMatrixReader excelMatrixReader = new ExcelMatrixReader("C:\\Users\\work\\Desktop\\BRB\\Matrix.xls", properties);

        ExcelMatrixReader.Option option = null;

        List<ExcelMatrixReader.Option> options = excelMatrixReader.getOptions();

        for (ExcelMatrixReader.Option o : options) {
            if (o.getPattern() != null) {
                if (quartly[1].matches(o.getPattern())) {
                    if (option == null) {
                        option = o;
                    } else {
                        DcsTypeException.Type type = DcsTypeException.Type.MATRIX_DUPLICATED_PATTERN;
                        throw new ConverterDcsTypeException(type, type.getReplaceableCode());
                    }
                }
            }
        }

        String period[][] = new String[][]{daily, weekly, monthly, quartly, annual};
        for (int i = 0; i < period.length; i++) {

            FileAnalyzer fileAnalyzer = new FileAnalyzer(period[i][0], period[i][1], null, language);
            fileAnalyzer.getGeneratedHeader();
            System.out.println(fileAnalyzer.getExtension());
            System.out.println("");
            System.out.println("FileName   = " + period[i][1]);
            System.out.println("Start date = " + fileAnalyzer.getPeriod(true));
            System.out.println("End date   = " + fileAnalyzer.getPeriod(false));

            System.out.println(period[i][1].matches(period[i][0]));
            System.out.println("______________________________________");
            System.out.println("");
            System.out.println("");

        }
    }*/

}
