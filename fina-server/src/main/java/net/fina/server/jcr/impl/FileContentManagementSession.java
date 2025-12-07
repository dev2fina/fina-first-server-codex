package net.fina.server.jcr.impl;

import jakarta.annotation.Resource;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.shared.jcr.RepositoryNodeMetaModel;
import net.fina.server.dcs.uploadfile.api.UploadFileStreamable;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.jcr.model.ContentStorageProvider;
import net.fina.server.jcr.proxy.FileRepositoryProxySession;
import net.fina.server.jcr.util.FileRepositoryUtil;
import net.fina.server.reports.entity.StoredReport;
import net.fina.server.returns.entity.ImportedReturn;
import net.fina.server.security.api.PropertyLocal;
import org.hibernate.Session;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;

@Stateless
@Interceptors(RecordingAuditor.class)
@SecurityDomain("FinASecurityDomain")
public class FileContentManagementSession {

    private final Logger log = Logger.getLogger(getClass().getName());

    @Inject
    private EntityManager em;
    @Resource
    private SessionContext sessionContext;
    @Inject
    private FileRepositoryProxySession fileRepositoryProxySession;
    @Inject
    private PropertyLocal propertyLocal;

    @Resource(mappedName = "java:jboss/datasources/FinADS")
    private DataSource dataSource;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveStoredReport(StoredReport report) throws FinATypeException {
        if (isRepositoryProviderActive()) {
            try {
                StringBuilder nameBuilder = new StringBuilder();
                nameBuilder.append(report.getReportPk().getReportId())
                        .append("-")
                        .append(report.getReportPk().getLangId())
                        .append("-")
                        .append(report.getReportPk().getHashCode());

                RepositoryNodeMetaModel fileNode = new RepositoryNodeMetaModel(FileRepositoryUtil.getStoredReportRootFolderPath(), nameBuilder.toString());
                fileNode.setContent(report.getReportResult());
                fileNode = fileRepositoryProxySession.saveFile(fileNode);
                report.setRepositoryFileId(fileNode.getId());
                report.setRepositoryFileVersionId(fileNode.getVersionId());
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
                sessionContext.setRollbackOnly();
                throw new FinATypeException(t.getMessage());
            }
            report.setReportResult(null);
        }
        em.persist(report);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public StoredReport loadGeneratedReport(StoredReport report) {
        if (isRepositoryProviderActive() && report.getRepositoryFileId() != null) {
            try {
                RepositoryNodeMetaModel fileNode = fileRepositoryProxySession.getFileContent(report.getRepositoryFileId(), report.getRepositoryFileVersionId());
                report.setReportResult(fileNode.getContent());
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        }
        return report;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveUploadFile(UploadFile uploadFile) throws FinATypeException {
        saveUploadFileStream(uploadFile, new ByteArrayInputStream(uploadFile.getUploadedFile()), String.valueOf(uploadFile.getUploadedFile().length));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public void saveUploadFileStream(UploadFile uploadFile, InputStream inputStream, String contentLength) throws FinATypeException {
        if (isRepositoryProviderActive()) {
            try {
                RepositoryNodeMetaModel fileNode = new RepositoryNodeMetaModel(FileRepositoryUtil.getUploadedFilesRootFolderPath(), uploadFile.getFileName());
                fileNode.setContent(uploadFile.getUploadedFile());
                fileNode = fileRepositoryProxySession.saveFile(fileNode, inputStream);
                uploadFile.setRepositoryFileId(fileNode.getId());
                uploadFile.setRepositoryFileVersionId(fileNode.getVersionId());
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
                sessionContext.setRollbackOnly();
                throw new FinATypeException(t.getMessage());
            }
            uploadFile.setUploadedFile(null);
            em.merge(uploadFile);

        } else {
            em.merge(uploadFile);
            em.flush();

            Session session = em.unwrap(Session.class);

            session.doWork(conn -> {
                String sql = "UPDATE SYS_UPLOADEDFILE SET UPLOADEDFILE = ? WHERE id = ?";
                try (Connection connection = conn; PreparedStatement ps = connection.prepareStatement(sql); InputStream stream = inputStream;) {
                    ps.setBinaryStream(1, stream);
                    ps.setLong(2, uploadFile.getId());
                    ps.executeUpdate();
                } catch (Throwable t) {
                    log.error(t.getMessage(), t);
                    sessionContext.setRollbackOnly();
                    throw new RuntimeException(t);
                }
            });

        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void loadUploadFileStreamInto(UploadFile uploadFile, UploadFileStreamable streamable) {
        if (isRepositoryProviderActive() && uploadFile.getRepositoryFileId() != null) {
            try (InputStream stream = fileRepositoryProxySession.getFileContentStream(uploadFile.getRepositoryFileId(), uploadFile.getRepositoryFileVersionId())) {
                streamable.readFileStream(stream);
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        } else {
            String sql = "SELECT UPLOADEDFILE FROM SYS_UPLOADEDFILE WHERE id = ?";
            try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setLong(1, uploadFile.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        try (InputStream stream = rs.getBinaryStream("UPLOADEDFILE")) {
                            streamable.readFileStream(stream);
                        }
                    }
                }
            } catch (DcsTypeException dcsTypeException) {
                throw dcsTypeException;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isRepositoryProviderActive() {
        String repositoryProvider = propertyLocal.getSystemProperty(PropertyKeys.CONTENT_STORAGE_PROVIDER);
        return repositoryProvider != null && ContentStorageProvider.REPOSITORY.equals(ContentStorageProvider.valueOf(repositoryProvider));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveImportedReturn(ImportedReturn importedReturn) throws FinATypeException {
        if (isRepositoryProviderActive()) {
            try {
                StringBuilder nameBuilder = new StringBuilder();
                nameBuilder.append(importedReturn.getReturnCode())
                        .append("-")
                        .append(importedReturn.getBankCode())
                        .append("-")
                        .append(importedReturn.getVersionCode());
                RepositoryNodeMetaModel fileNode = new RepositoryNodeMetaModel(FileRepositoryUtil.getImportedReturnRootFolderPath(), nameBuilder.toString());
                fileNode.setContent(importedReturn.getContent());
                fileNode = fileRepositoryProxySession.saveFile(fileNode);
                importedReturn.setRepositoryFileId(fileNode.getId());
                importedReturn.setRepositoryFileVersionId(fileNode.getVersionId());
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
                sessionContext.setRollbackOnly();
                throw new FinATypeException(t.getMessage());
            }
            importedReturn.setContent(null);
        }
        em.persist(importedReturn);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public byte[] loadImportedReturnContent(ImportedReturn importedReturn) {
        if (isRepositoryProviderActive() && importedReturn.getRepositoryFileId() != null) {
            try {
                RepositoryNodeMetaModel fileNode = fileRepositoryProxySession.getFileContent(importedReturn.getRepositoryFileId(), importedReturn.getRepositoryFileVersionId());
                return fileNode.getName() != null ? fileNode.getContent() : importedReturn.getContent();
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
                return importedReturn.getContent();
            }
        }
        return importedReturn.getContent();
    }
}
