package net.fina.server.fsop.impl;

import net.fina.common.client.constants.ImportStatus;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.fsop.model.FsopImportedReturnMetaModel;
import net.fina.server.processing.ProcessingUtil;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.returns.xml.Item;
import net.fina.server.returns.xml.Return;

import java.util.*;

public class FsopReturnTemplateProcessor extends FsopReturnTemplateProcessorBase {

    private final FsopImportedReturnMetaModel importedReturn;
    private final net.fina.server.returns.xml.Return returnXml;
    private final List<ProcessItem> items;

    private final Map<String, ProcessItem> returnTemplate;

    //< tableId, < nodeId, List < rowNumber > >
    private final Map<Long, Map<Long, Set<Long>>> tableNodeRowsMap;

    private final FsopTemplateErrorHandler fsopTemplateErrorHandler;

    private final ProcessingUtil util;

    //Extra items
    private final List<Item> extraItems;

    //Extract xml items
    private final Map<Long, Integer> vctTableMaxRowNumber;

    public FsopReturnTemplateProcessor(FsopImportedReturnMetaModel importedReturn, Return returnXml, List<ProcessItem> items) {
        this.importedReturn = importedReturn;
        this.returnXml = returnXml;
        this.items = items;

        returnTemplate = new HashMap<>();
        if (items != null) {
            for (ProcessItem item : items) {
                returnTemplate.put(item.code, item);
            }
        }

        tableNodeRowsMap = new HashMap<>();

        fsopTemplateErrorHandler = new FsopTemplateErrorHandler();

        this.util = new ProcessingUtil(fsopTemplateErrorHandler, importedReturn.getLanguageNumberFormat().trim(), importedReturn.getLanguageDateFormat().trim(), importedReturn.getLanguageDateTimeFormat().trim(), importedReturn.getLangId());

        extraItems = new ArrayList<>();
        vctTableMaxRowNumber = new HashMap<>();
    }

    @Override
    public Map<Long, ProcessItem> process() {

        parseItems();

        insertVctVariables();

        long startTime = System.currentTimeMillis();
        log.info("Start Check Required Items: " + importedReturn.getReturnCode());
        List<String> requiredItems = checkRequiredItems();
        log.info("End Check Required Items: " + importedReturn.getReturnCode() + ", elapsed time: " + (System.currentTimeMillis() - startTime) + " ms");

        if (util.getErrorHandler().hasErrors(importedReturn.getId())) {
            throw new FsopTemplateException(this.importedReturn, fsopTemplateErrorHandler.getFinalMessage().toString());
        }

        if (!requiredItems.isEmpty()) {
            throw new FsopTemplateException(this.importedReturn, "Following items are required: " + requiredItems + "\n");
        }

        //Append absent items message
        if (!extraItems.isEmpty()) {
            throw new FsopTemplateException(this.importedReturn, "Warning: Following items are absent: " + extraItems + "\n");
        }

        importedReturn.setStatus(ImportStatus.IMPORTED);
        importedReturn.setMessage(fsopTemplateErrorHandler.getFinalMessage().length() == 0 ? "Process OK" : fsopTemplateErrorHandler.getFinalMessage().toString());

        Map<Long, ProcessItem> result = new HashMap<>();
        if (items != null) {
            for (ProcessItem item : items) {
                result.put(item.nodeId, item);
            }
        }
        return result;
    }

    private void parseItems() {
        if (returnXml != null && returnXml.getBody() != null && returnXml.getBody().getItems() != null) {
            for (Item item : returnXml.getBody().getItems()) {
                if (extraItems.size() > MAX_ITEMS_SIZE_TO_LOG) {
                    log.warn("Too many extra items detected, skipping required items collection");
                    return;
                }

                ProcessItem pItem = returnTemplate.get(item.getItemCode().trim());

                if (pItem != null
                        && (pItem.nodeType == MDTNodeTypes.INPUT || pItem.nodeType == MDTNodeTypes.LIST)
                        && item.getRow() >= 0) {

                    //Compile duplicate items detection collections
                    Map<Long, Set<Long>> tableNodes = tableNodeRowsMap.get(pItem.tableId);
                    if (tableNodes == null) {
                        tableNodes = new HashMap<>();
                        tableNodeRowsMap.put(pItem.tableId, tableNodes);
                    }
                    Set<Long> rowNumbers = tableNodes.get(pItem.nodeId);
                    if (rowNumbers == null) {
                        rowNumbers = new HashSet<>();
                        tableNodes.put(pItem.nodeId, rowNumbers);
                    }

                    String value = item.getValue();

                    pItem.setInit(!(value == null || value.trim().isEmpty()));

                    int rowNumber = item.getRow();

                    if (pItem.tableType == ReturnTableType.VCT) {
                        try {
                            //Detect duplicate VCT rows
                            if (rowNumbers.contains((long) rowNumber)) {
                                extraItems.add(item);
                            } else {
                                rowNumbers.add((long) rowNumber);

                                //Compile VCT max row number
                                Object maxRowNumber = vctTableMaxRowNumber.get(pItem.tableId);
                                if (maxRowNumber != null) {
                                    vctTableMaxRowNumber.put(pItem.tableId, rowNumber > (int) maxRowNumber ? rowNumber : (int) maxRowNumber);
                                } else {
                                    vctTableMaxRowNumber.put(pItem.tableId, rowNumber);
                                }

                                pItem.values.put(rowNumber, value);
                                pItem.nValues.put(rowNumber, .0);
                                pItem.idByRowNumber.put(rowNumber, (long) (rowNumber + 1));

                                util.validateProcessItem(pItem, rowNumber);
                            }
                        } catch (Throwable t) {
                            log.error(t.getMessage(), t);
                        }
                    } else {
                        //Detect duplicate MCT rows
                        if (!rowNumbers.isEmpty()) {
                            extraItems.add(item);
                        } else {
                            int mctRowNum = pItem.values.entrySet().stream().findFirst().get().getKey();
                            rowNumbers.add((long) mctRowNum);

                            pItem.values.put(mctRowNum, value);
                            pItem.nValues.put(mctRowNum, .0);

                            util.validateProcessItem(pItem, mctRowNum);
                        }
                    }
                } else {
                    extraItems.add(item);
                }
            }
        }
    }

    private void insertVctVariables() {
        //Insert VCT variables
        for (Map.Entry<Long, Integer> vctTableIdMaxRowNumberEntry : vctTableMaxRowNumber.entrySet()) {
            for (ProcessItem item : items) {
                if (item.tableType == ReturnTableType.VCT && item.tableId == vctTableIdMaxRowNumberEntry.getKey() && item.nodeType == MDTNodeTypes.VARIABLE) {
                    for (int i = 0; i <= vctTableIdMaxRowNumberEntry.getValue(); i++) {
                        item.values.put(i, "");
                        item.nValues.put(i, .0);
                        item.idByRowNumber.put(i, (long) (i + 1));
                    }
                }
            }
        }
    }

    private List<String> checkRequiredItems() {
        List<String> requiredItems = new ArrayList<>();
        if (items != null) {
            for (ProcessItem item : items) {

                if (requiredItems.size() > MAX_ITEMS_SIZE_TO_LOG) {
                    log.warn("Too many required items detected, skipping required items collection");
                    return requiredItems;
                }

                if (item.required && (item.nodeType == MDTNodeTypes.INPUT || item.nodeType == MDTNodeTypes.LIST)) {
                    switch (item.tableType) {
                        case NT:
                        case MCT:
                            if (!item.isInit()) {
                                requiredItems.add("Code: " + item.code + ", Description:" + item.description);
                            }
                            break;
                        case VCT:
                            Integer maxRowNumber = vctTableMaxRowNumber.get(item.tableId);
                            if (maxRowNumber != null) {
                                for (int i = 0; i <= maxRowNumber; i++) {
                                    if (item.values.get(i) == null || item.values.get(i).trim().isEmpty()) {
                                        requiredItems.add("Code: " + item.code + ", Description:" + item.description + ", row number:" + (i + 1));
                                    }
                                }
                            }
                            break;
                    }
                }
            }
        }
        return requiredItems;
    }
}
