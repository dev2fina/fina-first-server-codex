package net.fina.server.processing.api;

import net.fina.common.client.returns.ProcessResult;
import net.fina.server.i18n.entity.Language;
import net.fina.server.processing.model.ComparisonItem;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.returns.entity.Return;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface FinaCrossFileValidator {

    Map<Long, ProcessResult> validatePostProcessComparisons(Collection<Return> processedReturns, Map<Long, List<ComparisonItem>> comparisons, Collection<ProcessItem> processItems, Language language);
}
