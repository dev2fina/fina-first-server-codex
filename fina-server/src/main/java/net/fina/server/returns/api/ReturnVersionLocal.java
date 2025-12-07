package net.fina.server.returns.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.returns.entity.ReturnVersion;

import java.util.List;
import java.util.Map;

public interface ReturnVersionLocal {

    public List<ReturnVersion> loadAllReturnVersion();

    public List<ReturnVersion> loadReturnVersions(boolean loadAll);

    public ReturnVersion save(ReturnVersion returnVersion) throws FinATypeException;

    public void delete(long id) throws FinATypeException;

    public ReturnVersion findByCode(String code);

    public ReturnVersion loadSimpleReturnVersion(long id);

    boolean checkReturnVersionCodeUnique(ReturnVersion returnVersion);

    List<ReturnVersion> loadReturnVersionsByCodes(List<String> codes);

    Map<String, Long> loadRetrunVersionCodeIdMap();
}
