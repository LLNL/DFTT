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
package llnl.gnem.core.database.login;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * User: dodge1
 * Date: Jun 20, 2005
 * Time: 7:44:08 AM
 */
@XmlTransient
public abstract class DbServiceInfo implements Serializable {

    // Version 1.0.0 variables
	@XmlElement(required=false, namespace = "http://www.llnl.gov/gnemcore/1.0.0")
	private String serverName;
	@XmlElement(required=false, namespace = "http://www.llnl.gov/gnemcore/1.0.0")
	private Integer portNumber;
    @XmlElement(required=false, namespace = "http://www.llnl.gov/gnemcore/1.0.0")
    private String domain;

    // Elements used by version 1.0.0 and 1.0.1
	@XmlElement(required=true, namespace = "http://www.llnl.gov/gnemcore/1.0.0")
	private String serviceId;	
	@XmlElement(required=false, namespace = "http://www.llnl.gov/gnemcore/1.0.0")
	private Boolean isSensitiveService;
	
	// Element used by 1.0.1
    @XmlElement(required=false, namespace = "http://www.llnl.gov/gnemcore/1.0.0")
    private String connectionDescriptor;
    
    static final long serialVersionUID = 1L;

    public DbServiceInfo() {
    	super();
    }
    
    public DbServiceInfo(String serviceId, String connectionDescriptor, boolean isSensitiveService) {
        super();
        this.serviceId = serviceId;
        this.connectionDescriptor = connectionDescriptor;
        this.isSensitiveService = isSensitiveService;
    }
    
	public DbServiceInfo(String serverName, int portNumber, String serviceId, String domain,
			boolean isSensitiveService) {
		super();
		this.serverName = serverName;
		this.portNumber = portNumber;
		this.serviceId = serviceId;
		this.domain = domain;
		this.isSensitiveService = isSensitiveService;
	}
    

    public abstract Connection initializeConnection( String login, String password ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException;
    public abstract String getConnSpec();

    public String getServerName()
    {
        return serverName;
    }

    public int getPortNumber()
    {
        return portNumber;
    }

    public String getServiceId()
    {
        return serviceId;
    }

    public boolean isSensitiveService()
    {
        return isSensitiveService;
    }
    
    public String getDomain() {
        return domain;
    }

    public String getConnectionDescriptor() {
        return connectionDescriptor;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbServiceInfo other = (DbServiceInfo) o;

        if (isSensitiveService != other.isSensitiveService) return false;
        if (portNumber != other.portNumber) return false;
        if (serverName != null ? !serverName.equals(other.serverName) : other.serverName != null) return false;
        if (serviceId != null ? !serviceId.equals(other.serviceId) : other.serviceId != null) return false;
        if (connectionDescriptor != null ? !connectionDescriptor.equals(other.connectionDescriptor) : other.connectionDescriptor != null) return false;
        
        return true;
    }

    @Override
    public int hashCode() {
        int result = serverName != null ? serverName.hashCode() : 0;
        result = 31 * result + portNumber;
        result = 31 * result + (serviceId != null ? serviceId.hashCode() : 0);
        result = 31 * result + (connectionDescriptor != null ? connectionDescriptor.hashCode() : 0);
        result = 31 * result + (isSensitiveService ? 1 : 0);
        return result;
    }

    @Override
    public String toString()
 {
        StringBuilder sb = new StringBuilder();
        if (connectionDescriptor == null) {
            sb.append(serviceId + " on " + serverName).append(" (Port ").append(portNumber).append(')');
        } else {
            sb.append(serviceId).append(" = ").append(connectionDescriptor);
        }
        return sb.toString();
    }
}
