package net.fina.server.reg.impl;

import net.fina.server.reg.api.RegReportDataLocal;
import net.fina.server.util.RegDS;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Stateless
@Local(RegReportDataLocal.class)
@TransactionManagement(TransactionManagementType.BEAN)
public class RegReportDataSession implements RegReportDataLocal {

    @Inject
    @RegDS
    private EntityManager regEm;

    @Override
    public double cregvaluevctsumif(String tableName, String columnName, String sumColumnName, String criterion, char operation, String criterionValue, List<Long> scheduleIds) {
        StringBuilder qb = new StringBuilder();
        qb.append("select sum(").append(sumColumnName).append(") from ")
                .append(tableName)
                .append(" where schedule_id in")
                .append(scheduleIds.toString().replace("[", "(").replace("]", ")"))
                .append(" and ")
                .append(columnName)
                .append(operation)
                .append("?");

        Object result = regEm.createNativeQuery(qb.toString())
                .setParameter(1, criterionValue)
                .getSingleResult();

        return result != null ? ((Number) result).doubleValue() : 0.0;
    }

    @Override
    public double cregvaluevctsumifs(String tableName, String sumColumnName, Map<String, String> criterions, List<Long> scheduleIDs) {
        StringBuilder qb = new StringBuilder();

        qb.append("select sum(").append(sumColumnName).append(") from ")
                .append(tableName)
                .append(" where schedule_id in")
                .append(scheduleIDs.toString().replace("[", "(").replace("]", ")"))
                .append(" and 1=1 ");

        List<String> values = new ArrayList<>();
        for (Map.Entry<String, String> entry : criterions.entrySet()) {
            qb.append(" and ").append(entry.getKey())
                    .append("=?");
            values.add(entry.getValue());
        }

        Query query = regEm.createNativeQuery(qb.toString());
        for (int i = 0; i < values.size(); i++) {
            query.setParameter(i+1, values.get(i));
        }

        Object result = query.getSingleResult();

        return result != null ? ((Number) result).doubleValue() : 0.0;
    }
}
