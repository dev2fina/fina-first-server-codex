package net.fina.common.client.exception;

public class FinAAccessDeniedException extends RuntimeException {

    public FinAAccessDeniedException() {
        super("net.fina.exception.invalidUserPermissions");
    }
}
