package net.fina.server.classifier.impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.property.PropertyKeys;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.classifier.MDTCatalogItemVersionHelper;
import net.fina.server.classifier.api.MDTCatalogItemLocal;
import net.fina.server.classifier.entity.*;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.security.api.PropertyLocal;
import net.fina.server.security.api.UserLocal;
import net.fina.server.util.DBUtil;

import java.util.*;
import java.util.stream.Collectors;

@Stateless
@Interceptors(RecordingAuditor.class)
public class MDTCatalogItemSession implements MDTCatalogItemLocal {

    @Inject
    private EntityManager em;

    @Inject
    private MDTNodeLocal mdtNodeLocal;

    @Inject
    private PropertyLocal propertyLocal;

    @Inject
    private UserLocal userLocal;

    @Override
    public List<MDTCatalogItem> load(long catalogId) {
        return em.createQuery("select new net.fina.server.classifier.entity.MDTCatalogItem(i.id, i.mdtCatalogColumn, i.version, i.value, i.nvalue, i.row, i.row.isDeleted, ver.createdAt) from IN_MDT_CATALOG_ITEMS i, IN_MDT_CATALOG_ITEM_ROW_VERSION ver where i.row.catalog.id=:catalogId and i.version.id=(select max(v.id) from IN_MDT_CATALOG_ITEM_ROW_VERSION v where " + " v.row.catalog.id=:catalogId and v.row.rowNumber=i.row.rowNumber) and ver.id=(select min(v.id) from IN_MDT_CATALOG_ITEM_ROW_VERSION v where v.row.catalog.id=:catalogId and v.row.rowNumber=i.row.rowNumber) order by i.row.rowNumber desc", MDTCatalogItem.class).setParameter("catalogId", catalogId).getResultList();
    }

    @Override
    public List<MDTCatalogItem> loadByRows(long catalogId, long parentRowId, int offset, int limit, long langId, List<String> filterColumns) {
        StringBuilder sb = new StringBuilder("SELECT NEW net.fina.server.classifier.entity.MDTCatalogItem(i.id, i.mdtCatalogColumn, i.version, i.value, i.nvalue, i.row, ir.isDeleted, irv.createdAt) " +
                "FROM IN_MDT_CATALOG_ITEMS i left join IN_MDT_CATALOG_ITEM_ROWS ir on ir.id=i.row.id left join IN_MDT_CATALOG_ITEM_ROW_VERSION irv on i.version.id=irv.id" +
                " where ir.catalog.id=:catalogId and i.latest=true " +
                (parentRowId >= 0 ? "and i.row.parentId=:parentRowId " : ""));

        if (filterColumns != null && !filterColumns.isEmpty()) {
            return loadFilteredItems(sb, catalogId, filterColumns, offset, limit, parentRowId);
        }

        List<Long> rows = loadCatalogRowIds(catalogId, parentRowId, offset, limit);

        if (!rows.isEmpty()) {
            String rowIdsConcatenateQuery = DBUtil.get().generateConcatenatedInStatementWithIds("ir.id", rows);
            sb.append("and (").append(rowIdsConcatenateQuery).append(") order by ir.rowNumber");

            return loadQueryResult(catalogId, parentRowId, sb);

        }
        return new ArrayList<>();
    }

    @Override
    public long countRootRows(long catalogId, List<String> filterColumns) {
        if (filterColumns != null && !filterColumns.isEmpty()) {
            StringBuilder sb = new StringBuilder("SELECT count(distinct ir.id) " +
                    "FROM IN_MDT_CATALOG_ITEMS i left join IN_MDT_CATALOG_ITEM_ROWS ir on ir.id=i.row.id left join IN_MDT_CATALOG_ITEM_ROW_VERSION irv on i.version.id=irv.id" +
                    " where ir.catalog.id=:catalogId and i.latest=true and i.row.parentId=0 ");

            constructRowIdsFilterQuery(sb, filterColumns, -1, -1, 0);

            TypedQuery<Long> countQuery = em.createQuery(sb.toString(), Long.class);
            countQuery.setParameter("catalogId", catalogId);
            return countQuery.getSingleResult();
        } else {
            return em.createQuery("select count(r.id) from IN_MDT_CATALOG_ITEM_ROWS r where r.catalog.id=:catalogId and r.parentId=:parentRowId", Long.class)
                    .setParameter("catalogId", catalogId).setParameter("parentRowId", 0L)
                    .getSingleResult();
        }
    }

    @Override
    public MDTCatalogItem createItem(MDTCatalogItem catalogItem) throws FinATypeException {
        em.persist(catalogItem);
        return catalogItem;
    }

    @Override
    public MDTCatalogItem updateItem(MDTCatalogItem catalogItem) {
        em.merge(catalogItem);
        return catalogItem;
    }

    @Override
    public int getLastRowNumber(long catalogId) {
        Integer rowNumber = em.createQuery("select max(i.row.rowNumber) from IN_MDT_CATALOG_ITEMS i where i.row.catalog.id=:catalogId", Integer.class).setParameter("catalogId", catalogId).getSingleResult();

        return rowNumber == null ? 0 : rowNumber;
    }

    @Override
    public MDTCatalogItemRowVersion saveVersion(MDTCatalogItemRowVersion itemVersion) {
        if (itemVersion.getId() > 0) {
            return em.merge(itemVersion);
        }
        em.persist(itemVersion);
        return itemVersion;
    }

    @Override
    public String getCurrentItemVersion(long catalogId, int rowNumber) {
        List<MDTCatalogItemRowVersion> versions = em.createQuery("select  iv from IN_MDT_CATALOG_ITEM_ROW_VERSION iv where iv.row.rowNumber=:rowNumber and iv.row.catalog.id=:catalogId order by iv.createdAt desc ", MDTCatalogItemRowVersion.class)
                .setParameter("catalogId", catalogId)
                .setParameter("rowNumber", rowNumber)
                .getResultList();

        return versions.isEmpty() ? "0" : versions.get(0).getVersionCode();
    }

    @Override
    public MDTCatalogItemRowVersion getVersionItem(long catalogId, int rowNumber, String versionCode) {
        return em.createQuery("select iv from IN_MDT_CATALOG_ITEM_ROW_VERSION iv where iv.row.rowNumber=:rowNumber and iv.row.catalog.id=:catalogId and iv.versionCode=:versionCode", MDTCatalogItemRowVersion.class).setParameter("catalogId", catalogId).setParameter("rowNumber", rowNumber).setParameter("versionCode", versionCode).getSingleResult();
    }

    @Override
    public void deleteRowItems(long categoryId, long rowId, Boolean deleteChildren) throws FinATypeException {
        List<MDTCatalogItemRow> itemRows = new ArrayList<>();

        List<MDTCatalogItemRow> rows = em.createQuery("select i from IN_MDT_CATALOG_ITEM_ROWS i where i.id=:rowId and i.catalog.id=:catalogId", MDTCatalogItemRow.class)
                .setParameter("rowId", rowId)
                .setParameter("catalogId", categoryId)
                .getResultList();

        for (MDTCatalogItemRow row : rows) {
            getActiveDependencies(categoryId, row, itemRows);
        }

        if ((deleteChildren == null || !deleteChildren) && !itemRows.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }

        itemRows.addAll(rows);

        for (MDTCatalogItemRow mdtCatalogItemRow : itemRows) {
            try {
                saveItemRowVersion(mdtCatalogItemRow, true);

                long dataNodeId = em.createQuery("select max(i.row.dataNode.id) from IN_MDT_CATALOG_ITEMS i where i.row.id=:rowId and i.row.catalog.id=:catalogId", Long.class).
                        setParameter("catalogId", categoryId)
                        .setParameter("rowId", mdtCatalogItemRow.getId())
                        .getSingleResult();
                String catalogTrashNodeCode = propertyLocal.getSystemProperty(PropertyKeys.CATALOG_TRASH_FOLDER_NODE_CODE);
                MDTNode catalogTrashNode = mdtNodeLocal.findByCode(catalogTrashNodeCode);

                if (catalogTrashNode == null) {
                    throw new FinATypeException("Catalog Trash Node is not created!");
                }
                mdtNodeLocal.moveNode(dataNodeId, catalogTrashNode.getId());
            } catch (NullPointerException ignored) {
            }
        }

        List<Long> itemRowIds = itemRows.stream().map(MDTCatalogItemRow::getId).collect(Collectors.toList());

        em.createQuery("update IN_MDT_CATALOG_ITEM_ROWS ir set ir.isDeleted=true where ir.id in (:itemRowIds)").setParameter("itemRowIds", itemRowIds).executeUpdate();

    }

    @Override
    public void deleteRowItems(long categoryId, List<Long> rowIds, Boolean deleteChildren) throws FinATypeException {
        for (Long i : rowIds) {
            deleteRowItems(categoryId, i, deleteChildren);
        }
    }

    @Override
    public boolean hasActiveDependencies(long categoryId, List<Long> rowIds) {
        List<MDTCatalogItemRow> itemRows = new ArrayList<>();

        List<MDTCatalogItemRow> rows = em.createQuery("select i from IN_MDT_CATALOG_ITEM_ROWS i where i.id in(:rowIds) and i.catalog.id=:catalogId", MDTCatalogItemRow.class)
                .setParameter("rowIds", rowIds)
                .setParameter("catalogId", categoryId)
                .getResultList();

        for (MDTCatalogItemRow row : rows) {
            getActiveDependencies(categoryId, row, itemRows);
        }

        return itemRows.size() > 0;
    }

    private void getActiveDependencies(long categoryId, MDTCatalogItemRow element, List<MDTCatalogItemRow> deleteRowIds) {
        List<MDTCatalogItemRow> activeChildren = em.createQuery("select i from IN_MDT_CATALOG_ITEM_ROWS i where i.parentId=:parentId and i.catalog.id=:catalogId", MDTCatalogItemRow.class).setParameter("parentId", element.getId()).setParameter("catalogId", categoryId).getResultList().stream().filter(e -> !e.isDeleted()).collect(Collectors.toList());

        for (MDTCatalogItemRow child : activeChildren) {
            deleteRowIds.add(child);
            getActiveDependencies(categoryId, child, deleteRowIds);
        }
    }

    @Override
    @SuppressWarnings("JpaQlInspection")
    public List<MDTCatalogItem> loadVersionHistory(long catalogId, long rowId) {
        List<MDTCatalogItemRowVersion> versions = em.createQuery(
                        "select v from IN_MDT_CATALOG_ITEM_ROW_VERSION v where v.row.id = :rowId order by v.id asc",
                        MDTCatalogItemRowVersion.class)
                .setParameter("rowId", rowId)
                .getResultList();

        if (versions.isEmpty()) {
            return Collections.emptyList();
        }

        List<MDTCatalogItem> allRowItems = em.createQuery("select i from IN_MDT_CATALOG_ITEMS i where i.row.id = :rowId", MDTCatalogItem.class)
                .setParameter("rowId", rowId)
                .getResultList();

        Map<Long, List<MDTCatalogItem>> itemsByVersion = allRowItems.stream().collect(Collectors.groupingBy(item -> item.getVersion().getId()));
        return MDTCatalogItemVersionHelper.getVersionHistory(versions, itemsByVersion);
    }


    @Override
    public MDTCatalogItemRow saveItemRow(MDTCatalogItemRow itemRow) {
        em.persist(itemRow);
        return itemRow;
    }

    @Override
    public MDTCatalogItem getItemById(long catalogItemId) {
        return em.find(MDTCatalogItem.class, catalogItemId);
    }

    @Override
    public MDTCatalogItemRow getItemRowById(long rowId) {
        return em.find(MDTCatalogItemRow.class, rowId);
    }

    @Override
    public int count(long catalogId) {
        return em.createQuery("select count(i.id) from IN_MDT_CATALOG_ITEMS i " + "where i.row.catalog.id=:catalogId and i.version.id=(select max(v.id) from IN_MDT_CATALOG_ITEM_ROW_VERSION v " + "where v.row.catalog.id=:catalogId and v.row.rowNumber=i.row.rowNumber)", Long.class).setParameter("catalogId", catalogId).getSingleResult().intValue();
    }

    @Override
    public void resetItemsLatestVersionStatus(long rowId) {
        em.createQuery("update IN_MDT_CATALOG_ITEMS i set i.latest=false where i.row.id=:rowId").setParameter("rowId", rowId).executeUpdate();
    }

    @Override
    public List<Long> restoreDeletedNode(MDTCatalogItemRow itemRow) {
        List<Long> path = new ArrayList<>();
        restoreParent(itemRow.getParentId(), path);
        path.add(itemRow.getId());
        itemRow.setDeleted(false);
        em.merge(itemRow);
        saveItemRowVersion(itemRow, false);
        return path;

    }

    @Override
    public void moveMdtCatalogItem(long catalogId, long itemRowId, long itemRowNumber, long parentRowId) {
        if (itemRowId == 0) itemRowId = 1;

        MDTCatalogItemRow movedRow = em.find(MDTCatalogItemRow.class, itemRowId);
        if (movedRow == null) return;

        boolean parentChanged = movedRow.getParentId() != parentRowId;

        if (parentChanged) {

            List<MDTCatalogItemRow> newSiblings = em.createQuery(
                            "SELECT ir FROM IN_MDT_CATALOG_ITEM_ROWS ir WHERE ir.parentId = :parentId and ir.catalog.id=:catalogId ORDER BY ir.rowNumber ASC",
                            MDTCatalogItemRow.class)
                    .setParameter("parentId", parentRowId)
                    .setParameter("catalogId", catalogId)
                    .getResultList();

            movedRow.setParentId(parentRowId);
            int targetIndex;
            if (itemRowNumber == -1) {
                targetIndex = newSiblings.size();
            } else {
                targetIndex = (int) Math.max(0, Math.min(itemRowNumber - 1, newSiblings.size()));
            }

            newSiblings.add(targetIndex, movedRow);

            for (int i = 0; i < newSiblings.size(); i++) {
                newSiblings.get(i).setRowNumber(i + 1);
            }
        } else {
            List<MDTCatalogItemRow> siblings = em.createQuery(
                            "SELECT ir FROM IN_MDT_CATALOG_ITEM_ROWS ir WHERE ir.parentId = :parentId AND ir.id != :movedId and ir.catalog.id = :catalogId ORDER BY ir.rowNumber ASC",
                            MDTCatalogItemRow.class)
                    .setParameter("parentId", parentRowId)
                    .setParameter("movedId", itemRowId)
                    .setParameter("catalogId", catalogId)
                    .getResultList();

            int targetSequence = (int) Math.max(1, Math.min(itemRowNumber, siblings.size() + 1)) - 1;
            siblings.add(targetSequence, movedRow);

            for (int i = 0; i < siblings.size(); i++) {
                siblings.get(i).setRowNumber(i + 1);
            }
        }
    }


    private void restoreParent(long parentId, List<Long> path) {
        if (parentId > 0) {
            path.add(parentId);
            MDTCatalogItemRow parent = em.find(MDTCatalogItemRow.class, parentId);
            em.createQuery("update IN_MDT_CATALOG_ITEM_ROWS r set r.isDeleted=false where r.id=:id")
                    .setParameter("id", parentId)
                    .executeUpdate();

            if (parent.getParentId() > 0) {
                restoreParent(parent.getParentId(), path);
            }
        }
    }

    private List<Long> loadCatalogRowIds(long catalogId, long parentRowId, int offset, int limit) {
        TypedQuery<Long> query = em.createQuery("select r.id from IN_MDT_CATALOG_ITEM_ROWS r where r.catalog.id=:catalogId " + (parentRowId >= 0 ? "and r.parentId=:parentRowId" : ""), Long.class)
                .setParameter("catalogId", catalogId);

        if (parentRowId >= 0) {
            query.setParameter("parentRowId", parentRowId);
        }

        if (!(parentRowId > 0)) {
            if (offset > 0) {
                query.setFirstResult(offset);
            }

            if (limit > 0) {
                query.setMaxResults(limit);
            }
        }
        return query.getResultList();

    }

    private List<MDTCatalogItem> loadItemsByColumnFilter(List<String> filterColumns, int start, int limit) {
        long langId = ThreadLocalHolder.getLanguage().getId();

        StringBuilder sb = new StringBuilder();
        boolean isValidList = filterColumns != null && !filterColumns.isEmpty();

        if (isValidList) {
            int parameterIndex = 0;

            sb.append("select i from IN_MDT_CATALOG_ITEMS i where ");

            for (int i = 0; i < filterColumns.size(); i++) {
                if (i > 0) {
                    sb.append(" or ");
                }
                String[] filter = filterColumns.get(i).split("=");
                Long key = getLongFilterValue(filter[0]);
                String val = filter[1];

                DataType type;
                MDTCatalogColumn column = em.find(MDTCatalogColumn.class, key);

                if (column == null) {
                    sb.append("i.mdtCatalogColumn.id =-1");
                    break;
                } else {
                    type = column.getDataType();
                }

                sb.append("(i.mdtCatalogColumn.id = :columnId").append(parameterIndex);
                switch (type) {
                    case DATE:
                        String[] dateSplit = val.split("\\|");
                        if (val.startsWith("|")) {
                            sb.append(" and (i.nvalue <= :to").append(parameterIndex).append("))");
                        } else if (val.endsWith("|")) {
                            sb.append(" and (i.nvalue >= :from").append(parameterIndex).append("))");
                        } else if (dateSplit.length == 2) {
                            sb.append(" and (i.nvalue >= :from").append(parameterIndex).append(" and i.nvalue <= :to").append(parameterIndex).append("))");
                        }
                        break;
                    case STRING:
                        sb.append(" and exists (select 1 from SYS_STRINGS s where i.value = s.id and s.langId = :langId and s.value like :columnValue").append(parameterIndex).append("))");
                        break;
                    case NUMBER:
                    case INTEGER:
                        sb.append(" and (i.nvalue = :nvalue").append(parameterIndex).append("))");
                        break;
                }

                parameterIndex++;
            }
        }
        sb.append(" and i.latest=true");
        sb.append(" order by i.row.rowNumber desc");

        TypedQuery<MDTCatalogItem> query = em.createQuery(sb.toString(), MDTCatalogItem.class);

        if (isValidList) {
            int parameterIndex = 0;

            for (String filter : filterColumns) {
                String[] split = filter.split("=");
                Long key = getLongFilterValue(split[0]);
                String val = split[1];

                DataType type;
                MDTCatalogColumn column = em.find(MDTCatalogColumn.class, key);

                if (column == null) {
                    break;
                } else {
                    type = column.getDataType();
                }

                query.setParameter("columnId" + parameterIndex, key == null ? -1 : key);

                switch (type) {
                    case DATE:
                        // | is delimiter for date filter values (from|to)
                        String[] dateSplit = val.split("\\|");
                        if (val.startsWith("|")) {
                            query.setParameter("to" + parameterIndex, getDoubleFilterValue(dateSplit[1]));
                        } else if (val.endsWith("|")) {
                            query.setParameter("from" + parameterIndex, getDoubleFilterValue(dateSplit[0]));
                        } else if (dateSplit.length == 2) {
                            query.setParameter("from" + parameterIndex, getDoubleFilterValue(dateSplit[0]));
                            query.setParameter("to" + parameterIndex, getDoubleFilterValue(dateSplit[1]));
                        }
                        break;
                    case INTEGER:
                    case NUMBER:
                        double nValue = getDoubleFilterValue(val);
                        query.setParameter("nvalue" + parameterIndex, nValue);
                        break;
                    case STRING:
                        query.setParameter("langId", langId);
                        query.setParameter("columnValue" + parameterIndex, "%" + val + "%");
                        break;
                }

                parameterIndex++;
            }
        }

        if (start >= 0 && limit > 0) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    private List<MDTCatalogItem> loadFilteredItems(StringBuilder sb, long catalogId, List<String> filterColumns, int offset, int limit, long parentRowId) {
        constructRowIdsFilterQuery(sb, filterColumns, offset, limit, parentRowId);
        return loadQueryResult(catalogId, parentRowId, sb);

    }

    private void constructRowIdsFilterQuery(StringBuilder sb, List<String> filterColumns, int offset, int limit, long parentRowId) {
        if (filterColumns != null && !filterColumns.isEmpty()) {
            String rowIdsConcatenateQuery = "";
            List<MDTCatalogItem> filteredItems = loadItemsByColumnFilter(filterColumns, offset, limit);

            if (filteredItems != null && !filteredItems.isEmpty()) {

                Set<MDTCatalogItemRow> mdtCatalogItemRows = new HashSet<>();
                Set<MDTCatalogItemRow> mdtCatalogItemRootRows = new HashSet<>();

                for (MDTCatalogItem mdtCatalogItem : filteredItems) {
                    initCatalogItemRowHierarchy(mdtCatalogItemRows, mdtCatalogItemRootRows, mdtCatalogItem.getRow());
                }

                if (!mdtCatalogItemRows.isEmpty()) {
                    rowIdsConcatenateQuery = generateRowIdsStatement(parentRowId, limit, mdtCatalogItemRows, mdtCatalogItemRootRows);
                }
            }
            if (!rowIdsConcatenateQuery.trim().isEmpty()) {
                sb.append(" and(").append(rowIdsConcatenateQuery).append(")");
            } else {
                sb.append(" and i.row.id in (-1)");
            }
        }

    }

    private MDTCatalogItemRow getCatalogItemFirstLevelRoot(MDTCatalogItemRow row) {
        if (row.getParentId() == 0) {
            return row;
        }
        return getCatalogItemFirstLevelRoot(getItemRowById(row.getParentId()));
    }

    private void initCatalogItemRowHierarchy(Set<MDTCatalogItemRow> rows, Set<MDTCatalogItemRow> rootRows, MDTCatalogItemRow row) {
        rows.add(row);
        if (row.getParentId() != 0) {
            initCatalogItemRowHierarchy(rows, rootRows, getItemRowById(row.getParentId()));
        } else {
            rootRows.add(row);
        }
    }

    private String generateRowIdsStatement(long parentRowId, int limit, Set<MDTCatalogItemRow> mdtCatalogItemRows, Set<MDTCatalogItemRow> mdtCatalogItemRootRows) {
        String rowIdsConcatenateQuery;
        if (parentRowId > 0) {
            return DBUtil.get().generateConcatenatedInStatementWithIds("i.row.id", mdtCatalogItemRows.stream().map(MDTCatalogItemRow::getId).collect(Collectors.toList()));
        } else {

            List<MDTCatalogItemRow> filterRows = new ArrayList<>(mdtCatalogItemRows);

            if (mdtCatalogItemRootRows.size() > limit) {
                filterRows = new ArrayList<>();

                List<MDTCatalogItemRow> root = new ArrayList<>(mdtCatalogItemRootRows);
                List<Long> rootIds = root.stream().map(MDTCatalogItemRow::getId).toList();

                for (MDTCatalogItemRow row : mdtCatalogItemRows) {
                    if (rootIds.contains(getCatalogItemFirstLevelRoot(row).getId())) {
                        filterRows.add(row);
                    }
                }
            }
            rowIdsConcatenateQuery = DBUtil.get().generateConcatenatedInStatementWithIds("i.row.id", filterRows.stream().map(MDTCatalogItemRow::getId).collect(Collectors.toList()));
        }
        return rowIdsConcatenateQuery;
    }

    private List<MDTCatalogItem> loadQueryResult(long catalogId, long parentRowId, StringBuilder sb) {
        TypedQuery<MDTCatalogItem> itemsQuery = em.createQuery(sb.toString(), MDTCatalogItem.class);
        itemsQuery.setParameter("catalogId", catalogId);

        if (parentRowId >= 0 && sb.toString().contains(":parentRowId")) {
            itemsQuery.setParameter("parentRowId", parentRowId);
        }
        return itemsQuery.getResultList();
    }

    private Long getLongFilterValue(String filterValue) {
        long result;
        try {
            result = Long.parseLong(filterValue);
        } catch (Throwable ignored) {
            return -1L;
        }
        return result;
    }

    private double getDoubleFilterValue(String filterValue) {
        double result;
        try {
            result = Double.parseDouble(filterValue);
        } catch (Throwable ignored) {
            return -1;
        }
        return result;
    }

    private void saveItemRowVersion(MDTCatalogItemRow mdtCatalogItemRow, boolean deleted) {
        MDTCatalogItemRowVersion itemVersion = new MDTCatalogItemRowVersion();
        itemVersion.setCreatedAt(new Date());
        itemVersion.setUser(userLocal.getCurrentUser());
        itemVersion.setVersionCode(MDTCatalogItemVersionHelper.getVersion(getCurrentItemVersion(mdtCatalogItemRow.getCatalog().getId(), mdtCatalogItemRow.getRowNumber())));
        itemVersion.setRow(mdtCatalogItemRow);
        itemVersion.setDeleted(deleted);
        saveVersion(itemVersion);
    }


}
