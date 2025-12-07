package net.fina.server.legislative.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.FileSignerException;
import net.fina.common.shared.WrongFileTypeException;
import net.fina.server.legislative.entity.LegislativeDocument;
import net.fina.server.legislative.entity.LegislativeDocumentCategory;

import java.util.List;
import java.util.Map;

public interface LegislativeDocumentLocal {

    List<LegislativeDocument> loadByFiTypeAndCategory(long fiTypeId, long categoryId);

    List<LegislativeDocument> loadByFiTypeAndCategory(long fiTypeId, long categoryId, String fileName);

    List<LegislativeDocument> loadByFiTypeAndCategory(long fiTypeId, long categoryId, String fileName, String sortField, String sortDir);

    LegislativeDocument save(LegislativeDocument document) throws WrongFileTypeException, FileSignerException, FinATypeException;

    List<LegislativeDocument> saveDocuments(List<LegislativeDocument> documents) throws WrongFileTypeException, FileSignerException, FinATypeException;

    void delete(List<Long> ids) throws FinATypeException;

    List<LegislativeDocumentCategory> loadCategories();

    LegislativeDocumentCategory saveCategory(LegislativeDocumentCategory category);

    void deleteCategory(long categoryId) throws FinATypeException;

    LegislativeDocument findById(long id);

    Map<Long, Integer> countCategoryByFilename(String fileName);

    List<LegislativeDocument> loadLatestDocuments(List<Long> fiTypeIds, int total);

    Map<LegislativeDocumentCategory, List<LegislativeDocument>> loadCategoryDocumentMap(String fileName);
}
