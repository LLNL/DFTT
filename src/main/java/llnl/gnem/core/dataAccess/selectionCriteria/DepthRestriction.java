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
public class DepthRestriction extends DoubleRangeRestriction {

    public DepthRestriction(double minDepth, double maxDepth) {
        super(minDepth, maxDepth, "depth");
    }

    public DepthRestriction() {
        super(null, null, "depth");
    }
}
