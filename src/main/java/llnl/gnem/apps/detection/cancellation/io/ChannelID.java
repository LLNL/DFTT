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
package llnl.gnem.apps.detection.cancellation.io;

import com.oregondsp.io.SACHeader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dodge1
 */
public class ChannelID {
  
  private String station;
  private String component;
  private String network;
  private String location;
  
  
  
  public ChannelID( String station, String component ) {
    this.station   = station.trim();
    this.component = component.trim();
    this.network   = null;
    this.location  = null;
  }
  
  
  
  public ChannelID( String station, String component, String network ) {
    this.station   = station.trim();
    this.component = component.trim();
    this.network   = network.trim();
    this.location  = null;
  }
  
  
  
  public ChannelID( String station, String component, String network, String location ) {
    this.station   = station.trim();
    this.component = component.trim();
    this.network   = network != null && !network.isEmpty() ? network.trim() : "--";
    this.location  = location != null && !location.isEmpty() ? location.trim() : "--";
  }  
  
  
  
  public ChannelID( SACHeader header ) {
    station   = header.kstnm.trim();
    component = header.kcmpnm.trim();
    network   = header.knetwk.trim();
    location  = null;
  }
  
  
  
  public String getStation() {
    return station;
  }
  
  
  
  public String getComponent() {
    return component;
  }
  
  
  
  public String getNetwork() {
    return network;
  }
  
  
  
  public String getLocation() {
    return location;
  }
  
  
  @Override
  public boolean equals( Object o ) {
    
    ChannelID other = (ChannelID) o;
    
    boolean retval = true;
    
    if ( !station.equals( other.station ) ) retval = false;
    if ( !component.equals( other.component ) ) retval = false;
    if ( network != null  &&  other.network != null ) {
      if ( !network.equals( other.network ) ) retval = false;
    }
    if ( location != null  &&  other.location != null ) {
      if ( !location.equals( other.location ) ) retval = false;
    }
    
    return retval;
  }
  
  
  
  @Override
  public String toString() {
    return station + "." + component;
  }
  
 
}
