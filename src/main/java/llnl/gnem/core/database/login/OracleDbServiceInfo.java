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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import llnl.gnem.core.database.dbInfo;
import llnl.gnem.core.util.ApplicationLogger;
import oracle.jdbc.driver.OracleDriver;

/**
 * Created by dodge1 Date: Nov 23, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
@XmlType(namespace = "http://www.llnl.gov/gnemcore/1.0.0", propOrder = {})
public class OracleDbServiceInfo extends DbServiceInfo {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@XmlTransient
	private String connSpec;

    public OracleDbServiceInfo() {
    	super();
    }
    
    public OracleDbServiceInfo(String serviceId, String connnectionDescriptor,  boolean isSensitiveService) {
        super(serviceId, connnectionDescriptor, isSensitiveService);
    }
    
    public OracleDbServiceInfo(String serverName, int portNumber, String serviceId, String domain, boolean isSensitiveService) {
        super(serverName, portNumber, serviceId, domain, isSensitiveService);
    }

    @Override
    public Connection initializeConnection(String login, String password) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        dbInfo dbi = dbInfo.getdbInfoInstance();
        DriverManager.registerDriver(new OracleDriver());
        
        // Let's support (2) flavors
        // SERVICE NAME @//host_name:port_number/service_name
        // CONNECTION STRING <what ever is typed in the config file>
        StringBuilder sb = new StringBuilder(dbi.getDriverName() + ":@");
        String connectionDescriptor = getConnectionDescriptor();
        if (connectionDescriptor == null || connectionDescriptor.isEmpty()) {
            // legacy implementation
            sb.append(getServerName());
            sb.append(':');
            sb.append(getPortNumber());
            sb.append('/');
            sb.append(getServiceId());

            String domain = getDomain();
            if (!domain.isEmpty()) {
                sb.append('.');
                sb.append(domain);
            }
            connSpec = sb.toString();
        } else {
            // new implementation
            sb.append(getConnectionDescriptor());
            connSpec = sb.toString();
        }
        
        ApplicationLogger.getInstance().log(Level.FINE, "initializeConnection connSpec: " + connSpec + " for user: " + login);
        try {
            return DriverManager.getConnection(connSpec, login, password);
        } catch (Exception e) {

            String msg = String.format("Failed database login with credentials: (username=%s, spec=%s)", login, connSpec);
            ApplicationLogger.getInstance().log(Level.FINE, msg, e);
            throw new SQLException(msg, e);
        }
    }

    @Override
    public String getConnSpec() {
        return connSpec;
    }
}
