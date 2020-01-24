/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
