package net.fina.server.reg.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegDatabaseUtil {

    private static Logger log = Logger.getLogger(RegDatabaseUtil.class.getName());

    private String dbProductName;

    public RegDatabaseUtil(Connection connection) {
        try {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            if (databaseMetaData != null) {
                dbProductName = databaseMetaData.getDatabaseProductName();
            }
        } catch (Throwable t) {
            dbProductName = "";
            log.log(Level.ALL, t.getMessage(), t);
        }
    }

    public boolean isOracle() {
        return dbProductName.toLowerCase().contains("oracle");
    }

    public boolean isMsSql() {
        return dbProductName.toLowerCase().contains("microsoft sql server");
    }

    public boolean isMysql() {
        return dbProductName.toLowerCase().contains("mysql");
    }

    public void closeStatement(Statement st) {
        try {
            if (st != null) {
                st.close();
            }
        } catch (Throwable th) {
            log.log(Level.ALL, "Error during closing statement", th);
        }
    }

    public void closeResultSet(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Throwable th) {
            log.log(Level.ALL, "Error during closing result set", th);
        }
    }

    public void close(ResultSet rs, Statement st, Connection con) {
        closeResultSet(rs);
        closeStatement(st);
    }
}
