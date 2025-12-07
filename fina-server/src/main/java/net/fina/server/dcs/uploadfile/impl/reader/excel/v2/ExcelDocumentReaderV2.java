package net.fina.server.dcs.uploadfile.impl.reader.excel.v2;

import net.fina.common.client.exception.ConverterDcsTypeException;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.dcs.uploadfile.impl.reader.DocumentReader;
import net.fina.server.dcs.uploadfile.impl.reader.excel.MatrixMappingOption;
import net.fina.server.dcs.uploadfile.impl.reader.excel.MatrixMappingSource;
import net.fina.server.dcs.uploadfile.impl.reader.excel.util.Conditions;
import net.fina.server.dcs.uploadfile.impl.util.ExcelDigitalSignatureChecker;
import net.fina.server.returns.xml.Body;
import net.fina.server.returns.xml.Header;
import net.fina.server.returns.xml.Return;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcelDocumentReaderV2 extends DocumentReader {
    private final Logger log = Logger.getLogger(getClass());

    public ExcelDocumentReaderV2(Map<String, Object> properties) throws DcsTypeException {
        super(properties);
        MatrixMappingSource matrixMappingSource = (MatrixMappingSource) properties.get("dcs.matrix.mapping.source");

        if (matrixMappingSource == null) {
            DcsTypeException.Type type = DcsTypeException.Type.MATRIX_MAPPING_SOURCE_NOT_SET;
            throw new ConverterDcsTypeException(type, type.getReplaceableCode());
        }
        if (matrixMappingSource.equals(MatrixMappingSource.UNKNOWN)) {
            DcsTypeException.Type type = DcsTypeException.Type.MATRIX_MAPPING_SOURCE_NOT_SET;
            throw new ConverterDcsTypeException(type, type.getReplaceableCode());
        }
        log.info("Excel document reader has been created");

    }


    /**
     * @return This list of xml files that have to be imported
     * @throws DcsTypeException if any error/exception occurs
     */
    @Override
    public List<Return> getReturns() throws DcsTypeException {

        ExcelDigitalSignatureChecker excelDigitalSignatureChecker = new ExcelDigitalSignatureChecker(properties);
        if (excelDigitalSignatureChecker.isCheckDigitalSignature()) {
            excelDigitalSignatureChecker.checkDigitalSignature(file.getUploadedFile());
        }

        if (file == null || file.getUploadedFile() == null || file.getUploadedFile().length == 0) {
            throw new DcsTypeException(DcsTypeException.Type.EMPTY_CONTENT_ERROR);
        }
        return getXmlReturnFromExcelFile(file);

    }


    private List<Return> getXmlReturnFromExcelFile(UploadFile file) throws DcsTypeException {
        List<Return> returns = new ArrayList<>();

//        String PRIMARY = properties.get("dcs.primary.matrix").toString();
        boolean sheetControl = (properties.get("dcs.excelSheetControl") == null || properties.get("dcs.excelSheetControl").toString().equals("1"));
        Conditions cond = sheetControl ? Conditions.EQUALS : Conditions.SUBSET_INV;

        try {
//            ExcelMappingReader pmr = new ExcelMappingReader(PRIMARY, properties);
            List<MatrixMappingOption> primaryOptions = (List<MatrixMappingOption>) properties.get("net.fina.matrix.mapping.options.data");
            ExcelDataFileReaderV2 mfr = new ExcelDataFileReaderV2(file.getUploadedFile(), primaryOptions, cond, properties);

            checkDefinitionCode(primaryOptions);

            Header header = (Header) properties.get("dcs.xml.header");

            for (MatrixMappingOption option : primaryOptions) {
                if (cond != Conditions.SUBSET_INV || mfr.hasSheet(option.getSheetName())) {
                    Return currentReturn = new Return();
                    Header newHeader = new Header();
                    newHeader.setBankCode(header.getBankCode());
                    newHeader.setBankName(header.getBankName());
                    newHeader.setLng(header.getLng());
                    newHeader.setPeriodEnd(header.getPeriodEnd());
                    newHeader.setPeriodFrom(header.getPeriodFrom());
                    newHeader.setReturnName(header.getReturnName());
                    newHeader.setSigned(header.getSigned());
                    newHeader.setVer(header.getVer());
                    newHeader.setReturnCode(option.getReturnCode());
                    Body body = new Body();
                    currentReturn.setHeader(newHeader);
                    currentReturn.setBody(body);

                    try {
                        switch (option.getTableType()) {
                            case MCT:
                                body.setItems(mfr.getMCTresult(option));
                                break;
                            case VCT:
                                body.setItems(mfr.getVCTResult(option));
                                break;
                            case MULTIVCT:
                                body.setItems(mfr.getMultiVCT(option));
                                break;
                            case MIXED:
                                body.setItems(mfr.getMixedResults(option));
                                break;
                            case COMBINED:
                                body.setItems(mfr.getCombined(option));
                                break;
                            default:
                                throw new IllegalArgumentException("Unsupported Table type '" + option.getTableType() + "' , valid values are MCT, VCT and MIXED");
                        }
                        returns.add(currentReturn);
                    } catch (DcsTypeException ex) {
                        if (!ex.getReasonsList().isEmpty()) {
                            this.conventerReasons.addAll(ex.getReasonsList());
                        } else {
                            throw ex;
                        }
                    }

                    //Check Fi Code
                    checkFiFileNameCodeAndContentFiCode(mfr, option, header);
                }
            }
        } catch (InvalidFormatException ex) {
            log.error(ex.getMessage(), ex);
            DcsTypeException.Type type = DcsTypeException.Type.CONTENT_READ_ERROR;
            throw new ConverterDcsTypeException(type, type.getReplaceableCode());
        } catch (IllegalArgumentException ex) {
            log.error(ex.getMessage(), ex);
            DcsTypeException.Type type = DcsTypeException.Type.TABLE_TYPE_ERROR;
            throw new ConverterDcsTypeException(type, type.getReplaceableCode());
        } catch (DcsTypeException ex) {
            log.error(ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            if (ex.getMessage().contains(DcsTypeException.Type.INVALID_STRUCRURE.getCode())) {
                throw new DcsTypeException(DcsTypeException.Type.INVALID_STRUCRURE);
            } else {
                throw new DcsTypeException(DcsTypeException.Type.GENERAL_ERROR);
            }
        }

        if (conventerReasons.size() > 0) {
            throw new DcsTypeException(conventerReasons);
        }
        return returns;
    }

    private void checkDefinitionCode(List<MatrixMappingOption> options) {
        List<String> codes = new ArrayList<>();
        List<String> wrongCode = new ArrayList<>();

        for (Object code : (List) properties.get("dcs.returnDefCodesList")) {
            codes.add(code.toString().toUpperCase());
        }

        for (MatrixMappingOption option : options) {
            String code = option.getReturnCode().toUpperCase();
            if (!codes.contains(code)) {
                wrongCode.add(code);
            }
        }
        if (!wrongCode.isEmpty()) {
            log.error("Wrong return codes: " + wrongCode.toString());
            StringBuilder sb = new StringBuilder();
            sb.append("${net.fina.dcs.converter.incorrectReturnCode}");
            sb.append(" : ");
            sb.append(String.join(",", wrongCode));
            DcsTypeException ex = new DcsTypeException(sb.toString());
            ex.setType(DcsTypeException.Type.GENERAL_ERROR);
            throw ex;
        }
    }

    private void checkFiFileNameCodeAndContentFiCode(ExcelDataFileReaderV2 mfr, MatrixMappingOption option, Header header) {
//        String fiCodeReference = option.getFiCodeReference();
//        if (fiCodeReference != null && (!fiCodeReference.trim().isEmpty())) {
//            String dataFileFiCode = mfr.getDataFileFiCode(option);
//            if (dataFileFiCode == null || (!dataFileFiCode.trim().equals(header.getBankCode().trim()))) {
//                StringBuilder sb = new StringBuilder()
//                        .append("${net.fina.dcs.converter.invalidFileNameFiAndContentFiCode}")
//                        .append(" File name FI code:'")
//                        .append(header.getBankCode())
//                        .append("', Content FI Code:'")
//                        .append(dataFileFiCode)
//                        .append("'");
//                DcsTypeException ex = new DcsTypeException(sb.toString());
//                ex.setType(DcsTypeException.Type.INVALID_FILE_NAME_FI_AND_FILE_CONTENT_FI);
//                throw ex;
//            }
//        }
    }
}
