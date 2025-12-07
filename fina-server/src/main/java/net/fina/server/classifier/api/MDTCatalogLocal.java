package net.fina.server.classifier.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.classifier.entity.MDTCatalog;
import net.fina.server.classifier.model.MDTCatalogDeleteResultModel;

import jakarta.ejb.Local;
import java.util.List;

@Local
public interface MDTCatalogLocal {

    MDTCatalog findById(long id);

    MDTCatalog create(MDTCatalog mdtCatalog) throws FinATypeException;

    MDTCatalogDeleteResultModel delete(long id);

    List<MDTCatalog> load(int offset, int limit, String filterValue);

    long count(String filterValue);

    MDTCatalog update(MDTCatalog catalog);

    MDTCatalog findByCode(String code);

    MDTCatalog getCatalogWithAttachmentById(long id);
}
