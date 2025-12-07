package net.fina.server.util;


import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;

/**
 * Author: Oto Iantbelidze
 * Created: 06.10.25
 */
public class SqlInjectionDetector {

    public static void validateReadOnlyQuery(String querySql) {
        try {

            if (querySql != null) {
                querySql = querySql.toUpperCase();
                Statement st = CCJSqlParserUtil.parse(querySql);
                if(st ==null){
                    throw new RuntimeException("Unparsable query  : [" + querySql + ']');
                }
                if (!(st instanceof Select)) {
                    throw new RuntimeException("SQL Injection attempt detected in query filter : [" + querySql + ']');
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
