package net.fina.common.client.filter;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: nikoloz
 * Date: 8/1/13
 * Time: 10:09 PM
 */
public enum AuditLogFilter {
    ENTITY_ID,
    ENTITY_NAME,
    ENTITY_PROPERTY,
    OLD_VALUE,
    NEW_VALUE,
    OPERATION_TYPE,
    ACTOR_ID,
    TIME_FROM,
    TIME_TO,
    CLIENT_NAME,
    OFFSET,
    LIMIT,
    SORT_DISABLED;
}
