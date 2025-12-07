package net.fina.server.classifier.proxy;

import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.server.util.PagingUtil;
import net.fina.common.shared.LanguageSampleModel;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.classifier.MDTCatalogItemVersionHelper;
import net.fina.server.classifier.api.MDTCatalogItemLocal;
import net.fina.server.classifier.api.MDTCatalogLocal;
import net.fina.server.classifier.entity.*;
import net.fina.server.classifier.model.MDTCatalogItemMetaModel;
import net.fina.server.classifier.model.MDTCatalogRowItemMetaModel;
import net.fina.server.classifier.model.helper.MdtCatalogItemModelHelper;
import net.fina.server.i18n.helper.Description;
import net.fina.server.i18n.proxy.LanguageProxySession;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.security.api.PropertyLocal;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.User;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@SecurityDomain("FinASecurityDomain")
@PermitAll
public class MdtCatalogItemProxySession {
    private final Logger log = Logger.getLogger(getClass().getName());

    @Inject
    private MDTCatalogLocal mdtCatalogLocal;
    @Inject
    private MDTCatalogItemLocal mdtCatalogItemLocal;
    @Inject
    private MDTNodeLocal mdtNodeLocal;
    @Inject
    private LanguageProxySession languageProxySession;
    @Inject
    private UserLocal userLocal;
    @Resource
    private SessionContext sessionContext;
    @Inject
    private PropertyLocal propertyLocal;

    @RolesAllowed({PermissionIdNames.CATALOG_AMEND, PermissionIdNames.CATALOG_IMPORT})
    @Transactional(rollbackOn = FinATypeException.class)
    public MDTCatalogRowItemMetaModel create(long catalogId, MDTCatalogRowItemMetaModel model) throws FinATypeException {
        MDTCatalogItemMetaModel keyItem = model.getRowItems().stream().filter(i -> i.getColumn().isKey()).findFirst().orElse(null);
        if (keyItem != null) {
            List<LanguageSampleModel> allLanguages = languageProxySession.loadLanguages();
            Map<Long, String> langIdValueMap = allLanguages.stream().collect(Collectors.toMap(LanguageSampleModel::getId, l -> (String) keyItem.getValue()));
            keyItem.setValuesI18n(langIdValueMap);
        }
        return create(mdtCatalogLocal.findById(catalogId), model, -1, -1, userLocal.getCurrentUser(), false);
    }

    @RolesAllowed({PermissionIdNames.CATALOG_AMEND, PermissionIdNames.CATALOG_IMPORT})
    public MDTCatalogRowItemMetaModel create(MDTCatalog catalog, MDTCatalogRowItemMetaModel model, int rowNumber, int nodeNumber, User user, boolean imported) throws FinATypeException {
        try {
            //validate
            validateCatalogItems(model.getRowItems());

            // check if parent is not leaf
            checkParentRow(model.getParentRowId());
            LanguageSampleModel language = ThreadLocalHolder.getLanguage();

            int lastRowNumber = rowNumber <= 0 ? mdtCatalogItemLocal.getLastRowNumber(catalog.getId()) + 1 : rowNumber;

            MDTNode dataElementNode = null;
            if (model.isLeaf() && !model.isDeleted()) {
                dataElementNode = createDataElementNode(catalog, model, language.getId(), imported ? nodeNumber : rowNumber, imported);
                model.setNodeId(dataElementNode.getId());
            }

            // save row
            MDTCatalogItemRow itemRow = new MDTCatalogItemRow();
            itemRow.setCatalog(catalog);
            itemRow.setLeaf(true);
            itemRow.setRowNumber(lastRowNumber);
            itemRow.setDataNode(dataElementNode);
            itemRow.setLeaf(model.isLeaf());
            itemRow.setParentId(model.getParentRowId());
            itemRow.setDeleted(model.isDeleted());
            itemRow = mdtCatalogItemLocal.saveItemRow(itemRow);

            //save row version
            MDTCatalogItemRowVersion itemVersion = new MDTCatalogItemRowVersion();
            itemVersion.setCreatedAt(new Date());
            itemVersion.setUser(user);
            itemVersion.setVersionCode("" + 0);
            itemVersion.setRow(itemRow);
            itemVersion = mdtCatalogItemLocal.saveVersion(itemVersion);

            //save items
            for (MDTCatalogItemMetaModel itemModel : model.getRowItems()) {
                if (itemModel.getColumn().getIsRequired() && itemModel.getValue() == null) {
                    throw new FinATypeException("Fill Required Fields");
                }

                MDTCatalogItem item = new MDTCatalogItem();
                item.setId(0);
                item.setRow(itemRow);
                item.setMdtCatalogColumn(new MDTCatalogColumn(itemModel.getColumn().getId(), itemModel.getColumn().isKey()));
                item.setVersion(itemVersion);
                item.setLatest(true);

                if (itemModel.getValue() != null) {
                    if (itemModel.getColumn().isKey() && itemRow.isLeaf()) {
                        item.setValue(new Description(language.getId(), 0, itemModel.getValue().toString()));
                    } else {
                        switch (itemModel.getColumn().getDataType()) {
                            case STRING:
                                if (itemModel.getValuesI18n() != null && !itemModel.getValuesI18n().isEmpty()) {
                                    Description description = new Description();
                                    description.setNameStrId(0);
                                    for (Map.Entry<Long, String> entry : itemModel.getValuesI18n().entrySet()) {
                                        if (entry.getValue() != null) {
                                            description.addDescription(entry.getKey(), entry.getValue());
                                        }
                                    }
                                    item.setValue(description);
                                } else {
                                    item.setValue(new Description(language.getId(), 0, itemModel.getValue().toString()));
                                }
                                break;
                            case NUMBER:
                                item.setNvalue(((Number) itemModel.getValue()).doubleValue());
                                break;
                            case INTEGER:
                                item.setNvalue(((Number) itemModel.getValue()).intValue());
                                break;
                            case DATE:
                                item.setNvalue(((Number) itemModel.getValue()).longValue());
                                break;
                        }
                    }
                }

                item = mdtCatalogItemLocal.createItem(item);
                itemModel.setId(item.getId());
                itemModel.setRowNumber(itemRow.getRowNumber());
            }

            model.setRowId(itemRow.getId());
            model.setParentRowId(itemRow.getParentId());
            model.setRowNumber(lastRowNumber);
            model.setCreatedAt(itemVersion.getCreatedAt());
        } catch (FinATypeException ft) {
            sessionContext.setRollbackOnly();
            log.error(ft.getMessage(), ft);
            throw ft;
        } catch (Throwable t) {
            sessionContext.setRollbackOnly();
            log.error(t.getMessage(), t);
            throw new FinATypeException(FinATypeException.Type.GENERAL_ERROR);
        }

        return model;
    }

    @RolesAllowed({PermissionIdNames.CATALOG_AMEND, PermissionIdNames.CATALOG_IMPORT})
    @Transactional(rollbackOn = FinATypeException.class)
    private MDTNode createDataElementNode(MDTCatalog catalog, MDTCatalogRowItemMetaModel model, long langId, int rowNumber, boolean imported) throws FinATypeException {
        MDTNode dataElementNode = new MDTNode();
        dataElementNode.setId(0);
        dataElementNode.setParentId(catalog.getCatalogNode().getId());
        dataElementNode.setType(MDTNodeTypes.DATA);
        dataElementNode.setDataType(MDTNodeDataTypes.UNKNOWN);
        MDTCatalogItemMetaModel keyItem = model.getRowItems().stream().filter(i -> i.getColumn().isKey()).findFirst().get();

        StringBuilder newCode = new StringBuilder();
        String originalCode = catalog.getCatalogNode().getCode();

        String catalogTrashNodeCode = propertyLocal.getSystemProperty(PropertyKeys.CATALOG_TRASH_FOLDER_NODE_CODE);
        MDTNode catalogTrashNode = mdtNodeLocal.findByCode(catalogTrashNodeCode);

        if (rowNumber <= 0) {
            long childCount = mdtNodeLocal.childrenCount(dataElementNode.getParentId());
            newCode.setLength(0);
            newCode.trimToSize();
            newCode.append(originalCode).append(".").append(childCount + 1);
            dataElementNode.setCode(newCode.toString());

            if (!mdtNodeLocal.checkCodeUnique(dataElementNode)) {
                long deletedNodesCount = mdtNodeLocal.childrenCountWhereCodeLike(catalogTrashNode.getId(), originalCode + ".%");
                dataElementNode.setCode(originalCode + "." + (deletedNodesCount + childCount + 1));
            }
        } else {
            newCode.setLength(0);
            newCode.trimToSize();
            newCode.append(originalCode).append(".").append(rowNumber);
            dataElementNode.setCode(newCode.toString());
        }

        //check unique key in equation inside data elements
        if (!imported) {
            checkUniqueKey(keyItem.getValue().toString().trim(), catalog.getCatalogNode(), catalogTrashNode);
        }


        dataElementNode.setEquation(keyItem.getValue().toString());
        Description description = new Description();
        description.setNameStrId(0);

        for (Map.Entry<Long, String> entry : keyItem.getValuesI18n().entrySet()) {
            description.addDescription(entry.getKey(), entry.getValue());
        }

        dataElementNode.setDescription(description);
        dataElementNode.setCatalog(true);
        dataElementNode.setSequence(rowNumber <= 0 ? mdtNodeLocal.getNodeChildMaxSequence(catalog.getCatalogNode().getId()) + 1 : rowNumber);
        if (imported) {
            return mdtNodeLocal.saveImportedNode(dataElementNode);
        }
        return mdtNodeLocal.save(dataElementNode);
    }

    @RolesAllowed({PermissionIdNames.CATALOG_AMEND, PermissionIdNames.CATALOG_IMPORT})
    @Transactional(rollbackOn = FinATypeException.class)
    public MDTCatalogRowItemMetaModel update(long catalogId, MDTCatalogRowItemMetaModel model, String langCode) throws FinATypeException {


        try {
            // check if parent is not leaf
            checkParentRow(model.getParentRowId());
            LanguageSampleModel language = languageProxySession.getLanguageByCode(langCode);

            MDTCatalogItemRow itemRow = new MDTCatalogItemRow();
            itemRow.setId(model.getRowId());

            //save row version
            MDTCatalogItemRowVersion itemVersion = new MDTCatalogItemRowVersion();
            itemVersion.setCreatedAt(new Date());
            itemVersion.setUser(userLocal.getCurrentUser());
            itemVersion.setVersionCode(MDTCatalogItemVersionHelper.getVersion(mdtCatalogItemLocal.getCurrentItemVersion(catalogId, model.getRowNumber())));
            itemVersion.setRow(itemRow);
            itemVersion = mdtCatalogItemLocal.saveVersion(itemVersion);

            model.setModifiedAt(itemVersion.getCreatedAt());
            MDTNode dataElementNode = null;
            if (model.isLeaf()) {
                dataElementNode = mdtNodeLocal.findNodeById(model.getNodeId());
                if (dataElementNode == null) {
                    throw new FinATypeException("catalog item mdt node id is not valid or does not exists");
                }
            }

            mdtCatalogItemLocal.resetItemsLatestVersionStatus(model.getRowId());

            for (MDTCatalogItemMetaModel itemModel : model.getRowItems()) {
                if (itemModel.getColumn().getIsRequired() && itemModel.getValue() == null) {
                    throw new FinATypeException("Fill Required Fields");
                }

                MDTCatalogItem item = new MDTCatalogItem();
                item.setId(0);
                item.setRow(itemRow);
                item.setMdtCatalogColumn(new MDTCatalogColumn(itemModel.getColumn().getId()));
                item.setVersion(itemVersion);
                item.setLatest(true);

                if (itemModel.getValue() != null) {
                    if (!itemModel.getColumn().isKey()) {
                        switch (itemModel.getColumn().getDataType()) {
                            case STRING:
                                MDTCatalogItem catalogItem = mdtCatalogItemLocal.getItemById(itemModel.getId());
                                Description description = new Description(language.getId(), 0, itemModel.getValue().toString());

                                if (catalogItem != null) {
                                    if (catalogItem.getValue() != null && catalogItem.getValue().getDescriptions() != null) {
                                        for (Map.Entry<Long, String> desc : catalogItem.getValue().getDescriptions().entrySet()) {
                                            if (desc.getKey() != language.getId()) {
                                                description.addDescription(desc.getKey(), desc.getValue());
                                            }
                                        }
                                    }
                                }

                                item.setValue(description);
                                break;
                            case NUMBER:
                                item.setNvalue(((Number) itemModel.getValue()).doubleValue());
                                break;
                            case INTEGER:
                                item.setNvalue(((Number) itemModel.getValue()).intValue());
                                break;
                            case DATE:
                                if (itemModel.getValue() instanceof String) {
                                    item.setNvalue(new BigDecimal((String) itemModel.getValue()).longValue());
                                } else {
                                    item.setNvalue(((Number) itemModel.getValue()).longValue());
                                }
                                break;
                        }
                    } else {
                        MDTCatalogItem mdtCatalogItem = mdtCatalogItemLocal.getItemById(itemModel.getId());
                        if (dataElementNode != null) {
                            dataElementNode.setDescription(new Description(language.getId(), dataElementNode.getDescription().getNameStrId(), dataElementNode.getEquation()));
                            mdtNodeLocal.save(dataElementNode);
                        }

                        if (!mdtCatalogItem.getRow().isLeaf()) {
                            item.setValue(mdtCatalogItem.getValue());
                        } else {
                            item.setValue(dataElementNode.getDescription());
                        }
                    }
                }

                mdtCatalogItemLocal.createItem(item);
            }

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            if (t instanceof FinATypeException) {
                throw t;
            }
            throw new FinATypeException(FinATypeException.Type.GENERAL_ERROR);
        }

        return model;
    }

    @RolesAllowed({PermissionIdNames.CATALOG_REVIEW, PermissionIdNames.CATALOG_EXPORT})
    public List<MDTCatalogRowItemMetaModel> loadRowItems(long catalogId, String langCode) {
        List<MDTCatalogRowItemMetaModel> result = new ArrayList<>();
        try {
            LanguageSampleModel language = languageProxySession.getLanguageByCode(langCode);
            MDTCatalog catalog = mdtCatalogLocal.findById(catalogId);

            List<MDTCatalogItem> rowItems = mdtCatalogItemLocal.load(catalogId);
            result = convertItemsToRowItems(rowItems, catalog, language.getId());

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return result;
    }

    @RolesAllowed(PermissionIdNames.CATALOG_REVIEW)
    public PaginatedListWrapper<MDTCatalogRowItemMetaModel> loadRowItems(int page, int limit, long catalogId, long parentRowId, List<String> filterColumns) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        List<MDTCatalogRowItemMetaModel> resultModels = new ArrayList<>();
        PaginatedListWrapper<MDTCatalogRowItemMetaModel> result = new PaginatedListWrapper<>();
        result.setList(resultModels);
        result.setCurrentPage(page);


        MDTCatalog catalog = mdtCatalogLocal.findById(catalogId);

        int offset = PagingUtil.getOffsetFromPage(page, limit);
        List<MDTCatalogItem> rowItems = mdtCatalogItemLocal.loadByRows(catalogId, parentRowId, offset, limit, langId, filterColumns);
        if (!rowItems.isEmpty()) {
            result.setList(convertItemsToRowItems(rowItems, catalog, langId));
            result.setTotalResults(mdtCatalogItemLocal.countRootRows(catalogId, filterColumns));
        } else {
            result.setTotalResults(0);
        }

        return result;
    }

    @RolesAllowed({PermissionIdNames.CATALOG_REVIEW, PermissionIdNames.CATALOG_EXPORT})
    public List<MDTCatalogRowItemMetaModel> loadRowItemsAll(long catalogId, String langCode) {
        LanguageSampleModel language = languageProxySession.getLanguageByCode(langCode);
        MDTCatalog catalog = mdtCatalogLocal.findById(catalogId);

        List<MDTCatalogItem> rowItems = mdtCatalogItemLocal.loadByRows(catalogId, -1, -1, -1, -1, null);
        return convertItemsToRowItems(rowItems, catalog, language.getId());
    }

    @RolesAllowed(PermissionIdNames.CATALOG_DELETE)
    public void deleteRow(long categoryId, long rowId, Boolean deleteChildren) throws FinATypeException {
        mdtCatalogItemLocal.deleteRowItems(categoryId, rowId, deleteChildren);
    }

    @RolesAllowed(PermissionIdNames.CATALOG_DELETE)
    public void deleteRows(long categoryId, List<Long> rowIds, Boolean deleteChildren) throws FinATypeException {
        mdtCatalogItemLocal.deleteRowItems(categoryId, rowIds, deleteChildren);
    }

    @RolesAllowed(PermissionIdNames.CATALOG_REVIEW)
    public boolean hasActiveDependencies(long categoryId, List<Long> rowIds) {
        return mdtCatalogItemLocal.hasActiveDependencies(categoryId, rowIds);
    }

    @RolesAllowed(PermissionIdNames.CATALOG_REVIEW)
    public List<MDTCatalogRowItemMetaModel> loadVersionHistory(long catalogId, long rowId, String langCode) {
        LanguageSampleModel language = languageProxySession.getLanguageByCode(langCode);
        MDTCatalog catalog = mdtCatalogLocal.findById(catalogId);

        List<MDTCatalogItem> items = mdtCatalogItemLocal.loadVersionHistory(catalogId, rowId);

        return convertItemsToRowItems(items, catalog, language.getId());
    }


    @RolesAllowed(PermissionIdNames.CATALOG_DELETE)
    public List<Long> restoreDeletedRow(long rowId) throws FinATypeException {
        MDTCatalogItemRow itemRow = mdtCatalogItemLocal.getItemRowById(rowId);
        if (itemRow == null) throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, "Catalog Row Not Found");
        if (itemRow.isDeleted()) {

            if (itemRow.isLeaf()) {
                MDTCatalog catalog = itemRow.getCatalog();
                MDTNode dataNode = itemRow.getDataNode();
                //restore node
                mdtNodeLocal.moveNode(dataNode.getId(), catalog.getCatalogNode().getId());
            }

            //restore row
            return mdtCatalogItemLocal.restoreDeletedNode(itemRow);
        } else {
            throw new FinATypeException("Row Is Not Deleted!");
        }
    }

    private List<MDTCatalogRowItemMetaModel> convertItemsToRowItems(List<MDTCatalogItem> rowItems, MDTCatalog catalog, long langId) {
        List<MDTCatalogRowItemMetaModel> result = new ArrayList<>();

        Map<Long, Map<String, Map<Long, MDTCatalogItem>>> rowMap = new LinkedHashMap<>();

        for (MDTCatalogItem rowItem : rowItems) {
            Map<String, Map<Long, MDTCatalogItem>> versionMap = rowMap.computeIfAbsent(rowItem.getRow().getId(), s -> new HashMap<>());
            Map<Long, MDTCatalogItem> itemList = versionMap.computeIfAbsent(rowItem.getVersion().getVersionCode(), s -> new HashMap<>());
            itemList.put(rowItem.getMdtCatalogColumn().getId(), rowItem);
        }

        for (Map.Entry<Long, Map<String, Map<Long, MDTCatalogItem>>> rows : rowMap.entrySet()) {
            for (Map.Entry<String, Map<Long, MDTCatalogItem>> version : rows.getValue().entrySet()) {
                List<MDTCatalogItemMetaModel> itemList = new ArrayList<>();
                MDTCatalogRowItemMetaModel row = new MDTCatalogRowItemMetaModel();
                MDTCatalogItem ritem = version.getValue().entrySet().stream().findFirst().get().getValue();

                MDTCatalogItemRow itemRow = ritem.getRow();

                row.setRowNumber(itemRow.getRowNumber());
                row.setRowId(itemRow.getId());
                row.setParentRowId(itemRow.getParentId());
                row.setVersion(ritem.getVersion().getVersionCode());
                row.setNodeId(itemRow.isLeaf() ? itemRow.getDataNode() != null ? itemRow.getDataNode().getId() : 0 : 0);
                row.setLeaf(itemRow.isLeaf());
                row.setCreatedAt(ritem.getCreatedAt());
                row.setModifiedAt(ritem.getVersion().getCreatedAt());
                row.setDeleted(ritem.isDeleted());

                List<MDTCatalogColumn> columns = catalog.getCatalogColumns();
                columns.sort(Comparator.comparingInt(MDTCatalogColumn::getSequence));

                for (MDTCatalogColumn catalogColumn : columns) {
                    MDTCatalogItem item = version.getValue().get(catalogColumn.getId());
                    if (item == null) {
                        item = new MDTCatalogItem();
                        item.setRow(ritem.getRow());
                        item.setMdtCatalogColumn(catalogColumn);
                        item.setVersion(new MDTCatalogItemRowVersion());
                    }
                    MDTCatalogItemMetaModel itemModel = MdtCatalogItemModelHelper.toModel(item, langId);
                    if (item.getValue() != null && item.getValue().getDescriptions() != null) {
                        itemModel.getValuesI18n().putAll(item.getValue().getDescriptions());

                    }
                    itemList.add(itemModel);

                    if (item.getMdtCatalogColumn().isKey()) {
                        itemModel.setValue(item.getValue() != null ? item.getValue().getDescription(langId) : "");
                    }

                    if (item.getMdtCatalogColumn().isKey() && item.getRow().isLeaf()) {
                        itemModel.setValue(item.getRow().getDataNode() != null ? item.getRow().getDataNode().getEquation() : itemModel.getValue());
                    }
                }

                row.setRowItems(itemList);
                result.add(row);
            }
        }
        return result;
    }

    public int count(long catalogId) {
        return mdtCatalogItemLocal.count(catalogId);
    }

    public void moveMdtCatalogItemRow(long catalogId, long itemRowId, long itemRowNumber, long parentRowId) {
        mdtCatalogItemLocal.moveMdtCatalogItem(catalogId, itemRowId, itemRowNumber, parentRowId);
    }

    private void checkParentRow(long parentRowId) throws FinATypeException {
        if (parentRowId != 0) {
            MDTCatalogItemRow parentItemRow = mdtCatalogItemLocal.getItemRowById(parentRowId);
            if (parentItemRow.isLeaf()) {
                throw new FinATypeException("Leaf can not have children!");
            }
        }
    }

    private void checkUniqueKey(String key, MDTNode catalogNode, MDTNode catalogTrashNode) throws FinATypeException {
        mdtNodeLocal.checkUniqueEquationInSubTree(catalogNode, catalogTrashNode, key);
    }


    private void validateCatalogItems(List<MDTCatalogItemMetaModel> items) throws FinATypeException {

        for (MDTCatalogItemMetaModel item : items) {
            if (item.getColumn().isKey()) {
                if (item.getValue() == null || ((String) item.getValue()).trim().isEmpty()) {
                    throw new FinATypeException("Catalog Key is Required ");
                }

            } else if (item.getColumn().getIsRequired()) {
                boolean valid = true;
                switch (item.getColumn().getDataType()) {
                    case STRING:
                        valid = item.getValue() != null && !((String) item.getValue()).trim().isEmpty();
                        break;
                    case DATE:
                        valid = item.getValue() != null && ((Number) item.getValue()).longValue() > 0;
                        break;
                    case INTEGER:
                    case NUMBER:
                        valid = item.getValue() != null;
                        break;
                }

                if (!valid) {
                    throw new FinATypeException("Column [" + item.getColumn().getName() + "] is required. value= [" + item.getValue() + "]");
                }
            }

        }
    }
}
