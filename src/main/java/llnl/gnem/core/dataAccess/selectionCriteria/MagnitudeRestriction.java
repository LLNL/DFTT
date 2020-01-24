/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.selectionCriteria;

/**
 *
 * @author dodge1
 */
public class MagnitudeRestriction extends DoubleRangeRestriction{
    public MagnitudeRestriction(double minMag, double maxMag){
        super(minMag,maxMag,"magnitude");
    }
    public MagnitudeRestriction()
    {
        super(null,null,"magnitude");
    }
}
