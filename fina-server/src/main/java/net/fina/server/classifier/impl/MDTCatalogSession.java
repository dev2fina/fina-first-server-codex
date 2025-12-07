package net.fina.server.classifier.impl;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.property.PropertyKeys;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.classifier.api.MDTCatalogColumnLocal;
import net.fina.server.classifier.api.MDTCatalogLocal;
import net.fina.server.classifier.entity.MDTCatalog;
import net.fina.server.classifier.entity.MDTCatalogColumn;
import net.fina.server.classifier.model.MDTCatalogDeleteResultModel;
import net.fina.server.i18n.api.SysStringLocal;
import net.fina.server.i18n.helper.Description;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.legislative.entity.LegislativeDocument;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.mdt.impl.MDTCacheManager;
import net.fina.server.security.api.PropertyLocal;
import net.fina.server.util.DBUtil;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Stateless
@Interceptors(RecordingAuditor.class)
public class MDTCatalogSession implements MDTCatalogLocal {

    private final Logger log = Logger.getLogger(getClass().getName());

    @Inject
    private EntityManager em;

    @Inject
    private MDTNodeLocal mdtNodeLocal;

    @Inject
    private MDTCatalogColumnLocal mdtCatalogColumnLocal;

    @Inject
    private PropertyLocal propertyLocal;

    @Inject
    private SysStringLocal sysStringLocal;

    @EJB
    private MDTCacheManager mdtCacheManager;

    @Override
    public MDTCatalog findById(long id) {
        return em.find(MDTCatalog.class, id);
    }

    @Override
    public MDTCatalog create(MDTCatalog mdtCatalog) throws FinATypeException {

        // validate catalog columns names
        validateCatalogColumns(mdtCatalog.getCatalogColumns());
        if (mdtCatalog.getId() > 0) {
            //merge meta info
            MDTCatalog existed = em.find(MDTCatalog.class, mdtCatalog.getId());
            existed.setModifiedAt(new Date());
            existed.setAbbreviation(mdtCatalog.getAbbreviation());
            existed.setReferenceNumber(mdtCatalog.getReferenceNumber());
            existed.setSource(mdtCatalog.getSource());
            existed.getCatalogNode().setDescription(mdtCatalog.getCatalogNode().getDescription());
            List<MDTCatalogColumn> columns = mdtCatalogColumnLocal.save(orderColumns(mdtCatalog.getCatalogColumns()));
            existed.setCatalogColumns(columns);
            existed.setAncestorCatalogInfo(mdtCatalog.getAncestorCatalogInfo());
            existed.setValidTo(mdtCatalog.getValidTo());
            existed.setLegislativeDocument(mdtCatalog.getLegislativeDocument());

            if ((mdtCatalog.getAttachment() != null && mdtCatalog.getAttachment().length > 0)
                    || (mdtCatalog.getAttachmentName() == null || mdtCatalog.getAttachmentName().trim().isEmpty())) {
                existed.setAttachmentName(mdtCatalog.getAttachmentName());
                existed.setAttachment(mdtCatalog.getAttachment());
            }
        } else {
            //create data element folder node
            String parentNodeCode = propertyLocal.getSystemProperty(PropertyKeys.CATALOG_PARENT_FOLDER_NODE_CODE);
            MDTNode parentNode = mdtNodeLocal.findByCode(parentNodeCode);
            if (parentNode == null) {
                log.error("CATALOG Data Element Folder Code is not configured or does not exist node");
                throw new FinATypeException("CATALOG Data Element Folder Code is not configured or does not exist node");
            }
            long dataElementFolderParentId = parentNode.getId();
            mdtCatalog.getCatalogNode().setParentId(dataElementFolderParentId);

            int counter = 1;
            StringBuilder newCode = new StringBuilder();
            String originalCode = parentNode.getCode();

            do {
                newCode.setLength(0);
                newCode.trimToSize();
                newCode.append(originalCode).append(".").append(counter++);
                mdtCatalog.getCatalogNode().setCode(newCode.toString());
            } while (!mdtNodeLocal.checkCodeUnique(mdtCatalog.getCatalogNode()));

            mdtCatalog.getCatalogNode().setSequence(mdtNodeLocal.getNodeChildMaxSequence(parentNode.getId()) + 1);
            MDTNode catalogNode = mdtNodeLocal.save(mdtCatalog.getCatalogNode());

            mdtCatalog.setCatalogNode(catalogNode);

            em.persist(mdtCatalog);
            //save columns
            List<MDTCatalogColumn> columns = mdtCatalogColumnLocal.create(orderColumns(mdtCatalog.getCatalogColumns()));
            mdtCatalog.setCatalogColumns(columns);
        }

        return mdtCatalog;
    }

    private void validateCatalogColumns(List<MDTCatalogColumn> catalogColumns) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();
        Set<String> uniqueDescriptions = new HashSet<>();

        for (MDTCatalogColumn column : catalogColumns) {
            String description = column.getName().getDescription(langId).toLowerCase();
            if (uniqueDescriptions.contains(description)) {
                throw new FinATypeException("Catalog column names must be unique");
            } else {
                uniqueDescriptions.add(description);
            }
        }

    }

    private List<MDTCatalogColumn> orderColumns(List<MDTCatalogColumn> columns) {
        AtomicInteger index = new AtomicInteger(1);
        return columns.stream()
                .peek(e -> e.setSequence(e.isKey() ? 0 : index.getAndIncrement()))
                .collect(Collectors.toList());
    }

    @Override
    public MDTCatalogDeleteResultModel delete(long id) {
        MDTCatalog catalog = em.find(MDTCatalog.class, id);
        long catalogNodeId = catalog.getCatalogNode().getId();

        //collect catalog node dependant node codes
        List<String> depNodeCodes = loadMdtDependantNodeCodes(catalogNodeId);

        List<String> comparisonNodeCodes = new ArrayList<>();
        List<Long> trashedNodeIds = em.createQuery("select c.dataNode.id from IN_MDT_CATALOG_ITEM_ROWS c where c.catalog.id=:catalogId and c.isDeleted=true ", Long.class)
                .setParameter("catalogId", id).getResultList();

        List<Long> rowItemsNodeIds = em.createQuery("select dataNode.id from IN_MDT_CATALOG_ITEM_ROWS where catalog.id = :id and dataNode.id is not null ", Long.class)
                .setParameter("id", id).getResultList();
        List<Long> rowItemIds = em.createQuery("select  id from IN_MDT_CATALOG_ITEM_ROWS where catalog.id = :id", Long.class)
                .setParameter("id", id).getResultList();

        //generate concatenate in statement to avoid sqlserver exceptions
        String rowIdsConcatenateQuery = DBUtil.get().generateConcatenatedInStatementWithIds("row.id", rowItemIds);

        List<Long> sysStringIds = em.createQuery("select value from IN_MDT_CATALOG_ITEMS  where (" + rowIdsConcatenateQuery + ")", Description.class)
                .getResultList().stream().map(Description::getNameStrId).toList();

        if (!rowItemIds.isEmpty()) {

            em.createQuery("delete from IN_MDT_CATALOG_ITEMS where (" + rowIdsConcatenateQuery + ")").executeUpdate();
            em.createQuery("delete from IN_MDT_CATALOG_ITEM_ROW_VERSION where (" + rowIdsConcatenateQuery + ")").executeUpdate();
        }

        if (!rowItemsNodeIds.isEmpty()) {
            String dataNodeIdsConcatenateQuery = DBUtil.get().generateConcatenatedInStatementWithIds("dataNode.id", rowItemsNodeIds);
            em.createQuery("delete from IN_MDT_CATALOG_ITEM_ROWS where (" + dataNodeIdsConcatenateQuery + ") ").executeUpdate();

            dataNodeIdsConcatenateQuery = dataNodeIdsConcatenateQuery.replaceAll("dataNode", "node");

            //collect node comparisons
            comparisonNodeCodes = loadComparisonCodes(dataNodeIdsConcatenateQuery);

            em.createQuery("delete from IN_MDT_COMPARISON where (" + dataNodeIdsConcatenateQuery + ")").executeUpdate();
            em.createQuery("delete from IN_MDT_NODES as node where (" + dataNodeIdsConcatenateQuery + ") ").executeUpdate();
        }

        if (!trashedNodeIds.isEmpty()) {
            // delete catalog mdt nodes in trash
            em.createQuery("delete from IN_MDT_NODES n where n.id in (:nodeIds)").setParameter("nodeIds", trashedNodeIds).executeUpdate();
        }
        em.createQuery("delete from IN_MDT_CATALOG_ITEM_ROWS  where catalog.id=:id").setParameter("id", id).executeUpdate();
        em.createQuery("delete from IN_MDT_DEPENDENT_NODES where depNode.nodeId=:nodeId").setParameter("nodeId", catalogNodeId).executeUpdate();
        em.createQuery("delete from IN_MDT_CATALOG_COLUMNS cc where cc.catalog.id=:catalogId").setParameter("catalogId", catalog.getId()).executeUpdate();
        em.remove(catalog.getCatalogNode());
        em.remove(catalog);

        //sync nodes cache
        rowItemsNodeIds.forEach(nodeId -> mdtCacheManager.removeCache(nodeId));
        mdtCacheManager.removeCache(catalogNodeId);

        // clear sys_strings
        sysStringLocal.delete(sysStringIds);

        return new MDTCatalogDeleteResultModel(depNodeCodes, comparisonNodeCodes);
    }

    @Override
    public List<MDTCatalog> load(int offset, int limit, String filterValue) {
        String queryString = "select c.id," +//0
                "c.code," + //1
                "c.createdAt," +//2
                "c.modifiedAt," +//3
                "c.validTo," +//4
                "c.abbreviation," +//5
                "c.ancestorCatalogInfo," +//6
                "c.attachmentName," +//7
                "c.source," +//8
                "c.referenceNumber," +//9
                "c.catalogNode.code," +//10
                "c.catalogNode.id," +//11
                "c.catalogNode.description," +//12
                "l.fileName," +//13
                "l.id" +//14
                " from IN_MDT_CATALOG c left outer join c.legislativeDocument l ";

        Query query;
        if (filterValue != null && !filterValue.trim().isEmpty()) {
            long langId = ThreadLocalHolder.getLanguage().getId();

            filterValue = filterValue.trim();
            query = em.createQuery(queryString + "  inner join SYS_STRINGS ss on ss.id=c.catalogNode.description and ss.langId=:langId " +
                    "where c.code like :filterValue or c.abbreviation like :filterValue " +
                    "or c.referenceNumber like :filterValue or c.source like :filterValue or c.attachmentName like :filterValue " +
                    "or c.ancestorCatalogInfo like :filterValue or c.catalogNode.code like :filterValue or " +
                    "c.legislativeDocument.fileName like :filterValue or ss.value like :filterValue " +
                    " order by c.modifiedAt desc", Object[].class);
            query.setParameter("filterValue", "%" + filterValue + "%");
            query.setParameter("langId", langId);
        } else {
            query = em.createQuery(queryString + " order by c.modifiedAt desc", Object[].class);
        }

        if (offset > 0) {
            query.setFirstResult(offset);
        }

        if (limit > 0) {
            query.setMaxResults(limit);
        }

        List<Object[]> objectResults = query.getResultList();
        List<MDTCatalog> result = new ArrayList<>();

        objectResults.forEach(obj -> {
            MDTCatalog catalog = new MDTCatalog();
            catalog.setId(((Number) obj[0]).longValue());
            catalog.setCode((String) obj[1]);
            catalog.setCreatedAt((Date) obj[2]);
            catalog.setModifiedAt((Date) obj[3]);
            catalog.setValidTo((Date) obj[4]);
            catalog.setAbbreviation((String) obj[5]);
            catalog.setAncestorCatalogInfo((String) obj[6]);
            catalog.setAttachmentName((String) obj[7]);
            catalog.setSource((String) obj[8]);
            catalog.setReferenceNumber((String) obj[9]);
            MDTNode catalogNode = new MDTNode();
            catalogNode.setCode((String) obj[10]);
            catalogNode.setId(((Number) (obj[11] != null ? obj[11] : 0)).longValue());
            catalogNode.setDescription((Description) obj[12]);
            catalog.setCatalogNode(catalogNode);
            LegislativeDocument ld = new LegislativeDocument();
            ld.setFileName((String) obj[13]);
            ld.setId(((Number) (obj[14] != null ? obj[14] : 0)).longValue());
            catalog.setLegislativeDocument(ld);

            catalog.setCatalogColumns(em.createQuery("select cc from IN_MDT_CATALOG_COLUMNS cc where cc.catalog.id=:catalogId", MDTCatalogColumn.class)
                    .setParameter("catalogId", catalog.getId()).getResultList());

            result.add(catalog);
        });


        return result;
    }

    @Override
    public long count(String filterValue) {
        if (filterValue != null && !filterValue.trim().isEmpty()) {
            filterValue = filterValue.trim();
            TypedQuery<Long> query = em.createQuery("select count(c.id) from IN_MDT_CATALOG c where " +
                    "c.code like :filterValue or c.abbreviation like :filterValue " +
                    "or c.referenceNumber like :filterValue or c.source like :filterValue " +
                    "or c.attachmentName like :filterValue " +
                    "or c.ancestorCatalogInfo like :filterValue or c.catalogNode.code like :filterValue " +
                    "or c.legislativeDocument.fileName like :filterValue", Long.class);
            query.setParameter("filterValue", "%" + filterValue + "%");
            return query.getSingleResult();
        }
        return em.createQuery("select count(c.id) from IN_MDT_CATALOG c", Long.class).getSingleResult();
    }

    @Override
    public MDTCatalog update(MDTCatalog catalog) {
        return em.merge(catalog);
    }

    @Override
    public MDTCatalog findByCode(String code) {
        try {
            return em.createQuery("select c from IN_MDT_CATALOG c where c.code=:code", MDTCatalog.class)
                    .setParameter("code", code)
                    .getSingleResult();
        } catch (NullPointerException | NoResultException ex) {
            return null;
        }
    }

    @Override
    public MDTCatalog getCatalogWithAttachmentById(long id) {
        return em.find(MDTCatalog.class, id);
    }

    private List<String> loadMdtDependantNodeCodes(Long parentFolderId) {
        return em.createQuery("select code from IN_MDT_NODES where id in (select dn.depNode.dependentNodeId from IN_MDT_DEPENDENT_NODES dn where dn.depNode.nodeId=:parentFolderId)", String.class)
                .setParameter("parentFolderId", parentFolderId).getResultList();

    }

    private List<String> loadComparisonCodes(String nodeIdsConcatenateQuery) {
        return em.createQuery("select node.code from IN_MDT_COMPARISON where (" + nodeIdsConcatenateQuery + ")", String.class).getResultList();
    }
}
