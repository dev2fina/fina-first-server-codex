package net.fina.server.security.api;

import net.fina.server.security.entity.Permission;

import java.util.List;
import java.util.Map;

public interface PermissionLocal {
    Permission loadPermissionByIdName(String idName);

    List<Permission> loadPermissions();

    void updateDescription(Map<String, String> keyValues, long langId);

    Permission findById(long id);

}
