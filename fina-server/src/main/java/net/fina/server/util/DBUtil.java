package net.fina.server.util;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import net.fina.security.util.SecurityUtil;
import net.fina.server.fi.entity.Fi_;
import net.fina.server.security.api.PropertyLocal;
import net.fina.server.security.impl.PropertySession;
import org.apache.commons.beanutils.PropertyUtils;
import org.jboss.logging.Logger;

import javax.naming.InitialContext;
import java.io.Serializable;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class DBUtil implements Serializable {
    public static final int IN_CLAUSE_MAX_PARAMS = 999;
    private static Logger log = Logger.getLogger(DBUtil.class.getName());
    private static DBUtil dbUtil;

    private String dbProductName;

    private DBUtil() {
        initDatabaseProductName();
    }

    public static DBUtil get() {
        if (dbUtil == null) {
            dbUtil = new DBUtil();
        }
        return dbUtil;
    }

    public boolean isOracle() {
        return dbProductName.toLowerCase().indexOf("oracle") >= 0;
    }

    public boolean isMsSql() {
        return dbProductName.toLowerCase().indexOf("microsoft sql server") >= 0;
    }

    public boolean isMysql() {
        return dbProductName.toLowerCase().indexOf("mysql") >= 0;
    }

    private void initDatabaseProductName() {
        try {
            InitialContext ic = new InitialContext();
            PropertyLocal current = (PropertyLocal) ic.lookup(SecurityUtil.generateGlobalJndiName(ic, PropertySession.class, PropertyLocal.class));
            DatabaseMetaData databaseMetaData = current.getDatabaseMetaData();
            if (databaseMetaData != null) {
                dbProductName = databaseMetaData.getDatabaseProductName();
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Predicate> buildPredicate(CriteriaBuilder cb, From f, Collection<Long> ids) {
        List<Predicate> predicates = new ArrayList<>();
        List<Long> fiArray = ids.stream().collect(Collectors.toList());
        if (ids.size() <= DBUtil.IN_CLAUSE_MAX_PARAMS) {
            predicates.add(f.get(Fi_.id).in(ids));
        } else {
            int listSize = ids.size();
            List<Long> subList;
            for (int i = 0; i < listSize; i += DBUtil.IN_CLAUSE_MAX_PARAMS) {
                if (listSize > i + DBUtil.IN_CLAUSE_MAX_PARAMS) {
                    subList = fiArray.subList(i, (i + DBUtil.IN_CLAUSE_MAX_PARAMS));
                } else {
                    subList = fiArray.subList(i, listSize);
                }

                predicates.add(f.get(Fi_.id).in(subList));
            }
        }
        return predicates;
    }

    public List<Predicate> buildPredicate(Path<Integer> path, Collection<? extends Number> ids) {
        List<Predicate> predicates = new ArrayList<>();
        List<Number> fiArray = new ArrayList<>(ids);
        if (ids.size() <= DBUtil.IN_CLAUSE_MAX_PARAMS) {
            predicates.add(path.in(ids));
        } else {
            int listSize = ids.size();
            List<Number> subList;
            for (int i = 0; i < listSize; i += DBUtil.IN_CLAUSE_MAX_PARAMS) {
                if (listSize > i + DBUtil.IN_CLAUSE_MAX_PARAMS) {
                    subList = fiArray.subList(i, (i + DBUtil.IN_CLAUSE_MAX_PARAMS));
                } else {
                    subList = fiArray.subList(i, listSize);
                }

                predicates.add(path.in(subList));
            }
        }
        return predicates;
    }

    public String generateConcatenatedInStatementWithIds(String fieldName, List params) {
        return generateConcatenatedInStatement(fieldName, params, null);
    }

    public String generateConcatenatedInStatement(String fieldName, List params, Class type) {
        StringBuilder sb = new StringBuilder();

        if (params == null || params.isEmpty()) {
            log.warn("Received empty parameter list!");
            return "1 = 0";
        }

        List subList;
        int listSize = params.size();

        for (int i = 0; i < listSize; i += DBUtil.IN_CLAUSE_MAX_PARAMS) {
            if (listSize > i + DBUtil.IN_CLAUSE_MAX_PARAMS) {
                subList = params.subList(i, (i + DBUtil.IN_CLAUSE_MAX_PARAMS));
            } else {
                subList = params.subList(i, listSize);
            }

            if (!sb.toString().isEmpty()) {
                sb.append(" OR ");
            }

            sb.append(" ").append(fieldName).append(" IN ( ");
            for (int j = 0; j < subList.size(); j++) {
                if (j > 0) {
                    sb.append(", ");
                }

                appendParameter(sb, subList.get(j), type);
            }
            sb.append(" ) ");
        }

        return sb.toString();
    }

    private void appendParameter(StringBuilder sb, Object value, Class type) {
        if (type == String.class) {
            sb.append("'").append(value).append("'");
        } else {
            sb.append(value);
        }
    }

    public String generateConcatenatedNotInStatementWithIds(String fieldName, List params, Class type) {
        StringBuilder sb = new StringBuilder();

        List subList;
        int listSize = params.size();

        for (int i = 0; i < listSize; i += DBUtil.IN_CLAUSE_MAX_PARAMS) {
            if (listSize > i + DBUtil.IN_CLAUSE_MAX_PARAMS) {
                subList = params.subList(i, (i + DBUtil.IN_CLAUSE_MAX_PARAMS));
            } else {
                subList = params.subList(i, listSize);
            }

            if (!sb.toString().isEmpty()) {
                sb.append(" AND ");
            }

            sb.append(" ").append(fieldName).append(" NOT IN ( ");
            for (int j = 0; j < subList.size(); j++) {
                if (j > 0) {
                    sb.append(", ");
                }

                appendParameter(sb, subList.get(j), type);
            }
            sb.append(" ) ");
        }

        return sb.toString();
    }

    public String generateConcatenatedInStatements(String alias, List params, String objectFieldName) {
        StringBuilder sb = new StringBuilder();
        List subList;
        int listSize = params.size();
        try {

            for (int i = 0; i < listSize; i += DBUtil.IN_CLAUSE_MAX_PARAMS) {
                if (listSize > i + DBUtil.IN_CLAUSE_MAX_PARAMS) {
                    subList = params.subList(i, (i + DBUtil.IN_CLAUSE_MAX_PARAMS));
                } else {
                    subList = params.subList(i, listSize);
                }

                if (!sb.toString().isEmpty()) {
                    sb.append(" OR ");
                }

                sb.append(" ").append(alias).append(" IN ( ");
                for (int j = 0; j < subList.size(); j++) {
                    if (j > 0) {
                        sb.append(", ");
                    }
                    Object obj = subList.get(j);

                    appendParameter(sb, PropertyUtils.getProperty(obj, objectFieldName), obj.getClass().getDeclaredField("id").getType());
                }
                sb.append(" ) ");
            }

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

        return sb.toString();
    }

    public List<Predicate> buildAndSplitPredicates(CriteriaBuilder cb, Path<?> path, Class<?> type, Collection<? extends Number> array) {
        List<Predicate> predicates = new ArrayList<>();
        if (array.size() <= DBUtil.IN_CLAUSE_MAX_PARAMS) {
            predicates.add(path.in(array));
        } else {
            List<? extends Number> listArray;
            if (array instanceof List) {
                listArray = (List<? extends Number>) array;
            } else {
                listArray = new ArrayList<>(array);
            }

            int listSize = listArray.size();
            List<? extends Number> subList;

            for (int i = 0; i < listSize; i += DBUtil.IN_CLAUSE_MAX_PARAMS) {
                if (listSize > i + DBUtil.IN_CLAUSE_MAX_PARAMS) {
                    subList = listArray.subList(i, (i + DBUtil.IN_CLAUSE_MAX_PARAMS));
                    //for mssql 2100 parameters limit
                    if (isMsSql()) {
                        String in = subList.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(","));
                        Predicate rawInPredicate = path.in(cb.function("SQL", type, cb.literal(in)));
                        predicates.add(rawInPredicate);
                    } else {
                        //oracle limit 1000 parameter in statement
                        predicates.add(path.in(subList));
                    }
                } else {
                    subList = listArray.subList(i, listSize);
                    predicates.add(path.in(subList));
                }

            }
        }

        return predicates;
    }

}
