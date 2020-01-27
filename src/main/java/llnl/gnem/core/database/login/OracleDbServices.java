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
