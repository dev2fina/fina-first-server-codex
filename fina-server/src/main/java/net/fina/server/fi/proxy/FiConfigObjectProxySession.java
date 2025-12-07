package net.fina.server.fi.proxy;

import net.fina.server.fi.model.FiBranchMetaModel;
import net.fina.server.fi.model.FiManagementMetaModel;
import net.fina.server.fi.util.FiConfigurationObjectType;
import net.fina.server.util.UIConfigurationAttribute;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.ejb.Stateless;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class FiConfigObjectProxySession {

    public Map<String, String> getFiObjectConfigurationFieldsByType(FiConfigurationObjectType objectType) {
        Map<String, String> result = new TreeMap<>();
        switch (objectType) {
            case BRANCH:
                result = getObjectFieldConfiguration(FiBranchMetaModel.class);
                break;
            case MANAGEMENT:
                result = getObjectFieldConfiguration(FiManagementMetaModel.class);
            default:
                break;
        }

        return result;
    }

    private Map<String, String> getObjectFieldConfiguration(Class clazz) {
        Map<String, String> result = new TreeMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(UIConfigurationAttribute.class) != null) {
                result.put(field.getName(), field.getType().getSimpleName());
            }
        }

        return result;
    }

}
