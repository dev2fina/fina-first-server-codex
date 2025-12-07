package net.fina.server.fi.impl;

import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.property.PropertyKeys;
import net.fina.server.fi.api.RegionLocal;
import net.fina.server.fi.entity.Region;
import net.fina.server.i18n.api.SysStringLocal;
import net.fina.server.i18n.cache.SysStringCacheManager;
import net.fina.server.i18n.entity.SysStringId;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.mdt.api.MDTDataNodeCatalogSourceType;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.mdt.proxy.MDTDataNodeCatalogProxySession;
import net.fina.server.security.api.PropertyLocal;

import java.util.*;

@Stateless
@Local(RegionLocal.class)
@Interceptors(RecordingAuditor.class)
public class RegionSession implements RegionLocal {

    @Inject
    private EntityManager em;
    @EJB
    private SysStringLocal sysStringLocal;
    @EJB
    private PropertyLocal propertyLocal;
    @EJB
    private SysStringCacheManager cacheManager;
    @Inject
    private MDTDataNodeCatalogProxySession mdtDataNodeCatalogProxySession;
    @Inject
    private RegionCacheManager regionCacheManager;

    @Override
    public List<Region> loadRegions() {
        return em.createQuery("select r from IN_COUNTRY_DATA r where (r.deleted = false or r.deleted is null) order by r.parentId,  r.sequence, r.id DESC", Region.class)
                .getResultList();
    }

    @Override
    public List<Region> loadRegionsFirstLevel() {
        return em.createQuery("select r from IN_COUNTRY_DATA r where (r.deleted = false or r.deleted is null) and r.parentId = 0 order by r.id asc ", Region.class)
                .getResultList();
    }

    @Override
    public Map<String, Region> loadRegionsCodeMap() {
        Map<String, Region> regionsCodeMap = new HashMap<>();
        for (Region region : loadRegions()) {
            regionsCodeMap.put(region.getCode().trim(), region);
        }
        return regionsCodeMap;
    }

    @Override
    public Map<String, Region> loadRegionsCode1Map() {
        Map<String, Region> regionsCodeMap = new HashMap<>();
        for (Region region : loadRegions()) {
            String code;
            if (region.getCode1() == null) {
                code = region.getCode();
            } else {
                code = region.getCode1();
            }
            regionsCodeMap.put(code.trim(), region);
        }
        return regionsCodeMap;
    }

    @Override
    public Map<Long, Region> loadRegionsIdMap() {
        Map<Long, Region> regionsCodeMap = new HashMap<>();
        for (Region region : loadRegions()) {
            regionsCodeMap.put(region.getId(), region);
        }
        return regionsCodeMap;
    }

    @Override
    public Region save(Region region) throws FinATypeException {
        Region reg = findByCode(region.getCode());
        if (reg != null && reg.isDeleted()) {
            throw new FinATypeException(FinATypeException.Type.ENTITY_PROGRAMMATICALLY_DELETED);
        }
        checkCodeUnique(region.getCode(), region.getId());

        if (region.getId() == 0) {
            MDTNode mdtNode = mdtDataNodeCatalogProxySession.checkAndCreateDataElementNodeBySourceType(region.getCode(), MDTDataNodeCatalogSourceType.REGIONAL_STRUCTURE);
            region.setMdtNode(mdtNode);

            em.persist(region);
        } else {
            Region existing = em.find(Region.class, region.getId());
            region.setVersion(existing.getVersion());

            if (existing.getCode() != null && !existing.getCode().trim().isEmpty() && !existing.getCode().equals(region.getCode())) {
                checkCodeUnique(region.getCode(), region.getId());
            }

            if (existing.getMdtNode() != null) {
                region.setMdtNode(existing.getMdtNode());
            } else {
                MDTNode mdtNode = mdtDataNodeCatalogProxySession.checkAndCreateDataElementNodeBySourceType(region.getCode(), MDTDataNodeCatalogSourceType.REGIONAL_STRUCTURE);
                region.setMdtNode(mdtNode);
            }

            region = em.merge(region);
        }

        regionCacheManager.addToCache(region);
        return region;
    }

    @Override
    public void delete(Long id) throws FinATypeException {
        //Check has region children
        List<Long> childrenIds = em.createQuery("select c.id from IN_COUNTRY_DATA c where c.parentId=:id and  c.deleted = false", Long.class)
                .setParameter("id", id)
                .getResultList();
        if (!childrenIds.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }

        //Check bank regions dependency
        Query banksDependencyQuery = em.createQuery("select c.id from IN_BANKS c where c.regionId =:id")
                .setParameter("id", id);
        if (!banksDependencyQuery.getResultList().isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.USED_BY_BANK);
        }

        //Check branch regions
        Query branchDependencyQuery = em.createQuery("select c.id from IN_BANK_BRANCHES c where c.region.id =:id")
                .setParameter("id", id);
        if (!branchDependencyQuery.getResultList().isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.USED_BY_BANK_BRANCH);
        }

        //Remove Region
        Region region = em.find(Region.class, id);
        if (region != null) {
            region.setDeleted(true);
            if (region.getMdtNode() != null) {
                mdtDataNodeCatalogProxySession.deleteDataElementMdtNode(region.getMdtNode().getCode(), MDTDataNodeCatalogSourceType.REGIONAL_STRUCTURE);
            }

            em.merge(region);
        }
    }

    private void checkCodeUnique(String code, long id) throws FinATypeException {
        List<Long> regionIds = em.createQuery("select r.id from IN_COUNTRY_DATA r where trim(r.code)=:code and r.id <>:id", Long.class)
                .setParameter("id", id)
                .setParameter("code", code)
                .getResultList();
        if (!regionIds.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE);
        }
    }

    @Override
    public void setProperties(Map<Integer, String> map, Long curLangId) {
        // Set Max Level
        int maxLevel = Integer.parseInt(map.get(0));
        propertyLocal.setSystemProperty(PropertyKeys.REGION_PROPERTIES_MAXLEVEL, map.get(0));

        // new Strings Buffer
        StringBuffer buf = new StringBuffer();

        ArrayList<Long> list = new ArrayList<Long>();

        // Get Old String
        String oldString = propertyLocal.getSystemProperty(PropertyKeys.REGION_PROPERTIES_LEVELNAMES);
        if (oldString == null || oldString.trim().isEmpty() || oldString.trim().equals("null")) {
            oldString = "{1=[]}";
        }
        StringTokenizer stringTokenizer = new StringTokenizer(oldString, "#");
        while (stringTokenizer.hasMoreTokens()) {
            String tokenName = (String) stringTokenizer.nextElement();
            tokenName = tokenName.replace("{", "");
            tokenName = tokenName.replace("}", "");
            String[] s = tokenName.split("=");

            s[1] = s[1].replace("[", "");
            s[1] = s[1].replace("]", "");
            String[] strIds;
            if (s[1].trim().isEmpty()) {
                strIds = new String[0];
            } else {
                strIds = s[1].split(",");
            }
            Long langId = Long.parseLong(s[0]);

            if (curLangId.longValue() == langId.longValue()) {
                for (String strId : strIds) {
                    if (!strId.isEmpty())
                        sysStringLocal.delete(Long.parseLong(strId.trim()));
                }
            } else {
                List<String> newStrIds = new ArrayList<>();
                if (maxLevel > strIds.length) { // if property added
                    Collections.addAll(newStrIds, strIds);
                    newStrIds.add(String.valueOf(sysStringLocal.add(langId, "NONAME")));
                    buf.append("{").append(langId).append("=").append(newStrIds).append("}");
                    buf.append("#");
                } else if (maxLevel < strIds.length) { // if property deleted
                    newStrIds.addAll(Arrays.asList(strIds).subList(0, strIds.length - 1));
                    sysStringLocal.delete(Long.parseLong(strIds[strIds.length - 1].trim()));
                    buf.append("{").append(langId).append("=").append(newStrIds).append("}");
                    buf.append("#");
                } else {
                    //if property edited
                    buf.append("{").append(tokenName).append("}");
                    buf.append("#");
                }
            }
        }

        for (int i = 1; i < map.size(); i++) {
            oldString = map.get(i);
            if (oldString != null) {
                long nameStrId = sysStringLocal.add(curLangId, oldString);
                list.add(nameStrId);
            }
        }

        buf.append("{").append(curLangId).append("=").append(list.toString()).append("}");

        propertyLocal.setSystemProperty(PropertyKeys.REGION_PROPERTIES_LEVELNAMES, buf.toString());
    }

    @Override
    public Map<Integer, String> getProperties(Long curLangId) {
        boolean addedValues = false;
        Map<Integer, String> map = new HashMap<>();
        map.put(0, propertyLocal.getSystemProperty(PropertyKeys.REGION_PROPERTIES_MAXLEVEL));
        int maxLevel = Integer.valueOf(propertyLocal.getSystemProperty(PropertyKeys.REGION_PROPERTIES_MAXLEVEL));

        if (maxLevel > 0) {
            String s = propertyLocal.getSystemProperty(PropertyKeys.REGION_PROPERTIES_LEVELNAMES);
            StringTokenizer stringTokenizer = new StringTokenizer(s, "#");
            int length = 0;
            int index = 1;
            while (stringTokenizer.hasMoreTokens()) {
                String token = stringTokenizer.nextToken();
                token = token.replace("{", "");
                token = token.replace("}", "");

                String[] ids = token.split("=");

                ids[1] = ids[1].replace("[", "");
                ids[1] = ids[1].replace("]", "");
                String[] strIds = ids[1].split(",");

                length = strIds.length;

                if (Long.parseLong(ids[0]) == curLangId) {
                    for (String strId : strIds) {
                        SysStringId sysStringId = new SysStringId();
                        sysStringId.setId(Long.parseLong(strId.trim()));
                        sysStringId.setLangId(Long.parseLong(ids[0]));

                        String value = cacheManager.getDescription(sysStringId) != null ? (String) cacheManager.getDescription(sysStringId) : "NONAME";

                        map.put(index, value);
                        index++;
                    }
                    addedValues = true;
                    break;
                }
            }
            if (!addedValues) {
                for (int i = 0; i < length; i++) {
                    map.put(index, "NONAME");
                    index++;
                }
            }
        }
        return map;
    }

    @Override
    @Deprecated
    public String getPath(Long id, Long langId) {
        StringBuffer buffer = new StringBuffer();
        Query query = em.createQuery("select cd from IN_COUNTRY_DATA cd where cd.id=:id");
        query.setParameter("id", id);
        List<Region> list = query.getResultList();
        while (!list.isEmpty()) {
            buffer.insert(0, list.get(0).getCode() + ":" + list.get(0).getDescription().getDescription(langId) + " | ");
            query.setParameter("id", list.get(0).getParentId());
            list = query.getResultList();
        }
        if (buffer.length() > 1) {
            return buffer.substring(0, buffer.length() - 2);
        } else {
            return " ";
        }
    }

    @Deprecated
    public int allocateString() {
        Query con = em.createQuery("select max(id) from SYS_STRINGS");
        int id = con.getFirstResult() + 1;
        Query insertQuery = em.createNativeQuery("insert into SYS_STRINGS (id,langID,value) values(?,?,?)");
        insertQuery.setParameter(1, id);
        insertQuery.setParameter(2, 1);
        insertQuery.setParameter(3, "NONAME");
        insertQuery.executeUpdate();
        return id;
    }

    @Override
    public List<Region> dragAndDrop(List<Region> regions) {
        for (Region region : regions) {
            Region r = em.find(Region.class, region.getId());
            r.setParentId(region.getParentId());
        }
        return regions;
    }

    @Override
    public Region getRegionWithId(long regionId) {
        return em.find(Region.class, regionId);
    }

    @Deprecated
    public Map<Integer, String> getPropertiesOld(Long langId) {

        Map<Integer, String> map = null;

        try {
            String maxLevel = propertyLocal.getSystemProperty("fina2.regionstructuretree.maxlevel");
            map = new HashMap<Integer, String>();
            map.put(0, maxLevel);
            if (maxLevel != null) {
                String levelName = propertyLocal.getSystemProperty("fina2.regionstructuretree.levelname");
                if (levelName != null)
                    levelName = levelName.trim();

                String selectedStrings = null;
                if (levelName != null) {
                    selectedStrings = createSelectedStrings(levelName, langId.intValue());
                    if (selectedStrings != null) {
                        map = getPropertiesLevelNames(langId.intValue(), selectedStrings, map);
                    } else {
                        ArrayList<Integer> list = new ArrayList<Integer>();
                        StringTokenizer tokenNames = new StringTokenizer(levelName, "#");
                        while (tokenNames.hasMoreElements()) {
                            String tokenName = (String) tokenNames.nextElement();
                            StringTokenizer langToken = new StringTokenizer(tokenName, "=");
                            String s = (String) langToken.nextElement();
                            int lang = Integer.parseInt(s.charAt(s.length() - 1) + "");
                            list.add(lang);
                        }
                        if (list.size() > 0) {
                            Collections.sort(list);
                            int newLangId = list.get(0);
                            selectedStrings = createSelectedStrings(levelName, newLangId);
                            map = getPropertiesLevelNames(newLangId, selectedStrings, map);
                        }
                    }
                }
            }
        } catch (Exception ignore) {
        }
        return map;
    }

    @Deprecated
    private String createSelectedStrings(String src, int langId) {
        StringTokenizer tokenNames = new StringTokenizer(src, "#");
        while (tokenNames.hasMoreElements()) {
            String tokenName = (String) tokenNames.nextElement();
            StringTokenizer langToken = new StringTokenizer(tokenName, "=");
            String s = (String) langToken.nextElement();
            int lang = Integer.parseInt(s.substring(1));
            if (langId == lang) {
                StringBuilder getNameStrs = new StringBuilder(langToken.nextElement().toString());
                getNameStrs.deleteCharAt(0);
                getNameStrs.deleteCharAt(getNameStrs.length() - 1);
                getNameStrs.deleteCharAt(getNameStrs.length() - 1);
                return getNameStrs.toString();
            }
        }
        return null;
    }

    @Deprecated
    private Map<Integer, String> getPropertiesLevelNames(int langId, String selectedStrings, Map<Integer, String> map) {
        try {
            List<String> languages;
            Query languageQuery = em.createNamedQuery("SELECT value FROM SYS_STRINGS WHERE ID in(" + selectedStrings + ") and langId=?");
            languageQuery.setParameter(1, langId);
            languages = languageQuery.getResultList();
            int key = 1;
            for (String name : languages) {
                if (name != null) {
                    // es gasaworebelia
                    // name = encode(name, language.getXmlEncoding());
                } else {
                    name = "NONAME";
                }
                map.put(key++, name);
            }
        } catch (Exception ignore) {
        }
        return map;
    }

    @Override
    public List<Long> loadRegionChildrenIdsByCode(String regionCode) {
        List<Long> result = new ArrayList<>();
        if (regionCode != null) {
            long regionId = em.createQuery("select cd.id from IN_COUNTRY_DATA cd where trim(cd.code)=:regionCode and (cd.deleted = false or cd.deleted is null)", Long.class)
                    .setParameter("regionCode", regionCode.trim())
                    .getSingleResult();
            getChildren(result, regionId);
        }
        return result;
    }

    @Override
    public Region findByCode(String regionCode) throws FinATypeException {
        List<Region> regions = em.createQuery("select cd from IN_COUNTRY_DATA cd where trim(cd.code)=:code", Region.class)
                .setParameter("code", regionCode)
                .getResultList();
        if (regions.size() == 1) {
            return regions.iterator().next();
        } else {
            return null;
        }
    }


    @Override
    public Region findByCode1(String regionCode) throws FinATypeException {
        List<Region> regions = em.createQuery("select cd from IN_COUNTRY_DATA cd where trim(cd.code1)=:code and (cd.deleted = false or cd.deleted is null)", Region.class)
                .setParameter("code", regionCode)
                .getResultList();
        if (regions.size() == 1) {
            return regions.iterator().next();
        } else {
            throw new FinATypeException("Region code doesn't exist. Region code: [ " + regionCode + " ]");
        }
    }

    private void getChildren(List<Long> result, long regionId) {
        result.add(regionId);
        List<Long> children = em.createQuery("select cd.id from IN_COUNTRY_DATA cd where cd.parentId=:regionId and (cd.deleted = false or cd.deleted is null)", Long.class)
                .setParameter("regionId", regionId)
                .getResultList();
        for (long id : children) {
            getChildren(result, id);
        }
    }

    @Override
    public List<Region> getRegionByIds(List<Long> ids) {
        return em.createQuery("SELECT cd FROM IN_COUNTRY_DATA cd WHERE cd.id IN (:regionIds) and (cd.deleted = false or cd.deleted is null)", Region.class)
                .setParameter("regionIds", ids)
                .getResultList();
    }

    @Override
    public Region restoreDeletedRegion(long regionId) throws FinATypeException {
        Region region = em.find(Region.class, regionId);
        if (region != null) {
            region.setDeleted(false);

            if (region.getMdtNode() != null) {
                mdtDataNodeCatalogProxySession.restoreDataElementMdtNode(region.getMdtNode().getCode(), MDTDataNodeCatalogSourceType.REGIONAL_STRUCTURE);
            }

            return save(region);
        } else {
            throw new FinATypeException("Region not found");
        }
    }

    @Override
    public Region findDeletedRegionByCode(String regionCode) {
        List<Region> deleted = em.createQuery("select r from IN_COUNTRY_DATA  r  where r.code=:regionCode and r.deleted = true ", Region.class)
                .setParameter("regionCode", regionCode).getResultList();
        return deleted != null && !deleted.isEmpty() ? deleted.get(0) : null;
    }

    @Override
    public List<Region> loadByParentId(long parentId) {
        return em.createQuery("select r from IN_COUNTRY_DATA  r  where r.parentId=:parentId and r.deleted = false ", Region.class)
                .setParameter("parentId", parentId)
                .getResultList();
    }

    @Override
    public List<Long> loadRegionIdsByParent(long regionId) {
        return em.createQuery("select r.id from IN_COUNTRY_DATA r where r.parentId=:parentId", Long.class)
                .setParameter("parentId", regionId)
                .getResultList();
    }

    @Override
    public List<Region> loadRegionTreeByParentId(long parentId) {
        List<Region> regions = new ArrayList<>();
        Region region = em.find(Region.class, parentId);

        if (region != null) {
            regions.add(region);
            loadRegionsRecursively(parentId, regions);
            return regions;
        }
        return regions;

    }

    private void loadRegionsRecursively(long parentId, List<Region> regions) {
        List<Region> subRegions = em.createQuery("select r from IN_COUNTRY_DATA r where r.parentId = :parentId and (r.deleted = false or r.deleted is null )", Region.class)
                .setParameter("parentId", parentId)
                .getResultList();

        for (Region region : subRegions) {
            regions.add(region);
            loadRegionsRecursively(region.getId(), regions);
        }
    }


}
