package net.fina.server.reg.database;

public abstract class BaseDatabaseMeta implements DatabaseInterface {

    private static final String FIELDNAME_PROTECTOR = "_";

    /**
     * @return true if we need to append the PRIMARY KEY block in the create table block after the fields, required for
     * Cache.
     */
    @Override
    public boolean requiresCreateTablePrimaryKeyAppend() {
        return false;
    }

    /**
     * Sanitize a string for usage as a field name
     * <ul>
     * <li>Append an underscore to any field name that matches a reserved word</li>
     * <li>Replaces spaces with underscores</li>
     * <li>Prefixes a string with underscore that begins with a number</li>
     * </ul>
     *
     * @param fieldName value to sanitize
     * @return
     */
    @Override
    public String getSafeFieldname(String fieldName) {
        StringBuffer newName = new StringBuffer(fieldName.length());

        char[] protectors = getFieldnameProtector().toCharArray();

        // alpha numerics , underscores, field protectors only
        for (int idx = 0; idx < fieldName.length(); idx++) {
            char c = fieldName.charAt(idx);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || (c == '_')) {
                newName.append(c);
            } else if (c == ' ') {
                newName.append('_');
            } else {
                // allow protectors
                for (char protector : protectors) {
                    if (c == protector) {
                        newName.append(c);
                    }
                }
            }
            // else {
            // swallow this character
            // }
        }

        // don't allow reserved words
        StringBuilder fieldNameBuilder = new StringBuilder(newName.toString());
        for (String reservedWord : getReservedWords()) {
            if (fieldNameBuilder.toString().equalsIgnoreCase(reservedWord)) {
                fieldNameBuilder.append(getFieldnameProtector());
            }
        }
        fieldName = fieldNameBuilder.toString();

        fieldName = fieldName.replace(" ", getFieldnameProtector());

        // can't start with a number
        if (fieldName.matches("^[0-9].*")) {
            fieldName = getFieldnameProtector() + fieldName;
        }
        return fieldName;
    }

    protected String getFieldnameProtector() {
        return FIELDNAME_PROTECTOR;
    }

    /**
     * @return an array of reserved words for the database type...
     */
    @Override
    public String[] getReservedWords() {
        return new String[]{};
    }

    /**
     * Get the maximum length of a text field (VARCHAR) for this database connection. If this size is exceeded use a CLOB.
     *
     * @return The maximum VARCHAR field length for this database type. (mostly identical to getMaxTextFieldLength() -
     * CLOB_LENGTH)
     */
    @Override
    public int getMaxVARCHARLength() {
        return Const.DEFAULT_VARCHAR_MAX_LENGTH;
    }

    /**
     * @return true if the database supports the Timestamp data type (nanosecond precision and all)
     */
    @Override
    public boolean supportsTimestampDataType() {
        return false;
    }
}
