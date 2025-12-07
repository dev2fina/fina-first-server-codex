package net.fina.server.reg.impl;

import net.fina.common.client.exception.DcsTypeException;
import net.fina.server.reg.model.*;
import net.fina.server.reg.util.processor.RegFileProcessorUtilBase;
import net.fina.server.reg.validator.InputValidator;
import net.fina.server.reg.validator.RegInputValidator;
import net.fina.server.reg.validator.ValidationErrorType;
import net.fina.common.server.StatisticsLogger;
import org.jboss.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;

public class RegXmlHandler extends DefaultHandler {
    final List<String> processedSheetNames = new ArrayList<>();
    private final Logger log = Logger.getLogger(getClass().getName());
    private final RegProcessConfig config;
    private final Connection conn;
    private final RegFileProcessorUtilBase regFileProcessorUtil;
    private final StatisticsLogger statLog;
    private final Map<String, CellConfigModel> currentRowValues = new HashMap<>();
    private Locator locator;
    private StringBuilder elementValue;
    private String sheetName;
    private int currentRow;
    private String cellAttrName;
    private boolean processSheet;
    private String insertQuery;
    private InputValidator inputValidator;
    private PreparedStatement ps;
    private InputsMetaModel inputs;
    private int cellNumber;

    public RegXmlHandler(RegProcessConfig config, List<String> ignoreSheetNames, Connection conn, RegFileProcessorUtilBase regFileProcessorUtil) {
        this.conn = conn;
        this.statLog = new StatisticsLogger("REG Xml File Process");
        this.config = config;
        this.regFileProcessorUtil = regFileProcessorUtil;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        super.setDocumentLocator(locator);
        this.locator = locator;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (elementValue == null) {
            elementValue = new StringBuilder();
        } else {
            elementValue.append(ch, start, length);
        }
    }


    @Override
    public void startElement(String uri, String lName, String qName, Attributes attr) throws SAXException {
        switch (qName) {
            case "dataSheet":
                sheetName = attr.getValue("name");
                inputs = getInputMetaModel(config.getInputs(), sheetName);
                processSheet = inputs != null;

                if (processSheet) {
                    processedSheetNames.add(sheetName);
                    try {
                        statLog.logStage("Process Data Sheet : " + sheetName);
                        inputValidator = new RegInputValidator(inputs);
                        config.getInputValidatorMap().put(inputs.getReturnCode(), inputValidator);

                        insertQuery = config.getInsertQuery().get(inputs.getReturnCode());
                        ps = conn.prepareStatement(insertQuery);

                    } catch (Exception ex) {
                        log.error(ex.getMessage(), ex);
                    }
                }
                break;
            case "row":
                currentRow++;
                break;
            case "cell":
                cellNumber++;
                cellAttrName = attr.getValue("name");
                elementValue = new StringBuilder();
                break;

        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
            case "dataSheet":
                currentRow = 0;
                insertQuery = null;
                if (processSheet) {
                    try {
                        boolean isValid = inputValidator.isValid();

                        if (isValid) {
                            statLog.logStage("Execute batch");
                            ps.executeBatch();
                        }
                        statLog.logMessage("End Process Data Sheet : " + sheetName);
                    } catch (Exception t) {
                        throw new SAXException(t);
                    }
                }
                break;
            case "row":
                if (processSheet) {
                    try {
                        regFileProcessorUtil.prePersistCheck(inputs, currentRowValues, ps, currentRow - 1, inputValidator, sheetName, config, true);
                    } catch (InterruptedException ie) {
                        log.error(ie.getMessage());
                        processSheet = false;
                    } catch (Exception t) {
                        processSheet = false;
                        log.error(t.getMessage(), t);
                        throw new DcsTypeException(DcsTypeException.Type.GENERAL_ERROR);
                    }
                }
                currentRowValues.clear();
                cellNumber = 0;
                break;
            case "cell":
                try {
                    InputMetaModel inputMetaModel = inputs.getInputs().get(cellNumber - 1);
                    if (!cellAttrName.equals(inputMetaModel.getColumn())) {
                        String validationMessage = "Cell Name [" + cellAttrName + "] does not match config column name [" + inputMetaModel.getColumn() + "], Line [" + locator.getLineNumber() + "]  Column [" + locator.getColumnNumber() + "]";
                        inputValidator.addError(ValidationErrorType.OTHER, validationMessage);
                        log.warn(validationMessage);
                        break;
                    }

                    CellConfigModel cellConfigModel = new CellConfigModel(elementValue.toString(), elementValue.toString());
                    cellConfigModel.setRegFileType(RegFileType.XML);
                    cellConfigModel.setXmlColumnNumber(locator.getColumnNumber());
                    cellConfigModel.setXmlLineNumber(locator.getLineNumber());

                    currentRowValues.put(cellAttrName + currentRow, cellConfigModel);

                } catch (Exception ignore) {
                }
                break;
            case "dataSheets":
                List<String> requiredNames = new ArrayList<>();
                for (InputsMetaModel input : config.getInputs()) {
                    if (!processedSheetNames.contains(input.getSheetName())) {
                        requiredNames.add(input.getSheetName());
                    }
                }

                if (!requiredNames.isEmpty()) {
                    throw new DcsTypeException(Collections.singletonList("following sheets are missing or incorrect" + requiredNames));
                }
                break;
        }
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        log.error("Parse Error At Row [" + locator.getLineNumber() + "] " + "Column [" + locator.getColumnNumber() + "]", e);
        throw e;
    }


    private InputsMetaModel getInputMetaModel(List<InputsMetaModel> inputsMetaModels, String sheetName) {
        return inputsMetaModels.stream().filter(inputsMetaModel -> inputsMetaModel.getSheetName().equals(sheetName)).findFirst().orElse(null);
    }
}
