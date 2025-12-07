package net.fina.first.service.ecm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fina.first.config.EcmConfig;
import net.fina.first.dto.ecm.NodeMetaModel;
import net.fina.first.dto.ecm.PaginatedListWrapper;
import net.fina.first.dto.ecm.VersionMetaModel;
import net.fina.first.exception.BusinessException;
import net.fina.first.exception.FileProcessingException;
import net.fina.first.exception.ResourceNotFoundException;
import net.fina.first.model.Document;
import net.fina.first.model.FiRegistry;
import net.fina.first.model.enums.DocumentType;
import net.fina.first.repository.DocumentRepository;
import net.fina.first.repository.FiRegistryRepository;
import net.fina.first.service.AuditLogService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ecm.alfresco.enabled", havingValue = "true", matchIfMissing = true)
public class DocumentService {

    private final AlfrescoClientService alfrescoClient;
    private final DocumentRepository documentRepository;
    private final FiRegistryRepository fiRegistryRepository;
    private final EcmConfig ecmConfig;
    private final AuditLogService auditLogService;

    private static final String FI_DOCUMENT_TYPE = "fina:fiDocument";
    private static final String PROP_DOCUMENT_TYPE = "fina:fiDocumentType";
    private static final String PROP_DOCUMENT_NUMBER = "fina:fiDocumentNumber";
    private static final String PROP_DOCUMENT_DATE = "fina:fiDocumentDate";
    private static final String PROP_DOCUMENT_ACTION_ID = "fina:fiDocumentActionId";
    private static final String PROP_DISPLAY_NAME = "fina:fiDocumentDisplayName";
    private static final String PROP_IS_LAST_VERSION = "fina:fiDocumentIsLastVersion";

    @Transactional
    public Document uploadDocument(Long fiRegistryId, Long actionId, MultipartFile file,
                                   DocumentType documentType, String documentNumber,
                                   LocalDate documentDate, String displayName) {
        log.info("Uploading document for FI Registry: {}, type: {}", fiRegistryId, documentType);

        FiRegistry fiRegistry = fiRegistryRepository.findById(fiRegistryId)
                .orElseThrow(() -> new ResourceNotFoundException("FiRegistry", "id", fiRegistryId));

        String folderId = getOrCreateDocumentsFolder(fiRegistry);

        Map<String, Object> properties = new TreeMap<>();
        properties.put(PROP_DOCUMENT_TYPE, documentType.name());
        if (documentNumber != null) {
            properties.put(PROP_DOCUMENT_NUMBER, documentNumber);
        }
        if (documentDate != null) {
            properties.put(PROP_DOCUMENT_DATE, documentDate.toString());
        }
        if (actionId != null) {
            properties.put(PROP_DOCUMENT_ACTION_ID, actionId.toString());
        }
        properties.put(PROP_DISPLAY_NAME, displayName != null ? displayName : file.getOriginalFilename());
        properties.put(PROP_IS_LAST_VERSION, true);

        String fileName = generateUniqueFileName(file.getOriginalFilename());
        NodeMetaModel uploadedNode = alfrescoClient.uploadFile(folderId, fileName, file);
        alfrescoClient.updateNode(uploadedNode.getId(), properties);

        Document document = new Document();
        document.setFiRegistry(fiRegistry);
        document.setEcmNodeId(uploadedNode.getId());
        document.setFileName(fileName);
        document.setOriginalFileName(file.getOriginalFilename());
        document.setContentType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setDocumentType(documentType);
        document.setDocumentNumber(documentNumber);
        document.setDocumentDate(documentDate);
        document.setDisplayName(displayName != null ? displayName : file.getOriginalFilename());

        Document savedDocument = documentRepository.save(document);

        auditLogService.log("DOCUMENT_UPLOAD", "Document", savedDocument.getId(),
                null, "File: " + fileName + ", Type: " + documentType);

        return savedDocument;
    }

    @Transactional(readOnly = true)
    public byte[] downloadDocument(Long documentId) {
        log.debug("Downloading document: {}", documentId);

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", documentId));

        return alfrescoClient.getNodeContent(document.getEcmNodeId());
    }

    @Transactional(readOnly = true)
    public byte[] downloadDocumentByNodeId(String nodeId) {
        log.debug("Downloading document by node ID: {}", nodeId);
        return alfrescoClient.getNodeContent(nodeId);
    }

    @Transactional
    public void deleteDocument(Long documentId) {
        log.info("Deleting document: {}", documentId);

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", documentId));

        alfrescoClient.deleteNode(document.getEcmNodeId(), false);

        document.setDeleted(true);
        document.setDeletedAt(LocalDateTime.now());
        documentRepository.save(document);

        auditLogService.log("DOCUMENT_DELETE", "Document", documentId,
                "File: " + document.getFileName(), null);
    }

    @Transactional
    public Document updateDocumentMetadata(Long documentId, DocumentType documentType,
                                           String documentNumber, LocalDate documentDate,
                                           String displayName) {
        log.info("Updating document metadata: {}", documentId);

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", documentId));

        String oldValue = document.toString();

        Map<String, Object> properties = new TreeMap<>();
        if (documentType != null) {
            properties.put(PROP_DOCUMENT_TYPE, documentType.name());
            document.setDocumentType(documentType);
        }
        if (documentNumber != null) {
            properties.put(PROP_DOCUMENT_NUMBER, documentNumber);
            document.setDocumentNumber(documentNumber);
        }
        if (documentDate != null) {
            properties.put(PROP_DOCUMENT_DATE, documentDate.toString());
            document.setDocumentDate(documentDate);
        }
        if (displayName != null) {
            properties.put(PROP_DISPLAY_NAME, displayName);
            document.setDisplayName(displayName);
        }

        if (!properties.isEmpty()) {
            alfrescoClient.updateNode(document.getEcmNodeId(), properties);
        }

        Document savedDocument = documentRepository.save(document);

        auditLogService.log("DOCUMENT_UPDATE", "Document", documentId,
                oldValue, savedDocument.toString());

        return savedDocument;
    }

    @Transactional(readOnly = true)
    public List<Document> findByFiRegistry(Long fiRegistryId) {
        return documentRepository.findByFiRegistryIdAndDeletedFalse(fiRegistryId);
    }

    @Transactional(readOnly = true)
    public List<Document> findByFiRegistryAndType(Long fiRegistryId, DocumentType documentType) {
        return documentRepository.findByFiRegistryIdAndDocumentTypeAndDeletedFalse(
                fiRegistryId, documentType);
    }

    @Transactional(readOnly = true)
    public List<Document> findByActionId(Long actionId) {
        return documentRepository.findByActionIdAndDeletedFalse(actionId);
    }

    @Transactional(readOnly = true)
    public Document findById(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", documentId));
    }

    @Transactional(readOnly = true)
    public NodeMetaModel getEcmNodeDetails(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", documentId));

        return alfrescoClient.getNode(document.getEcmNodeId());
    }

    @Transactional(readOnly = true)
    public List<VersionMetaModel> getDocumentVersions(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", documentId));

        return alfrescoClient.getNodeVersions(document.getEcmNodeId());
    }

    @Transactional
    public Document revertToVersion(Long documentId, String versionId, String comment) {
        log.info("Reverting document {} to version {}", documentId, versionId);

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", documentId));

        alfrescoClient.revertVersion(document.getEcmNodeId(), versionId, comment);

        auditLogService.log("DOCUMENT_REVERT", "Document", documentId,
                null, "Reverted to version: " + versionId);

        return document;
    }

    @Transactional
    public Document copyDocument(Long documentId, Long targetFiRegistryId) {
        log.info("Copying document {} to FI Registry {}", documentId, targetFiRegistryId);

        Document sourceDocument = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", documentId));

        FiRegistry targetFiRegistry = fiRegistryRepository.findById(targetFiRegistryId)
                .orElseThrow(() -> new ResourceNotFoundException("FiRegistry", "id", targetFiRegistryId));

        String targetFolderId = getOrCreateDocumentsFolder(targetFiRegistry);
        String newFileName = generateUniqueFileName(sourceDocument.getOriginalFileName());

        NodeMetaModel copiedNode = alfrescoClient.copyNode(
                sourceDocument.getEcmNodeId(), targetFolderId, newFileName);

        Document newDocument = new Document();
        newDocument.setFiRegistry(targetFiRegistry);
        newDocument.setEcmNodeId(copiedNode.getId());
        newDocument.setFileName(newFileName);
        newDocument.setOriginalFileName(sourceDocument.getOriginalFileName());
        newDocument.setContentType(sourceDocument.getContentType());
        newDocument.setFileSize(sourceDocument.getFileSize());
        newDocument.setDocumentType(sourceDocument.getDocumentType());
        newDocument.setDocumentNumber(sourceDocument.getDocumentNumber());
        newDocument.setDocumentDate(sourceDocument.getDocumentDate());
        newDocument.setDisplayName(sourceDocument.getDisplayName());

        return documentRepository.save(newDocument);
    }

    public PaginatedListWrapper<NodeMetaModel> searchDocuments(Long fiRegistryId, String query,
                                                               int start, int limit) {
        FiRegistry fiRegistry = fiRegistryRepository.findById(fiRegistryId)
                .orElseThrow(() -> new ResourceNotFoundException("FiRegistry", "id", fiRegistryId));

        String folderId = fiRegistry.getEcmFolderId();
        if (folderId == null) {
            return PaginatedListWrapper.of(Collections.emptyList(), 0, limit, 0);
        }

        String aftsQuery = String.format("ANCESTOR:'workspace://SpacesStore/%s' AND TYPE:'%s'",
                folderId, FI_DOCUMENT_TYPE);

        if (query != null && !query.isBlank()) {
            aftsQuery += " AND (cm:name:*" + query + "* OR " + PROP_DISPLAY_NAME + ":*" + query + "*)";
        }

        return alfrescoClient.search(aftsQuery, start, limit, "cm:modified", "DESC");
    }

    public PaginatedListWrapper<NodeMetaModel> getFiRegistryDocuments(String fiRegistryFolderId,
                                                                       int start, int limit) {
        return alfrescoClient.getNodeChildren(fiRegistryFolderId, start, limit,
                "modifiedAt DESC", "(isFile=true)");
    }

    private String getOrCreateDocumentsFolder(FiRegistry fiRegistry) {
        if (fiRegistry.getEcmFolderId() != null) {
            return fiRegistry.getEcmFolderId();
        }

        String rootPath = ecmConfig.getFiRegistryRootPath();
        String folderName = "FI_" + fiRegistry.getId() + "_" + fiRegistry.getCode();

        try {
            NodeMetaModel rootFolder = alfrescoClient.getNodeByPath(rootPath);
            NodeMetaModel fiFolder = alfrescoClient.createFolder(rootFolder.getId(), folderName);

            fiRegistry.setEcmFolderId(fiFolder.getId());
            fiRegistryRepository.save(fiRegistry);

            return fiFolder.getId();
        } catch (Exception e) {
            throw new FileProcessingException("Failed to create ECM folder for FI Registry: " +
                    fiRegistry.getId(), e);
        }
    }

    private String generateUniqueFileName(String originalFileName) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        int dotIndex = originalFileName.lastIndexOf('.');

        if (dotIndex > 0) {
            String name = originalFileName.substring(0, dotIndex);
            String extension = originalFileName.substring(dotIndex);
            return name + "_" + timestamp + extension;
        }
        return originalFileName + "_" + timestamp;
    }
}
