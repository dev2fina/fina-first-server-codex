package net.fina.server.dashboard.dynamic.impl;

import jakarta.annotation.Resource;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import net.fina.common.client.exception.FinATypeException;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.dashboard.SqlKeywords;
import net.fina.server.dashboard.dynamic.DashboardQueryPlaceHolders;
import net.fina.server.dashboard.dynamic.api.DashletQueryExecutor;
import net.fina.server.util.SqlInjectionDetector;
import org.apache.commons.lang.text.StrSubstitutor;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
@Local(DashletQueryExecutor.class)
@SecurityDomain("FinASecurityDomain")
@TransactionManagement(TransactionManagementType.BEAN)
public class DashletQueryExecutorImpl implements DashletQueryExecutor {
    private final Logger log = Logger.getLogger(getClass().getName());
    @Resource(mappedName = "java:jboss/datasources/FinaDashboardDS")
    private DataSource dataSource;

    @Inject
    private EntityManager em;

    @Override
    public List<String> parseQueryColumns(String query) throws FinATypeException {
        try {
            query = replaceParameters(query, new HashMap<>());

            List<String> columnNames = new ArrayList<>();
            try (Connection conn = dataSource.getConnection()) {
                PreparedStatement selectStmt = conn.prepareStatement(query);
                ResultSetMetaData metaData = selectStmt.getMetaData();

                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    columnNames.add(metaData.getColumnLabel(i));
                }

                return columnNames;
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            log.error(query);
            throw new FinATypeException("Invalid Query provided in dashlet");
        }
    }

    @Override
    public List<Map<String, Object>> loadDashletData(String query, Map<String, Object> queryParams, int limit) throws FinATypeException {
        List<Map<String, Object>> data = new ArrayList<>();
        query = replaceParameters(query, queryParams);
        List<String> columns = parseQueryColumns(query);

        StringBuilder sb = new StringBuilder(query);

        if (isOrderByNeeded(query, limit)) {
            String orderBy = SqlKeywords.ORDER_BY.value();
            String desc = SqlKeywords.DESC.name();
            String orderByClause = String.format(" %s %s %s", orderBy, columns.get(0), desc);
            sb.append(orderByClause);
        }

        SqlInjectionDetector.validateReadOnlyQuery(sb.toString());

        Query q = em.createNativeQuery(sb.toString());

        if (limit > 0) {
            q.setFirstResult(0);
            q.setMaxResults(limit);
        }

        List<Object[]> result = q.getResultList();

        for (Object[] row : result) {
            Map<String, Object> dataMap = new HashMap<>();
            for (int c = 0; c < columns.size(); c++) {
                dataMap.put(columns.get(c), row[c]);
            }
            data.add(dataMap);
        }


        return data;
    }


    private String replaceParameters(String query, Map<String, Object> params) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        params.put(DashboardQueryPlaceHolders.LANG_ID, langId);

        return StrSubstitutor.replace(query, params);
    }

    private boolean isOrderByNeeded(String query, int limit) {
        query = query.toUpperCase();
        return query.contains(SqlKeywords.DISTINCT.name()) && !query.contains(SqlKeywords.ORDER_BY.name()) && limit > 0;
    }
}
