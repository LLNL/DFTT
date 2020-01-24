/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.waveformViewer;

import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.gui.plotting.MouseMode;

/**
 *
 * @author vieceli1
 */
public interface SeismogramViewer {

    public void clear();

    public void dataWereLoaded(boolean b);

    public void updateForFailedCorrelation();

    public void loadClusterResult();

    public void updateForChangedTrace();

    public void setMouseMode(MouseMode mouseMode);

    public void maybeHighlightTrace(CorrelationComponent cc);

    public void adjustWindow(double windowStart, double winLen);

    public void displayAllPicks();
    
    public void clearAllPicks();
    
}
