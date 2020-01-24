package llnl.gnem.core.database.login;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.prefs.Preferences;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import llnl.gnem.core.util.ApplicationLogger;

/**
 * User: dodge1 Date: Jun 20, 2005 Time: 7:56:37 AM
 */
@SuppressWarnings({"NonThreadSafeLazyInitialization"})
public class DbServiceInfoManager {
    private static final String LLNL_GNEMCORE_CONFIG_DIR = "llnl.gnemcore.config.dir";
    private static final String DB_CONFIG_FILE = "gnemCoreDbCfg.xml";
	private static DbServiceInfoManager instance;
    private final Map<String, DbServiceInfo> services;
    private DbServiceInfo selectedService;
    private final Preferences prefs;

    public void setServiceBySid(String sid) throws IOException {
        DbServiceInfo service = services.get(sid);

        if (service != null) {
            setSelectedService(service);
        } else // Service not found...
        {
            ApplicationLogger.getInstance().log(Level.FINE, "setServiceBySid() service not found for: " + sid + " in services: " + services);
            throw new IllegalArgumentException("No service for SID: " + sid);
        }
    }

    public static DbServiceInfoManager getInstance() throws IOException, ClassNotFoundException {
        if (instance == null) {
            instance = new DbServiceInfoManager();
        }
        return instance;
    }


    /**
     * Intended to support testing
     */
    protected static void resetInstance() {
        instance = null;
    }

	private DbServiceInfoManager() throws ClassNotFoundException, IOException {
		prefs = Preferences.userNodeForPackage(this.getClass());
		services = new HashMap<>();

		String defaultService = "gmp";

		Map<String, ? extends DbServiceInfo> servicesFromFile = getOracleDbServices();
		if (!servicesFromFile.isEmpty()) {
		    services.putAll(servicesFromFile);
		} else {
			ApplicationLogger.getInstance().log(Level.SEVERE, "Database Service Configuration Information not loaded.");
		}

		DbServiceInfo info = getPreferredInfoObject();

		if (info == null) {
			selectedService = services.get(defaultService);
		} else {
			selectedService = info;
			services.put(info.getServiceId().toLowerCase(), info);
		}
	}

	private Map<String, ? extends DbServiceInfo> getOracleDbServices() {

		Map<String, DbServiceInfo> results = new HashMap<>();

		try {
			URL url = this.getClass().getResource("/oracleDbServices.xsd");
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = sf.newSchema(url);

			JAXBContext jaxbContext = JAXBContext.newInstance(OracleDbServices.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			jaxbUnmarshaller.setSchema(schema);

			OracleDbServices myServices = null;
			String directory = System.getProperty(LLNL_GNEMCORE_CONFIG_DIR);
			if (directory == null) {
				// the Override property is not set, look for the default gnemCoreDbCfg.xml in the classpath
				ApplicationLogger.getInstance().log(Level.INFO,
				        "GnemCore DB Configuration Directory: " + LLNL_GNEMCORE_CONFIG_DIR + " not specified.");
				URL urlData = this.getClass().getResource("/" + DB_CONFIG_FILE);
				myServices = (OracleDbServices) jaxbUnmarshaller.unmarshal(urlData);
			} else {
				String fileName = directory + File.separator + DB_CONFIG_FILE;
				ApplicationLogger.getInstance().log(Level.INFO, "GnemCore DB Configuration File used: " + fileName);
				File file = new File(fileName);

				if (file.exists()) {
					myServices = (OracleDbServices) jaxbUnmarshaller.unmarshal(file);
				} else {
					ApplicationLogger.getInstance().log(Level.SEVERE,
					        "Cannot Locate DB Configuration Override File: " + fileName);
				}
			}

            if (myServices != null) {
                for (DbServiceInfo serviceInfo : myServices.getServices()) {
                    // if domain is null don't add it to the key
                    if (serviceInfo.getDomain() != null) {
                        results.put(serviceInfo.getServiceId() + serviceInfo.getDomain(), serviceInfo);
                    } else {
                        results.put(serviceInfo.getServiceId().toLowerCase(), serviceInfo);
                    }
                }

            }
		} catch (JAXBException e) {
			ApplicationLogger.getInstance().log(Level.SEVERE, "Error processing DB Configuration information. " , e);
		} catch (SAXException e) {
			ApplicationLogger.getInstance().log(Level.SEVERE, "Error processing DB Configuration information. " , e);
		}

		return results;
	}

    private DbServiceInfo getPreferredInfoObject() throws IOException, ClassNotFoundException {
        byte[] defArray = new byte[]{'a', 'b', 'c'};
        byte[] buffer = prefs.getByteArray("preferredService", defArray);
        if (Arrays.equals(defArray, buffer)) {
            return null;
        } else {
            ByteArrayInputStream bais = null;
            ObjectInputStream ois = null;

            try {
                bais = new ByteArrayInputStream(buffer);
                ois = new ObjectInputStream(bais);
                return (DbServiceInfo) ois.readObject();
            } catch (Exception e) {
                return null;
            } finally {
                if (ois != null) {
                    ois.close();
                }
                if (bais != null) {
                    bais.close();
                }
            }
        }
    }

    public Iterator<DbServiceInfo> iterator() {
        return getServices().iterator();
    }

    public DbServiceInfo getSelectedService() {
        return selectedService;
    }

    public void setSelectedService(DbServiceInfo selectedService) throws IOException {
        this.selectedService = selectedService;
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(selectedService);
            byte[] prefServiceAsBytes = baos.toByteArray();
            prefs.putByteArray("preferredService", prefServiceAsBytes);
        } finally {
            if (oos != null) {
                oos.close();
            }
            if (baos != null) {
                baos.close();
            }
        }
    }

    public Collection<DbServiceInfo> getServices() {
        return new ArrayList<>(services.values());
    }

}
