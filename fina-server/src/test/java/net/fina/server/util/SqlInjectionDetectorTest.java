package net.fina.server.util;


import net.sf.jsqlparser.JSQLParserException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Author: Oto Iantbelidze
 * Created: 06.10.25
 */
public class SqlInjectionDetectorTest {

    @org.junit.jupiter.api.Test
    void testValidSelectQuery() {
        String validSelectQuery = "SELECT * FROM users WHERE id = 1";
        assertDoesNotThrow(() -> SqlInjectionDetector.validateReadOnlyQuery(validSelectQuery));
    }


    @org.junit.jupiter.api.Test
    void testInvalidDeleteQuery() {
        String invalidDeleteQuery = "DELETE FROM users WHERE id = 1";
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                SqlInjectionDetector.validateReadOnlyQuery(invalidDeleteQuery)
        );
        assertTrue(exception.getMessage().contains("SQL Injection attempt detected"));
    }


    @org.junit.jupiter.api.Test
    void testInvalidUpdateQuery() {
        String invalidUpdateQuery = "UPDATE users SET name = 'test' WHERE id = 1";
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                SqlInjectionDetector.validateReadOnlyQuery(invalidUpdateQuery)
        );
        assertTrue(exception.getMessage().contains("SQL Injection attempt detected"));
    }


    @org.junit.jupiter.api.Test
    void testInvalidInsertQuery() {
        String invalidInsertQuery = "INSERT INTO users (id, name) VALUES (1, 'test')";
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                SqlInjectionDetector.validateReadOnlyQuery(invalidInsertQuery)
        );
        assertTrue(exception.getMessage().contains("SQL Injection attempt detected"));
    }


    @org.junit.jupiter.api.Test
    void testNullQuery() {
        assertDoesNotThrow(() -> SqlInjectionDetector.validateReadOnlyQuery(null));
    }


    @org.junit.jupiter.api.Test
    void testEmptyQuery() {
        String emptyQuery = "";
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                SqlInjectionDetector.validateReadOnlyQuery(emptyQuery)
        );
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertTrue(exception.getMessage().contains("Unparsable query"));
    }


    @org.junit.jupiter.api.Test
    void testValidFormattedSelectQuery() {
        String formattedSelectQuery = "  SELECT    name    FROM   users   WHERE   id  =   1  ";
        assertDoesNotThrow(() -> SqlInjectionDetector.validateReadOnlyQuery(formattedSelectQuery));
    }

    @org.junit.jupiter.api.Test
    void testMalformedQuery() {
        String malformedQuery = "SELECT FROM WHERE";
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                SqlInjectionDetector.validateReadOnlyQuery(malformedQuery)
        );
        assertTrue(exception.getCause() instanceof JSQLParserException);
    }


    @org.junit.jupiter.api.Test
    void testSelectQueryWithJoin() {
        String selectWithJoinQuery = "SELECT u.name, r.role FROM users u INNER JOIN roles r ON u.role_id = r.id";
        assertDoesNotThrow(() -> SqlInjectionDetector.validateReadOnlyQuery(selectWithJoinQuery));
    }


    @Test
    void testSelectQueryWithSubquery() {
        String selectWithSubquery = "SELECT name FROM users WHERE id IN (SELECT user_id FROM orders WHERE total > 100)";
        assertDoesNotThrow(() -> SqlInjectionDetector.validateReadOnlyQuery(selectWithSubquery));
    }
}
