package net.fina.server.returns.impl;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.ReturnDefinitionFilter;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.returns.DependenceModel;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.common.server.util.CommonUtil;
import net.fina.common.shared.SortField;
import net.fina.security.util.SecurityUtil;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.helper.Description;
import net.fina.server.interceptors.LogDescription;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.matrix.api.SubMatrixLocal;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTComparison;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.mdt.impl.MDTCacheManager;
import net.fina.server.mdt.xml.v1.Comparison;
import net.fina.server.mdt.xml.v1.Mdt;
import net.fina.server.mdt.xml.v1.Node;
import net.fina.server.mdt.xml.v1.Optional;
import net.fina.server.processing.dependency.DependencyItem;
import net.fina.server.processing.dependency.DependencyItemTopologicalSort;
import net.fina.server.returns.api.ReturnDefinitionLocal;
import net.fina.server.returns.entity.*;
import net.fina.server.returns.event.ReturnFormatChangeEvent;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.User;
import net.fina.server.util.DBUtil;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Stateless
@Interceptors(RecordingAuditor.class)
public class ReturnDefinitionSession implements ReturnDefinitionLocal {
    private final Logger log = Logger.getLogger(getClass());

    @Inject
    private EntityManager em;

    @EJB
    private UserLocal current;
    @EJB
    private MDTNodeLocal mdtNodeLocal;
    @EJB
    private MDTCacheManager mdtCacheManager;
    @EJB
    private LanguageLocal languageLocal;
    @EJB
    private ReturnCacheManager returnCacheManager;

    @Inject
    private Event<ReturnFormatChangeEvent> returnFormatChangeEvent;
    @Inject
    private SubMatrixLocal subMatrixSession;

    /**
     * @see List<ReturnDefinition> load(Map<ReturnDefinitionFilter, Object> filterObjectMap)
     */
    @Deprecated
    public HashSet<ReturnDefinition> load(Map<Integer, String> propMap, boolean userDef) {
        if (!userDef) {
            Query query = em.createQuery("select rd from IN_RETURN_DEFINITIONS rd");
            Set<ReturnDefinition> result = new HashSet<ReturnDefinition>();
            result.addAll(query.getResultList());
            return (HashSet<ReturnDefinition>) result;
        }
        String queryString = "";
        if (propMap != null) {
            if (propMap.get(1) != null) {
                queryString += " and trim(rd.code) like '%" + propMap.get(1) + "%'";
            }
            if (propMap.get(3) != null) {
                queryString += " and trim(rd.returnType.code) like '%" + propMap.get(3) + "%'";
            }
            if (propMap.get(4) != null) {
                queryString += " and rd.id = " + propMap.get(4);
            }

        }
        long userId = current.getCurrentUserId();

        HashSet<ReturnDefinition> result = new HashSet<ReturnDefinition>();

        // Load User Return Definitions
        String userQlString = "select rd.id, rd.version, rd.code, rd.description, dt.id, dt.version, dt.code, dt.node.id, dt.node.version, dt.node.code, dt.type, rd.returnType.id,rd.returnType.version,rd.returnType.code,rd.returnType.description,dt.visibleLevel  from SYS_USERS as u, IN(u.returnDefinitions) as rd, IN(rd.definitionTables) as dt where u.id=:userId";
        Query loadUserReturnDefinitionsQuery = em.createQuery(userQlString + " " + queryString);
        loadUserReturnDefinitionsQuery.setParameter("userId", userId);
        result.addAll(getReturnDefinitionsFromResult(loadUserReturnDefinitionsQuery.getResultList()));

        // Load User Roles Return Definitions
        String userRolesQlString = "select rd.id, rd.version, rd.code, rd.description, dt.id, dt.version, dt.code, dt.node.id, dt.node.version, dt.node.code, dt.type, rd.returnType.id,rd.returnType.version,rd.returnType.code,rd.returnType.description,dt.visibleLevel  from SYS_USERS as u, IN(u.roles) as ur, IN(ur.returnDefinitions) rd, IN(rd.definitionTables) as dt where u.id=:userId";
        Query loadUserRolesReturnDefinitionQuery = em.createQuery(userRolesQlString + " " + queryString);
        loadUserRolesReturnDefinitionQuery.setParameter("userId", userId);
        result.addAll(getReturnDefinitionsFromResult(loadUserRolesReturnDefinitionQuery.getResultList()));

        Collections.sort(new ArrayList(result), new ReturnDefinitionComparator());

        return result;
    }

    @Override
    public List<ReturnDefinition> load(Map<ReturnDefinitionFilter, Object> filterObjectMap) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ReturnDefinition> query = cb.createQuery(ReturnDefinition.class);
        Root<ReturnDefinition> root = query.from(ReturnDefinition.class);
        root.fetch(ReturnDefinition_.returnType);

        List<Predicate> predicates = getFilterPredicates(cb, root, filterObjectMap);
        boolean loadAll = ((filterObjectMap.get(ReturnDefinitionFilter.LOAD_All) instanceof Boolean) ? (Boolean) filterObjectMap.get(ReturnDefinitionFilter.LOAD_All) : false);
        if (!loadAll) {
            List<Predicate> idPredList = DBUtil.get().buildAndSplitPredicates(cb, root.get(ReturnDefinition_.id), Long.class, current.getCallerPrincipal().getReturnDefinitions());
            predicates.add(cb.and(cb.or(idPredList.toArray(new Predicate[0]))));
        }
        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }
        SortField sortField = (SortField) filterObjectMap.get(ReturnDefinitionFilter.SORT);
        if (sortField == null) {
            sortField = new SortField("code", "ASC");
        }

        Path<String> orderBy;
        if (sortField.getProperty().equalsIgnoreCase("typeCode")) {
            orderBy = root.get(ReturnDefinition_.returnType).get(ReturnType_.code);
        } else {
            orderBy = root.get(sortField.getProperty());
        }

        query.orderBy(sortField.isAsc() ? cb.asc(orderBy) : cb.desc(orderBy));

        TypedQuery<ReturnDefinition> typedQuery = em.createQuery(query);

        if (filterObjectMap.get(ReturnDefinitionFilter.PAGE_OFFSET) != null && filterObjectMap.get(ReturnDefinitionFilter.PAGE_LIMIT) != null) {
            typedQuery.setFirstResult((int) filterObjectMap.get(ReturnDefinitionFilter.PAGE_OFFSET));
            typedQuery.setMaxResults((int) filterObjectMap.get(ReturnDefinitionFilter.PAGE_LIMIT));
        }

        return typedQuery.getResultList();
    }

    private List<Predicate> getFilterPredicates(CriteriaBuilder cb, Root<ReturnDefinition> returnDefinitionRoot, Map<ReturnDefinitionFilter, Object> filter) {
        List<Predicate> predicates = new ArrayList<>();
        long langId = ThreadLocalHolder.getLanguage() != null ? ThreadLocalHolder.getLanguage().getId() : languageLocal.getDefaultLanguage().getId();
        if (filter != null) {

            for (Map.Entry<ReturnDefinitionFilter, Object> entry : filter.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                switch (entry.getKey()) {
                    case CODE:
                        predicates.add(cb.like(returnDefinitionRoot.get(ReturnDefinition_.code), entry.getValue().toString()));
                        break;
                    case RETURN_TYPE_ID:
                        predicates.add(cb.equal(returnDefinitionRoot.get(ReturnDefinition_.returnType).get(ReturnType_.id), entry.getValue()));
                        break;
                    case IDS: {
                        List<Predicate> idPredList = DBUtil.get().buildAndSplitPredicates(cb, returnDefinitionRoot.get(ReturnDefinition_.id), Long.class, (ArrayList<Long>) entry.getValue());
                        predicates.add(cb.and(cb.or(idPredList.toArray(new Predicate[0]))));
                        break;
                    }
                    case LOAD_MANUAL_INPUT_ONLY: {
                        predicates.add(cb.equal(returnDefinitionRoot.get(ReturnDefinition_.manualInput), entry.getValue()));
                        break;
                    }
                    case CONTACT_PERSON: {
                        predicates.add(cb.like(returnDefinitionRoot.get(ReturnDefinition_.generalInfo), entry.getValue().toString()));
                        break;
                    }
                    case DESCRIPTION:
                        List<Long> rdIds = em.createQuery("select rd.id from SYS_STRINGS s inner join IN_RETURN_DEFINITIONS rd on rd.description=s.id and s.langId =:langId and s.value like :value", Long.class)
                                .setParameter("langId", langId)
                                .setParameter("value", "%" + entry.getValue().toString() + "%")
                                .getResultList();


                        predicates.add(returnDefinitionRoot.get(ReturnDefinition_.id).in(rdIds.isEmpty() ? Collections.singletonList(-1L) : rdIds));
                        break;
                }
            }
        }
        return predicates;
    }

    private Collection<ReturnDefinition> getReturnDefinitionsFromResult(List<Object[]> result) {
        Map<Long, ReturnDefinition> returnDefinitionsMap = new HashMap<>();
        for (Object[] objects : result) {

            long id = (long) objects[0];

            ReturnDefinition returnDefinition = returnDefinitionsMap.get(id);

            if (returnDefinition == null) {
                returnDefinition = new ReturnDefinition(id, (Integer) objects[1], objects[2].toString(), (Description) objects[3]);
                returnDefinition.setDefinitionTables(new ArrayList<DefinitionTable>());
                returnDefinitionsMap.put(id, returnDefinition);
            }

            DefinitionTable dt = new DefinitionTable();
            dt.setId((long) objects[4]);
            dt.setVersion((Integer) objects[5]);
            dt.setCode(objects[6] != null ? objects[6].toString() : "");
            dt.setVisibleLevel((Long) objects[15]);

            MDTNode node = new MDTNode((long) objects[7], (Integer) objects[8]);
            node.setCode(objects[9].toString());

            dt.setType((ReturnTableType) objects[10]);
            dt.setNode(node);

            ReturnType returnType = new ReturnType();
            returnType.setId((Long) objects[11]);
            returnType.setVersion((Integer) objects[12]);
            returnType.setCode(objects[13].toString());
            returnType.setDescription((Description) objects[14]);

            returnDefinition.setReturnType(returnType);

            returnDefinition.getDefinitionTables().add(dt);

        }
        return returnDefinitionsMap.values();
    }

    @Override
    public DefinitionTable loadReturnDefinitionTableById(long returnDefinitionId, long definitionTableId) {

        String qlString = "select rd.code,dt.code,dt.node.code,dt.type from IN_RETURN_DEFINITIONS rd, IN(rd.definitionTables) dt where rd.id=:returnDefinitionId and dt.id=:definitionTableId ";
        Query query = em.createQuery(qlString)
                .setParameter("returnDefinitionId", returnDefinitionId)
                .setParameter("definitionTableId", definitionTableId);

        Object[] objects = (Object[]) query.getSingleResult();

        DefinitionTable table = new DefinitionTable();

        table.setCode(objects[1].toString());

        ReturnDefinition returnDefinition = new ReturnDefinition();
        returnDefinition.setCode(objects[0].toString());
        table.setReturnDefinition(returnDefinition);

        MDTNode mdtNode = new MDTNode();
        mdtNode.setCode(objects[2].toString());

        table.setNode(mdtNode);

        table.setType(objects[3] != null ? (ReturnTableType) objects[3] : null);

        return table;
    }

    @Override

    public ReturnDefinition getDefinitionInfo(long returnId) {

        ReturnDefinition def = null;
        try {
            Query formatQuery = em.createQuery("select retDef from IN_RETURN_DEFINITIONS retDef " + "where retDef.id in (select schedules.returnDefinition.id " + "from IN_SCHEDULES schedules, IN_RETURNS rets " + "where schedules.id=rets.schedule.id and rets.id=:returnId)");
            formatQuery.setParameter("returnId", returnId);
            def = (ReturnDefinition) formatQuery.getSingleResult();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return def;
    }

    @SuppressWarnings("unchecked")
    public boolean checkDependency(Long defId) {
        boolean bool = true;
        Query dependencySchedule = em.createQuery("select s.id from IN_SCHEDULES s where s.returnDefinition.id=:id");
        dependencySchedule.setParameter("id", defId);
        List<Integer> integers = dependencySchedule.getResultList();
        List list = em.createQuery("select p from IN_PACKAGES p JOIN p.returnDefinitions rd where rd.id=:id").setParameter("id", defId).getResultList();
        if (integers.size() > 0 || list.size() > 0) {
            bool = false;
        }
        return bool;
    }


    @Override
    public void delete(List<ReturnDefinition> definitions) throws FinATypeException {
        if (definitions == null) {
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE);
        }

        List<Long> definitionIds = definitions.stream()
                .map(ReturnDefinition::getId)
                .toList();

        List<String> usedDefinitions = definitions.stream()
                .filter(def -> !checkDependency(def.getId()))
                .map(ReturnDefinition::getCode)
                .toList();

        if (!usedDefinitions.isEmpty()) {
            throw new FinATypeException(
                    FinATypeException.Type.DEPENDENCY_ERROR,
                    usedDefinitions.toArray(new String[0])
            );
        }

        List<String> userLogins = em.createQuery("select u.login from SYS_USERS u, in (u.returnDefinitions) rd where rd.id in (:definitionIds) ", String.class)
                .setParameter("definitionIds", definitionIds)
                .getResultList();

        em.createQuery("delete from SYS_USER_RETURNS where userDefinition.definitionId in (:definitionIds)")
                .setParameter("definitionIds", definitionIds)
                .executeUpdate();

        em.createNativeQuery("delete from SYS_ROLE_RETURNS where DEFINITION_ID in (:definitionIds)")
                .setParameter("definitionIds", definitionIds)
                .executeUpdate();

        deleteReturnDefinitionTables(definitionIds);

        int deletedCount = em.createQuery("delete from IN_RETURN_DEFINITIONS rd where rd.id in (:definitionIds)")
                .setParameter("definitionIds", definitionIds)
                .executeUpdate();

        returnCacheManager.remove(definitionIds);

        userLogins.forEach(SecurityUtil::flushAuthCache);

        log.info("Deleted return definitions. count: " + deletedCount);

    }

    @Override
    public void deleteReturnDefinitionTables(ReturnDefinition definition) {
        em.createQuery("delete from IN_DEFINITION_TABLES where returnDefinition.id=:definitionId")
                .setParameter("definitionId", definition.getId())
                .executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override

    public List<DefinitionTable> loadDefinitontables(Long defId) {
        Query query = em.createQuery("select new " + DefinitionTable.class.getName() + " (dt.id, dt.code, dt.node, dt.nodeVisible, dt.visibleLevel, dt.type, dt.evalType, dt.version, dt.sequence) from IN_DEFINITION_TABLES dt where dt.returnDefinition.id=:id order by dt.sequence");
        query.setParameter("id", defId);
        return query.getResultList();
    }

    @Override
    public List<DefinitionTable> loadAllDefinitionTables() {
        return em.createNamedQuery("DT.findAll", DefinitionTable.class).getResultList();
    }

    @Override
    public ReturnDefinition save(ReturnDefinition definition) throws FinATypeException {
        if (!isCodeUnique(definition.getCode(), definition.getId())) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE);
        }

        ReturnType returnType = em.find(ReturnType.class, definition.getReturnType().getId());
        if (definition.getId() > 0) {
            Collection<DefinitionTable> tables = definition.getDefinitionTables();
            List<DefinitionTable> oldTables = em.createQuery("select dt from IN_DEFINITION_TABLES dt where dt.returnDefinition.id=:defId", DefinitionTable.class)
                    .setParameter("defId", definition.getId())
                    .getResultList();

            if (oldTables.size() > definition.getDefinitionTables().size()) {
                List<DefinitionTable> tmp = new ArrayList<>(oldTables);
                tmp.removeAll(tables);
                for (DefinitionTable t : tmp) {
                    subMatrixSession.deleteTableByDefinitionTableId(t.getId());
                    em.remove(t);
                }
            }

            // Save definition tables
            for (DefinitionTable dt : tables) {
                saveDefinitionTable(dt);
            }

            definition = em.merge(definition);

        } else {

            List<DefinitionTable> tables = new ArrayList<DefinitionTable>();

            ReturnDefinition persistedReturnDefinition = new ReturnDefinition();
            persistedReturnDefinition.setCode(definition.getCode());
            persistedReturnDefinition.setDescription(definition.getDescription());
            persistedReturnDefinition.setDisable(definition.getDisable());
            persistedReturnDefinition.setGeneralInfo(definition.getGeneralInfo());
            persistedReturnDefinition.setVersion(0);
            persistedReturnDefinition.setManualInput(definition.isManualInput());
            persistedReturnDefinition.setDefinitionTables(tables);

            em.persist(persistedReturnDefinition);

            persistedReturnDefinition.setReturnType(returnType);

            for (DefinitionTable dt : definition.getDefinitionTables()) {
                dt.setReturnDefinition(persistedReturnDefinition);
                DefinitionTable table = saveDefinitionTable(dt);
                tables.add(table);
            }

            User user = em.find(User.class, current.getCurrentUserId());
            Collection<ReturnDefinition> userReturnDefinitions = user.getReturnDefinitions();
            userReturnDefinitions.add(persistedReturnDefinition);

            // Add caller principal
            current.getCallerPrincipal().getReturnDefinitions().add(persistedReturnDefinition.getId());

            definition = persistedReturnDefinition;
        }

        Map<Long, Collection<ReturnDefinition>> definitionDependencies = getReturnDefinitionsDependencies(false, definition.getId());
        returnCacheManager.update(definition.getId(), definitionDependencies.get(definition.getId()));

        return definition;
    }

    @SuppressWarnings("rawtypes")
    public boolean isCodeUnique(String code, long id) {
        Query codeUniqueQuery = em.createQuery("select rd.id from IN_RETURN_DEFINITIONS rd where trim(rd.code)=:code and rd.id <>:id");
        codeUniqueQuery.setParameter("id", id);
        codeUniqueQuery.setParameter("code", code);
        List ids = codeUniqueQuery.getResultList();
        if (ids.size() > 0)
            return false;
        return true;

    }

    @Override
    public DefinitionTable saveDefinitionTable(DefinitionTable definitionTable) throws FinATypeException {
        long returnDefinitionId = definitionTable.getReturnDefinition().getId();
        if (!isCodeUniqueDefinitionTable(definitionTable.getCode(), definitionTable.getId(), returnDefinitionId)) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE);
        }
        MDTNode mdtNode = em.find(MDTNode.class, definitionTable.getNode().getId());
        definitionTable.setNode(mdtNode);
        definitionTable.setReturnDefinition(em.find(ReturnDefinition.class, returnDefinitionId));
        if (definitionTable.getId() > 0) {
            definitionTable = em.merge(definitionTable);
        } else {
            em.persist(definitionTable);
            subMatrixSession.createTable(definitionTable.getReturnDefinition().getId(), definitionTable);
        }
        return definitionTable;
    }

    @Override
    public void deleteDefinitionTable(DefinitionTable definitionTable) throws FinATypeException {
        ReturnDefinition definition = em.find(ReturnDefinition.class, definitionTable.getReturnDefinition().getId());

        DefinitionTableId defTableId = new DefinitionTableId();
        defTableId.setId(definitionTable.getId());
        defTableId.setReturnDefinition(definition);

        DefinitionTable defTable = em.find(DefinitionTable.class, defTableId);

        subMatrixSession.deleteTableByDefinitionTableId(defTable.getId());

        em.remove(defTable);

        em.flush();
    }

    /**
     * @param code of return definition table.
     * @param id   of return definition table.
     * @param rdId of the return definition.
     * @return <b>TRUE</b> if code is unique <u>in return definition scope</u>,
     * otherwise <b>FALSE</b>
     */
    @SuppressWarnings("rawtypes")
    public boolean isCodeUniqueDefinitionTable(String code, Long id, long rdId) {
        Query codeUniqueQuery = em.createQuery("select rdt.id from IN_DEFINITION_TABLES rdt where trim(rdt.code)=:code and rdt.id <>:id and rdt.returnDefinition.id =:rdId");
        codeUniqueQuery.setParameter("id", id);
        codeUniqueQuery.setParameter("code", code);
        codeUniqueQuery.setParameter("rdId", rdId);
        List ids = codeUniqueQuery.getResultList();
        if (ids.size() > 0)
            return false;
        return true;
    }

    @Override

    public String getJson(HashMap<Integer, String> hashMap) {

        HashSet<ReturnDefinition> definitions = load(new HashMap<Integer, String>(), false);
        JSONObject rootObject = new JSONObject();
        rootObject.put("id", rootObject.hashCode());
        rootObject.put("name", "Root");
        JSONArray jsonArray = new JSONArray();
        JSONArray childArray = new JSONArray();
        for (ReturnDefinition definition : definitions) {
            JSONObject childObject = new JSONObject();
            childObject.put("id", definition.getId());
            childObject.put("name", definition.getCode());
            if (definition.getId() == Long.parseLong(hashMap.get(1))) {
                childObject.put("children", jsonArray);
            }
            childArray.put(childObject);
        }
        long id = Long.parseLong(hashMap.get(1));
        List<ReturnDefinition> list = getDependencies(id);

        for (ReturnDefinition definition : list) {
            if (definition.getId() != id) {
                JSONObject jsonObject2 = new JSONObject();
                jsonObject2.put("id", definition.getId());
                jsonObject2.put("name", definition.getCode());
                jsonArray.put(jsonObject2);
            }
        }
        rootObject.put("children", childArray);
        return rootObject.toString();
    }

    @SuppressWarnings("unchecked")
    public ArrayList<ReturnDefinition> getDependendedReturnDefinition(Long mdtNodeId) {
        ArrayList<ReturnDefinition> rezult = new ArrayList<ReturnDefinition>();

        Query getdefids = em.createQuery("select dt.returnDefinition.id from IN_DEFINITION_TABLES dt where dt.node.id=:nodeId");
        Query query = em.createQuery("select rd.id, rd.code from IN_RETURN_DEFINITIONS rd where rd.id in(:ids)");
        ArrayList<Long> longs = getParenIds(mdtNodeId);
        for (Long a : longs) {
            getdefids.setParameter("nodeId", a);
            List<Long> defIds_ = getdefids.getResultList();
            if (!defIds_.isEmpty()) {
                query.setParameter("ids", defIds_);
                List<Object[]> list = query.getResultList();
                for (Object[] rdef : list) {
                    ReturnDefinition returnDefinition = new ReturnDefinition();
                    returnDefinition.setId((Long) rdef[0]);
                    returnDefinition.setCode((String) rdef[1]);
                    rezult.add(returnDefinition);
                }
            }
        }
        return rezult;

    }

    @SuppressWarnings("unchecked")
    public ArrayList<Long> getChildNodes(Long nodeID) {
        ArrayList<Long> arrayList = new ArrayList<Long>();
        Query query = em.createQuery("select mn.id, mn.type from IN_MDT_NODES mn where mn.parentId=:parentId and mn.disabled=false");
        query.setParameter("parentId", nodeID);
        List<Object[]> nodes = query.getResultList();
        for (Object[] node : nodes) {
            arrayList.add((Long) node[0]);
            if (node[1] == MDTNodeTypes.NODE) {
                arrayList.addAll(getChildNodes((Long) node[0]));
            }
        }
        return arrayList;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<ReturnDefinition> getDependencies(Long definitionID) {
        ArrayList<ReturnDefinition> dependecies = new ArrayList<ReturnDefinition>();
        Query query = em.createQuery("select dt.node.id " + "from IN_DEFINITION_TABLES dt where  dt.returnDefinition.id =:defId");
        query.setParameter("defId", definitionID);
        List<Long> nodes = query.getResultList();
        HashSet<Long> childNodes = new HashSet<Long>();
        for (Long mdtNodeId : nodes) {
            childNodes.add(mdtNodeId);
            childNodes.addAll(getChildNodes(mdtNodeId));
        }
        if (!childNodes.isEmpty()) {
            Query dependenNodes = em.createQuery("select a.depNode.nodeId " + "from IN_MDT_DEPENDENT_NODES a " + "where a.depNode.dependentNodeId in (:ids)");
            dependenNodes.setParameter("ids", childNodes);

            List<Long> nodeids = dependenNodes.getResultList();
            for (Long id : nodeids) {
                dependecies.addAll(getDependendedReturnDefinition(id));
            }
        }
        return dependecies;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Long> getParenIds(Long id) {
        List<Long> rezult = new ArrayList<Long>();
        rezult.add(id);
        List<Long> parentids = new ArrayList<Long>();
        Query query = em.createQuery("select n.parentId from IN_MDT_NODES n where n.id=:id");
        query.setParameter("id", id);
        parentids = query.getResultList();
        while (!parentids.isEmpty()) {
            rezult.add(parentids.get(0));
            query.setParameter("id", parentids.get(0));
            parentids = query.getResultList();
        }
        return (ArrayList<Long>) rezult;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UserReturnDefinition> userReturnDefinitions(Long userId) {
        Query query = em.createQuery("select ud from SYS_USER_RETURNS ud where ud.userDefinition.userId=:userId");
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    @LogDescription(ignore = true)
    public Mdt loadReturnDefinitionMdtV1(
            ReturnDefinition returnDefinition,
            Map<String, Boolean> activate,
            Map<Long, List<MDTComparison>> comparisons,
            Map<Long, String> languagesMap,
            Map<Long, Set<Long>> dependencies,
            Map<Long, List<MDTNode>> allMdtNodesByParentIde
    ) {

        Collection<DefinitionTable> definitionTables = returnDefinition.getDefinitionTables();
        log.info("Generating xml definition code : " + returnDefinition.getCode());
        List<Node> nodes = new ArrayList<>();
        for (DefinitionTable dt : definitionTables) {
            MDTNode root = mdtCacheManager.getMdtNodesById().get(dt.getNode().getId());
            loadDefinitionTableNodes(root);
            Optional opt = null;
            if (activate.get("optional")) {
                opt = new Optional();
                opt.setTableId(dt.getId());
                if (dt.getType() != null) {
                    opt.setTableType(dt.getType().ordinal());
                }
                if (dt.getEvalType() != null) {
                    opt.setEvalType(dt.getEvalType().ordinal());
                }
            }
            mdtNodeToXmlNodeV1(root, nodes, dependencies, comparisons, languagesMap, opt, activate);
        }
        Mdt mdt = new Mdt();
        mdt.setNodes(nodes);
        log.info("Generated xml definition code : " + returnDefinition.getCode());

        return mdt;
    }

    @Override
    @LogDescription(ignore = true)
    public net.fina.server.mdt.xml.v2.Node loadReturnDefinitionMdtV2(
            ReturnDefinition returnDefinition,
            Map<String, Boolean> activate,
            Map<Long, List<MDTComparison>> comparisons,
            Map<Long, String> languagesMap,
            Map<Long, Set<String>> dependencies,
            Map<Long, List<MDTNode>> allMdtNodesByParentId
    ) {
        Collection<DefinitionTable> definitionTables = returnDefinition.getDefinitionTables();

        net.fina.server.mdt.xml.v2.Node node = new net.fina.server.mdt.xml.v2.Node();
        node.setCode(returnDefinition.getCode());
        node.setType(MDTNodeTypes.NODE);
        node.setChildren(new ArrayList<>());
        for (DefinitionTable dt : definitionTables) {
            net.fina.server.mdt.xml.v2.Optional optional = null;
            if (activate.get("optional")) {
                optional = new net.fina.server.mdt.xml.v2.Optional();
                optional.setTableCode(dt.getCode());
                optional.setTableType(dt.getType());
                optional.setEvalType(dt.getEvalType());
                optional.setTableSequence(dt.getSequence());
            }
            MDTNode root = dt.getNode();
            net.fina.server.mdt.xml.v2.Node rootNode = mdtNodeToXmlNodeV2(root, dependencies, comparisons, languagesMap, activate, optional);
            loadDefinitionTableNodesV2(root, rootNode, dependencies, comparisons, languagesMap, activate, allMdtNodesByParentId, optional);
            node.getChildren().add(rootNode);
        }
        return node;
    }

    @Override
    public List<ReturnDefinition> loadReturnDefinitionsById(List<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        String inStatement = DBUtil.get().generateConcatenatedInStatementWithIds("r.id", ids);
        String query = "select r from IN_RETURN_DEFINITIONS r " +
                "where " +
                inStatement;
        List<ReturnDefinition> result = em.createQuery(query, ReturnDefinition.class).getResultList();

        result.forEach(r -> r.getDefinitionTables().size());
        return result;
    }

    @Override
    public ReturnDefinition getReturnDefinitionById(long id) throws FinATypeException {
        if (!current.getCallerPrincipal().getReturnDefinitions().contains(id)) {
            throw new FinATypeException("No Permission");
        }
        return em.find(ReturnDefinition.class, id);
    }

    @Override
    public List<ReturnDefinition> loadReturnDefinitionsByTableType(ReturnTableType type) {
        return em.createQuery("SELECT DISTINCT rd FROM IN_RETURN_DEFINITIONS rd JOIN rd.definitionTables dt WHERE rd.id in (:userRdIds) AND dt.type=:type", ReturnDefinition.class)
                .setParameter("userRdIds", current.getCallerPrincipal().getReturnDefinitions())
                .setParameter("type", type)
                .getResultList();
    }

    @Override
    public String getReturnDefinitionCodeById(long id) throws FinATypeException {
        if (!current.getCallerPrincipal().getReturnDefinitions().contains(id)) {
            throw new FinATypeException("No Permission");
        }
        return em.createQuery("select rd.code from IN_RETURN_DEFINITIONS  rd where rd.id=:definitionId", String.class).setParameter("definitionId", id).getSingleResult();
    }

    @Override
    public long getReturnDefinitionIdByCode(String code) {
        return em.createQuery("select rd.id from IN_RETURN_DEFINITIONS rd where rd.code=:definitionCode", Long.class)
                .setParameter("definitionCode", code)
                .getSingleResult();
    }

    @Override
    public List<ReturnDefinition> loadReturnDefinitionsByReturnTypeAndTableType(Long returnTypeId, ReturnTableType tableType) {
        return em.createQuery("SELECT DISTINCT rd FROM IN_RETURN_DEFINITIONS rd JOIN rd.definitionTables dt WHERE rd.returnType.id=:returnTypeId and rd.id in (:userRdIds) AND dt.type=:type", ReturnDefinition.class)
                .setParameter("userRdIds", current.getCallerPrincipal().getReturnDefinitions())
                .setParameter("type", tableType)
                .setParameter("returnTypeId", returnTypeId)
                .getResultList();
    }

    @Override
    public int count(Map<ReturnDefinitionFilter, Object> filterObjectMap) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ReturnDefinition> root = query.from(ReturnDefinition.class);
        query.select(cb.count(root));

        List<Predicate> predicates = getFilterPredicates(cb, root, filterObjectMap);
        boolean loadAll = ((filterObjectMap.get(ReturnDefinitionFilter.LOAD_All) instanceof Boolean) ? (Boolean) filterObjectMap.get(ReturnDefinitionFilter.LOAD_All) : false);
        if (!loadAll) {
            List<Predicate> idPredList = DBUtil.get().buildAndSplitPredicates(cb, root.get(ReturnDefinition_.id), Long.class, current.getCallerPrincipal().getReturnDefinitions());
            predicates.add(cb.and(cb.or(idPredList.toArray(new Predicate[0]))));
        }
        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }

        TypedQuery<Long> typedQuery = em.createQuery(query);

        return typedQuery.getSingleResult().intValue();
    }

    public Map<String, ReturnDefinition> loadReturnDefinitionCodeObjectMap() {
        return em.createQuery("select r.code,r  from IN_RETURN_DEFINITIONS  r ", Tuple.class)
                .getResultStream().collect(
                        Collectors.toMap(
                                tuple -> (String) tuple.get(0),
                                tuple -> ((ReturnDefinition) tuple.get(1))
                        )
                );
    }

    @Override
    public List<ReturnDefinition> loadReturnDefinitionsByReturnType(List<Long> returnTypeIds) {
        if (returnTypeIds == null || returnTypeIds.isEmpty()) return new ArrayList<>();

        return em.createQuery("select new " + ReturnDefinition.class.getName() + " (rd.id,rd.code) from IN_RETURN_DEFINITIONS rd where rd.returnType.id in (:returnTypeIds)", ReturnDefinition.class)
                .setParameter("returnTypeIds", returnTypeIds)
                .getResultList();

    }

    @Override
    public List<ReturnDefinition> loadActiveReturnDefinitions() {
        return em.createQuery("select rd from IN_RETURN_DEFINITIONS rd where rd.disable=false or disable is null ", ReturnDefinition.class)
                .getResultList();
    }

    private void loadDefinitionTableNodes(MDTNode mdtNode) {
        List<MDTNode> mdtNodes = mdtNodeLocal.loadMdtNodesByParentId(mdtNode.getId());
        if (mdtNodes != null) {
            for (MDTNode node : mdtNodes) {
                loadDefinitionTableNodes(node);
            }
            mdtNode.setChildren(mdtNodes);
        }
    }

    private void loadDefinitionTableNodesV2(MDTNode mdtNode, net.fina.server.mdt.xml.v2.Node nodes, Map<Long, Set<String>> dependencies, Map<Long, List<MDTComparison>> comparisonsMap, Map<Long, String> languagesMap, Map<String, Boolean> activate, Map<Long, List<MDTNode>> allMdtNodesByParentId, net.fina.server.mdt.xml.v2.Optional optional) {
        List<MDTNode> mdtNodes = allMdtNodesByParentId.get(mdtNode.getId());
        List<net.fina.server.mdt.xml.v2.Node> nodeList = new ArrayList<>();
        if (mdtNodes != null) {
            for (MDTNode node : mdtNodes) {
                net.fina.server.mdt.xml.v2.Node n = mdtNodeToXmlNodeV2(node, dependencies, comparisonsMap, languagesMap, activate, optional);
                nodeList.add(n);
                loadDefinitionTableNodesV2(node, n, dependencies, comparisonsMap, languagesMap, activate, allMdtNodesByParentId, optional);
            }
            nodes.setChildren(nodeList);
        }
    }

    private void mdtNodeToXmlNodeV1(MDTNode mdtNode, List<Node> nodes, Map<Long, Set<Long>> dependencies, Map<Long, List<MDTComparison>> comparisonsMap, Map<Long, String> languagesMap, Optional opt, Map<String, Boolean> activate) {
        if (!mdtNode.isDisabled()) {
            Node node = mdtNodeToXmlNodeV1(mdtNode, dependencies, comparisonsMap, languagesMap, activate);
            if (opt != null) {
                node.setOptional(opt);
            }
            nodes.add(node);
            for (MDTNode n : mdtNode.getChildren()) {
                mdtNodeToXmlNodeV1(n, nodes, dependencies, comparisonsMap, languagesMap, opt, activate);
            }
        }
    }

    private Node mdtNodeToXmlNodeV1(MDTNode mdtNode, Map<Long, Set<Long>> dependencies, Map<Long, List<MDTComparison>> comparisonsMap, Map<Long, String> languagesMap, Map<String, Boolean> activate) {
        Node node = new Node();

        if (activate.get("id")) {
            node.setId(mdtNode.getId());
        }

        if (activate.get("code")) {
            node.setCode(mdtNode.getCode());
        }

        if (activate.get("parentId")) {
            node.setParentId(mdtNode.getParentId());
        }

        if (activate.get("type")) {
            node.setType(mdtNode.getType().ordinal());
        }

        if (activate.get("dataType")) {
            node.setDataType(mdtNode.getDataType().ordinal());
        }

        if (activate.get("equation")) {
            node.setEquation(mdtNode.getEquation());
        }

        if (activate.get("sequence")) {
            node.setSequence(mdtNode.getSequence());
        }

        if (activate.get("disabled")) {
            node.setDisabled(mdtNode.isDisabled() ? 1 : 0);
        }

        if (activate.get("required")) {
            node.setRequired(mdtNode.isRequired() ? 1 : 0);
        }

        if (activate.get("dependents")) {
            List<Long> dependentNodes = new ArrayList<Long>();
            if (dependencies.containsKey(mdtNode.getId())) {
                dependentNodes.addAll(dependencies.get(mdtNode.getId()));
            }
            node.setDependentNodes(dependentNodes);
        }

        if (activate.get("descriptions")) {
            List<net.fina.server.mdt.xml.v1.Description> descriptions = new ArrayList<>();
            for (Entry<Long, String> e : mdtNode.getDescription().getDescriptions().entrySet()) {
                net.fina.server.mdt.xml.v1.Description d = new net.fina.server.mdt.xml.v1.Description();
                d.setLangCode(languagesMap.get(e.getKey()));
                d.setValue(e.getValue());

                descriptions.add(d);
            }
            node.setDescriptions(descriptions);
        }

        if (activate.get("comparisons")) {
            List<Comparison> nodeComparisons = new ArrayList<>();
            if (comparisonsMap.containsKey(mdtNode.getId())) {
                for (MDTComparison comparison : comparisonsMap.get(mdtNode.getId())) {
                    Comparison c = new Comparison();
                    c.setId(comparison.getId());
                    c.setEquation(comparison.getRightEquation());
                    c.setCondition(comparison.getCondition() != null ? comparison.getCondition().ordinal() : 0);
                    c.setNodeId(comparison.getNode().getId());

                    nodeComparisons.add(c);
                }
            }
            node.setComparisons(nodeComparisons);
        }
        return node;
    }

    private net.fina.server.mdt.xml.v2.Node mdtNodeToXmlNodeV2(MDTNode mdtNode, Map<Long, Set<String>> dependencies, Map<Long, List<MDTComparison>> comparisonsMap, Map<Long, String> languagesMap, Map<String, Boolean> activate, net.fina.server.mdt.xml.v2.Optional optional) {
        net.fina.server.mdt.xml.v2.Node node = new net.fina.server.mdt.xml.v2.Node();
        if (activate.get("code")) {
            node.setCode(mdtNode.getCode());
        }
        if (activate.get("type")) {
            node.setType(mdtNode.getType());
        }
        if (activate.get("dataType")) {
            node.setDataType(mdtNode.getDataType());
        }
        if (activate.get("equation")) {
            node.setEquation(mdtNode.getEquation());
        }
        if (activate.get("sequence")) {
            node.setSequence(mdtNode.getSequence());
        }
        if (activate.get("disabled")) {
            node.setDisabled(mdtNode.isDisabled());
        }
        if (activate.get("required")) {
            node.setRequired(mdtNode.isRequired());
        }
        if (activate.get("parentId")) {
            node.setParentId(mdtNode.getParentId());
        }

        if (activate.get("dependents")) {
            List<String> dependentNodes = new ArrayList<String>();
            if (dependencies.containsKey(mdtNode.getId())) {
                dependentNodes.addAll(dependencies.get(mdtNode.getId()));
            }
            node.setDependentNodeCodes(dependentNodes);
        }

        if (activate.get("descriptions")) {
            List<net.fina.server.mdt.xml.v2.Description> descriptions = new ArrayList<>();
            for (Entry<Long, String> e : mdtNode.getDescription().getDescriptions().entrySet()) {
                net.fina.server.mdt.xml.v2.Description d = new net.fina.server.mdt.xml.v2.Description();
                d.setLangCode(languagesMap.get(e.getKey()));
                d.setValue(e.getValue());
                descriptions.add(d);
            }
            node.setDescriptions(descriptions.toArray(new net.fina.server.mdt.xml.v2.Description[0]));
        }

        if (activate.get("comparisons")) {
            List<net.fina.server.mdt.xml.v2.Comparison> nodeComparisons = new ArrayList<>();
            if (comparisonsMap.containsKey(mdtNode.getId())) {
                for (MDTComparison comparison : comparisonsMap.get(mdtNode.getId())) {
                    net.fina.server.mdt.xml.v2.Comparison c = new net.fina.server.mdt.xml.v2.Comparison();
                    c.setLeftEquation(comparison.getLeftEquation());
                    c.setRightEquation(comparison.getRightEquation());
                    c.setCondition(comparison.getCondition());
                    c.setNumberPattern(comparison.getNumberPattern());
                    c.setTemplate(comparison.getTemplate());
                    nodeComparisons.add(c);
                }
            }
            node.setComparisons(nodeComparisons);
        }

        if (optional != null) {
            node.setOptional(optional);
        }

        return node;
    }

    @Override
    public ReturnDefinitionFormat getReturnDefinitionFormat(long returnDefinitionId) {
        return em.find(ReturnDefinitionFormat.class, returnDefinitionId);
    }

    @Override
    public Map<Long, ReturnDefinitionFormat> getReturnDefinitionFormatsByReturnIds(List<Long> returnIds) {
        Map<Long, ReturnDefinitionFormat> result = new HashMap<>();
        if (returnIds != null && (!returnIds.isEmpty())) {
            List<ReturnDefinitionFormat> queryResult = em.createQuery("select rdFormat from IN_RETURNS r, ReturnDefinitionFormat rdFormat where rdFormat.id = r.schedule.returnDefinition.id and r.id in (:returnIds)", ReturnDefinitionFormat.class)
                    .setParameter("returnIds", returnIds)
                    .getResultList();
            for (ReturnDefinitionFormat format : queryResult) {
                result.put(format.getId(), format);
            }
        }
        return result;
    }

    @Override
    public ReturnDefinitionFormat saveReturnDefinitionFormat(ReturnDefinitionFormat returnDefinitionFormat) {
        try {
            return em.merge(returnDefinitionFormat);
        } finally {
            returnFormatChangeEvent.fire(new ReturnFormatChangeEvent(returnDefinitionFormat.getId()));
        }
    }

    @Override
    public List<DependenceModel> getDependencies(List<ReturnDefinition> definitionList) {
        Map<Long, Collection<ReturnDefinition>> cachedDependenies = returnCacheManager.loadReturnDefinitionDependencies();
        final Collection<Long> userDefinitions = current.getCallerPrincipal().getReturnDefinitions();

        // to save user's definitions dependencies
        Map<Long, Set<Long>> dependenciesMap = new HashMap<>();

        if (definitionList == null) {
            for (Long userDefId : userDefinitions) {
                Set<Long> depsSet = new HashSet<>();

                Collection<ReturnDefinition> ids = cachedDependenies.get(userDefId);
                if (ids != null) {
                    for (ReturnDefinition depId : ids) {
                        if (userDefinitions.contains(depId.getId())) {
                            depsSet.add(depId.getId());
                        }
                    }
                }
                dependenciesMap.put(userDefId, depsSet);
            }
        } else {
            for (ReturnDefinition definition : definitionList) {
                long defId = definition.getId();
                if (userDefinitions.contains(defId)) {
                    Set<Long> depsSet = new HashSet<>();

                    Collection<ReturnDefinition> ids = cachedDependenies.get(defId);
                    if (ids != null) {
                        for (ReturnDefinition depId : ids) {
                            if (userDefinitions.contains(depId.getId())) {
                                depsSet.add(depId.getId());
                            }
                        }
                    }
                    dependenciesMap.put(defId, depsSet);
                }
            }
            // fill depended on;
            // if dependenciesMap.size equals usersDefinitions.size this means
            // that all dependencies will be built anyway.
            if (dependenciesMap.size() < userDefinitions.size()) {
                Set<Long> dependenciesMapKeySet = new HashSet<>(dependenciesMap.keySet());
                for (long dependedOnId : dependenciesMapKeySet) {
                    for (long userDefId : userDefinitions) {
                        if (dependedOnId == userDefId) {
                            continue;
                        }
                        Collection<ReturnDefinition> definitions = cachedDependenies.get(userDefId);
                        if (definitions != null) {
                            boolean contains = false;
                            for (ReturnDefinition definition : definitions) {
                                contains |= definition.getId() == dependedOnId;
                            }
                            if (contains) {
                                Set<Long> depOnSet = dependenciesMap.get(userDefId);
                                if (depOnSet == null) {
                                    depOnSet = new HashSet<>();
                                    dependenciesMap.put(userDefId, depOnSet);
                                }
                                depOnSet.add(dependedOnId);
                            }
                        }
                    }
                }
            }
        }

        // all used return definitions ids
        Set<Long> allIds = new HashSet<>();
        for (Entry<Long, Set<Long>> entry : dependenciesMap.entrySet()) {
            allIds.add(entry.getKey());
            allIds.addAll(entry.getValue());
        }

        Map<ReturnDefinitionFilter, Object> filterObjectMap = new HashMap<>();
        if (allIds.size() == userDefinitions.size()) {
            filterObjectMap.put(ReturnDefinitionFilter.LOAD_All, true);
        } else {
            filterObjectMap.put(ReturnDefinitionFilter.IDS, new ArrayList<Long>(new ArrayList<>(allIds)));
        }

        List<DependenceModel> dependenceModels = new ArrayList<>();
        for (ReturnDefinition definition : this.load(filterObjectMap)) {
            DependenceModel model = new DependenceModel();
            dependenceModels.add(model);

            model.setId(String.valueOf(definition.getId()));
            model.setName(definition.getCode());
            model.setReturnType(definition.getReturnType().getCode());

            Set<Long> idSet = dependenciesMap.get(definition.getId());
            if (idSet != null) {
                List<String> ids = new ArrayList<>();
                for (Long aLong : idSet) {
                    ids.add(String.valueOf(aLong));
                }
                model.setDependentId(ids.toArray(new String[0]));
            }
        }

        return dependenceModels;
    }


    @Override
    @SuppressWarnings("JpaQlInspection")
    @TransactionTimeout(unit = TimeUnit.MINUTES, value = 10)
    public Map<Long, Collection<ReturnDefinition>> getReturnDefinitionsDependencies(boolean excludeDisableReturnDefinitions, long definitionId) {
        Map<Long, Collection<ReturnDefinition>> dependencies = new HashMap<>();

        String sql = "select new " + ReturnDefinition.class.getName() + "(rd.id, rd.version, rd.code, rd.description, rd.returnType.id, rd.returnType.code, rd.returnType.description, dt.node.id)" +
                " from IN_RETURN_DEFINITIONS rd, IN(rd.definitionTables) dt ";

        String where = "";
        if (excludeDisableReturnDefinitions) {
            where += " where (rd.disable is null or rd.disable = false)";
        }

        if (definitionId > 0) {
            if (where.isEmpty()) {
                where = " where rd.id = :returnDefinitionId";
            } else {
                where += " and rd.id = :returnDefinitionId";
            }
        }

        sql += where;

        TypedQuery<ReturnDefinition> definitionTypedQuery = em.createQuery(sql, ReturnDefinition.class);

        if (definitionId > 0) {
            definitionTypedQuery.setParameter("returnDefinitionId", definitionId);
        }

        List<ReturnDefinition> returnDefinitionNodes = definitionTypedQuery.getResultList();

        Map<Long, List<Long>> returnDefinitionNodesListMap = new HashMap<>();
        Map<Long, List<Long>> nodeIdAndReturnDefinitionId = new HashMap<>();
        Map<Long, ReturnDefinition> idDefinitionMap = new HashMap<>();

        log.info("Return Definition Size : " + returnDefinitionNodes.size());

        int counter = 0;

        Map<Long, List<Long>> nodesMap = new HashMap<>();

        for (ReturnDefinition returnDefinition : returnDefinitionNodes) {
            counter++;
            if (counter % 100 == 0) {
                log.info(MessageFormat.format("Return Definition cache {0}/{1}", counter, returnDefinitionNodes.size()));
            }
            long nodeId = returnDefinition.getNodeId();
            long returnDefinitionId = returnDefinition.getId();
            idDefinitionMap.put(returnDefinitionId, returnDefinition);

            List<Long> returnDefinitionNodeIds = returnDefinitionNodesListMap.computeIfAbsent(returnDefinitionId, k -> new ArrayList<>());

            if (!nodesMap.containsKey(nodeId)) {
                nodesMap.put(nodeId, returnDefinitionNodeIds);
                mdtNodeLocal.loadNodeChildren(returnDefinitionNodeIds, nodeId);
            } else {
                returnDefinitionNodeIds.addAll(nodesMap.get(nodeId));
            }


            for (Long id : returnDefinitionNodeIds) {
                List<Long> nodeReturnDefinitions = nodeIdAndReturnDefinitionId.computeIfAbsent(id, k -> new ArrayList<>());
                nodeReturnDefinitions.add(returnDefinitionId);
            }
        }

        for (Entry<Long, List<Long>> entry : returnDefinitionNodesListMap.entrySet()) {
            Set<ReturnDefinition> dependentReturn = new HashSet<>();
            if (!entry.getValue().isEmpty()) {
                List<List<Long>> partitions = CommonUtil.partitions(entry.getValue());

                TypedQuery<Long> query = em.createQuery("select mdn.depNode.nodeId from IN_MDT_DEPENDENT_NODES mdn where mdn.depNode.dependentNodeId in(:nodes) and mdn.depNode.nodeId not in(:nodes)", Long.class);

                List<Long> dependentNodes = new ArrayList<>();
                for (List<Long> nodes : partitions) {
                    query.setParameter("nodes", nodes);
                    dependentNodes.addAll(query.getResultList());
                }

                for (long dependentNodeId : dependentNodes) {
                    List<Long> dependentReturnIds = nodeIdAndReturnDefinitionId.get(dependentNodeId);
                    if (dependentReturnIds != null) {
                        for (Long dependentReturnId : dependentReturnIds) {
                            ReturnDefinition depRet = idDefinitionMap.get(dependentReturnId);
                            if (depRet.getId() != entry.getKey()) {
                                dependentReturn.add(idDefinitionMap.get(dependentReturnId));
                            }
                        }
                    }
                }
            }
            dependencies.put(entry.getKey(), dependentReturn);
//            dependentReturn.removeIf(returnDefinition -> entry.getKey().equals(returnDefinition.getId()));
        }
        return dependencies;
    }

    @Override

    public Map<Long, String> gerReturnDefinitionIds(List<String> returnDefinitionCodes) {
        Map<Long, String> result = new HashMap<>();
        if (returnDefinitionCodes != null && (!returnDefinitionCodes.isEmpty())) {
            List<Object[]> objectList = em.createQuery("select rd.id,rd.code from IN_RETURN_DEFINITIONS rd where trim(rd.code) in(:returnDefinitionCodes)").setParameter("returnDefinitionCodes", returnDefinitionCodes).getResultList();
            for (Object[] objects : objectList) {
                result.put((long) objects[0], objects[1].toString());
            }
        }
        return result;
    }

    @Override

    public List<String> loadDefinitionCodes() {
        return em.createQuery("SELECT trim(rd.code) from IN_RETURN_DEFINITIONS rd").getResultList();
    }

    @Override
    @LogDescription(name = "Order processed return definition ids")
    public List<DependencyItem> orderReturnDefinitionIds(Map<Long, Long> definitionIdReturnId) {
        List<DependencyItem> items = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : definitionIdReturnId.entrySet()) {
            Collection<ReturnDefinition> definitions = returnCacheManager.loadReturnDefinitionDependencies().get(entry.getKey());
            List<Long> dependentIds = new ArrayList<>();
            for (ReturnDefinition returnDefinition : definitions) {
                if (definitionIdReturnId.get(returnDefinition.getId()) != null) {
                    dependentIds.add(returnDefinition.getId());
                }
            }
            DependencyItem item = new DependencyItem();
            item.id = entry.getKey();
            item.externalId = entry.getValue();
            item.dependentIds.addAll(dependentIds);
            items.add(item);
        }

        DependencyItemTopologicalSort topologicalSort = new DependencyItemTopologicalSort();
        topologicalSort.DFS(items);
        return items;
    }

    @Override
    public Map<String, Integer> orderReturnDefinitionIds(List<String> returnDefinitionCodes) {
        Map<Long, String> returnDefinitionCodeIds = gerReturnDefinitionIds(returnDefinitionCodes);
        List<DependencyItem> items = new ArrayList<>();
        for (Map.Entry<Long, String> entry : returnDefinitionCodeIds.entrySet()) {
            Collection<ReturnDefinition> definitions = returnCacheManager.loadReturnDefinitionDependencies().get(entry.getKey());
            List<Long> dependentIds = new ArrayList<>();
            if (definitions != null) {
                for (ReturnDefinition returnDefinition : definitions) {
                    if (returnDefinitionCodeIds.get(returnDefinition.getId()) != null) {
                        dependentIds.add(returnDefinition.getId());
                    }
                }
            }
            DependencyItem item = new DependencyItem();
            item.id = entry.getKey();
            item.code = entry.getValue();
            item.dependentIds.addAll(dependentIds);
            items.add(item);
        }

        DependencyItemTopologicalSort topologicalSort = new DependencyItemTopologicalSort();
        topologicalSort.DFS(items);

        Map<String, Integer> result = new HashMap<>();
        for (DependencyItem item : items) {
            result.put(item.code, item.end);
        }
        return result;
    }

    @Override
    public Map<Long, Collection<Long>> loadReturnDefinitionDependenciesIds() {
        Map<Long, Collection<Long>> result = new HashMap<>();
        for (Map.Entry<Long, Collection<ReturnDefinition>> entry : returnCacheManager.loadReturnDefinitionDependencies().entrySet()) {
            Collection<Long> returnDefinitions = new ArrayList<>();
            for (ReturnDefinition rd : entry.getValue()) {
                returnDefinitions.add(rd.getId());
            }
            result.put(entry.getKey(), returnDefinitions);
        }
        return result;
    }

    @Override
    public void getUsedReturnDefinitions(long returnDefinitionId, Set<Long> result) {
        if (!result.contains(returnDefinitionId)) {

            Map<Long, Collection<Long>> returnDefinitionDependenciesIds = loadReturnDefinitionDependenciesIds();

            for (Map.Entry<Long, Collection<Long>> entry : returnDefinitionDependenciesIds.entrySet()) {

                if (entry.getValue().contains(returnDefinitionId)) {
                    long tmpDefinitionId = entry.getKey();
                    result.add(tmpDefinitionId);
                    getUsedReturnDefinitions(tmpDefinitionId, result);
                }

            }
        }
    }

    @Override
    public Collection<Long> getReturnDefinitionDependencies(long returnDefinitionId) {
        Map<Long, Collection<Long>> returnDefinitionDependenciesIds = loadReturnDefinitionDependenciesIds();
        Collection<Long> result = new ArrayList<>();
        getReturnDefinitionDependencies(returnDefinitionDependenciesIds, returnDefinitionId, result);
        return result;
    }

    @Override
    @SuppressWarnings("JpaQlInspection")
    public void reorderDefinitionTables(long definitionId, List<Long> sortedTableIds) {
        List<DefinitionTable> tables = em.createQuery(
                        "select dt from IN_DEFINITION_TABLES dt where dt.id in :ids and dt.returnDefinition.id=:definitionId", DefinitionTable.class)
                .setParameter("ids", sortedTableIds)
                .setParameter("definitionId", definitionId)
                .getResultList();

        Map<Long, DefinitionTable> tableMap = tables.stream()
                .collect(Collectors.toMap(DefinitionTable::getId, Function.identity()));

        for (int i = 0; i < sortedTableIds.size(); i++) {
            Long id = sortedTableIds.get(i);
            DefinitionTable table = tableMap.get(id);
            if (table != null) {
                table.setSequence(i);
            }
        }
    }

    @Override
    public Map<ReturnType, List<ReturnDefinition>> loadRTypeDefinitionMapByIds(List<Long> definitionIds) {
        List<ReturnDefinition> returnDefinitions = em.createQuery("select r from IN_RETURN_DEFINITIONS r where r.id in(:rdIds)", ReturnDefinition.class)
                .setParameter("rdIds", definitionIds)
                .getResultList();

        Map<ReturnType, List<ReturnDefinition>> result = new HashMap<>();

        for (ReturnDefinition rd : returnDefinitions) {
            result.computeIfAbsent(rd.getReturnType(), k -> new ArrayList<>()).add(rd);
        }

        return result;

    }

    @Override
    public List<ReturnDefinition> loadReturnDefinitionsByIds(Collection<Long> ids) {
        return em.createQuery("select r from IN_RETURN_DEFINITIONS r where r.id in(:ids)", ReturnDefinition.class)
                .setParameter("ids", ids)
                .getResultList();
    }


    private void getReturnDefinitionDependencies(Map<Long, Collection<Long>> returnDefinitionDependenciesIds, long returnDefinitionId, Collection<Long> result) {
        Collection<Long> dependencies = returnDefinitionDependenciesIds.get(returnDefinitionId);
        if (dependencies == null) {
            return;
        }
        for (long dependency : dependencies) {
            getReturnDefinitionDependencies(returnDefinitionDependenciesIds, dependency, result);
        }
        result.addAll(dependencies);
    }


    private void deleteReturnDefinitionTables(List<Long> definitionIds) {
        em.createQuery("delete from IN_DEFINITION_TABLES where returnDefinition.id in (:definitionIds)")
                .setParameter("definitionIds", definitionIds)
                .executeUpdate();
    }


    static class ReturnDefinitionComparator implements Comparator<ReturnDefinition> {
        @Override
        public int compare(ReturnDefinition a, ReturnDefinition b) {
            return a.getCode().compareTo(b.getCode());
        }
    }
}
