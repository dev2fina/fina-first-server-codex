package net.fina.server.fsop.impl;

import net.fina.server.processing.ErrorHandler;
import net.fina.server.processing.model.ComparisonItem;
import net.fina.server.processing.model.ProcessItem;

public class FsopTemplateErrorHandler implements ErrorHandler {

    private final StringBuffer finalMessage = new StringBuffer();

    private boolean hasError;

    @Override
    public void onError(Throwable ex, net.fina.server.processing.model.ProcessItem item, MessageId messageId, Object... params) {
        if (params != null && params.length > 0) {
            finalMessage.append(params[0]).append("  \n");
            hasError = true;
        }
    }

    @Override
    public void onComparisonError(ComparisonItem comparisonItem, ProcessItem item, MessageId messageId, Object... params) {
        onError(null, item, messageId, params);
    }

    @Override
    public boolean hasErrors(long returnId) {
        return hasError;
    }

    @Override
    public String getErrorMessage(long returnId) {
        return finalMessage.toString();
    }

    public StringBuffer getFinalMessage() {
        return finalMessage;
    }
}
