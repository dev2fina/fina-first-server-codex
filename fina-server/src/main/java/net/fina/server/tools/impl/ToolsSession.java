package net.fina.server.tools.impl;

import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.ReturnDefinitionFilter;
import net.fina.common.client.fis.FiTypeModel;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.client.returns.ReturnModel;
import net.fina.common.client.tools.mdt.tester.MdtTesterResultMetaModel;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.common.shared.FilterConfig;
import net.fina.common.shared.FilterConfigKey;
import net.fina.server.dcs.uploadfile.impl.converter.ConverterUtil;
import net.fina.server.fi.entity.FiType;
import net.fina.server.fi.proxy.FiProxySession;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.cache.SysStringCacheManager;
import net.fina.server.i18n.entity.Language;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTComparison;
import net.fina.server.mdt.entity.MDTDependentNode;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.mdt.impl.MDTCacheManager;
import net.fina.server.mdt.impl.MDTXmlVersion;
import net.fina.server.mdt.xml.v1.Mdt;
import net.fina.server.mdt.xml.v2.Node;
import net.fina.server.misc.ProductHelper;
import net.fina.server.processing.api.ProcessingStoreLocal;
import net.fina.server.returns.api.ReturnDefinitionLocal;
import net.fina.server.returns.api.ReturnLocal;
import net.fina.server.returns.entity.ReturnDefinition;
import net.fina.server.security.api.PropertyLocal;
import net.fina.server.security.product.OSTLicense;
import net.fina.server.st.crypto.SecurityManagerBase;
import net.fina.server.st.crypto.SecurityManagerFactory;
import net.fina.server.st.impl.MDTReleaseSession;
import net.fina.server.tools.api.ToolsLocal;
import net.fina.server.tools.mdt.v2.Converter;
import net.fina.server.tools.mdt.v2.ConverterFactory;
import net.fina.server.tools.mdt.v2.DefaultStreamedContent;
import net.fina.server.tools.mdt.v2.StreamedContent;
import net.fina.server.tools.mdt.v2.tester.impl.MdtTesterImpl;
import net.fina.server.tools.util.ExcelComparator;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jboss.logging.Logger;

import java.io.*;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * User: Oleg
 * Date: 12/15/13
 * Time: 6:43 PM
 */
@Stateless
@Local(ToolsLocal.class)
@Interceptors(RecordingAuditor.class)
public class ToolsSession implements ToolsLocal {

    private static volatile Double PROGRESS;
    private final Logger log = Logger.getLogger(getClass());
    @EJB
    private SysStringCacheManager sysStringCacheManager;
    @EJB
    private ReturnDefinitionLocal returnDefinitionLocal;
    @EJB
    private LanguageLocal languageLocal;
    @EJB
    private PropertyLocal propertyLocal;
    @EJB
    private MDTNodeLocal mdtNodeLocal;
    @EJB
    private ReturnLocal returnLocal;
    @EJB
    private ProcessingStoreLocal processingStoreLocal;
    @EJB
    private MDTReleaseSession releaseSession;
    @Inject
    private MDTCacheManager mdtCacheManager;
    @Inject
    private FiProxySession fiProxySession;

    private String submissionToolAndMdtReleaseResult;
    private int maxProgress;
    private int progressCount;
    /**
     * MDT Generator(Converter)
     */

    private StreamedContent downloadFile;

    /**
     * Cache Manager
     */
    @Override
    public void restartCache() {
        sysStringCacheManager.clearCache();
        log.info("Clear sys string cache.");

        sysStringCacheManager.createCache();
        log.info("Create sys string cache.");
    }

    @Override
    public void generateMdtXml(List<String> propertyNames, boolean allReturns, String returnDefinitionCode, String outFolderLocation) {
        Map<String, Boolean> propertyMap = getMDTxmlActiveProperties();
        for (Map.Entry<String, Boolean> entry : propertyMap.entrySet()) {
            if (propertyNames.contains(entry.getKey())) {
                entry.setValue(true);
            } else {
                entry.setValue(false);
            }
        }

        generateMdtXml(propertyMap, allReturns, returnDefinitionCode, outFolderLocation);
    }

    /**
     * MDT to XML
     */
    @Override
    public void generateMdtXml(Map<String, Boolean> activate, boolean allReturns, String returnDefinitionCode, String outFolderLocation) {

        try {
            JAXBContext context = JAXBContext.newInstance("net.fina.server.mdt.xml.v1");

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            log.info("Load Comparisons.");
            Map<Long, List<MDTComparison>> comparisons = new HashMap<>();
            for (MDTComparison comparison : mdtNodeLocal.loadComparisons(new HashMap<>(), null)) {
                long nid = comparison.getNode().getId();
                comparisons.putIfAbsent(nid, new ArrayList<>());
                comparisons.get(nid).add(comparison);
            }

            log.info("Load Comparisons.");
            Map<Long, String> languagesMap = new HashMap<>();
            for (Long id : languageLocal.getLanguageIds()) {
                languagesMap.put(id, languageLocal.getLanguageCodeById(id).trim());
            }

            log.info("Get all mdt dependent nodes");
            final List<MDTDependentNode> allMdtDependentNodes = processingStoreLocal.loadAllMdtDependentNodes();
            final Map<Long, Set<String>> dependencies = new HashMap<>();
            final Map<Long, Set<Long>> dependenciesLongMap = new HashMap<>();
            for (MDTDependentNode depNode : allMdtDependentNodes) {
                long dependentNodeId = depNode.getDepNode().getDependentNodeId();

                if (!dependencies.containsKey(dependentNodeId)) {
                    dependencies.put(dependentNodeId, new HashSet<>());
                }
                dependencies.get(dependentNodeId).add(mdtNodeLocal.getMdtNodeCodeById(depNode.getDepNode().getNodeId()));

                if (!dependenciesLongMap.containsKey(dependentNodeId)) {
                    dependenciesLongMap.put(dependentNodeId, new HashSet<>());
                }
                dependenciesLongMap.get(dependentNodeId).add(depNode.getDepNode().getNodeId());
            }

            log.info("Get mdt nodes by parent id");
            final Map<Long, List<MDTNode>> allMdtNodesByParentId = processingStoreLocal.loadMdtNodesByParentId();

            Map<Integer, String> filter = null;
            if (!allReturns) {
                filter = new HashMap<>();
                filter.put(1, returnDefinitionCode);
            }

            HashSet<ReturnDefinition> definitions = returnDefinitionLocal.load(filter, true);
            for (ReturnDefinition rd : definitions) {
                Mdt mdt = returnDefinitionLocal.loadReturnDefinitionMdtV1(rd, activate, comparisons, languagesMap, dependenciesLongMap, allMdtNodesByParentId);
                saveMdtXml(
                        marshaller,
                        mdt,
                        rd.getCode().trim() + ".mdt.xml",
                        outFolderLocation
                );
            }

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public void saveMdtXml(Marshaller marshaller, Mdt mdt, String fileName, String outFolderLocation) {
        try {
            marshaller.marshal(mdt, new File(outFolderLocation + "" + File.separator + fileName));
            log.info("Save MDT xml (Folder " + outFolderLocation + ")");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }


    /**
     * Return To XML
     */
    @Override
    public void convertReturnToXml(Date fromDate, Date toDate, String languageCode, String outFolderLocation) {
        FilterConfig filterConfig = new FilterConfig();

        if (fromDate != null) {
            filterConfig.setFilterParam(FilterConfigKey.PERIOD_FROM_DATE, fromDate);
        }

        if (toDate != null) {
            filterConfig.setFilterParam(FilterConfigKey.PERIOD_TO_DATE, toDate);
        }

        try {
            List<ReturnModel> packages = returnLocal.loadPackages(filterConfig, -1, -1, null);

            Language language = languageLocal.getLanguageByCode(languageCode);
            DateFormat df = new SimpleDateFormat("yyyyMMdd");

            for (ReturnModel rm : packages) {

                List<Map<String, Object>> returnsList = returnLocal.laodPackageReturns(rm.getFiId(), rm.getPeriodId(), rm.getReturnTypeId(), rm.getVersionId(), filterConfig);

                for (Map<String, Object> returns : returnsList) {

                    String fiCode = returns.get("fiCode") != null ? returns.get("fiCode").toString() : null;
                    String periodToDateString = returns.get("periodToDate") != null ? df.format((Date) returns.get("periodToDate")) : null;
                    String returnTypeCode = returns.get("returnTypeCode") != null ? returns.get("returnTypeCode").toString() : null;
                    String versionCode = returns.get("returnVersionCode") != null ? returns.get("returnVersionCode").toString() : null;
                    String returnDefinitionCode = returns.get("retrunDefinitionCode") != null ? returns.get("retrunDefinitionCode").toString() : null;

                    String name = fiCode + "_" + periodToDateString + "_" + returnTypeCode + "_" + versionCode + "_" + returnDefinitionCode;

                    long returnId = returns.get("id") != null ? (Long) returns.get("id") : 0;

                    returnLocal.saveReturnXml(returnId, rm.getVersionId(), language.getId(), name, outFolderLocation);

                }
            }
            log.info("Converting finished.");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);

        }
    }

    private String getMdtVersion() {
        String mdtVersion = "0";
        String oldMdtVersion = propertyLocal.getSystemProperty(PropertyKeys.MDT_RELEASE_VERSION);
        if (oldMdtVersion != null) {
            oldMdtVersion = oldMdtVersion.trim();
            if (!oldMdtVersion.isEmpty()) {
                try {
                    mdtVersion = Integer.toString(Integer.parseInt(oldMdtVersion) + 1);
                    propertyLocal.setSystemProperty(PropertyKeys.MDT_RELEASE_VERSION, mdtVersion);
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                }
            }
        }
        return mdtVersion;
    }

    private void archiveOldTemplates(File templates, File submissionTool) {

        DateFormat dateFormat = new SimpleDateFormat("_ddMMyyyy-hhmmss.SSS");

        String extension = ".bak";

        /**
         * Templates
         */
        if (templates.exists()) {
            File templatesOldFile = new File(templates.getParentFile(), templates.getName() + dateFormat.format(new Date()) + extension);
            templates.renameTo(templatesOldFile);
            log.info("Achived Templates folder");
        }

        /**
         * Submission Tool
         */
        if (submissionTool.exists()) {
            File submissiobToolOldFile = new File(submissionTool.getParentFile(), submissionTool.getName() + dateFormat.format(new Date()) + extension);
            submissionTool.renameTo(submissiobToolOldFile);
            log.info("Achived SubmissionTool file");
        }
    }

    /**
     * Release ST and MDT
     */
    @SuppressWarnings("resource")
    private void addFilesToZip(File source, File[] files, String path) {
        try {
            File tmpZip = File.createTempFile(source.getName(), null);
            tmpZip.delete();
            if (!source.renameTo(tmpZip)) {
                throw new Exception("Could not make temp file (" + source.getName() + ")");
            }
            byte[] buffer = new byte[4096];

            JarInputStream jin = new JarInputStream(new FileInputStream(tmpZip));

            JarOutputStream out = new JarOutputStream(new FileOutputStream(source), jin.getManifest());

            for (int i = 0; i < files.length; i++) {
                InputStream in = new FileInputStream(files[i]);
                out.putNextEntry(new ZipEntry(path + files[i].getName()));
                for (int read = in.read(buffer); read > -1; read = in.read(buffer)) {
                    out.write(buffer, 0, read);
                }
                out.closeEntry();
                in.close();
            }
            for (ZipEntry ze = jin.getNextEntry(); ze != null; ze = jin.getNextEntry()) {
                if (!zipEntryMatch(ze.getName(), files, path)) {
                    out.putNextEntry(ze);
                    for (int read = jin.read(buffer); read > -1; read = jin.read(buffer)) {
                        out.write(buffer, 0, read);
                    }
                    out.closeEntry();
                }
            }
            out.close();
            tmpZip.delete();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void onComplete() {
        PROGRESS = 0.0;
        maxProgress = 0;
        progressCount = 0;
    }

    @Override
    public void updateTemplates(String submissionToolFileName, String signBatFile, String submissionToolEmptyFileName, Map<String, Boolean> activate) {
        try {
            onComplete();
            JAXBContext context = JAXBContext.newInstance("net.fina.server.mdt.xml");

            Marshaller marshaller = context.createMarshaller();

            String submissionToolPath = ConfigurationUtil.get().get("SubmissionTool.RepositoryPath");
            submissionToolPath += submissionToolPath.endsWith(File.separator) ? "" : File.separator;

            File fileDirectory = new File(submissionToolPath);

            File templatesFile = new File(fileDirectory, "templates/");
            File target = new File(fileDirectory, submissionToolFileName);

            /**
             * Archive Old Files
             */
            archiveOldTemplates(templatesFile, target);

            if (!templatesFile.exists()) {
                templatesFile.mkdir();
            }

            Map<ReturnDefinitionFilter, Object> filterObjectMap = new HashMap<>();
            filterObjectMap.put(ReturnDefinitionFilter.LOAD_All, true);
            List<ReturnDefinition> definitions = returnDefinitionLocal.load(filterObjectMap);

            maxProgress = definitions.size();

            Properties properties = new Properties();

            Properties returnProperties = new Properties();

            log.info("Load Comparisons.");
            Map<Long, List<MDTComparison>> comparisons = new HashMap<>();
            for (MDTComparison comparison : mdtNodeLocal.loadComparisons(new HashMap<>(), null)) {
                long nid = comparison.getNode().getId();
                comparisons.putIfAbsent(nid, new ArrayList<>());
                comparisons.get(nid).add(comparison);
            }

            log.info("Load Comparisons.");
            Map<Long, String> languagesMap = new HashMap<>();
            for (Long id : languageLocal.getLanguageIds()) {
                languagesMap.put(id, languageLocal.getLanguageCodeById(id).trim());
            }

            log.info("Get all mdt dependent nodes");
            final List<MDTDependentNode> allMdtDependentNodes = processingStoreLocal.loadAllMdtDependentNodes();
            final Map<Long, Set<String>> dependencies = new HashMap<>();
            final Map<Long, Set<Long>> dependenciesLongMap = new HashMap<>();
            for (MDTDependentNode depNode : allMdtDependentNodes) {
                long dependentNodeId = depNode.getDepNode().getDependentNodeId();

                if (!dependencies.containsKey(dependentNodeId)) {
                    dependencies.put(dependentNodeId, new HashSet<>());
                }
                dependencies.get(dependentNodeId).add(mdtNodeLocal.getMdtNodeCodeById(depNode.getDepNode().getNodeId()));

                if (!dependenciesLongMap.containsKey(dependentNodeId)) {
                    dependenciesLongMap.put(dependentNodeId, new HashSet<>());
                }
                dependenciesLongMap.get(dependentNodeId).add(depNode.getDepNode().getNodeId());
            }

            log.info("Get mdt nodes by parent id");
            final Map<Long, List<MDTNode>> allMdtNodesByParentId = processingStoreLocal.loadMdtNodesByParentId();

            for (ReturnDefinition rd : definitions) {
                Mdt mdt = returnDefinitionLocal.loadReturnDefinitionMdtV1(rd, activate, comparisons, languagesMap, dependenciesLongMap, allMdtNodesByParentId);

                progressCount++;

                double p = (1.0 / (double) maxProgress) * (double) progressCount;
                PROGRESS = p;

                String code = rd.getCode().trim();
                String name = code + ".mdt.xml";

                marshaller.marshal(mdt, new File(templatesFile, name));

                properties.put(code, name);
                returnProperties.put(code, rd.getReturnType().getCode());
            }

            try (FileOutputStream out = new FileOutputStream(new File(templatesFile, "mdt.properties"))) {
                properties.store(out, "Generated Templates.");
            }

            try (FileOutputStream out = new FileOutputStream(new File(templatesFile, "return.properties"))) {
                returnProperties.store(out, "Generated Templates.");
            }

            try (FileOutputStream out = new FileOutputStream(new File(templatesFile, "version.properties"))) {
                Properties versionProperties = new Properties();
                versionProperties.put("mdtVersion", getMdtVersion());
                versionProperties.store(out, "Generated MDT Version.");
            }

            File source = new File(fileDirectory, submissionToolEmptyFileName);

            try (FileOutputStream out = new FileOutputStream(target)) {
                Files.copy(source.toPath(), out);
            }

            addFilesToZip(target, templatesFile.listFiles(), "MDT/");

            signSubmissionToolFile(submissionToolPath, signBatFile);

            log.info("Progress Completed");

            submissionToolAndMdtReleaseResult = "Completed";

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            submissionToolAndMdtReleaseResult = "Error";
        }
    }

    @Override
    public String getReleaseResult() {
        return submissionToolAndMdtReleaseResult;
    }

    private boolean zipEntryMatch(String zeName, File[] files, String path) {
        for (int i = 0; i < files.length; i++) {
            if ((path + files[i].getName()).equals(zeName)) {
                return true;
            }
        }
        return false;
    }

    private void signSubmissionToolFile(String repositoryPath, String signBatFileName) throws IOException {
        String signBatFile = repositoryPath + signBatFileName;

        String[] cmd = new String[5];
        cmd[0] = "cmd.exe";
        cmd[1] = "/C";
        cmd[2] = "start";
        cmd[3] = "/min";
        cmd[4] = signBatFile;

        Runtime run = Runtime.getRuntime();
        run.exec(cmd);
    }

    public StreamedContent getDownloadFile() {
        return downloadFile;
    }

    public void setDownloadFile(StreamedContent downloadFile) {
        this.downloadFile = downloadFile;
    }

    public StreamedContent convert(byte[] content, String optionalSheet, String fileName, boolean enableDownload) throws FinATypeException {

        String result;
        log.info("started");

        if (content != null) {
            try (InputStream in = new ByteArrayInputStream(content)) {

                List<String> languageCodes = new ArrayList<>(languageLocal.getLanguagesCodeNameMap().keySet());

                Converter converter = ConverterFactory.createConverter(in, optionalSheet, languageCodes);

                Map<String, Node> files = converter.convert();

                if (files.isEmpty()) {
                    throw new FinATypeException(FinATypeException.Type.MDT_MODEL_CONVERT_ERROR);
                }

                JAXBContext context = JAXBContext.newInstance(Node.class);

                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                try (ZipOutputStream zipOut = new ZipOutputStream(out)) {

                    for (Iterator<Map.Entry<String, Node>> it = files.entrySet().iterator(); it.hasNext(); ) {

                        Map.Entry<String, Node> e = it.next();

                        try (ByteArrayOutputStream mdtOut = new ByteArrayOutputStream()) {
                            ZipEntry zipEntry = new ZipEntry(e.getKey() + ".mdt.xml");
                            zipOut.putNextEntry(zipEntry);

                            marshaller.marshal(e.getValue(), mdtOut);

                            zipOut.write(mdtOut.toByteArray());
                        }
                    }

                }

                try (ByteArrayInputStream stream = new ByteArrayInputStream(out.toByteArray())) {
                    downloadFile = new DefaultStreamedContent(stream, "application/zip", fileName + "_result.zip");
                    setDownloadFile(downloadFile);
                }
                result = "File " + fileName + " Converted, you Can download.";

            } catch (FinATypeException ex) {
                log.error(ex.getType().getCode(), ex);
                throw ex;
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                result = "Error:\n" + ExceptionUtils.getStackTrace(ex);
                throw new RuntimeException(ex);
            }
            log.info(result);
        }
        return getDownloadFile();
    }

    public List<String> getSheetNames(byte[] content) {
        List<String> sheetNames = new ArrayList<String>();
        Converter converter;
        try {
            converter = ConverterFactory.createSimpleConverter(new ByteArrayInputStream(content));
            sheetNames = converter.getSheetNames();
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return sheetNames;
    }

    public Map<String, byte[]> extractZip(byte[] zipFileContent) throws Exception {
        Map<String, byte[]> files = new HashMap<>();

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipFileContent))) {

            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {

                if (!ze.isDirectory()) {

                    int n;
                    byte[] buf = new byte[1024];
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    while ((n = zis.read(buf, 0, 1024)) > -1) {
                        out.write(buf, 0, n);
                    }

                    files.put(ze.getName(), out.toByteArray());

                }

                zis.closeEntry();
            }
        }
        return files;
    }

    //FinA File Decryption
    @Override
    public byte[] decryptFinaFile(byte[] certificate, char[] password, byte[] finaFile) throws Exception {
        String alias = ConfigurationUtil.get().get("KeyStoreAlias");
        Map<String, byte[]> files = extractZip(finaFile);
        String user = null;

        if (files.containsKey("author")) {
            user = new String(files.get("author")).toLowerCase();
        }

        SecurityManagerBase securityManager = SecurityManagerFactory.get(certificate, alias, password, files.get("stamp"), files.get("clientCertificate"), user);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (@SuppressWarnings("resource")
             ZipOutputStream zipOut = new ZipOutputStream(out)) {
            for (Map.Entry<String, byte[]> entry : files.entrySet()) {
                String fileName = entry.getKey();
                byte[] content = entry.getValue();
                if (ConverterUtil.isExtension(fileName, "xml")) {
                    content = securityManager.decrypt(content);

                    byte[] sign = files.get(fileName + ".sign");
                    if (!securityManager.verifySign(content, sign)) {
                        zipOut.close();
                        throw new DcsTypeException(DcsTypeException.Type.SECURITY_INVALID_SIGN);
                    }

                    ZipEntry zipEntry = new ZipEntry(fileName);
                    zipOut.putNextEntry(zipEntry);
                    zipOut.write(content);
                }
            }
        }
        return out.toByteArray();
    }

    @Override
    public byte[] decryptFinaFile(byte[] finaFile) throws Exception {
        Map<String, byte[]> files = extractZip(finaFile);
        String user = null;

        if (files.containsKey("author")) {
            user = new String(files.get("author")).toLowerCase();
        }

        SecurityManagerBase securityManager = SecurityManagerFactory.get(null, null, null, files.get("stamp"), files.get("clientCertificate"), user);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (@SuppressWarnings("resource")
             ZipOutputStream zipOut = new ZipOutputStream(out)) {
            for (Map.Entry<String, byte[]> entry : files.entrySet()) {
                String fileName = entry.getKey();
                byte[] content = entry.getValue();
                if (ConverterUtil.isExtension(fileName, "xml")) {
                    content = securityManager.decrypt(content);

                    byte[] sign = files.get(fileName + ".sign");
                    if (!securityManager.verifySign(content, sign)) {
                        zipOut.close();
                        throw new DcsTypeException(DcsTypeException.Type.SECURITY_INVALID_SIGN);
                    }

                    ZipEntry zipEntry = new ZipEntry(fileName);
                    zipOut.putNextEntry(zipEntry);
                    zipOut.write(content);
                }
            }
        }
        return out.toByteArray();
    }

    @Override
    public Double getReleaseProgress() {
        return PROGRESS;
    }

    public void setReleaseProgress(Double progress) {
        this.PROGRESS = progress;
    }

    @Override
    public List<MdtTesterResultMetaModel> runMdtTester(long parentNodeId) {
        return new MdtTesterImpl().validate(mdtNodeLocal.loadDescendantNodes(parentNodeId), mdtCacheManager.getMdtNodesById(), mdtNodeLocal.loadAllMdtDependentNodes());
    }

    @Override
    public List<String> getExcelFileDiff(byte[] excelFileContent1, byte[] excelFileContent2) throws FinATypeException {
        try (InputStream is1 = new ByteArrayInputStream(excelFileContent1);
             InputStream is2 = new ByteArrayInputStream(excelFileContent2);
             Workbook wb1 = WorkbookFactory.create(is1);
             Workbook wb2 = WorkbookFactory.create(is2)) {

            return ExcelComparator.compare(wb1, wb2);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            throw new FinATypeException(FinATypeException.Type.GENERAL_ERROR);
        }
    }

    @Override
    public void releaseMDT(List<String> fiTypeCodes) {
        OSTLicense ostLicense = ProductHelper.getInstance().getOstLicense();
        switch (ostLicense.getLicenseKind()) {
            case PAID -> {
                fiTypeCodes = fiTypeCodes.stream().filter(code -> ostLicense.getFiTypes().contains(code.trim().toUpperCase())).toList();
            }
            case PROHIBITED -> {
                fiTypeCodes = new ArrayList<>();
            }
        }

        if (fiTypeCodes == null) {
            return;
        }
        List<FiType> fiTypes = new ArrayList<>();
        for (String code : fiTypeCodes) {
            FiType fiType = new FiType(code);
            fiTypes.add(fiType);
        }

        releaseSession.updateTemplates(MDTXmlVersion.VERSION2, getMDTxmlActiveProperties(), fiTypes);
    }

    @Override
    public List<FiTypeModel> loadReleaseFiTypes() {
        List<FiTypeModel> fiTypes = fiProxySession.loadFiTypes(false);

        OSTLicense ostLicense = ProductHelper.getInstance().getOstLicense();
        switch (ostLicense.getLicenseKind()) {
            case PAID -> {
                return fiTypes.stream().filter(t -> ostLicense.getFiTypes().contains(t.getCode().trim().toUpperCase())).toList();
            }
            case PROHIBITED -> {
                return new ArrayList<>();
            }
        }


        return fiTypes;
    }

    private Map<String, Boolean> getMDTxmlActiveProperties() {
        Map<String, Boolean> properties = new HashMap<>();

        properties.put("id", true);
        properties.put("code", true);
        properties.put("parentId", true);
        properties.put("type", true);
        properties.put("dataType", true);
        properties.put("equation", true);
        properties.put("sequence", true);
        properties.put("evalMethod", true);
        properties.put("disabled", true);
        properties.put("required", true);
        properties.put("dependents", true);
        properties.put("descriptions", true);
        properties.put("comparisons", true);
        properties.put("optional", true);

        return properties;
    }
}


