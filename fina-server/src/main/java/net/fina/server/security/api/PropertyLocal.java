package net.fina.server.security.api;

import net.fina.common.client.exception.FinATypeException;

import java.sql.DatabaseMetaData;
import java.util.List;
import java.util.Map;

public interface PropertyLocal {

    void setSystemProperty(String key, String value);

    void setSystemProperty(String key, String value, boolean ignoreAuditLog);

    String getSystemProperty(String key);

    String getSystemProperty(String key, String defaultValue);

    Map<String, String> getSystemProperty();

    void setSystemProperty(Map<String, String> propMap);

    DatabaseMetaData getDatabaseMetaData() throws Exception;

    List<String> getAllEntity();

    void saveSystemProperty(String key, String value) throws FinATypeException;

    int getTransactionIsolationLevel();

    List<String[]> getDatabaseProperties() throws Exception;
}
