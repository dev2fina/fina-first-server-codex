package net.fina.server.st.impl;

import com.google.gson.Gson;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.interceptor.Interceptors;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.entity.FiType;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTComparison;
import net.fina.server.mdt.entity.MDTDependentNode;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.mdt.impl.MDTXmlVersion;
import net.fina.server.mdt.xml.v1.Mdt;
import net.fina.server.mdt.xml.v2.Node;
import net.fina.server.processing.api.ProcessingStoreLocal;
import net.fina.server.returns.api.PackageLocal;
import net.fina.server.returns.api.ReturnDefinitionLocal;
import net.fina.server.returns.entity.ReturnDefinition;
import net.fina.server.returns.entity.ReturnPackage;
import net.fina.server.security.api.PropertyLocal;
import net.fina.server.st.api.TemplateLocal;
import net.fina.server.st.xml.*;
import net.fina.common.server.StatisticsLogger;
import org.apache.commons.io.FileUtils;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Singleton
@Interceptors(RecordingAuditor.class)
public class MDTReleaseSession {
    private static final String PACKAGE_CONFIGURATION_VERSION = "1.1";
    private Logger log = Logger.getLogger(getClass());
    @EJB
    private PropertyLocal propertyLocal;
    @EJB
    private ReturnDefinitionLocal definitionLocal;
    @EJB
    private FiLocal fiLocal;
    @EJB
    private PackageLocal packageLocal;
    @EJB
    private TemplateLocal templateLocal;
    @EJB
    private MDTNodeLocal mdtNodeLocal;
    @EJB
    private LanguageLocal languageLocal;
    @EJB
    private ProcessingStoreLocal processingStoreLocal;

    private boolean lock = false;


    @TransactionTimeout(unit = TimeUnit.MINUTES, value = 180)
    public void updateTemplates(MDTXmlVersion mdtXmlVersion, Map<String, Boolean> activate, List<FiType> fiTypes) {
        if (lock) {
            return;
        }
        lock = true;

        try (StatisticsLogger statLog = new StatisticsLogger("MDT Release: " + fiTypes + ", MDT XML Version:" + mdtXmlVersion.name())) {

            statLog.logMessage("Start mdt release");

            JAXBContext context = null;
            switch (mdtXmlVersion) {
                case VERSION1: {
                    context = JAXBContext.newInstance("net.fina.server.mdt.xml.v1");
                    break;
                }
                case VERSION2: {
                    context = JAXBContext.newInstance(Node.class);
                    break;
                }
            }

            Marshaller marshaller = context.createMarshaller();

            String submissionToolTemplatePath = ConfigurationUtil.get().get("SubmissionTool.TemplatesPath");
            submissionToolTemplatePath += submissionToolTemplatePath.endsWith(File.separator) ? "" : File.separator;

            File fileDirectory = new File(submissionToolTemplatePath);
            File templatesFile = new File(fileDirectory, "templates/");

            /**
             * Archive Old Files
             */
            statLog.logStage("Archive Old templates.");
            archiveOldTemplates(templatesFile, fiTypes);

            statLog.logStage("Load Package.");
            List<ReturnPackage> returnPackages = packageLocal.load();


            statLog.logStage("Load Comparisons.");
            Map<Long, List<MDTComparison>> comparisons = new HashMap<>();
            for (MDTComparison comparison : mdtNodeLocal.loadComparisons(new HashMap<>(), null)) {
                long nid = comparison.getNode().getId();
                comparisons.putIfAbsent(nid, new ArrayList<>());
                comparisons.get(nid).add(comparison);
            }

            statLog.logStage("Load Comparisons.");
            Map<Long, String> languagesMap = new HashMap<>();
            for (Long id : languageLocal.getLanguageIds()) {
                languagesMap.put(id, languageLocal.getLanguageCodeById(id).trim());
            }

            statLog.logStage("Get all mdt dependent nodes");
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

            statLog.logStage("Get mdt nodes by parent id");
            final Map<Long, List<MDTNode>> allMdtNodesByParentId = processingStoreLocal.loadMdtNodesByParentId();

            for (FiType selectedFiType : fiTypes) {

                String selectedFiTypeCode = selectedFiType.getCode().trim();

                PackagesType packagesType = new PackagesType();

                for (ReturnPackage returnPackage : returnPackages) {

                    for (FiType fiType : returnPackage.getFiTypes()) {
                        if (selectedFiTypeCode.equalsIgnoreCase(fiType.getCode().trim())) {

                            PackageType packageType = new PackageType();
                            packageType.setCode(returnPackage.getCode());
                            packageType.setName(returnPackage.getCode());
                            packageType.setNote(returnPackage.getNote());

                            ReturnsType returnsType = new ReturnsType();
                            packageType.setReturns(returnsType);

                            for (ReturnDefinition returnDefinition : returnPackage.getReturnDefinitions()) {
                                String code = returnDefinition.getCode().trim();
                                String name = code + ".mdt.xml";

                                ReturnType returnType = new ReturnType();
                                returnType.setCode(code);
                                returnType.setName(name);
                                returnsType.getReturn().add(returnType);

                                Mdt mdt = null;
                                Node node = null;

                                File fiTypeFile = checkAndCreateDirectory(templatesFile, fiType.getCode());
                                File xmlFile = new File(fiTypeFile, name);

                                if (!xmlFile.exists()) {

                                    statLog.logStage("Export: " + name);

                                    switch (mdtXmlVersion) {
                                        case VERSION1:
                                            mdt = definitionLocal.loadReturnDefinitionMdtV1(returnDefinition, activate, comparisons, languagesMap, dependenciesLongMap, allMdtNodesByParentId);
                                            initListNodesV1(allMdtNodesByParentId, mdt);
                                            break;
                                        case VERSION2:
                                            node = definitionLocal.loadReturnDefinitionMdtV2(returnDefinition, activate, comparisons, languagesMap, dependencies, allMdtNodesByParentId);
                                            initListNodesV2(allMdtNodesByParentId, node);
                                            break;
                                    }

                                    switch (mdtXmlVersion) {
                                        case VERSION1:
                                            marshaller.marshal(mdt, xmlFile);
                                            break;
                                        case VERSION2:
                                            marshaller.marshal(node, xmlFile);
                                            break;
                                    }
                                }
                            }
                            packagesType.getPackage().add(packageType);
                        }
                    }
                }

                VersionsType versionType = new VersionsType();
                versionType.setMdt(getMdtReleaseVersion(selectedFiTypeCode));
                versionType.setOst(templateLocal.getOstVersion());
                versionType.setConfiguration(PACKAGE_CONFIGURATION_VERSION);

                packagesType.setVersions(versionType);

                File fiTypeFile = checkAndCreateDirectory(templatesFile, selectedFiTypeCode);

                JAXBContext packageContext = JAXBContext.newInstance("net.fina.server.st.xml");
                Marshaller packageMarshaller = packageContext.createMarshaller();
                packageMarshaller.marshal(packagesType, new File(fiTypeFile, "package.xml"));
            }


            //serialize all return mdt
            File mdtFolder = checkAndCreateDirectory(templatesFile, "mdt");
            List<ReturnDefinition> returnDefinitions = definitionLocal.loadActiveReturnDefinitions();

            statLog.logStage("Serialize Return MDT Templates");
            for (ReturnDefinition rd : returnDefinitions) {
                String name = String.format("%s.mdt.xml", rd.getCode().trim());
                File mdtXmlFile = new File(mdtFolder, name);

                Mdt mdt = null;
                Node node = null;

                switch (mdtXmlVersion) {
                    case VERSION1:
                        mdt = definitionLocal.loadReturnDefinitionMdtV1(rd, activate, comparisons, languagesMap, dependenciesLongMap, allMdtNodesByParentId);
                        initListNodesV1(allMdtNodesByParentId, mdt);
                        break;
                    case VERSION2:
                        node = definitionLocal.loadReturnDefinitionMdtV2(rd, activate, comparisons, languagesMap, dependencies, allMdtNodesByParentId);
                        initListNodesV2(allMdtNodesByParentId, node);
                        break;
                }

                switch (mdtXmlVersion) {
                    case VERSION1:
                        marshaller.marshal(mdt, mdtXmlFile);
                        break;
                    case VERSION2:
                        marshaller.marshal(node, mdtXmlFile);
                        break;
                }
            }

            log.info("Progress Completed");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            lock = false;
        }
    }

    private void initListNodesV1(Map<Long, List<MDTNode>> allMdtNodesByParentId, Mdt mdt) {
        List<net.fina.server.mdt.xml.v1.Node> nodes = mdt.getNodes();
        for (net.fina.server.mdt.xml.v1.Node n : nodes) {
            if (n.getType() == MDTNodeTypes.LIST.ordinal()) {
                int hash = String.valueOf(n.getId()).hashCode();
                List<MDTNode> dataItemNodes = allMdtNodesByParentId.get(Long.valueOf(n.getEquation().trim()));
                StringBuilder dataItems = new StringBuilder();
                for (MDTNode dataItemNode : dataItemNodes) {
                    dataItems.append(dataItemNode.getEquation().trim()).append(hash);
                }
                n.setEquation(dataItems.toString());
            }
        }
    }

    private void initListNodesV2(Map<Long, List<MDTNode>> allMdtNodesByParentId, Node nodeV2) {
        initListNodeV2(allMdtNodesByParentId, nodeV2);
        List<net.fina.server.mdt.xml.v2.Node> nodes = nodeV2.getChildren();
        if (nodes != null && !nodes.isEmpty()) {
            for (net.fina.server.mdt.xml.v2.Node node : nodes) {
                if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                    initListNodesV2(allMdtNodesByParentId, node);
                } else if (node.getType() == MDTNodeTypes.LIST) {
                    initListNodeV2(allMdtNodesByParentId, node);
                }
            }
        }
    }


    /**
     * Checks if node is List Element and add Data Items equations as node equations, separated by node code value hash
     *
     * @param allMdtNodesByParentId All mdt nodes map, keys are parent node ids, needed to get data elements
     * @param node                  Mdt xml node v2
     */
    private void initListNodeV2(Map<Long, List<MDTNode>> allMdtNodesByParentId, Node node) {
        if (node.getType() == MDTNodeTypes.LIST) {
            int hash = String.valueOf(node.getCode()).hashCode();
            if (node.getEquation() != null && !node.getEquation().isBlank()) {
                List<MDTNode> dataItemNodes = allMdtNodesByParentId.getOrDefault(Long.valueOf(node.getEquation().trim()), new ArrayList<>());
                StringBuilder dataItems = new StringBuilder();
                for (MDTNode dataItemNode : dataItemNodes) {
                    dataItems.append(dataItemNode.getEquation().trim()).append(hash);
                }
                node.setEquation(dataItems.toString());
            } else {
                node.setEquation("");
            }

        }
    }

    private File checkAndCreateDirectory(File file, @NotNull String fileName) {
        File fiTypeFile = new File(file, fileName.trim());
        if (!fiTypeFile.exists()) {
            fiTypeFile.mkdir();
        }
        return fiTypeFile;
    }

    private boolean checkFiTypeReturn(String returnTypeCode, String fiTypeCode) {
        return returnTypeCode.startsWith(fiTypeCode.trim());
    }

    private void archiveOldTemplates(File templates, List<FiType> fiTypes) throws IOException {

        DateFormat dateFormat = new SimpleDateFormat("_ddMMyyyy-hhmmss.SSS");

        String extension = ".bak";

        /**
         * Templates
         */
        if (templates.exists()) {
            File templatesOldFile = new File(templates.getParentFile(), templates.getName() + dateFormat.format(new Date()) + extension);
            templates.renameTo(templatesOldFile);

            if (!templates.exists()) {
                templates.mkdir();
            }

            FileUtils.copyDirectory(templatesOldFile, templates);

            for (FiType fiType : fiTypes) {
                String fiTypeCode = fiType.getCode().trim();
                for (File fiTypeFile : templates.listFiles()) {
                    if (fiTypeCode.equals(fiTypeFile.getName())) {
                        FileUtils.deleteDirectory(fiTypeFile);
                    }
                }
            }

            log.info("Achived Templates folder");
        } else {
            templates.mkdir();
        }
    }

    private String getMdtReleaseVersion(String fiTypeCode) {
        Gson gson = new Gson();
        int mdtVersion = 1;
        String oldReleaseVersion = propertyLocal.getSystemProperty(PropertyKeys.MDT_RELEASE_VERSION);
        if (oldReleaseVersion == null || oldReleaseVersion.isEmpty()) {
            saveMdtReleaseVersion(gson, fiTypeCode, mdtVersion);
        } else {
            try {
                MdtReleaseVersion releaseVersion = gson.fromJson(oldReleaseVersion, MdtReleaseVersion.class);
                boolean exist = false;
                for (MdtReleaseVersion.Version version : releaseVersion.getVersions()) {
                    if (version.getT().trim().equalsIgnoreCase(fiTypeCode)) {
                        version.setV(version.getV() + 1);
                        mdtVersion = version.getV();
                        exist = true;
                    }
                }
                if (!exist) {
                    releaseVersion.getVersions().add(new MdtReleaseVersion.Version(fiTypeCode, mdtVersion));
                }
                propertyLocal.setSystemProperty(PropertyKeys.MDT_RELEASE_VERSION, gson.toJson(releaseVersion));
            } catch (Throwable t) {
                saveMdtReleaseVersion(gson, fiTypeCode, mdtVersion);
                log.error(t.getMessage(), t);
            }
        }
        return Integer.toString(mdtVersion);
    }

    private void saveMdtReleaseVersion(Gson gson, String fiTypeCode, int mdtVersion) {
        MdtReleaseVersion releaseVersion = new MdtReleaseVersion();
        releaseVersion.getVersions().add(new MdtReleaseVersion.Version(fiTypeCode, mdtVersion));
        propertyLocal.setSystemProperty(PropertyKeys.MDT_RELEASE_VERSION, gson.toJson(releaseVersion));
    }

    public void saveMdtXml(Marshaller marshaller, Mdt mdt, String fileName, String outFolderLocation) {
        try {
            marshaller.marshal(mdt, new File(outFolderLocation + "" + File.separator + fileName));
            log.info("Save MDT xml (Folder " + outFolderLocation + ")");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
