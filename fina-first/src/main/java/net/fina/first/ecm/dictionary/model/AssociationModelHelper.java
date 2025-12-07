package net.fina.first.ecm.dictionary.model;

import net.fina.ecm.alfresco.api.dictionary.model.ClassAssociationRepresentation;

import java.util.ArrayList;
import java.util.List;

public class AssociationModelHelper {
    public static List<ClassAssociationMetaModel> getMetaModels(List<ClassAssociationRepresentation> associationList) {
        List<ClassAssociationMetaModel> res = new ArrayList<>();
        for (ClassAssociationRepresentation representation : associationList) {
            res.add(getMetaModel(representation));
        }

        return res;
    }

    public static ClassAssociationMetaModel getMetaModel(ClassAssociationRepresentation representation) {
        ClassAssociationMetaModel metaModel = new ClassAssociationMetaModel();
        metaModel.setName(representation.getName());
        metaModel.setTitle(representation.getTitle());
        metaModel.setUrl(representation.getUrl());

        if (representation.getSource() != null) {
            AssociationSourceTargetMetaModel sourceMetaModel = new AssociationSourceTargetMetaModel();
            sourceMetaModel.setClassName(representation.getSource().getSourceClass());
            sourceMetaModel.setMandatory(representation.getSource().isMandatory());
            sourceMetaModel.setMany(representation.getSource().isMany());

            metaModel.setSource(sourceMetaModel);
        }

        if (representation.getTarget() != null) {
            AssociationSourceTargetMetaModel targetMetaModel = new AssociationSourceTargetMetaModel();
            targetMetaModel.setClassName(representation.getTarget().getTargetClass());
            targetMetaModel.setMandatory(representation.getTarget().isMandatory());
            targetMetaModel.setMany(representation.getTarget().isMany());

            metaModel.setTarget(targetMetaModel);
        }

        return metaModel;
    }
}
