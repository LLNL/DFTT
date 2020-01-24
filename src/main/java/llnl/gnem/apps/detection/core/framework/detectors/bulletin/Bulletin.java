/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.core.framework.detectors.bulletin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.TimeT;

/**
 *
 * @author dodge1
 */
public class Bulletin implements Serializable{
    private static final long serialVersionUID = 1L;

    private final ArrayList<BulletinRecord> records;

    public Bulletin(Collection<BulletinRecord> input) {
        records = new ArrayList<>(input);
    }
    
    public int size()
    {
        return records.size();
    }

    public Collection<BulletinRecord> getBulletinRecords(Epoch epoch) {
        ArrayList<BulletinRecord> result = new ArrayList<>();
        for( BulletinRecord br : records){
            if( epoch.ContainsTime(new TimeT(br.getExpectedPTime()))){
                result.add(br);
                ApplicationLogger.getInstance().log(Level.FINE, br.toString());
            }
        }

        return result;
    }
}
