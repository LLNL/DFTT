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
