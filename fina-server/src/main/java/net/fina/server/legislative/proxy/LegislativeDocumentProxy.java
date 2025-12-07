package net.fina.server.legislative.proxy;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.FileSignerException;
import net.fina.common.shared.LanguageSampleModel;
import net.fina.common.shared.WrongFileTypeException;
import net.fina.messages.MessagesUtil;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.entity.Language;
import net.fina.server.i18n.helper.Description;
import net.fina.server.legislative.api.LegislativeDocumentLocal;
import net.fina.server.legislative.entity.LegislativeDocument;
import net.fina.server.legislative.entity.LegislativeDocumentCategory;
import net.fina.server.legislative.model.LegislativeCategoryMetaModel;
import net.fina.server.legislative.model.LegislativeDocumentMetaModel;
import net.fina.server.legislative.model.LegislativeDocumentModelHelper;
import org.jboss.ejb3.annotation.SecurityDomain;

import java.util.*;
import java.util.stream.Collectors;

@Stateless
@SecurityDomain("FinASecurityDomain")
@RolesAllowed(PermissionIdNames.LEGISLATIVE_DOCUMENT_REVIEW)
public class LegislativeDocumentProxy {

    @EJB
    private LegislativeDocumentLocal legislativeDocumentLocal;

    @EJB
    private FiLocal fiLocal;


    @Inject
    private LanguageLocal languageLocal;


    public List<LegislativeDocumentCategory> loadCategories() {
        return legislativeDocumentLocal.loadCategories();
    }

    public List<LegislativeCategoryMetaModel> loadCategories(String langCode, String fileName) {
        Language language = languageLocal.getLanguageByCode(langCode);
        List<LegislativeCategoryMetaModel> result = new ArrayList<>();

        Map<Long, Integer> countCategoryByFilename = new HashMap<>();
        if (fileName != null && !fileName.trim().isEmpty()) {
            countCategoryByFilename.putAll(legislativeDocumentLocal.countCategoryByFilename(fileName));
        }

        loadCategories().forEach(category -> {
            long categoryId = category.getId();
            LegislativeCategoryMetaModel model = new LegislativeCategoryMetaModel(
                    categoryId,
                    category.getName().getDescription(language.getId()),
                    category.getName().getNameStrId(),
                    countCategoryByFilename.get(categoryId),
                    category.getVersion());
            result.add(model);
        });

        return result;
    }

    public List<LegislativeDocumentMetaModel> loadByCategory(long categoryId) {
        return loadByCategory(categoryId, null, null, null, null);
    }

    public List<LegislativeDocumentMetaModel> loadByCategory(long categoryId, String langCode, String fileName, String sortField, String sortDir) {
        List<Long> fiTypeIds = fiLocal.loadUserFiTypeIds();
        List<LegislativeDocumentMetaModel> models = new ArrayList<>();

        Language language = langCode == null ? null : languageLocal.getLanguageByCode(langCode);

        for (Long fiTypeId : fiTypeIds) {
            for (LegislativeDocument document : legislativeDocumentLocal.loadByFiTypeAndCategory(fiTypeId, categoryId, fileName, sortField, sortDir)) {
                LegislativeDocumentMetaModel model = new LegislativeDocumentMetaModel();
                model.setId(document.getId());
                model.setFileName(document.getFileName());
                model.setPublish(document.getPublish());
                model.setBytes(document.getContentSize());
                if (language != null) {
                    model.setDescription(document.getDescription().getDescription(language.getId()));
                }
                models.add(model);
            }
        }

        return models;
    }

    public LegislativeDocumentMetaModel loadById(long id) {
        List<Long> userFiTypeIds = fiLocal.loadUserFiTypeIds();
        LegislativeDocument document = legislativeDocumentLocal.findById(id);

        LegislativeDocumentMetaModel model = new LegislativeDocumentMetaModel();
        if (document != null && userFiTypeIds.contains(document.getFiType().getId())) {
            model.setId(document.getId());
            model.setFileName(document.getFileName());
            model.setContent(document.getContent());
            model.setPublish(document.getPublish());
            model.setBytes(document.getContent() == null ? 0 : document.getContent().length);
        }
        return model;
    }

    public List<LegislativeDocumentMetaModel> getDocumentsWithContent(List<Long> fileIds) {
        List<LegislativeDocumentMetaModel> result = new ArrayList<>();

        fileIds.forEach(fileId -> {
            LegislativeDocumentMetaModel doc = loadById(fileId);
            if (doc != null && doc.getId() > 0) {
                result.add(doc);
            }
        });

        return result;
    }

    public List<LegislativeDocumentMetaModel> loadLatestDocuments(int total, String langCode) {
        List<LegislativeDocumentMetaModel> result = new ArrayList<>();

        Language language = languageLocal.getLanguageByCode(langCode);
        List<Long> fiTypeIds = fiLocal.loadUserFiTypeIds();

        for (LegislativeDocument document : legislativeDocumentLocal.loadLatestDocuments(fiTypeIds, total)) {
            LegislativeDocumentMetaModel model = new LegislativeDocumentMetaModel();
            model.setId(document.getId());
            model.setFileName(document.getFileName());
            model.setPublish(document.getPublish());
            model.setBytes(document.getContentSize());
            if (language != null) {
                model.setDescription(document.getDescription().getDescription(language.getId()));
            }
            result.add(model);
        }
        return result;
    }

    @RolesAllowed(PermissionIdNames.LEGISLATIVE_DOCUMENT_AMEND)
    public List<LegislativeCategoryMetaModel> saveCategories(List<LegislativeCategoryMetaModel> categories) throws FinATypeException {
        List<LegislativeCategoryMetaModel> result = new ArrayList<>();
        for (LegislativeCategoryMetaModel c : categories) {
            result.add(saveCategory(c));
        }

        return result;
    }

    public LegislativeCategoryMetaModel saveCategory(LegislativeCategoryMetaModel model) throws FinATypeException {
        String categoryName = model.getName();
        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();
        if (categoryName == null || categoryName.trim().isEmpty() ||
                !isCategoryNameUnique(categoryName.trim(), model.getId(), lang.getId())) {
            throw new FinATypeException(MessagesUtil.getString("net.fina.legislativeBasis.category.invalidDescriptionError", lang.getCode()));
        }

        LegislativeDocumentCategory category = new LegislativeDocumentCategory();
        category.setId(model.getId());
        category.setVersion(model.getVersion());
        category.setName(new Description(lang.getId(), model.getNameStrId(), categoryName.trim()));

        LegislativeDocumentCategory saved = legislativeDocumentLocal.saveCategory(category);

        return new LegislativeCategoryMetaModel(
                saved.getId(),
                saved.getName().getDescription(lang.getId()),
                saved.getName().getNameStrId(),
                0,
                saved.getVersion()
        );
    }

    private boolean isCategoryNameUnique(String categoryName, long categoryId, long langId) {
        List<LegislativeDocumentCategory> categoryList = legislativeDocumentLocal.loadCategories();
        for (LegislativeDocumentCategory category : categoryList) {
            String existingCategoryName = category.getName().getDescription(langId);
            if (existingCategoryName != null && categoryName.equals(existingCategoryName.trim()) && categoryId != category.getId()) {
                return false;
            }
        }

        return true;
    }

    @RolesAllowed(PermissionIdNames.LEGISLATIVE_DOCUMENT_DELETE)
    public void deleteCategory(long categoryId) throws FinATypeException {
        legislativeDocumentLocal.deleteCategory(categoryId);
    }

    @RolesAllowed(PermissionIdNames.LEGISLATIVE_DOCUMENT_DELETE)
    public void deleteDocument(long documentId) throws FinATypeException {
        legislativeDocumentLocal.delete(Collections.singletonList(documentId));
    }

    public List<LegislativeDocumentMetaModel> loadByFiTypeAndCategory(long fiTypeId, long categoryId) {
        long langId = ThreadLocalHolder.getLanguage().getId();

        List<LegislativeDocumentMetaModel> resultList = new ArrayList<>();

        for (LegislativeDocument document : legislativeDocumentLocal.loadByFiTypeAndCategory(fiTypeId, categoryId)) {
            LegislativeDocumentMetaModel model = new LegislativeDocumentMetaModel();
            model.setId(document.getId());
            model.setFileName(document.getFileName());
            model.setPublish(document.getPublish());
            model.setBytes(document.getContentSize());
            model.setDescription(document.getDescription().getDescription(langId));
            model.setSign(document.getSign());
            model.setNotify(document.getNotify());
            model.setPublisher(document.getPublisher().getDescription().getDescription(langId));
            resultList.add(model);
        }

        return resultList;
    }

    @RolesAllowed(PermissionIdNames.LEGISLATIVE_DOCUMENT_AMEND)
    public List<LegislativeDocumentMetaModel> saveDocument(List<LegislativeDocument> legislativeDocuments) throws FinATypeException, FileSignerException, WrongFileTypeException {
        List<LegislativeDocument> documents = legislativeDocumentLocal.saveDocuments(legislativeDocuments);
        return LegislativeDocumentModelHelper.toModels(documents);
    }

    @RolesAllowed(PermissionIdNames.LEGISLATIVE_DOCUMENT_REVIEW)
    public List<LegislativeCategoryMetaModel> searchDocuments(String fileName) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        Map<LegislativeDocumentCategory, List<LegislativeDocument>> filtered = legislativeDocumentLocal.loadCategoryDocumentMap(fileName);
        List<LegislativeCategoryMetaModel> result = new ArrayList<>();

        for (Map.Entry<LegislativeDocumentCategory, List<LegislativeDocument>> entry : filtered.entrySet()) {
            LegislativeDocumentCategory category = entry.getKey();
            LegislativeCategoryMetaModel model = new LegislativeCategoryMetaModel(
                    category.getId(),
                    category.getName().getDescription(langId),
                    category.getName().getNameStrId(),
                    0,
                    category.getVersion()
            );
            model.setDocuments(entry.getValue().stream().map(LegislativeDocumentModelHelper::toModel).collect(Collectors.toList()));
            result.add(model);
        }

        return result;
    }
}
