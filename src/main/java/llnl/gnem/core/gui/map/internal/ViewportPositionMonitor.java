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
package llnl.gnem.core.gui.map.internal;

import java.util.logging.Level;

import llnl.gnem.core.gui.map.ViewPort;
import llnl.gnem.core.util.ApplicationLogger;

/**
 *
 * @author dodge1
 */
public class ViewportPositionMonitor {
    public static final int MAX_LEVELS = 15;

    private int elevationLevel;
    private final double[] levelArray;
    private ViewPort zoomStartViewPort;
    private ViewPort zoomEndViewPort;
    private ViewportPositionMonitor() {
        levelArray = new double[MAX_LEVELS];
        for( int j = 0; j < MAX_LEVELS; ++j){
            double v = j;
            levelArray[j] = Math.pow(2.0, v);
//            ApplicationLogger.getInstance().log(Level.FINE, "elevation("+levelArray[j]+") level("+j+")");
        }
        elevationLevel = MAX_LEVELS-1;
        zoomStartViewPort = null;
    }

    public static ViewportPositionMonitor getInstance() {
        return ViewportPositionMonitorHolder.instance;
    }

    public double getApproxElevation(int level) {
    	level  = Math.max(Math.min(MAX_LEVELS-1, level), 0);
    	return levelArray[level] + 0.01;
    }

    public int getElevationLevel(double elev) {
        for( int j = 0; j < MAX_LEVELS; ++j){
            if(elev <= levelArray[j])
                return j;
        }
        return MAX_LEVELS-1;
    }

    double getCombineDistKm() {
    	// Ensure that we never combine at lowest elevation level
    	if (elevationLevel==0) {
    		return -1.0;
    	}
    	// 100 km times 1 over the inverse of the elevation level
    	return 100.0 * ( 1.0 / ( MAX_LEVELS - elevationLevel));
    }

    public int getCurrentElevationLevel() {
    	return elevationLevel;
    }

    public ViewPort getCurrentViewport() {
        return zoomEndViewPort;
    }

    private static class ViewportPositionMonitorHolder {

        private static final ViewportPositionMonitor instance = new ViewportPositionMonitor();
    }

    public boolean notifyViewportChange( ViewPort currentViewPort)
    {
        int level = getElevationLevel(currentViewPort.getEyeElevationKm());
        if( zoomStartViewPort == null ){
            zoomStartViewPort = currentViewPort;
            elevationLevel = level;
            return false;
        }
        else{
            if(level != elevationLevel){
                zoomStartViewPort = null;
                zoomEndViewPort = currentViewPort;
                return true;
            }
            else if( currentViewPort.getHorizontalShiftPercent(zoomStartViewPort) > 10){
                zoomStartViewPort = null;
                zoomEndViewPort = currentViewPort;
                return true;
            }
            else
                return false;
        }
     }
}
