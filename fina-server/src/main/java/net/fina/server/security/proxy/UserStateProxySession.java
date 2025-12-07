package net.fina.server.security.proxy;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import net.fina.server.security.api.StateLocal;
import net.fina.server.security.entity.State;
import net.fina.server.security.model.UserStateMetaModel;
import net.fina.server.security.model.helper.UserStateMetaModelHelper;

import java.util.List;

@Stateless
@Deprecated
public class UserStateProxySession {
    @EJB
    StateLocal stateLocal;

    public List<UserStateMetaModel> getCurrentUserState() {
        List<State> userStates = stateLocal.loadCurrentUserStates();
        return UserStateMetaModelHelper.getModels(userStates);
    }

    public UserStateMetaModel saveCurrentUserState(UserStateMetaModel userState) {
        State state = UserStateMetaModelHelper.getEntity(userState, null);
        State existingState = stateLocal.loadStateByKey(state.getCode(), state.getUserId());
        if (existingState != null) {
            state.setId(existingState.getId());
        }
        return UserStateMetaModelHelper.getModel(stateLocal.save(state));
    }

    public void delete(UserStateMetaModel userState) {
        stateLocal.delete(UserStateMetaModelHelper.getEntity(userState, null));
    }
}
