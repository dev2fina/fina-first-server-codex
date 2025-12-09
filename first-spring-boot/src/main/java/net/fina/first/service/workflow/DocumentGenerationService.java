package net.fina.first.service.workflow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fina.first.model.Document;
import net.fina.first.model.FiRegistry;
import net.fina.first.model.FiRegistryAction;
import net.fina.first.model.enums.DocumentType;
import net.fina.first.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for generating registration documents.
 * Creates official documents upon successful registration approval.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentGenerationService {

    private final DocumentRepository documentRepository;

    /**
     * Generate all required registration documents.
     */
    public List<Document> generateRegistrationDocuments(FiRegistry registry, FiRegistryAction action) {
        log.info("Generating registration documents for: {}", registry.getCode());

        List<Document> documents = new ArrayList<>();

        // 1. Registration Certificate
        documents.add(generateRegistrationCertificate(registry, action));

        // 2. Registration Decree
        documents.add(generateRegistrationDecree(registry, action));

        // 3. Confirmation Letter
        documents.add(generateConfirmationLetter(registry, action));

        // Save all documents
        List<Document> saved = documentRepository.saveAll(documents);

        log.info("Generated {} documents for registration: {}", saved.size(), registry.getCode());
        return saved;
    }

    /**
     * Generate Registration Certificate.
     */
    private Document generateRegistrationCertificate(FiRegistry registry, FiRegistryAction action) {
        String documentNumber = generateDocumentNumber("CERT", registry);

        Document document = Document.builder()
                .fiRegistry(registry)
                .action(action)
                .documentType(DocumentType.REGISTRATION_CERTIFICATE)
                .name("Registration Certificate - " + registry.getFirmName())
                .number(documentNumber)
                .documentDate(LocalDate.now())
                .description("Official registration certificate for " + registry.getFirmName())
                .fileUuid(UUID.randomUUID().toString())
                .fileName("certificate_" + registry.getCode() + ".pdf")
                .mimeType("application/pdf")
                .build();

        // In a real implementation, this would generate an actual PDF document
        // using a template engine like Apache POI, iText, or JasperReports

        return document;
    }

    /**
     * Generate Registration Decree.
     */
    private Document generateRegistrationDecree(FiRegistry registry, FiRegistryAction action) {
        String documentNumber = generateDocumentNumber("DECREE", registry);

        Document document = Document.builder()
                .fiRegistry(registry)
                .action(action)
                .documentType(DocumentType.DECREE)
                .name("Registration Decree - " + registry.getFirmName())
                .number(documentNumber)
                .documentDate(LocalDate.now())
                .description("Official registration decree for " + registry.getFirmName())
                .fileUuid(UUID.randomUUID().toString())
                .fileName("decree_" + registry.getCode() + ".pdf")
                .mimeType("application/pdf")
                .build();

        return document;
    }

    /**
     * Generate Confirmation Letter.
     */
    private Document generateConfirmationLetter(FiRegistry registry, FiRegistryAction action) {
        String documentNumber = generateDocumentNumber("LETTER", registry);

        Document document = Document.builder()
                .fiRegistry(registry)
                .action(action)
                .documentType(DocumentType.CONFIRMATION_LETTER)
                .name("Confirmation Letter - " + registry.getFirmName())
                .number(documentNumber)
                .documentDate(LocalDate.now())
                .description("Official confirmation letter for " + registry.getFirmName())
                .fileUuid(UUID.randomUUID().toString())
                .fileName("confirmation_" + registry.getCode() + ".pdf")
                .mimeType("application/pdf")
                .build();

        return document;
    }

    /**
     * Generate a unique document number.
     */
    private String generateDocumentNumber(String prefix, FiRegistry registry) {
        String year = String.valueOf(LocalDate.now().getYear());
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uniquePart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        return String.format("%s-%s-%s-%s", prefix, year, registry.getCode(), uniquePart);
    }

    /**
     * Generate Gap Letter (for correction requests).
     */
    public Document generateGapLetter(FiRegistry registry, FiRegistryAction action, String reason) {
        String documentNumber = generateDocumentNumber("GAP", registry);

        Document document = Document.builder()
                .fiRegistry(registry)
                .action(action)
                .documentType(DocumentType.GAP_LETTER)
                .name("Gap Correction Letter - " + registry.getFirmName())
                .number(documentNumber)
                .documentDate(LocalDate.now())
                .description("Gap correction request: " + reason)
                .fileUuid(UUID.randomUUID().toString())
                .fileName("gap_letter_" + registry.getCode() + ".pdf")
                .mimeType("application/pdf")
                .build();

        return documentRepository.save(document);
    }

    /**
     * Generate Refusal Letter (for rejected registrations).
     */
    public Document generateRefusalLetter(FiRegistry registry, FiRegistryAction action, String reason) {
        String documentNumber = generateDocumentNumber("REFUSAL", registry);

        Document document = Document.builder()
                .fiRegistry(registry)
                .action(action)
                .documentType(DocumentType.REFUSAL_LETTER)
                .name("Refusal Letter - " + registry.getFirmName())
                .number(documentNumber)
                .documentDate(LocalDate.now())
                .description("Registration refusal: " + reason)
                .fileUuid(UUID.randomUUID().toString())
                .fileName("refusal_" + registry.getCode() + ".pdf")
                .mimeType("application/pdf")
                .build();

        return documentRepository.save(document);
    }
}
