package net.fina.server.security.model.helper;

import net.fina.common.shared.user.UserModel;
import net.fina.server.i18n.helper.Description;
import net.fina.server.misc.ObjectUtil;
import net.fina.server.security.entity.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserModelHelper {

    public static UserModel toModel(User user, long langId) {
        UserModel result = new UserModel();
        ObjectUtil.copyProperties(user, result);

        result.setId(user.getId());
        result.setLogin(user.getLogin());
        result.setBlocked(user.getBlocked());
        result.setDeleted(user.isDeleted());
        result.setDescription(user.getDescription().getDescription(langId));
        result.setDescriptionStrId(user.getDescription().getNameStrId());
        result.setTitleDescription(user.getTitledescription().getDescription(langId));
        result.setTitleDescriptionStrId(user.getTitledescription().getNameStrId());
        result.setContactPerson(user.getContactPersonDescription().getDescription(langId));
        if (user.getContactPersonPosition() != null) {
            result.setContactPersonPosition(user.getContactPersonPosition().getDescription(langId));
            result.setContactPersonPositionStrId(user.getContactPersonPosition().getNameStrId());
        }
        result.setEmail(user.getEmail());
        result.setPhone(user.getPhone());
        result.setUserType(user.getUserType());
        return result;
    }

    public static List<UserModel> toModels(Collection<User> users, long langId) {
        return users.stream().map(u -> toModel(u, langId)).collect(Collectors.toList());
    }

    public static User toEntity(UserModel user, long langId) {
        User result = new User();
        ObjectUtil.copyProperties(user, result);

        result.setId(user.getId());
        result.setLogin(user.getLogin());
        result.setBlocked(user.isBlocked());
        result.setDeleted(user.isDeleted());
        result.setDescription(new Description(langId, user.getDescriptionStrId(), user.getDescription()));
        result.setTitledescription(new Description(langId, user.getTitleDescriptionStrId(), user.getTitleDescription()));
        result.setContactPersonDescription(new Description(langId, user.getContactPersonStrId(), user.getContactPerson()));
        result.setContactPersonPosition(new Description(langId, user.getContactPersonPositionStrId(), user.getContactPersonPosition()));
        result.setEmail(user.getEmail());
        result.setPhone(user.getPhone());
        result.setPassword(user.getPassword());
        result.setChangePassword(user.isChangePassword());


        return result;
    }
}
