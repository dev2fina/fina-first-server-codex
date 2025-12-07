package net.fina.first.ecm.complexstructure;

import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.first.ecm.EcmConstants;

import java.util.Objects;

public class ComplexStructureCalculateResult {
    private NodeRepresentation physicalPerson;
    private double capitalPercentage;

    public ComplexStructureCalculateResult(NodeRepresentation physicalPerson, double capitalPercentage) {
        this.physicalPerson = physicalPerson;
        this.capitalPercentage = capitalPercentage;
    }

    public NodeRepresentation getPhysicalPerson() {
        return physicalPerson;
    }

    public void setPhysicalPerson(NodeRepresentation physicalPerson) {
        this.physicalPerson = physicalPerson;
    }

    public double getCapitalPercentage() {
        return capitalPercentage;
    }

    public void setCapitalPercentage(double capitalPercentage) {
        this.capitalPercentage = capitalPercentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComplexStructureCalculateResult)) return false;
        ComplexStructureCalculateResult that = (ComplexStructureCalculateResult) o;
        String thatIdNumber = (String) that.getPhysicalPerson().getProperties().get(EcmConstants.FI_PERSON_PROP_PERSONAL_NUMBER);
        String thisIdNumber = (String) ((ComplexStructureCalculateResult) o).getPhysicalPerson().getProperties().get(EcmConstants.FI_PERSON_PROP_PERSONAL_NUMBER);
        return thatIdNumber.trim().equals(thisIdNumber.trim());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCapitalPercentage(), getPhysicalPerson().getId());
    }
}
