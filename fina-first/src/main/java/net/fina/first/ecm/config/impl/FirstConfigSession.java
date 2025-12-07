package net.fina.first.ecm.config.impl;

import net.fina.common.server.util.CommonUtil;
import net.fina.common.shared.config.ConfigMetaModel;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.config.api.FirstConfigLocal;
import net.fina.first.ecm.people.api.PeopleLocal;
import net.fina.first.interceptors.FirstRecordingAuditor;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

@Stateless
@Local(FirstConfigLocal.class)
@Interceptors(FirstRecordingAuditor.class)
@SecurityDomain("FinASecurityDomain")
public class FirstConfigSession implements FirstConfigLocal {

    private static final Logger log = Logger.getLogger(FirstConfigSession.class.getName());

    @Inject
    private PeopleLocal peopleProxySession;

    @Inject
    private EcmClientProxySession clientProxySession;

    @Override
    public ConfigMetaModel getConfig() {

        boolean isEcmEnable = CommonUtil.isEcmEnable();

        Map<String, Serializable> properties = new TreeMap<>();
        properties.put(AlfrescoPropConstants.IS_ECM_ENABLE, isEcmEnable);

        if (isEcmEnable) {
            properties.put(AlfrescoPropConstants.CURRENT_USER, peopleProxySession.getCurrentPerson());
            properties.put(AlfrescoPropConstants.MODEL_HIDDEN_FIELD_NAMES_KEY, new TreeSet<>(Arrays.asList(AlfrescoConfiguration.get().getAlfrescoArrayProperty(AlfrescoPropConstants.MODEL_HIDDEN_FIELD_NAMES_KEY))));
            properties.put(AlfrescoPropConstants.MODEL_GRID_CHECK_PREFIXES_KEY, new TreeSet<>(Arrays.asList(AlfrescoConfiguration.get().getAlfrescoArrayProperty(AlfrescoPropConstants.MODEL_GRID_CHECK_PREFIXES_KEY))));
            properties.put(AlfrescoPropConstants.DATA_TYPE_FI_TYPE_KEY, AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.DATA_TYPE_FI_TYPE_KEY));
            properties.put(AlfrescoPropConstants.DATA_TYPE_FI_REGISTRIES_KEY, new TreeSet<>(Arrays.asList(AlfrescoConfiguration.get().getAlfrescoArrayProperty(AlfrescoPropConstants.DATA_TYPE_FI_REGISTRIES_KEY))));
            properties.put(AlfrescoPropConstants.DATA_TYPE_REGIONAL_STRUCTURE_REGION, AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.DATA_TYPE_REGIONAL_STRUCTURE_CITY));
            properties.put(AlfrescoPropConstants.DATA_TYPE_REGIONAL_STRUCTURE_CITY, AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.DATA_TYPE_REGIONAL_STRUCTURE_REGION));
            properties.put(AlfrescoPropConstants.REGIONAL_STRUCTURE_FOLDER_PATH, AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.REGIONAL_STRUCTURE_FOLDER_PATH));
            properties.put(AlfrescoPropConstants.DOCUMENT_REPOSITORY_HIDDEN_WORKFLOW_KEYS_KEY, new TreeSet<>(Arrays.asList(AlfrescoConfiguration.get().getAlfrescoArrayProperty(AlfrescoPropConstants.DOCUMENT_REPOSITORY_HIDDEN_WORKFLOW_KEYS_KEY))));
            properties.put(AlfrescoPropConstants.RENDITION_NOT_REQUIRED_MIME_TYPES_KEY, new TreeSet<>(Arrays.asList(AlfrescoConfiguration.get().getAlfrescoArrayProperty(AlfrescoPropConstants.RENDITION_NOT_REQUIRED_MIME_TYPES_KEY))));
            properties.put(AlfrescoPropConstants.AOS_REMOTE_URL_KEY, AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.AOS_REMOTE_URL_KEY));
            setUserRootNode(properties);
        }

        ConfigMetaModel config = new ConfigMetaModel();
        config.setProperties(properties);

        return config;
    }

    @Override
    public String getProperty(String key) {
        return AlfrescoConfiguration.get().getAlfrescoProperty(key);
    }

    private void setUserRootNode(Map<String, Serializable> properties) {
        try {
            properties.put(AlfrescoPropConstants.CURRENT_USER_ROOT_NODE, clientProxySession.getAlfrescoClient().getNodesAPI().getNodeCall("-my-"));
        } catch (Exception ex) {
            log.error(ex);
        }
    }

}
