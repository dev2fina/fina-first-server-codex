package net.fina.server.returns.impl;

import jakarta.ejb.*;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.odstoolkit.AbstractFactory;
import net.fina.odstoolkit.FactoryProducer;
import net.fina.odstoolkit.writer.AooWriterBase;
import net.fina.server.returns.api.ReturnDefinitionLocal;
import net.fina.server.returns.api.ReturnDefinitionTemplateRepositoryLocal;
import net.fina.server.returns.entity.ReturnDefinition;
import net.fina.server.returns.entity.ReturnDefinitionFormat;
import net.fina.server.returns.event.ReturnFormatChangeEvent;
import net.fina.server.store.api.RepositoryLocal;
import net.fina.server.store.model.RepositoryFile;
import net.fina.server.store.qualifier.Template;
import net.fina.common.server.StatisticsLogger;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;

import javax.jcr.Session;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Stateless
@Local(ReturnDefinitionTemplateRepositoryLocal.class)
public class ReturnDefinitionTemplateRepositorySession implements ReturnDefinitionTemplateRepositoryLocal {

    private final static String LOCK = ".lock";

    @Inject
    private Logger log;

    @Template
    @Inject
    private Session repositorySession;

    @EJB
    private ReturnDefinitionLocal returnDefinitionLocal;
    @EJB
    private RepositoryLocal repositoryLocal;

    @Override
    public byte[] loadPackageTemplate(String returnTypeCode) throws IOException {
        String dir = ConfigurationUtil.get().get("RETURN_PACKAGE_TEMPLATE_REPO_DIR");

        //Check Lock
        if (new File(dir, LOCK).exists()) {
            return null;
        }

        File file = new File(dir, returnTypeCode + ".ods");

        if (!file.exists()) {
            return null;
        }

        return Files.readAllBytes(file.toPath());
    }

    @Override
    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void storePackageTemplatesByReturnDefinitionId(@Observes(during = TransactionPhase.AFTER_SUCCESS) ReturnFormatChangeEvent event) {
        try {
            ReturnDefinition rd = returnDefinitionLocal.getReturnDefinitionById(event.getReturnDefinitionId());
            storePackageTemplates(rd.getReturnType().getCode().trim());
        } catch (FinATypeException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void storePackageTemplates() {
        storePackageTemplates(null);
    }

    private void storePackageTemplates(String returnTypeCode) {
        File lock = null;
        try (StatisticsLogger statLog = new StatisticsLogger("Store Package templates")) {

            statLog.logMessage("Start");

            String dir = ConfigurationUtil.get().get("RETURN_PACKAGE_TEMPLATE_REPO_DIR");
            statLog.logStage("Detect and clear base dir '" + dir + "'");
            File baseDir = checkBaseDir(dir);

            lock = lock(baseDir);

            statLog.logStage("Load return Definitions");
            HashSet<ReturnDefinition> returnDefinitions = returnDefinitionLocal.load(null, false);

            Map<String, List<ReturnDefinition>> returnDefinitionsByReturnTypeCode = new HashMap<>();
            for (ReturnDefinition rd : returnDefinitions) {
                if (returnTypeCode == null || rd.getReturnType().getCode().trim().equals(returnTypeCode)) {
                    List<ReturnDefinition> tmp = returnDefinitionsByReturnTypeCode.computeIfAbsent(rd.getReturnType().getCode().trim(), k -> new ArrayList<>());
                    tmp.add(rd);
                }
            }

            //Load properties
            File propertiesFile = new File(baseDir, "meta.properties");
            Properties properties = loadProperties(propertiesFile);

            statLog.logStage("Compile templates");
            for (Map.Entry<String, List<ReturnDefinition>> e : returnDefinitionsByReturnTypeCode.entrySet()) {
                List<ReturnDefinition> definitions = e.getValue();

                String stamp = createIdsSum(definitions) + "@" + createVersionsSum(definitions);

                String oldStamp = properties.getProperty(e.getKey());

                String returnFormatOdsRepositoryPath = String.format("/%s/%s%s", e.getKey(), e.getKey(), ".ods");

                if (returnTypeCode != null || oldStamp == null || (!oldStamp.equals(stamp)) || repositoryLocal.getLatestVersionId(repositorySession, returnFormatOdsRepositoryPath) == null) {
                    try {
                        statLog.logStage("Compile:" + e.getKey());
                        byte[] template = createWorkBook(definitions);

                        RepositoryFile rf = new RepositoryFile();
                        rf.setContent(template);
                        rf.setFileName(e.getKey() + ".ods");
                        rf.setPath("/" + e.getKey());

                        repositoryLocal.saveFile(repositorySession, rf);

                        writeFile(baseDir, template, e.getKey() + ".ods");
                    } catch (Throwable t) {
                        log.error(t.getMessage(), t);
                    }
                }
                properties.put(e.getKey(), stamp);
            }

            saveProperties(propertiesFile, properties);

        } finally {
            if (lock != null) {
                lock.delete();
            }
        }
    }

    private int createIdsSum(List<ReturnDefinition> returnDefinitions) {
        int sum = 0;
        for (ReturnDefinition rd : returnDefinitions) {
            sum += (int) rd.getId();
        }
        return sum;
    }

    private int createVersionsSum(List<ReturnDefinition> returnDefinitions) {
        int sum = 0;
        for (ReturnDefinition rd : returnDefinitions) {
            sum += rd.getVersion();
        }
        return sum;
    }

    private byte[] createWorkBook(List<ReturnDefinition> returnDefinitions) throws Exception {
        final AbstractFactory factory = FactoryProducer.getFactory("odftoolkit");

        AooWriterBase aooWriter = factory.getAooWriter();
        aooWriter.init(aooWriter.createEmptySpreadsheetDocument());
        aooWriter.removeSheetByIndex(0);

        for (ReturnDefinition rd : returnDefinitions) {
            ReturnDefinitionFormat format = returnDefinitionLocal.getReturnDefinitionFormat(rd.getId());
            if (format == null) {
                log.warn("Return Definition format is null : " + rd.getCode());
                continue;
            }
            AooWriterBase tmpAooWriter;
            if (format.getFormat() == null) {
                tmpAooWriter = factory.getAooWriter();
                tmpAooWriter.init(null);
            } else {
                tmpAooWriter = factory.getAooWriter(format.getFormat());
            }

            //Clean [0,0] cell
            tmpAooWriter.setCellValue(0, 0, " ");

            aooWriter.appendSheet(tmpAooWriter.getCurrentSheet(), rd.getCode().trim());
        }

        return aooWriter.getSpreadsheetDocument();
    }

    private Properties loadProperties(File propertiesFile) {
        Properties properties = new Properties();
        if (propertiesFile.exists()) {
            try (InputStream in = new FileInputStream(propertiesFile)) {
                properties.load(in);
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        }
        return properties;
    }

    private void saveProperties(File propertiesFile, Properties properties) {
        try (OutputStream out = new FileOutputStream(propertiesFile)) {
            properties.store(out, "Don't change");
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

    private void writeFile(File baseDir, byte[] content, String name) throws IOException {
        Files.write(new File(baseDir, name).toPath(), content);
    }

    private File checkBaseDir(String dir) {
        return new File(dir);
    }


    private File lock(File baseDir) {
        File lock = new File(baseDir, LOCK);
        try {
            lock.createNewFile();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return lock;
    }
}
