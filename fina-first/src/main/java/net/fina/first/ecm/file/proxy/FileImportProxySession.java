package net.fina.first.ecm.file.proxy;

import net.fina.common.server.util.CommonUtil;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.ecm.alfresco.api.core.model.body.BulkFileImportBodyInitiate;
import net.fina.ecm.alfresco.api.core.model.body.BulkFileImportExistingFileMode;
import net.fina.ecm.alfresco.api.core.model.representation.xml.fileimport.bulk.BulkFilesystemImportStatus;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.file.exception.FileImportIsNotEnabledException;
import net.fina.first.ecm.file.model.BulkFileImportStatusMetaModel;
import net.fina.first.ecm.file.model.BulkFileImportStatusModelHelper;
import net.fina.first.interceptors.FirstRecordingAuditor;
import org.apache.http.HttpStatus;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.core.Response;

@Stateless
@Interceptors(FirstRecordingAuditor.class)
public class FileImportProxySession {

    @Inject
    private EcmClientProxySession ecmClientProxySession;

    public BulkFileImportStatusMetaModel getBulkFileImportStatus() throws FileImportIsNotEnabledException {

        checkEcmFileImportEnable();

        BulkFilesystemImportStatus bulkFilesystemImportStatus = ecmClientProxySession.getAlfrescoClientByUser(ConfigurationUtil.get().get("ECM.adminUsername")).getFileImportAPI().getBulkImportStatus("xml");
        return BulkFileImportStatusModelHelper.getMetaModel(bulkFilesystemImportStatus);
    }

    public BulkFileImportStatusMetaModel initiateBulkFileImport() throws FileImportIsNotEnabledException {

        checkEcmFileImportEnable();

        BulkFileImportBodyInitiate bulkFileImportBodyInitiate = new BulkFileImportBodyInitiate();
        bulkFileImportBodyInitiate.setSourceDirectory(ConfigurationUtil.get().get("ECM.fileImport.batch.sourceDirectory"));
        bulkFileImportBodyInitiate.setTargetPath(ConfigurationUtil.get().get("ECM.fileImport.batch.targetPath"));
        bulkFileImportBodyInitiate.setExistingFileMode(BulkFileImportExistingFileMode.valueOf(ConfigurationUtil.get().get("ECM.fileImport.batch.existingFileMode").toUpperCase()));
        bulkFileImportBodyInitiate.setBatchSize(Integer.parseInt(ConfigurationUtil.get().get("ECM.fileImport.batch.batchSize")));
        bulkFileImportBodyInitiate.setNumThreads(Integer.parseInt(ConfigurationUtil.get().get("ECM.fileImport.batch.numThreads")));

        Response rs = ecmClientProxySession.getAlfrescoClientByUser(ConfigurationUtil.get().get("ECM.adminUsername")).getFileImportAPI().initiateBulkImport(bulkFileImportBodyInitiate);
        if (rs.getStatus() == HttpStatus.SC_MOVED_TEMPORARILY) {
            return getBulkFileInitiateFinishStatus();
        }

        return null;
    }

    private BulkFileImportStatusMetaModel getBulkFileInitiateFinishStatus() throws FileImportIsNotEnabledException {
        BulkFileImportStatusMetaModel status = getBulkFileImportStatus();
        if (status.getCurrentStatus().equalsIgnoreCase("In progress")) {
            return getBulkFileInitiateFinishStatus();
        }
        return status;
    }

    private void checkEcmFileImportEnable() throws FileImportIsNotEnabledException {
        if (!CommonUtil.isEcmEnable() || !CommonUtil.isEcmFileImportEnabled()) {
            throw new FileImportIsNotEnabledException("ECM File Import Is Not Enabled!");
        }
    }
}
