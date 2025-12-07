package net.fina.server.mdt.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import jakarta.ejb.*;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.server.mdt.entity.MDTNode;
import org.jboss.logging.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Singleton
@Startup
@DependsOn({"SysStringCacheManager"})
@Lock(LockType.READ)
public class MDTCacheManager {
    private final Logger log = Logger.getLogger(getClass());

    @Resource(lookup = "java:jboss/infinispan/cache/fina-mdt-cache-container/fina-mdt-cache")
    private org.infinispan.Cache<Long, MDTNode> nodesCache;
    @Resource(lookup = "java:jboss/infinispan/cache/fina-mdt-cache-container/fina-mdt-code-cache")
    private org.infinispan.Cache<String, MDTNode> nodeCodeCache;


    private boolean init;

    @Inject
    private EntityManager em;

    @PostConstruct
    public void autoStart() {
        initCache();
    }

    public void initCache() {

        log.info("Constructing object of " + this.getClass().getName());

        if (this.nodesCache.isEmpty()) {

            // Load all MDT nodes
            List<MDTNode> mdtNodes = em.createNamedQuery("MDT.findAll", MDTNode.class).getResultList();

            log.info("load Mdt Nodes. Size: " + mdtNodes.size());

            for (MDTNode node : mdtNodes) {
                loadDependencies(node);
                //Hibernate Lazy Initialization Bug
                node.getComparisons().size();
                nodesCache.put(node.getId(), node);
                nodeCodeCache.put(node.getCode(), node);
            }

        }
        init = true;

        log.info("Mdt Nodes Cache size: " + this.nodesCache.size());
        log.info(getClass().getName() + " - cache loaded.");
    }

    @PreDestroy
    public void clearCache() {
        nodesCache.clear();
        log.info(getClass().getName() + " Cache Cleared.");
    }

    public Collection<MDTNode> getMdtNodes() {
        return nodesCache.values();
    }

    public Map<Long, MDTNode> getMdtNodesById() {
        return nodesCache;
    }

    public Map<String, MDTNode> getMdtNodesByCode() {
        return nodeCodeCache;
    }

    public Object removeCache(Object object) {
        log.info("MDT Cache remove - " + object);
        MDTNode removedNode = nodesCache.remove(object);
        nodeCodeCache.remove(removedNode.getCode());
        return removedNode;
    }

    public void addCache(MDTNode mdtNode) {
        log.info("MDT Cache Update - " + mdtNode);
        //Hibernate Lazy Initialization Bug
        mdtNode.getComparisons().size();
        nodesCache.put(mdtNode.getId(), mdtNode);
        nodeCodeCache.put(mdtNode.getCode(), mdtNode);
    }


    private void loadDependencies(MDTNode node) {
        if (node.getType() == MDTNodeTypes.VARIABLE) {
            List<MDTNode> dependencies = node.getDependentNodes();
            for (MDTNode n : dependencies) {
                if (n.getId() != node.getId())
                    loadDependencies(n);
            }
        }
    }

    public MDTNode getNode(long nodeId) {
        return nodesCache.get(nodeId);
    }

    public MDTNode getNode(String nodeCode) {
        return nodeCodeCache.get(nodeCode);
    }

    public boolean isInit() {
        return init;
    }
}
