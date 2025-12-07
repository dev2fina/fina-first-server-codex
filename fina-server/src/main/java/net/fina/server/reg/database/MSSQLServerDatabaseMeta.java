package net.fina.server.reg.database;

import net.fina.server.reg.model.InputMetaModel;
import net.fina.server.reg.model.InputTypeEnum;

public class MSSQLServerDatabaseMeta extends BaseDatabaseMeta implements DatabaseInterface {

    @Override
    public String getFieldDefinition(InputMetaModel metaModel, String tk, String pk, boolean use_autoinc, boolean add_fieldname, boolean add_cr) {
        String retval = "";

        String fieldname = metaModel.getCode();

        int length = metaModel.getLength();
        int precision = -1;
        if (metaModel.getPrecision() != null) {
            precision = metaModel.getPrecision();
        }

        if (add_fieldname) {
            retval += getSafeFieldname(fieldname) + " ";
        }

        InputTypeEnum type = metaModel.getTypeEnum();

        switch (type) {
            case DATE:
                retval += "DATE";
                break;
            case TIMESTAMP:
            case DATETIME:
                retval += "DATETIME";
                break;
            case BOOLEAN:
                if (supportsBooleanDataType()) {
                    retval += "BIT";
                } else {
                    retval += "CHAR(1)";
                }
                break;
            case NUMBER:
            case INTEGER:
            case BIGNUMBER:
                if (fieldname.equalsIgnoreCase(tk) || // Technical key
                        fieldname.equalsIgnoreCase(pk) // Primary key
                ) {
                    if (use_autoinc) {
                        retval += "BIGINT PRIMARY KEY IDENTITY(0,1)";
                    } else {
                        retval += "BIGINT PRIMARY KEY";
                    }
                } else {
                    if (precision == 0) {
                        if (length > 18) {
                            retval += "DECIMAL(" + length + ",0)";
                        } else {
                            if (length > 9) {
                                retval += "BIGINT";
                            } else {
                                retval += "INT";
                            }
                        }
                    } else {
                        if (precision > 0) {
                            if (length > 0) {
                                retval += "DECIMAL(" + length + "," + precision + ")";
                            }
                        } else {
                            retval += "FLOAT";
                        }
                    }
                }
                break;
            case STRING:
                if (length < getMaxVARCHARLength()) {
                    // Maybe use some default DB String length in case length<=0
                    if (length > 0) {
                        retval += "NVARCHAR(" + length + ")";
                    } else {
                        retval += "NVARCHAR(4000)";
                    }
                } else {
                    retval += "TEXT"; // Up to 2bilion characters.
                }
                break;
            default:
                retval += " UNKNOWN";
                break;
        }

        if (add_cr) {
            retval += Const.CR;
        }

        return retval;
    }

    @Override
    public String[] getReservedWords() {
        return new String[]{
                /*
                 * Transact-SQL Reference: Reserved Keywords Includes future keywords: could be reserved in future releases of SQL
                 * Server as new features are implemented. REMARK: When SET QUOTED_IDENTIFIER is ON (default), identifiers can be
                 * delimited by double quotation marks, and literals must be delimited by single quotation marks. When SET
                 * QUOTED_IDENTIFIER is OFF, identifiers cannot be quoted and must follow all Transact-SQL rules for identifiers.
                 */
                "ABSOLUTE", "ACTION", "ADD", "ADMIN", "AFTER", "AGGREGATE", "ALIAS", "ALL", "ALLOCATE", "ALTER", "AND",
                "ANY", "ARE", "ARRAY", "AS", "ASC", "ASSERTION", "AT", "AUTHORIZATION", "BACKUP", "BEFORE", "BEGIN",
                "BETWEEN", "BINARY", "BIT", "BLOB", "BOOLEAN", "BOTH", "BREADTH", "BREAK", "BROWSE", "BULK", "BY", "CALL",
                "CASCADE", "CASCADED", "CASE", "CAST", "CATALOG", "CHAR", "CHARACTER", "CHECK", "CHECKPOINT", "CLASS",
                "CLOB", "CLOSE", "CLUSTERED", "COALESCE", "COLLATE", "COLLATION", "COLUMN", "COMMIT", "COMPLETION",
                "COMPUTE", "CONNECT", "CONNECTION", "CONSTRAINT", "CONSTRAINTS", "CONSTRUCTOR", "CONTAINS",
                "CONTAINSTABLE", "CONTINUE", "CONVERT", "CORRESPONDING", "CREATE", "CROSS", "CUBE", "CURRENT",
                "CURRENT_DATE", "CURRENT_PATH", "CURRENT_ROLE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER",
                "CURSOR", "CYCLE", "DATA", "DATABASE", "DATE", "DAY", "DBCC", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE",
                "DEFAULT", "DEFERRABLE", "DEFERRED", "DELETE", "DENY", "DEPTH", "DEREF", "DESC", "DESCRIBE", "DESCRIPTOR",
                "DESTROY", "DESTRUCTOR", "DETERMINISTIC", "DIAGNOSTICS", "DICTIONARY", "DISCONNECT", "DISK", "DISTINCT",
                "DISTRIBUTED", "DOMAIN", "DOUBLE", "DROP", "DUMMY", "DUMP", "DYNAMIC", "EACH", "ELSE", "END", "END-EXEC",
                "EQUALS", "ERRLVL", "ESCAPE", "EVERY", "EXCEPT", "EXCEPTION", "EXEC", "EXECUTE", "EXISTS", "EXIT",
                "EXTERNAL", "FALSE", "FETCH", "FILE", "FILLFACTOR", "FIRST", "FLOAT", "FOR", "FOREIGN", "FOUND", "FREE",
                "FREETEXT", "FREETEXTTABLE", "FROM", "FULL", "FUNCTION", "GENERAL", "GET", "GLOBAL", "GO", "GOTO",
                "GRANT", "GROUP", "GROUPING", "HAVING", "HOLDLOCK", "HOST", "HOUR", "IDENTITY", "IDENTITY_INSERT",
                "IDENTITYCOL", "IF", "IGNORE", "IMMEDIATE", "IN", "INDEX", "INDICATOR", "INITIALIZE", "INITIALLY",
                "INNER", "INOUT", "INPUT", "INSERT", "INT", "INTEGER", "INTERSECT", "INTERVAL", "INTO", "IS", "ISOLATION",
                "ITERATE", "JOIN", "KEY", "KILL", "LANGUAGE", "LARGE", "LAST", "LATERAL", "LEADING", "LEFT", "LESS",
                "LEVEL", "LIKE", "LIMIT", "LINENO", "LOAD", "LOCAL", "LOCALTIME", "LOCALTIMESTAMP", "LOCATOR", "MAP",
                "MATCH", "MINUTE", "MODIFIES", "MODIFY", "MODULE", "MONTH", "NAMES", "NATIONAL", "NATURAL", "NCHAR",
                "NCLOB", "NEW", "NEXT", "NO", "NOCHECK", "NONCLUSTERED", "NONE", "NOT", "NULL", "NULLIF", "NUMERIC",
                "OBJECT", "OF", "OFF", "OFFSETS", "OLD", "ON", "ONLY", "OPEN", "OPENDATASOURCE", "OPENQUERY",
                "OPENROWSET", "OPENXML", "OPERATION", "OPTION", "OR", "ORDER", "ORDINALITY", "OUT", "OUTER", "OUTPUT",
                "OVER", "PAD", "PARAMETER", "PARAMETERS", "PARTIAL", "PATH", "PERCENT", "PLAN", "POSTFIX", "PRECISION",
                "PREFIX", "PREORDER", "PREPARE", "PRESERVE", "PRIMARY", "PRINT", "PRIOR", "PRIVILEGES", "PROC",
                "PROCEDURE", "PUBLIC", "RAISERROR", "READ", "READS", "READTEXT", "REAL", "RECONFIGURE", "RECURSIVE",
                "REF", "REFERENCES", "REFERENCING", "RELATIVE", "REPLICATION", "RESTORE", "RESTRICT", "RESULT", "RETURN",
                "RETURNS", "REVOKE", "RIGHT", "ROLE", "ROLLBACK", "ROLLUP", "ROUTINE", "ROW", "ROWCOUNT", "ROWGUIDCOL",
                "ROWS", "RULE", "SAVE", "SAVEPOINT", "SCHEMA", "SCOPE", "SCROLL", "SEARCH", "SECOND", "SECTION", "SELECT",
                "SEQUENCE", "SESSION", "SESSION_USER", "SET", "SETS", "SETUSER", "SHUTDOWN", "SIZE", "SMALLINT", "SOME",
                "SPACE", "SPECIFIC", "SPECIFICTYPE", "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "START", "STATE",
                "STATEMENT", "STATIC", "STATISTICS", "STRUCTURE", "SYSTEM_USER", "TABLE", "TEMPORARY", "TERMINATE",
                "TEXTSIZE", "THAN", "THEN", "TIME", "TIMESTAMP", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TO", "TOP",
                "TRAILING", "TRAN", "TRANSACTION", "TRANSLATION", "TREAT", "TRIGGER", "TRUE", "TRUNCATE", "TSEQUAL",
                "UNDER", "UNION", "UNIQUE", "UNKNOWN", "UNNEST", "UPDATE", "UPDATETEXT", "USAGE", "USE", "USER", "USING",
                "VALUE", "VALUES", "VARCHAR", "VARIABLE", "VARYING", "VIEW", "WAITFOR", "WHEN", "WHENEVER", "WHERE",
                "WHILE", "WITH", "WITHOUT", "WORK", "WRITE", "WRITETEXT", "YEAR", "ZONE"};
    }

    /**
     * @return true if the database supports a boolean, bit, logical, ... datatype The default is false: map to a string.
     */
    @Override
    public boolean supportsBooleanDataType() {
        return false;
    }

    @Override
    public int getMaxVARCHARLength() {
        return 8000;
    }
}
