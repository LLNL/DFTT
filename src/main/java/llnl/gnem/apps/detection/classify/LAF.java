/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.classify;

import java.util.List;

/**
 *
 * @author dodge1
 */
public interface LAF {
    public static enum Status {

        valid, invalid,unset
    }
    
    
    public List<String> getAttributeList();
    public List<Double> getValues();
    public Status getLabel();
}
