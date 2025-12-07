package net.fina.server.security.model.helper;

import net.fina.common.shared.user.PermissionModel;
import net.fina.server.security.entity.Permission;

public class PermissionModelHelper {

    public static PermissionModel toModel(Permission p, long langId) {
        return new PermissionModel(p.getId(),
                p.getDescription().getNameStrId(),
                p.getDescription().getDescription(langId),
                p.getIdName(),
                false,
                false);
    }

    public static PermissionModel toModel(Permission p, long langId,boolean permitted) {
        return new PermissionModel(p.getId(),
                p.getDescription().getNameStrId(),
                p.getDescription().getDescription(langId),
                p.getIdName(),
                false,
                permitted);
    }
}
