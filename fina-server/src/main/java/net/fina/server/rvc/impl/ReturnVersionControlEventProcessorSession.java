package net.fina.server.rvc.impl;

import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.interceptor.Interceptors;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.rvc.api.ReturnVersionControlEventProcessorLocal;
import net.fina.server.rvc.api.ReturnVersionControlLocal;
import net.fina.server.rvc.event.ReturnItemsStoreEvent;
import net.fina.server.rvc.event.ReturnStatusStoreEvent;
import net.fina.server.rvc.event.ReturnVersionControlEvent;
import net.fina.common.server.StatisticsLogger;

@Stateless
@Local(ReturnVersionControlEventProcessorLocal.class)
@Interceptors(RecordingAuditor.class)
public class ReturnVersionControlEventProcessorSession implements ReturnVersionControlEventProcessorLocal {

    @EJB
    private ReturnVersionControlLocal returnVersionControlLocal;
    @EJB
    private ReturnVersionControlCacheInMemoryStoreService returnVersionControlCacheInMemoryStoreService;

    @Override
    public void storeReturnItems(@Observes(during = TransactionPhase.AFTER_SUCCESS) ReturnItemsStoreEvent event) {
        checkAndCallReturnStore(event);
    }

    @Override
    public void storeReturnStatus(@Observes(during = TransactionPhase.AFTER_SUCCESS) ReturnStatusStoreEvent event) {
        checkAndCallReturnStore(event);
    }

    private void checkAndCallReturnStore(ReturnVersionControlEvent firstEvent) {
        returnVersionControlCacheInMemoryStoreService.set(firstEvent.getType(), firstEvent);

        String findType = "STATUS";
        if (firstEvent.getType().equals(findType)) {
            findType = "ITEM";
        }

        ReturnVersionControlEvent secondEvent = returnVersionControlCacheInMemoryStoreService.get(firstEvent.getProcessId(), findType);

        if (secondEvent != null) {
            try {
                ReturnItemsStoreEvent itemsStoreEvent;
                ReturnStatusStoreEvent statusStoreEvent;
                if (firstEvent.getType().equals("ITEM")) {
                    itemsStoreEvent = (ReturnItemsStoreEvent) firstEvent;
                    statusStoreEvent = (ReturnStatusStoreEvent) secondEvent;
                } else {
                    itemsStoreEvent = (ReturnItemsStoreEvent) secondEvent;
                    statusStoreEvent = (ReturnStatusStoreEvent) firstEvent;

                }


                StatisticsLogger statisticsLogger = new StatisticsLogger("Store Return Version // return id : " + itemsStoreEvent.getReturnId() + " //");
                statisticsLogger.logStage("Store Return : " + itemsStoreEvent.getReturnId());
                returnVersionControlLocal.storeReturn(itemsStoreEvent, statusStoreEvent);
                statisticsLogger.close();
            } finally {
                //Clear cache
                returnVersionControlCacheInMemoryStoreService.remove(firstEvent);
            }
        }
    }
}
