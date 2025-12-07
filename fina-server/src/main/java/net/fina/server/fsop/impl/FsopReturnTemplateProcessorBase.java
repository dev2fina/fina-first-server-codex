package net.fina.server.fsop.impl;

import net.fina.server.processing.model.ProcessItem;
import org.jboss.logging.Logger;

import java.util.Map;

public abstract class FsopReturnTemplateProcessorBase {
    protected final Logger log = Logger.getLogger(getClass());
    protected final int MAX_ITEMS_SIZE_TO_LOG = 200;
    public abstract Map<Long, ProcessItem> process();

}
