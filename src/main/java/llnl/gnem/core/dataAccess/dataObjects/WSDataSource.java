/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.dataObjects;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author dodge1
 */
public class WSDataSource {

	private final int dataServiceId;
	private final String sourceCode;
	private final int sourceId;
	private final String serviceURL;
	private final WSServiceType serviceType;
	private final Date lastUpdate;
	private final String format;
	private final String queryEventString;
	private final String queryParamString;

	public WSDataSource(String sourceCode, int sourceId, String serviceURL, WSServiceType serviceType) {
		this.sourceCode = sourceCode;
		this.sourceId = sourceId;
		this.serviceURL = serviceURL;
		this.serviceType = serviceType;
		this.lastUpdate = null;
		this.format = "";
		this.queryEventString = "";
		this.queryParamString = "";
		this.dataServiceId = -1;
	}

	public WSDataSource(String sourceCode, int sourceId, String serviceURL, 
                WSServiceType serviceType, Date lastUpdate, String format, 
                String queryEventString, String queryParamString,
			int dataServiceId) {
		this.sourceCode = sourceCode;
		this.sourceId = sourceId;
		this.serviceURL = serviceURL;
		this.serviceType = serviceType;
		this.lastUpdate = lastUpdate;
		this.format = format;
		this.queryEventString = queryEventString;
		this.queryParamString = queryParamString;
		this.dataServiceId = dataServiceId;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public int getSourceId() {
		return sourceId;
	}

	public String getServiceURL() {
		return serviceURL;
	}

	public WSServiceType getServiceType() {
		return serviceType;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public String getFormat() {
		return format;
	}

	public String getQueryEventString() {
		return queryEventString;
	}

	public String getQueryParamString() {
		return queryParamString;
	}

	public int getDataServiceId() {
		return dataServiceId;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 17 * hash + Objects.hashCode(this.sourceCode);
		hash = 17 * hash + this.sourceId;
		hash = 17 * hash + this.dataServiceId;
		hash = 17 * hash + Objects.hashCode(this.serviceURL);
		hash = 17 * hash + Objects.hashCode(this.serviceType);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final WSDataSource other = (WSDataSource) obj;
		if (this.sourceId != other.sourceId) {
			return false;
		}
		if (this.dataServiceId != other.dataServiceId) {
			return false;
		}
		if (!Objects.equals(this.sourceCode, other.sourceCode)) {
			return false;
		}
		if (!Objects.equals(this.serviceURL, other.serviceURL)) {
			return false;
		}
		if (this.serviceType != other.serviceType) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "WSDataSource{" + "sourceCode=" + sourceCode + ", sourceId=" + sourceId + ", serviceURL=" + serviceURL + ", serviceType=" + serviceType + ", serviceType=" + serviceType + ", format="
				+ format + ", last updated=" + lastUpdate + ", Query Event String=" + queryEventString + ", Query Param String =" + queryParamString + '}';
	}
}
