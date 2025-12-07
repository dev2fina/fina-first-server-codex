package net.fina.server.store.impl;


import javax.jcr.Repository;

/**
 * Author: Oto Iantbelidze
 * Created: 24.10.25
 */
public interface JcrRepositoryProvider {
    Repository get(FinaRepository repo);
}
