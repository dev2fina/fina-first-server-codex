package net.fina.server.classifier.model.helper;

import net.fina.server.classifier.entity.MDTCatalog;
import net.fina.server.classifier.entity.MDTCatalogColumn;
import net.fina.server.classifier.model.MDTCatalogMetaModel;
import net.fina.server.legislative.entity.LegislativeDocument;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MDTCatalogModelHelper {

    public static MDTCatalog toEntity(MDTCatalogMetaModel model) {
        MDTCatalog result = new MDTCatalog();
        result.setId(model.getId());
        result.setSource(model.getSource());
        result.setCreatedAt(new Date());
        result.setModifiedAt(model.getModifiedAt() == null ? model.getCreatedAt() : new Date());
        result.setAbbreviation(model.getAbbreviation());
        result.setReferenceNumber(model.getReferenceNumber());
        result.setCode(model.getCode());

        if (model.getLegislativeDocumentId() > 0) {
            LegislativeDocument legislativeDocument = new LegislativeDocument();
            legislativeDocument.setId(model.getLegislativeDocumentId());
            result.setLegislativeDocument(legislativeDocument);
        }

        result.setAttachmentName(model.getAttachmentName());
        result.setAttachment(model.getAttachment());
        result.setAncestorCatalogInfo(model.getAncestorCatalogInfo());
        result.setValidTo(model.getValidTo());

        return result;
    }

    public static MDTCatalog toEntity(MDTCatalogMetaModel model, MDTCatalog entity) {

        entity.setSource(model.getSource());
        entity.setCreatedAt(new Date());
        entity.setModifiedAt(model.getModifiedAt() == null ? model.getCreatedAt() : new Date());
        entity.setAbbreviation(model.getAbbreviation());
        entity.setReferenceNumber(model.getReferenceNumber());
        entity.setCode(model.getCode());

        if (model.getLegislativeDocumentId() > 0) {
            LegislativeDocument legislativeDocument = new LegislativeDocument();
            legislativeDocument.setId(model.getLegislativeDocumentId());
            entity.setLegislativeDocument(legislativeDocument);
        }

        entity.setAttachmentName(model.getAttachmentName());
        entity.setAttachment(model.getAttachment());
        entity.setAncestorCatalogInfo(model.getAncestorCatalogInfo());
        entity.setValidTo(model.getValidTo());

        return entity;
    }

    public static MDTCatalogMetaModel toModel(MDTCatalog entity, long langId) {
        MDTCatalogMetaModel result = new MDTCatalogMetaModel();
        result.setId(entity.getId());
        List<MDTCatalogColumn> catalogColumns = entity.getCatalogColumns();
        catalogColumns.sort(Comparator.comparingInt(MDTCatalogColumn::getSequence));
        result.setCatalogColumns(MDTCatalogColumnModelHelper.toModels(catalogColumns, langId));
        result.setAbbreviation(entity.getAbbreviation());
        result.setReferenceNumber(entity.getReferenceNumber());
        result.setModifiedAt(entity.getModifiedAt());
        result.setSource(entity.getSource());
        result.setName(entity.getCatalogNode().getDescription().getDescription(langId));
        result.setNameStrId(entity.getCatalogNode().getDescription().getNameStrId());
        result.setCreatedAt(entity.getCreatedAt());
        result.setMdtCode(entity.getCatalogNode().getCode());
        result.setCode(entity.getCode());

        LegislativeDocument ld = entity.getLegislativeDocument();
        if (ld != null) {
            result.setLegislativeDocumentId(ld.getId());
            result.setLegislativeDocumentName(ld.getFileName());
        }

        result.setAttachmentName(entity.getAttachmentName());
        result.setAttachment(entity.getAttachment());
        result.setAncestorCatalogInfo(entity.getAncestorCatalogInfo());
        result.setValidTo(entity.getValidTo());

        return result;
    }

    public static List<MDTCatalogMetaModel> toModels(List<MDTCatalog> entities, long langId) {
        List<MDTCatalogMetaModel> result = new ArrayList<>();
        entities.forEach(e -> result.add(toModel(e, langId)));
        return result;
    }
}
