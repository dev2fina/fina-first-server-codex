package net.fina.first.ecm.dictionary.model;

import net.fina.ecm.alfresco.api.dictionary.model.ClassPropertyRepresentation;

import java.util.ArrayList;
import java.util.List;

public class ClassPropertyModelHelper {

    public static List<ClassPropertyMetaModel> getMetaModels(List<ClassPropertyRepresentation> classPropertyRepresentations) {
        List<ClassPropertyMetaModel> result = new ArrayList<>();
        if (classPropertyRepresentations != null && !classPropertyRepresentations.isEmpty()) {
            for (ClassPropertyRepresentation classPropertyRepresentation : classPropertyRepresentations) {
                result.add(getMetaModel(classPropertyRepresentation));
            }
        }
        return result;
    }

    public static ClassPropertyMetaModel getMetaModel(ClassPropertyRepresentation classPropertyRepresentation) {
        ClassPropertyMetaModel result = new ClassPropertyMetaModel();
        result.setConstraints(ConstraintModelHelper.getMetaModels(classPropertyRepresentation.getConstraints()));
        result.setDataType(classPropertyRepresentation.getDataType());
        result.setDefaultValues(classPropertyRepresentation.getDefaultValues());
        result.setDescription(classPropertyRepresentation.getDescription());
        result.setEnforced(classPropertyRepresentation.isEnforced());
        result.setIndexed(classPropertyRepresentation.isIndexed());
        result.setIndexedAtomically(classPropertyRepresentation.isIndexedAtomically());
        result.setMandatory(classPropertyRepresentation.isMandatory());
        result.setMultiValued(classPropertyRepresentation.isMultiValued());
        result.setName(classPropertyRepresentation.getName());
        result.setTitle(classPropertyRepresentation.getTitle());
        result.setProtectedValue(classPropertyRepresentation.isProtectedValue());

        return result;
    }

}
