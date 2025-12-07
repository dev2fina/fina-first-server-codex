package net.fina.first.ecm.workflow.model;

import net.fina.ecm.alfresco.api.workflow.model.FormRepresentation;
import net.fina.ecm.alfresco.api.workflow.model.WorkflowProcessDefinitionRepresentation;
import net.fina.first.ecm.dictionary.model.ClassPropertyMetaModel;

import java.util.List;

public class WorkflowProcessDefinitionMetaModel {
    private WorkflowProcessDefinitionRepresentation processDefinition;
    private List<FormRepresentation> form;
    private List<ClassPropertyMetaModel> classProperties;


    public WorkflowProcessDefinitionMetaModel() {
    }

    public WorkflowProcessDefinitionMetaModel(WorkflowProcessDefinitionRepresentation processDefinition, List<FormRepresentation> form) {
        this.processDefinition = processDefinition;
        this.form = form;
    }

    public WorkflowProcessDefinitionRepresentation getProcessDefinition() {
        return processDefinition;
    }

    public void setProcessDefinition(WorkflowProcessDefinitionRepresentation processDefinition) {
        this.processDefinition = processDefinition;
    }

    public List<FormRepresentation> getForm() {
        return form;
    }

    public void setForm(List<FormRepresentation> form) {
        this.form = form;
    }

    public List<ClassPropertyMetaModel> getClassProperties() {
        return classProperties;
    }

    public void setClassProperties(List<ClassPropertyMetaModel> classProperties) {
        this.classProperties = classProperties;
    }
}
