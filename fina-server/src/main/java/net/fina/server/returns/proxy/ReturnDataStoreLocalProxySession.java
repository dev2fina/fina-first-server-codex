package net.fina.server.returns.proxy;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.i18n.proxy.LanguageProxySession;
import net.fina.server.returns.api.ReturnDataStoreLocal;
import net.fina.server.returns.model.RDataMetaModel;
import net.fina.server.returns.model.RItemMetaModel;
import net.fina.server.security.proxy.UserProxySession;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class ReturnDataStoreLocalProxySession {

    private final Logger log = Logger.getLogger(getClass().getName());

    @Inject
    private LanguageProxySession languageProxySession;

    @Inject
    private ReturnDataStoreLocal returnDataStoreLocal;

    @Inject
    private ReturnProxySession returnProxySession;

    @Inject
    private UserProxySession userProxySession;


    public RDataMetaModel loadReturnData(long returnId, long versionId,  String langCode) {
        long langId = languageProxySession.getLanguageByCode(langCode).getId();

        return returnDataStoreLocal.loadReturnData(returnId, versionId, langId);
    }

    public RDataMetaModel loadReturnData(long returnId, long versionId, long langId) {
        RDataMetaModel result = returnDataStoreLocal.loadReturnData(returnId, versionId, langId);

        Map<String, Map<Long, RItemMetaModel>> itemsByCodesMap = new HashMap<>();
        Map<Long, String> codeByIdMap = new HashMap<>();

        result.getTables().forEach(table -> {
            int startIndex = table.getType().equals(ReturnTableType.MCT) ? 1 : 0;
            for (int i = startIndex; i < table.getRows().size(); i++) {
                List<RItemMetaModel> rowItems = table.getRows().get(i).getRowItems();
                rowItems.forEach((rowItem) -> {
                    setItem(codeByIdMap, itemsByCodesMap, rowItem);

                    rowItem.setDependentNodeIds(new ArrayList<>());
                    rowItem.setUsedByNodeIds(new ArrayList<>());

                    result.getDependentNodes().forEach(dependentNode -> {
                        if (dependentNode.getDependentNodeId() == rowItem.getNodeId()) {
                            rowItem.getDependentNodeIds().add(dependentNode.getNodeId());
                        }
                        if (dependentNode.getNodeId() == rowItem.getNodeId()) {
                            rowItem.getUsedByNodeIds().add(dependentNode.getDependentNodeId());
                        }
                    });
                });
            }

        });

        result.getDependencies().forEach(dep -> {
            setItem(codeByIdMap, itemsByCodesMap, dep);
        });

        result.setCodeByIdMap(codeByIdMap);
        result.setItemsByCodesMap(itemsByCodesMap);

        return result;
    }

    private void setItem
            (Map<Long, String> codeByIdMap, Map<String, Map<Long, RItemMetaModel>> itemsByCodesMap, RItemMetaModel item) {

        codeByIdMap.put(item.getNodeId(), item.getCode());
        Map<Long, RItemMetaModel> rowNumberItemMap;
        if (!itemsByCodesMap.containsKey(item.getCode())) {
            rowNumberItemMap = new HashMap<>();
            rowNumberItemMap.put(item.getRowNumber(), item);
            if (item.getCode() != null) {
                itemsByCodesMap.put(item.getCode(), rowNumberItemMap);
            } else {
                log.error("Node Code is null - " + item);
            }
        } else {
            rowNumberItemMap = itemsByCodesMap.get(item.getCode());
        }
        rowNumberItemMap.put(item.getRowNumber(), item);
    }
}
