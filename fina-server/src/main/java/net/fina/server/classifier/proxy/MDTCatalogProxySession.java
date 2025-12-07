package net.fina.server.classifier.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.server.util.PagingUtil;
import net.fina.common.shared.LanguageSampleModel;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.server.classifier.api.MDTCatalogLocal;
import net.fina.server.classifier.entity.DataType;
import net.fina.server.classifier.entity.MDTCatalog;
import net.fina.server.classifier.entity.MDTCatalogColumn;
import net.fina.server.classifier.model.MDTCatalogColumnMetaModel;
import net.fina.server.classifier.model.MDTCatalogDeleteResultModel;
import net.fina.server.classifier.model.MDTCatalogMetaModel;
import net.fina.server.classifier.model.helper.MDTCatalogColumnModelHelper;
import net.fina.server.classifier.model.helper.MDTCatalogModelHelper;
import net.fina.server.i18n.helper.Description;
import net.fina.server.i18n.proxy.LanguageProxySession;
import net.fina.server.legislative.api.LegislativeDocumentLocal;
import net.fina.server.mdt.entity.MDTNode;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class MDTCatalogProxySession {
    @Inject
    private LegislativeDocumentLocal legislativeDocumentLocal;
    @Inject
    private MDTCatalogLocal mdtCatalogLocal;
    @Inject
    private LanguageProxySession languageProxySession;

    @RolesAllowed({PermissionIdNames.CATALOG_REVIEW, PermissionIdNames.CATALOG_EXPORT})
    public PaginatedListWrapper<MDTCatalogMetaModel> load(int page, int limit, String filterValue, String langCode) {
        int offset = PagingUtil.getOffsetFromPage(page, limit);
        List<MDTCatalog> catalogs = mdtCatalogLocal.load(offset, limit, filterValue);
        List<MDTCatalogMetaModel> modelList = MDTCatalogModelHelper.toModels(catalogs, languageProxySession.getLanguageIdByCode(langCode));

        PaginatedListWrapper<MDTCatalogMetaModel> result = new PaginatedListWrapper<>();
        result.setList(modelList);
        result.setTotalResults(mdtCatalogLocal.count(filterValue));
        result.setCurrentPage(page);

        return result;
    }

    @RolesAllowed({PermissionIdNames.CATALOG_AMEND, PermissionIdNames.CATALOG_IMPORT})
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public MDTCatalogMetaModel getOrSave(MDTCatalogMetaModel model, String langCode) throws FinATypeException {
        MDTCatalog catalog = mdtCatalogLocal.findByCode(model.getCode());
        LanguageSampleModel language = languageProxySession.getLanguageByCode(langCode);

        if (catalog == null) {
            return saveCatalog(model, langCode);
        } else {
            //update catalog column descriptions
            for (int i = 0; i < catalog.getCatalogColumns().size(); i++) {
                MDTCatalogColumn catalogColumn = catalog.getCatalogColumns().get(i);
                MDTCatalogColumnMetaModel importedColumn = model.getCatalogColumns().get(i);
                if (importedColumn != null) {
                    Description description = catalogColumn.getName();
                    Description newDescription = new Description();
                    newDescription.setNameStrId(description.getNameStrId());
                    for (Map.Entry<Long, String> entry : importedColumn.getNames().entrySet()) {
                        newDescription.addDescription(entry.getKey(), entry.getValue());
                    }
                    catalogColumn.setName(newDescription);
                }
            }
        }


        return MDTCatalogModelHelper.toModel(catalog, language.getId());
    }

    @RolesAllowed({PermissionIdNames.CATALOG_AMEND, PermissionIdNames.CATALOG_IMPORT})
    public MDTCatalog getCatalog(String catalogCode) {
        return mdtCatalogLocal.findByCode(catalogCode);
    }

    @RolesAllowed({PermissionIdNames.CATALOG_AMEND, PermissionIdNames.CATALOG_IMPORT})
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public MDTCatalogMetaModel save(MDTCatalogMetaModel model, String langCode) throws FinATypeException {
        MDTCatalog catalog = mdtCatalogLocal.findByCode(model.getCode());
        if (catalog != null && catalog.getId() != model.getId()) {
            throw new FinATypeException("CODE is not unique");
        }

        return saveCatalog(model, langCode);
    }


    private MDTCatalogMetaModel saveCatalog(MDTCatalogMetaModel model, String langCode) throws FinATypeException {
        LanguageSampleModel language = languageProxySession.getLanguageByCode(langCode);
        MDTCatalog catalog = MDTCatalogModelHelper.toEntity(model);
        if (catalog.getLegislativeDocument() != null) {
            catalog.setLegislativeDocument(legislativeDocumentLocal.findById(catalog.getLegislativeDocument().getId()));
        }

        MDTCatalogColumnMetaModel keyColumn = model.getCatalogColumns().stream().filter(MDTCatalogColumnMetaModel::isKey).findFirst().orElse(null);
        if (keyColumn == null) {
            throw new FinATypeException("Key column is not presented.");
        }

        if (!keyColumn.getDataType().equals(DataType.STRING)) {
            throw new FinATypeException("Key Column Data Type is not STRING");
        }

        for (MDTCatalogColumnMetaModel catalogColumnMetaModel : model.getCatalogColumns()) {
            if (catalogColumnMetaModel.getName() == null || catalogColumnMetaModel.getName().trim().isEmpty()) {
                throw new FinATypeException("Column name is not presented.");
            }
        }

        MDTNode dataElementFolderNode = new MDTNode();
        dataElementFolderNode.setCatalog(true);

        List<LanguageSampleModel> activeLanguages = languageProxySession.loadLanguages();
        Description description = new Description();
        description.setNameStrId(model.getNameStrId());
        for (LanguageSampleModel lang : activeLanguages) {
            description.addDescription(lang.getId(), model.getName());
        }

        dataElementFolderNode.setDescription(description);

        dataElementFolderNode.setEquation("");
        dataElementFolderNode.setType(MDTNodeTypes.NODE);
        dataElementFolderNode.setDataType(MDTNodeDataTypes.UNKNOWN);

        catalog.setModifiedAt(new Date());
        catalog.setCatalogNode(dataElementFolderNode);

        catalog.setCatalogColumns(MDTCatalogColumnModelHelper.toEntities(model.getCatalogColumns(), language.getId()));

        catalog = mdtCatalogLocal.create(catalog);


        return MDTCatalogModelHelper.toModel(catalog, language.getId());
    }


    @RolesAllowed(PermissionIdNames.CATALOG_DELETE)
    public MDTCatalogDeleteResultModel delete(long catalogId) {
        return mdtCatalogLocal.delete(catalogId);
    }

    @RolesAllowed({PermissionIdNames.CATALOG_REVIEW, PermissionIdNames.CATALOG_EXPORT})
    public MDTCatalogMetaModel getCatalogById(long catalogId, String langCode) {
        LanguageSampleModel language = languageProxySession.getLanguageByCode(langCode);

        MDTCatalog catalog = mdtCatalogLocal.findById(catalogId);

        return MDTCatalogModelHelper.toModel(catalog, language.getId());
    }

    @RolesAllowed(PermissionIdNames.CATALOG_REVIEW)
    public MDTCatalogMetaModel getCatalogWithAttachmentById(long id, String langCode) {
        MDTCatalog mdtCatalog = mdtCatalogLocal.getCatalogWithAttachmentById(id);
        return MDTCatalogModelHelper.toModel(mdtCatalog, languageProxySession.getLanguageIdByCode(langCode));
    }
}
