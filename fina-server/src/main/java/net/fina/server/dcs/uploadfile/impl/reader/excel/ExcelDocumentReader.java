package net.fina.server.dcs.uploadfile.impl.reader.excel;


import net.fina.common.client.exception.ConverterDcsTypeException;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.common.client.exception.DcsTypeException.Type;
import net.fina.server.dcs.uploadfile.impl.reader.DocumentReader;
import net.fina.server.dcs.uploadfile.impl.reader.excel.util.Conditions;
import net.fina.server.dcs.uploadfile.impl.reader.excel.util.ExcelFileDecryptorUtil;
import net.fina.server.dcs.uploadfile.impl.util.ExcelDigitalSignatureChecker;
import net.fina.server.returns.xml.Body;
import net.fina.server.returns.xml.Header;
import net.fina.server.returns.xml.Return;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Class is used to read from excel file
 */
public class ExcelDocumentReader extends DocumentReader {
    private final Logger log = Logger.getLogger(getClass());


    /**
     * @param properties
     * @throws DcsTypeException if any error occurs during reading properties
     */
    public ExcelDocumentReader(Map<String, Object> properties) throws DcsTypeException {
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
        if (matrixMappingSource.equals(MatrixMappingSource.EXCEL)) {
            if (properties.get("dcs.main.matrix") == null) {
                Type type = Type.MAIN_MATRIX_NOT_SET;
                throw new ConverterDcsTypeException(type, type.getReplaceableCode());
            }
            if (properties.get("dcs.primary.matrix") == null) {
                Type type = Type.MATRIX_NOT_SET;
                throw new ConverterDcsTypeException(type, type.getReplaceableCode());
            }
        }
        log.info("Excel document reader has been created");
    }


    /**
     * @return This list of xml files that have to be imported
     * @throws DcsTypeException if any error/exception occurs
     */
    @Override
    public List<Return> getReturns() throws DcsTypeException {
        if (file == null || file.getUploadedFile() == null || file.getUploadedFile().length == 0) {
            throw new DcsTypeException(Type.EMPTY_CONTENT_ERROR);
        }
        MatrixOptionBase matrixOptionBase = (MatrixOptionBase) properties.get("dcs.primary.matrix.option");
        byte[] uploadFileContent = file.getUploadedFile();

        if (matrixOptionBase.isEncryptEnabled()) {
            byte[] keystoreBytes = (byte[]) properties.get("dcs.security.certificate");
            uploadFileContent = ExcelFileDecryptorUtil.extractFileContent(uploadFileContent, keystoreBytes, properties.get("dcs.user.login").toString());
        }

        ExcelDigitalSignatureChecker excelDigitalSignatureChecker = new ExcelDigitalSignatureChecker(properties);
        if (excelDigitalSignatureChecker.isCheckDigitalSignature()) {
            excelDigitalSignatureChecker.checkDigitalSignature(uploadFileContent);
        }
        return getXmlReturnFromExcelFile(uploadFileContent);

    }


    private List<Return> getXmlReturnFromExcelFile(byte[] extracted) throws DcsTypeException {
        List<Return> returns = new ArrayList<>();

        String PRIMARY = properties.get("dcs.primary.matrix").toString();
        boolean sheetControl = (properties.get("dcs.excelSheetControl") == null || properties.get("dcs.excelSheetControl").toString().equals("1"));
        Conditions cond = sheetControl ? Conditions.EQUALS : Conditions.SUBSET_INV;

        try {
            ExcelMappingReader pmr = new ExcelMappingReader(PRIMARY, properties);
            ExcelDataFileReader mfr = new ExcelDataFileReader(extracted, PRIMARY, cond, properties);

            List<ExcelMappingReader.Option> primaryOptions = pmr.getOptions();
            checkDefinitionCode(primaryOptions);

            Header header = (Header) properties.get("dcs.xml.header");

            for (ExcelMappingReader.Option option : primaryOptions) {
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
                        switch (option.getType()) {
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
                                throw new IllegalArgumentException("Unsupported Table type '" + option.getType() + "' , valid values are MCT, VCT and MIXED");
                        }
                        returns.add(currentReturn);
                    } catch (DcsTypeException ex) {
                        if (ex.getReasonsList().size() > 0) {
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
            Type type = Type.CONTENT_READ_ERROR;
            throw new ConverterDcsTypeException(type, type.getReplaceableCode());
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            String sb = "${net.fina.dcs.converter.missingSubMatrix} : " + PRIMARY.substring(PRIMARY.lastIndexOf(File.separator) + 1, PRIMARY.length());
            throw new ConverterDcsTypeException(Type.CONTENT_READ_ERROR, sb);
        } catch (IllegalArgumentException ex) {
            log.error(ex.getMessage(), ex);
            Type type = Type.TABLE_TYPE_ERROR;
            throw new ConverterDcsTypeException(type, type.getReplaceableCode());
        } catch (DcsTypeException ex) {
            log.error(ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            if (ex.getMessage().contains(Type.INVALID_STRUCRURE.getCode())) {
                throw new DcsTypeException(Type.INVALID_STRUCRURE);
            } else {
                throw new DcsTypeException(Type.GENERAL_ERROR);
            }
        }

        if (conventerReasons.size() > 0) {
            throw new DcsTypeException(conventerReasons);
        }
        return returns;
    }

    private void checkDefinitionCode(List<ExcelMappingReader.Option> options) {
        List<String> codes = new ArrayList<>();
        List<String> wrongCode = new ArrayList<>();

        for (Object code : (List) properties.get("dcs.returnDefCodesList")) {
            codes.add(code.toString().toUpperCase());
        }

        for (ExcelMappingReader.Option option : options) {
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
            ex.setType(Type.GENERAL_ERROR);
            throw ex;
        }
    }

    private void checkFiFileNameCodeAndContentFiCode(ExcelDataFileReader mfr, ExcelMappingReader.Option option, Header header) {
        String fiCodeReference = option.getFiCodeReference();
        if (fiCodeReference != null && (!fiCodeReference.trim().isEmpty())) {
            String dataFileFiCode = mfr.getDataFileFiCode(option);
            if (dataFileFiCode == null || (!dataFileFiCode.trim().equals(header.getBankCode().trim()))) {
                StringBuilder sb = new StringBuilder()
                        .append("${net.fina.dcs.converter.invalidFileNameFiAndContentFiCode}")
                        .append(" File name FI code:'")
                        .append(header.getBankCode())
                        .append("', Content FI Code:'")
                        .append(dataFileFiCode)
                        .append("'");
                DcsTypeException ex = new DcsTypeException(sb.toString());
                ex.setType(Type.INVALID_FILE_NAME_FI_AND_FILE_CONTENT_FI);
                throw ex;
            }
        }
    }
}