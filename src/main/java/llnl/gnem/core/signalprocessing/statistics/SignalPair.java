/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.signalprocessing.statistics;

import llnl.gnem.core.signalprocessing.WindowFunction;
import llnl.gnem.core.util.SeriesMath;

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
