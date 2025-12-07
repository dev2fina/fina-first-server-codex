package net.fina.first.ecm.notification.impl;

import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.parameters.OrderByParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.AssociationBody;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultNodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultSetRepresentation;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.first.FirstUtil;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.node.api.NodeLocal;
import net.fina.first.ecm.notification.api.NotificationInfoLocal;
import net.fina.first.ecm.notification.model.NotificationInfoMetaModel;
import net.fina.first.ecm.notification.model.NotificationInfoModelHelper;
import net.fina.first.ecm.registry.model.FiRegistryActionType;
import net.fina.first.ecm.registry.model.FiRegistryMetaModel;
import net.fina.first.ecm.registry.model.FiRegistryMetaModelHelper;
import net.fina.first.ecm.search.api.SearchLocal;
import net.fina.first.interceptors.FirstRecordingAuditor;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import java.util.*;

@Stateless
@Local(NotificationInfoLocal.class)
@Interceptors(FirstRecordingAuditor.class)
public class NotificationInfoSession implements NotificationInfoLocal {

    @Inject
    private EcmClientProxySession ecmClientProxySession;

    @Inject
    private SearchLocal searchLocal;

    @Inject
    private NodeLocal nodeLocal;

    @Override
    public List<NotificationInfoMetaModel> load() {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();

        String notificationsFolderPath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.NOTIFICATIONS_FOLDER_PATH_KEY);
        String folderRoot = APIConstants.FOLDER_ROOT;
        ResultPaging<NodeRepresentation> notifications = client.getNodesAPI().listNodeChildrenCall(folderRoot, null, null,
                new OrderByParam(Collections.singletonList("createdAt desc")), null,
                new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), notificationsFolderPath,
                null, null);

        List<NotificationInfoMetaModel> notificationModels = NotificationInfoModelHelper.getMetaModels(notifications.getObjects());

        // Set associated infos
        for (NotificationInfoMetaModel model : notificationModels) {
            ResultPaging<NodeRepresentation> targetAssociations = client.getNodesAPI()
                    .listTargetAssociationsCall(model.getId(), null, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), null);
            for (NodeRepresentation nr : targetAssociations.getObjects()) {
                if (nr.getAssociation().getAssocType().equals(EcmConstants.NOTIFICATION_ASSOC_FI_REGISTRY)) {
                    FiRegistryMetaModel fiRegistryMetaModel = FiRegistryMetaModelHelper.getModel(nr);
                    model.setAssociatedFiRegistry(fiRegistryMetaModel);
                }

                if (nr.getAssociation().getAssocType().equals(EcmConstants.NOTIFICATION_ASSOC_FI_REGISTRY_ACTION)) {
                    model.setAssociatedActionType(FiRegistryActionType.valueOf((String) nr.getProperties().get(EcmConstants.ACTION_PROP_TYPE)));
                    model.setAssociatedActionFinalStatus((String) nr.getProperties().get(EcmConstants.ACTION_PROP_FINAL_PROGRESS_STATUS));
                }
            }
        }

        return notificationModels;
    }

    @Override
    public void save(NotificationInfoMetaModel model) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();

        NodeBodyUpdate nodeBodyUpdate = new NodeBodyUpdate();
        TreeMap<String, Object> nodeProperties = new TreeMap<>();
        nodeProperties.put(EcmConstants.NOTIFICATION_PROP_IS_SENT, model.isSent());
        nodeBodyUpdate.setProperties(nodeProperties);

        client.getNodesAPI().updateNodeCall(model.getId(), nodeBodyUpdate);
    }

    @Override
    public void delete(String notificationId) throws NodeException {
        nodeLocal.deleteNodeById(notificationId);
    }

    @Override
    public void delete(AlfrescoClient client, String notificationId) {
        client.getNodesAPI().deleteNodeCall(notificationId);
    }

    @Override
    public void deleteNotificationsForAction(String actionId, String userLogin) {
        AlfrescoClient client = AlfrescoClient.getInstance();
        deleteExistingNotifications(client, actionId, userLogin);
    }

    @Override
    public void updateActionNotifications(String acceptLanguage, String fiRegistryId, String actionId, String userLogin, boolean isPausedOnGap) throws NodeException {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient(acceptLanguage);

        deleteExistingNotifications(client, actionId, userLogin);

        FiRegistryMetaModel fiRegistry = FiRegistryMetaModelHelper.getModel(client.getNodesAPI().getNodeCall(fiRegistryId));
        NodeRepresentation fiRegistryAction = client.getNodesAPI().getNodeCall(actionId);

        Date scheduledSendDatePreliminary;
        Date scheduledSendDateSameDay;
        Date actionDeadline;
        Date now = new Date();
        final int PRELIMINARY_NOTIFICATION_DAYS_DIFF = 3;

        //Create new notifications

        if (isPausedOnGap) {
            int numDaysToFixGap = FirstUtil.getValue(fiRegistryAction.getProperties().get(EcmConstants.ACTION_PROP_NUM_DAYS_FOR_GAP_CORRECTION), Integer.class);
            actionDeadline = addDays(now, numDaysToFixGap);
            scheduledSendDatePreliminary = addDays(now, numDaysToFixGap - PRELIMINARY_NOTIFICATION_DAYS_DIFF);
            scheduledSendDateSameDay = addDays(now, numDaysToFixGap);

            // Update number of days redactor has left to complete action after process is resumed
            updateActionDeadlineInfo(client, fiRegistryAction);

        } else { // This means, notifications are being updated after process has been resumed
            int numDaysForActionCompletion = FirstUtil.getValue(fiRegistryAction.getProperties()
                    .get(EcmConstants.ACTION_PROP_NUM_DAYS_TO_FINISH), Integer.class);

            actionDeadline = addDays(now, numDaysForActionCompletion);
            scheduledSendDatePreliminary = addDays(now, numDaysForActionCompletion - PRELIMINARY_NOTIFICATION_DAYS_DIFF);
            scheduledSendDateSameDay = addDays(now, numDaysForActionCompletion);
        }

        String notificationFolderId = getNotificationsFolder().getId();
        createNotification(client, notificationFolderId, fiRegistry, fiRegistryAction, scheduledSendDatePreliminary, actionDeadline, userLogin, isPausedOnGap);
        createNotification(client, notificationFolderId, fiRegistry, fiRegistryAction, scheduledSendDateSameDay, actionDeadline, userLogin, isPausedOnGap);

    }

    @Override
    public void markAsSent(String notificationId) throws NodeException {
        TreeMap<String, Object> propertiesToUpdate = new TreeMap<>();
        propertiesToUpdate.put(EcmConstants.NOTIFICATION_PROP_IS_SENT, true);
        NodeBodyUpdate nodeBodyUpdate = new NodeBodyUpdate(propertiesToUpdate);
        nodeLocal.updateNode(notificationId, nodeBodyUpdate);
    }

    @Override
    public void changeActionNotificationsAddressee(String actionId, String addresseeLogin) throws NodeException {
        String query = "TYPE:'" + EcmConstants.NOTIFICATION_TYPE + "' AND " + EcmConstants.NOTIFICATION_PROP_ACTION_ID + ": '" + actionId + "'";
        ResultSetRepresentation<ResultNodeRepresentation> resultSet = searchLocal.searchAFTS("*", query, 0, 1000);
        if (resultSet != null && resultSet.getObjects() != null && resultSet.getObjects().size() > 0) {
            for (ResultNodeRepresentation nodeRepresentation : resultSet.getObjects()) {
                String notificationId = nodeRepresentation.getId();
                TreeMap<String, Object> updatedProps = new TreeMap<>();
                updatedProps.put(EcmConstants.NOTIFICATION_PROP_ADDRESSEE, addresseeLogin);
                NodeBodyUpdate nbu = new NodeBodyUpdate(updatedProps);

                nodeLocal.updateNode(notificationId, nbu);
            }
        }
    }

    private void deleteExistingNotifications(AlfrescoClient client, String actionId, String userLogin) {
        String query = "TYPE:'" + EcmConstants.NOTIFICATION_TYPE + "' AND " +
                EcmConstants.NOTIFICATION_PROP_ACTION_ID + ": '" + actionId + "'";
        if (userLogin != null) {
            query += " AND " +
                    EcmConstants.NOTIFICATION_PROP_ADDRESSEE + ": '" + userLogin + "'";
        }
        ResultSetRepresentation<ResultNodeRepresentation> existingNotifications = searchLocal.searchAFTS("*", query, 0, 1000);

        for (ResultNodeRepresentation resultNodeRepresentation : existingNotifications.getObjects()) {
            client.getNodesAPI().deleteNodeCall(resultNodeRepresentation.getId());
        }
    }

    private NodeRepresentation getNotificationsFolder() {
        String notificationsFolderPath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.NOTIFICATIONS_FOLDER_PATH_KEY);
        return nodeLocal.getNodeById(APIConstants.FOLDER_ROOT, null, notificationsFolderPath, null);
    }

    private void updateActionDeadlineInfo(AlfrescoClient client, NodeRepresentation actionNode) {
        Date actionCreationDate = actionNode.getCreatedAt();
        int numDaysForActionCompletion = FirstUtil.getValue(actionNode.getProperties()
                .get(EcmConstants.ACTION_PROP_NUM_DAYS_TO_FINISH), Integer.class);

        Date now = new Date();
        int numDaysPassed = (int) (now.getTime() - actionCreationDate.getTime()) / 86400000; // 24 hours in milliseconds

        TreeMap<String, Object> propertiesToUpdate = new TreeMap<>();
        propertiesToUpdate.put(EcmConstants.ACTION_PROP_NUM_DAYS_TO_FINISH, numDaysForActionCompletion - numDaysPassed);
        NodeBodyUpdate nodeBodyUpdate = new NodeBodyUpdate(propertiesToUpdate);

        client.getNodesAPI().updateNodeCall(actionNode.getId(), nodeBodyUpdate);
    }

    private Date addDays(Date date, int numDays) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, numDays);

        return cal.getTime();
    }


    private void createNotification(AlfrescoClient client, String parentId, FiRegistryMetaModel fiRegistryMetaModel,
                                    NodeRepresentation actionNode, Date sendDate, Date deadline, String userLogin, boolean isGap) throws NodeException {
        TreeMap<String, Object> notificationProperties = new TreeMap<>();
        notificationProperties.put(EcmConstants.NOTIFICATION_PROP_ADDRESSEE, userLogin);
        notificationProperties.put(EcmConstants.NOTIFICATION_PROP_DEADLINE, deadline);
        notificationProperties.put(EcmConstants.NOTIFICATION_PROP_SCHEDULED_SEND_DATE, sendDate);
        notificationProperties.put(EcmConstants.NOTIFICATION_PROP_IS_SENT, false);
        notificationProperties.put(EcmConstants.NOTIFICATION_PROP_IS_GAP, isGap);
        notificationProperties.put(EcmConstants.NOTIFICATION_PROP_ACTION_ID, actionNode.getId());

        NodeBodyCreate nodeBodyCreate = new NodeBodyCreate(UUID.randomUUID().toString(), EcmConstants.NOTIFICATION_TYPE);
        nodeBodyCreate.setProperties(notificationProperties);

        NodeRepresentation newNotification = nodeLocal.createNode(parentId, nodeBodyCreate, client);

        // Create FiRegistry association
        AssociationBody fiRegistryAssociationBody = new AssociationBody();
        fiRegistryAssociationBody.setAssocType(EcmConstants.NOTIFICATION_ASSOC_FI_REGISTRY);
        fiRegistryAssociationBody.setTargetId(fiRegistryMetaModel.getId());
        client.getNodesAPI().createAssocationCall(newNotification.getId(), fiRegistryAssociationBody, null);

        // Create FiRegistryAction association
        AssociationBody fiRegistryActionAssociationBody = new AssociationBody();
        fiRegistryActionAssociationBody.setAssocType(EcmConstants.NOTIFICATION_ASSOC_FI_REGISTRY_ACTION);
        fiRegistryActionAssociationBody.setTargetId(actionNode.getId());
        client.getNodesAPI().createAssocationCall(newNotification.getId(), fiRegistryActionAssociationBody, null);
    }

}
