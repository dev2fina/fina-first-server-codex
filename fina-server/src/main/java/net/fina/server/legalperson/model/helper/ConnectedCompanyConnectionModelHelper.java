package net.fina.server.legalperson.model.helper;

import net.fina.server.legalperson.entity.ConnectedCompanyConnection;
import net.fina.server.legalperson.model.ConnectedCompanyConnectionModel;

public class ConnectedCompanyConnectionModelHelper {

    public static ConnectedCompanyConnectionModel toModel(ConnectedCompanyConnection model, long langId) {
        ConnectedCompanyConnectionModel result = new ConnectedCompanyConnectionModel();
        result.setId(model.getId());
        result.setConnectionType(model.getConnectionType());
        result.setStrategicPlan(model.getStrategicPlan());
        result.setBusinessActivity(model.getBusinessActivity());
        result.setSource(LegalPersonModelHelper.toModel(model.getSource(), langId));
        result.setDestination(LegalPersonModelHelper.toModel(model.getDestination(), langId));

        return result;
    }


    public static ConnectedCompanyConnection toEntity(ConnectedCompanyConnectionModel model, long langId) {
        ConnectedCompanyConnection result = new ConnectedCompanyConnection();
        result.setId(model.getId());
        result.setConnectionType(model.getConnectionType());
        result.setStrategicPlan(model.getStrategicPlan());
        result.setBusinessActivity(model.getBusinessActivity());
        result.setSource(LegalPersonModelHelper.toEntity(model.getSource(), langId));
        result.setDestination(LegalPersonModelHelper.toEntity(model.getDestination(), langId));

        return result;
    }
}
