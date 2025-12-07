package net.fina.server.security.api;

import net.fina.server.security.entity.State;

import java.util.List;

public interface StateLocal {

    State save(State state);

    State loadStateByKey(String key, Long userId);

    List<State> loadCurrentUserStates();

    void delete(State userState);
}
