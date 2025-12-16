package net.fina.first.service.registry;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import net.fina.first.model.registry.FinancialInstitution;
import net.fina.first.model.registry.RegistryDocument;
import net.fina.first.repository.registry.RegistryDocumentRepository;

@Service
@RequiredArgsConstructor
public class RegistryDocumentService {

    private final RegistryDocumentRepository registryDocumentRepository;

    public void generateApprovalDocuments(FinancialInstitution fi) {
        RegistryDocument confirmation = new RegistryDocument();
        confirmation.setFinancialInstitution(fi);
        confirmation.setDocumentType("CONFIRMATION_LETTER");
        confirmation.setFileName(fi.getRegistryCode() + "_confirmation.pdf");
        confirmation.setGeneratedAt(LocalDate.now());
        registryDocumentRepository.save(confirmation);
    }

    public List<RegistryDocument> documentsForFi(Long fiId) {
        return registryDocumentRepository.findByFinancialInstitutionId(fiId);
    }
}
