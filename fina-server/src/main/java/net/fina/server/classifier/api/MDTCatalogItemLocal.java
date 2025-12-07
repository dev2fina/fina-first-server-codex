package net.fina.server.classifier.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.classifier.entity.MDTCatalogItem;
import net.fina.server.classifier.entity.MDTCatalogItemRow;
import net.fina.server.classifier.entity.MDTCatalogItemRowVersion;

import jakarta.ejb.Local;
import net.fina.server.classifier.model.MDTCatalogRowItemMetaModel;

import java.util.List;

@Local
public interface MDTCatalogItemLocal {
    List<MDTCatalogItem> load(long catalogId);

    List<MDTCatalogItem> loadByRows(long catalogId, long parentRowId, int offset, int limit, long langId, List<String> filterColumns);

    long countRootRows(long catalogId,  List<String> filterColumns);

    MDTCatalogItem createItem(MDTCatalogItem catalogItem) throws FinATypeException;

    MDTCatalogItem updateItem(MDTCatalogItem catalogItem);

    int getLastRowNumber(long catalogId);

    MDTCatalogItemRowVersion saveVersion(MDTCatalogItemRowVersion itemVersion);

    String getCurrentItemVersion(long catalogId, int rowNumber);

    MDTCatalogItemRowVersion getVersionItem(long catalogId, int rowNumber, String versionCode);

    void deleteRowItems(long categoryId, long rowId, Boolean deleteChildren) throws FinATypeException;

    void deleteRowItems(long categoryId, List<Long> rowIds, Boolean deleteChildren) throws FinATypeException;

    boolean hasActiveDependencies(long categoryId, List<Long> rowIds);

    List<MDTCatalogItem> loadVersionHistory(long catalogId, long rowId);

    MDTCatalogItemRow saveItemRow(MDTCatalogItemRow itemRow);

    MDTCatalogItem getItemById(long catalogItemId);

    MDTCatalogItemRow getItemRowById(long rowId);

    int count(long catalogId);

    void resetItemsLatestVersionStatus(long rowId);

    List<Long> restoreDeletedNode(MDTCatalogItemRow row);

    void moveMdtCatalogItem(long catalogId, long itemRowId, long itemRowNumber, long parentRowId);
}
