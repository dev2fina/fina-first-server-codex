package net.fina.ecm.util;

import net.fina.common.server.util.ConfigurationUtil;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.search.body.QueryBody;
import net.fina.ecm.alfresco.api.search.body.RequestPagination;
import net.fina.ecm.alfresco.api.search.body.RequestQuery;
import net.fina.ecm.alfresco.api.search.model.ResultNodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultSetRepresentation;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AlfrescoConfiguration {
    private Logger log = Logger.getLogger(getClass());
    private Map<String, String> propertyMap;

    private static volatile AlfrescoConfiguration _instance;

    private AlfrescoConfiguration() {
        postLoad();
    }

    //DCL pattern
    public static AlfrescoConfiguration get() {
        if (_instance == null || _instance.propertyMap.isEmpty()) {
            synchronized (AlfrescoConfiguration.class) {
                if (_instance == null) {
                    _instance = new AlfrescoConfiguration();
                } else if (_instance.propertyMap.isEmpty()) {
                    _instance.postLoad();
                }
            }
        }
        return _instance;
    }

    private synchronized void postLoad() {
        try {
            propertyMap = new ConcurrentHashMap<>();
            String endpoint = ConfigurationUtil.get().get("ECM.endpoint");
            String adminUserName = ConfigurationUtil.get().get("ECM.adminUsername");
            String userHashSalt = ConfigurationUtil.get().get("ECM.userHashSalt");
            AlfrescoClient client = new AlfrescoClient.Builder().connectExternal(endpoint, adminUserName, userHashSalt).build();
            String query = "select * from fina:configuration";
            RequestQuery requestQuery = new RequestQuery().query(query).language(RequestQuery.LanguageEnum.CMIS);
            QueryBody queryBody = new QueryBody().query(requestQuery)
                    .include(Arrays.asList("properties", "path"))
                    .paging(new RequestPagination().skipCount(0).maxItems(1000));
            ResultSetRepresentation<ResultNodeRepresentation> searchResult = client.getSearchAPI().search(queryBody);

            searchResult.getObjects().forEach(node -> {
                propertyMap.put(node.getName(), (String) node.getProperties().get("fina:propertyValue"));
            });

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public String getAlfrescoProperty(String key) {
        return propertyMap.get(key);
    }

    public String[] getAlfrescoArrayProperty(String key) {
        try {
            return propertyMap.get(key).split(",");
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return new String[]{};
    }

    public synchronized void sync() {
        postLoad();
    }

    public void addProperty(String key, String value) {
        propertyMap.put(key, value);
    }

}
