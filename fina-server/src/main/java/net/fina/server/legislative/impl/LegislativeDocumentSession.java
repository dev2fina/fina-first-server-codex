package net.fina.server.legislative.impl;

import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.server.util.FileTypeCheckUtil;
import net.fina.common.shared.FileSignerException;
import net.fina.common.shared.WrongFileTypeException;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.entity.FiType;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.legislative.api.LegislativeDocumentLocal;
import net.fina.server.legislative.entity.LegislativeDocument;
import net.fina.server.legislative.entity.LegislativeDocumentCategory;
import net.fina.server.legislative.event.LegislativeDocumentNotificationEvent;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.crypto.signature.FileSigner;
import net.fina.server.security.crypto.signature.FileSignerFactorySession;
import net.fina.server.security.entity.User;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@Local(LegislativeDocumentLocal.class)
@Interceptors(RecordingAuditor.class)
public class LegislativeDocumentSession implements LegislativeDocumentLocal {

    @Inject
    private EntityManager em;

    @EJB
    private UserLocal current;
    @EJB
    private FiLocal fiLocal;
    @EJB
    private FileSignerFactorySession fileSignerFactorySession;

    @Inject
    private Event<LegislativeDocumentNotificationEvent> notificationEvent;

    @Override
    public List<LegislativeDocument> loadByFiTypeAndCategory(long fiTypeId, long categoryId, String fileName) {
        return em.createQuery("select new net.fina.server.legislative.entity.LegislativeDocument(ld.id,ld.fileName,ld.publish,ld.sign,ld.notify,ld.publisher,ld.fiType,ld.category,ld.contentSize,ld.version,ld.description)  from IN_LAW_DOCUMENT ld where ld.fiType.id=:fiTypeId and ld.category.id=:categoryId and ld.fileName like :fileName", LegislativeDocument.class)
                .setParameter("fiTypeId", fiTypeId)
                .setParameter("categoryId", categoryId)
                .setParameter("fileName", "%" + fileName + "%")
                .getResultList();
    }

    @Override
    public List<LegislativeDocument> loadByFiTypeAndCategory(long fiTypeId, long categoryId) {
        return em.createQuery("select new net.fina.server.legislative.entity.LegislativeDocument(ld.id,ld.fileName,ld.publish,ld.sign,ld.notify,ld.publisher,ld.fiType,ld.category,ld.contentSize,ld.version,ld.description)  from IN_LAW_DOCUMENT ld where ld.fiType.id=:fiTypeId and ld.category.id=:categoryId", LegislativeDocument.class)
                .setParameter("fiTypeId", fiTypeId)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }

    @Override
    public List<LegislativeDocument> loadByFiTypeAndCategory(long fiTypeId, long categoryId, String fileName, String sortField, String sortDir) {
        boolean isFileNameProvided = (fileName != null && !fileName.trim().isEmpty());
        if (sortField != null && !sortField.isEmpty() && sortDir != null && !sortDir.isEmpty()) {
            String query = "select new net.fina.server.legislative.entity.LegislativeDocument(ld.id,ld.fileName,ld.publish,ld.sign,ld.notify,ld.publisher,ld.fiType,ld.category,ld.contentSize,ld.version,ld.description) from IN_LAW_DOCUMENT ld where ld.fiType.id=:fiTypeId and ld.category.id=:categoryId";

            if (isFileNameProvided) {
                query += " and ld.fileName like :fileName";
            }
            query += " order by ld.{0} {1}";

            TypedQuery<LegislativeDocument> typedQuery = em.createQuery(MessageFormat.format(query, sortField, sortDir), LegislativeDocument.class)
                    .setParameter("fiTypeId", fiTypeId)
                    .setParameter("categoryId", categoryId);

            if (isFileNameProvided) {
                typedQuery.setParameter("fileName", "%" + fileName + "%");
            }

            return typedQuery.getResultList();
        }
        return isFileNameProvided ? loadByFiTypeAndCategory(fiTypeId, categoryId, fileName) : loadByFiTypeAndCategory(fiTypeId, categoryId);
    }

    @Override
    public List<LegislativeDocument> saveDocuments(List<LegislativeDocument> legislativeDocuments) throws FinATypeException, FileSignerException {

        List<LegislativeDocument> result = new ArrayList<>();

        if (legislativeDocuments != null && !legislativeDocuments.isEmpty()) {
            for (LegislativeDocument document : legislativeDocuments) {
                result.add(save(document));

            }
        }
        return result;

    }

    @Override
    public LegislativeDocument save(LegislativeDocument document) throws FileSignerException, FinATypeException {

        FiType fiType = em.find(FiType.class, document.getFiType().getId());
        if (fiType == null) {
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, "Invalid Fi Type");
        }
        LegislativeDocumentCategory category = em.find(LegislativeDocumentCategory.class, document.getCategory().getId());
        if (category == null) {
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, "Invalid Category");
        }
        //Check file content
        if (document.getId() <= 0 || (document.getContent() != null && document.getContent().length > 0)) {
            try {
                FileTypeCheckUtil.checkDefault(document.getContent());
            } catch (WrongFileTypeException e) {
                throw new FinATypeException(e.getMessage());
            }

            //Sign File
            if (document.getSign() != null && document.getSign()) {
                try {


                    FileSigner fileSigner = fileSignerFactorySession.createFileSigner();
                    document.setContent(fileSigner.sign(document.getFileName(), document.getContent(), FileTypeCheckUtil.getFileType(document.getContent())));
                } catch (FileSignerException e) {
                    throw new FinATypeException(e.getMessage());
                }
            }
        }


        document.setPublisher(em.find(User.class, current.getCurrentUserId()));
        document.setPublish(new Date());
        document.setFiType(fiType);
        document.setCategory(category);
        document.setContentSize(document.getContent() == null ? 0 : document.getContent().length);

        if (document.getId() > 0) {
            LegislativeDocument tmp = em.find(LegislativeDocument.class, document.getId());
            if (document.getContent() == null || document.getFileName() == null || document.getFileName().isEmpty()) {
                if (tmp.getSign() != null && !tmp.getSign() && document.getSign() != null && document.getSign()) {
                    try {


                        FileSigner fileSigner = fileSignerFactorySession.createFileSigner();
                        document.setContent(fileSigner.sign(tmp.getFileName(), tmp.getContent(), FileTypeCheckUtil.getFileType(tmp.getContent())));
                    } catch (FileSignerException e) {
                        throw new FinATypeException(e.getMessage());
                    }
                } else {
                    document.setContent(tmp.getContent());
                }
                document.setFileName(tmp.getFileName());
            }
            document.setVersion(tmp.getVersion());
            document = em.merge(document);
        } else {
            em.persist(document);

            if (document.getNotify() != null && document.getNotify()) {
                LegislativeDocumentNotificationEvent event = new LegislativeDocumentNotificationEvent(document);
                notificationEvent.fire(event);
            }
        }

        return document;
    }

    @Override
    public void delete(List<Long> ids) throws FinATypeException {
        boolean validParam = ids != null && !ids.isEmpty();
        //check dependency
        if (validParam) {
            List<Long> usedByCatalogIds = em.createQuery("select c.id from IN_MDT_CATALOG c where c.legislativeDocument.id in (:ids)", Long.class)
                    .setParameter("ids", ids)
                    .getResultList();
            if (!usedByCatalogIds.isEmpty()) {
                throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
            }

            em.createQuery("delete from IN_LAW_DOCUMENT where id in (:ids)")
                    .setParameter("ids", ids)
                    .executeUpdate();
        }
    }

    @Override
    public List<LegislativeDocumentCategory> loadCategories() {
        return em.createQuery("select ldc from IN_LAW_DOC_CATEGORY ldc", LegislativeDocumentCategory.class).getResultList();
    }

    @Override
    public LegislativeDocumentCategory saveCategory(LegislativeDocumentCategory category) {
        if (category.getId() > 0) {
            category = em.merge(category);
        } else {
            em.persist(category);
        }
        return category;
    }

    @Override
    public void deleteCategory(long categoryId) throws FinATypeException {
        List<Long> usedCategories = em.createQuery("select ld.id from IN_LAW_DOCUMENT ld where ld.category.id=:categoryId", Long.class)
                .setParameter("categoryId", categoryId)
                .getResultList();

        if (!usedCategories.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }

        LegislativeDocumentCategory ldc = em.find(LegislativeDocumentCategory.class, categoryId);
        em.remove(ldc);
    }

    @Override
    public LegislativeDocument findById(long id) {
        return em.find(LegislativeDocument.class, id);
    }

    @Override
    public Map<Long, Integer> countCategoryByFilename(String fileName) {
        Map<Long, Integer> result = em.createQuery("select ld.category.id as categoryId,count(ld.category.id) as categoryCount from IN_LAW_DOCUMENT ld where ld.fileName like :fileName and ld.fiType.id in (:fiTypeIds) group by ld.category.id", Tuple.class)
                .setParameter("fileName", "%" + fileName + "%")
                .setParameter("fiTypeIds", fiLocal.loadUserFiTypeIds())
                .getResultStream().collect(
                        Collectors.toMap(
                                tuple -> ((Number) tuple.get(0)).longValue(),
                                tuple -> ((Number) tuple.get(1)).intValue()
                        )
                );
        return result;
    }

    @Override
    public List<LegislativeDocument> loadLatestDocuments(List<Long> fiTypeIds, int total) {
        return em.createQuery("select new net.fina.server.legislative.entity.LegislativeDocument(d.id,d.fileName,d.publish,d.sign,d.notify,d.publisher,d.fiType,d.category,d.contentSize,d.version,d.description) from IN_LAW_DOCUMENT d where d.fiType.id in(:fiTypeIds) order by d.publish desc ", LegislativeDocument.class)
                .setParameter("fiTypeIds", fiTypeIds)
                .setFirstResult(0)
                .setMaxResults(total)
                .getResultList();
    }

    @Override
    public Map<LegislativeDocumentCategory, List<LegislativeDocument>> loadCategoryDocumentMap(String fileName) {
        List<LegislativeDocument> documents = em.createQuery("select d from IN_LAW_DOCUMENT d where d.fileName like :fileName", LegislativeDocument.class)
                .setParameter("fileName", "%" + fileName + "%")
                .getResultList();

        Map<LegislativeDocumentCategory, List<LegislativeDocument>> result = new HashMap<>();

        for (LegislativeDocument document : documents) {
            LegislativeDocumentCategory category = document.getCategory();
            result.putIfAbsent(category, new ArrayList<>());

            result.get(category).add(document);
        }

        return result;
    }
}
