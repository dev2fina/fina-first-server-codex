package net.fina.server.processing.model;

import net.fina.common.client.mdt.MDTComparisonConditions;
import net.fina.common.shared.mdt.ProcessStage;

import java.io.Serializable;

public class ComparisonItem implements Serializable {
    public long nodeId;
    public MDTComparisonConditions condition;
    public String leftEquation;
    public String equation;
    public String messageTemplate;
    public String numberPattern;
    public ProcessStage processStage = ProcessStage.DEFAULT;
}
