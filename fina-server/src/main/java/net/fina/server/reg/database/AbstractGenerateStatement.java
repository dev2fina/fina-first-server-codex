package net.fina.server.reg.database;


import net.fina.server.reg.model.InputMetaModel;

import java.util.List;

public abstract class AbstractGenerateStatement {

    public static String FILE_ID_FIELD = "file_id";
    public static String SCHEDULE_ID_FIELD = "schedule_id";
    public static int FILE_ID_FIELD_SIZE = 10;

    protected final DatabaseInterface databaseInterface;
    protected final List<InputMetaModel> fields;

    public AbstractGenerateStatement(DatabaseInterface databaseInterface, List<InputMetaModel> fields, List<InputMetaModel> extraFields) {
        this.databaseInterface = databaseInterface;
        this.fields = fields;
        this.fields.addAll(extraFields);
    }


}
