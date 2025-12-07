package net.fina.server.classifier.model.helper;

import net.fina.server.classifier.entity.MDTCatalogItem;
import net.fina.server.classifier.entity.MDTCatalogItemRow;
import net.fina.server.classifier.model.MDTCatalogItemMetaModel;

import java.util.Date;

public class MdtCatalogItemModelHelper {
    public static MDTCatalogItemMetaModel toModel(MDTCatalogItem entity, long langId) {
        MDTCatalogItemRow itemRow = entity.getRow();

        MDTCatalogItemMetaModel result = new MDTCatalogItemMetaModel();
        result.setId(entity.getId());
        result.setNodeId(itemRow.getDataNode() != null ? itemRow.getDataNode().getId() : 0);
        result.setVersionCode(entity.getVersion().getVersionCode());
        result.setVersionId(entity.getVersion().getId());
        result.setColumn(MDTCatalogColumnModelHelper.toModel(entity.getMdtCatalogColumn(), langId));
        result.setRowNumber(itemRow.getRowNumber());
        result.setDeleted(entity.isDeleted());
        switch (entity.getMdtCatalogColumn().getDataType()) {
            case STRING:
                if (entity.getValue() != null) {
                    result.setValue(entity.getValue().getDescription(langId));
                    result.setNameStrId(entity.getValue().getNameStrId());
                }
                break;
            case NUMBER:
            case INTEGER:
                result.setValue(entity.getNvalue());
                break;
            case DATE:
                if (entity.getNvalue() > 0) {
                    result.setValue(new Date((long) entity.getNvalue()));
                }
                break;
        }
        return result;
    }
}
