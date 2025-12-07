package net.fina.server.reg.impl;

import jakarta.annotation.Resource;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import net.fina.common.client.exception.CorruptedFileException;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.server.dcs.uploadfile.impl.reader.excel.util.ExcelFileDecryptorUtil;
import net.fina.server.dcs.uploadfile.impl.util.ExcelDigitalSignatureChecker;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.jcr.impl.FileContentManagementSession;
import net.fina.server.reg.api.RegProcessor;
import net.fina.server.reg.model.CellConfigModel;
import net.fina.server.reg.model.InputsMetaModel;
import net.fina.server.reg.model.RegProcessConfig;
import net.fina.server.reg.qualifier.RegExcelProcessor;
import net.fina.server.reg.util.RegExcelProcessingUtil;
import net.fina.server.reg.util.processor.RegFileProcessorUtilBase;
import net.fina.server.reg.validator.InputValidator;
import net.fina.server.reg.validator.RegInputValidator;
import net.fina.server.reg.validator.ValidationErrorType;
import net.fina.common.server.StatisticsLogger;
import net.fina.server.util.TempFileUtil;
import org.apache.activemq.artemis.utils.collections.ConcurrentHashSet;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;
import org.jboss.logging.Logger;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.sql.DataSource;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

@Stateless
@Local(RegProcessor.class)
@RegExcelProcessor
@Interceptors(RecordingAuditor.class)
@TransactionManagement(value = TransactionManagementType.BEAN)
public class RegExcelFileProcessor implements RegProcessor {

    @Inject
    private Logger log;
    @Resource(lookup = "java:jboss/ee/concurrency/executor/regExecutor")
    private ManagedExecutorService managedExecutorService;

    @Inject
    private FileContentManagementSession fileContentManagementSession;


    public List<String> process(RegProcessConfig config, Connection connection, RegFileProcessorUtilBase regFileProcessorUtil) {
        File tmpFile = null;
        Set<String> errors = new HashSet<>();
        try {
            tmpFile = TempFileUtil.createTempFile(config.getFileName());
            final File finalTmpFile = tmpFile;

            if (config.isEncryptEnabled()) {
                byte[] keystoreBytes = (byte[]) config.getProperties().get("dcs.security.certificate");
                fileContentManagementSession.loadUploadFileStreamInto(config.getUploadFile(), inputStream -> ExcelFileDecryptorUtil.extractFileContentToFile(inputStream, keystoreBytes, config.getUser(), finalTmpFile));
            } else {
                fileContentManagementSession.loadUploadFileStreamInto(config.getUploadFile(), inputStream -> FileUtils.copyInputStreamToFile(inputStream, finalTmpFile));
            }

            List<String> fileSheetNames = new ArrayList<>();

            try (OPCPackage container = OPCPackage.open(finalTmpFile, PackageAccess.READ);) {

                // validate digital signature
                log.info("Digital signature validation");

                validateDigitalSignature(config, container);

                log.info("Create XSSFReader");

                XSSFReader xssfReader = new XSSFReader(container);
                xssfReader.setUseReadOnlySharedStringsTable(true);
                log.info("Load Shared Strings Table");
                SharedStrings strings = xssfReader.getSharedStringsTable();
                StylesTable styles = xssfReader.getStylesTable();
                log.info("Validate and persist rows ");
                //validate sheet names
                int sheetCount = validateSheetNames(xssfReader, config, fileSheetNames, config.getAcceptedDefinitionCodes());
                log.info("Sheet Count : " + sheetCount);

                Iterator<InputStream> sheets = xssfReader.getSheetsData();
                boolean hasErros = false;

                if (sheets instanceof XSSFReader.SheetIterator) {

                    XSSFReader.SheetIterator sheetIterator = (XSSFReader.SheetIterator) sheets;
                    boolean isValid = false;
                    while (sheetIterator.hasNext()) {
                        InputStream stream = sheetIterator.next();

                        String sheetName = sheetIterator.getSheetName();

                        for (InputsMetaModel inputs : config.getInputs()) {

                            if (RegExcelProcessingUtil.checkSheetNamePattern(sheetName, inputs)) {
                                config.getInputValidatorMap().computeIfAbsent(inputs.getReturnCode(), key -> new RegInputValidator(inputs));

                                if (config.getAcceptedDefinitionCodes().contains(inputs.getReturnCode()) || (config.isPackageRejectEnabled() && !config.getAcceptedDefinitionCodes().isEmpty())) {
                                    log.warn("Return With Accepted Status : " + inputs.getReturnCode());
                                    continue;
                                }

                                final InputValidator inputValidator = config.getInputValidatorMap().get(inputs.getReturnCode());
                                String actionQuery = config.getActionQuery(inputs.getReturnCode());

                                try (PreparedStatement ps = connection.prepareStatement(actionQuery); InputStream sheetStream = stream) {

                                    processSheet(ps, inputs, styles, strings, sheetStream, inputValidator, inputs.getStartRow(), config, sheetName, regFileProcessorUtil, !hasErros);
                                    isValid = inputValidator.isValid();

                                    if (isValid) {
                                        log.info("Execute batch : " + sheetName);
                                        ps.executeBatch();
                                        log.info("Executed batch : " + sheetName);
                                    } else {
                                        hasErros = true;
                                    }

                                } catch (DcsTypeException de) {
                                    log.error(de.getMessage(), de);
                                    inputValidator.addError(ValidationErrorType.OTHER, de.getMessage());
                                } catch (CorruptedFileException cfe) {
                                    log.error(cfe.getMessage(), cfe);
                                    errors.add(cfe.getMessage());
                                } catch (Exception e) {
                                    log.error(e.getMessage(), e);
                                    errors.add(InputValidator.GENERAL_ERROR);
                                }
                            }
                        }

                    }

                    if (isValid) {
                        connection.commit();
                    } else {
                        connection.rollback();
                    }

                }

            }

        } catch (DcsTypeException dcsTypeException) {
            throw new RegFileProcessException(dcsTypeException);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return Collections.singletonList(InputValidator.GENERAL_ERROR);
        } finally {

            // delete tmp file
            try {
                tmpFile.delete();
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        }

        return new ArrayList<>(errors);
    }


    @Override
    public List<String> processParallel(RegProcessConfig config, DataSource dataSource, Connection autClosable, RegFileProcessorUtilBase regFileProcessorUtil) {
        File tmpFile = null;
        Set<String> errors = new ConcurrentHashSet<>();
        try {
            tmpFile = TempFileUtil.createTempFile(config.getFileName());
            final File finalTmpFile = tmpFile;


            if (config.isEncryptEnabled()) {

                byte[] keystoreBytes = (byte[]) config.getProperties().get("dcs.security.certificate");
                fileContentManagementSession.loadUploadFileStreamInto(config.getUploadFile(), inputStream -> ExcelFileDecryptorUtil.extractFileContentToFile(inputStream, keystoreBytes, config.getUser(), finalTmpFile));
            } else {
                fileContentManagementSession.loadUploadFileStreamInto(config.getUploadFile(), inputStream -> FileUtils.copyInputStreamToFile(inputStream, finalTmpFile));
            }

            List<String> fileSheetNames = new ArrayList<>();

            try (OPCPackage container = OPCPackage.open(finalTmpFile, PackageAccess.READ);) {

                // validate digital signature
                log.info("Digital signature validation");

                validateDigitalSignature(config, container);

                log.info("Create XSSFReader");

                XSSFReader xssfReader = new XSSFReader(container);
                xssfReader.setUseReadOnlySharedStringsTable(true);
                log.info("Load Shared Strings Table");
                SharedStrings strings = xssfReader.getSharedStringsTable();
                StylesTable styles = xssfReader.getStylesTable();
                log.info("Validate and persist rows ");
                //validate sheet names
                validateSheetNames(xssfReader, config, fileSheetNames, config.getAcceptedDefinitionCodes());

                Iterator<InputStream> sheets = xssfReader.getSheetsData();
                List<Callable<Connection>> tasks = new ArrayList<>();

                AtomicBoolean hasError = new AtomicBoolean(false);

                if (sheets instanceof XSSFReader.SheetIterator) {

                    XSSFReader.SheetIterator sheetIterator = (XSSFReader.SheetIterator) sheets;

                    while (sheetIterator.hasNext()) {
                        InputStream stream = sheetIterator.next();

                        String sheetName = sheetIterator.getSheetName();

                        for (InputsMetaModel inputs : config.getInputs()) {

                            if (RegExcelProcessingUtil.checkSheetNamePattern(sheetName, inputs)) {
                                config.getInputValidatorMap().computeIfAbsent(inputs.getReturnCode(), key -> new RegInputValidator(inputs));

                                if (config.getAcceptedDefinitionCodes().contains(inputs.getReturnCode()) || (config.isPackageRejectEnabled() && !config.getAcceptedDefinitionCodes().isEmpty())) {
                                    log.warn("Return With Accepted Status : " + inputs.getReturnCode());
                                    continue;
                                }

                                tasks.add(() -> {

                                    final InputValidator inputValidator = config.getInputValidatorMap().get(inputs.getReturnCode());
                                    String actionQuery = config.getActionQuery(inputs.getReturnCode());

                                    Connection conn = dataSource.getConnection();
                                    conn.setAutoCommit(false);

                                    try (PreparedStatement ps = conn.prepareStatement(actionQuery); InputStream sheetStream = stream) {

                                        processSheet(ps, inputs, styles, strings, sheetStream, inputValidator, inputs.getStartRow(), config, sheetName, regFileProcessorUtil, !hasError.get());
                                        boolean isValid = inputValidator.isValid();

                                        if (isValid) {
                                            log.info("Execute batch : " + sheetName);
                                            ps.executeBatch();
                                            conn.commit();
                                            log.info("Executed batch : " + sheetName);
                                        } else {
                                            hasError.set(true);
                                            conn.rollback();
                                        }

                                        log.info("commit : " + sheetName);
                                    } catch (DcsTypeException de) {
                                        log.error(de.getMessage(), de);
                                        hasError.set(true);
                                        inputValidator.addError(ValidationErrorType.OTHER, de.getMessage());
                                        conn.rollback();
                                    } catch (CorruptedFileException cfe) {
                                        hasError.set(true);
                                        log.error(cfe.getMessage(), cfe);
                                        errors.add(cfe.getMessage());
                                    } catch (Exception e) {
                                        log.error(e.getMessage(), e);
                                        hasError.set(true);
                                        errors.add(InputValidator.GENERAL_ERROR);
                                        conn.rollback();
                                    }

                                    return conn;

                                });

                            }
                        }

                    }

                    int processors = Runtime.getRuntime().availableProcessors();
                    int threadCount = processors <= 6 ? processors - 1 : 6;

                    log.info("Processors : " + processors + " threads : " + threadCount);

                    Semaphore semaphore = new Semaphore(threadCount);

                    List<Future<Connection>> futures = new CopyOnWriteArrayList<>();

                    for (Callable<Connection> task : tasks) {
                        semaphore.acquire(); // blocks if already 6 are running
                        Future<Connection> future = managedExecutorService.submit(() -> {
                            try {
                                return task.call();
                            } finally {
                                semaphore.release(); // allow next task
                            }
                        });

                        futures.add(future);
                    }

                    List<Connection> connections = new ArrayList<>();


                    for (Future<Connection> f : futures) {
                        try {
                            Connection conn = f.get();
                            if (conn != null) {
                                connections.add(conn);
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }


                    for (Connection conn : connections) {
                        closeConnection(conn);
                    }
                }

            }


        } catch (DcsTypeException dcsTypeException) {
            return Collections.singletonList(dcsTypeException.getType() != null ? "${" + dcsTypeException.getType().getCode() + "}" : dcsTypeException.getMessage());
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            if (t.getCause() instanceof DcsTypeException) {
                DcsTypeException dcsTypeException = (DcsTypeException) t.getCause();
                return Collections.singletonList(dcsTypeException.getType() != null ? "${" + dcsTypeException.getType().getCode() + "}" : dcsTypeException.getMessage());
            }
            return Collections.singletonList(InputValidator.GENERAL_ERROR);
        } finally {

            // delete tmp file
            try {
                tmpFile.delete();
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        }

        return new ArrayList<>(errors);
    }

    private void closeConnection(Connection conn) {
        try {
            conn.close();
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

    private int validateSheetNames(XSSFReader xssfReader, RegProcessConfig config, List<String> fileSheetNames, List<String> ignoreSheetNames) throws Exception {
        Set<String> invalidSheetNames = new HashSet<>();
        Iterator<InputStream> sheets = xssfReader.getSheetsData();
        int sheetCounter = 0;
        if (sheets instanceof XSSFReader.SheetIterator) {

            XSSFReader.SheetIterator sheetIterator = (XSSFReader.SheetIterator) sheets;

            while (sheetIterator.hasNext()) {
                sheetCounter++;
                try (InputStream stream = sheetIterator.next()) {

                    String sheetName = sheetIterator.getSheetName();
                    fileSheetNames.add(sheetName);

                    if (!RegExcelProcessingUtil.checkSheetNamePattern(sheetName, config.getInputs())) {
                        invalidSheetNames.add(sheetName);
                    }
                }
            }

        }

        for (InputsMetaModel im : config.getInputs()) {

            if (fileSheetNames.stream().noneMatch(s -> s.toLowerCase().contains(im.getSheetName().replace("*", "").toLowerCase())) && !ignoreSheetNames.contains(im.getSheetName())) {
                invalidSheetNames.add(im.getSheetName());
            }
        }

        if (!invalidSheetNames.isEmpty()) {
            log.error("missing sheet names: " + invalidSheetNames);
            String sb = "${net.fina.dcs.converter.sheetsAreMissing}" + " : " + String.join(", ", invalidSheetNames);
            throw new DcsTypeException(sb);
        }

        return sheetCounter;
    }

    private void processSheet(PreparedStatement ps, InputsMetaModel inputs,
                              StylesTable styles, SharedStrings strings,
                              InputStream stream, InputValidator inputValidator,
                              int startRowNum, RegProcessConfig config,
                              String sheetName, RegFileProcessorUtilBase regFileProcessorUtil,
                              boolean persistData) throws Exception {

        try (StatisticsLogger statLog = new StatisticsLogger("Reg Sheet Process :  " + sheetName);) {


            final InputSource sheetSource = new InputSource(stream);
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setNamespaceAware(true);
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader sheetParser = saxParser.getXMLReader();

            statLog.logMessage("File Name : " + config.getFileName() + " , Sheet Name : " + sheetName + " , return code : " + inputs.getReturnCode());
            statLog.logStage("Start Sheet Process [" + sheetName + "]");

            final ContentHandler handler = new RegXSSFSheetXMLHandler(styles, strings, new RegXSSFSheetXMLHandler.SheetContentsHandler() {
                private final Map<String, CellConfigModel> currentRowValues = new HashMap<>();
                private int currentRow;
                private int emptyRowSize;

                @Override
                public void startRow(int rowNum) {
                    this.currentRow = rowNum;
                }

                @Override
                public void endRow() {
                    if (currentRow >= startRowNum) {
                        if (currentRow > 0 && currentRow % 50_000 == 0) {
                            log.info(sheetName + " processed rows : " + currentRow);
                        }
                        try {

                            if (currentRowValues.isEmpty()) {
                                emptyRowSize++;
                                log.info("Empty Row Size " + emptyRowSize);
                                if (emptyRowSize >= config.getMaxEmptyRowNumber()) {
                                    // Stop reading file
                                    log.error("Reached Max Empty Rows Size " + emptyRowSize + " SheetName " + sheetName);
                                    throw new DcsTypeException("Reached Max Empty Rows Size" + emptyRowSize + " SheetName " + sheetName);
                                }
                            } else {
                                emptyRowSize = 0;
                                regFileProcessorUtil.prePersistCheck(inputs, currentRowValues, ps, currentRow, inputValidator, sheetName, config, persistData);

                                currentRowValues.clear();
                            }

                        } catch (InterruptedException ie) {
                            log.error(ie.getMessage(), ie);
                            throw new DcsTypeException(ie);
                        } catch (DcsTypeException ex) {
                            log.error(ex.getMessage(), ex);
                            throw ex;
                        } catch (Exception ex) {
                            log.error(ex.getMessage(), ex);
                            throw new RuntimeException(ex);
                        }
                    }
                }

                @Override
                public void cell(String cellReference, CellConfigModel cellConfigModel) {
                    if (currentRow >= startRowNum) {
                        currentRowValues.put(cellReference, cellConfigModel);
                    }
                }
            }, false, config.getDateFormat(), inputs.getColumnPrecisionMap());
            sheetParser.setContentHandler(handler);
            sheetParser.parse(sheetSource);
        }
    }


    private void validateDigitalSignature(RegProcessConfig config, OPCPackage opcPackage) {
        try {
            ExcelDigitalSignatureChecker excelDigitalSignatureChecker = new ExcelDigitalSignatureChecker(config.getProperties());
            excelDigitalSignatureChecker.checkDigitalSignature(opcPackage);
        } catch (DcsTypeException e) {
            throw new RegFileProcessException(e);
        }

    }
}
