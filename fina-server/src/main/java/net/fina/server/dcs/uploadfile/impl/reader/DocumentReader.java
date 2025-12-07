package net.fina.server.dcs.uploadfile.impl.reader;

import net.fina.common.client.exception.DcsTypeException;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.returns.xml.Return;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class is used to read documents, any reader must extend DocumentReader
 *
 * @author dato.java
 */
public abstract class DocumentReader {
    protected final List<String> conventerReasons = new ArrayList<>();
    protected final Map<String, Object> properties;
    protected UploadFile file;

    public DocumentReader(Map<String, Object> properties) throws DcsTypeException {
        if (properties == null) {
            throw new NullPointerException("Properties parameter is null!");
        }
        this.properties = properties;
    }


    /**
     * @return the list of files that have to be converted
     */
    public UploadFile getFile() {
        return file;
    }

    /**
     * sets files that have to converted
     */
    public void setFile(UploadFile file) {
        this.file = file;
    }

    /**
     * @return This list of xml files that have to be imported
     * @throws DcsTypeException if any error/exception occurs
     */
    public abstract List<Return> getReturns() throws DcsTypeException;

}
