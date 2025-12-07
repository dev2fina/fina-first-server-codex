package net.fina.first.common.exception;

import net.fina.ecm.alfresco.api.common.representation.UnexpectedErrorRepresentation;
import net.fina.first.FirstUtil;

public class NodeException extends Exception {

    public NodeException(String message) {
        super(message);
    }

    public NodeException(UnexpectedErrorRepresentation unexpectedErrorRepresentation) {
        this(FirstUtil.getModifiedErrorBriefSummeryMessage(unexpectedErrorRepresentation.getBriefSummary()));
    }

}
