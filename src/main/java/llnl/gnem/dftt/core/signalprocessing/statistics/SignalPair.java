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
package llnl.gnem.dftt.core.signalprocessing.statistics;

import llnl.gnem.dftt.core.signalprocessing.WindowFunction;
import llnl.gnem.dftt.core.util.SeriesMath;

/**
 *
 * @author dodge1
 */
public class SignalPair {

    private final float[] data1;
    private final float[] data2;
    private final double dt;

    public SignalPair(float[] data1, float[] data2, double dt) {
        this.data1 = data1.clone();
        SeriesMath.RemoveMean(this.data1);
        this.data2 = data2.clone();
        SeriesMath.RemoveMean(this.data2);
        this.dt = dt;
    }

    /**
     * @return the data1
     */
    protected float[] getData1() {
        return data1;
    }

    /**
     * @return the data2
     */
    protected float[] getData2() {
        return data2;
    }

    /**
     * @return the dt
     */
    protected double getDt() {
        return dt;
    }

    public void applyWindowFunction(WindowFunction.WindowType windowType, double pct) {
        if(windowType == WindowFunction.WindowType.TUKEY){
            WindowFunction.setTukeyCoeff((float)pct);
        }
        WindowFunction.applyWindow(data1, windowType);
        WindowFunction.applyWindow(data2, windowType);
    }
    
}
