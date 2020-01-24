/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.gaps;

import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author dodge1
 */
@ThreadSafe
public class PrePostParamPair {

    private final Param pre;
    private final Param post;

    public PrePostParamPair(Param pre, Param post) {
        this.pre = pre;
        this.post = post;
    }

    @Override
    public String toString() {
        return "PrePostParamPair{" + "pre=" + pre + ", post=" + post + '}';
    }

    public Param getPre() {
        return pre;
    }

    public Param getPost() {
        return post;
    }
    
    public double getShiftSignificance()
    {
        double meanDifference = Math.abs(post.getMean() - pre.getMean());
        double avgStd = (pre.getStd() + post.getStd())/2;
        return avgStd > 0 ? meanDifference/avgStd : meanDifference;
    }

    public Param getWeightedAvgParam(int start, int end, int idx) {
        double length = end - start;
        double current = idx - start;
        double wgt1 = (length - current) / length;
        double wgt2 = current / length;
        double newMean = wgt1 * pre.getMean() + wgt2 * post.getMean();
        double newStd = wgt1 * pre.getStd() + wgt2 * post.getStd();
        return new Param(newMean, newStd);
    }
}
