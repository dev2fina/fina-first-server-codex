package net.fina.first.ecm.file.model;

import net.fina.ecm.alfresco.api.core.model.representation.xml.fileimport.bulk.TargetStatistics;

public class BulkFileImportStatusTargetStatisticsModelHelper {

    public static BulkFileImportStatusTargetStatisticsMetaModel getMetaModel(TargetStatistics targetStatistics) {
        BulkFileImportStatusTargetStatisticsMetaModel metaModel = new BulkFileImportStatusTargetStatisticsMetaModel();

        if (targetStatistics != null) {
            metaModel.setSpaceNodesCreated(targetStatistics.getSpaceNodesCreated());
            metaModel.setSpaceNodesReplaced(targetStatistics.getSpaceNodesReplaced());
            metaModel.setSpaceNodesSkipped(targetStatistics.getSpaceNodesSkipped());
            metaModel.setSpacePropertiesWritten(targetStatistics.getSpacePropertiesWritten());
            metaModel.setContentNodesCreated(targetStatistics.getContentNodesCreated());
            metaModel.setContentNodesReplaced(targetStatistics.getContentNodesReplaced());
            metaModel.setContentNodesSkipped(targetStatistics.getContentNodesSkipped());
            metaModel.setContentBytesWritten(targetStatistics.getContentBytesWritten());
            metaModel.setContentPropertiesWritten(targetStatistics.getContentPropertiesWritten());
            metaModel.setContentVersionsCreated(targetStatistics.getContentVersionsCreated());
            metaModel.setContentVersionsBytesWritten(targetStatistics.getContentVersionsBytesWritten());
            metaModel.setContentVersionsPropertiesWritten(targetStatistics.getContentVersionsPropertiesWritten());
        }

        return metaModel;
    }

}
