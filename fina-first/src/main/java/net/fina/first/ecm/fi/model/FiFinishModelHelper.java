package net.fina.first.ecm.fi.model;

public class FiFinishModelHelper {
    public static FiFinishModel getModel(FiFinishMetaModel metaModel) {
        FiFinishModel model = new FiFinishModel();

        if (metaModel != null) {
            model.setPreFinishVariables(metaModel.getPreFinishVariables());
            model.setFinishVariables(metaModel.getFinishVariables());
            model.setNewProcessName(metaModel.getNewProcessName());
        }

        return model;
    }
}
