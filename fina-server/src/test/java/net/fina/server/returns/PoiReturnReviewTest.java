package net.fina.server.returns;

import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.common.client.returns.ProcessReturnInfo;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.returns.converter.ConvertOptions;
import net.fina.server.returns.converter.PoiConverterFactory;
import net.fina.server.returns.converter.api.PoiConverter;
import net.fina.server.returns.impl.PoiReturnReviewProcessor;
import net.fina.server.returns.model.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

public class PoiReturnReviewTest {

    private ReturnReviewConfigModel configModel;
    private byte[] template;
    private String outFolderPath;

    @Before
    public void before() throws Exception {
        template = Files.readAllBytes(new File("./src/test/resources/ReturnReview/Template.xlsx").toPath());

        outFolderPath = "./target/PoiReturnReview/";

        File outFile = new File(outFolderPath);
        if (!outFile.exists()) {
            outFile.mkdir();
        }

    }


    @Test
    public void testMct() throws Exception {

        List<RDataMetaModel> rdms = new ArrayList<>();

        RDataMetaModel rDataMetaModel = new RDataMetaModel();
        rdms.add(rDataMetaModel);
        rDataMetaModel.setDefinitionId(1);
        rDataMetaModel.setFiCode("BNK");
        rDataMetaModel.setFiDescription("Test Bank");
        rDataMetaModel.setFromDate(new Date());
        rDataMetaModel.setToDate(new Date());
        rDataMetaModel.setReturnCode("Sheet1");
        rDataMetaModel.setReturnDescription("test return");
        List<RTableMetaModel> rTableMetaModels = new ArrayList<>();
        rDataMetaModel.setTables(rTableMetaModels);
        RTableMetaModel rTableMetaModel = new RTableMetaModel();
        rTableMetaModels.add(rTableMetaModel);
        rTableMetaModel.setType(ReturnTableType.MCT);
        rTableMetaModel.setDescription("TestDescritprion");
        List<RTableRowMetaModel> rows = new ArrayList<>();

        RTableRowMetaModel rt0 = new RTableRowMetaModel();
        List<RItemMetaModel> items0 = new ArrayList<>();
        for (int j = 0; j < 3; j++) {
            RItemMetaModel rItemMetaModel = new RItemMetaModel();
            rItemMetaModel.setCode("CODE-" + "0" + j);
            rItemMetaModel.setDescription("Description-" + "0" + j);
            rItemMetaModel.setDataType(MDTNodeDataTypes.NUMERIC);
            double value = 9999.9999;
            rItemMetaModel.setValue("" + value);
            rItemMetaModel.setNvalue(value);
            items0.add(rItemMetaModel);
        }
        rt0.setRowItems(items0);
        rows.add(rt0);

        for (int i = 1; i < 46; i++) {
            RTableRowMetaModel rt = new RTableRowMetaModel();
            List<RItemMetaModel> items = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                RItemMetaModel rItemMetaModel = new RItemMetaModel();
                rItemMetaModel.setCode("CODE-" + i + "" + j);
                rItemMetaModel.setDescription("Description-" + i + "" + j);
                rItemMetaModel.setDataType(MDTNodeDataTypes.NUMERIC);
                double value = new Random().nextDouble();
                rItemMetaModel.setValue("" + value);
                rItemMetaModel.setNvalue(value);
                items.add(rItemMetaModel);
            }
            rt.setRowItems(items);
            rows.add(rt);
        }
        rTableMetaModel.setRows(rows);


        ProcessReturnInfo returnInfo = new ProcessReturnInfo();
        returnInfo.setLangId(1l);
        returnInfo.setNumberFormat("###");
        returnInfo.setDateFormat("dd-MM-yyy");
        returnInfo.setDateTimeFormat("dd-MM-yyy HH:mm:ss");
        Map<ProcessReturnInfo, RDataMetaModel> infoRDataMetaModelMap = new LinkedHashMap<>();

        infoRDataMetaModelMap.put(returnInfo, rDataMetaModel);

        configModel = new ReturnReviewConfigModel.Builder()
                .df(new SimpleDateFormat("dd/MM/yyyy"), new SimpleDateFormat("dd/MM/yyyy HH:mm"))
                .langId(1)
                .infoRDataMetaModelMap(infoRDataMetaModelMap)
                .template(template)
                .build();

        byte[] result = new PoiReturnReviewProcessor(configModel)
                .process();
        String outFileName = outFolderPath + "column_styles_"
                + new SimpleDateFormat("dd_HH_mm_ss").format(new Date()) + ".xlsx";
        Files.write(new File(outFileName).toPath(), result);
    }


    @Test
    public void testVCT() throws Exception {
        List<RDataMetaModel> rdms = new ArrayList<>();

        RDataMetaModel rDataMetaModel = new RDataMetaModel();
        rdms.add(rDataMetaModel);
        rDataMetaModel.setDefinitionId(1);
        rDataMetaModel.setFiCode("BNK");
        rDataMetaModel.setFiDescription("Test Bank");
        rDataMetaModel.setFromDate(new Date());
        rDataMetaModel.setToDate(new Date());
        rDataMetaModel.setReturnCode("VCT");
        rDataMetaModel.setReturnDescription("test return");
        List<RTableMetaModel> rTableMetaModels = new ArrayList<>();
        rDataMetaModel.setTables(rTableMetaModels);
        RTableMetaModel rTableMetaModel = new RTableMetaModel();
        rTableMetaModels.add(rTableMetaModel);
        rTableMetaModel.setType(ReturnTableType.VCT);
        rTableMetaModel.setEvalMethod(MDTNodeEvalMethods.SUM);
        rTableMetaModel.setVisibleLevel(1);
        rTableMetaModel.setDescription("TestDescription");
        List<RTableRowMetaModel> rows = new ArrayList<>();


        for (int i = 0; i < 21; i++) {
            RTableRowMetaModel rt = new RTableRowMetaModel();
            List<RItemMetaModel> items = new ArrayList<>();
            double value = new Random(10000).nextDouble();
            items.add(getItem(MDTNodeEvalMethods.SUM, MDTNodeDataTypes.TEXT, "Code " + i, "ბანკის დასახელება", String.valueOf(value), value));
            items.add(getItem(MDTNodeEvalMethods.SUM, MDTNodeDataTypes.TEXT, "Code " + i, "ქვეყანა", String.valueOf(value), value));
            items.add(getItem(MDTNodeEvalMethods.SUM, MDTNodeDataTypes.TEXT, "Code " + i, "ქვეყნის კოდი", String.valueOf(value), value));
            items.add(getItem(MDTNodeEvalMethods.SUM, MDTNodeDataTypes.DATE, "Code " + i, "საქმიანი ურთიერთობის დამყარების თარიღი", "04/04/2006", value));
            items.add(getItem(MDTNodeEvalMethods.SUM, MDTNodeDataTypes.TEXT, "Code " + i, "მინიჭებული რისკის დონე", "04/04/2006", value));
            items.add(getItem(MDTNodeEvalMethods.SUM, MDTNodeDataTypes.NUMERIC, "Code " + i, "ბრუნვის მოცულობა", String.valueOf(value), value));
            rt.setRowItems(items);
            rows.add(rt);
        }
        rTableMetaModel.setRows(rows);


        ProcessReturnInfo returnInfo = new ProcessReturnInfo();
        returnInfo.setLangId(1l);
        returnInfo.setNumberFormat("#,##0.00");
        returnInfo.setDateFormat("dd/MM/yyyy");
        returnInfo.setDateTimeFormat("dd/MM/yyyy HH:mm:ss");
        Map<ProcessReturnInfo, RDataMetaModel> infoRDataMetaModelMap = new LinkedHashMap<>();

        infoRDataMetaModelMap.put(returnInfo, rDataMetaModel);

        configModel = new ReturnReviewConfigModel.Builder()
                .df(new SimpleDateFormat(returnInfo.getDateFormat()), new SimpleDateFormat(returnInfo.getDateTimeFormat()))
                .langId(1)
                .infoRDataMetaModelMap(infoRDataMetaModelMap)
                .template(template)
                .build();

        byte[] result = new PoiReturnReviewProcessor(configModel)
                .process();
        String outFileName = outFolderPath + "column_styles_"
                + new SimpleDateFormat("dd_HH_mm_ss").format(new Date()) + ".xlsx";
        Files.write(new File(outFileName).toPath(), result);
    }

    private RItemMetaModel getItem(MDTNodeEvalMethods tableEvalMethod, MDTNodeDataTypes dataTypes, String code, String description,
                                   String value,
                                   double nvalue) {
        RItemMetaModel it = new RItemMetaModel();
        it.setCode(code);
        it.setDescription(description);
        it.setNvalue(nvalue);
        it.setValue(value);
        it.setDataType(dataTypes);
        it.setTableEvalMethod(tableEvalMethod);
        it.setNodeEvalMethod(MDTNodeEvalMethods.UNKNOWN);

        return it;
    }


    @Test
    public void testConvertToHtml() throws Exception {
        ConvertOptions convertOptions = ConvertOptions.HTML;
        PoiConverter converter = PoiConverterFactory.create(convertOptions);
        Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(template));

        byte[] result = converter.convert(workbook);
        String outFileName = outFolderPath + "convert_"
                + new SimpleDateFormat("dd_HH_mm_ss").format(new Date()) + "." + convertOptions.name().toLowerCase();
        Files.write(new File(outFileName).toPath(), result);
    }

}
