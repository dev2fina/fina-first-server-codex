package net.fina.server.rvc.api;

import net.fina.server.rvc.event.ReturnItemsStoreEvent;
import net.fina.server.rvc.event.ReturnStatusStoreEvent;

public interface ReturnVersionControlEventProcessorLocal {

    void storeReturnItems(ReturnItemsStoreEvent event);

    void storeReturnStatus(ReturnStatusStoreEvent event);
}
