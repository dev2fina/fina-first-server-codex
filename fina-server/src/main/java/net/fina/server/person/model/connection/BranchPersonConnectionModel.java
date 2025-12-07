package net.fina.server.person.model.connection;

import net.fina.server.fi.entity.FiPersonConnectionType;

import java.util.List;

public class BranchPersonConnectionModel {
    private String code;
    private String description;
    private List<FiPersonConnectionType> positions;

    public BranchPersonConnectionModel() {
    }

    public BranchPersonConnectionModel(String code, String description, List<FiPersonConnectionType> positions) {
        this.code = code;
        this.description = description;
        this.positions = positions;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public List<FiPersonConnectionType> getPositions() {
        return positions;
    }
}
