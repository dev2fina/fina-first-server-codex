package net.fina.server.reg.database;


import net.fina.server.reg.model.InputMetaModel;

public interface DatabaseInterface {
    /**
     * Describe a Value as a field in the database.
     *
     * @param metaModel     The value to describe
     * @param tk            The field that's going to be the technical key
     * @param pk            The field that's going to be the primary key
     * @param use_autoinc   Use autoincrement or not
     * @param add_fieldname Add the fieldname to the definition or not
     * @param add_cr        Add a cariage return at the end of the definition or not.
     * @return a value described as a field in this database.
     */
    String getFieldDefinition(InputMetaModel metaModel, String tk, String pk, boolean use_autoinc, boolean add_fieldname, boolean add_cr);

    /**
     * @return true if we need to append the PRIMARY KEY block in the create table block after the fields, required for
     * Cache.
     */
    boolean requiresCreateTablePrimaryKeyAppend();

    /**
     * Given a String, this will sanitize and return a value safe for usage as a column name
     *
     * @param fieldname value to sanitize
     * @return a String safe for usage as a column name without the need for quoting
     */
    String getSafeFieldname(String fieldname);

    /**
     * @return an array of reserved words for the database type...
     */
    String[] getReservedWords();

    /**
     * @return true if the database supports a boolean, bit, logical, ... datatype
     */
    boolean supportsBooleanDataType();

    /**
     * Get the maximum length of a text field (VARCHAR) for this database connection. If this size is exceeded use a CLOB.
     *
     * @return The maximum VARCHAR field length for this database type. (mostly identical to getMaxTextFieldLength() -
     * CLOB_LENGTH)
     */
    int getMaxVARCHARLength();

    /**
     * @return true if the database supports the Timestamp data type (nanosecond precision and all)
     */
    boolean supportsTimestampDataType();
}
