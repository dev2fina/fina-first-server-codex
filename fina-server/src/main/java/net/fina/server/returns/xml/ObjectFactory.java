package net.fina.server.returns.xml;

import jakarta.xml.bind.annotation.XmlRegistry;

/**
 * 
 * @author dato.java
 * 
 */
@XmlRegistry
public class ObjectFactory {

	public ObjectFactory() {

	}

	public Return createReturn() {
		return new Return();
	}
}
