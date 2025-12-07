package net.fina.server.fi.impl;

import jakarta.annotation.Resource;
import jakarta.ejb.*;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.FiBranchFilter;
import net.fina.common.client.filter.FiFilter;
import net.fina.common.client.fis.FiBranchTypeCounterModel;
import net.fina.common.client.fis.FiImportResult;
import net.fina.common.client.fis.FiImportWrapper;
import net.fina.common.client.fis.FiTypeSimpleModel;
import net.fina.common.server.util.CommonUtil;
import net.fina.messages.MessagesUtil;
import net.fina.security.auth.CustomPrincipal;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.fi.api.*;
import net.fina.server.fi.entity.*;
import net.fina.server.fi.event.WsFiEvent;
import net.fina.server.fi.event.WsFiEventType;
import net.fina.server.fi.util.FiBeneficiaryFilterType;
import net.fina.server.fi.util.FiBranchAndManagementTypeValidator;
import net.fina.server.fi.xml.FiXmlHelper;
import net.fina.server.fi.xml.FiXmlParser;
import net.fina.server.fi.xml.Fis;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.helper.Description;
import net.fina.server.i18n.impl.LanguageListSingleton;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.legalperson.api.LegalPersonLocal;
import net.fina.server.legalperson.entity.*;
import net.fina.server.legalperson.entity.metainfo.*;
import net.fina.server.legalperson.model.PersonFilter;
import net.fina.server.license.entity.*;
import net.fina.server.mdt.api.MDTDataNodeCatalogSourceType;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.mdt.proxy.MDTDataNodeCatalogProxySession;
import net.fina.server.person.api.PersonLocal;
import net.fina.server.person.entity.CriminalRecord;
import net.fina.server.person.entity.Person;
import net.fina.server.person.entity.PersonPosition;
import net.fina.server.person.entity.Person_;
import net.fina.server.person.model.ShareMetaModel;
import net.fina.server.returns.entity.Schedule;
import net.fina.server.returns.entity.Schedule_;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.User;
import net.fina.server.security.entity.User_;
import net.fina.server.util.DBUtil;
import net.fina.server.util.RegionUtil;
import org.apache.commons.lang.StringUtils;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Stateless
@Local(FiLocal.class)
@Interceptors(RecordingAuditor.class)
@SuppressWarnings("JpaQlInspection")
public class FiSession implements FiLocal {

    private final Logger log = Logger.getLogger(getClass());

    @Inject
    private EntityManager em;
    @EJB
    private UserLocal current;
    @EJB
    private RegionLocal regionLocal;
    @Inject
    private LanguageLocal languageLocal;
    @Inject
    private LanguageListSingleton languageListSingleton;
    @Inject
    private PeerGroupLocal peerGroupLocal;
    @Inject
    private MDTDataNodeCatalogProxySession mdtDataNodeCatalogProxySession;

    @Inject
    private LegalPersonLocal legalPersonLocal;

    @Inject
    private PersonLocal personLocal;
    @Inject
    private FiBranchTypeLocal fiBranchTypeLocal;
    @Inject
    private FiBeneficiaryLocal beneficiaryLocal;

    @Resource
    private SessionContext sessionContext;


    @Inject
    private Event<WsFiEvent> fiImportEvent;


    @Override
    public List<Fi> load(Map<FiFilter, Object> fiFilterObjectMap) {
        return load(fiFilterObjectMap, -1, -1, languageLocal.getDefaultLanguage().getId());
    }

    @Override
    @SuppressWarnings("JpaQlInspection")
    public List<Fi> loadAllFis(Boolean excludeDisabled) {

        String sql = "select new net.fina.server.fi.entity.Fi( b.id, b.code, b.description, b.addressDescription, b.createdAt, b.modifiedAt,  b.fiType, l.code) from IN_BANKS b left join IN_LICENCES l on l.fi.id = b.id and l.isDefault=true ";
        String predicate = " where ";

        if (excludeDisabled != null && excludeDisabled) {
            sql += (predicate + " b.disable=false");
        }

        sql += " order by b.id desc ";

        TypedQuery<Fi> query = em.createQuery(sql, Fi.class);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("JpaQlInspection")
    public List<Fi> loadPermittedFiTree(Boolean excludeDisabled) {
        List<Long> fiIds = new ArrayList<>(current.getCallerPrincipal().getFis());
        String fiConcatenatedInStatement = "(" + DBUtil.get().generateConcatenatedInStatement("b.id", fiIds, Long.class) + ")";
        String sql = "select new net.fina.server.fi.entity.Fi( b.id, b.code, b.description, b.addressDescription, b.createdAt, b.modifiedAt,  b.fiType, l.code) from IN_BANKS b left join IN_LICENCES l on l.fi.id = b.id and l.isDefault=true where " + fiConcatenatedInStatement;

        if (excludeDisabled != null && excludeDisabled) {
            sql += " and b.disable=false";
        }


        sql += " order by b.id desc ";

        TypedQuery<Fi> query = em.createQuery(sql, Fi.class);
        return query.getResultList();
    }


    @Override
    @SuppressWarnings("JpaQlInspection")
    public List<Fi> loadUserRoleFis(long entityId, boolean isUser, Boolean excludeDisabled) {

        String sql = "select new net.fina.server.fi.entity.Fi( b.id, b.code, b.description, b.addressDescription, b.createdAt, b.modifiedAt, b.fiType, l.code) from IN_BANKS b left join IN_LICENCES l on l.fi.id = b.id and l.isDefault=true";

        if (isUser) {
            sql += ", SYS_USER_BANKS ub where ub.bankId=b.id and ub.userId=:userId ";
        } else {
            sql += ", SYS_ROLE_BANKS ub where ub.bankId=b.id and ub.roleId=:roleId ";
        }

        if (excludeDisabled != null && excludeDisabled) {
            sql += "and b.disable=false ";
        }

        sql += "order by b.id desc ";
        Query query = em.createQuery(sql, Fi.class);

        if (isUser) {
            query.setParameter("userId", entityId);
        } else {
            query.setParameter("roleId", entityId);
        }

        return query.getResultList();
    }


    @Override
    public List<Fi> load(Map<FiFilter, Object> fiFilterObjectMap, int offset, int limit, long langId) {
        boolean loadAll = ((fiFilterObjectMap.get(FiFilter.LOAD_All) instanceof Boolean) ? (Boolean) fiFilterObjectMap.get(FiFilter.LOAD_All) : false);
        boolean excludeDisabled = (fiFilterObjectMap.get(FiFilter.EXCLUDE_DISABLED) != null && fiFilterObjectMap.get(FiFilter.EXCLUDE_DISABLED) instanceof Boolean && (Boolean) fiFilterObjectMap.get(FiFilter.EXCLUDE_DISABLED));
        if (loadAll) {
            String sql = "select distinct b from IN_BANKS b";
            String predicate = " where ";
            if (fiFilterObjectMap.get(FiFilter.USER_ID) != null) {
                sql += ",SYS_USER_BANKS  ub where ub.bankId=b.id and ub.userId=:userId ";
                predicate = " and ";
            } else if (fiFilterObjectMap.get(FiFilter.ROLE_ID) != null) {
                sql += ",SYS_ROLE_BANKS  ub where ub.bankId=b.id and ub.roleId=:roleId ";
            }
            if (fiFilterObjectMap.get(FiFilter.CODE) != null && excludeDisabled) {
                sql += (predicate + " b.code=:fiCode and b.disable=:showDisabled");
            } else if (fiFilterObjectMap.get(FiFilter.CODE) != null) {
                sql += (predicate + " b.code=:fiCode ");
            } else if (excludeDisabled) {
                sql += (predicate + " b.disable=:showDisabled");
            }

            sql += " order by b.id desc ";

            Query query = em.createQuery(sql, Fi.class);
            if (fiFilterObjectMap.get(FiFilter.CODE) != null) {
                String fiCode = fiFilterObjectMap.get(FiFilter.CODE).toString().toLowerCase();
                query.setParameter("fiCode", fiCode);
            }
            if (fiFilterObjectMap.get(FiFilter.USER_ID) != null) {
                long userId = (long) fiFilterObjectMap.get(FiFilter.USER_ID);
                query.setParameter("userId", userId);
            } else if (fiFilterObjectMap.get(FiFilter.ROLE_ID) != null) {
                long roleId = (long) fiFilterObjectMap.get(FiFilter.ROLE_ID);
                query.setParameter("roleId", roleId);
            }
            if (excludeDisabled) {
                query.setParameter("showDisabled", false);
            }

            if (offset > 0) {
                query.setFirstResult(offset);
            }

            if (limit > 0) {
                query.setMaxResults(limit);
            }

            List<Fi> fis = query.getResultList();

            //TODO Performance
            fis.stream().filter(fi -> fi.getPeerGroup() != null).forEach(fi -> {
                fi.getPeerGroup().forEach(PeerGroup::getId);
            });
            return fis;
        } else {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Fi> cq = cb.createQuery(Fi.class);

            Root<Fi> root = cq.from(Fi.class);

            cq.select(root).distinct(true);
            cq.orderBy(cb.desc(root.get(Fi_.ID)));

            List<Predicate> predicates = getFilterPredicate(cb, root, fiFilterObjectMap, langId);

            cq.where(predicates.toArray(new Predicate[0]));

            TypedQuery<Fi> query = em.createQuery(cq);

            if (offset >= 0 && limit > 0) {
                query.setFirstResult(offset).setMaxResults(limit);
            }

            return query.getResultList();
        }
    }


    @Override
    public long count(Map<FiFilter, Object> fiFilterObjectMap, long langId) {
        boolean loadAll = ((fiFilterObjectMap.get(FiFilter.LOAD_All) instanceof Boolean) ? (Boolean) fiFilterObjectMap.get(FiFilter.LOAD_All) : false);
        boolean excludeDisabled = (fiFilterObjectMap.get(FiFilter.EXCLUDE_DISABLED) != null && fiFilterObjectMap.get(FiFilter.EXCLUDE_DISABLED) instanceof Boolean && (Boolean) fiFilterObjectMap.get(FiFilter.EXCLUDE_DISABLED));
        if (loadAll) {
            String sql = "select distinct count(b.id) from IN_BANKS b";
            String predicate = " where ";
            if (fiFilterObjectMap.get(FiFilter.USER_ID) != null) {
                sql += ",SYS_USER_BANKS  ub where ub.bankId=b.id and ub.userId=:userId ";
                predicate = " and ";
            } else if (fiFilterObjectMap.get(FiFilter.ROLE_ID) != null) {
                sql += ",SYS_ROLE_BANKS  ub where ub.bankId=b.id and ub.roleId=:roleId ";
            }
            if (fiFilterObjectMap.get(FiFilter.CODE) != null && excludeDisabled) {
                sql += (predicate + " b.code=:fiCode and b.disable=:showDisabled");
            } else if (fiFilterObjectMap.get(FiFilter.CODE) != null) {
                sql += (predicate + " b.code=:fiCode ");
            } else if (excludeDisabled) {
                sql += (predicate + " b.disable=:showDisabled");
            }

            sql += " order by b.code asc ";

            TypedQuery<Long> query = em.createQuery(sql, Long.class);
            if (fiFilterObjectMap.get(FiFilter.CODE) != null) {
                String fiCode = fiFilterObjectMap.get(FiFilter.CODE).toString().toLowerCase();
                query.setParameter("fiCode", fiCode);
            }
            if (fiFilterObjectMap.get(FiFilter.USER_ID) != null) {
                long userId = (long) fiFilterObjectMap.get(FiFilter.USER_ID);
                query.setParameter("userId", userId);
            } else if (fiFilterObjectMap.get(FiFilter.ROLE_ID) != null) {
                long roleId = (long) fiFilterObjectMap.get(FiFilter.ROLE_ID);
                query.setParameter("roleId", roleId);
            }
            if (excludeDisabled) {
                query.setParameter("showDisabled", false);
            }

            return query.getSingleResult();
        } else {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<Fi> root = cq.from(Fi.class);

            cq.select(cb.count(root.get(Fi_.id)));
            List<Predicate> predicates = getFilterPredicate(cb, root, fiFilterObjectMap, langId);
            cq.where(predicates.toArray(new Predicate[0]));
            TypedQuery<Long> query = em.createQuery(cq);

            return query.getSingleResult();
        }
    }

    @Override
    public Fi findFiByCode(String code) {
        try {
            return em.createNamedQuery("findFiByCode", Fi.class).setParameter("code", code.trim()).getSingleResult();
        } catch (NullPointerException | NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")

    public boolean fiExists(String code) {
        boolean exists = true;
        Query findFiByCodeQuery = em.createQuery("SELECT count(f.code) FROM IN_BANKS f where trim(f.code)=:code");
        findFiByCodeQuery.setParameter("code", code);
        List<Long> countList = findFiByCodeQuery.getResultList();

        long count = Long.parseLong(countList.get(0).toString());
        if (count == 0) {
            exists = false;
        }

        return exists;
    }

    @Override

    public Fi save(Fi fi, String langCode) throws FinATypeException {
        long langId = languageListSingleton.getLanguageIdMap().get(langCode);
        if (fi.getFiType() == null) {
            throw new FinATypeException(MessagesUtil.getString("net.fina.fi.exception.fiTypeRequired"));
        }
        if (!checkCodeUnique(fi)) {
            throw new FinATypeException(CommonUtil.compileMessageWithParams(MessagesUtil.getString("net.fina.fi.exception.codeUnique"), fi.getCode()));
        }
//        if (fi.getEmail() != null && !fi.getEmail().trim().isEmpty() && !checkEmailUnique(fi)) {
//            throw new FinATypeException(CommonUtil.compileMessageWithParams(MessagesUtil.getString(FinATypeException.Type.FI_EMAIL_UNIQUE.getCode()), fi.getEmail(), fi.getCode()));
//        }
        if (fi.getIdentificationCode() != null && !checkIdentificationCodeUnique(fi.getIdentificationCode(), fi.getId())) {
            throw new FinATypeException(CommonUtil.compileMessageWithParams(MessagesUtil.getString("net.fina.exception.IdCodeNotUnique", langCode), fi.getIdentificationCode()));
        }
        if (fi.getSwiftCode() != null && fi.getSwiftCode().length() > 11) {
            throw new FinATypeException("Swift code length cannot exceed 11 characters");
        }
        return doSaveFi(fi, langId);
    }

    @Override

    public Fi saveECM(Fi fi, long langId) throws FinATypeException {
        return doSaveFi(fi, langId);
    }

    private Fi doSaveFi(Fi fi, long langId) throws FinATypeException {
        boolean isCreateNew = fi.getId() <= 0;

        if (fi.getId() != 0) {
            Fi changedFi = em.find(Fi.class, fi.getId());
            fi.setVersion(changedFi.getVersion());

            //for hibernate envers
            setDescriptionIdForEnvers(changedFi.getShortName(), fi.getShortName(), langId);
            setDescriptionIdForEnvers(changedFi.getDescription(), fi.getDescription(), langId);
            setDescriptionIdForEnvers(changedFi.getAddressDescription(), fi.getAddressDescription(), langId);

            if (changedFi.getCode() != null && !changedFi.getCode().trim().isEmpty() && !changedFi.getCode().equals(fi.getCode())) {
                throw new FinATypeException(FinATypeException.Type.INVALID_CODE);
            }

            if (fi.getLicences() == null) {
                fi.setLicences(changedFi.getLicences());
            }

            if (changedFi.getMdtNode() != null) {
                fi.setMdtNode(changedFi.getMdtNode());
            } else {
                MDTNode mdtNode = mdtDataNodeCatalogProxySession.checkAndCreateDataElementNodeBySourceType(fi.getCode(), MDTDataNodeCatalogSourceType.FI);
                fi.setMdtNode(mdtNode);
            }

            if (fi.getUsers() == null) {
                fi.setUsers(changedFi.getUsers());
            }

            fi.setCriminalRecords(changedFi.getCriminalRecords());

        } else {
            MDTNode mdtNode = mdtDataNodeCatalogProxySession.checkAndCreateDataElementNodeBySourceType(fi.getCode(), MDTDataNodeCatalogSourceType.FI);
            fi.setMdtNode(mdtNode);
            fi.setCreatedAt(new Date());
        }

        fi.setModifiedAt(new Date());


        fi = em.merge(fi);

        legalPersonLocal.createFiAsLegalPerson(fi);

        if (isCreateNew) {

            UserFi association = new UserFi();
            association.setBankId(fi.getId());
            association.setUserId(current.getCurrentUserId());
            association.setCanAmend(true);

            em.merge(association);

            //Add caller principal
            current.getCallerPrincipal().getFis().add(fi.getId());
        }

        return fi;
    }

    @Override

    public void delete(long fiId) throws FinATypeException {
        Fi fi = em.find(Fi.class, fiId);

        // check schedules
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Schedule> scheduleRoot = cq.from(Schedule.class);
        Join<Schedule, Fi> scheduleFiJoin = scheduleRoot.join(Schedule_.fi);
        cq.where(cb.equal(scheduleFiJoin.get(Fi_.id), fiId));
        cq.select(scheduleRoot.get(Schedule_.id));

        if (!em.createQuery(cq).getResultList().isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }

        // check licenses
        cb = em.getCriteriaBuilder();
        cq = cb.createQuery(Long.class);
        Root<Licence> licenseRoot = cq.from(Licence.class);
        Join<Licence, Fi> licenceFiJoin = licenseRoot.join(Licence_.fi);
        cq.where(cb.equal(licenceFiJoin.get(Fi_.id), fiId));
        cq.select(licenseRoot.get(Licence_.id));

        if (!em.createQuery(cq).getResultList().isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }

        // check managements
        cb = em.getCriteriaBuilder();
        cq = cb.createQuery(Long.class);
        Root<FiManagement> managementRoot = cq.from(FiManagement.class);

        cq.where(cb.equal(managementRoot.get(FiManagement_.fiId), fiId));
        cq.select(managementRoot.get(FiManagement_.id));

        if (!em.createQuery(cq).getResultList().isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }

        // check branches
        cb = em.getCriteriaBuilder();
        cq = cb.createQuery(Long.class);
        Root<FiBranch> branchRoot = cq.from(FiBranch.class);
        cq.where(cb.and(cb.equal(branchRoot.get(FiBranch_.bankId), fiId), cb.equal(branchRoot.get(FiBranch_.deleted), false)));
        cq.select(branchRoot.get(FiBranch_.id));

        if (!em.createQuery(cq).getResultList().isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }

        if (fi != null) {
            boolean isFiAsMdtDataNodeEnable = mdtDataNodeCatalogProxySession.isEntityAsMdtDataNodeEnable(MDTDataNodeCatalogSourceType.FI);
            if (isFiAsMdtDataNodeEnable) { // disable fi
                fi.setDisable(true);

                // check and move related mdt node
                if (fi.getMdtNode() != null) {
                    mdtDataNodeCatalogProxySession.deleteDataElementMdtNode(fi.getMdtNode().getCode(), MDTDataNodeCatalogSourceType.FI);
                }

                // disable external users
                em.createQuery("update SYS_USERS " + "set blocked = 1, disabled = 1 where id in " + "(select distinct u.id from SYS_USER_BANKS ub " + "join SYS_USERS u on u.id = ub.userId " + "join IN_BANKS b on b.id = ub.bankId " + "left join u.permissions p " + "left join u.roles r " + "left join r.permissions rp " + "where b.code = :fiCode and " + "(p.idName = :externalUserPermission or rp.idName = :externalUserPermission))").setParameter("fiCode", fi.getCode()).setParameter("externalUserPermission", PermissionIdNames.FINA_WEB_EXTERNAL_USER).executeUpdate();

                em.merge(fi);
            } else { // delete fi
                // delete user permissions
                em.createQuery("DELETE from SYS_USER_BANKS u WHERE u.bankId=:bankId")
                        .setParameter("bankId", fiId)
                        .executeUpdate();

                em.createQuery("DELETE from SYS_ROLE_BANKS r WHERE r.bankId=:bankId")
                        .setParameter("bankId", fiId)
                        .executeUpdate();

                em.createNativeQuery("delete from IN_BANK_PERSON_CONNECTIONS where FI_PERSON_ID in (select id from IN_BANK_PERSONS where FI_ID=:fiId )")
                        .setParameter("fiId", fiId)
                        .executeUpdate();

                em.createQuery("delete from IN_BANK_BRANCHES where bankId=:fiId")
                        .setParameter("fiId", fiId)
                        .executeUpdate();

                em.createQuery("delete from IN_BANK_PERSONS where fi.id=:fiId")
                        .setParameter("fiId", fi.getId())
                        .executeUpdate();


                // delete bank
                LegalPerson legalPerson = legalPersonLocal.getLegalPersonByFiId(fi.getId());
                if (legalPerson != null) {
                    legalPersonLocal.deleteLegalPerson(List.of(legalPerson.getId()));
                    em.remove(legalPerson);
                }
                em.remove(fi);
            }

        }
    }

    @Override

    public boolean canAmendFi(long fiId) {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override

    public List<Fi> loadFiByCodes(List<String> codes) {
        if (codes.size() < 1) {
            return new ArrayList<Fi>();
        }
        Query query = em.createQuery("select fi from IN_BANKS fi where fi.code in(:codes)");
        query.setParameter("codes", codes);
        List<Fi> list = query.getResultList();
        return list;
    }

    @Override

    public List<String> loadFiCodes() {
        return loadFiCodes(current.getCurrentUserLogin());
    }

    @SuppressWarnings("unchecked")
    @Override

    public List<String> loadFiCodes(String userLogin) {
        return current.getUserFiCodes(userLogin);
    }

    @SuppressWarnings("unchecked")
    @Override

    public Map<String, Description> loadFiCodeAndNames() {
        return loadFiCodeAndNames(current.getCurrentUserLogin());
    }

    @SuppressWarnings("unchecked")
    @Override

    public Map<String, Description> loadFiCodeAndNames(String userLogin) {
        TypedQuery<Object[]> userFis = em.createQuery("SELECT trim(fi.code), fi.description from SYS_USERS as u, IN(u.fis) fi WHERE trim(LOWER(u.login))=:userLogin", Object[].class);
        TypedQuery<Object[]> roleFis = em.createQuery("SELECT trim(fi.code), fi.description from SYS_USERS as u, IN(u.roles)ur, IN(ur.fis) fi WHERE trim(LOWER(u.login))=:userLogin", Object[].class);
        userFis.setParameter("userLogin", userLogin.trim().toLowerCase());
        roleFis.setParameter("userLogin", userLogin.trim().toLowerCase());
        List<Object[]> resultList = userFis.getResultList();
        resultList.addAll(roleFis.getResultList());
        Map<String, Description> returnValue = new HashMap<>(resultList.size());
        for (Object[] result : resultList) {
            returnValue.put((String) result[0], (Description) result[1]);
        }
        return returnValue;
    }

    @SuppressWarnings("rawtypes")
    public boolean isCodeUnique(String code, long id) {
        Query codeUniqueQuery = em.createQuery("select c.id from IN_LICENCE_TYPES c where trim(c.code)=:code and c.id<>:id");
        codeUniqueQuery.setParameter("id", id);
        codeUniqueQuery.setParameter("code", code);
        List ids = codeUniqueQuery.getResultList();
        if (ids.size() > 0) return false;
        return true;

    }

    // Licence
    @SuppressWarnings("unchecked")
    @Override

    public List<Licence> loadFiLicences(long fiId) {
        Query loadFiLicensesQuery = em.createQuery("select l from IN_LICENCES l where l.fi.id=:fiId ", Licence.class);
        loadFiLicensesQuery.setParameter("fiId", fiId);
        return loadFiLicensesQuery.getResultList();
    }

    @Override
    public List<Licence> loadFiLicences(long fiId, int start, int limit, String filterValue) {
        StringBuilder queryStr = new StringBuilder("select l from IN_LICENCES l where l.fi.id = :fiId ");

        if (filterValue != null && !filterValue.trim().isEmpty()) {
            queryStr.append(" and lower(trim(l.code)) like :filterValue");
        }

        TypedQuery<Licence> query = em.createQuery(queryStr.toString(), Licence.class)
                .setParameter("fiId", fiId);

        if (filterValue != null && !filterValue.trim().isEmpty()) {
            query.setParameter("filterValue", "%" + filterValue.toLowerCase() + "%");
        }

        if (start >= 0 && limit > 0) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }


    //Licence History
    @Override

    public Collection<LicenceHistory> loadFiLicenceHistories(long fiId) {
        return em.createQuery("select lh from IN_BANKS b, IN(b.licences) bl,IN_LICENCES_HISTORY  lh where bl.id=lh.licence.id and  b.id=:fiId order by lh.changeDate", LicenceHistory.class).setParameter("fiId", fiId).getResultList();
    }


    @Override
    public Licence saveFiLicence(Licence licence) throws FinATypeException {
        if (!checkLicenseCodeUnique(licence)) {
            throw new FinATypeException("License code is not unique, Fi: " + licence.getFi().getCode());
        }

        //Remove old default
        if (licence.getIsDefault() != null && licence.getIsDefault()) {
            em.createQuery("update IN_LICENCES  set isDefault=false where fi.id=:fiId and id<>:lId")
                    .setParameter("fiId", licence.getFi().getId())
                    .setParameter("lId", licence.getId())
                    .executeUpdate();
        }

        String change = licence.getChange();
        if (licence.getId() > 0) {
            Licence existing = em.find(Licence.class, licence.getId());
            licence.setDateOfChange(new Date());
            licence.setVersion(existing.getVersion());

            for (LicenseBankingOperation bo : existing.getOperations()) {
                if (!licence.getOperations().contains(bo)) {
                    em.remove(bo);
                }
            }

        }
        licence = em.merge(licence);

        LicenceHistory lh = new LicenceHistory();
        lh.setLicence(licence);
        lh.setChangeDate(new Date());
        lh.setLicenceStatus(licence.getLicenceStatus());
        lh.setChange(change);
        em.persist(lh);

        return licence;
    }

    @Override
    public Licence saveFiLicenceECM(Licence licence) {
        licence = em.merge(licence);
        return licence;
    }

    @Override
    public void deleteFiLicence(long licenceId) {
        Licence licence = em.find(Licence.class, licenceId);
        if (licence != null) {
            em.createQuery("delete from IN_LICENCES_HISTORY h  where h.licence.id = :licenceId").setParameter("licenceId", licenceId).executeUpdate();
            em.remove(licence);
        }
    }

    // FI Group
    @Override

    public Collection<PeerGroup> getFiPeerGroups(long fiId) {
        Fi fi = em.find(Fi.class, fiId);
        return fi.getPeerGroup();
    }

    @Override

    public Collection<PeerGroup> setFiPeerGroups(long fiId, List<PeerGroup> groups) {
        Fi fi = em.find(Fi.class, fiId);
        saveFiPeerGroupHistory(fi, groups);
        fi.setPeerGroup(groups);
        return groups;
    }

    //TODO
    private void saveFiPeerGroupHistory(Fi fi, List<PeerGroup> groups) {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(new Date());

        Collection<PeerGroup> currentPeerGroups = fi.getPeerGroup();

        //old groups
        for (PeerGroup pg : currentPeerGroups) {
            PeerGroupHistory history = null;
            boolean exist = false;
            for (PeerGroup newPeerGroup : groups) {
                if (pg.getId() == newPeerGroup.getId()) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                history = createPeerGroupHistory(fi, pg);
                history.setChange(currentCalendar.getTime());
            }
            if (history != null) {
                em.persist(history);
            }
        }

        //new groups
        for (PeerGroup pg : groups) {
            PeerGroupHistory history = null;
            boolean exist = false;
            for (PeerGroup oldPeerGroup : groups) {
                if (pg.getId() == oldPeerGroup.getId()) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                history = createPeerGroupHistory(fi, pg);
                history.setChange(currentCalendar.getTime());
            }
            if (history != null) {
                em.persist(history);
            }
        }
    }

    private PeerGroupHistory createPeerGroupHistory(Fi fi, PeerGroup peerGroup) {
        PeerGroupHistory history = new PeerGroupHistory();
        history.setFiId(fi.getId());
        history.setFiGroupId(peerGroup.getId());
        return history;
    }

    // Fi Branch
    @Override
    public List<FiBranch> loadFiBranchs(Long fiId) {
        return loadFiBranchsQuery(fiId).getResultList();
    }

    @Override
    public List<FiBranch> loadFiBranchs(Long fiId, int offset, int limit) {
        Query query = loadFiBranchsQuery(fiId);
        if (offset > 0) {
            query.setFirstResult(offset);
        }

        if (limit > 0) {
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<FiBranch> loadFiBranchesByType(Long fiId, int offset, int limit, long fiBranchTypeId, Map<FiBranchFilter, Object> filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<FiBranch> cq = cb.createQuery(FiBranch.class);
        Root<FiBranch> root = cq.from(FiBranch.class);

        cq.select(root);

        List<Predicate> predicates = getFiBranchPredicates(cb, root, filter);
        predicates.add(cb.or(cb.equal(root.get(FiBranch_.deleted), false), cb.isNull(root.get(FiBranch_.deleted))));
        if (fiBranchTypeId > 0) {
            predicates.add(cb.equal(root.get(FiBranch_.fiBranchType).get(FiBranchType_.id), fiBranchTypeId));
        }
        predicates.add(cb.equal(root.get(FiBranch_.bankId), fiId));

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(root.get(FiBranch_.id)));

        TypedQuery<FiBranch> query = em.createQuery(cq);

        if (offset > 0) {
            query.setFirstResult(offset);
        }

        if (limit > 0) {
            query.setMaxResults(limit);
        }
        return query.getResultList();

    }


    private List<Predicate> getFiBranchPredicates(CriteriaBuilder cb, Root<FiBranch> root, Map<FiBranchFilter, Object> filterMap) {
        List<Predicate> predicates = new ArrayList<>();
        long langId = ThreadLocalHolder.getLanguage().getId();

        if (filterMap != null && !filterMap.isEmpty()) {
            for (Map.Entry<FiBranchFilter, Object> entry : filterMap.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }

                switch (entry.getKey()) {
                    case ADDRESS:
                        List<Long> addressIds = em.createQuery("select b.id from SYS_STRINGS s inner join IN_BANK_BRANCHES b on b.address=s.id and s.langId =:langId and s.value like :value", Long.class)
                                .setParameter("langId", langId)
                                .setParameter("value", "%" + entry.getValue().toString() + "%")
                                .getResultList();

                        predicates.add(root.get(FiBranch_.id).in(addressIds.isEmpty() ? Collections.singletonList(-1L) : addressIds));
                        break;
                    case CHANGE_DATE:
                        predicates.add(cb.equal(root.get(FiBranch_.changeDate), entry.getValue()));
                        break;
                    case CODE:
                        predicates.add(cb.like(root.get(FiBranch_.code), "%" + entry.getValue() + "%"));
                        break;
                    case COMMENT:
                        List<Long> commentIds = em.createQuery("select b.id from SYS_STRINGS s inner join IN_BANK_BRANCHES b on b.comment=s.id and s.langId =:langId and s.value like :value", Long.class)
                                .setParameter("langId", langId)
                                .setParameter("value", "%" + entry.getValue().toString() + "%")
                                .getResultList();


                        predicates.add(root.get(FiBranch_.id).in(commentIds.isEmpty() ? Collections.singletonList(-1L) : commentIds));
                        break;
                    case CREATE_DATE:
                        predicates.add(cb.equal(root.get(FiBranch_.createDate), entry.getValue()));
                        break;
                    case EMAIL:
                        predicates.add(cb.like(root.get(FiBranch_.email), "%" + entry.getValue() + "%"));
                        break;
                    case MANAGER:
                        List<Long> managerIds = em.createQuery("select distinct b.id from SYS_STRINGS s, IN_BANK_BRANCHES b inner join IN_BANK_PERSONS p on b.manager.id = p.id where p.person.name = s.id and  s.langId = :langId and s.value like :value ", Long.class)
                                .setParameter("langId", langId)
                                .setParameter("value", "%" + entry.getValue() + "%")
                                .getResultList();

                        predicates.add(root.get(FiBranch_.id).in(managerIds.isEmpty() ? Collections.singletonList(-1L) : managerIds));
                        break;
                    case MANAGER_APP_DATE:
                        predicates.add(cb.equal(root.get(FiBranch_.managerAppointmentDate), entry.getValue()));
                        break;

                    case MANAGER_ID_NUMBER:
                        predicates.add(cb.like(root.get(FiBranch_.manager).get(FiPerson_.person).get(Person_.identificationNumber), "%" + entry.getValue() + "%"));
                        break;
                    case NAME:
                        List<Long> nameIds = em.createQuery("select b.id from SYS_STRINGS s inner join IN_BANK_BRANCHES b on b.name=s.id and s.langId =:langId and s.value like :value", Long.class)
                                .setParameter("langId", langId)
                                .setParameter("value", "%" + entry.getValue().toString() + "%")
                                .getResultList();

                        predicates.add(root.get(FiBranch_.id).in(nameIds.isEmpty() ? Collections.singletonList(-1L) : nameIds));
                        break;
                    case PHONE:
                        predicates.add(cb.like(root.get(FiBranch_.phone), "%" + entry.getValue() + "%"));
                        break;
                    case REGION_ID:
                        Collection<Long> regionIds = new ArrayList<>((Collection<Long>) entry.getValue());
                        predicates.add(root.get(FiBranch_.region).get(Region_.id).in(regionIds));
                        break;
                    case REGISTRATION_NUMBER:
                        predicates.add(cb.like(root.get(FiBranch_.registrationNumber), "%" + entry.getValue() + "%"));
                        break;
                    case RENEWAL_DATE:
                        predicates.add(cb.equal(root.get(FiBranch_.renewalDate), entry.getValue()));
                        break;
                    case SHORT_NAME:
                        List<Long> shortNameIds = em.createQuery("select b.id from SYS_STRINGS s inner join IN_BANK_BRANCHES b on b.shortName=s.id and s.langId =:langId and s.value like :value", Long.class)
                                .setParameter("langId", langId)
                                .setParameter("value", "%" + entry.getValue() + "%")
                                .getResultList();


                        predicates.add(root.get(FiBranch_.id).in(shortNameIds.isEmpty() ? Collections.singletonList(-1L) : shortNameIds));
                        break;
                    case SUSPENSION_DATE:
                        predicates.add(cb.equal(root.get(FiBranch_.suspensionDate), entry.getValue()));
                        break;
                    case CLOSE_DATE:
                        predicates.add(cb.equal(root.get(FiBranch_.closeDate), entry.getValue()));
                        break;
                    case DISABLED:
                        predicates.add(cb.equal(root.get(FiBranch_.disable), entry.getValue()));
                        break;
                    case ACCOUNTANT:

                        List<Long> accountantIds = em.createQuery("select distinct b.id from SYS_STRINGS s, IN_BANK_BRANCHES b inner join IN_BANK_PERSONS p on b.chiefAccountant.id = p.id where p.person.name = s.id and  s.langId = :langId and s.value like :value ", Long.class)
                                .setParameter("langId", langId)
                                .setParameter("value", "%" + entry.getValue() + "%")
                                .getResultList();

                        predicates.add(root.get(FiBranch_.id).in(accountantIds.isEmpty() ? Collections.singletonList(-1L) : accountantIds));

                        break;
                    case ACCOUNTANT_APP_DATE:
                        predicates.add(cb.equal(root.get(FiBranch_.chiefAccountantAppointmentDate), entry.getValue()));
                        break;

                    case ACCOUNTANT_ID_NUMBER:
                        predicates.add(cb.like(root.get(FiBranch_.chiefAccountant).get(FiPerson_.person).get(Person_.identificationNumber), "%" + entry.getValue() + "%"));
                        break;
                    case STORAGE_AVAILABLE:
                        predicates.add(cb.equal(root.get(FiBranch_.isStorageAvailable), entry.getValue()));
                        break;
                }
            }
        }

        return predicates;
    }

    @Override
    public List<FiBranch> loadFiBranchesByType(long fiBranchTypeId) {
        TypedQuery<FiBranch> query = em.createQuery("select ibb from IN_BANK_BRANCHES ibb where ibb.fiBranchType.id=:fiBranchTypeId and (ibb.deleted=:isDeleted or ibb.deleted is null)", FiBranch.class).setParameter("fiBranchTypeId", fiBranchTypeId).setParameter("isDeleted", false);
        return query.getResultList();
    }

    private Query loadFiBranchsQuery(Long fiId) {
        Query query = em.createQuery("select fb from IN_BANK_BRANCHES fb where fb.bankId=:fiId and (fb.deleted=:isDeleted or fb.deleted is null)");
        query.setParameter("fiId", fiId);
        query.setParameter("isDeleted", false);
        return query;
    }

    @Override
    public FiBranch findFiBranchById(long branchId) {
        return em.createQuery("select fb from IN_BANK_BRANCHES fb where fb.id=:branchId", FiBranch.class).setParameter("branchId", branchId).getSingleResult();
    }

    @Override
    @Transactional(rollbackOn = FinATypeException.class)
    public FiBranch saveFiBranch(FiBranch fiBranch, long langId, boolean importMode) throws FinATypeException {
        //check code exists
        if (fiBranch.getCode() == null || fiBranch.getCode().trim().isEmpty()) {
            throw new FinATypeException("Branch code is empty");

        }
        if (fiBranch.getFiBranchType() == null) {
            throw new FinATypeException("Fi branch type is required, Fi branch: " + fiBranch.getCode());
        }

        if (fiBranch.getRegion() == null) {
            throw new FinATypeException("Fi branch region is required, Fi branch:" + fiBranch.getCode());
        }

        fiBranch.setCode(fiBranch.getCode().trim());
        boolean isCodeUnique = isCodeUniqueFiBranch(fiBranch.getCode());

        if (fiBranch.getId() > 0) {
            FiBranch existing = em.find(FiBranch.class, fiBranch.getId());

            if (existing.getCode() != null && !existing.getCode().trim().isEmpty() && !existing.getCode().equals(fiBranch.getCode())) {
                if (!isCodeUnique) {
                    throw new FinATypeException("Branch Code Is not Unique : " + fiBranch.getCode());
                }
            }
        } else {
            if (!isCodeUnique) {
                throw new FinATypeException("Branch Code Is not Unique : " + fiBranch.getCode());
            }
        }

        if (fiBranch.getRegion() != null) {
            fiBranch.setRegion(em.find(Region.class, fiBranch.getRegion().getId()));
        }


        //validations are for in importMode created persons
        if (importMode) {
            if (fiBranch.getChiefAccountant() != null) {
                FiPerson accountant = fiBranch.getChiefAccountant();
                accountant = validateAndSetPerson(accountant, fiBranch, FiPersonConnectionType.BRANCH_CHIEF_ACCOUNTANT);
                fiBranch.setChiefAccountant(accountant);
            }


            if (fiBranch.getManager() != null) {
                FiPerson manager = fiBranch.getManager();
                manager = validateAndSetPerson(manager, fiBranch, FiPersonConnectionType.BRANCH_MANAGER);
                fiBranch.setManager(manager);
            }
        }

        if (fiBranch.getId() == 0) {

            MDTNode mdtNode = mdtDataNodeCatalogProxySession.checkAndCreateDataElementNodeBySourceType(fiBranch.getCode(), MDTDataNodeCatalogSourceType.FI_BRANCH);
            fiBranch.setMdtNode(mdtNode);

            em.persist(fiBranch);
        } else {
            FiBranch existing = em.find(FiBranch.class, fiBranch.getId());

            setDescriptionIdForEnvers(existing.getAddress(), fiBranch.getAddress(), langId);
            setDescriptionIdForEnvers(existing.getComment(), fiBranch.getComment(), langId);
            setDescriptionIdForEnvers(existing.getName(), fiBranch.getName(), langId);
            setDescriptionIdForEnvers(existing.getShortName(), fiBranch.getShortName(), langId);

            if (existing.getMdtNode() != null) {
                fiBranch.setMdtNode(existing.getMdtNode());
            } else {
                MDTNode mdtNode = mdtDataNodeCatalogProxySession.checkAndCreateDataElementNodeBySourceType(fiBranch.getCode(), MDTDataNodeCatalogSourceType.FI_BRANCH);
                fiBranch.setMdtNode(mdtNode);
            }

            fiBranch = em.merge(fiBranch);
        }

        return fiBranch;
    }


    @Override
    public void deleteFiBranch(long branchId) throws FinATypeException {
        FiBranch branch = em.find(FiBranch.class, branchId);
        branch.setDeleted(true);

        if (branch.getMdtNode() != null) {
            mdtDataNodeCatalogProxySession.deleteDataElementMdtNode(branch.getMdtNode().getCode(), MDTDataNodeCatalogSourceType.FI_BRANCH);
        }

        //remove person connection
        if (branch.getChiefAccountant() != null) {
            personLocal.removeFiPersonConnection(branch.getChiefAccountant(), FiPersonConnectionType.BRANCH_CHIEF_ACCOUNTANT);
            branch.setChiefAccountant(null);
        }
        if (branch.getManager() != null) {
            personLocal.removeFiPersonConnection(branch.getManager(), FiPersonConnectionType.BRANCH_MANAGER);
            branch.setManager(null);
        }
        em.merge(branch);
    }


    @Override
    public FiBranch findDeletedFiBranchByBranchCode(long fiId, String fiBranchCode) {
        List<FiBranch> deleted = em.createQuery("select fb from IN_BANK_BRANCHES fb where fb.bankId=:fiId and fb.code=:branchCode and fb.deleted = true ", FiBranch.class).setParameter("fiId", fiId).setParameter("branchCode", fiBranchCode).getResultList();
        return deleted != null && !deleted.isEmpty() ? deleted.get(0) : null;
    }

    @Override
    public void restoreDeletedFiBranch(long fiBranchId) throws FinATypeException {
        FiBranch fiBranch = em.find(FiBranch.class, fiBranchId);
        fiBranch.setDeleted(false);

        if (fiBranch.getMdtNode() != null) {
            mdtDataNodeCatalogProxySession.restoreDataElementMdtNode(fiBranch.getMdtNode().getCode(), MDTDataNodeCatalogSourceType.FI_BRANCH);
        }
        em.merge(fiBranch);
    }

    // FI Management
    @SuppressWarnings("unchecked")
    @Override
    public List<Management> loadManagements() {
        Query query = em.createNamedQuery("loadmanagement", Management.class);
        return query.getResultList();
    }

    @Override

    public Management save(Management management) throws FinATypeException {
        if (!isCodeUniqueManagement(management.getCode(), management.getId())) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE);
        }
        FiBranchAndManagementTypeValidator.validateFields(management.getJsonConfig(), Set.of("person"));

        if (management.getId() > 0) {
            Management existing = em.find(Management.class, management.getId());
            management.setVersion(existing.getVersion());
            management = em.merge(management);
        } else {
            em.persist(management);
        }
        return management;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void deleteManagement(long id) throws FinATypeException {
        Query query = em.createNativeQuery("select id from IN_BANK_MANAGEMENT where MANAGINGBODYID=:id");
        query.setParameter("id", id);
        List bankids = query.getResultList();
        if (bankids.size() > 0) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }
        Management management = em.find(Management.class, id);
        if (management != null) {
            em.remove(management);
        }

    }


    @Override

    public List<FiManagement> loadFiManagement(long fiId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<FiManagement> query = cb.createQuery(FiManagement.class);
        Root<FiManagement> fiManagement = query.from(FiManagement.class);

        query.where(cb.equal(fiManagement.get(FiManagement_.fiId), fiId));

        return em.createQuery(query).getResultList();
    }

    @Override
    public List<FiManagement> loadFiManagementByType(long fiId, long managementTypeId, int start, int limit, String filterString) {
        StringBuilder qb = new StringBuilder("select bm from IN_BANK_MANAGEMENT bm");
        boolean filterActive = !StringUtils.isBlank(filterString);
        if (filterActive) {
            qb.append(" inner join SYS_STRINGS ss on ss.id  = bm.fiPerson.person.name and ss.langId=:langId");
        }


        qb.append(" where bm.fiId = :fiId");

        if (managementTypeId > 0) {
            qb.append(" and bm.management.id = :managementId");
        }

        if (filterActive) {
            qb.append(" and (lower(trim(ss.value)) like :filterString or bm.fiPerson.person.identificationNumber like :filterString)");
        }

        qb.append(" order by bm.id desc");

        TypedQuery<FiManagement> query = em.createQuery(qb.toString(), FiManagement.class)
                .setParameter("fiId", fiId);

        if (managementTypeId > 0) {
            query.setParameter("managementId", managementTypeId);
        }

        if (filterActive) {
            query.setParameter("filterString", "%" + filterString.trim().toLowerCase() + "%");
            query.setParameter("langId", ThreadLocalHolder.getLanguage().getId());
        }


        if (start >= 0 && limit > 0) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }


    @Override
    public long countFiManagementByType(long fiId, long managementTypeId, String filterString) {
        StringBuilder qb = new StringBuilder("select count(bm.id) from IN_BANK_MANAGEMENT bm");
        boolean filterActive = !StringUtils.isBlank(filterString);

        if (filterActive) {
            qb.append(" inner join SYS_STRINGS ss on ss.id  = bm.fiPerson.person.name and ss.langId=:langId");
        }

        qb.append(" where bm.fiId = :fiId");

        if (managementTypeId > 0) {
            qb.append(" and bm.management.id = :managementId");
        }

        if (filterActive) {
            qb.append(" and (lower(trim(ss.value)) like :filterString or bm.fiPerson.person.identificationNumber like :filterString)");
        }

        TypedQuery<Long> query = em.createQuery(qb.toString(), Long.class)
                .setParameter("fiId", fiId);

        if (managementTypeId > 0) {
            query.setParameter("managementId", managementTypeId);
        }

        if (filterActive) {
            query.setParameter("filterString", "%" + filterString.trim().toLowerCase() + "%");
            query.setParameter("langId", ThreadLocalHolder.getLanguage().getId());
        }

        return query.getSingleResult();
    }


    @Override
    public void deleteFiManagement(long managementId) {
        FiManagement fiManagement = em.find(FiManagement.class, managementId);

        //delete Committees
        if (!fiManagement.getFiManagementCommitteeList().isEmpty()) {
            fiManagement.getFiManagementCommitteeList().forEach(committee -> em.remove(committee));
        }

        //remove person connection
        if (fiManagement.getFiPerson() != null) {
            personLocal.removeFiPersonConnection(fiManagement.getFiPerson(), FiPersonConnectionType.MANAGER);
        }

        em.remove(fiManagement);
    }


    @Override
    public FiManagement saveFiManagement(FiManagement entity, long langId) throws FinATypeException {
        entity.setManagement(em.find(Management.class, entity.getManagement().getId()));

        if (entity.getId() == 0) {
            if (entity.getFiPerson() != null) {
                Person person = entity.getFiPerson().getPerson();

                validateFiManagementDuplication(entity.getFiId(), entity.getManagement().getId(), person);

                if (person.getIdentificationNumber() == null || person.getIdentificationNumber().trim().isEmpty()) {
                    throw new FinATypeException("Person Identification Code is required in Management");
                }
                if (person.getCitizenship() == null) {
                    throw new FinATypeException("Citizenship is required in Management");
                }
                person = personLocal.findByIdentificationCodeAndRegion(person.getIdentificationNumber(), person.getCitizenship().getId());
                if (person == null) {
                    throw new FinATypeException("Person with identification number [" + entity.getFiPerson().getPerson().getIdentificationNumber() + "] and region not found in Management");
                }

                FiPerson fiPerson = personLocal.createOrUpdateFiPersonRelation(entity.getFiId(), person.getId(), FiPersonConnectionType.MANAGER);
                entity.setFiPerson(fiPerson);
            }

            //save committees
            for (FiManagementCommittee fiManagementCommittee : entity.getFiManagementCommitteeList()) {
                em.persist(fiManagementCommittee);
            }

            em.persist(entity);
        } else {
            FiManagement existing = em.find(FiManagement.class, entity.getId());
            entity.setVersion(existing.getVersion());

            //remove deleted committees
            for (FiManagementCommittee committee : existing.getFiManagementCommitteeList()) {
                if (!entity.getFiManagementCommitteeList().contains(committee)) {
                    em.remove(committee);
                }
            }

            setDescriptionIdForEnvers(existing.getDescription(), entity.getDescription(), langId);
            setDescriptionIdForEnvers(existing.getLastDescription(), entity.getLastDescription(), langId);
            setDescriptionIdForEnvers(existing.getPost(), entity.getPost(), langId);
            setDescriptionIdForEnvers(existing.getCommectId1(), entity.getCommectId1(), langId);
            setDescriptionIdForEnvers(existing.getCommectId2(), entity.getCommectId2(), langId);
            setDescriptionIdForEnvers(existing.getRegistrationId1(), entity.getRegistrationId1(), langId);
            setDescriptionIdForEnvers(existing.getRegistrationId2(), entity.getRegistrationId2(), langId);
            setDescriptionIdForEnvers(existing.getRegistrationId3(), entity.getRegistrationId3(), langId);

            entity = em.merge(entity);
        }
        return entity;
    }

    @Override
    public FiManagement getFiManagementById(long fiManagementId) {
        return em.find(FiManagement.class, fiManagementId);
    }

    // FI Type
    @SuppressWarnings("unchecked")
    @Override

    public List<FiType> loadFiTypes() {
        Query query = em.createNamedQuery("loadbanktypes", FiType.class);
        return query.getResultList();
    }

    @Override

    public FiType save(FiType fiType) throws FinATypeException {
        if (!isCodeUniqueFiType(fiType.getCode(), fiType.getId())) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE);
        }
        if (fiType.getId() > 0) {
            fiType.setVersion(em.find(FiType.class, fiType.getId()).getVersion());
            fiType = em.merge(fiType);
        } else {
            em.persist(fiType);
        }
        return fiType;
    }

    @SuppressWarnings("rawtypes")
    @Override

    public void deleteFiType(long id) throws FinATypeException {
        // GET dependencies
        Query query = em.createQuery("select c.id from  IN_BANKS c where c.fiType.id=:id");
        query.setParameter("id", id);
        List banks = query.getResultList();

        query = em.createQuery("select p from IN_PACKAGES p join p.fiTypes t where t.id =:id");
        query.setParameter("id", id);
        List packages = query.getResultList();

        if (banks.size() > 0 || packages.size() > 0) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }
        FiType returnType = em.find(FiType.class, id);
        em.remove(returnType);
    }

    @SuppressWarnings("rawtypes")

    public boolean isCodeUniqueFiType(String code, long id) {
        Query codeUniqueQuery = em.createQuery("select c.id from IN_BANK_TYPES c where trim(c.code)=:code and c.id <>:id");
        codeUniqueQuery.setParameter("id", id);
        codeUniqueQuery.setParameter("code", code);
        List ids = codeUniqueQuery.getResultList();
        if (ids.size() > 0) return false;
        return true;

    }

    @Override

    public Description getFiShortNameByCode(String code) {
        if (code != null) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Description> query = cb.createQuery(Description.class);
            Root<Fi> root = query.from(Fi.class);
            query.select(root.get(Fi_.shortName)).where(cb.equal(cb.trim(root.get(Fi_.code)), code.trim()));
            return em.createQuery(query).getSingleResult();
        }
        return null;
    }

    @Override
    public boolean checkCodeUnique(Fi fi) {
        return em.createNamedQuery("checkFiCodeUnique").setParameter("code", fi.getCode()).setParameter("id", fi.getId()).getResultList().isEmpty();
    }

    @Override
    public boolean checkEmailUnique(Fi fi) {
        return em.createNamedQuery("checkFiEmailUnique").setParameter("email", fi.getEmail().trim().toLowerCase()).setParameter("id", fi.getId()).getResultList().isEmpty();
    }

    @Override
    public boolean checkLicenseCodeUnique(Licence licence) {
        return em.createNamedQuery("checkLicenseCodeUnique").setParameter("code", licence.getCode()).setParameter("id", licence.getId()).getResultList().isEmpty();
    }

    @Override
    public boolean checkLicenseTypeCodeUnique(LicenceType licenceType) {
        return em.createNamedQuery("checkLicenseTypeCodeUnique").setParameter("code", licenceType.getCode()).setParameter("id", licenceType.getId()).getResultList().isEmpty();
    }

    @Override
    public boolean checkCriterionCodeUnique(Criterion criterion) {
        return em.createNamedQuery("checkCriterionCodeUnique").setParameter("code", criterion.getCode()).setParameter("id", criterion.getId()).getResultList().isEmpty();
    }

    @Override
    public boolean checkPeerGroupsCodeUnique(PeerGroup peerGroup) {
        return em.createNamedQuery("checkPeerGroupCodeUnique").setParameter("code", peerGroup.getCode()).setParameter("id", peerGroup.getId()).getResultList().isEmpty();
    }

    @Override
    public boolean checkFiTypeCodeUnique(FiType fiType) {
        return em.createNamedQuery("checkFiTypeCodeUnique").setParameter("code", fiType.getCode()).setParameter("id", fiType.getId()).getResultList().isEmpty();
    }

    @Override
    public boolean checkRegionCodeUnique(Region region) {
        return em.createNamedQuery("checkRegionCodeUnique").setParameter("code", region.getCode()).setParameter("id", region.getId()).getResultList().isEmpty();
    }

    @Override
    public boolean checkManagementCodeUnique(Management management) {
        return em.createNamedQuery("checkManagementCodeUnique").setParameter("code", management.getCode()).setParameter("id", management.getId()).getResultList().isEmpty();
    }

    @Override
    public List<FiType> loadFiTypesByIds(List<Long> ids) {
        return em.createQuery("select r from IN_BANK_TYPES r where r.id in (:ftIds)", FiType.class).setParameter("ftIds", ids).getResultList();
    }

    @Override
    public List<String> loadUserFiTypes() {
        HashSet<Long> fiIds = (HashSet<Long>) current.getCallerPrincipal().getFis();

        if (fiIds == null || fiIds.isEmpty()) {
            return Collections.emptyList();
        }

        String inStatement = DBUtil.get().generateConcatenatedInStatementWithIds("b.id", fiIds.stream().toList());
        String queryString = "select trim(b.fiType.code) from IN_BANKS b where " + inStatement;

        return em.createQuery(queryString, String.class).getResultList();
    }

    @Override

    public List<Long> loadUserFiTypeIds() {
        HashSet<Long> fiIds = (HashSet<Long>) current.getCallerPrincipal().getFis();

        if (fiIds == null || fiIds.isEmpty()) {
            return Collections.emptyList();
        }

        String inStatement = DBUtil.get().generateConcatenatedInStatementWithIds("b.id", fiIds.stream().toList());
        String queryString = "select distinct b.fiType.id from IN_BANKS b where " + inStatement;

        return em.createQuery(queryString, Long.class).getResultList();
    }

    @Override

    public void setGroupsForFiType(long currentFiTypeId, long changedFiTypeId, long criterionId, boolean isDefault) throws FinATypeException {
        List<Fi> changedFis = em.createQuery("select b from IN_BANKS b where b.fiType.id=:fiTypeid", Fi.class).setParameter("fiTypeid", changedFiTypeId).getResultList();
        List<Fi> currentFis = em.createQuery("select fi from IN_BANKS fi where fi.fiType.id=:fiTypeId", Fi.class).setParameter("fiTypeId", currentFiTypeId).getResultList();

        List<PeerGroup> groups = em.createQuery("select g from IN_BANK_GROUPS g where g.parentId=:criterionId", PeerGroup.class).setParameter("criterionId", criterionId).getResultList();
        /**
         * update old fi dependencies
         */

        for (Fi fi : currentFis) {
            for (PeerGroup pg : groups) {
                if (fi.getPeerGroup().contains(pg)) {
                    fi.getPeerGroup().remove(pg);
                }
            }
        }

        /**
         * if group is default change default criterion and set new fi groups,
         * else remove another fi type groups dependencies
         */
        if (isDefault) {
            for (Fi fi : changedFis) {
                setFiPeerGroups(fi.getId(), groups.subList(0, 1));
            }
        }
    }

    @Override

    public List<User> loadFiUsers(long id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> userRoot = query.from(User.class);

        Subquery<Long> subQuery = query.subquery(Long.class);
        Root<FiUser> fiUserRoot = subQuery.from(FiUser.class);
        subQuery.select(fiUserRoot.get(FiUser_.userId));
        subQuery.where(cb.equal(fiUserRoot.get(FiUser_.bankId), id));

        query.where(userRoot.get(User_.id).in(subQuery));

        return em.createQuery(query).getResultList();
    }

    @Override
    public Map<Long, Long> getFisAndRegionIds() {
        Map<Long, Long> result = new HashMap<>();
        TypedQuery<Object[]> query = em.createQuery("select fi.id,fi.regionId from IN_BANKS fi", Object[].class);
        List<Object[]> queryResultList = query.getResultList();
        for (Object[] qr : queryResultList) {
            result.put((Long) qr[0], (Long) qr[1]);
        }
        return result;
    }

    @Override
    public Map<String, Long> getNumberOfFisByType(int limit, int offset) {
        Map<String, Long> result = new HashMap<>();
        TypedQuery<Object[]> query = em.createQuery("select b.fiType.code,count (b.id) from IN_BANKS b where b.disable=:disabled group by b.fiType.code", Object[].class).setParameter("disabled", false);
        if (limit > 0) {
            query.setFirstResult(offset);
            query.setMaxResults(limit);
        }
        List<Object[]> queryReslt = query.getResultList();
        for (Object[] qr : queryReslt) {
            result.put((String) qr[0], (Long) qr[1]);
        }
        return result;
    }

    @Override
    public Map<Long, Long> getNumberOfFisByRegion() {
        Map<Long, Long> result = new HashMap<>();
        TypedQuery<Object[]> query = em.createQuery("select b.regionId,count (b.id) from IN_BANKS b group by b.regionId", Object[].class);
        List<Object[]> queryReslt = query.getResultList();
        for (Object[] qr : queryReslt) {
            result.put((Long) qr[0], (Long) qr[1]);
        }
        return result;
    }

    @Override
    public List<Long> loadFiIdsByTypeId(long typeId) {
        long userId = current.getCurrentUserId();
        List<Long> userFiIds = em.createQuery("SELECT b.id FROM IN_BANKS b,SYS_USER_BANKS sub where b.fiType.id = :typeId and b.id=sub.bankId and sub.userId=:userId", Long.class).setParameter("typeId", typeId).setParameter("userId", userId).getResultList();
        List<Long> result = new ArrayList<>(userFiIds);
        List<Long> roleFiIds = em.createQuery("select distinct b.id from IN_BANKS b," + "SYS_ROLE_BANKS  ub where ub.bankId=b.id and b.fiType.id=:fiTypeId and ub.roleId in(select r.id from SYS_ROLES r,IN(r.users)ru where ru.id=:userId) ", Long.class).setParameter("userId", userId).setParameter("fiTypeId", typeId).getResultList();
        result.addAll(roleFiIds);

        return new ArrayList<>(new HashSet<>(result));
    }

    @Override
    public byte[] exportFis(List<Long> fiList) {
        Fis exportFis = new Fis();
        Fi currFi;
        List<FiManagement> managementList;
        List<FiBranch> branchList;
        List<LicenceHistory> licenceHistoryList;
        List<Beneficiary> beneficiaryList;
        Map<String, Long> langIdMapByCode = languageLocal.getLanguagesCodeIdMap();
        Map<String, Region> regionMapByCode = regionLocal.loadRegionsCodeMap();
        Map<String, Long> branchTypeCodeIdMap = loadBranchTypeCodeIdMap();
        FiXmlHelper helper = new FiXmlHelper(langIdMapByCode, regionMapByCode, branchTypeCodeIdMap, "dd/MM/yyyy");
        List<Long> userFis = current.getUserFis(current.getCurrentUserId());

        //filter permitted fis
        fiList.retainAll(userFis);

        for (long id : fiList) {
            currFi = em.find(Fi.class, id);
            managementList = loadFiManagement(id);
            branchList = loadFiBranchs(id);
            licenceHistoryList = (List<LicenceHistory>) loadFiLicenceHistories(id);
            beneficiaryList = beneficiaryLocal.load(id, -1, -1, FiBeneficiaryFilterType.ALL, null);
            Long regionId = currFi.getRegionId();
            String regionCode = regionId != null ? regionLocal.getRegionWithId(regionId).getCode() : null;
            exportFis.getFi().add(helper.fiToXmlModel(currFi, regionCode, managementList, branchList, licenceHistoryList, beneficiaryList));
        }

        return FiXmlParser.getInstance().exportFis(exportFis);
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public void importFis(byte[] file, String langCode) {
        FiImportResult importResult = new FiImportResult();
        Map<String, Long> langIdMapByCode = languageListSingleton.getLanguageIdMap();
        long langId = languageLocal.getLanguageByCodeOrDefault(langCode).getId();
        int maxRegionLevel = Integer.parseInt(regionLocal.getProperties(new ArrayList<Long>(langIdMapByCode.values()).get(0)).get(0));
        Map<String, Region> regionMapByCode = regionLocal.loadRegionsCodeMap();
        FiXmlHelper helper = new FiXmlHelper(langIdMapByCode, regionMapByCode, "dd/MM/yyyy", loadBranchTypeCodeIdMap(), maxRegionLevel);
        List<Long> importedFiIds = new ArrayList<>();
        Fis importedFis = null;
        String currUserLogin = current.getCurrentUserLogin();
        try {
            importedFis = FiXmlParser.getInstance().importFis(file);
        } catch (Exception e) {
            importResult.getExceptions().add(new FinATypeException(e.getMessage()));
            log.error(e.getMessage(), e);
            updateFiImportProgress(0, 1, WsFiEventType.IMPORT, currUserLogin, importResult);
            return;
        }

        if (importedFis != null) {
            CustomPrincipal callerPrincipal = current.getCallerPrincipal();
            Collection<Long> currentUserFis = callerPrincipal.getFis();
            List<String> notUniqueEmails = getNonUniqueEmails(importedFis, currentUserFis);
            List<net.fina.server.fi.xml.Fi> xmlFis = importedFis.getFi();
            int totalFis = xmlFis.size();

            try {
                for (net.fina.server.fi.xml.Fi xmlFi : xmlFis) {
                    try {

                        Fi fi = helper.xmlModelToFi(xmlFi);
                        LegalPersonMetaInfo metaInfo = fi.getFiAdditionalInfo();

                        //import fi meta info
                        saveFiAdditionalInfo(fi, metaInfo, importResult);

                        List<LicenceHistory> historyList = helper.xmlModelToLicenseHistoryList(xmlFi.getHistories());

                        if (fiExists(fi.getCode()) && (xmlFi.isOverwrite() != null && xmlFi.isOverwrite())) {
                            Fi existingFi = findFiByCode(fi.getCode());

                            if (!checkIdentificationCodeUnique(fi.getIdentificationCode(), existingFi.getId()) || !currentUserFis.contains(existingFi.getId())) {
                                importResult.getExceptions().add(new FinATypeException(CommonUtil.compileMessageWithParams(MessagesUtil.getString("net.fina.exception.IdCodeNotUnique", langCode), fi.getIdentificationCode())));
                                importResult.getNotImportedFis().add(existingFi.getCode());
                                continue;
                            }

                            checkImportedFisEmailUnique(xmlFi, importResult, notUniqueEmails);
                            mergeFis(fi, existingFi, importResult);
                            fi = em.merge(existingFi);
                            importResult.getModifiedFis().add(fi.getCode());
                        } else {
                            fi = createImportedFi(fi, importResult, langCode);
                        }

                        if (fi != null) {
                            importedFiIds.add(fi.getId());
                            // import  management
                            List<FiManagement> managementList = helper.xmlModelToManagementList(xmlFi.getManagements(), fi);
                            addFiManagement(fi, managementList, importResult, langId);

                            //Import branches
                            List<FiBranch> branchList = helper.xmlModelToFiBranchList(xmlFi.getBranches(), fi);
                            importFiBranches(fi, branchList, langId);

                            //import licenses
                            addLicenseHistory((List<Licence>) fi.getLicences(), historyList, importResult);

                            //import fi meta info
                            saveFiAdditionalInfo(fi, metaInfo, importResult);

                            //import beneficiaries shareholders
                            List<Beneficiary> beneficiaries = helper.xmlToBeneficiaries(xmlFi.getBeneficiaries(), fi.getCode());
                            importBeneficiaries(beneficiaries, fi, importResult);

                            importResult.getImportedFis().add(fi.getCode());

                        }
                        //update import progress
                        updateFiImportProgress(xmlFis.indexOf(xmlFi), totalFis, WsFiEventType.IMPORT, currUserLogin, importResult);

                    } catch (FinATypeException | ParseException e) {
                        importResult.getExceptions().add(new FinATypeException(e.getMessage()));
                        log.error(e.getMessage(), e);
                        updateFiImportProgress(xmlFis.indexOf(xmlFi), totalFis, WsFiEventType.IMPORT, currUserLogin, importResult);

                    }
                }
            } catch (Throwable t) {
                log.error("Unexpected error: " + t.getMessage(), t);
                importResult.getExceptionMessages().add("General Error");
                updateFiImportProgress(totalFis, totalFis, WsFiEventType.IMPORT, currUserLogin, importResult);
            }
            if (!importResult.getExceptionMessages().isEmpty()) {
                //FI created but problem is in another components of FI
                if (!importedFiIds.isEmpty() && importResult.getImportedFis().isEmpty()) {
                    callerPrincipal.getFis().removeAll(importedFiIds);
                }
                sessionContext.setRollbackOnly();
            }

        }

    }

    private void importBeneficiaries(List<Beneficiary> beneficiaries, Fi fi, FiImportResult importResult) {
        for (Beneficiary b : beneficiaries) {

            try {
                importBeneficiary(b, fi);
            } catch (FinATypeException ex) {
                log.error(ex.getMessage(), ex);
                importResult.getExceptions().add(ex);
            }
        }

    }

    private void importBeneficiary(Beneficiary b, Fi fi) throws FinATypeException {
        //TODO validation and persist
        if (b.getPhysicalPerson() != null) {
            b.setPhysicalPerson(personLocal.findByIdentificationCodeAndRegion(b.getPhysicalPerson().getIdentificationNumber(), b.getPhysicalPerson().getCitizenship().getId()));
        } else if (b.getLegalPerson() != null) {
            b.setLegalPerson(legalPersonLocal.findByIdentificationNumber(b.getLegalPerson().getIdentificationNumber()));

            if (b.getFinalBeneficiaries() != null) {
                b.getFinalBeneficiaries().forEach(fb -> {
                    fb.setPerson(personLocal.findByIdentificationCodeAndRegion(fb.getPerson().getIdentificationNumber(), fb.getPerson().getCitizenship().getId()));
                });
            }
        }
        beneficiaryLocal.create(b, fi.getId());
    }

    @Override
    public Management findManagementByCode(String code) {
        List<Management> managementList = em.createQuery("SELECT m FROM IN_MANAGING_BODIES m WHERE trim(m.code)=:code", Management.class).setParameter("code", code).getResultList();
        if (!managementList.isEmpty()) {
            return managementList.get(0);
        }
        return null;
    }

    @Override
    public LicenceType getLicenceTypeByCode(String code) {
        return em.createNamedQuery("getLicenceTypeByCode", LicenceType.class).setParameter("code", code).getSingleResult();
    }

    @Override
    public List<User> loadFiTypeUsers(long fiTypeId) {
        return em.createQuery("select u from SYS_USERS u, IN(u.fis) f where f.fiType.id=:fiTypeId", User.class).setParameter("fiTypeId", fiTypeId).getResultList();
    }

    @Override
    public Map<Fi, String> loadFiRegionMap(Collection<Long> ids, long langId) {

        List<Fi> result = new ArrayList<>();
        if (ids == null || ids.isEmpty()) {
            result = em.createQuery("select fi from IN_BANKS fi", Fi.class).getResultList();
        } else {
            result = em.createQuery("select fi from IN_BANKS fi where fi.id in :ids", Fi.class).setParameter("ids", ids).getResultList();
        }

        List<Region> regions = regionLocal.loadRegions();

        Map<Fi, String> fisRegionsMap = new HashMap<>();

        result.forEach(fi -> {
            fisRegionsMap.put(fi, RegionUtil.getRegionalAddress(regions, fi, langId, "/", false));
        });

        return fisRegionsMap;
    }

    @Override
    public List<Fi> loadFisByTypeId(long typeId) {
        return em.createQuery("select fi from IN_BANKS fi where fi.fiType.id=:ftypeId", Fi.class).setParameter("ftypeId", typeId).getResultList();
    }

    @Override
    public List<String> loadAllFiCodes() {
        return em.createQuery("select trim(fi.code) from IN_BANKS fi", String.class).getResultList();
    }

    @Override
    @SuppressWarnings("JpaQlInspection")
    public List<Fi> loadUserRoleFis(long userId) {
        return em.createQuery("select distinct new " + Fi.class.getName() + "( b.id,b.code, b.description,b.shortName,b.fiType) from IN_BANKS b," + "SYS_ROLE_BANKS  ub where ub.bankId=b.id and ub.roleId in(select r.id from SYS_ROLES r,IN(r.users)ru where ru.id=:userId) ", Fi.class).setParameter("userId", userId).getResultList();
    }

    @Override
    public List<Long> loadAllUsersInFis(List<Long> fiIds) {
        if (fiIds == null || fiIds.isEmpty()) {
            return Collections.emptyList();
        }

        String inStatement = DBUtil.get().generateConcatenatedInStatementWithIds("f.id", fiIds);
        String queryString = "select distinct u.id from SYS_USERS u JOIN u.fis f where " + inStatement;

        return em.createQuery(queryString, Long.class).getResultList();
    }


    @Override
    public String getFiTypeCodeByFiCode(String fiCode) {
        return findFiByCode(fiCode).getFiType().getCode();
    }


    @Override
    public long countBranches(long fiId) {
        return em.createQuery("select count(fb.id) from IN_BANK_BRANCHES fb where fb.bankId=:fiId and (fb.deleted=:isDeleted or fb.deleted is null)", Long.class).setParameter("fiId", fiId).setParameter("isDeleted", false).getSingleResult();
    }

    @Override
    public long countBranches(long fiId, long fiBranchTypeId, Map<FiBranchFilter, Object> filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<FiBranch> root = cq.from(FiBranch.class);

        cq.select(cb.countDistinct(root.get(FiBranch_.id)));

        List<Predicate> predicates = getFiBranchPredicates(cb, root, filter);
        predicates.add(cb.equal(root.get(FiBranch_.deleted), Boolean.FALSE));
        predicates.add(cb.and(cb.or(cb.equal(root.get(FiBranch_.deleted), false), cb.isNull(root.get(FiBranch_.deleted)))));
        if (fiBranchTypeId > 0) {
            predicates.add(cb.equal(root.get(FiBranch_.fiBranchType).get(FiBranchType_.id), fiBranchTypeId));
        }
        predicates.add(cb.equal(root.get(FiBranch_.bankId), fiId));

        cq.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Long> query = em.createQuery(cq);

        return query.getSingleResult();
    }

    @Override
    public Fi getFiById(long fiId) {
        return em.find(Fi.class, fiId);
    }

    @Override
    public List<FiBranchTypeCounterModel> loadFiBranchCountGroupByFiType(long fiId, long langId) {
        List<FiBranchTypeCounterModel> result = new ArrayList<>();

        List<Tuple> tuples = em.createQuery("select fb.fiBranchType.name,count(fb.id) from IN_BANK_BRANCHES  fb  where fb.bankId=:bankId and fb.deleted=false group by fb.fiBranchType", Tuple.class).setParameter("bankId", fiId).getResultList();

        for (Tuple tuple : tuples) {
            Description description = tuple.get(0, Description.class);
            int count = tuple.get(1, Long.class).intValue();
            result.add(new FiBranchTypeCounterModel(description.getDescription(langId), count));
        }

        return result;

    }

    @Override
    public void checkUserHasFiAccess(long fiId) throws FinATypeException {
        CustomPrincipal principal = current.getCallerPrincipal();
        if (fiId > 0 && !principal.getFis().contains(fiId)) {
            String errMessage = MessageFormat.format("User {0} does not have permission on fi [{1}]", principal.getName(), fiId);
            log.error(errMessage);
            throw new FinATypeException(FinATypeException.Type.INVALID_PERMISSIONS);
        }
    }

    @Override
    public boolean checkIdentificationCodeUnique(String identificationCode, long id) {
        return em.createNamedQuery("checkFiIdentificationCodeUnique").setParameter("identificationCode", identificationCode).setParameter("id", id).getResultList().isEmpty();
    }

    @Override
    public Fi findByIdentificationCode(String identificationCode) {
        List<Fi> fis = em.createQuery("select f from IN_BANKS f where f.identificationCode=:code", Fi.class).setParameter("code", identificationCode.trim()).getResultList();
        return fis.isEmpty() ? null : fis.get(0);
    }

    public List<Beneficiary> getFiShares(Map<PersonFilter, Object> filterMap, long fiId) throws FinATypeException {
        checkUserHasFiAccess(fiId);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Beneficiary> cq = cb.createQuery(Beneficiary.class);
        LegalPerson legalPerson = legalPersonLocal.getLegalPersonByFiId(fiId);

        Root<LegalPerson> root = cq.from(LegalPerson.class);
        Join<LegalPerson, Beneficiary> beneficiaryJoin = root.join(LegalPerson_.beneficiaries, JoinType.LEFT);
        Join<LegalPerson, Region> countryJoin = root.join(LegalPerson_.country, JoinType.LEFT);

        cq.multiselect(beneficiaryJoin.get(Beneficiary_.id), beneficiaryJoin.get(Beneficiary_.share), beneficiaryJoin.get(Beneficiary_.creationDate), root.get(LegalPerson_.id), root.get(LegalPerson_.name), root.get(LegalPerson_.identificationNumber), root.get(LegalPerson_.fiId), countryJoin);

        List<Predicate> predicates = getShareFilterPredicates(cb, root, beneficiaryJoin, filterMap);
        predicates.add(cb.equal(beneficiaryJoin.get(Beneficiary_.legalPerson).get(LegalPerson_.id), legalPerson.getId()));

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(beneficiaryJoin.get(Beneficiary_.creationDate)));

        TypedQuery<Beneficiary> query = em.createQuery(cq);
        return query.getResultList();
    }

    @Override
    public List<CriminalRecord> loadFiCriminalRecords(long fiId) throws FinATypeException {
        checkUserHasFiAccess(fiId);
        return getFiById(fiId).getCriminalRecords();
    }

    @Override
    public CriminalRecord createFiCriminalRecord(long fiId, CriminalRecord criminalRecord) throws FinATypeException {
        checkUserHasFiAccess(fiId);

        Fi fi = getFiById(fiId);
        em.persist(criminalRecord);

        fi.getCriminalRecords().add(criminalRecord);

        return criminalRecord;
    }

    @Override
    public void updateFiCriminalRecord(long fiId, CriminalRecord criminalRecord) throws FinATypeException {
        checkUserHasFiAccess(fiId);
        em.merge(criminalRecord);
        Fi fi = em.find(Fi.class, fiId);
        fi.setModifiedAt(new Date());
        em.merge(fi);
    }

    @Override
    public void deleteFiCriminalRecord(long fiId, long recordId) throws FinATypeException {
        checkUserHasFiAccess(fiId);

        Fi fi = getFiById(fiId);
        Optional<CriminalRecord> recordOptional = fi.getCriminalRecords().stream().filter(s -> s.getId() == recordId).findFirst();
        if (recordOptional.isPresent()) {
            CriminalRecord criminalRecord = recordOptional.get();
            fi.getCriminalRecords().remove(criminalRecord);
            em.remove(criminalRecord);
        }
    }

    @Override
    public Licence loadFiLicense(long licenseId) {
        return em.find(Licence.class, licenseId);
    }

    @Override
    public long countFiLicenses(long fiId) {
        return em.createQuery("select count(l.id) from IN_LICENCES l where l.fi.id=:fiId", Long.class).setParameter("fiId", fiId).getSingleResult();
    }

    @Override
    public Fi getFiByIdentificationCode(String identificationNumber) {
        return em.createQuery("select fi from IN_BANKS fi where trim(fi.identificationCode)=:identificationCode", Fi.class).setParameter("identificationCode", identificationNumber.trim()).getSingleResult();
    }

    @Override
    public long createFiShare(long fiId, ShareMetaModel share) throws FinATypeException {
        if (fiId == share.getCompany().getFiId()) {
            throw new FinATypeException("Fi cannot create a share for itself");
        }
        LegalPerson benficiaryPerson = legalPersonLocal.getLegalPersonByFiId(fiId);
        LegalPerson ownerPerson = legalPersonLocal.getLegalPersonById(share.getCompany().getId());

        double totalShare = ownerPerson.getBeneficiaries().stream().mapToDouble(Beneficiary::getShare).sum() + share.getSharePercentage();

        if (totalShare > 100) {
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE);
        }

        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setShare(share.getSharePercentage());
        beneficiary.setCreationDate(share.getShareDate() == null ? new Date() : share.getShareDate());
        beneficiary.setActive(true);

        beneficiary.setLegalPerson(benficiaryPerson);

        em.persist(beneficiary);

        ownerPerson.getBeneficiaries().add(beneficiary);

        //beneficiary connection
        if (ownerPerson.getFiId() > 0) {
            legalPersonLocal.createFiLegalPersonRelation(benficiaryPerson.getId(), ownerPerson.getFiId(), FiLegalPersonConnectionType.BENEFICIARY);
        }
        //current fi connection to share fin
        legalPersonLocal.createFiLegalPersonRelation(ownerPerson.getId(), fiId, FiLegalPersonConnectionType.OTHER_SHARE);


        return beneficiary.getId();
    }

    @Override
    public void updateFiShare(long fiId, long beneficiaryId, long legalPersonId, double sharePercentage, Date shareDate) throws FinATypeException {
        LegalPerson owner = legalPersonLocal.getLegalPersonById(legalPersonId);

        Beneficiary beneficiary = em.find(Beneficiary.class, beneficiaryId);

        if (beneficiary.getLegalPerson().getFiId() == fiId) {
            double totalShare = owner.getBeneficiaries().stream().filter(b -> b.getId() != beneficiaryId).mapToDouble(Beneficiary::getShare).sum() + sharePercentage;

            if (totalShare > 100) {
                throw new FinATypeException(FinATypeException.Type.INVALID_VALUE);
            }
            beneficiary.setShare(sharePercentage);
            beneficiary.setCreationDate(shareDate);
        }
    }

    @Override
    public void deleteFiShare(long companyId, long shareId) {
        LegalPerson legalPerson = legalPersonLocal.getLegalPersonById(companyId);

        Beneficiary beneficiary = em.find(Beneficiary.class, shareId);
        em.remove(beneficiary);
        legalPerson.getBeneficiaries().remove(beneficiary);

    }

    @Override
    public List<Fi> loadActiveFisByType(long fiTypeId, Boolean active) {
        return loadActiveFisByType(Collections.singletonList(fiTypeId), active);
    }

    @Override
    public List<Fi> loadActiveFisByType(List<Long> fiTypeIds, Boolean filterActive) {
        if (fiTypeIds == null || fiTypeIds.isEmpty()) {
            return new ArrayList<>();
        }

        String queryStr = "select new net.fina.server.fi.entity.Fi(b.id, b.code, b.description) " +
                "from IN_BANKS b " +
                "where b.fiType.id in (:fiTypeIds)";

        if (filterActive != null) {
            queryStr += " and b.disable = :disabled";
        }

        TypedQuery<Fi> query = em.createQuery(queryStr, Fi.class)
                .setParameter("fiTypeIds", fiTypeIds);

        if (filterActive != null) {
            query.setParameter("disabled", !filterActive);
        }

        return query.getResultList();
    }

    @Override
    public FiBranch getFiBranchById(long id) {
        return em.find(FiBranch.class, id);
    }

    @Override
    public List<BusinessEntityType> loadBusinessEntityTypes() {
        return em.createQuery("select t from IN_BUSINESS_ENTITY_TYPE t", BusinessEntityType.class).getResultList();
    }

    @Override
    public Collection<EconomicEntityType> loadEconomicEntityTypes() {
        return em.createQuery("select t from IN_ECONOMIC_ENTITY_TYPE t", EconomicEntityType.class).getResultList();
    }

    @Override
    public Collection<EquityFormType> loadEquityFormTypes() {
        return em.createQuery("select t from IN_EQUITY_FORM_TYPE t", EquityFormType.class).getResultList();
    }

    @Override
    public Collection<ManagementFormType> loadManagementFormTypes() {
        return em.createQuery("select t from IN_MANAGEMENT_FORM_TYPE t", ManagementFormType.class).getResultList();
    }

    @Override
    public Map<Long, FiTypeSimpleModel> loadFiIDTypeMap() {
        Map<Long, FiTypeSimpleModel> result = new HashMap<>();
        long langId = ThreadLocalHolder.getLanguage().getId();

        List<Tuple> tuples = em.createQuery("select fi.id,fi.fiType from IN_BANKS  fi ", Tuple.class).getResultList();

        for (Tuple tuple : tuples) {
            long fiID = (long) tuple.get(0);
            FiType fiType = (FiType) tuple.get(1);
            result.put(fiID, new FiTypeSimpleModel(fiType.getId(), fiType.getCode(), fiType.getDescription().getDescription(langId)));
        }

        return result;
    }

    @Override
    public void removePersonConnectionByConnectionId(long id) {
        em.remove(em.find(FiPersonConnection.class, id));
    }

    @Override
    public Map<String, Long> loadFiTypeCodeIdMap() {
        return em.createQuery("select code,id  from IN_BANK_TYPES ", Tuple.class)
                .getResultStream().collect(
                        Collectors.toMap(
                                tuple -> (String) tuple.get(0),
                                tuple -> ((Number) tuple.get(1)).longValue()
                        )
                );
    }

    @Override
    @SuppressWarnings("JpaQlInspection")
    public List<Fi> loadFisByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        String concatenatedInd = DBUtil.get().generateConcatenatedInStatement("b.id", ids, Long.class);
        return em.createQuery("select new " + Fi.class.getName() + " (b.id, b.code, b.description) from IN_BANKS b where  (" + concatenatedInd + ")", Fi.class)
                .getResultList();
    }

    @Override
    @SuppressWarnings("JpaQlInspection")
    public List<Fi> loadFisByPeerGroup(long groupId) {
        if (groupId <= 0) return new ArrayList<>();
        return em.createQuery("select new " + Fi.class.getName() + " (b.id, b.code, b.description) from IN_BANKS b join b.peerGroup pg where pg.id=:groupId", Fi.class)
                .setParameter("groupId", groupId)
                .getResultList();

    }

    @Override
    public Fi updateFiPermittedUsers(long fiId, List<Long> permittedUsers, List<String> responsibleUsers) {
        Fi fi = getFiById(fiId);

        List<User> fiUsers = current.loadUsersByLogins(responsibleUsers);
        fi.setUsers(fiUsers);

        em.merge(fi);

        for (User user : fiUsers) {
            if (!permittedUsers.contains(user.getId())) {
                permittedUsers.add(user.getId());
            }
        }


        // save user permissions for this bank
        if (!permittedUsers.isEmpty()) {
            List<UserFi> userFis = new ArrayList<>();
            for (long permittedUserId : permittedUsers) {
                UserFi userFi = new UserFi();
                userFi.setBankId(fi.getId());
                userFi.setUserId(permittedUserId);
                userFis.add(userFi);
            }
            current.saveUserFiPermissions(fiId, userFis);
        }

        return fi;
    }

    @Override
    public void removeFiPermission(long fiId) {
        em.createQuery("delete from SYS_USER_BANKS where bankId=:fiId").setParameter("fiId", fiId).executeUpdate();
        em.createQuery("delete from SYS_ROLE_BANKS where bankId=:fiId").setParameter("fiId", fiId).executeUpdate();
        current.getCallerPrincipal().getFis().remove(fiId);
    }

    private Map<String, Long> loadBranchTypeCodeIdMap() {
        return fiBranchTypeLocal.load().stream().collect(Collectors.toMap(FiBranchType::getCode, FiBranchType::getId));
    }

    private void setDescriptionIdForEnvers(Description existing, Description newDescription, long langId) {

        if (existing.getDescription(langId) != null && newDescription != null && newDescription.getDescription(langId) != null && !existing.getDescription(langId).equals(newDescription.getDescription(langId))) {
            newDescription.setNameStrId(0);
        }
    }

    private void createManagementFiPerson(Person person) {
        if (person.getPositions() != null) {
            Iterator<PersonPosition> iterator = person.getPositions().iterator();
            while (iterator.hasNext()) {
                PersonPosition position = iterator.next();
                LegalPerson company = legalPersonLocal.findByIdentificationNumber(position.getCompany().getIdentificationNumber());
                if (company != null) {
                    position.setCompany(company);
                } else {
                    iterator.remove();
                }
            }
        }

        person = em.merge(person);
        for (PersonPosition position : person.getPositions()) {
            em.persist(position);
        }
    }

    private List<Predicate> getFilterPredicate(CriteriaBuilder cb, Root<Fi> root, Map<FiFilter, Object> filter, long langId) {
        List<Predicate> predicates = new ArrayList<>();
        if (filter != null) {
            for (Map.Entry<FiFilter, Object> entry : filter.entrySet()) {
                if (entry.getValue() != null) {
                    switch (entry.getKey()) {
                        case CODE:
                            predicates.add(cb.like(cb.lower(cb.trim(root.get(Fi_.code))), "%" + entry.getValue().toString().toLowerCase() + "%"));
                            break;
                        case TYPE_ID:
                            predicates.add(cb.equal(root.get(Fi_.fiType).get(FiType_.id), entry.getValue()));
                            break;
                        case USER_ID:
                            List<Predicate> cPredicates = new ArrayList<>();
                            CriteriaQuery<UserFi> fiCriteriaQuery = cb.createQuery(UserFi.class);
                            Subquery<Long> fiUserSubquery = fiCriteriaQuery.subquery(Long.class);
                            Root<UserFi> fiUserQRoot = fiUserSubquery.from(UserFi.class);
                            fiUserSubquery.select(fiUserQRoot.get(UserFi_.BANK_ID));
                            fiUserSubquery.where(cb.equal(fiUserQRoot.get(UserFi_.USER_ID), current.getCurrentUserId()));
                            cPredicates.add(root.get(Fi_.id).in(fiUserSubquery));

                            List<Long> roleIds = current.getCallerPrincipal().getRoles();

                            CriteriaQuery<RoleFi> fiRoleQuery = cb.createQuery(RoleFi.class);
                            Subquery<Long> fiRoleSubquery = fiRoleQuery.subquery(Long.class);
                            Root<RoleFi> fiRoleQRoot = fiRoleSubquery.from(RoleFi.class);
                            fiRoleSubquery.select(fiRoleQRoot.get(RoleFi_.BANK_ID));
                            fiRoleSubquery.where(fiRoleQRoot.get(RoleFi_.ROLE_ID).in(roleIds));
                            cPredicates.add(root.get(Fi_.id).in(fiRoleSubquery));

                            predicates.add(cb.and(cb.or(cPredicates.toArray(new Predicate[0]))));
                            break;
                        case REGION_IDS:
                            Collection<Long> regionIds = new ArrayList<>((Collection<Long>) entry.getValue());
                            predicates.add(root.get(Fi_.regionId).in(regionIds));
                            break;
                        case TYPE_CODE:
                            predicates.add(cb.equal(root.get(Fi_.fiType).get(FiType_.code), entry.getValue()));
                            break;
                        case EXCLUDE_DISABLED:
                            boolean excludeDisabled = entry.getValue() instanceof Boolean && (Boolean) entry.getValue();
                            if (excludeDisabled) {
                                predicates.add(cb.equal(root.get(Fi_.disable), false));
                            }
                            break;
                        case FI_IDS:
                            Collection<Long> fiIds = (Collection<Long>) entry.getValue();
                            Collection<Long> userFis = current.getCallerPrincipal().getFis();
                            List<Long> filtered = fiIds.stream().distinct().filter(userFis::contains).collect(Collectors.toList());

                            if (!filtered.isEmpty()) {
                                List<Predicate> fisPredList = DBUtil.get().buildAndSplitPredicates(cb, root.get(Fi_.id), Long.class, filtered);
                                predicates.add(cb.and(cb.or(fisPredList.toArray(new Predicate[0]))));
                            }
                            break;
                        case CONTACT_PERSON:
                            predicates.add(cb.like(cb.lower(cb.trim(root.get(Fi_.contactPerson))), "%" + entry.getValue().toString().toLowerCase() + "%"));
                            break;
                        case REPRESENTATIVE_PERSON:
                            predicates.add(cb.like(cb.lower(cb.trim(root.get(Fi_.representativePerson))), "%" + entry.getValue().toString().toLowerCase() + "%"));
                            break;
                        case CREATED_AT_FROM:
                            predicates.add(cb.greaterThanOrEqualTo(root.get(Fi_.createdAt), new Date(Long.parseLong(entry.getValue().toString()))));
                            break;
                        case CREATED_AT_TO:
                            predicates.add(cb.lessThanOrEqualTo(root.get(Fi_.createdAt), new Date(Long.parseLong(entry.getValue().toString()))));
                            break;
                        case MODIFIED_AT_FROM:
                            predicates.add(cb.greaterThanOrEqualTo(root.get(Fi_.modifiedAt), new Date(Long.parseLong(entry.getValue().toString()))));
                            break;
                        case MODIFIED_AT_TO:
                            predicates.add(cb.lessThanOrEqualTo(root.get(Fi_.modifiedAt), new Date(Long.parseLong(entry.getValue().toString()))));
                            break;
                        case LICENSE_CODE:
                            CriteriaQuery<Licence> licenseQuery = cb.createQuery(Licence.class);
                            Subquery<Long> licenseSubQuery = licenseQuery.subquery(Long.class);
                            Root<Licence> fiUserRoot = licenseSubQuery.from(Licence.class);
                            licenseSubQuery.select(fiUserRoot.get(Licence_.fi).get(Fi_.id));
                            licenseSubQuery.where(cb.like(fiUserRoot.get(Licence_.code), "%" + entry.getValue().toString() + "%"));
                            predicates.add(root.get(Fi_.id).in(licenseSubQuery));
                            break;
                        case ADDRESS:

                            List<Long> fiAddressIds = em.createQuery("select b.id from SYS_STRINGS s inner join IN_BANKS b on b.addressDescription=s.id and s.langId =:langId and s.value like :value", Long.class)
                                    .setParameter("langId", langId)
                                    .setParameter("value", "%" + entry.getValue().toString() + "%")
                                    .getResultList();


                            predicates.add(root.get(Fi_.id).in(fiAddressIds.isEmpty() ? Collections.singletonList(-1L) : fiAddressIds));
                            break;
                        case DESCRIPTION:

                            List<Long> fiDescriptionIds = em.createQuery("select b.id from SYS_STRINGS s inner join IN_BANKS b on b.description=s.id and s.langId =:langId and s.value like :value", Long.class)
                                    .setParameter("langId", langId)
                                    .setParameter("value", "%" + entry.getValue().toString() + "%")
                                    .getResultList();


                            predicates.add(root.get(Fi_.id).in(fiDescriptionIds.isEmpty() ? Collections.singletonList(-1L) : fiDescriptionIds));

                            break;
                        case LEGAL_FORM:
                            predicates.add(cb.like(cb.lower(cb.trim(root.get(Fi_.legalForm))), "%" + entry.getValue().toString().toLowerCase() + "%"));
                            break;
                        case IDENTIFICATION_CODE:
                            predicates.add(cb.like(cb.lower(cb.trim(root.get(Fi_.identificationCode))), "%" + entry.getValue().toString().toLowerCase() + "%"));
                            break;
                        case REGISTERED_AT_FROM:
                            predicates.add(cb.greaterThanOrEqualTo(root.get(Fi_.registrationDate), new Date(Long.parseLong(entry.getValue().toString()))));
                            break;
                        case REGISTERED_AT_TO:
                            predicates.add(cb.lessThanOrEqualTo(root.get(Fi_.registrationDate), new Date(Long.parseLong(entry.getValue().toString()))));
                            break;
                        case DECREE_NUMBER:
                            predicates.add(cb.like(root.get(Fi_.decreeNumber), "%" + entry.getValue() + "%"));
                            break;
                        case INSPECTION_END_DATE_FROM:
                            predicates.add(cb.greaterThanOrEqualTo(root.get(Fi_.INSPECTION_END_DATE), new Date(Long.parseLong(entry.getValue().toString()))));
                            break;
                        case INSPECTION_END_DATE_TO:
                            predicates.add(cb.lessThanOrEqualTo(root.get(Fi_.INSPECTION_END_DATE), new Date(Long.parseLong(entry.getValue().toString()))));
                            break;
                    }
                }
            }
        }
        return predicates;
    }

    private List<Predicate> getShareFilterPredicates(CriteriaBuilder cb, Root<LegalPerson> root, Join<LegalPerson, Beneficiary> beneficiaryJoin, Map<PersonFilter, Object> filterMap) {
        List<Predicate> predicates = new ArrayList<>();
        long langId = ThreadLocalHolder.getLanguage().getId();

        if (filterMap != null && !filterMap.isEmpty()) {
            for (Map.Entry<PersonFilter, Object> entry : filterMap.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }

                switch (entry.getKey()) {
                    case NAME:

                        List<Long> descriptionIds = em.createQuery("select lp.id from SYS_STRINGS s inner join IN_LEGAL_PERSONS lp on lp.name=s.id and s.langId=:langId and s.value like :value", Long.class)
                                .setParameter("langId", langId).setParameter("value", "%" + entry.getValue().toString() + "%")
                                .getResultList();

                        predicates.add(root.get(LegalPerson_.id).in(descriptionIds.isEmpty() ? Collections.singletonList(-1L) : descriptionIds));
                        break;
                    case ID_NUMBER:
                        predicates.add(cb.like(root.get(LegalPerson_.identificationNumber), "%" + entry.getValue() + "%"));
                        break;
                    case COUNTRY_ID:
                        Join<LegalPerson, Region> countryJoin = root.join(LegalPerson_.country, JoinType.LEFT);
                        predicates.add(cb.equal(countryJoin.get(Region_.ID), entry.getValue()));
                        break;
                    case BENEFICIARY_CREATION_DATE_FROM:
                        predicates.add(cb.greaterThanOrEqualTo(beneficiaryJoin.get(Beneficiary_.creationDate), (Date) entry.getValue()));
                        break;
                    case BENEFICIARY_CREATION_DATE_TO:
                        predicates.add(cb.lessThanOrEqualTo(beneficiaryJoin.get(Beneficiary_.creationDate), (Date) entry.getValue()));
                        break;
                }
            }
        }
        return predicates;
    }

    private boolean checkPassportNumberUnique(Person person) {
        return em.createQuery("select p.id from IN_PERSONS p where p.id<>:id and lower(trim(p.passportNumber))=:passportNumber ")
                .setParameter("id", person.getId())
                .setParameter("passportNumber", person.getPassportNumber().trim().toLowerCase())
                .getResultList().isEmpty();
    }

    private boolean isCodeUniqueManagement(String code, long id) {
        Query codeUniqueQuery = em.createQuery("select c.id from IN_MANAGING_BODIES c where trim(c.code)=:code and c.id<>:id");
        codeUniqueQuery.setParameter("id", id);
        codeUniqueQuery.setParameter("code", code);
        return codeUniqueQuery.getResultList().isEmpty();
    }

    private boolean isCodeUniqueFiBranch(String code) {
        Query codeUniqueQuery = em.createQuery("select ibb.id from IN_BANK_BRANCHES ibb where trim(ibb.code)=:code");
        codeUniqueQuery.setParameter("code", code);
        return codeUniqueQuery.getResultList().isEmpty();
    }

    private void importFiBranches(Fi fi, List<FiBranch> branchList, long langId) throws FinATypeException {
        for (FiBranch branch : branchList) {
            branch.setBankId(fi.getId());
            saveFiBranch(branch, langId, true);
        }
    }

    private void addLicenseHistory(List<Licence> licences, List<LicenceHistory> historyList, FiImportResult importResult) {
        for (LicenceHistory history : historyList) {
            Licence license = licences.stream().filter(l -> l.getCode().equals(history.getLicence().getCode())).findFirst().orElseGet(null);
            if (license != null) {
                history.setLicence(license);
                em.persist(history);
            } else {
                importResult.getNonExistentLicenses().add(history.getLicence().getCode());
            }
        }
    }


    private void addFiManagement(Fi fi, List<FiManagement> managementList, FiImportResult importResult, long langId) throws FinATypeException {
        for (FiManagement fiManagement : managementList) {
            Management m = findManagementByCode(fiManagement.getManagement().getCode());
            if (m != null) {
                Person person = fiManagement.getFiPerson().getPerson();
                if (personLocal.findByIdentificationCodeAndRegion(person.getIdentificationNumber(), person.getCitizenship().getId()) == null) {
                    if (!checkPassportNumberUnique(person)) {
                        throw new FinATypeException("Person with passport number [" + person.getPassportNumber() + "] already exists!");
                    } else {
                        createManagementFiPerson(person);
                        fiManagement.getFiPerson().setPerson(person);
                    }

                }
                fiManagement.setManagement(m);
                fiManagement.setFiId(fi.getId());
                saveFiManagement(fiManagement, langId);
            } else {
                importResult.getNonExistentManagements().add(fiManagement.getManagement().getCode());
            }
        }
    }

    private Fi createImportedFi(Fi fi, FiImportResult importResult, String langCode) throws FinATypeException {
        List<Licence> licenceList = (List<Licence>) fi.getLicences();
        FiType type = findFiTypeByCode(fi.getFiType().getCode());

        if (type != null) {
            fi.setFiType(type);
        } else {
            importResult.getNonExistentTypes().add(fi.getFiType().getCode());
            importResult.getNotImportedFis().add(fi.getCode());
            return null;
        }

        if (!checkIdentificationCodeUnique(fi.getIdentificationCode(), fi.getId()) || legalPersonLocal.findByIdentificationNumber(fi.getIdentificationCode()) != null) {
            importResult.getExceptions().add(new FinATypeException(CommonUtil.compileMessageWithParams(MessagesUtil.getString("net.fina.exception.IdCodeNotUnique", langCode), fi.getIdentificationCode())));
            importResult.getNotImportedFis().add(fi.getCode());
            return null;
        }

        if (!checkCodeUnique(fi)) {
            importResult.getExceptions().add(new FinATypeException(CommonUtil.compileMessageWithParams(MessagesUtil.getString("net.fina.fi.exception.codeUnique", langCode), fi.getCode())));
            importResult.getNotImportedFis().add(fi.getCode());
            return null;
        }

        List<PeerGroup> fiGroups = new ArrayList<>();
        fi.getPeerGroup().stream().forEach(pg -> {
            PeerGroup group = peerGroupLocal.getPeerGroupByCode(pg.getCode());
            if (group != null) {
                fiGroups.add(group);
            } else {
                importResult.getNonExistentGroups().add(pg.getCode());
            }
        });
        fi.setPeerGroup(fiGroups);

        List<Licence> createdLicenses = new ArrayList<>();
        for (Licence license : licenceList) {
            LicenceType licType = findLicenseTypeByCode(license.getLicenseType().getCode());
            if (licType != null) {
                license.setLicenseType(licType);
                license.setFi(fi);
                createdLicenses.add(license);
            } else {
                importResult.getNonExistentLicenseTypes().add(license.getLicenseType().getCode());
            }
        }
        fi.setLicences(createdLicenses.isEmpty() ? null : createdLicenses);

        Fi savedFi = save(fi, langCode);
        current.getCallerPrincipal().getFis().add(savedFi.getId());

        importFiLicenses(fi.getLicences());

        return savedFi;
    }

    private void saveFiAdditionalInfo(Fi fi, LegalPersonMetaInfo metaInfo, FiImportResult importResult) {
        if (metaInfo != null) {
            boolean validate = false;
            if (metaInfo.getBusinessEntityType() != null) {
                BaseLegalEntityType saved = saveFiAdditionalInfoCatalog(metaInfo.getBusinessEntityType());

                if (saved != null) {
                    metaInfo.setBusinessEntityType((BusinessEntityType) saved);
                    validate = true;
                } else {
                    importResult.getExceptions().add(new FinATypeException("Fi Meta Info Business Type with code [" + metaInfo.getBusinessEntityType().getCode().trim() + "] does not exist, Skipping it..."));
                }
            }
            if (metaInfo.getEconomicEntityType() != null) {
                BaseLegalEntityType saved = saveFiAdditionalInfoCatalog(metaInfo.getEconomicEntityType());

                if (saved != null) {
                    metaInfo.setBusinessEntityType((BusinessEntityType) saved);
                    validate = true;
                } else {
                    importResult.getExceptions().add(new FinATypeException("Fi Meta Info Economic Entity Type with code [" + metaInfo.getBusinessEntityType().getCode().trim() + "] does not exist, Skipping it..."));
                }
            }
            if (metaInfo.getEquityFormType() != null) {
                BaseLegalEntityType saved = saveFiAdditionalInfoCatalog(metaInfo.getEquityFormType());

                if (saved != null) {
                    metaInfo.setBusinessEntityType((BusinessEntityType) saved);
                    validate = true;
                } else {
                    importResult.getExceptions().add(new FinATypeException("Fi Meta Info Equity Form Type with code [" + metaInfo.getBusinessEntityType().getCode().trim() + "] does not exist, Skipping it..."));
                }
            }
            if (metaInfo.getManagementFormType() != null) {
                BaseLegalEntityType saved = saveFiAdditionalInfoCatalog(metaInfo.getManagementFormType());

                if (saved != null) {
                    metaInfo.setBusinessEntityType((BusinessEntityType) saved);
                    validate = true;
                } else {
                    importResult.getExceptions().add(new FinATypeException("Fi Meta Info Management Form Type with code [" + metaInfo.getBusinessEntityType().getCode().trim() + "] does not exist, Skipping it..."));
                }
            }

            if (validate) {
                em.persist(metaInfo);
                fi.setFiAdditionalInfo(metaInfo);
            } else {
                fi.setFiAdditionalInfo(null);
            }
        }
    }

    private BaseLegalEntityType saveFiAdditionalInfoCatalog(BaseLegalEntityType entity) {
        List<BaseLegalEntityType> existing = em.createQuery("select c from " + entity.getClass().getAnnotation(Entity.class).name() + " c where c.code=:code", BaseLegalEntityType.class).setParameter("code", entity.getCode().trim()).getResultList();

        if (!existing.isEmpty()) {
            return existing.get(0);
        }

        return null;
    }

    private void mergeFis(Fi source, Fi target, FiImportResult importResult) throws FinATypeException {
        if (source.getFiType().getCode() != null && !source.getFiType().getCode().isEmpty()) {
            FiType type = findFiTypeByCode(source.getFiType().getCode());
            if (type != null) {
                target.setFiType(type);
            } else {
                importResult.getNonExistentTypes().add(source.getFiType().getCode());
            }
        }

        if (source.getSwiftCode() != null && !source.getSwiftCode().isEmpty()) {
            target.setSwiftCode(source.getSwiftCode());
        }

        if (source.getPhone() != null && !source.getPhone().isEmpty()) {
            target.setPhone(source.getPhone());
        }

        if (source.getEmail() != null && !source.getEmail().isEmpty()) {
            target.setEmail(source.getEmail());
        }

        if (source.getFax() != null && !source.getFax().isEmpty()) {
            target.setFax(source.getFax());
        }

        if (source.getRegionId() != null) {
            target.setRegionId(source.getRegionId());
        }

        if (source.getDescription() != null) {
            for (Map.Entry<Long, String> desc : source.getDescription().getDescriptions().entrySet()) {
                target.getDescription().addDescription(desc.getKey(), desc.getValue());
            }
        }

        if (source.getShortName() != null) {
            for (Map.Entry<Long, String> desc : source.getShortName().getDescriptions().entrySet()) {
                target.getShortName().addDescription(desc.getKey(), desc.getValue());
            }
        }

        if (source.getAddressDescription() != null) {
            for (Map.Entry<Long, String> desc : source.getAddressDescription().getDescriptions().entrySet()) {
                target.getAddressDescription().addDescription(desc.getKey(), desc.getValue());
            }
        }

        if (source.getLicences() != null && !source.getLicences().isEmpty()) {
            Map<String, Licence> sourceLicenseMap = source.getLicences().stream().collect(Collectors.toMap(Licence::getCode, l -> l));
            Map<String, Licence> targetLicenseMap = target.getLicences().stream().collect(Collectors.toMap(Licence::getCode, l -> l));

            for (Map.Entry<String, Licence> sEntry : sourceLicenseMap.entrySet()) {
                LicenceType licenceType = findLicenseTypeByCode(sEntry.getValue().getLicenseType().getCode());
                if (licenceType == null) {
                    importResult.getNonExistentLicenseTypes().add(sEntry.getValue().getLicenseType().getCode());
                    continue;
                }

                if (targetLicenseMap.containsKey(sEntry.getKey())) {
                    mergeLicense(licenceType, sEntry.getValue(), targetLicenseMap.get(sEntry.getKey()));
                } else {
                    Licence newLicense = sEntry.getValue();
                    newLicense.setLicenseType(licenceType);
                    newLicense.setFi(target);
                    saveFiLicence(newLicense);
                }
            }
        }

        if (source.getPeerGroup() != null && !source.getPeerGroup().isEmpty()) {
            List<PeerGroup> groups = new ArrayList<>();
            source.getPeerGroup().stream().forEach(group -> {
                PeerGroup pg = peerGroupLocal.getPeerGroupByCode(group.getCode());
                if (pg != null) {
                    groups.add(pg);
                } else {
                    importResult.getNonExistentGroups().add(group.getCode());
                }
            });
            target.setPeerGroup(groups);
        }
    }

    private void mergeLicense(LicenceType licenceType, Licence source, Licence target) throws FinATypeException {
        target.setLicenseType(licenceType);

        if (source.getCreationDate() != null) {
            target.setCreationDate(source.getCreationDate());
        }

        if (source.getDateOfChange() != null) {
            target.setDateOfChange(source.getDateOfChange());
        }

        if (source.getIsDefault() != null) {
            target.setIsDefault(source.getIsDefault());
        }

        if (source.getLicenceStatus() != null) {
            target.setLicenceStatus(source.getLicenceStatus());
        }

        if (source.getReasons() != null) {
            for (Map.Entry<Long, String> desc : source.getReasons().getDescriptions().entrySet()) {
                target.getReasons().addDescription(desc.getKey(), desc.getValue());
            }
        }

        saveFiLicence(target);
    }

    private void importFiLicenses(Collection<Licence> licences) throws FinATypeException {
        if (licences == null) return;
        for (Licence licence : licences) {
            saveFiLicence(licence);
        }
    }

    private FiType findFiTypeByCode(String code) {
        List<FiType> typeList = em.createQuery("SELECT t FROM IN_BANK_TYPES t WHERE trim(t.code)=:code", FiType.class).setParameter("code", code.trim()).getResultList();
        if (!typeList.isEmpty()) {
            return typeList.get(0);
        }
        return null;
    }

    private LicenceType findLicenseTypeByCode(String code) {
        List<LicenceType> typeList = em.createQuery("SELECT t FROM IN_LICENCE_TYPES t WHERE trim(t.code)=:code", LicenceType.class).setParameter("code", code.trim()).getResultList();
        if (!typeList.isEmpty()) {
            return typeList.get(0);
        }
        return null;
    }

    private void checkImportedFisEmailUnique(net.fina.server.fi.xml.Fi fiXmlModel, FiImportResult fiImportResult, List<String> notUniqueEmauls) throws FinATypeException {
        if (fiXmlModel.getEmail() != null && !fiXmlModel.getEmail().trim().isEmpty()) {
            if (notUniqueEmauls.contains(fiXmlModel.getEmail().trim()) || !isOverwritedFiEmailUnique(fiXmlModel)) {
                fiImportResult.getNotImportedFis().add(fiXmlModel.getCode());
                throw new FinATypeException("FI [ " + fiXmlModel.getCode() + " ] Email [" + fiXmlModel.getEmail() + " ] not unique");
            }
        }
    }

    private boolean isOverwritedFiEmailUnique(net.fina.server.fi.xml.Fi fiModel) {
        return em.createQuery("select b.id from IN_BANKS b where b.code<>:bankCode and b.email=:email", Long.class).setParameter("bankCode", fiModel.getCode().trim()).setParameter("email", fiModel.getEmail().trim()).getResultList().isEmpty();
    }

    private List<String> getNonUniqueEmails(Fis fis, Collection<Long> fiIds) {
        List<String> result = new ArrayList<>();
        Set<String> emails = new HashSet<>();
        try {
            for (net.fina.server.fi.xml.Fi xmlFi : fis.getFi()) {
                Fi fi = findFiByCode(xmlFi.getCode().trim());
                if (!fiIds.contains(fi.getId()) && xmlFi.getEmail() != null && !xmlFi.getEmail().trim().isEmpty()) {
                    if (!emails.contains(xmlFi.getEmail().trim())) {
                        emails.add(xmlFi.getEmail().trim());
                    } else {
                        result.add(xmlFi.getEmail().trim());
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        return result;
    }

    private FiPerson validateAndSetPerson(FiPerson fiPerson, FiBranch fiBranch, FiPersonConnectionType connectionType) throws FinATypeException {
        if (fiPerson != null) {
            Person person = fiPerson.getPerson();
            if (person.getIdentificationNumber() == null || person.getIdentificationNumber().trim().isEmpty()) {
                throw new FinATypeException(connectionType.name() + " Person Identification Code is required, Branch code:  " + fiBranch.getCode());
            }
            if (person.getCitizenship() == null) {
                throw new FinATypeException(connectionType.name() + " Person Citizenship is required, Branch code: " + fiBranch.getCode());
            }
            person = personLocal.findByIdentificationCodeAndRegion(person.getIdentificationNumber(), person.getCitizenship().getId());
            if (person == null) {
                throw new FinATypeException("Person with identification number [" + fiPerson.getPerson().getIdentificationNumber() + "] and region not found, Branch code: " + fiBranch.getCode());
            }
            fiPerson = personLocal.createOrUpdateFiPersonRelation(fiPerson.getFi().getId(), person.getId(), connectionType);
            return fiPerson;
        }
        return null;
    }

    private void updateFiImportProgress(int currentIndex, int totalFis, WsFiEventType eventType, String currUserLogin, FiImportResult fiImportResult) {
        long percentage = Math.round((double) (currentIndex + 1) / totalFis * 100);
        boolean finished = percentage == 100L && currentIndex + 1 == totalFis;
        if (finished) {
            for (FinATypeException exception : fiImportResult.getExceptions()) {
                if (exception.getMessage() != null) {
                    fiImportResult.getExceptionMessages().add(exception.getMessage());
                }
            }
            fiImportResult.setExceptions(null);
            fiImportEvent.fire(new WsFiEvent(new FiImportWrapper(fiImportResult, percentage), eventType, currUserLogin));
        } else {
            fiImportEvent.fire(new WsFiEvent(new FiImportWrapper(null, percentage), eventType, currUserLogin));
        }
    }

    private void validateFiManagementDuplication(long fiId, long managementId, Person person) throws FinATypeException {
        List<Long> existingFiManagements = em.createQuery("select bm.id from IN_BANK_MANAGEMENT bm where bm.fiId=:fiId and bm.management.id=:managementId and bm.fiPerson.person.id=:personId", Long.class)
                .setParameter("fiId", fiId)
                .setParameter("managementId", managementId)
                .setParameter("personId", person.getId())
                .getResultList();

        if (!existingFiManagements.isEmpty()) {
            throw new FinATypeException("Management with person [" + person.getIdentificationNumber() + "] already exists on this type");
        }

    }
}
