package net.fina.first.ecm.notification.model;

import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.first.FirstUtil;
import net.fina.first.ecm.EcmConstants;

import java.util.ArrayList;
import java.util.List;

public class NotificationInfoModelHelper {

    public static List<NotificationInfoMetaModel> getMetaModels(List<NodeRepresentation> nodeRepresentations) {
        List<NotificationInfoMetaModel> result = new ArrayList<>();

        if (nodeRepresentations != null && nodeRepresentations.size() > 0) {
            for (NodeRepresentation node : nodeRepresentations) {
                result.add(getMetaModel(node));
            }
        }

        return result;
    }

    public static NotificationInfoMetaModel getMetaModel(NodeRepresentation nodeRepresentation) {
        NotificationInfoMetaModel result = new NotificationInfoMetaModel();

        result.setId(nodeRepresentation.getId());
        result.setAddressee(FirstUtil.getValue(nodeRepresentation.getProperties()
                .get(EcmConstants.NOTIFICATION_PROP_ADDRESSEE), String.class));
        result.setScheduledSendDate(FirstUtil.getDateValue((String) nodeRepresentation.getProperties()
                .get(EcmConstants.NOTIFICATION_PROP_SCHEDULED_SEND_DATE), FirstUtil.DATE_FORMAT_LONG_STRING));
        result.setSent(FirstUtil.getValue(nodeRepresentation.getProperties()
                .get(EcmConstants.NOTIFICATION_PROP_IS_SENT), Boolean.class));
        result.setGapNotification(FirstUtil.getValue(nodeRepresentation.getProperties()
                .get(EcmConstants.NOTIFICATION_PROP_IS_GAP), Boolean.class));
        result.setDeadline(FirstUtil.getDateValue((String) nodeRepresentation.getProperties()
                .get(EcmConstants.NOTIFICATION_PROP_DEADLINE), FirstUtil.DATE_FORMAT_LONG_STRING));

        return result;
    }

}
