package net.fina.first.ecm.file.model;

import net.fina.ecm.alfresco.api.core.model.representation.xml.fileimport.bulk.BulkFilesystemImportStatus;

public class BulkFileImportStatusModelHelper {

    public static BulkFileImportStatusMetaModel getMetaModel(BulkFilesystemImportStatus status) {
        BulkFileImportStatusMetaModel metaModel = new BulkFileImportStatusMetaModel();

        if (status != null) {
            metaModel.setCurrentStatus(status.getCurrentStatus());
            metaModel.setResultOfLastExecution(status.getResultOfLastExecution());
            metaModel.setSourceDirectory(status.getSourceDirectory());
            metaModel.setTargetSpace(status.getTargetSpace());
            metaModel.setStartDate(status.getStartDate());
            metaModel.setEndDate(status.getEndDate());
            metaModel.setDurationInNS(status.getDurationInNS());
            metaModel.setCompletedBatches(status.getCompletedBatches());

            BulkFileImportStatusSourceStatisticsMetaModel sourceStatisticsMetaModel = BulkFileImportStatusSourceStatisticsModelHelper.getMetaModel(status.getSourceStatistics());
            metaModel.setSourceStatistics(sourceStatisticsMetaModel);

            BulkFileImportStatusTargetStatisticsMetaModel targetStatisticsMetaModel = BulkFileImportStatusTargetStatisticsModelHelper.getMetaModel(status.getTargetStatistics());
            metaModel.setTargetStatistics(targetStatisticsMetaModel);
        }

        return metaModel;
    }

}
