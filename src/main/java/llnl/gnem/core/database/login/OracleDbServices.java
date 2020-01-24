package llnl.gnem.core.database.login;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(namespace = "http://www.llnl.gov/gnemcore/1.0.0", propOrder = {"services"})
@XmlRootElement(namespace = "http://www.llnl.gov/gnemcore/1.0.0")
@XmlAccessorType(XmlAccessType.FIELD)
public class OracleDbServices {
	@XmlElementWrapper(name = "services", required = true, namespace = "http://www.llnl.gov/gnemcore/1.0.0")
	@XmlElement(name="service", namespace = "http://www.llnl.gov/gnemcore/1.0.0")
	private List<OracleDbServiceInfo> services;
	
	public OracleDbServices() {
		super();
	}
	
	public List<OracleDbServiceInfo> getServices() {
		return services;
	}

	public void setServices(List<OracleDbServiceInfo> services) {
		this.services = services;
	}

	@Override
    public String toString() {
	    return "OracleDbServices [services=" + services + "]";
    }
}
