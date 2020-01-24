/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.selectionCriteria;

import llnl.gnem.core.util.Epoch;

/**
 *
 * @author dodge1
 */
public class TimeRestriction extends DoubleRangeRestriction{
    public TimeRestriction(Epoch epoch){
        super(epoch.getStart(), epoch.getEnd(),"time");
    }
    
    public TimeRestriction()
    {
        super(null,null,"time");
    }
    
}
