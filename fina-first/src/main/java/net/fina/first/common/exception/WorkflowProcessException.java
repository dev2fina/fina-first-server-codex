package net.fina.first.common.exception;

import net.fina.ecm.alfresco.api.common.representation.UnexpectedErrorRepresentation;
import net.fina.first.FirstUtil;

public class WorkflowProcessException extends Exception {

    private WorkflowProcessException(String message) {
        super(message);
    }

    public WorkflowProcessException(UnexpectedErrorRepresentation unexpectedErrorRepresentation) {
        this(FirstUtil.getModifiedErrorBriefSummeryMessage(unexpectedErrorRepresentation.getBriefSummary()));
    }

}
