/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.fdsn;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author dodge1
 */
public class FedCatalog {
    
    private static List<String> BLACK_LISTED_STATION_SERVICE_SOURCES = new ArrayList<>();
    
    static {
        BLACK_LISTED_STATION_SERVICE_SOURCES.add("IRISPH5");
        BLACK_LISTED_STATION_SERVICE_SOURCES.add("RASPISHAKE");
    }

    private List<DataCenter> dcs;

    public FedCatalog() throws IOException {
        dcs = new ArrayList<>();
 
        URL url = new URL("http://service.iris.edu/irisws/fedcatalog/1/datacenters");
        try (InputStream is = url.openStream()) {
            InputStreamReader isr = new InputStreamReader(is);
            JsonReader reader = new JsonReader(isr);
            while (reader.hasNext()) {

                JsonToken token = reader.peek();
                switch (token) {
                    case BEGIN_ARRAY:
                        reader.beginArray();
                        while (reader.hasNext()) {
                            JsonToken token2 = reader.peek();
                            switch (token2) {
                                case BEGIN_OBJECT:

                                    dcs.addAll(processObject(reader));
                                    break;
                                case END_OBJECT:
                                    System.out.println("end object");
                                    break;
                                default:
                                    System.out.println(token2);
                            }
                        }

                        break;
                    case END_ARRAY:
                        reader.endArray();
                        break;
                }
            }
            //only add the static set one time
            dcs.addAll(getStaticDataCenterDefinitions());
        }
    }
    
    private List<DataCenter> processObject(JsonReader reader) throws IOException {
        ArrayList<DataCenter> result = new ArrayList<>();
        String location = null;
        String website = null;
        String lastUpdate = null;
        String description = null;
        String name = null;

        String tokenName = "";
        reader.beginObject();
        while (reader.hasNext()) {
            JsonToken token3 = reader.peek();
            switch (token3) {
                case NAME:
                    tokenName = reader.nextName();

                    break;
                case STRING: {
                    String val3 = reader.nextString();
                    switch (tokenName) {
                        case "name":
                            name = val3;
                            break;
                        case "location":
                            location = val3;
                            break;
                        case "website":
                            website = val3;
                            break;
                        case "lastUpdate":
                            lastUpdate = val3;
                            break;
                        case "description":
                            description = val3;
                            break;
                    }
                    break;
                }
                case BEGIN_OBJECT:
                    List<DataService> services = processServiceURLs(reader);
                    DataCenter dc = new DataCenter(location, website, lastUpdate, description, name, services);
                    result.add(dc);
                    break;
                default:
                    System.out.println(token3);
                    break;
            }
        }
        reader.endObject();
        return result;
    }

    public static FedCatalog makeFromList() {
        List<DataCenter> dcs = new ArrayList<>();
        String location = "North America";
        String website = "http://service.iris.edu";
        String lastUpdate = "";
        String description = "IRIS Data Management Center";
        String name = "IRISDMC";
        List<DataService> services = new ArrayList<>();
        services.add(new DataService("waveformService", "http://service.iris.edu/fdsnws/dataselect/1/"));
        services.add(new DataService("stationService", "http://service.iris.edu/fdsnws/station/1/"));
        services.add(new DataService("eventService", "http://service.iris.edu/fdsnws/event/1/"));
        DataCenter dc = new DataCenter(location, website, lastUpdate, description, name, services);
        dcs.add(dc);

        location = "North America";
        website = "http://service.ncedc.org";
        lastUpdate = "";
        description = "Northern California Earthquake Data Center";
        name = "NCEDC";
        services = new ArrayList<>();
        services.add(new DataService("waveformService", "http://service.ncedc.org/fdsnws/dataselect/1/"));
        services.add(new DataService("stationService", "http://service.ncedc.org/fdsnws/station/1/"));
        services.add(new DataService("eventService", "http://service.ncedc.org/fdsnws/event/1/"));
        dc = new DataCenter(location, website, lastUpdate, description, name, services);
        dcs.add(dc);

        location = "Europe";
        website = "http://webservices.rm.ingv.it";
        lastUpdate = "";
        description = "INGC";
        name = "INGV";
        services = new ArrayList<>();
        services.add(new DataService("waveformService", "http://webservices.rm.ingv.it/fdsnws/dataselect/1/"));
        services.add(new DataService("stationService", "http://webservices.rm.ingv.it/fdsnws/station/1/"));
        services.add(new DataService("eventService", "http://webservices.rm.ingv.it/fdsnws/event/1/"));
        dc = new DataCenter(location, website, lastUpdate, description, name, services);
        dcs.add(dc);

        location = "Europe";
        website = "http://geofon.gfz-potsdam.de/";
        lastUpdate = "";
        description = "GEOFON Program, GFZ";
        name = "GEOFON";
        services = new ArrayList<>();
        services.add(new DataService("waveformService", "http://geofon.gfz-potsdam.de/fdsnws/dataselect/1/"));
        services.add(new DataService("stationService", "http://geofon.gfz-potsdam.de/fdsnws/station/1/"));
        dc = new DataCenter(location, website, lastUpdate, description, name, services);
        dcs.add(dc);

        return new FedCatalog(dcs);
    }

    private static List<DataService> buildServiceList(LinkedHashMap lhm) {
        List<DataService> dcs = new ArrayList<>();
        Object urls = lhm.get("serviceURLs");
        if (urls instanceof List) {
            List surls = (List) urls;
            for (Object obj2 : surls) {
                if (obj2 instanceof Map) {
                    Map map = (Map) obj2;
                    Set keys = map.keySet();
                    for (Object obj3 : keys) {
                        String value = (String) map.get(obj3);
                        dcs.add(new DataService((String) obj3, value));
                    }
                }
            }
        } else if (urls instanceof Map) {
            Map urlMap = (Map) urls;
            for (Object obj : urlMap.keySet()) {
                Object value = urlMap.get(obj);
                dcs.add(new DataService((String) obj, (String) value));
            }
        }
        return dcs;
    }

    /**
     * Provide ability to test.
     * @param dcs
     */
    protected FedCatalog(List<DataCenter> dcs) {
        this.dcs = new ArrayList<>(dcs);
    }

    public boolean supports(String source) {
        for (DataCenter dc : dcs) {
            if (dc.getName().equals(source)) {
                return true;
            }
        }
        return false;
    }

    public DataCenter getDataCenter(String source) {
        for (DataCenter dc : dcs) {
            if (dc.getName().equals(source)) {
                return dc;
            }
        }
        return null;
    }

    /**
     * getStationServiceSources returns a List of Data Center names that has a Station Service defined. This includes
     * the list published by IRIS and a static collection defined in this class.
     * 
     * @return
     */
    public Collection<String> getStationServiceSources() {
        Collection<String> result = new ArrayList<>();
        for (DataCenter dc : dcs) {
            if (dc.hasStationService()) {
                result.add(dc.getName());
            }
        }
        return result;
    }
    
    /**
     * getStationServiceSources returns a List of Data Center names that has a Station Service defined. This includes
     * the list published by IRIS and a static collection defined in this class.
     * 
     * If onlyMetadataCapture is set to true, then known Data Centers that have stations services that we do not want to
     * capture additional information about will be pruned.
     * 
     * @param onlyMetadataCapture
     * @return
     */
    public Collection<String> getStationServiceSources(boolean onlyMetadataCapture) {
        Collection<String> result = getStationServiceSources();
        if (onlyMetadataCapture) {
            for (String blackListed : BLACK_LISTED_STATION_SERVICE_SOURCES) {
                result.remove(blackListed);
            }
        }

        return result;
    }

    private List<DataService> processServiceURLs(JsonReader reader) throws IOException {
        List<DataService> services = new ArrayList<>();
        String serviceName = null;
        String url = null;
        reader.beginObject();
        while (reader.hasNext()) {
            JsonToken token3 = reader.peek();
            switch (token3) {
                case NAME:
                    serviceName = reader.nextName();
                    break;
                case STRING:
                    url = reader.nextString();
                    if (serviceName != null) {
                        services.add(new DataService(serviceName, url));
                        serviceName = null;
                    }
                    break;
            }
        }
        reader.endObject();
        return services;
    }

    private Collection<? extends DataCenter> getStaticDataCenterDefinitions() {
        ArrayList<DataCenter> result = new ArrayList<>();
        String location = "New Zealand";
        String website = "http://info.geonet.org.nz/display/equip/New+Zealand+National+Seismograph+Network";
        String lastUpdate = "";
        String description = "New Zealand National Seismograph Network";
        String name = "NZNSN";
        List<DataService> services = new ArrayList<>();
        services.add(new DataService("stationService", "http://service.geonet.org.nz/fdsnws/station/1/"));
        DataCenter dc = new DataCenter(location, website, lastUpdate, description, name, services);
        result.add(dc);

        location = "Europe";
        website = "https://www.orfeus-eu.org/data/eida/";
        lastUpdate = "";
        description = "European Integrated Data Archives";
        name = "ETHZ";
        services = new ArrayList<>();
        services.add(new DataService("stationService", "http://eida.ethz.ch/fdsnws/station/1/"));
        dc = new DataCenter(location, website, lastUpdate, description, name, services);
        result.add(dc);

        location = "Texas, USA";
        website = "http://www.beg.utexas.edu/texnet-cisr/texnet";
        lastUpdate = "";
        description = "Texas Earthquake Data Center";
        name = "TEXNET";
        services = new ArrayList<>();
        services.add(new DataService("stationService", "http://rtserve.beg.utexas.edu/fdsnws/station/1/"));
        dc = new DataCenter(location, website, lastUpdate, description, name, services);
        result.add(dc);
        return result;
    }

    @Override
    public String toString() {
        return "FedCatalog [dcs=" + dcs + "]";
    }

    public List<DataCenter> getDataCenters() {
        List<DataCenter> results = new ArrayList<>();
        results.addAll(dcs);
        return results;
    }
    
    
}
