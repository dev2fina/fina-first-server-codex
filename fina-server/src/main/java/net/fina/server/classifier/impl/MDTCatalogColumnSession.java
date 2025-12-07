package net.fina.server.classifier.impl;


import net.fina.server.classifier.api.MDTCatalogColumnLocal;
import net.fina.server.classifier.entity.MDTCatalogColumn;
import net.fina.server.interceptors.RecordingAuditor;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@Interceptors(RecordingAuditor.class)
public class MDTCatalogColumnSession implements MDTCatalogColumnLocal {
    @Inject
    private EntityManager em;

    @Override
    public List<MDTCatalogColumn> load(long catalogId) {
        return null;
    }

    @Override
    public MDTCatalogColumn create(MDTCatalogColumn column) {
        em.persist(column);
        return column;
    }

    @Override
    public List<MDTCatalogColumn> create(List<MDTCatalogColumn> columns) {
        columns.forEach(this::create);
        return columns;
    }

    @Override
    public List<MDTCatalogColumn> save(List<MDTCatalogColumn> columns) {
        return columns.stream()
                .map(this::save)
                .collect(Collectors.toList());
    }

    @Override
    public MDTCatalogColumn save(MDTCatalogColumn column) {
        if(column.getId() > 0) {
            return em.merge(column);
        } else {
            em.persist(column);
            return column;
        }
    }

}
