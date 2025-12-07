package net.fina.common.client.returns;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class ReturnsStatisticFilterConfig implements Serializable{
	private Date statusPeriodFrom;
	private Date statisPeriodTo;

	public Date getStatusPeriodFrom() {
		return statusPeriodFrom;
	}

	public void setStatusPeriodFrom(Date statusPeriodFrom) {
		this.statusPeriodFrom = statusPeriodFrom;
	}

	public Date getStatisPeriodTo() {
		return statisPeriodTo;
	}

	public void setStatisPeriodTo(Date statisPeriodTo) {
		this.statisPeriodTo = statisPeriodTo;
	}

}
