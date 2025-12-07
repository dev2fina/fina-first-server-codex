package net.fina.server.reg.util;

import java.io.InputStream;

public class RegXmlFileXsdHelper {
    public InputStream getXsdFile() throws Exception {
        String schemaFile = getClass().getPackage().getName().replace('.', '/') + "/stat_file_xml.xsd";
        return getClass().getClassLoader().getResourceAsStream(schemaFile);
    }
}
