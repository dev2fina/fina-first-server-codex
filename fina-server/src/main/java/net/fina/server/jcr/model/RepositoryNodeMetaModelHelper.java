package net.fina.server.jcr.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fina.common.shared.jcr.DescriptionModel;
import net.fina.common.shared.jcr.JcrCustomConstants;
import net.fina.common.shared.jcr.RepositoryNodeMetaModel;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.VersionRepresentation;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.util.ISO9075;
import org.jboss.logging.Logger;

import javax.jcr.Node;
import java.util.*;

public class RepositoryNodeMetaModelHelper {
    private static final Logger log = Logger.getLogger(RepositoryNodeMetaModelHelper.class);

    public static RepositoryNodeMetaModel jcrNodeToModel(Node current) throws Exception {
        RepositoryNodeMetaModel nodeMetaModel = new RepositoryNodeMetaModel();
        nodeMetaModel.setPath(ISO9075.decode(current.getPath()));
        nodeMetaModel.setId(current.getIdentifier());
        nodeMetaModel.setFolder(current.isNodeType(JcrConstants.NT_FOLDER));
        nodeMetaModel.setFile(current.isNodeType(JcrConstants.NT_FILE));
        nodeMetaModel.setName(ISO9075.decode(current.getName()));
        if (current.hasProperty(JcrCustomConstants.PROP_DESCRIPTION)) {
            nodeMetaModel.setDescriptions(parseDescriptionFromJson(current.getProperty(JcrCustomConstants.PROP_DESCRIPTION).getString()));
        }
        if (current.hasProperty(JcrCustomConstants.PROP_MARK_DELETED)) {
            nodeMetaModel.setDeleted(current.getProperty(JcrCustomConstants.PROP_MARK_DELETED).getBoolean());
        }
        if (current.hasProperty(JcrConstants.JCR_LASTMODIFIED)) {
            nodeMetaModel.setLastModified(current.getProperty(JcrConstants.JCR_LASTMODIFIED).getDate().getTime());
        } else {
            nodeMetaModel.setLastModified(current.getProperty(JcrConstants.JCR_CREATED).getDate().getTime());
        }
        nodeMetaModel.setParentId(current.getParent().getIdentifier());

        return nodeMetaModel;
    }

    public static RepositoryNodeMetaModel nodeRepresentationToModel(NodeRepresentation nodeRepresentation) {
        RepositoryNodeMetaModel model = new RepositoryNodeMetaModel();

        model.setId(nodeRepresentation.getId());
        if (nodeRepresentation.getProperties() != null) {
            model.setVersionId(getValue(nodeRepresentation.getProperties().get("cm:versionLabel"), String.class));
            model.setDescriptions(parseDescriptionFromJson(getValue(nodeRepresentation.getProperties().get("cm:description"), String.class)));
            model.setDeleted(getValue(nodeRepresentation.getProperties().get(JcrCustomConstants.PROP_MARK_DELETED), Boolean.class));
        }
        model.setName(nodeRepresentation.getName());
        model.setFile(nodeRepresentation.isFile());
        model.setFolder(nodeRepresentation.isFolder());
        model.setLastModified(nodeRepresentation.getModifiedAt());
        if (nodeRepresentation.getPath() != null) {
            model.setPath(nodeRepresentation.getPath().getName());
        }
        model.setParentId(nodeRepresentation.getParentId());

        return model;
    }

    public static RepositoryNodeMetaModel nodeRepresentationToModel(VersionRepresentation nodeRepresentation) {
        RepositoryNodeMetaModel model = new RepositoryNodeMetaModel();

        model.setId(nodeRepresentation.getId());
        model.setVersionId(getValue(nodeRepresentation.getProperties().get("cm:versionLabel"), String.class));
        model.setName(nodeRepresentation.getName());
        model.setDescriptions(parseDescriptionFromJson(getValue(nodeRepresentation.getProperties().get("cm:description"), String.class)));
        model.setFile(nodeRepresentation.getIsFile());
        model.setFolder(nodeRepresentation.getIsFolder());
        model.setLastModified(nodeRepresentation.getModifiedAt());
        model.setDeleted(getValue(nodeRepresentation.getProperties().get(JcrCustomConstants.PROP_MARK_DELETED), Boolean.class));

        return model;
    }

    public static List<RepositoryNodeMetaModel> versionsToList(List<VersionRepresentation> nodeRepresentationList) {
        List<RepositoryNodeMetaModel> result = new ArrayList<>();

        nodeRepresentationList.forEach(n -> {
            result.add(nodeRepresentationToModel(n));
        });

        return result;
    }

    public static List<RepositoryNodeMetaModel> toList(List<? extends NodeRepresentation> nodeRepresentationList) {
        List<RepositoryNodeMetaModel> result = new ArrayList<>();

        nodeRepresentationList.forEach(n -> {
            result.add(nodeRepresentationToModel(n));
        });

        return result;
    }

    public static <T extends Object> T getValue(Object value, Class<T> type) {
        if (value != null) {
            return type.cast(value);
        }
        return null;
    }


    public static String getJsonDescription(List<DescriptionModel> description) {
        String json = "";
        try {
            if (description != null) {
                return new ObjectMapper().writeValueAsString(description);
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

        return json;
    }

    public static List<DescriptionModel> parseDescriptionFromJson(String json) {
        try {
            if (json != null) {
                ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                List<DescriptionModel> descriptions = mapper.readValue(json, new TypeReference<List<DescriptionModel>>() {
                });
                return descriptions;
            }
        } catch (Throwable t) {
            //ignore
        }

        return new ArrayList<>();
    }

    public static String getDescription(List<DescriptionModel> descriptionMetaModels, String langCode) {
        Optional<DescriptionModel> desc = descriptionMetaModels.stream().filter(d -> d.getLangCode().equals(langCode)).findFirst();
        if (desc.isPresent()) {
            return desc.get().getDescription();
        }
        return "";
    }


    public static void sort(List<RepositoryNodeMetaModel> list, String orderBy) {
        Collections.sort(list, new Comparator<RepositoryNodeMetaModel>() {
            @Override
            public int compare(RepositoryNodeMetaModel t0, RepositoryNodeMetaModel t1) {
                if (orderBy == null) {
                    t1.getLastModified().compareTo(t0.getLastModified());
                }
                String[] params = orderBy.split(" ");
                String paramName = params[0];
                String sortDirection = params[1];

                if (sortDirection.equalsIgnoreCase("DESC")) {

                    if (paramName.equalsIgnoreCase("name")) {
                        t1.getName().compareTo(t0.getName());
                    } else if (paramName.equalsIgnoreCase("lastModified")) {
                        return t1.getLastModified().compareTo(t0.getLastModified());
                    }
                } else {
                    if (paramName.equalsIgnoreCase("name")) {
                        t0.getName().compareTo(t1.getName());
                    } else if (paramName.equalsIgnoreCase("lastModified")) {
                        return t0.getLastModified().compareTo(t1.getLastModified());
                    }
                }

                return 0;
            }
        });
    }
}
