package net.fina.server.mdt.xml.v1;

import jakarta.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

	public ObjectFactory() {

	}

	public Mdt createReturn() {
		return new Mdt();
	}
}
