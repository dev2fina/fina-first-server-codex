package net.fina.first.repository.registry;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import net.fina.first.model.registry.RegistryDocument;

public interface RegistryDocumentRepository extends JpaRepository<RegistryDocument, Long> {
    List<RegistryDocument> findByFinancialInstitutionId(Long fiId);
}
