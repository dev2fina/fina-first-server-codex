package net.fina.server.dcs.uploadfile.impl.converter;

import net.fina.common.client.exception.DcsTypeException;
import net.fina.common.client.exception.DcsTypeException.Type;
import net.fina.server.dcs.uploadfile.impl.reader.DocumentReader;

/**
 * Abstract class which must be parent of any converter implementation
 */
public abstract class AbstractConverter {
    /**
     * @param documentReader instance of DocumentReader class to define how to read file
     * @throws DcsTypeException if any validation error occurs before converting
     */
    public AbstractConverter(DocumentReader documentReader) throws DcsTypeException {
        if (documentReader == null)
            throw new DcsTypeException(Type.READER_NOT_SET);
    }

    /**
     * @return ConvertInfo object which keeps information about converted
     * file(s)
     * @throws DcsTypeException if any error/exception occurs during converting
     */
    public abstract ConverterInfo convert() throws DcsTypeException;
}
