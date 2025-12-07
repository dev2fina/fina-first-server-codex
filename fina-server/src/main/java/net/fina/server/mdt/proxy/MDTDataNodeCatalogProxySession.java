package net.fina.server.mdt.proxy;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.helper.Description;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.mdt.api.MDTDataNodeCatalogSourceType;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.security.api.PropertyLocal;
import org.jboss.logging.Logger;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import java.util.List;

@Stateless
@Interceptors(RecordingAuditor.class)
public class MDTDataNodeCatalogProxySession {
    private final Logger log = Logger.getLogger(getClass());
    @Inject
    private PropertyLocal propertyLocal;
    @Inject
    private MDTNodeLocal mdtNodeLocal;
    @Inject
    private LanguageLocal languageLocal;

    public MDTNode checkAndCreateDataElementNodeBySourceType(String objectCode, MDTDataNodeCatalogSourceType sourceType) throws FinATypeException {
        if (isEnable(sourceType.getEnablePropertyKey())) {
            String parentFolderNodeCodeKey = sourceType.getParentFolderNodeCodeKey();
            String parentFolderNodeCode = propertyLocal.getSystemProperty(parentFolderNodeCodeKey);

            validateParentNodeCode(parentFolderNodeCode, sourceType.name());

            MDTNode parentNode = mdtNodeLocal.findByCode(parentFolderNodeCode);
            long sequence = mdtNodeLocal.getNodeChildMaxSequence(parentNode.getId()) + 1;
            String code = sourceType.getCodePrefix() + objectCode;

            // create data element
            MDTNode dataElementNode = new MDTNode();
            dataElementNode.setId(0);
            dataElementNode.setParentId(parentNode.getId());
            dataElementNode.setType(MDTNodeTypes.DATA);
            dataElementNode.setEquation(objectCode);
            dataElementNode.setCatalog(true);
            dataElementNode.setSequence(sequence);
            dataElementNode.setCode(code);

            initDescriptions(dataElementNode, code);

            return mdtNodeLocal.save(dataElementNode);
        }
        return null;
    }

    public void deleteDataElementMdtNode(String nodeCode, MDTDataNodeCatalogSourceType sourceType) throws FinATypeException {
        moveNode(nodeCode, sourceType, sourceType.getDeletedParentFolderNodeCodeKey());
    }

    public void restoreDataElementMdtNode(String nodeCode, MDTDataNodeCatalogSourceType sourceType) throws FinATypeException {
        moveNode(nodeCode, sourceType, sourceType.getParentFolderNodeCodeKey());
    }

    public boolean isEntityAsMdtDataNodeEnable(MDTDataNodeCatalogSourceType sourceType) {
        return isEnable(sourceType.getEnablePropertyKey());
    }

    private void moveNode(String nodeCode, MDTDataNodeCatalogSourceType sourceType, String parentFolderNodeCodeKey) throws FinATypeException {
        if (isEnable(sourceType.getEnablePropertyKey())) {
            String parentFolderNodeCode = propertyLocal.getSystemProperty(parentFolderNodeCodeKey);
            validateParentNodeCode(parentFolderNodeCode, sourceType.name());

            MDTNode parentNode = mdtNodeLocal.findByCode(parentFolderNodeCode);
            long sequence = mdtNodeLocal.getNodeChildMaxSequence(parentNode.getId()) + 1;

            MDTNode mdtNode = mdtNodeLocal.findByCode(nodeCode);
            mdtNode.setParentId(parentNode.getId());
            mdtNode.setSequence(sequence);

            mdtNodeLocal.save(mdtNode);
        }
    }

    private void initDescriptions(MDTNode node, String value) {
        List<Long> langIds = languageLocal.getLanguageIds();
        Description description = new Description();
        for (Long langId : langIds) {
            description.setNameStrId(0);
            description.getDescriptions().put(langId, value);
        }
        node.setDescription(description);
    }

    private void validateParentNodeCode(String parentFolderNodeCode, String sourceTypeName) throws FinATypeException {
        if (parentFolderNodeCode == null || parentFolderNodeCode.trim().isEmpty() || mdtNodeLocal.findByCode(parentFolderNodeCode) == null) {
            String errorMessage = (sourceTypeName + " CATALOG Data Element parent Folder Code is not configured or node does not exist");
            log.error(errorMessage);
            throw new FinATypeException(errorMessage);
        }
    }

    private boolean isEnable(String propKey) {
        try {
            String enable = propertyLocal.getSystemProperty(propKey);
            return enable != null && Integer.parseInt(enable.trim()) > 0;
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return false;
    }
}
