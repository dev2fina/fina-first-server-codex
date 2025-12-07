package net.fina.first.common.exception;

import net.fina.ecm.alfresco.api.common.representation.UnexpectedErrorRepresentation;
import net.fina.first.FirstUtil;

public class RenditionException extends Exception {

    public RenditionException(String message) {
        super(message);
    }

    public RenditionException(UnexpectedErrorRepresentation unexpectedErrorRepresentation) {
        this(FirstUtil.getModifiedErrorBriefSummeryMessage(unexpectedErrorRepresentation.getBriefSummary()));
    }
}
