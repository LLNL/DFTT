/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
