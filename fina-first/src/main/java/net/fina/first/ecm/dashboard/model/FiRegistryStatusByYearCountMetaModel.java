package net.fina.first.ecm.dashboard.model;

public class FiRegistryStatusByYearCountMetaModel extends FiRegistryStatusCountMetaModel {
    private int year;

    public FiRegistryStatusByYearCountMetaModel() {
        super();
    }

    public FiRegistryStatusByYearCountMetaModel(String fiType, int active, int inactive, int canceled, int year) {
        super(fiType, active, inactive, canceled);
        this.year = year;
    }

    public FiRegistryStatusByYearCountMetaModel(FiRegistryStatusCountMetaModel fiRegistryStatusCountMetaModel, int year) {
        this(fiRegistryStatusCountMetaModel.getFiType(), fiRegistryStatusCountMetaModel.getActive(), fiRegistryStatusCountMetaModel.getInactive(), fiRegistryStatusCountMetaModel.getCanceled(), year);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
