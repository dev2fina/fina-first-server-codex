package net.fina.server.legislative.model;

import net.fina.server.ThreadLocalHolder;
import net.fina.server.legislative.entity.LegislativeDocument;

import java.util.ArrayList;
import java.util.List;

public class LegislativeDocumentModelHelper {

    public static LegislativeDocumentMetaModel toModel(LegislativeDocument document) {
        LegislativeDocumentMetaModel model = new LegislativeDocumentMetaModel();
        long langId = ThreadLocalHolder.getLanguage().getId();

        model.setId(document.getId());
        model.setContent(null);
        model.setDescription(document.getDescription().getDescription(langId));
        model.setNotify(document.getNotify());
        model.setSign(document.getSign());
        model.setPublish(document.getPublish());
        model.setPublisher(document.getPublisher().getDescription().getDescription(langId));
        model.setFileName(document.getFileName());
        model.setBytes(document.getContentSize());

        return model;
    }

    public static List<LegislativeDocumentMetaModel> toModels(List<LegislativeDocument> documents) {
        List<LegislativeDocumentMetaModel> result = new ArrayList<>();

        if (documents != null && !documents.isEmpty()) {
            for (LegislativeDocument document : documents) {
                result.add(toModel(document));
            }
        }

        return result;
    }
}
