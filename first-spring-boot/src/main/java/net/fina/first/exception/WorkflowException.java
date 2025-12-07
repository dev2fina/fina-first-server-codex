package net.fina.first.exception;

import lombok.Getter;

/**
 * Exception for workflow process errors.
 */
@Getter
public class WorkflowException extends RuntimeException {

    private final String processId;
    private final String currentState;
    private final String attemptedAction;

    public WorkflowException(String message) {
        super(message);
        this.processId = null;
        this.currentState = null;
        this.attemptedAction = null;
    }

    public WorkflowException(String message, String processId, String currentState, String attemptedAction) {
        super(message);
        this.processId = processId;
        this.currentState = currentState;
        this.attemptedAction = attemptedAction;
    }

    public WorkflowException(String message, Throwable cause) {
        super(message, cause);
        this.processId = null;
        this.currentState = null;
        this.attemptedAction = null;
    }
}
