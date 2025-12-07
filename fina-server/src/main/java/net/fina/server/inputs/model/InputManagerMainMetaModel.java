package net.fina.server.inputs.model;

import net.fina.common.client.returns.ImportModel;
import net.fina.common.client.returns.ScheduleModel;
import java.io.Serializable;

public class InputManagerMainMetaModel implements Serializable {

    private String id;
    private InputManagerMainModelType type;
    private boolean leaf;

    private ScheduleModel schedule;
    private InputManagerFiTypeMetaModel fiType;
    private InputManagerFiMetaModel fi;
    private InputManagerUploadFileMetaModel file;
    private ImportModel xml;

    public InputManagerMainMetaModel() {}

    public InputManagerMainMetaModel(String id, InputManagerMainModelType type, ScheduleModel schedule, InputManagerFiTypeMetaModel fiType,
                                     InputManagerFiMetaModel fi, InputManagerUploadFileMetaModel file, ImportModel xml) {

        this.id = id;
        this.type = type;
        this.leaf = type.equals(InputManagerMainModelType.XML);

        this.schedule = schedule;
        this.fiType = fiType;
        this.fi = fi;
        this.file = file;
        this.xml = xml;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public InputManagerMainModelType getType() {
        return type;
    }

    public void setType(InputManagerMainModelType type) {
        this.type = type;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public ScheduleModel getSchedule() {
        return schedule;
    }

    public void setSchedule(ScheduleModel schedule) {
        this.schedule = schedule;
    }

    public InputManagerFiTypeMetaModel getFiType() {
        return fiType;
    }

    public void setFiType(InputManagerFiTypeMetaModel fiType) {
        this.fiType = fiType;
    }

    public InputManagerFiMetaModel getFi() {
        return fi;
    }

    public void setFi(InputManagerFiMetaModel fi) {
        this.fi = fi;
    }

    public InputManagerUploadFileMetaModel getFile() {
        return file;
    }

    public void setFile(InputManagerUploadFileMetaModel file) {
        this.file = file;
    }

    public ImportModel getXml() {
        return xml;
    }

    public void setXml(ImportModel imported) {
        this.xml = imported;
    }

}
