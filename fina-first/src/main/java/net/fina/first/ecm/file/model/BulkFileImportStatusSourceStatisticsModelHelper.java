package net.fina.first.ecm.file.model;

import net.fina.ecm.alfresco.api.core.model.representation.xml.fileimport.bulk.SourceStatistics;

public class BulkFileImportStatusSourceStatisticsModelHelper {

    public static BulkFileImportStatusSourceStatisticsMetaModel getMetaModel(SourceStatistics sourceStatistics) {
        BulkFileImportStatusSourceStatisticsMetaModel metaModel = new BulkFileImportStatusSourceStatisticsMetaModel();

        if (sourceStatistics != null) {
            metaModel.setLastFileOrFolderProcessed(sourceStatistics.getLastFileOrFolderProcessed());
            metaModel.setFilesScanned(sourceStatistics.getFilesScanned());
            metaModel.setFoldersScanned(sourceStatistics.getFoldersScanned());
            metaModel.setUnreadableEntries(sourceStatistics.getUnreadableEntries());
            metaModel.setContentFilesRead(sourceStatistics.getContentFilesRead());
            metaModel.setContentBytesRead(sourceStatistics.getContentBytesRead());
            metaModel.setMetadataFilesRead(sourceStatistics.getMetadataFilesRead());
            metaModel.setMetadataBytesRead(sourceStatistics.getMetadataBytesRead());
            metaModel.setContentVersionFilesRead(sourceStatistics.getContentVersionFilesRead());
            metaModel.setContentVersionBytesRead(sourceStatistics.getContentVersionBytesRead());
            metaModel.setMetadataVersionFilesRead(sourceStatistics.getMetadataVersionFilesRead());
            metaModel.setMetadataVersionBytesRead(sourceStatistics.getMetadataVersionBytesRead());
        }

        return metaModel;
    }

}
