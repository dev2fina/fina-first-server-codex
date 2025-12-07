package net.fina.first.ecm.dictionary.model;

import net.fina.ecm.alfresco.api.dictionary.model.ConstraintRepresentation;

import java.util.ArrayList;
import java.util.List;

public class ConstraintModelHelper {

    public static List<ConstraintMetaModel> getMetaModels(List<ConstraintRepresentation> representations) {
        List<ConstraintMetaModel> result = new ArrayList<>();
        if (representations != null && !representations.isEmpty()) {
            for (ConstraintRepresentation representation : representations) {
                result.add(getMetaModel(representation));
            }
        }
        return result;
    }

    public static ConstraintMetaModel getMetaModel(ConstraintRepresentation representation) {
        ConstraintMetaModel metaModel = new ConstraintMetaModel();
        metaModel.setType(representation.getType());
        metaModel.setParameters(representation.getParameters());

        return metaModel;
    }

}
