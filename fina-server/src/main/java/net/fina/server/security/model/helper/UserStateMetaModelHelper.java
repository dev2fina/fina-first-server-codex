package net.fina.server.security.model.helper;

import net.fina.server.security.entity.State;
import net.fina.server.security.entity.User;
import net.fina.server.security.model.UserStateMetaModel;

import java.util.List;
import java.util.stream.Collectors;

public class UserStateMetaModelHelper {
    public static UserStateMetaModel getModel(State userState) {
        if (userState == null) return null;
        UserStateMetaModel userStateMetaModel = new UserStateMetaModel();
        userStateMetaModel.setKey(userState.getCode());
        userStateMetaModel.setValue(userState.getValue());
        return userStateMetaModel;
    }

    public static State getEntity(UserStateMetaModel userState, User user) {
        if (userState == null) return null;
        State entity = new State();
        entity.setCode(userState.getKey());
        entity.setValue(userState.getValue());
        if (user != null) {
            entity.setUserId(user.getId());
        }
        return entity;
    }

    public static List<UserStateMetaModel> getModels(List<State> userStates) {
        if (userStates == null) return null;
        return userStates.stream().map(userState -> getModel(userState) ).collect(Collectors.toList());
    }
}
