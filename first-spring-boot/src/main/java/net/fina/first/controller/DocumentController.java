package net.fina.first.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.fina.first.dto.common.ApiResponse;
import net.fina.first.dto.ecm.NodeMetaModel;
import net.fina.first.dto.ecm.PaginatedListWrapper;
import net.fina.first.dto.ecm.VersionMetaModel;
import net.fina.first.model.Document;
import net.fina.first.model.enums.DocumentType;
import net.fina.first.service.ecm.DocumentService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/fi-registries/{fiRegistryId}/documents")
@RequiredArgsConstructor
@Tag(name = "Documents", description = "Document management endpoints with Alfresco ECM integration")
@ConditionalOnProperty(name = "ecm.alfresco.enabled", havingValue = "true", matchIfMissing = true)
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping
    @Operation(summary = "Get all documents for an FI registry")
    @PreAuthorize("hasAuthority('DOCUMENT_READ')")
    public ResponseEntity<List<Document>> findByFiRegistry(@PathVariable Long fiRegistryId) {
        List<Document> documents = documentService.findByFiRegistry(fiRegistryId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get document by ID")
    @PreAuthorize("hasAuthority('DOCUMENT_READ')")
    public ResponseEntity<Document> findById(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id) {
        Document document = documentService.findById(id);
        return ResponseEntity.ok(document);
    }

    @GetMapping("/type/{documentType}")
    @Operation(summary = "Get documents by type")
    @PreAuthorize("hasAuthority('DOCUMENT_READ')")
    public ResponseEntity<List<Document>> findByType(
            @PathVariable Long fiRegistryId,
            @PathVariable DocumentType documentType) {
        List<Document> documents = documentService.findByFiRegistryAndType(fiRegistryId, documentType);
        return ResponseEntity.ok(documents);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload new document")
    @PreAuthorize("hasAuthority('DOCUMENT_CREATE')")
    public ResponseEntity<Document> upload(
            @PathVariable Long fiRegistryId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") DocumentType documentType,
            @RequestParam(value = "actionId", required = false) Long actionId,
            @RequestParam(value = "documentNumber", required = false) String documentNumber,
            @RequestParam(value = "documentDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate documentDate,
            @RequestParam(value = "displayName", required = false) String displayName) {

        Document document = documentService.uploadDocument(
                fiRegistryId, actionId, file, documentType,
                documentNumber, documentDate, displayName);
        return ResponseEntity.ok(document);
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download document content")
    @PreAuthorize("hasAuthority('DOCUMENT_READ')")
    public ResponseEntity<byte[]> download(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id) {
        Document document = documentService.findById(id);
        byte[] content = documentService.downloadDocument(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + document.getOriginalFileName() + "\"")
                .contentType(MediaType.parseMediaType(
                        document.getContentType() != null ?
                                document.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .contentLength(content.length)
                .body(content);
    }

    @PutMapping("/{id}/metadata")
    @Operation(summary = "Update document metadata")
    @PreAuthorize("hasAuthority('DOCUMENT_UPDATE')")
    public ResponseEntity<Document> updateMetadata(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id,
            @RequestParam(required = false) DocumentType documentType,
            @RequestParam(required = false) String documentNumber,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate documentDate,
            @RequestParam(required = false) String displayName) {

        Document document = documentService.updateDocumentMetadata(
                id, documentType, documentNumber, documentDate, displayName);
        return ResponseEntity.ok(document);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete document (soft delete)")
    @PreAuthorize("hasAuthority('DOCUMENT_DELETE')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok(ApiResponse.success("Document deleted successfully"));
    }

    @GetMapping("/{id}/versions")
    @Operation(summary = "Get document version history")
    @PreAuthorize("hasAuthority('DOCUMENT_READ')")
    public ResponseEntity<List<VersionMetaModel>> getVersions(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id) {
        List<VersionMetaModel> versions = documentService.getDocumentVersions(id);
        return ResponseEntity.ok(versions);
    }

    @PostMapping("/{id}/versions/{versionId}/revert")
    @Operation(summary = "Revert to a specific version")
    @PreAuthorize("hasAuthority('DOCUMENT_UPDATE')")
    public ResponseEntity<Document> revertToVersion(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id,
            @PathVariable String versionId,
            @RequestParam(required = false) String comment) {
        Document document = documentService.revertToVersion(id, versionId, comment);
        return ResponseEntity.ok(document);
    }

    @PostMapping("/{id}/copy/{targetFiRegistryId}")
    @Operation(summary = "Copy document to another FI registry")
    @PreAuthorize("hasAuthority('DOCUMENT_CREATE')")
    public ResponseEntity<Document> copy(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id,
            @PathVariable Long targetFiRegistryId) {
        Document document = documentService.copyDocument(id, targetFiRegistryId);
        return ResponseEntity.ok(document);
    }

    @GetMapping("/{id}/ecm-details")
    @Operation(summary = "Get ECM node details")
    @PreAuthorize("hasAuthority('DOCUMENT_READ')")
    public ResponseEntity<NodeMetaModel> getEcmDetails(
            @PathVariable Long fiRegistryId,
            @PathVariable Long id) {
        NodeMetaModel nodeDetails = documentService.getEcmNodeDetails(id);
        return ResponseEntity.ok(nodeDetails);
    }

    @GetMapping("/search")
    @Operation(summary = "Search documents")
    @PreAuthorize("hasAuthority('DOCUMENT_READ')")
    public ResponseEntity<PaginatedListWrapper<NodeMetaModel>> search(
            @PathVariable Long fiRegistryId,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "20") int limit) {
        PaginatedListWrapper<NodeMetaModel> results = documentService.searchDocuments(
                fiRegistryId, query, start, limit);
        return ResponseEntity.ok(results);
    }
}
