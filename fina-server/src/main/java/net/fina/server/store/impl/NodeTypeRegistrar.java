package net.fina.server.store.impl;


import jakarta.enterprise.context.ApplicationScoped;
import net.fina.common.shared.jcr.JcrCustomConstants;

import javax.jcr.NamespaceRegistry;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeTypeManager;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static net.fina.server.util.JcrUtil.registerCustomMixin;
import static net.fina.server.util.JcrUtil.registerFinaCustomNodeType;

/**
 * Author: Oto Iantbelidze
 * Created: 23.10.25
 */
@ApplicationScoped
public class NodeTypeRegistrar {

    private final Set<FinaRepository> registered = ConcurrentHashMap.newKeySet();

    public synchronized void ensureRegistered(Session session, FinaRepository repo) throws Exception {
        if (registered.contains(repo)) return;

        NodeTypeManager ntm = session.getWorkspace().getNodeTypeManager();
        NamespaceRegistry ns = session.getWorkspace().getNamespaceRegistry();

        boolean modified = false;
        if (!existsPrefix(ns, JcrCustomConstants.NAMESPACE_PREFIX)) {
            ns.registerNamespace(JcrCustomConstants.NAMESPACE_PREFIX, JcrCustomConstants.NAMESPACE_URI);
            modified = true;
        }

        if (!ntm.hasNodeType(JcrCustomConstants.FINA_NT_FOLDER_MIXIN)) {
            registerCustomTypes(ntm,ns);
            modified = true;
        }

        if (modified) session.save();

        registered.add(repo);
    }

    private boolean existsPrefix(NamespaceRegistry ns, String prefix) throws Exception {
        for (String p : ns.getPrefixes()) {
            if (p.equalsIgnoreCase(prefix)) return true;
        }
        return false;
    }

    private void registerCustomTypes(NodeTypeManager nodeTypeManager, NamespaceRegistry ns) throws Exception {

        Optional<String> finaPrefixOptional = Arrays.stream(ns.getPrefixes()).filter(name -> name.equalsIgnoreCase(JcrCustomConstants.NAMESPACE_PREFIX)).findFirst();
        if (!finaPrefixOptional.isPresent()) {
            ns.registerNamespace(JcrCustomConstants.NAMESPACE_PREFIX, JcrCustomConstants.NAMESPACE_URI);
        }

        if (!nodeTypeManager.hasNodeType(JcrCustomConstants.FINA_NT_FOLDER_MIXIN)) {
            registerFinaCustomNodeType(nodeTypeManager);
            registerCustomMixin(nodeTypeManager);
        }


    }
}