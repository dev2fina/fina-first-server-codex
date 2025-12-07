package net.fina.first.ecm.fi.model;

import net.fina.ecm.alfresco.api.workflow.model.VariableRepresentation;

import java.util.List;

public class FiFinishModel {
    private List<VariableRepresentation> preFinishVariables;
    private List<VariableRepresentation> finishVariables;
    private String newProcessName;

    public FiFinishModel() {
    }

    public FiFinishModel(List<VariableRepresentation> preFinishVariables,
                         List<VariableRepresentation> finishVariables,
                         String newProcessName) {
        this.preFinishVariables = preFinishVariables;
        this.finishVariables = finishVariables;
        this.newProcessName = newProcessName;
    }

    public List<VariableRepresentation> getPreFinishVariables() {
        return preFinishVariables;
    }

    public void setPreFinishVariables(List<VariableRepresentation> preFinishVariables) {
        this.preFinishVariables = preFinishVariables;
    }

    public List<VariableRepresentation> getFinishVariables() {
        return finishVariables;
    }

    public void setFinishVariables(List<VariableRepresentation> finishVariables) {
        this.finishVariables = finishVariables;
    }

    public String getNewProcessName() {
        return newProcessName;
    }

    public void setNewProcessName(String newProcessName) {
        this.newProcessName = newProcessName;
    }
}
