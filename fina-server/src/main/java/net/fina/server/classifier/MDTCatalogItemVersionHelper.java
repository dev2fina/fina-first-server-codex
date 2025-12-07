package net.fina.server.classifier;

import net.fina.server.classifier.entity.MDTCatalogItem;
import net.fina.server.classifier.entity.MDTCatalogItemRow;
import net.fina.server.classifier.entity.MDTCatalogItemRowVersion;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MDTCatalogItemVersionHelper {
    private static final Logger log = Logger.getLogger(MDTCatalogItemVersionHelper.class.getName());

    public static String getVersion(String version) {
        try {
            int v = Integer.parseInt(version);
            return String.valueOf(v + 1);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return String.valueOf(0);
    }

    public static List<MDTCatalogItem> getVersionHistory(List<MDTCatalogItemRowVersion> versions, Map<Long, List<MDTCatalogItem>> itemsByVersion) {
        List<MDTCatalogItem> result = new ArrayList<>();
        List<MDTCatalogItem> previousItems = null;
        MDTCatalogItemRow itemRow = versions.getFirst().getRow();

        for (MDTCatalogItemRowVersion version : versions) {
            previousItems = constructRowVersion(version, previousItems, itemRow, result, itemsByVersion);
        }

        return result;
    }

    private static List<MDTCatalogItem> constructRowVersion(MDTCatalogItemRowVersion version,
                                                            List<MDTCatalogItem> previousItems,
                                                            MDTCatalogItemRow itemRow,
                                                            List<MDTCatalogItem> result,
                                                            Map<Long, List<MDTCatalogItem>> itemsByVersion) {
        if (version.isDeleted()) {
            if (previousItems != null && !previousItems.isEmpty()) {
                for (MDTCatalogItem prev : previousItems) {
                    MDTCatalogItem copy = getMdtCatalogItem(itemRow, prev, version, true);
                    result.add(copy);
                }
            }
            return previousItems;
        } else {
            List<MDTCatalogItem> versionItems = itemsByVersion.getOrDefault(version.getId(), Collections.emptyList());

            if (versionItems.isEmpty() && previousItems != null && !previousItems.isEmpty()) {
                for (MDTCatalogItem prev : previousItems) {
                    MDTCatalogItem restored = getMdtCatalogItem(itemRow, prev, version, false);
                    result.add(restored);
                }
                return previousItems;
            }

            for (MDTCatalogItem item : versionItems) {
                item.setDeleted(false);
                item.setCreatedAt(version.getCreatedAt());
                result.add(item);
            }
            return versionItems;
        }
    }

    private static MDTCatalogItem getMdtCatalogItem(MDTCatalogItemRow itemRow, MDTCatalogItem prev, MDTCatalogItemRowVersion version, boolean deleted) {
        MDTCatalogItem restored = new MDTCatalogItem();
        restored.setId(0);
        restored.setRow(itemRow);
        restored.setMdtCatalogColumn(prev.getMdtCatalogColumn());
        restored.setVersion(version);
        restored.setValue(prev.getValue());
        restored.setNvalue(prev.getNvalue());
        restored.setDeleted(deleted);
        restored.setLatest(false);
        restored.setCreatedAt(version.getCreatedAt());
        return restored;
    }
}
