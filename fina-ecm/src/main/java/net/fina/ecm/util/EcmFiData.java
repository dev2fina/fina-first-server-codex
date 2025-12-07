package net.fina.ecm.util;

import net.fina.common.client.fis.FiModel;
import net.fina.common.client.fis.FiTypeModel;
import net.fina.common.shared.fi.FiManagementMetaModel;
import net.fina.common.shared.fi.LicenceMetaModel;
import net.fina.common.shared.fi.LicenceTypeMetaModel;
import net.fina.common.shared.fi.ManagementMetaModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by baaka on 9/10/2015.
 */
public class EcmFiData implements Serializable {
    private List<FiTypeModel> fiTypes;
    private List<FiModel> fis;
    private List<LicenceMetaModel> fiLicences;
    private List<ManagementMetaModel> managements;
    private List<FiManagementMetaModel> fiManagements;
    private List<LicenceTypeMetaModel> licenceTypes;

    public EcmFiData() {
    }

    public List<FiTypeModel> getFiTypes() {
        return fiTypes;
    }

    public void setFiTypes(List<FiTypeModel> fiTypes) {
        this.fiTypes = fiTypes;
    }

    public List<FiModel> getFis() {
        return fis;
    }

    public void setFis(List<FiModel> fis) {
        this.fis = fis;
    }

    public List<LicenceMetaModel> getFiLicences() {
        return fiLicences;
    }

    public void setFiLicences(List<LicenceMetaModel> fiLicences) {
        this.fiLicences = fiLicences;
    }

    public List<ManagementMetaModel> getManagements() {
        return managements;
    }

    public void setManagements(List<ManagementMetaModel> managements) {
        this.managements = managements;
    }

    public List<FiManagementMetaModel> getFiManagements() {
        return fiManagements;
    }

    public void setFiManagements(List<FiManagementMetaModel> fiManagements) {
        this.fiManagements = fiManagements;
    }

    public List<LicenceTypeMetaModel> getLicenceTypes() {
        return licenceTypes;
    }

    public void setLicenceTypes(List<LicenceTypeMetaModel> licenceTypes) {
        this.licenceTypes = licenceTypes;
    }
}
