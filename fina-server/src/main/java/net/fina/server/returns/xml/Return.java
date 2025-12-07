package net.fina.server.returns.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * XML return object for JAXB marshalling
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "RETURN")
@XmlType(name = "RETURN", propOrder = { "header", "body" })
public class Return {
	@XmlElement(name = "HEADER")
	private Header header;

	@XmlElement(name = "BODY")
	private Body body;

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

}
