package net.fina.first.ecm.fi.imp.model;

import java.util.Map;

public class RegistryModelHelp {

    public static RegistryModel copy(RegistryModel original) {
        RegistryModel newModel = new RegistryModel();
        newModel.setRegistryType(original.getRegistryType());
        newModel.getFolderProps().putAll(original.getFolderProps());

        for (Map.Entry<String, RegistryValueModel> e : original.getProperties().entrySet()) {
            newModel.getProperties().put(e.getKey(), copy(e.getValue()));
        }

        newModel.getDetailsMapping().putAll(original.getDetailsMapping());
        newModel.getDetailsValue().putAll(original.getDetailsValue());
        return newModel;
    }

    public static RegistryValueModel copy(RegistryValueModel original) {
        RegistryValueModel newModel = new RegistryValueModel();
        newModel.setValue(original.getValue());
        newModel.setReference(original.getReference());
        newModel.setDefaultValue(original.getDefaultValue());
        newModel.setValueType(original.getValueType());
        return newModel;
    }
}
