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
