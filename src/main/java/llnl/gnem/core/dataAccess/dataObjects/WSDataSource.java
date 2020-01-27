/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
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
