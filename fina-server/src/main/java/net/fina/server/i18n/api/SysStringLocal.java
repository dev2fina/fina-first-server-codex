package net.fina.server.i18n.api;

import net.fina.server.i18n.entity.SysString;

import java.util.List;

public interface SysStringLocal {

    List<SysString> loadSysStrings();

    void update(Long nameStrId, Long langId, String value);

    long add(Long langId, String value);

    void delete(long nameStrId);

    void delete(List<Long> nameStrIdList);
}
