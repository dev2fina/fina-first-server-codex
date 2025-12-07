package net.fina.first.ecm.dictionary.api;

import net.fina.first.ecm.dictionary.model.ClassAssociationMetaModel;
import net.fina.first.ecm.dictionary.model.ClassPropertyMetaModel;

import java.util.List;

public interface DictionaryLocal {
    List<ClassPropertyMetaModel> getClassProperties(String acceptLanguage, String className);

    List<ClassAssociationMetaModel> getClassAssociations(String acceptLanguage, String className);
}
