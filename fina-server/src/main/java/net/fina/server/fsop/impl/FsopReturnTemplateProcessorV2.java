package net.fina.server.fsop.impl;

import net.fina.common.client.constants.ImportStatus;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.fsop.model.FsopImportedReturnMetaModel;
import net.fina.server.processing.ProcessingUtil;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.returns.xml.Item;
import net.fina.server.returns.xml.Return;
import net.fina.common.server.StatisticsLogger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class FsopReturnTemplateProcessorV2 extends FsopReturnTemplateProcessorBase {
    private final FsopImportedReturnMetaModel importedReturn;
    private final net.fina.server.returns.xml.Return returnXml;
    private final List<ProcessItem> items;
    private final Map<String, ProcessItem> returnTemplate;
    private final Map<Long, Map<Long, Set<Long>>> tableNodeRowsMap;
    private final FsopTemplateErrorHandler fsopTemplateErrorHandler;
    private final ProcessingUtil util;
    private final List<Item> extraItems;
    private final Map<Long, Integer> vctTableMaxRowNumber;

    public FsopReturnTemplateProcessorV2(FsopImportedReturnMetaModel importedReturn, Return returnXml, List<ProcessItem> items) {
        this.importedReturn = importedReturn;
        this.returnXml = returnXml;
        this.items = items;

        this.returnTemplate = items == null ? new HashMap<>() :
                items.stream().collect(Collectors.toMap(item -> item.code, item -> item));

        this.tableNodeRowsMap = new ConcurrentHashMap<>();
        this.fsopTemplateErrorHandler = new FsopTemplateErrorHandler();
        this.util = new ProcessingUtil(fsopTemplateErrorHandler,
                importedReturn.getLanguageNumberFormat().trim(),
                importedReturn.getLanguageDateFormat().trim(),
                importedReturn.getLanguageDateTimeFormat().trim(),
                importedReturn.getLangId());

        this.extraItems = Collections.synchronizedList(new ArrayList<>());
        this.vctTableMaxRowNumber = new ConcurrentHashMap<>();
    }

    @Override
    public Map<Long, ProcessItem> process() {
        try (StatisticsLogger statLog = new StatisticsLogger(returnXml.getHeader().getReturnCode() + " Process Template")) {

            statLog.logStage("parse items " + returnXml.getBody().getItems().size());
            parseItems();

            statLog.logStage("Init Vct Variables");
            insertVctVariables();

            statLog.logStage("Check Required Items");
            List<String> requiredItems = checkRequiredItems();

            if (util.getErrorHandler().hasErrors(importedReturn.getId())) {
                throw new FsopTemplateException(this.importedReturn, fsopTemplateErrorHandler.getFinalMessage().toString());
            }

            if (!requiredItems.isEmpty()) {
                throw new FsopTemplateException(this.importedReturn, "Following items are required: " + requiredItems + "\n");
            }

            if (!extraItems.isEmpty()) {
                List<Item> tmpExtraItems = extraItems;
                if (extraItems.size() >= MAX_ITEMS_SIZE_TO_LOG) {
                    tmpExtraItems = extraItems.subList(0, MAX_ITEMS_SIZE_TO_LOG - 1);
                }
                throw new FsopTemplateException(this.importedReturn, "Warning: Following items are absent: " + tmpExtraItems + "\n");
            }

            importedReturn.setStatus(ImportStatus.IMPORTED);
            importedReturn.setMessage(fsopTemplateErrorHandler.getFinalMessage().length() == 0 ? "Process OK" : fsopTemplateErrorHandler.getFinalMessage().toString());

            statLog.logStage("Iterate items");
            Map<Long, ProcessItem> result = items == null ? new HashMap<>() :
                    items.stream().collect(Collectors.toMap(item -> item.nodeId, item -> item));

            return result;
        }
    }

    private void parseItems() {
        if (returnXml != null && returnXml.getBody() != null && returnXml.getBody().getItems() != null) {
            if (returnXml.getBody().getItems().size() > 100_000) {
                parseParallel();
            } else {
                parseSequential();
            }
        }
    }

    private void parseParallel() {
        int availableCores = Runtime.getRuntime().availableProcessors();
        log.info(" ============ Available processors: " + availableCores);
        try (ForkJoinPool customThreadPool = new ForkJoinPool(availableCores / 2)) {
            customThreadPool.submit(() -> returnXml.getBody().getItems().parallelStream().forEach(this::parseItem)).join();
        }
    }

    private void parseSequential() {
        returnXml.getBody().getItems().forEach(this::parseItem);
    }

    private void parseItem(Item item) {
        ProcessItem pItem = returnTemplate.get(item.getItemCode().trim());

        if (pItem != null && (pItem.nodeType == MDTNodeTypes.INPUT || pItem.nodeType == MDTNodeTypes.LIST) && item.getRow() >= 0) {
            Map<Long, Set<Long>> tableNodes = tableNodeRowsMap.computeIfAbsent(pItem.tableId, k -> new ConcurrentHashMap<>());
            Set<Long> rowNumbers = tableNodes.computeIfAbsent(pItem.nodeId, k -> new HashSet<>());

            String value = item.getValue();
            pItem.setInit(!(value == null || value.trim().isEmpty()));

            int rowNumber = item.getRow();

            if (pItem.tableType == ReturnTableType.VCT) {
                processVctItem(item, pItem, rowNumbers, rowNumber, value);
            } else {
                processMctItem(item, pItem, rowNumbers, value);
            }
        } else {
            extraItems.add(item);
        }
    }

    private void processVctItem(Item item, ProcessItem pItem, Set<Long> rowNumbers, int rowNumber, String value) {
        if (!rowNumbers.contains((long) rowNumber)) {
            rowNumbers.add((long) rowNumber);
            vctTableMaxRowNumber.merge(pItem.tableId, rowNumber, Math::max);

            pItem.values.put(rowNumber, value);
            pItem.nValues.put(rowNumber, .0);
            pItem.idByRowNumber.put(rowNumber, (long) (rowNumber + 1));

            util.validateProcessItem(pItem, rowNumber);
        } else {
            extraItems.add(item);
        }
    }

    private void processMctItem(Item item, ProcessItem pItem, Set<Long> rowNumbers, String value) {
        if (rowNumbers.isEmpty()) {
            int mctRowNum = pItem.values.entrySet().stream().findFirst().get().getKey();
            rowNumbers.add((long) mctRowNum);

            pItem.values.put(mctRowNum, value);
            pItem.nValues.put(mctRowNum, .0);

            util.validateProcessItem(pItem, mctRowNum);
        } else {
            extraItems.add(item);
        }
    }

    private void insertVctVariables() {
        vctTableMaxRowNumber.forEach((tableId, maxRowNumber) -> {
            items.stream()
                    .filter(item -> item.tableType == ReturnTableType.VCT && item.tableId == tableId && item.nodeType == MDTNodeTypes.VARIABLE)
                    .forEach(item -> {
                        for (int i = 0; i <= maxRowNumber; i++) {
                            item.values.put(i, "");
                            item.nValues.put(i, .0);
                            item.idByRowNumber.put(i, (long) (i + 1));
                        }
                    });
        });
    }

    private List<String> checkRequiredItems() {
        return items.stream()
                .filter(item -> item.required && (item.nodeType == MDTNodeTypes.INPUT || item.nodeType == MDTNodeTypes.LIST))
                .flatMap(item -> {
                    List<String> missingItems = new ArrayList<>();
                    switch (item.tableType) {
                        case NT:
                        case MCT:
                            if (!item.isInit()) {
                                missingItems.add("Code: " + item.code + ", Description: " + item.description);
                            }
                            break;
                        case VCT:
                            Integer maxRowNumber = vctTableMaxRowNumber.get(item.tableId);
                            if (maxRowNumber != null) {
                                for (int i = 0; i <= maxRowNumber; i++) {
                                    if (item.values.get(i) == null || item.values.get(i).trim().isEmpty()) {
                                        missingItems.add("Code: " + item.code + ", Description: " + item.description + ", row number: " + (i + 1));
                                    }
                                }
                            }
                            break;
                    }
                    return missingItems.stream();
                }).limit(MAX_ITEMS_SIZE_TO_LOG)
                .collect(Collectors.toList());
    }


}
