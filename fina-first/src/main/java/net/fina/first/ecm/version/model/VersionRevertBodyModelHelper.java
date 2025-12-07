package net.fina.first.ecm.version.model;

import net.fina.ecm.alfresco.api.core.model.body.RevertBody;

public class VersionRevertBodyModelHelper {

    public static RevertBody getRepresentation(VersionRevertBodyMetaModel metaModel) {
        return new RevertBody(metaModel.getComment(), metaModel.isMajorVersion());
    }

}
