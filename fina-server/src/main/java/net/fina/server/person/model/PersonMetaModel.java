package net.fina.server.person.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.common.client.fis.FiModel;
import net.fina.server.fi.entity.FiPersonConnectionType;
import net.fina.server.fi.model.RegionMetaModel;
import net.fina.server.person.model.connection.PersonConnectionsModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonMetaModel {
    private long id;
    private String name;
    private long nameStrId;
    private String identificationNumber;
    private String passportNumber;
    private PersonResidentStatus personResidentStatus;
    private RegionMetaModel citizenship;
    private PersonStatus status;
    private List<PersonEducationMetaModel> education;
    private List<RecommendationMetaModel> recommendations;
    private List<CriminalRecordMetaModel> criminalRecords;
    private List<ShareMetaModel> shares;
    private List<PersonPositionMetaModel> positions;
    private long fiPersonId;

    private Set<FiPersonConnectionType> connectionTypes;

    private List<FiModel> connectedFis;

    private List<PersonConnectionsModel> connections;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNameStrId() {
        return nameStrId;
    }

    public void setNameStrId(long nameStrId) {
        this.nameStrId = nameStrId;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public PersonResidentStatus getResidentStatus() {
        return personResidentStatus;
    }

    public void setResidentStatus(PersonResidentStatus personResidentStatus) {
        this.personResidentStatus = personResidentStatus;
    }

    public RegionMetaModel getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(RegionMetaModel citizenship) {
        this.citizenship = citizenship;
    }

    public PersonStatus getStatus() {
        return status;
    }

    public void setStatus(PersonStatus status) {
        this.status = status;
    }

    public List<PersonEducationMetaModel> getEducation() {
        return education == null ? new ArrayList<>() : education;
    }

    public void setEducation(List<PersonEducationMetaModel> education) {
        this.education = education;
    }

    public List<RecommendationMetaModel> getRecommendations() {
        return recommendations == null ? new ArrayList<>() : recommendations;
    }

    public void setRecommendations(List<RecommendationMetaModel> recommendations) {
        this.recommendations = recommendations;
    }

    public List<CriminalRecordMetaModel> getCriminalRecords() {
        return criminalRecords == null ? new ArrayList<>() : criminalRecords;
    }

    public void setCriminalRecords(List<CriminalRecordMetaModel> criminalRecords) {
        this.criminalRecords = criminalRecords;
    }

    public List<ShareMetaModel> getShares() {
        return shares == null ? new ArrayList<>() : shares;
    }

    public void setShares(List<ShareMetaModel> shares) {
        this.shares = shares;
    }

    public List<PersonPositionMetaModel> getPositions() {
        return positions == null ? new ArrayList<>() : positions;
    }

    public void setPositions(List<PersonPositionMetaModel> positions) {
        this.positions = positions;
    }

    public long getFiPersonId() {
        return fiPersonId;
    }

    public void setFiPersonId(long fiPersonId) {
        this.fiPersonId = fiPersonId;
    }

    public List<FiModel> getConnectedFis() {
        return connectedFis == null ? new ArrayList<>() : connectedFis;
    }

    public void setConnectedFis(List<FiModel> connectedFis) {
        this.connectedFis = connectedFis;
    }

    public List<PersonConnectionsModel> getConnections() {
        return connections == null ? new ArrayList<>() : connections;
    }

    public void setConnections(List<PersonConnectionsModel> connections) {
        this.connections = connections;
    }

    public Set<FiPersonConnectionType> getConnectionTypes() {
        return connectionTypes==null?new HashSet<>():connectionTypes;
    }

    public void setConnectionTypes(Set<FiPersonConnectionType> connectionTypes) {
        this.connectionTypes = connectionTypes;
    }
}
