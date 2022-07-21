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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author dodge1
 */
public class DataCenter {

    private final String location;
    private final String website;
    private final String lastUpdate;
    private final String description;
    private final String name;
    private final List<DataService> services;

    public DataCenter(String location, String website, String lastUpdate, String description, String name, List<DataService> services) {
        this.location = location;
        this.website = website;
        this.lastUpdate = lastUpdate;
        this.description = description;
        this.name = name;
        this.services = new ArrayList<>(services);
    }

    public Collection<String> getServiceList() {
        Collection<String> result = new ArrayList<>();
        for (DataService ds : services) {
            result.add(ds.getServiceName());
        }
        return result;
    }
    
    public String getCompoundDescription()
    {
        return description +", " + location + "; " + website;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @return the website
     */
    public String getWebsite() {
        return website;
    }

    /**
     * @return the lastUpdate
     */
    public String getLastUpdate() {
        return lastUpdate;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the services
     */
    public List<DataService> getServices() {
        return new ArrayList<>(services);
    }

    public boolean hasStationService() {
        for (DataService ds : services) {
            if (ds.getServiceName().equals("stationService")) {
                return true;
            }
        }
        return false;
    }

    public String getStationServiceUrl() {
        for (DataService ds : services) {
            if (ds.getServiceName().equals("stationService")) {
                return ds.getUrl();
            }
        }
        return null;
    }

    public boolean hasWaveformService() {
        for (DataService ds : services) {
            if( ds.getServiceName().equals("dataselectService")){
                return true;
            }
        }
        return false;
    }

    public String getWaveformServiceUrl() {
        for (DataService ds : services) {
            if (ds.getServiceName().equals("dataselectService")) {
                return ds.getUrl();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "DataCenter [name=" + name + ", location=" + location + ", lastUpdate=" + lastUpdate + ", website=" + website + ", description="
                        + description + ", services=" + services + "]";
    }
    
    
}
