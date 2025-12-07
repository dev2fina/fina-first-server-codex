package net.fina.server.classifier.proxy;

import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.LanguageSampleModel;
import net.fina.messages.MessagesUtil;
import net.fina.server.classifier.entity.MDTCatalog;
import net.fina.server.classifier.model.*;
import net.fina.server.classifier.util.MDTCatalogExportUtil;
import net.fina.server.classifier.util.MDTCatalogImportUtil;
import net.fina.server.i18n.proxy.LanguageProxySession;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.User;
import net.fina.common.server.StatisticsLogger;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class MDTCatalogImportExportProxySession {
    private final Logger log = Logger.getLogger(getClass().getName());

    @Inject
    private MDTCatalogProxySession catalogProxySession;
    @Inject
    private MdtCatalogItemProxySession catalogItemProxySession;
    @Inject
    private LanguageProxySession languageProxySession;
    @Inject
    private UserLocal userLocal;

    @Resource
    private SessionContext sessionContext;

    @RolesAllowed(PermissionIdNames.CATALOG_IMPORT)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public MDTCatalogImportStatusMetaModel importCatalogs(InputStream inputStream, String langCode) throws FinATypeException {
        MDTCatalogImportStatusMetaModel statusResult = new MDTCatalogImportStatusMetaModel();
        User user = userLocal.getCurrentUser();
        long langId = languageProxySession.getLanguageIdByCode(langCode);
        try(final StatisticsLogger statLog = new StatisticsLogger("MDT Catalog Import");) {
            statLog.logMessage("Start MDT Catalog Import ");
            Map<MDTCatalogMetaModel, List<MDTCatalogRowItemMetaModel>> data = new HashMap<>();

            Map<String, Long> langCodeIdMap = new HashMap<>();
            Map<String, Long> languageCodeIdMap = languageProxySession.loadLanguages()
                    .stream()
                    .collect(Collectors.toMap(
                            LanguageSampleModel::getCode,
                            LanguageSampleModel::getId
                    ));

            for (Map.Entry<String, String> entry : MDTCatalogImportUtil.languageCodeMap.entrySet()) {
                if (languageCodeIdMap.get(entry.getValue()) != null) {
                    langCodeIdMap.put(entry.getValue(), languageCodeIdMap.get(entry.getValue()));
                }
            }


            statusResult = new MDTCatalogImportUtil().readExcelFile(inputStream, langId, langCode, statLog, data, langCodeIdMap);
            if (statusResult.getErrors().isEmpty()) {
                statLog.logStage("Import Catalogs & Data");
                Map<Long, Long> excelRowIdMap = new HashMap<>();

                for (Map.Entry<MDTCatalogMetaModel, List<MDTCatalogRowItemMetaModel>> entry : data.entrySet()) {
                    MDTCatalogMetaModel catalog = catalogProxySession.getOrSave(entry.getKey(), langCode);
                    MDTCatalog catalogNode = catalogProxySession.getCatalog(catalog.getCode());
                    int rowCounter = catalogItemProxySession.count(catalog.getId());
                    int leafCounter = rowCounter;
                    int deletedLeafCount = 0;
                    Map<String, Long> existingRows = catalogItemProxySession.loadRowItems(catalog.getId(), langCode).stream().collect(Collectors.toMap((e) -> {
                        for (MDTCatalogItemMetaModel rowItem : e.getRowItems()) {
                            if (rowItem.getColumn().isKey()) {
                                return (String) rowItem.getValue();
                            }
                        }
                        return null;
                    }, MDTCatalogRowItemMetaModel::getRowId));

                    outerLoop:
                    for (MDTCatalogRowItemMetaModel row : entry.getValue()) {
                        rowCounter++;
                        long excelRowId = row.getRowId();
                        if (row.isLeaf()) {
                            leafCounter++;
                            if (row.isDeleted()) {
                                deletedLeafCount++;
                            }
                        }

                        if (!existingRows.isEmpty()) {
                            for (MDTCatalogItemMetaModel rowItem : row.getRowItems()) {
                                if (rowItem.getColumn().isKey() && existingRows.containsKey((String) rowItem.getValue())) {
                                    rowCounter++;
                                    excelRowIdMap.put(excelRowId, existingRows.get((String) rowItem.getValue()));
                                    statusResult.getWarnings().add("Item [" + rowItem.getValue() + "] was skipped");
                                    continue outerLoop;
                                }
                            }
                        }

                        row.setRowId(0);
                        if (row.getParentRowId() > 0) {
                            if (excelRowIdMap.containsKey(row.getParentRowId())) {
                                row.setParentRowId(excelRowIdMap.get(row.getParentRowId()));
                            } else {
                                statusResult.getWarnings().add("Could Not Find Parent Id [" + row.getParentRowId() + "] is missing or is not ordered!");
                            }
                        }
                        // set catalog columns
                        for (int k = 0; k < row.getRowItems().size(); k++) {
                            row.getRowItems().get(k).setColumn(catalog.getCatalogColumns().get(k));
                        }
                        MDTCatalogRowItemMetaModel saved = catalogItemProxySession.create(catalogNode, row, rowCounter, leafCounter - deletedLeafCount, user, true);
                        log.info("Imported : [" + catalog.getCode() + "] - Row : " + excelRowId);
                        excelRowIdMap.put(excelRowId, saved.getRowId());
                        row.setRowId(saved.getRowId());
                    }
                }
            } else {
                log.error(statusResult.getErrors());
            }
        } catch (FinATypeException ex) {
            if (ex.getType() != null) {
                ResourceBundle resourceBundle = MessagesUtil.loadMessageBundle(langCode);
                statusResult.getErrors().add(resourceBundle.getString(ex.getType().getCode()));
            } else {
                statusResult.getErrors().add(ex.getMessage() == null || ex.getMessage().isEmpty() ? "General Error Contact Administrator!" : ex.getMessage());
            }
            log.error(ex.getMessage());
            sessionContext.setRollbackOnly();
        } catch (Exception t) {
            sessionContext.setRollbackOnly();
            log.error(t.getMessage());
            throw new FinATypeException(t, FinATypeException.Type.GENERAL_ERROR);
        }

        return statusResult;

    }

    @RolesAllowed(PermissionIdNames.CATALOG_EXPORT)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public MDTCatalogDataExportModel export(long catalogId, String langCode, String exportMode) {
        String fileName = "Catalog_data.xlsx";
        try {
            MDTCatalogMetaModel catalog = catalogProxySession.getCatalogById(catalogId, langCode);
            List<LanguageSampleModel> languages = languageProxySession.loadLanguages();
            fileName = catalog.getName() + ".xlsx";
            byte[] content;
            if (exportMode != null && exportMode.equalsIgnoreCase("FULL")) {
                content = new MDTCatalogExportUtil().exportToExcelAdvanced(catalog, languages, catalogItemProxySession.loadRowItemsAll(catalogId, langCode));
            } else {
                content = new MDTCatalogExportUtil().exportToExcelSimple(catalog, catalogItemProxySession.loadRowItemsAll(catalogId, langCode));
            }
            return new MDTCatalogDataExportModel(fileName, content);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return new MDTCatalogDataExportModel(fileName, new byte[0]);
    }
}
