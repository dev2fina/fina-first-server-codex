package net.fina.server.reg.database;


import net.fina.server.reg.model.InputMetaModel;
import net.fina.server.reg.model.InputTypeEnum;

public class OracleDatabaseMeta extends BaseDatabaseMeta implements DatabaseInterface {

    @Override
    public String getFieldDefinition(InputMetaModel metaModel, String tk, String pk, boolean use_autoinc, boolean add_fieldname, boolean add_cr) {
        StringBuffer retval = new StringBuffer(128);

        String fieldname = metaModel.getCode();

        int length = metaModel.getLength();
        int precision = 0;
        if (metaModel.getPrecision() != null) {
            precision = metaModel.getPrecision();
        }

        if (add_fieldname) {
            retval.append(getSafeFieldname(fieldname)).append(' ');
        }

        InputTypeEnum type = metaModel.getTypeEnum();

        switch (type) {
            case TIMESTAMP:
            case DATETIME:
                if (supportsTimestampDataType()) {
                    retval.append("TIMESTAMP");
                } else {
                    retval.append("DATE");
                }
                break;
            case DATE:
                retval.append("DATE");
                break;
            case BOOLEAN:
                retval.append("CHAR(1)");
                break;
            case NUMBER:
            case BIGNUMBER:
                retval.append("NUMBER");
                if (length > 0) {
                    retval.append('(').append(length);
                    if (precision > 0) {
                        retval.append(", ").append(precision);
                    }
                    retval.append(')');
                }
                break;
            case INTEGER:
                retval.append("INTEGER");
                break;
            case STRING:
                if (length >= Const.DEFAULT_VARCHAR_MAX_LENGTH) {
                    retval.append("CLOB");
                } else {
                    if (length == 1) {
                        retval.append("CHAR(1)");
                    } else if (length > 0 && length <= getMaxVARCHARLength()) {
                        retval.append("VARCHAR2(").append(length).append(')');
                    } else {
                        if (length <= 0) {
                            retval.append("VARCHAR2(4000)"); // We don't know, so we just use the maximum...
                        } else {
                            retval.append("CLOB");
                        }
                    }
                }
                break;
            case BINARY: // the BLOB can contain binary data.
                retval.append("BLOB");
                break;
            default:
                retval.append(" UNKNOWN");
                break;
        }

        if (add_cr) {
            retval.append(Const.CR);
        }

        return retval.toString();
    }

    @Override
    public boolean supportsBooleanDataType() {
        return false;
    }

    @Override
    public String[] getReservedWords() {
        return new String[]{
                "ACCESS", "ADD", "ALL", "ALTER", "AND", "ANY", "ARRAYLEN", "AS", "ASC", "AUDIT", "BETWEEN", "BY", "CHAR",
                "CHECK", "CLUSTER", "COLUMN", "COMMENT", "COMPRESS", "CONNECT", "CREATE", "CURRENT", "DATE", "DECIMAL",
                "DEFAULT", "DELETE", "DESC", "DISTINCT", "DROP", "ELSE", "EXCLUSIVE", "EXISTS", "FILE", "FLOAT", "FOR",
                "FROM", "GRANT", "GROUP", "HAVING", "IDENTIFIED", "IMMEDIATE", "IN", "INCREMENT", "INDEX", "INITIAL",
                "INSERT", "INTEGER", "INTERSECT", "INTO", "IS", "LEVEL", "LIKE", "LOCK", "LONG", "MAXEXTENTS", "MINUS",
                "MODE", "MODIFY", "NOAUDIT", "NOCOMPRESS", "NOT", "NOTFOUND", "NOWAIT", "NULL", "NUMBER", "OF", "OFFLINE",
                "ON", "ONLINE", "OPTION", "OR", "ORDER", "PCTFREE", "PRIOR", "PRIVILEGES", "PUBLIC", "RAW", "RENAME",
                "RESOURCE", "REVOKE", "ROW", "ROWID", "ROWLABEL", "ROWNUM", "ROWS", "SELECT", "SESSION", "SET", "SHARE",
                "SIZE", "SMALLINT", "SQLBUF", "START", "SUCCESSFUL", "SYNONYM", "SYSDATE", "TABLE", "THEN", "TO",
                "TRIGGER", "UID", "UNION", "UNIQUE", "UPDATE", "USER", "VALIDATE", "VALUES", "VARCHAR", "VARCHAR2",
                "VIEW", "WHENEVER", "WHERE", "WITH"};
    }

}
