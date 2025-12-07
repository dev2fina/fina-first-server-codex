package net.fina.server.tools.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.fis.FiTypeModel;
import net.fina.common.client.tools.mdt.tester.MdtTesterResultMetaModel;
import net.fina.server.tools.mdt.v2.StreamedContent;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Oleg
 * Date: 12/15/13
 * Time: 6:42 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ToolsLocal {

    // Cache Manager
    void restartCache();

    // Mdt To XML
    void generateMdtXml(List<String> propertyNames, boolean allReturns, String returnDefinitionCode, String outFolderLocation);

    void generateMdtXml(Map<String, Boolean> activate, boolean allReturns, String returnDefinitionCode, String outFolderLocation);

    // Release Submission tool and MDT
    void updateTemplates(String submissionToolFileName, String signBatFile, String submissionToolEmptyFileName, Map<String, Boolean> activate);

    // Return To XML
    void convertReturnToXml(Date fromDate, Date toDate, String languageCode, String outFolderLocation);

    // MDT Generator
    StreamedContent convert(byte[] content, String optionalSheet, String fileName, boolean enableDownload) throws FinATypeException;

    List<String> getSheetNames(byte[] content);

    //FinA File Decryption
    byte[] decryptFinaFile(byte[] certificate, char[] password, byte[] finaFile) throws Exception;

    byte[] decryptFinaFile(byte[] finaFile) throws Exception;

    String getReleaseResult();

    Double getReleaseProgress();

    List<MdtTesterResultMetaModel> runMdtTester(long parentNodeId);

    List<String> getExcelFileDiff(byte[] wb1, byte[] wb2) throws FinATypeException;

    void releaseMDT(List<String> fiTypeCodes);

    List<FiTypeModel> loadReleaseFiTypes();
}
