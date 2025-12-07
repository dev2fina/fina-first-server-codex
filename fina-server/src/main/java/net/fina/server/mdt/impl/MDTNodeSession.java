package net.fina.server.mdt.impl;

import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.xml.bind.*;
import net.fina.auditlog.event.AuditLogAddManualRecordIdsEvent;
import net.fina.common.client.constants.OperationType;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.MDTComparisonFilter;
import net.fina.common.client.filter.MdtFilter;
import net.fina.common.client.mdt.*;
import net.fina.common.server.util.CommonUtil;
import net.fina.common.shared.SortField;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.api.SysStringLocal;
import net.fina.server.i18n.entity.Language;
import net.fina.server.i18n.helper.Description;
import net.fina.server.interceptors.LogDescription;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.*;
import net.fina.server.mdt.util.MDTNodeUtil;
import net.fina.server.mdt.xml.translation.TranslationNode;
import net.fina.server.mdt.xml.translation.TranslationNodes;
import net.fina.server.mdt.xml.v2.Comparison;
import net.fina.server.mdt.xml.v2.Node;
import net.fina.server.returns.entity.ReturnDefinition;
import net.fina.server.security.api.PropertyLocal;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.Role;
import net.fina.server.security.product.Product;
import net.fina.server.util.SortUtil;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Stateless
@Interceptors(RecordingAuditor.class)
@SecurityDomain("FinASecurityDomain")
@SuppressWarnings("JpaQlInspection")
public class MDTNodeSession implements MDTNodeLocal {

    private Logger log = Logger.getLogger(getClass());

    @Inject
    private EntityManager em;
    @EJB
    private MDTCacheManager mdtCacheManager;
    @EJB
    private LanguageLocal languageSession;
    @EJB
    private PropertyLocal propertySession;
    @EJB
    private SysStringLocal sysStringLocal;
    @EJB
    private UserLocal userLocal;

    @Resource
    private SessionContext sessionContext;

    @Inject
    private Event<AuditLogAddManualRecordIdsEvent> auditLogAddManualRecordIdsEvent;

    @Override
    public List<MDTNode> loadNodes(Map<MdtFilter, Object> filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MDTNode> query = cb.createQuery(MDTNode.class);
        Root<MDTNode> mdtNode = query.from(MDTNode.class);
        mdtNode.fetch(MDTNode_.comparisons, JoinType.LEFT);

        query.orderBy(cb.asc(mdtNode.get(MDTNode_.sequence)));
        query.distinct(true);
        List<Predicate> predicates = getFilterPredicates(cb, mdtNode, filter);
        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }
        int offset = (int) filter.getOrDefault(MdtFilter.OFFSET, -1);
        int limit = (int) filter.getOrDefault(MdtFilter.LIMIT, -1);

        Query typedQuery = em.createQuery(query);
        if (limit > 0 && offset > 0) {
            typedQuery.setFirstResult(offset);
            typedQuery.setMaxResults(limit);
        }
        return typedQuery.getResultList();
    }

    @Override
    public long count(Map<MdtFilter, Object> filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<MDTNode> mdtNode = query.from(MDTNode.class);

        query.select(cb.count(mdtNode.get(MDTNode_.id)));

        List<Predicate> predicates = getFilterPredicates(cb, mdtNode, filter);
        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }

        return em.createQuery(query).getSingleResult();
    }

    private void loadChildrenRecursive(MDTNode node) {
        if (node.getType() == MDTNodeTypes.NODE) {
            List<MDTNode> children = loadMdtNodesByParentId(node.getId());
            Collections.sort(children, new Comparator<MDTNode>() {
                @Override
                public int compare(MDTNode o1, MDTNode o2) {
                    if (o1 != null && o2 != null) {
                        return Long.compare(o1.getSequence(), o2.getSequence());
                    }
                    return 0;
                }
            });
            for (MDTNode child : children) {
                loadChildrenRecursive(child);
            }
            node.setChildren(children);
        }
    }

    private void loadChildrenRecursive(MDTNode node, List<MDTNode> nodes) {
        if (node.getType() == MDTNodeTypes.NODE) {
            List<MDTNode> children = loadMdtNodesByParentId(node.getId());
            for (MDTNode child : children) {
                loadChildrenRecursive(child, nodes);
            }
            nodes.addAll(children);
        }
    }

    @Override
    public byte[] exportMdtSubtreeXML(long nodeId) {
        MDTNode mdtNode = mdtCacheManager.getMdtNodesById().get(nodeId);

        Map<Long, List<MDTComparison>> comparisonsMap = new HashMap<>();
        List<MDTComparison> comparisons = loadComparisons(new HashMap<MDTComparisonFilter, Object>(), null);
        for (MDTComparison comparison : comparisons) {
            long nid = comparison.getNode().getId();
            comparisonsMap.computeIfAbsent(nid, k -> new ArrayList<>());
            comparisonsMap.get(nid).add(comparison);
        }
        comparisons = null;

        loadChildrenRecursive(mdtNode);

        List<Long> languageIds = languageSession.getLanguageIds();
        Map<Long, String> languagesMap = new HashMap<>();
        for (Long id : languageIds) {
            languagesMap.put(id, languageSession.getLanguageCodeById(id).trim());
        }

        Node xmlNode = new Node(mdtNode, languagesMap, comparisonsMap);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(Node.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(xmlNode, outputStream);
        } catch (JAXBException e) {
            //TODO: ???
            throw new RuntimeException(e);
        }
        return outputStream.toByteArray();
    }

    private void storeChildrenRecursive(Node node, long parentId, Map<String, Long> languageCodeIdMap, long defaultLanguageId) {
        MDTNode mdtNode = node.asMDTNode(languageCodeIdMap, defaultLanguageId, new HashMap<Long, List<MDTComparison>>());
        mdtNode.setParentId(parentId);
        try {
            save(mdtNode, true);
            for (Node child : node.getChildren()) {
                storeChildrenRecursive(child, mdtNode.getId(), languageCodeIdMap, defaultLanguageId);
            }
        } catch (FinATypeException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void getCodesFromSubtree(Node node, Set<String> codes) throws FinATypeException {
        String code = node.getCode().trim();
        if (codes.contains(code)) {
            throw new FinATypeException(FinATypeException.Type.NODE_EXISTS_ERROR);
        }
        codes.add(code);
        for (Node child : node.getChildren()) {
            getCodesFromSubtree(child, codes);
        }
    }

    private String replaceCodes(String oldString, Pattern pattern, Map<String, String> codesToUpdate) {
        String newCode, result = oldString == null ? "" : oldString;
        Set<String> codes = MDTNodeUtil.extractCodes(result, pattern);
        for (String code : codes) {
            newCode = codesToUpdate.get(code);
            if (newCode != null) {
                result = result.replace(code, newCode);
            }
        }
        return result;
    }

    private void fillDependedNodes(Node node, Pattern pattern, Map<String, Set<String>> dependentNodes) {
        HashSet<String> usedNodes = new HashSet<>();
        switch (node.getType()) {
            case VARIABLE: {
                usedNodes.addAll(MDTNodeUtil.extractCodes(node.getEquation(), pattern));
                break;
            }
            case NODE: {
                for (Node child : node.getChildren()) {
                    fillDependedNodes(child, pattern, dependentNodes);
                }
                break;
            }
        }
        for (Comparison comparison : node.getComparisons()) {
            usedNodes.addAll(MDTNodeUtil.extractCodes(comparison.getRightEquation(), pattern));
        }
        dependentNodes.put(node.getCode(), usedNodes);
    }

    private void fillComparisons(Node node, Map<String, Collection<Comparison>> comparisonMap) {
        Collection<Comparison> comparisons = node.getComparisons();
        if (node.getType() == MDTNodeTypes.NODE) {
            for (Node child : node.getChildren()) {
                fillComparisons(child, comparisonMap);
            }
        }
        comparisonMap.put(node.getCode().trim(), comparisons);
    }

    private void storeNodeProperties(Map<String, Set<String>> dependentNodes, Map<String, Collection<Comparison>> comparisonMap, MDTImportResult result) throws FinATypeException {
        List<MDTDependentNode> dependentNodeList = new ArrayList<>();
        Map<String, MDTNode> codeNodeMap = new HashMap<>();
        for (MDTNode node : mdtCacheManager.getMdtNodes()) {
            codeNodeMap.put(node.getCode().trim(), node);
        }
        for (Map.Entry<String, Set<String>> entry : dependentNodes.entrySet()) {
            MDTNode dependentNode = codeNodeMap.get(entry.getKey().trim());
            for (String remoteNodeCode : entry.getValue()) {
                MDTNode remoteNode = codeNodeMap.get(remoteNodeCode.trim());
                if (remoteNode == null) {
                    result.getCodesNotFound().add(remoteNodeCode);
                    result.getImportedCodesToFix().add(entry.getKey());
                } else {
                    MDTDependentNode depNode = new MDTDependentNode();
                    MDTDependentNodePK depNodePK = new MDTDependentNodePK();
                    depNodePK.setNodeId(remoteNode.getId());
                    depNodePK.setDependentNodeId(dependentNode.getId());
                    depNode.setDepNode(depNodePK);
                    MDTDependentNode checkedNode = checkCircularDependencies(remoteNode.getId(), dependentNode.getId());
                    if (checkedNode != null) {
                        MDTNode curNode = em.find(MDTNode.class, checkedNode.getDepNode().getNodeId());
                        MDTNode dNode = em.find(MDTNode.class, checkedNode.getDepNode().getDependentNodeId());
                        throw new FinATypeException("Circular Dependencies Is Not Allowed! Node with code=" + dNode.getCode() + " already has dependencies on node with code=" + curNode.getCode());
                    }
                    dependentNodeList.add(depNode);
//                    em.persist(depNode);
                }
            }
        }
        for (MDTDependentNode dn : dependentNodeList) {
            em.persist(dn);
        }
        for (Map.Entry<String, Collection<Comparison>> entry : comparisonMap.entrySet()) {
            MDTNode node = codeNodeMap.get(entry.getKey());
            for (Comparison comparison : entry.getValue()) {
                MDTComparison mdtComparison = comparison.asMDTComparison();
                mdtComparison.setNode(em.find(MDTNode.class, node.getId()));
                em.persist(mdtComparison);
            }
        }
    }

    private MDTDependentNode checkCircularDependencies(long nodeId, long depNodeId) {

        List<MDTDependentNode> dependentNodeList = em.createQuery("select n from IN_MDT_DEPENDENT_NODES  n where n.depNode.nodeId=:nodeId and n.depNode.dependentNodeId=:depNodeId").setParameter("nodeId", depNodeId).setParameter("depNodeId", nodeId).getResultList();

        return dependentNodeList.isEmpty() ? null : dependentNodeList.get(0);
    }

    private void prepareForImport(Node node, Pattern pattern, Map<String, String> codesToUpdate) {
        String newCode = codesToUpdate.get(node.getCode());
        if (newCode != null) {
            node.setCode(newCode);
        }

        node.setEquation(replaceCodes(node.getEquation(), pattern, codesToUpdate));

        for (Comparison c : node.getComparisons()) {
            c.setRightEquation(replaceCodes(c.getRightEquation(), pattern, codesToUpdate));
        }

        for (Node child : node.getChildren()) {
            prepareForImport(child, pattern, codesToUpdate);
        }
    }

    @Override
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public MDTImportResult importMDTSubtree(long nodeId, byte[] bytes) {
        MDTImportResult result = new MDTImportResult();
        InputStream inputStream = new ByteArrayInputStream(bytes);
        Set<String> newCodes = new HashSet<String>();
        Map<String, String> codesToUpdate = new HashMap<String, String>();
        try {
            Product.getInstance().check();

            Node unmarshalledNode = null;
            JAXBContext context = null;
            try {
                context = JAXBContext.newInstance(Node.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                unmarshalledNode = (Node) unmarshaller.unmarshal(inputStream);
            } catch (JAXBException e) {
                //TODO: ???
                throw new FinATypeException(e, FinATypeException.Type.GENERAL_ERROR);
            }

            getCodesFromSubtree(unmarshalledNode, newCodes);
            Set<String> importedNodeCOdes = new HashSet<>(newCodes);
            //get existing codes
            List<String> existingCodes = new ArrayList<>(mdtCacheManager.getMdtNodes().size());
            for (MDTNode node : mdtCacheManager.getMdtNodes()) {
                existingCodes.add(node.getCode().trim());
            }

            //extracting dependencies
            Pattern mdtCodePattern = Pattern.compile(MDTNodeUtil.MDT_CODE_REGEX);

            if (existingCodes.retainAll(newCodes)) {
                for (String code : existingCodes) {
                    //What to append to duplicate nodes?
                    String fixedCode = code + "_" + UUID.randomUUID().toString().replaceAll("-", "_");
                    codesToUpdate.put(code, fixedCode);
                    result.getImportedCodesToFix().add(fixedCode);
                    importedNodeCOdes.remove(code);
                    importedNodeCOdes.add(fixedCode);
                }
                result.setRenamedNodes(codesToUpdate);
                result.setImportedNodeCodes(importedNodeCOdes);
                prepareForImport(unmarshalledNode, mdtCodePattern, codesToUpdate);
            }

            Map<String, Set<String>> dependedNodes = new HashMap<>();
            fillDependedNodes(unmarshalledNode, mdtCodePattern, dependedNodes);


            //preparing comparisons
            Map<String, Collection<Comparison>> comparisonsMap = new HashMap<>();
            fillComparisons(unmarshalledNode, comparisonsMap);

            //preparing languages
            Map<String, Long> languageCodeIdMap = new HashMap<String, Long>();
            String defaultLanguageCode = propertySession.getSystemProperty("fina2.default.language.id");
            long defaultLanguageId = 1;
            if (defaultLanguageCode != null) {
                defaultLanguageCode = defaultLanguageCode.trim();
            }

            List<Language> languages = languageSession.loadLanguages();
            for (Language language : languages) {
                languageCodeIdMap.put(language.getCode().trim(), language.getId());
                if (language.getCode().trim().equalsIgnoreCase(defaultLanguageCode.trim())) {
                    defaultLanguageId = language.getId();
                }
            }

            //storing everything
            em.setFlushMode(FlushModeType.COMMIT);

            storeChildrenRecursive(unmarshalledNode, nodeId, languageCodeIdMap, defaultLanguageId);

            em.flush();
            em.clear();

            em.setFlushMode(FlushModeType.COMMIT);

            storeNodeProperties(dependedNodes, comparisonsMap, result);

            em.flush();
            em.clear();

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            Set<String> codesToRemoveFromCache = new HashSet<>(newCodes);
            for (Map.Entry<String, String> entry : codesToUpdate.entrySet()) {
                if (codesToRemoveFromCache.remove(entry.getKey())) {
                    codesToRemoveFromCache.add(entry.getValue());
                }
            }
            Map<String, MDTNode> nodesMap = getMdtNodesByCode();
            MDTNode node = null;
            for (String nodeCode : codesToRemoveFromCache) {
                node = nodesMap.get(nodeCode);
                if (node != null) {
                    mdtCacheManager.removeCache(node.getId());
                }
            }
            if (t instanceof FinATypeException) {
                result.setException((FinATypeException) t);
            } else {
                result.setException(new FinATypeException(t, FinATypeException.Type.NODE_ADD_ERROR));
            }
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                result.setException(new FinATypeException(e, FinATypeException.Type.GENERAL_ERROR));
            }
        }
        return result;
    }

    @Override
    public MdtImportTranslationResult importMdtTranslation(byte[] bytes) {
        MdtImportTranslationResult result = new MdtImportTranslationResult();
        TranslationNodes translationNodes;
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            String schemaFile = TranslationNodes.class.getPackage().getName().replace('.', '/') + "/mdt.translation.xsd";
            URL schemaUrl = TranslationNodes.class.getClassLoader().getResource(schemaFile);
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = (schemaUrl != null ? sf.newSchema(schemaUrl) : null);

            JAXBContext context = JAXBContext.newInstance(TranslationNodes.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setSchema(schema);

            Source source = new StreamSource(inputStream);
            JAXBElement<TranslationNodes> element = unmarshaller.unmarshal(source, TranslationNodes.class);
            translationNodes = element.getValue();

            if (result.getException() == null) {
                Map<String, MDTNode> translatedMdtNodesStore = new HashMap<>();
                Map<Long, Description> translatedDescriptionsStore = new HashMap<>();
                List<TranslationNode> xmlTranslationNodes = translationNodes.getNode();
                Map<String, MDTNode> mdtNodes = getMdtNodesByCode();
                if (xmlTranslationNodes != null) {
                    Map<String, Long> languageCodeIdsStore = languageSession.getLanguagesCodeIdMap();
                    for (TranslationNode xmlTranslationNode : xmlTranslationNodes) {
                        String xmlTranslationNodeCode = xmlTranslationNode.getCode().trim();
                        MDTNode mdtNode = mdtNodes.get(xmlTranslationNodeCode);
                        if (mdtNode == null) {
                            result.getNonExistingNodes().add(xmlTranslationNodeCode);
                        } else {
                            String langCode = xmlTranslationNode.getLangCode().trim();
                            Long langId = languageCodeIdsStore.get(langCode);
                            if (langId == null) {
                                result.getNonExistingLanguageCodes().add(langCode);
                            } else if (result.getNonExistingLanguageCodes().isEmpty() && result.getNonExistingNodes().isEmpty()) {
                                String mdtCode = mdtNode.getCode().trim();
                                String description = xmlTranslationNode.getDescription().trim();
                                if (translatedMdtNodesStore.get(mdtCode) != null) {
                                    translatedMdtNodesStore.get(mdtCode).getDescription().addDescription(langId, description);
                                } else {
                                    mdtNode.getDescription().addDescription(langId, description);
                                    translatedMdtNodesStore.put(mdtCode, mdtNode);
                                }

                                long nameStrId = mdtNode.getDescription().getNameStrId();
                                if (translatedDescriptionsStore.get(nameStrId) != null) {
                                    translatedDescriptionsStore.get(nameStrId).addDescription(langId, description);
                                } else {
                                    translatedDescriptionsStore.put(nameStrId, new Description(langId, nameStrId, description));
                                }
                            }
                        }
                    }

                    if (result.getNonExistingNodes().isEmpty() && result.getNonExistingLanguageCodes().isEmpty() && result.getException() == null) {
                        // update mdt cache
                        for (MDTNode mdtNode : translatedMdtNodesStore.values()) {
                            mdtCacheManager.addCache(mdtNode);
                        }
                        // update/insert sys string and it's cache
                        for (Map.Entry<Long, Description> translatedEntry : translatedDescriptionsStore.entrySet()) {
                            long nameStrId = translatedEntry.getKey();
                            for (Map.Entry<Long, String> translatedDescriptionEntry : translatedEntry.getValue().getDescriptions().entrySet()) {
                                long langId = translatedDescriptionEntry.getKey();
                                String value = translatedDescriptionEntry.getValue();
                                sysStringLocal.update(nameStrId, langId, value);
                            }
                        }
                    }

                }
            }
        } catch (Throwable e) {
            if (e instanceof JAXBException) {
                result.setException(new FinATypeException("Can not parse file"));
            } else {
                result.setException(new FinATypeException(e, FinATypeException.Type.GENERAL_ERROR));
            }
        }
        return result;
    }

    @Override
    public List<MDTNode> loadNodesSimple(long parentId) {
        Query query = em.createQuery("select new " + MDTNode.class.getName() + "( n.id, n.version, n.parentId, n.code, n.description, n.type, n.dataType, n.equation, n.key ) from IN_MDT_NODES as n where n.parentId=:parentId order by n.sequence").setParameter("parentId", parentId);
        return query.getResultList();
    }

    @Override
    public MDTNode loadNodeSimple(long nodeId) {
        Query query = em.createQuery("select new " + MDTNode.class.getName() + "( n.id, n.version, n.parentId, n.code, n.description, n.type, n.dataType, n.equation ) from IN_MDT_NODES as n where n.id=:nodeId").setParameter("nodeId", nodeId);
        return (MDTNode) query.getSingleResult();
    }

    public List<MDTNode> loadAllNodes() {
        List<MDTNode> nodes = new ArrayList<>();
        List<Object[]> nodesObject = em.createNamedQuery("MDT.findAllCodeAndDescription").getResultList();
        for (int i = 0; i < nodesObject.size(); i++) {
            Object[] arr = nodesObject.get(i);
            MDTNode node = new MDTNode();
            node.setId(Long.parseLong(arr[0].toString()));
            node.setCode(arr[1].toString().trim());
            node.setDescription((Description) arr[2]);
            nodes.add(node);
        }
        return nodes;
    }

    @Override
    public List<MDTDependentNode> loadAllMdtDependentNodes() {
        return em.createQuery("select mdn from IN_MDT_DEPENDENT_NODES mdn", MDTDependentNode.class).getResultList();
    }

    @Override
    public void reorder(MDTNode mdtNode) {
        List<MDTNode> mdtNodes = em.createNamedQuery("MDT.findByParentIdAndSequence", MDTNode.class).setParameter("parentId", mdtNode.getParentId()).setParameter("nodeSequence", mdtNode.getSequence()).getResultList();
        for (MDTNode mNode : mdtNodes) {
            if (mdtNode.getId() != mNode.getId()) {
                MDTNode node = em.find(MDTNode.class, mNode.getId());
                node.setSequence(mNode.getSequence() + 1);
                mdtCacheManager.addCache(node);
            }
        }
    }

    private void updateDependencies(MDTNode node) throws FinATypeException {
        TypedQuery<MDTDependentNode> mdtDependentNodeTypedQuery = em.createNamedQuery("MDTDependentNode.findAllByDependentNodeIds", MDTDependentNode.class);
        mdtDependentNodeTypedQuery.setParameter("dependentNodeIds", Arrays.asList(node.getId()));
        List<MDTDependentNode> dependentNodes = mdtDependentNodeTypedQuery.getResultList();
        for (MDTDependentNode dependentNode : dependentNodes) {
            em.remove(dependentNode);
        }

        Pattern mdtCodePattern = Pattern.compile(MDTNodeUtil.MDT_CODE_REGEX);
        HashSet<String> usedNodes = new HashSet<>(MDTNodeUtil.extractCodes(node.getEquation(), mdtCodePattern));

        for (MDTComparison comparison : node.getComparisons()) {
            usedNodes.addAll(MDTNodeUtil.extractCodes(comparison.getLeftEquation(), mdtCodePattern));
            usedNodes.addAll(MDTNodeUtil.extractCodes(comparison.getRightEquation(), mdtCodePattern));
        }

        if (MDTNodeTypes.LIST.equals(node.getType()) && node.getEquation() != null && !node.getEquation().trim().isEmpty()) {
            MDTNode depNode = findNodeById(Long.parseLong(node.getEquation().trim()));
            if (depNode != null && depNode.getType() != MDTNodeTypes.NODE) {
                throw new FinATypeException("List Element Dependant node should be a folder");
            }
            TypedQuery<String> query = em.createQuery("select m.code from IN_MDT_NODES m where m.id =: nodeId", String.class)
                    .setParameter("nodeId", Long.valueOf(node.getEquation()));

            List<String> result = query.getResultList();
            if (!result.isEmpty()) {
                usedNodes.add(result.get(0));
            }
        }

        Map<String, Set<String>> dependentCodesMap = new HashMap<String, Set<String>>();
        dependentCodesMap.put(node.getCode().trim(), usedNodes);
        try {
            storeNodeProperties(dependentCodesMap, new HashMap<String, Collection<Comparison>>(), new MDTImportResult());
        } catch (FinATypeException ex) {
            //rollback whole transaction
            sessionContext.setRollbackOnly();
            throw ex;
        }
    }

    @Override
    public MDTNode save(MDTNode node) throws FinATypeException {
        return save(node, false);
    }

    @Override
    public MDTNode saveImportedNode(MDTNode node) throws FinATypeException {
        if (!checkCodeUnique(node)) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE);
        }
        if (node.getId() == 0) {
            em.persist(node);
        } else {
            em.merge(node);
        }

        mdtCacheManager.addCache(node);

        return node;
    }

    @Override
    public List<MDTNode> save(List<MDTNode> nodes) throws FinATypeException {
        List<MDTNode> result = new ArrayList<>();
        for (MDTNode node : nodes) {
            MDTNode savedNode = save(node);
            result.add(savedNode);
        }
        return result;
    }

    private MDTNode save(MDTNode node, boolean importMode) throws FinATypeException {
        if (!checkCodeUnique(node)) {
            log.error(node.getCode() + " is not Unique!");
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE);
        }

        //validate existing mdt node code only if code changed
        if (node.getId() > 0) {
            String existingNodeCode = em.createQuery("select n.code from IN_MDT_NODES n where n.id=:id", String.class)
                    .setParameter("id", node.getId())
                    .getSingleResult();

            if (!existingNodeCode.equalsIgnoreCase(node.getCode())) {
                validateCode(node.getCode());
            }
            MDTNode existingNode = em.find(MDTNode.class, node.getId());
            //do not allow if node type changed from NODE to anything and it has children
            if (existingNode.getType().equals(MDTNodeTypes.NODE) && !node.getType().equals(existingNode.getType())) {
                List<MDTNode> children = em.createQuery("select n from IN_MDT_NODES n where n.parentId=:parentId", MDTNode.class)
                        .setMaxResults(1)
                        .setParameter("parentId", existingNode.getId())
                        .getResultList();
                if (!children.isEmpty()) {
                    throw new FinATypeException("Node Type Change is not allowed when it has children");
                }
            }
        } else {
            validateCode(node.getCode());
        }

        if (node.getType().equals(MDTNodeTypes.LIST)) {
            if (String.valueOf(node.getParentId()).equals(node.getEquation())) {
                throw new FinATypeException("List Element Should not point to its parent directory");
            }
        }

        if (node.getId() == 0) {
//            node.setVersion(0);
            if (!importMode) {
                node.setSequence(getNextSequenceForNode(node.getParentId()));
            }
            em.persist(node);
        } else {
            node.setVersion(em.find(MDTNode.class, node.getId()).getVersion());
            em.merge(node);
        }

        if (!isOrdered(node)) {
            reorder(node);
        }

        if (node.getParentId() != 0) {
            removeUserMdts(node.getId());
        }

        //Sync Cache
        MDTNode curNode = mdtCacheManager.getNode(node.getId());
        if (curNode != null) {
            curNode.getDescription().getDescriptions().entrySet().stream().filter(e -> !node.getDescription().getDescriptions().containsKey(e.getKey())).forEach(e -> {
                node.getDescription().getDescriptions().put(e.getKey(), e.getValue());
            });
        }
        mdtCacheManager.addCache(node);

        if (!importMode) {
            updateDependencies(node);
            mdtCacheManager.addCache(node);
        }

        return node;
    }

    private boolean isOrdered(MDTNode node) {
        List<MDTNode> siblings = loadMdtNodesByParentId(node.getParentId());
        for (MDTNode sibling : siblings) {
            if (sibling.getId() != node.getId() && sibling.getSequence() == node.getSequence()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public MDTNode findByCode(String code) {
        List<MDTNode> nodes = em.createNamedQuery("MDT.findByCode", MDTNode.class).setParameter("code", code).getResultList();
        if (nodes.isEmpty()) {
            return null;
        } else {
            return nodes.get(0);
        }
    }

    @Override
    public MDTNode findParentByCode(String childCode) {
        MDTNode node = findByCode(childCode);
        return em.find(MDTNode.class, node.getParentId());
    }

    /**
     * Checks given node's code for uniqueness.
     *
     * @param node node.code - the code to be checked for uniqueness
     *             node.id - the node id to exclude from check
     * @return true - if node is unique
     * false - if node is not unique
     */
    public boolean checkCodeUnique(MDTNode node) {
        return em.createNamedQuery("MDT.checkCodeUnique").setParameter("code", node.getCode()).setParameter("id", node.getId()).getResultList().isEmpty();
    }

    private void getChildrenIdsRecursive(List<Long> parentIds, List<Long> result) {
        List<Long> subParents = new ArrayList<>();
        for (Long parentId : parentIds) {
            for (MDTNode child : loadMdtNodesByParentId(parentId)) {
                result.add(child.getId());
                if (child.getType() == MDTNodeTypes.NODE) {
                    subParents.add(child.getId());
                }
            }
        }
        if (!subParents.isEmpty()) {
            getChildrenIdsRecursive(subParents, result);
        }
    }

    private Set<MDTDependentNode> getMDTDependentNodes() {
        return new HashSet<>(em.createNamedQuery("MDTDependentNode.findAll", MDTDependentNode.class).getResultList());
    }

    private void removeMdtNodesUnchecked(List<Long> nodeIds, MDTDeleteResult result) {
        TypedQuery<MDTDependentNode> dependentNodesByNodeIdQuery = em.createNamedQuery("MDTDependentNode.findByNodeAndDepNodeIds", MDTDependentNode.class);
        TypedQuery<MDTComparison> mdtComparisonTypedQuery = em.createNamedQuery("MDTComparison.findAllByNodeIds", MDTComparison.class);

        if (!nodeIds.isEmpty()) {
            Set<MDTDependentNode> mdtDependentNodes = new HashSet<>();
            Set<MDTComparison> mdtComparisons = new HashSet<>();
            List<List<Long>> partitionedNodeIds = CommonUtil.partitions(nodeIds);
            for (List<Long> partition : partitionedNodeIds) {
                dependentNodesByNodeIdQuery.setParameter("nodeIds", partition);
                dependentNodesByNodeIdQuery.setParameter("dependentNodeIds", partition);
                mdtDependentNodes.addAll(dependentNodesByNodeIdQuery.getResultList());

                mdtComparisonTypedQuery.setParameter("nodeIds", partition);
                mdtComparisons.addAll(mdtComparisonTypedQuery.getResultList());
            }

            for (MDTDependentNode dependentNode : mdtDependentNodes) {
                try {
                    em.remove(dependentNode);
                } catch (Throwable t) {
                    log.error(t.getMessage(), t);
                    throw new RuntimeException("Unable to remove DEPENDENTNODE record: " + dependentNode.toString(), t);
                }
            }
            for (MDTComparison comparison : mdtComparisons) {
                try {
                    em.remove(comparison);
                } catch (Throwable t) {
                    log.error(t.getMessage(), t);
                    throw new RuntimeException("Unable to remove COMPARISON record: " + comparison.toString(), t);
                }
            }
            TypedQuery<String> loadChildren = em.createQuery("select mdt.code from IN_MDT_NODES mdt where mdt.id in(:mdtIds)", String.class);

            int deletedNodes = 0;
            try {
                for (List<Long> partition : partitionedNodeIds) {
                    loadChildren.setParameter("mdtIds", partition);
                    result.getDeletedNodes().addAll(loadChildren.getResultList());
                    Query deleteMDTNodes = em.createQuery("delete from IN_MDT_NODES mdt where mdt.id in (:mdtIds)");

                    deleteMDTNodes.setParameter("mdtIds", new HashSet<>(partition));
                    deletedNodes += deleteMDTNodes.executeUpdate();

                    // audit log
                    auditLogAddManualRecordIdsEvent.fire(new AuditLogAddManualRecordIdsEvent(partition, OperationType.DELETE, new MDTNode()));
                }
                em.clear();
                em.flush();

                log.info("-----------------------------------");
                log.info("Deleted " + deletedNodes + " nodes.");
                log.info("-----------------------------------");
            } catch (Throwable throwable) {
                log.error(throwable.getMessage(), throwable);
                throw new RuntimeException("Unable to remove NODEs ", throwable);
            }
        }
    }

    @Override
    public Map<Long, MDTDeleteResult> delete(List<Long> nodeIds) throws FinATypeException {
        Map<Long, MDTDeleteResult> resultMap = new HashMap<>();
        if (nodeIds != null) {
            for (Long nodeId : nodeIds) {
                resultMap.put(nodeId, delete(nodeId));
            }
        }
        return resultMap;
    }

    @Deprecated
    private MDTDeleteResult delete(long nodeId) throws FinATypeException {
        MDTDeleteResult result = new MDTDeleteResult();

        MDTNode node = em.find(MDTNode.class, nodeId);

        List<Long> nodeChildrenIds = new ArrayList<>();
        getChildrenIdsRecursive(Collections.singletonList(node.getId()), nodeChildrenIds);

        List<Long> definitionTableNodeIds = em.createQuery("select dt.node.id from IN_DEFINITION_TABLES dt where dt.node.id=:nodeId", Long.class)
                .setParameter("nodeId", nodeId)
                .getResultList();

        List<Long> inReturnItemsNodeIds = em.createQuery("select distinct ri.mdtNode.id from IN_RETURN_ITEMS ri where ri.mdtNode.id=:nodeId", Long.class)
                .setParameter("nodeId", nodeId)
                .getResultList();


        List<Long> removedIds = new ArrayList<>(nodeChildrenIds);
        removedIds.add(nodeId);

        Set<MDTDependentNodePK> mdtDependentNodes = new HashSet<>();
        TypedQuery<MDTDependentNodePK> query = em.createNamedQuery("MDTDependentNodePK.findAllByNodeIds", MDTDependentNodePK.class);
        List<List<Long>> partitionedRemoveIds = CommonUtil.partitions(removedIds);
        for (List<Long> partition : partitionedRemoveIds) {
            query.setParameter("nodeIds", partition);
            mdtDependentNodes.addAll(query.getResultList());
        }

        List<Long> nodeParentIds = getParentIds(nodeId);

        Map<Long, MDTNode> cache = mdtCacheManager.getMdtNodesById();
        try {
            //check if node is participating in return definitions
            if (definitionTableNodeIds.contains(nodeId)) {
                result.getDefinitionTableNodeWarnings().add(cache.get(nodeId).getCode());
            }

            //check if node's children are participating in return definitions
            if (!Collections.disjoint(definitionTableNodeIds, nodeChildrenIds)) {
                for (Long childId : nodeChildrenIds) {
                    if (definitionTableNodeIds.contains(childId)) {
                        result.getDefinitionTableNodeWarnings().add(cache.get(childId).getCode());
                    }
                }
            }

            //check if node's parents are participating in return definitions
            if (!Collections.disjoint(nodeParentIds, definitionTableNodeIds)) {
                for (Long parentId : nodeParentIds) {
                    if (definitionTableNodeIds.contains(parentId)) {
                        result.getParentDefinitionTableNodeWarnings().add(cache.get(parentId).getCode());
                    }
                }
            }

            //check if node is used in other nodes out of it's subtree
            for (MDTDependentNodePK pk : mdtDependentNodes) {
                //1 in 1'000'000 case
                if (pk.getNodeId() == nodeId && !nodeChildrenIds.contains(pk.getDependentNodeId()) && pk.getDependentNodeId() != nodeId) {
                    result.getDependentNodeWarnings().put(cache.get(pk.getNodeId()).getCode(), cache.get(pk.getDependentNodeId()).getCode());
                }
            }

            //check if node's children are used in other nodes out of node's subtree
            for (MDTDependentNodePK pk : mdtDependentNodes) {
                if (nodeChildrenIds.contains(pk.getNodeId()) && nodeId != pk.getDependentNodeId() && !nodeChildrenIds.contains(pk.getDependentNodeId())) {
                    result.getDependentNodeWarnings().put(cache.get(pk.getNodeId()).getCode(), cache.get(pk.getDependentNodeId()).getCode());
                }
            }

            //check if node is used in in_return_items table
            if (inReturnItemsNodeIds.contains(nodeId)) {
                result.getInReturnItemsNodeWarnings().add(cache.get(nodeId).getCode());
            }

            //check if node's are used in in_return_items table
            if (!Collections.disjoint(inReturnItemsNodeIds, nodeChildrenIds)) {
                for (Long childId : nodeChildrenIds) {
                    if (inReturnItemsNodeIds.contains(childId)) {
                        result.getInReturnItemsNodeWarnings().add(cache.get(childId).getCode());
                    }
                }
            }


            //check if is root node and user has persmission
            if (node.getParentId() == 0) {

                List<String> dependentUsers = em.createQuery("select umdt.userMdtId.user.login from SYS_USER_MDT umdt where  umdt.userMdtId.node.id=:nodeId and umdt.userMdtId.user.id<>:userId", String.class).setParameter("nodeId", node.getId()).setParameter("userId", userLocal.getCurrentUserId()).getResultList();

                if (!dependentUsers.isEmpty()) {
                    result.getDependentUsers().addAll(dependentUsers);
                }

                List<String> dependentRoles = em.createQuery("select rmdt.roleMdtId.role.code from SYS_ROLE_MDT rmdt where  rmdt.roleMdtId.node.id=:nodeId", String.class).setParameter("nodeId", node.getId()).getResultList();

                if (!dependentRoles.isEmpty()) {
                    result.getDependentRoles().addAll(dependentRoles);
                }

            }

            if (result.getTotalSize() == 0) {
                boolean success = true;
                try {
                    removeMdtNodesUnchecked(nodeChildrenIds, result);
                    List<Long> mainNode = new ArrayList<>();
                    mainNode.add(nodeId);
                    removeUserMdts(nodeId);
                    removeMdtNodesUnchecked(mainNode, result);
                } catch (Throwable t) {
                    success = false;
                    throw t;
                } finally {
                    if (success) {
                        for (Long id : nodeChildrenIds) {
                            mdtCacheManager.removeCache(id);
                        }
                        mdtCacheManager.removeCache(nodeId);
                    }
                }
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            throw new FinATypeException(t, FinATypeException.Type.NODE_REMOVE_ERROR);
        }
        return result;
    }

    private void removeUserMdts(long nodeId) {
        em.createQuery("DELETE FROM SYS_USER_MDT mdt WHERE mdt.userMdtId.node.id=:nodeId").setParameter("nodeId", nodeId).executeUpdate();
    }

    @Override
    @Deprecated
    public Map<Integer, ArrayList<MDTDependency>> getDependencies(Long nodeId, Long langId) {

        HashMap<Integer, ArrayList<MDTDependency>> map = new HashMap<Integer, ArrayList<MDTDependency>>();
        ArrayList<MDTDependency> dependNode = new ArrayList<MDTDependency>();
        Query query = em.createQuery("select b from IN_MDT_DEPENDENT_NODES a, IN_MDT_NODES b where a.depNode.nodeId=:nodeId and b.id=a.depNode.dependentNodeId");
        query.setParameter("nodeId", nodeId);

        List<MDTNode> nodes = query.getResultList();
        for (MDTNode mdtNode : nodes) {
            MDTDependency dependency = new MDTDependency();
            dependency.setId(mdtNode.getId());
            dependency.setCode(mdtNode.getCode());
            dependency.setDescription(mdtNode.getDescription().getDescription(langId));
            dependency.setUsedBy("Used By");
            dependency.setType(mdtNode.getType());
            dependNode.add(dependency);
        }
        query = em.createQuery("select b from IN_MDT_DEPENDENT_NODES a, IN_MDT_NODES b where a.depNode.dependentNodeId=:nodeId and b.id=a.depNode.nodeId");
        query.setParameter("nodeId", nodeId);

        List<MDTNode> nodes2 = query.getResultList();
        for (MDTNode mdtNode : nodes2) {
            MDTDependency dependency = new MDTDependency();
            dependency.setId(mdtNode.getId());
            dependency.setCode(mdtNode.getCode());
            dependency.setDescription(mdtNode.getDescription().getDescription(langId));
            dependency.setUsedBy("Depend On");
            dependency.setType(mdtNode.getType());
            dependNode.add(dependency);
        }
        map.put(1, dependNode);

        ArrayList<MDTDependency> dependDefinitions = new ArrayList<MDTDependency>();
        Query definitionQuery = em.createQuery("select rd, dt.code" + " from IN_DEFINITION_TABLES dt, IN_RETURN_DEFINITIONS rd " + " where dt.node.id in(:ids) and rd.id=dt.returnDefinition.id");
        List<Long> parentIds = getParentIds(nodeId);
        if (parentIds != null) {
            parentIds.add(nodeId);
        }
        definitionQuery.setParameter("ids", parentIds);
        List<Object[]> definitions = definitionQuery.getResultList();
        for (Object[] objects : definitions) {
            ReturnDefinition definition = (ReturnDefinition) objects[0];
            MDTDependency dependency = new MDTDependency();
            dependency.setCode(definition.getCode());
            dependency.setDescription(definition.getDescription().getDescription(langId));
            dependency.setUsedBy((String) objects[1]);
            dependDefinitions.add(dependency);
        }
        map.put(2, dependDefinitions);
        return map;
    }

    @Override
    public List<Long> getParentIds(long nodeId) {
        List<Long> result = new ArrayList<>();
        Map<Long, MDTNode> nodesById = mdtCacheManager.getMdtNodesById();
        if (nodesById != null && nodesById.get(nodeId) != null) {
            long parentId = nodesById.get(nodeId).getParentId();
            while (parentId != 0) {
                result.add(parentId);
                parentId = nodesById.get(parentId).getParentId();
            }
        }
        return result;
    }

    @Override
    public boolean enableDisable(long id) {
        MDTNode node = em.find(MDTNode.class, id);
        if (node.isDisabled()) {
            node.setDisabled(false);
        } else {
            node.setDisabled(true);
        }
        return node.isDisabled();
    }

    @Override
    public List<MDTNode> getMDTNodeByCodes(List<String> codes) {
        if (codes.size() < 1) {
            return new ArrayList<MDTNode>();
        }
        Query query = em.createQuery("select new " + MDTNode.class.getName() + "( mn.id, mn.version, mn.parentId, mn.code, mn.description, mn.type ) from IN_MDT_NODES mn where trim(mn.code) in(:codes)");
        query.setParameter("codes", codes);
        return query.getResultList();
    }

    @Override
    public MDTComparison saveComparison(MDTComparison model) throws FinATypeException {
        MDTNode mdtNode = em.find(MDTNode.class, model.getNode().getId());
        if (mdtNode.getType() == MDTNodeTypes.NODE) {
            throw new FinATypeException("Comparison can not be added on folder");
        }
        model.setNode(mdtNode);
        if (model.getId() == 0) {
            em.persist(model);
        } else {
            model = em.merge(model);
            em.flush();
        }
//        mdtNode.getComparisons().add(model);
        updateDependencies(mdtNode);
        return model;
    }

    @Override
    public void deleteComparison(MDTComparison model) throws FinATypeException {
        TypedQuery<MDTComparison> query = em.createNamedQuery("MDTComparison.findByNodeAndId", MDTComparison.class);
        query.setParameter("id", model.getId());
        query.setParameter("nodeId", model.getNode().getId());
        MDTComparison mdtComparison = query.getSingleResult();
        MDTNode mdtNode = mdtComparison.getNode();
        removeComparisonDependentNode(mdtNode, mdtComparison);
    }


    @Override
    public List<MDTComparison> loadComparisons(Map<MDTComparisonFilter, Object> filter, SortField sortField) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MDTComparison> query = cb.createQuery(MDTComparison.class);
        Root<MDTComparison> mdtComparison = query.from(MDTComparison.class);
        Join<MDTComparison, MDTNode> nodeJoin = mdtComparison.join(MDTComparison_.node);


        Selection[] selections = new Selection[]{mdtComparison.get(MDTComparison_.id), mdtComparison.get(MDTComparison_.version), mdtComparison.get(MDTComparison_.condition),
                mdtComparison.get(MDTComparison_.leftEquation), mdtComparison.get(MDTComparison_.rightEquation), mdtComparison.get(MDTComparison_.template),
                mdtComparison.get(MDTComparison_.numberPattern), nodeJoin.get(MDTNode_.id), nodeJoin.get(MDTNode_.version), nodeJoin.get(MDTNode_.code),
                nodeJoin.get(MDTNode_.description), nodeJoin.get(MDTNode_.type), nodeJoin.get(MDTNode_.disabled), mdtComparison.get(MDTComparison_.processStage)};

        query.select(cb.construct(MDTComparison.class, selections));

        List<Predicate> predicates = getComparisonFilterPredicates(filter, cb, mdtComparison, nodeJoin);

        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }

        query.orderBy(cb.desc(nodeJoin.get(MDTNode_.code)));

        if (sortField != null) {
            String sortProperty = sortField.getProperty();
            boolean isAsc = sortField.isAsc();

            if (!sortProperty.isBlank()) {
                if (sortProperty.equalsIgnoreCase("node")) {
                    query.orderBy(isAsc ? cb.asc(nodeJoin.get(MDTNode_.code)) : cb.desc(nodeJoin.get(MDTNode_.code)));
                } else if (SortUtil.isPropertyExistOnEntity(mdtComparison, sortProperty)) {
                    query.orderBy(isAsc ? cb.asc(mdtComparison.get(sortProperty)) : cb.desc(mdtComparison.get(sortProperty)));
                }
            }
        }

        TypedQuery<MDTComparison> criteriaQuery = em.createQuery(query);
        int offset = (int) filter.getOrDefault(MDTComparisonFilter.PAGE_OFFSET, -1);
        int limit = (int) filter.getOrDefault(MDTComparisonFilter.PAGE_LIMIT, -1);

        if (offset >= 0 && limit > 0) {
            criteriaQuery.setFirstResult((offset));
            criteriaQuery.setMaxResults(limit);
        }

        return criteriaQuery.getResultList();
    }

    @Override
    public boolean isDepend(Long nodeId) {
        return em.createQuery("select c from IN_MDT_DEPENDENT_NODES c where c.depNode.dependentNodeId=:nodeId ").setParameter("nodeId", nodeId).getResultList().size() > 0;
    }

    @Override
    @LogDescription(ignore = true)
    public Map<Long, List<MDTNode>> loadAllNodesByParentId() {
        return loadAllNodesByParentId(true);
    }

    private Map<Long, List<MDTNode>> loadAllNodesByParentId(boolean excludeDisabled) {
        Collection<MDTNode> nodes = mdtCacheManager.getMdtNodes();
        Map<Long, List<MDTNode>> nodesMap = new HashMap<>();

        for (MDTNode node : nodes) {
            if (excludeDisabled && node.isDisabled()) continue;
            List<MDTNode> list;

            if (nodesMap.get(node.getParentId()) == null) {
                list = new ArrayList<>();
                nodesMap.put(node.getParentId(), list);
            } else {
                list = nodesMap.get(node.getParentId());
            }

            list.add(node);
        }

        return nodesMap;
    }

    @Override
    public MDTNode loadMdtNodes(long parentId) {

        MDTNode mdtNode = em.find(MDTNode.class, parentId);

        if (childrenCount(mdtNode.getId()) > 0) {

            Query loadMdtsQuery = em.createNamedQuery("MDT.findByParentIdEnabled").setParameter("parentId", parentId).setParameter("disabled", false);

            @SuppressWarnings("unchecked") List<MDTNode> nodes = loadMdtsQuery.getResultList();

            for (MDTNode node : nodes) {
                loadMdtNodes(node.getId());

                //TODO Performance
                for (MDTComparison c : node.getComparisons()) {
                    c.getId();
                }
                for (MDTNode n : node.getDependentNodes()) {
                    for (MDTNode mn : n.getDependentNodes()) {
                        mn.getComparisons();
                    }
                }

            }

            mdtNode.setChildren(nodes);
        }

        return mdtNode;
    }

    @Override
    public long childrenCount(long mdtNodeId) {
        Query query = em.createNamedQuery("MDT.childrenCount").setParameter("parentId", mdtNodeId);
        return (long) query.getSingleResult();
    }

    @Override
    public List<String> changeNodeType(List<String> codes, MDTNodeTypes type) {
        List<String> changeList = new ArrayList<>();
        List<MDTNode> mdtNodes = getMDTNodeByCodes(codes);

        em.setFlushMode(FlushModeType.COMMIT);

        for (MDTNode m : mdtNodes) {
            m.setType(type);
            m.setDependentNodes(null);
            m.setEquation(" ");
            em.merge(m);

            changeList.add(m.getCode());
        }
        return changeList;
    }

    @Override
    public long getMdtMaxId() {
        Query query = em.createQuery("select max(m.id) from IN_MDT_NODES as m");
        return (long) query.getSingleResult();
    }

    @Override
    public MDTNode loadSimpleMdtNode(long id) {
        StringBuilder qlString = new StringBuilder();
        qlString.append("SELECT ");
        qlString.append(" NEW ");
        qlString.append(MDTNode.class.getName());
        qlString.append("(");
        qlString.append("m.id, ");
        qlString.append("m.version ");
        qlString.append(")");
        qlString.append(" from IN_MDT_NODES  as m where m.id=:id ");
        Query query = em.createQuery(qlString.toString());
        query.setParameter("id", id);
        return (MDTNode) query.getSingleResult();
    }

    @Override
    public List<MDTNode> getNodePath(long nodeId) {
        List<MDTNode> path = new ArrayList<>();
        MDTNode mdtNode = em.find(MDTNode.class, nodeId);
        getNodeParent(mdtNode, path);
        return path;
    }

    private void getNodeParent(MDTNode mdtNode, List<MDTNode> path) {
        if (mdtNode != null) {
            if (mdtNode.getParentId() != 0) {
                MDTNode tmpNode = em.createQuery("select n from IN_MDT_NODES n where n.id=:parentId", MDTNode.class).setParameter("parentId", mdtNode.getParentId()).getSingleResult();
                getNodeParent(tmpNode, path);
            }
            path.add(mdtNode);
        }
    }

    @Override
    public Map<String, MDTNode> getMdtNodesByCode() {
        Map<String, MDTNode> result = new HashMap<>();
        for (MDTNode node : mdtCacheManager.getMdtNodes()) {
            result.put(node.getCode(), node);
        }
        return result;
    }

    @Override
    @LogDescription(ignore = true)
    public List<MDTNode> loadMdtNodesByParentId(long parentId) {
        List<MDTNode> nodes = new ArrayList<>();
        for (MDTNode mdtNode : mdtCacheManager.getMdtNodesById().values()) {
            if (!mdtNode.isDisabled()) {
                if (mdtNode.getParentId() == parentId) {
                    nodes.add(mdtNode);
                }
            }
        }
        Collections.sort(nodes, new Comparator<MDTNode>() {
            @Override
            public int compare(MDTNode mdtNode, MDTNode t1) {
                if (mdtNode.getSequence() == t1.getSequence()) {
                    return 0;
                }

                return mdtNode.getSequence() > t1.getSequence() ? 1 : -1;
            }
        });
        return nodes;
    }

    @Override
    @LogDescription(ignore = true)
    public void loadNodeChildren(List<Long> result, long nodeId) {
        List<MDTNode> nodes = loadMdtNodesByParentId(nodeId);
        for (MDTNode node : nodes) {
            if (node.getType() == MDTNodeTypes.NODE) {
                loadNodeChildren(result, node.getId());
            }
            result.add(node.getId());
        }
    }

    @Override
    @LogDescription(ignore = true)
    public String getMdtNodeCodeById(long id) {
        MDTNode node = mdtCacheManager.getMdtNodesById().get(id);
        return node != null ? node.getCode() : null;
    }

    @Override
    public boolean isUsed(long nodeId) {
        Query query = em.createQuery("select count (a) from IN_MDT_DEPENDENT_NODES a where a.depNode.nodeId=:nodeId");
        query.setParameter("nodeId", nodeId);
        Object result = query.getSingleResult();
        return result != null && ((long) result) > 0L;
    }

    @Override
    public List<String> loadMdtItemCodesByTypes(List<MDTNodeTypes> nodeTypes) {
        List<String> result = new ArrayList<>();
        for (MDTNode node : mdtCacheManager.getMdtNodes()) {
            if (nodeTypes.contains(node.getType())) {
                result.add(node.getCode() != null ? node.getCode().trim() : null);
            }
        }
        return result;
    }

    @Override
    public MDTNode findNodeById(long id) {
        Map<Long, MDTNode> nodes = mdtCacheManager.getMdtNodesById();
        return nodes != null ? nodes.get(id) : null;
    }

    @Override
    public Map<Long, MDTNode> getMdtNodesById() {
        return mdtCacheManager.getMdtNodesById();
    }

    private void removeComparisonDependentNode(MDTNode mdtNode, MDTComparison mdtComparison) {
        Iterator<MDTNode> iter = mdtNode.getDependentNodes().iterator();
        while (iter.hasNext()) {
            MDTNode node = iter.next();
            if (mdtComparison.getRightEquation().contains(node.getCode())) {
                /**
                 *  remove comparison dependent node
                 */
                if (!mdtNode.getEquation().contains(node.getCode())) {
                    em.createNamedQuery("MDTDependentNode.deleteComparisonDependency").setParameter("nodeId", node.getId()).executeUpdate();
                }
            }

        }
        em.remove(mdtComparison);
    }

    @Override
    public List<MDTNode> getUserMdtNodesFromCache() {
        List<Long> userNodes = loadUserRootNodeIds(userLocal.getCurrentUserId());

        Collection<List<MDTNode>> nodesCollection = getAllNodesByParentIdWithLevel1Id().values();

        List<MDTNode> nodesList = new ArrayList<>();
        for (List<MDTNode> nodes : nodesCollection) {
            nodesList.addAll(nodes.stream().filter(node -> userNodes.contains(node.getLevel1())).collect(Collectors.toList()));
        }
        return nodesList;
    }

    @Override
    public List<MDTNode> loadNodesFromCache(Map<MdtFilter, Object> filter, long langId) {

        String key = filter.get(MdtFilter.code).toString().toLowerCase();
        List<MDTNode> result = new ArrayList<>();

        java.util.function.Predicate<MDTNode> codePredicate = node -> node.getCode() != null && node.getCode().toLowerCase().contains(key);
        java.util.function.Predicate<MDTNode> namePredicate = node -> node.getDescription() != null && node.getDescription().getDescription(langId) != null && node.getDescription().getDescription(langId).toLowerCase().contains(key);

        java.util.function.Predicate<MDTNode> filterPredicate = codePredicate.or(namePredicate);

        if (filter.get(MdtFilter.showLeaves) != null && filter.get(MdtFilter.showLeaves).equals(false)) {
            java.util.function.Predicate<MDTNode> leafPredicate = node -> node.getType().equals(MDTNodeTypes.NODE);
            filterPredicate = leafPredicate.and(codePredicate.or(namePredicate));
        }

        if (filter.get(MdtFilter.TYPES) != null) {
            List<MDTNodeTypes> types = (List<MDTNodeTypes>) filter.get(MdtFilter.TYPES);
            if (!types.isEmpty()) {
                java.util.function.Predicate<MDTNode> typePredicate = node -> types.contains(node.getType());
                filterPredicate = typePredicate.and(codePredicate.or(namePredicate));
            }
        }

        int offset = (int) filter.getOrDefault(MdtFilter.OFFSET, -1);
        int limit = (int) filter.getOrDefault(MdtFilter.LIMIT, -1);

        if (!key.isEmpty()) {
            if (offset >= 0 && limit > 0) {
                result.addAll(getUserMdtNodesFromCache().stream().filter(filterPredicate).skip(offset).limit(limit).toList());
            } else {
                result.addAll(getUserMdtNodesFromCache().stream().filter(filterPredicate).toList());
            }
        }

        return result;
    }

    @Override
    public int countFromCache(Map<MdtFilter, Object> filter, long langId) {
        String key = filter.get(MdtFilter.code).toString().toLowerCase();
        java.util.function.Predicate<MDTNode> codePredicate = node -> node.getCode() != null && node.getCode().toLowerCase().contains(key);
        java.util.function.Predicate<MDTNode> namePredicate = node -> node.getDescription() != null && node.getDescription().getDescription(langId) != null && node.getDescription().getDescription(langId).toLowerCase().contains(key);

        if (!key.isEmpty()) {
            return (int) getUserMdtNodesFromCache().stream().filter(codePredicate.or(namePredicate)).count();
        }

        return 0;
    }

    @Override
    public List<MDTNode> loadDamagedMdtNodes() {
        List<MDTNode> result = new ArrayList<>();
        Map<Long, MDTNode> mdtNodeMap = mdtCacheManager.getMdtNodesById();
        mdtNodeMap.values().stream().filter(node -> node.isDamagedEquation()).forEach(node -> {
            loadDamagedMdtNodeHierarchy(mdtNodeMap, result, node);
        });
        return result;
    }

    @Override
    public List<MDTNode> loadRootNodesFromCache() {
        return mdtCacheManager.getMdtNodes().stream().filter(node -> node.getParentId() == 0 && node.getType() == MDTNodeTypes.NODE).sorted((o1, o2) -> Long.compare(o1.getSequence(), o2.getSequence())).collect(Collectors.toList());
    }

    @Override
    public List<UserMdt> loadUserRootNodes(long userId) {

        return em.createQuery("SELECT DISTINCT uMdt FROM SYS_USERS u, in(u.userMdts) uMdt WHERE u.id=:userId", UserMdt.class).setParameter("userId", userId).getResultList().stream().sorted((o1, o2) -> Long.compare(o1.getUserMdtId().getNode().getSequence(), o2.getUserMdtId().getNode().getSequence())).collect(Collectors.toList());
    }

    @Override
    public List<RoleMdt> loadUserRoleRootNodes(long userId) {
        List<Long> roleIds = userLocal.findUserbyId(userId).getRoles().stream().map(Role::getId).collect(Collectors.toList());
        return em.createQuery("SELECT DISTINCT uMdt FROM SYS_ROLES u, in(u.roleMdts) uMdt WHERE u.id in :roleIds", RoleMdt.class).setParameter("roleIds", roleIds.isEmpty() ? Collections.singletonList(-1L) : roleIds).getResultList().stream().sorted(Comparator.comparingLong(o -> o.getRoleMdtId().getNode().getSequence())).collect(Collectors.toList());
    }

    private List<Long> loadUserRootNodeIds(long userId) {
        List<Long> roleIds = userLocal.findUserbyId(userId).getRoles().stream().map(Role::getId).collect(Collectors.toList());
        List<Long> roleMdtIds = em.createQuery("SELECT DISTINCT uMdt.roleMdtId.node.id FROM SYS_ROLES u left join u.roleMdts uMdt WHERE u.id in :roleIds", Long.class).setParameter("roleIds", roleIds.isEmpty() ? Collections.singletonList(-1L) : roleIds).getResultList();

        List<Long> userMdtIds = em.createQuery("SELECT DISTINCT uMdt.userMdtId.node.id FROM SYS_USERS u, in(u.userMdts) uMdt WHERE u.id=:userId", Long.class).setParameter("userId", userId).getResultList();

        userMdtIds.addAll(roleMdtIds);

        return userMdtIds;
    }

    private void loadDamagedMdtNodeHierarchy(Map<Long, MDTNode> mdtNodeMap, List<MDTNode> demagedNodes, MDTNode node) {
        if (!demagedNodes.contains(node)) {
            demagedNodes.add(node);
        }
        if (node.getParentId() > 0) {
            loadDamagedMdtNodeHierarchy(mdtNodeMap, demagedNodes, mdtNodeMap.get(node.getParentId()));
        }
    }

    @Override
    public Map<Long, List<MDTNode>> getAllNodesByParentIdWithLevel1Id() {
        Map<Long, List<MDTNode>> nodesByParentId = loadAllNodesByParentId(false);

        List<MDTNode> roots = nodesByParentId.get(0L);
        if (roots != null) {
            for (MDTNode node : roots) {
                compileTree(node.getId(), node, nodesByParentId);
            }
        }

        return nodesByParentId;
    }

    @Override
    public UserMdt getUserMdt(long nodeId, long userId) {
        return em.createQuery("SELECT uMdt FROM SYS_USERS u, in(u.userMdts) uMdt WHERE u.id=:userId AND uMdt.userMdtId.node.id=:nodeId", UserMdt.class).setParameter("userId", userId).setParameter("nodeId", nodeId).getSingleResult();
    }

    @Override
    public Collection<MDTNode> getMdtNodesFromCache() {
        return mdtCacheManager.getMdtNodes();
    }

    @Override
    public long getNodeChildMaxSequence(long nodeId) {
        List<Long> result = em.createQuery("select max(n.sequence) from IN_MDT_NODES n where n.parentId=:parentId", Long.class).setParameter("parentId", nodeId).getResultList();
        return result.isEmpty() || result.get(0) == null ? 0 : result.get(0);
    }

    @Override
    public void checkUniqueEquationInSubTree(MDTNode catalogNode, MDTNode catalogTrashNode, String key) throws FinATypeException {
        long nodeCount = em.createQuery("select count(n.id) from IN_MDT_NODES n where n.parentId=:parentId and lower(n.equation)=:equation", Long.class)
                .setParameter("parentId", catalogNode.getId())
                .setParameter("equation", key.trim().toLowerCase())
                .getSingleResult();
        if (nodeCount > 0) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE);
        }
        List<Long> deletedNodeIds = em.createQuery("select n.id from IN_MDT_NODES n where n.parentId=:parentId and n.code like :code and lower(n.equation)=:equation", Long.class)
                .setParameter("parentId", catalogTrashNode.getId())
                .setParameter("code", catalogNode.getCode() + ".%")
                .setParameter("equation", key.trim().toLowerCase())
                .getResultList();

        if (!deletedNodeIds.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.ENTITY_PROGRAMMATICALLY_DELETED, new String[]{String.valueOf(deletedNodeIds.get(0))});
        }
    }

    @Override
    public MDTNode fixInconsistentDependency(long nodeId) throws FinATypeException {
        MDTNode mdtNode = em.find(MDTNode.class, nodeId);
        mdtNode.setDependentNodes(null);
        save(mdtNode);
        return mdtNode;
    }

    @Override
    public void moveNode(long nodeId, long parentNodeID) {
        em.createQuery("update IN_MDT_NODES set parentId=:parentId where id=:nodeId").setParameter("nodeId", nodeId).setParameter("parentId", parentNodeID).executeUpdate();
        MDTNode node = mdtCacheManager.getNode(nodeId);
        node.setParentId(parentNodeID);
        mdtCacheManager.addCache(node);
    }

    @Override
    public int countComparisons(Map<MDTComparisonFilter, Object> filter) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<MDTComparison> mdtComparison = query.from(MDTComparison.class);
        Join<MDTComparison, MDTNode> nodeJoin = mdtComparison.join(MDTComparison_.node);

        query.select(cb.countDistinct(mdtComparison.get(MDTComparison_.id)));

        List<Predicate> predicates = getComparisonFilterPredicates(filter, cb, mdtComparison, nodeJoin);

        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }

        return em.createQuery(query).getSingleResult().intValue();
    }

    @Override
    public long childrenCountWhereCodeLike(long parentId, String codeLike) {
        return em.createQuery("select count(n.id) from IN_MDT_NODES n where n.parentId=:parentId and n.code like :code", Long.class)
                .setParameter("parentId", parentId)
                .setParameter("code", codeLike)
                .getSingleResult();
    }

    @Override
    public List<MDTNode> loadDescendantNodes(long parentId) {
        MDTNode parent = em.find(MDTNode.class, parentId);
        List<MDTNode> result = new ArrayList<>();
        result.add(parent);
        if (parent != null) {
            loadChildrenRecursive(parent, result);
        }

        return result;
    }


    private void compileTree(long root, MDTNode node, Map<Long, List<MDTNode>> nodesByParentId) {
        if (node != null) {
            node.setLevel1(root);
            List<MDTNode> children = nodesByParentId.get(node.getId());
            if (children != null) {
                for (MDTNode n : children) {
                    compileTree(root, n, nodesByParentId);
                }
            }
        }
    }

    private long getNextSequenceForNode(long parentId) {
        return em.createQuery("select coalesce(max(n.sequence + 1), 0) from IN_MDT_NODES n where n.parentId=:parentId", Long.class).setParameter("parentId", parentId).getSingleResult();
    }

    private void validateCode(String code) throws FinATypeException {
        if (code == null || code.trim().isEmpty()) {
            throw new FinATypeException("Node Code can not be empty");
        }
        String regex = "^[^+\\-=*:]*$";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(code);

        if (!matcher.matches()) {
            log.error("Node code contains +, -, *, = or :");
            throw new FinATypeException("Following characters are not allowed : [+ , - , * , = , : ]");
        }
    }

    private List<Predicate> getComparisonFilterPredicates(Map<MDTComparisonFilter, Object> filter, CriteriaBuilder cb, Root<MDTComparison> root, Join<MDTComparison, MDTNode> nodeJoin) {
        List<Predicate> predicates = new ArrayList<>();
        if (filter != null) {
            for (Map.Entry<MDTComparisonFilter, Object> entry : filter.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                switch (entry.getKey()) {
                    case NODE_ID: {
                        predicates.add(cb.equal(nodeJoin.get(MDTNode_.id), entry.getValue()));
                        break;
                    }
                    case NODE_CODE: {
                        predicates.add(cb.like(nodeJoin.get(MDTNode_.code), entry.getValue().toString()));
                        break;
                    }
                    case RIGHT_EQUATION: {
                        predicates.add(cb.like(root.get(MDTComparison_.rightEquation), entry.getValue().toString()));
                        break;
                    }
                    case LEFT_EQUATION: {
                        predicates.add(cb.like(root.get(MDTComparison_.leftEquation), entry.getValue().toString()));
                        break;
                    }
                    case NUMBER_PATTERN: {
                        predicates.add(cb.like(root.get(MDTComparison_.numberPattern), entry.getValue().toString()));
                        break;
                    }
                    case CONDITION: {
                        predicates.add(cb.equal(root.get(MDTComparison_.condition), entry.getValue()));
                        break;
                    }
                    case ERROR_TEMPLATE: {
                        predicates.add(cb.like(root.get(MDTComparison_.template), entry.getValue().toString()));
                        break;
                    }
                }
            }
        }
        return predicates;

    }

    private List<Predicate> getFilterPredicates(CriteriaBuilder cb, Root<MDTNode> mdtNode, Map<MdtFilter, Object> filter) {
        List<Predicate> predicates = new ArrayList<>();
        if (filter != null) {
            for (Map.Entry<MdtFilter, Object> entry : filter.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                switch (entry.getKey()) {
                    case parentId:
                        predicates.add(cb.equal(mdtNode.get(MDTNode_.parentId), entry.getValue()));
                        break;
                    case code:
                        predicates.add(cb.like(cb.lower(cb.trim(mdtNode.get(MDTNode_.code))), entry.getValue().toString().toLowerCase()));
                        break;
                    case disable:
                        predicates.add(cb.equal(mdtNode.get(MDTNode_.disabled), entry.getValue()));
                        break;
                    case showLeaves:
                        predicates.add(cb.equal(mdtNode.get(MDTNode_.type), MDTNodeTypes.NODE));
                        break;
                }
            }
        }
        return predicates;
    }

}
