package llnl.gnem.core.gui.plotting.plotobject;

import llnl.gnem.core.gui.plotting.JBasicPlot;
import llnl.gnem.core.gui.plotting.JPlotContainer;

import java.awt.*;

/**
 * User: dodge1
 * Date: Jun 8, 2005
 * Time: 1:26:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlotObjectRenderer implements Runnable {
        private PlotObject obj;

        public PlotObjectRenderer( PlotObject obj )
        {
            this.obj = obj;
        }

        public void run()
        {
            JBasicPlot plot = obj.getOwner();
            if( plot != null ){
                JPlotContainer container = plot.getOwner();
                if( container != null ){
                    Graphics g = container.getGraphics();
                    if( g != null )
                        obj.render( g, plot );
                }
            }
        }
    }
