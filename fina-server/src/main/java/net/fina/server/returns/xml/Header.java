package net.fina.server.returns.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Jaxb Class for xml file header
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Header", propOrder = { "bankCode", "bankName", "returnCode", "returnName", "periodFrom", "periodEnd", "ver", "signed", "lng" })
public class Header implements Cloneable {
	@XmlElement(name = "BANKCODE")
	private String bankCode;
	@XmlElement(name = "BANKNAME")
	private String bankName;
	@XmlElement(name = "RETURNCODE")
	private String returnCode;
	@XmlElement(name = "RETURNNAME")
	private String returnName;
	@XmlElement(name = "PERIODFROM")
	private String periodFrom;
	@XmlElement(name = "PERIODEND")
	private String periodEnd;
	@XmlElement(name = "VER")
	private String ver;
	@XmlElement(name = "SIGNED")
	private String signed;
	@XmlElement(name = "LNG")
	private String lng;

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnName() {
		return returnName;
	}

	public void setReturnName(String returnName) {
		this.returnName = returnName;
	}

	public String getPeriodFrom() {
		return periodFrom;
	}

	public void setPeriodFrom(String periodFrom) {
		this.periodFrom = periodFrom;
	}

	public String getPeriodEnd() {
		return periodEnd;
	}

	public void setPeriodEnd(String periodEnd) {
		this.periodEnd = periodEnd;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getSigned() {
		return signed;
	}

	public void setSigned(String signed) {
		this.signed = signed;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	@Override
	public String toString() {
		return "Header [bankCode=" + bankCode + ", bankName=" + bankName + ", returnCode=" + returnCode + ", returnName=" + returnName + ", periodFrom=" + periodFrom + ", periodEnd=" + periodEnd
				+ ", ver=" + ver + ", signed=" + signed + ", lng=" + lng + "]";
	}

    @Override
    public Header clone() {
        try {
           return (Header) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
