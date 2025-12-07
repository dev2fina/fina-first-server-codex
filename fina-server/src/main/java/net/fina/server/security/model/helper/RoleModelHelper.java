package net.fina.server.security.model.helper;

import net.fina.common.shared.user.RoleModel;
import net.fina.server.i18n.helper.Description;
import net.fina.server.security.entity.Role;

public class RoleModelHelper {
    public static Role toEntity(RoleModel role, long langId) {
        Role result = new Role();
        result.setId(role.getId());
        result.setCode(role.getCode());
        result.setDescription(new Description(langId, role.getDescriptionStrId(), role.getDescription()));

        return result;
    }

    public static RoleModel toModel(Role role, long langId) {
        RoleModel model = new RoleModel();
        model.setId(role.getId());
        model.setCode(role.getCode());
        model.setDescription(role.getDescription().getDescription(langId));
        model.setDescriptionStrId(role.getDescription().getNameStrId());

        return model;
    }
}
