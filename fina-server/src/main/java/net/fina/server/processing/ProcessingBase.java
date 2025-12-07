package net.fina.server.processing;

import net.fina.common.client.returns.ProcessResult;
import net.fina.server.processing.model.ComparisonItem;
import net.fina.server.processing.model.ProcessItem;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ProcessingBase {
    ProcessResult process(Collection<ProcessItem> nodes);

    ProcessResult ruleValidation(Collection<ProcessItem> nodes, Map<Long, List<ComparisonItem>> comparisons);
}
