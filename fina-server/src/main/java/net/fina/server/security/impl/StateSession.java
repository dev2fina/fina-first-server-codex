package net.fina.server.security.impl;

import net.fina.server.interceptors.LogDescription;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.security.api.StateLocal;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.State;

import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;

@Stateless
@Local(StateLocal.class)
@Interceptors(RecordingAuditor.class)
public class StateSession implements StateLocal {

    @Inject
    private EntityManager em;

    @EJB
    private UserLocal current;

    @Override
    
    public State save(State state) {
        if (state.getUserId() == 0) {
            state.setUserId(current.getCurrentUserId());
        }
        if (state.getId() > 0) {
            state = em.merge(state);
        } else {
            State oldState = loadStateByKey(state.getCode(), state.getUserId());
            if (oldState != null) {
                State s = em.find(State.class, oldState.getId());
                s.setCode(state.getCode());
                s.setValue(state.getValue());
            } else {
                em.persist(state);
            }
        }

        return state;
    }

    @Override
    @LogDescription(ignore = true)
    public State loadStateByKey(String key, Long userId) {
        State state = null;

        if (userId == null) {
            userId = current.getCurrentUserId();
        }

        Query loadQuery = em.createNamedQuery("State.findByCodeAndUserId");
        loadQuery.setParameter("code", key);
        loadQuery.setParameter("userId", userId);

        try {
            state = (State) loadQuery.getSingleResult();
        } catch (Exception ex) {
            //TODO Ignore Exception
        }

        return state;
    }

    @Override
    public List<State> loadCurrentUserStates() {
        return em.createNamedQuery("State.findByUserId", State.class).setParameter("userId", current.getCurrentUserId()).getResultList();
    }

    @Override
    public void delete(State userState) {
        long currentUserId = current.getCurrentUserId();
        em.createQuery("delete from SYS_USER_STATES userState where userState.userId = :userId and userState.code = :code")
                .setParameter("userId", currentUserId)
                .setParameter("code", userState.getCode())
                .executeUpdate();
    }
}
