package net.fina.first.ecm.node.api;

import net.fina.ecm.alfresco.api.search.body.QueryBody;
import net.fina.first.ecm.node.model.ExportTemplate;

public interface NodeExporter {

    byte[] getExportNodeHierarchyContent(ExportTemplate exportTemplate, String rootFolderId, String childNodeType, QueryBody filterQueryBody) throws Throwable;

}
