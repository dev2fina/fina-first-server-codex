package net.fina.server.mdt.xml.v1;

import java.io.File;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

//TODO Test Class
public class TestMain {

	public static void main(String[] args) throws Exception {
		JAXBContext context = JAXBContext.newInstance("net.fina.server.mdt.xml.v1");

		Unmarshaller unmarshaller = context.createUnmarshaller();
		Mdt mdt = (Mdt) unmarshaller.unmarshal(new File("D:\\exportedMdt-S0.xml"));

		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(mdt, new File("D:\\test.xml"));

		System.out.println("End");

	}
}