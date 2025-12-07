package net.fina.server.rvc.impl;

import net.fina.server.rvc.xml.FileType;
import net.fina.server.rvc.xml.ObjectFactory;
import net.fina.server.rvc.xml.ReturnStatusesType;
import net.fina.server.rvc.xml.StatusType;
import org.junit.Test;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class StatusXmlTest {

    @Test
    public void test() throws JAXBException, IOException {

        JAXBContext context = JAXBContext.newInstance(net.fina.server.rvc.xml.ObjectFactory.class.getPackage().getName());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ObjectFactory objectFactory = new ObjectFactory();

        ReturnStatusesType statusesType = objectFactory.createReturnStatusesType();

        StatusType statusType = objectFactory.createStatusType();
        statusType.setNote("blah");
        statusType.setStatus("demo status");

        statusesType.getStatus().add(statusType);
        statusesType.getStatus().add(statusType);

        FileType fileType = objectFactory.createFileType();
        fileType.setReturnFileVersionId("v1");
        fileType.setMdtFileVersionId("v1");
        statusType.setFile(fileType);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            marshaller.marshal(statusesType, out);
            System.out.println(new String(out.toByteArray()));
        }
    }
}
