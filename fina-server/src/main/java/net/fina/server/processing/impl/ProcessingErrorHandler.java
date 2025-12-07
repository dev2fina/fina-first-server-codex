package net.fina.server.processing.impl;

import net.fina.messages.MessagesUtil;
import net.fina.server.processing.ErrorHandler;
import net.fina.server.processing.model.ComparisonItem;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.processing.model.ProcessReturnModel;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * nikoloz on 8/17/15.
 */
public class ProcessingErrorHandler implements ErrorHandler {
    private final Logger log = Logger.getLogger(getClass());

    private final Map<Long, String> errors;
    private final ProcessReturnModel returnModel;
    private final ComparisonMessageTemplateUtil comparisonMessageTemplateUtil;
    private final String langCode;

    public ProcessingErrorHandler(ProcessReturnModel returnModel, String langCode) {
        this.errors = new ConcurrentHashMap<>();
        this.returnModel = returnModel;
        this.comparisonMessageTemplateUtil = new ComparisonMessageTemplateUtil();
        this.langCode = langCode;
    }

    @Override
    public void onError(Throwable ex, ProcessItem item, MessageId messageId, Object... params) {
        onError(item, messageId, params);
    }

    @Override
    public void onComparisonError(ComparisonItem comparisonItem, ProcessItem item, MessageId messageId, Object... params) {
        if (comparisonItem.messageTemplate != null && !Objects.equals(comparisonItem.messageTemplate.trim(), "")) {
            try {
                String compMessage = comparisonMessageTemplateUtil.process(comparisonItem, item, params);
                onError(item, compMessage);
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
                onError(item, messageId, params);
            }
        } else {
            onError(item, messageId, params);
        }
    }

    private void onError(ProcessItem item, MessageId messageId, Object... params) {
        String message = compileMessage(messageId, params);
        onError(item, message);
    }

    private void onError(ProcessItem item, String message) {
        long errorReturnId;
        if (item == null) {
            errorReturnId = this.returnModel.getId();
        } else {
            errorReturnId = item.returnId;
        }
        String oldMessage = errors.get(errorReturnId);
        errors.put(errorReturnId, (oldMessage == null ? "" : oldMessage + " | \n") + message);
    }

    private String compileMessage(MessageId messageId, Object... params) {
        String message = MessagesUtil.getString(messageId.getCode(), langCode);
        if (params != null && params.length > 0) {
            message = String.format(message, params);
        }
        return message;
    }

    @Override
    public boolean hasErrors(long returnId) {
        return errors.get(returnId) != null && (!errors.get(returnId).isEmpty());
    }

    @Override
    public String getErrorMessage(long returnId) {
        return errors.get(returnId);
    }
}
