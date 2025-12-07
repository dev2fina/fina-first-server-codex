package net.fina.first.ecm.fi.model;

import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.first.FirstUtil;
import net.fina.first.ecm.EcmConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FiTypeModelHelper {

    public static List<FiTypeMetaModel> getModels(List<NodeRepresentation> fiTypeNodeRepresentations) {
        List<FiTypeMetaModel> result = new ArrayList<>();
        if (fiTypeNodeRepresentations != null && !fiTypeNodeRepresentations.isEmpty()) {
            for (NodeRepresentation nodeRepresentation : fiTypeNodeRepresentations) {
                result.add(getModel(nodeRepresentation.getProperties(), nodeRepresentation.getId()));
            }
        }
        return result;
    }

    public static FiTypeMetaModel getModel(Map<String, Object> properties, String id) {
        FiTypeMetaModel model = new FiTypeMetaModel();
        model.setId(id);

        if (properties != null && !properties.isEmpty()) {
            model.setCode(FirstUtil.getValue(properties.get(EcmConstants.PROP_CODE), String.class));
            model.setDescription(FirstUtil.getValue(properties.get(EcmConstants.PROP_DESCRIPTION), String.class));
            model.setRegistrationWorkflowKey(FirstUtil.getValue(properties.get(EcmConstants.PROP_REGISTRATION_WORKFLOW_KEY), String.class));
            model.setChangeWorkflowKey(FirstUtil.getValue(properties.get(EcmConstants.PROP_CHANGE_WORKFLOW_KEY), String.class));
            model.setDisableWorkflowKey(FirstUtil.getValue(properties.get(EcmConstants.PROP_DISABLE_WORKFLOW_KEY), String.class));
            model.setBranchChangeWorkflowKey(FirstUtil.getValue(properties.get(EcmConstants.PROP_BRANCH_CHANGE_WORKFLOW_KEY), String.class));
            model.setBranchEditWorkflowKey(FirstUtil.getValue(properties.get(EcmConstants.PROP_BRANCH_EDIT_WORKFLOW_KEY), String.class));
            model.setDocumentWithdrawalWorkflowKey(FirstUtil.getValue(properties.get(EcmConstants.PROP_DOCUMENT_WITHDRAWAL_WORKFLOW_KEY), String.class));
            model.setBranchTypes(FirstUtil.getValue(properties.get(EcmConstants.PROP_BRANCH_TYPES), List.class));
        }

        return model;
    }

}
