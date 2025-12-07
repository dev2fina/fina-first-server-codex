package net.fina.server.fi.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.server.fi.sync.model.FiSyncMetaModel;

import java.util.List;

public interface FiFirstSyncLocal {
    void syncFis(List<FiSyncMetaModel> models, List<Long> languageIds) throws FinATypeException;
}
