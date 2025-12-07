package net.fina.server.person.model.connection;

import net.fina.server.legalperson.model.BeneficiaryMetaModel;
import net.fina.server.person.model.PersonPositionMetaModel;
import net.fina.server.person.model.ShareMetaModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PersonConnectionsModel {
    private String uid;
    private String name;
    private PersonConnectionType connectionType;
    private List<BranchPersonConnectionModel> fiBranchPositions;
    private List<String> fiManagements;
    private List<BeneficiaryMetaModel> fiBeneficiaries;
    private List<PersonPositionMetaModel> personPositions;
    private List<ShareMetaModel> personShares;


    public PersonConnectionsModel() {
    }

    public PersonConnectionsModel(String name, PersonConnectionType connectionType, List<BranchPersonConnectionModel> fiBranchPositions, List<String> fiManagements, List<BeneficiaryMetaModel> fiBeneficiaries) {
        this.name = name;
        this.connectionType = connectionType;
        this.fiBranchPositions = fiBranchPositions;
        this.fiManagements = fiManagements;
        this.fiBeneficiaries = fiBeneficiaries;
        this.uid = UUID.randomUUID().toString();
    }

    public PersonConnectionsModel(String name, List<PersonPositionMetaModel> personPositions, List<ShareMetaModel> personShares) {
        this.name = name;
        this.connectionType = PersonConnectionType.LEGAL_PERSON;
        this.personPositions = personPositions;
        this.personShares = personShares;
        this.uid = UUID.randomUUID().toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PersonConnectionType getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(PersonConnectionType connectionType) {
        this.connectionType = connectionType;
    }

    public List<String> getFiManagements() {
        return fiManagements == null ? new ArrayList<>() : fiManagements;
    }

    public void setFiManagements(List<String> fiManagements) {
        this.fiManagements = fiManagements;
    }

    public List<BeneficiaryMetaModel> getFiBeneficiaries() {
        return fiBeneficiaries == null ? new ArrayList<>() : fiBeneficiaries;
    }

    public void setFiBeneficiaries(List<BeneficiaryMetaModel> fiBeneficiaries) {
        this.fiBeneficiaries = fiBeneficiaries;
    }

    public List<BranchPersonConnectionModel> getFiBranchPositions() {
        return fiBranchPositions == null ? new ArrayList<>() : fiBranchPositions;
    }

    public void setFiBranchPositions(List<BranchPersonConnectionModel> fiBranchPositions) {
        this.fiBranchPositions = fiBranchPositions;
    }

    public List<PersonPositionMetaModel> getPersonPositions() {
        return personPositions == null ? new ArrayList<>() : personPositions;
    }

    public void setPersonPositions(List<PersonPositionMetaModel> personPositions) {
        this.personPositions = personPositions;
    }

    public List<ShareMetaModel> getPersonShares() {
        return personShares == null ? new ArrayList<>() : personShares;
    }

    public void setPersonShares(List<ShareMetaModel> personShares) {
        this.personShares = personShares;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonConnectionsModel that = (PersonConnectionsModel) o;
        return Objects.equals(uid, that.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }
}
