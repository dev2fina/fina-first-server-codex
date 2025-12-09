package net.fina.first.exception;

/**
 * Exception thrown when user lacks required permissions.
 */
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public static AccessDeniedException forPermission(String requiredPermission) {
        return new AccessDeniedException(
                String.format("Access denied. Required permission: %s", requiredPermission));
    }
}
