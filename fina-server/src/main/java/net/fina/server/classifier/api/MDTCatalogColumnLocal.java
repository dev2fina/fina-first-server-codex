package net.fina.server.classifier.api;

import net.fina.server.classifier.entity.MDTCatalogColumn;

import jakarta.ejb.Local;
import java.util.List;

@Local
public interface MDTCatalogColumnLocal {
    List<MDTCatalogColumn> load(long catalogId);

    MDTCatalogColumn create(MDTCatalogColumn column);

    List<MDTCatalogColumn> create(List<MDTCatalogColumn> columns) ;

    List<MDTCatalogColumn> save(List<MDTCatalogColumn> columns) ;

    MDTCatalogColumn save(MDTCatalogColumn column);

}
