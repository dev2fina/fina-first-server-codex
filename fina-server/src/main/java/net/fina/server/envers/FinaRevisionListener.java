package net.fina.server.envers;

import net.fina.security.api.AuthorizationLocal;
import org.hibernate.envers.RevisionListener;

import jakarta.inject.Inject;

public class FinaRevisionListener implements RevisionListener {
    @Inject
    private AuthorizationLocal authorizationLocal;
    @Override
    public void newRevision(Object revisionEntity) {
        RevInfo exampleRevEntity = (RevInfo) revisionEntity;


        exampleRevEntity.setUsername(authorizationLocal.getCallerPrincipal().getName());
    }
}
