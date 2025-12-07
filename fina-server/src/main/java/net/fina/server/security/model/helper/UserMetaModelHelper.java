package net.fina.server.security.model.helper;

import net.fina.server.security.entity.User;
import net.fina.server.security.model.UserMetaModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UserMetaModelHelper {
    public static User fromModel(UserMetaModel model) {
        User user = new User();
        user.setId(model.getId());
        user.setLogin(model.getLogin());
        return user;
    }

    public static List<User> fromModels(Collection<UserMetaModel> models) {
        if (models == null) {
            return Collections.emptyList();
        }

        List<User> users = new ArrayList<>(models.size());
        for (UserMetaModel model : models) {
            users.add(fromModel(model));
        }
        return users;
    }

    public static UserMetaModel toModel(User user) {
        UserMetaModel meta = new UserMetaModel();
        meta.setId(user.getId());
        meta.setLogin(user.getLogin());
        return meta;
    }

    public static List<UserMetaModel> toModels(Collection<User> users) {
        if (users == null) {
            return Collections.emptyList();
        }
        
        List<UserMetaModel> models = new ArrayList<>(users.size());
        for (User user : users) {
            models.add(toModel(user));
        }
        return models;
    }
}
