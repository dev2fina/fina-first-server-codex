package net.fina.server.util;

import net.fina.common.shared.jcr.JcrCustomConstants;
import org.apache.jackrabbit.JcrConstants;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.NodeTypeTemplate;
import javax.jcr.nodetype.PropertyDefinitionTemplate;
import java.text.ParseException;

public class JcrUtil {

    public static void registerFinaCustomNodeType(NodeTypeManager nodeTypeManager) throws Exception {
        NodeTypeTemplate ndt = nodeTypeManager.createNodeTypeTemplate();

        PropertyDefinitionTemplate createPropertyDefinitionTemplate = nodeTypeManager.createPropertyDefinitionTemplate();
        createPropertyDefinitionTemplate.setName(JcrCustomConstants.PROP_MARK_DELETED);
        createPropertyDefinitionTemplate.setRequiredType(PropertyType.BOOLEAN);
        createPropertyDefinitionTemplate.setMandatory(false);
        createPropertyDefinitionTemplate.setQueryOrderable(true);
        createPropertyDefinitionTemplate.setFullTextSearchable(true);
        ndt.getPropertyDefinitionTemplates().add(createPropertyDefinitionTemplate);

        PropertyDefinitionTemplate descriptionPropertyDefinitionTemplate = nodeTypeManager.createPropertyDefinitionTemplate();
        descriptionPropertyDefinitionTemplate.setName(JcrCustomConstants.PROP_DESCRIPTION);
        descriptionPropertyDefinitionTemplate.setRequiredType(PropertyType.STRING);
        descriptionPropertyDefinitionTemplate.setMultiple(false);
        descriptionPropertyDefinitionTemplate.setQueryOrderable(true);
        descriptionPropertyDefinitionTemplate.setFullTextSearchable(true);
        ndt.getPropertyDefinitionTemplates().add(descriptionPropertyDefinitionTemplate);

        PropertyDefinitionTemplate externalIdPropDefinition = nodeTypeManager.createPropertyDefinitionTemplate();
        externalIdPropDefinition.setName(JcrCustomConstants.PROP_EXTERNAL_ID);
        externalIdPropDefinition.setRequiredType(PropertyType.STRING);
        externalIdPropDefinition.setQueryOrderable(true);
        externalIdPropDefinition.setFullTextSearchable(true);
        ndt.getPropertyDefinitionTemplates().add(externalIdPropDefinition);

        PropertyDefinitionTemplate lastModifiedProp = nodeTypeManager.createPropertyDefinitionTemplate();
        lastModifiedProp.setName(JcrConstants.JCR_LASTMODIFIED);
        lastModifiedProp.setRequiredType(PropertyType.DATE);
        lastModifiedProp.setQueryOrderable(true);
        lastModifiedProp.setFullTextSearchable(true);
        ndt.getPropertyDefinitionTemplates().add(lastModifiedProp);

        String myNodeTypeName = JcrCustomConstants.FINA_FILE_NODE_TYPE;
        ndt.setName(myNodeTypeName);
        ndt.setMixin(false);
        ndt.setQueryable(true);

        String[] str = {JcrConstants.NT_FILE};
        ndt.setDeclaredSuperTypeNames(str);

        nodeTypeManager.registerNodeType(ndt, true);

    }

    public static void registerCustomMixin(NodeTypeManager nodeTypeManager) throws RepositoryException, ParseException {

        // Define the custom mixin using NodeTypeTemplate
        NodeTypeTemplate nodeTypeTemplate = nodeTypeManager.createNodeTypeTemplate();
        nodeTypeTemplate.setQueryable(true);

        nodeTypeTemplate.setName(JcrCustomConstants.FINA_NT_FOLDER_MIXIN);
        nodeTypeTemplate.setDeclaredSuperTypeNames(new String[]{JcrConstants.MIX_REFERENCEABLE});


        PropertyDefinitionTemplate descriptionPropertyDef = nodeTypeManager.createPropertyDefinitionTemplate();
        descriptionPropertyDef.setName(JcrCustomConstants.PROP_DESCRIPTION);
        descriptionPropertyDef.setRequiredType(PropertyType.STRING);

        PropertyDefinitionTemplate lastModifiedPropertyDef = nodeTypeManager.createPropertyDefinitionTemplate();
        lastModifiedPropertyDef.setName(JcrConstants.JCR_LASTMODIFIED);
        lastModifiedPropertyDef.setRequiredType(PropertyType.DATE);
        lastModifiedPropertyDef.setQueryOrderable(true);
        lastModifiedPropertyDef.setFullTextSearchable(true);
        nodeTypeTemplate.setMixin(true);

        // Add the property definition to the node type template
        nodeTypeTemplate.getPropertyDefinitionTemplates().add(descriptionPropertyDef);
        nodeTypeTemplate.getPropertyDefinitionTemplates().add(lastModifiedPropertyDef);


        // Register the node type template
        nodeTypeManager.registerNodeType(nodeTypeTemplate, true);
    }


}
