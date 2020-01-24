package llnl.gnem.core.gui.plotting.plotobject;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;

import llnl.gnem.core.gui.plotting.JBasicPlot;
import llnl.gnem.core.gui.plotting.transforms.Coordinate;
import llnl.gnem.core.gui.plotting.transforms.CoordinateTransform;

/**
 * PcolorLogLinear is a PlotObject that renders a sampled function of two variables
 * using color to represent the dependent variable.  While Pcolor required the sample
 * to be evenly spaced, this implementation will allow for a less evenly spaced rendered sampling.
 * 
 * @see llnl.gnem.core.gui.plotting.plotobject.Pcolor
 * 
 * 
 * @see llnl.gnem.plotting.colormap.Colormap
 * @see llnl.gnem.plotting.colormap.HotColormap
 */
public class PcolorLogLinear extends Pcolor {

	 public PcolorLogLinear(double[][] zValues, double[] hobValues, double[] yieldValues) {
	    super(zValues, hobValues, yieldValues);
    }

	/**
     * Renders this object onto the supplied graphics context owned by owner.
     *
     * @param g     The graphics context to use.
     * @param owner The JBasicPlot that owns this PlotObject.
     */
    @Override
    public void render( Graphics g, JBasicPlot owner )
    {
        if( !isVisible() )
            return;

        region.clear();
        int numXpts = xValues.length;
        int numYpts = yValues.length;

        CoordinateTransform ct = owner.getCoordinateTransform();
        Coordinate coord = new Coordinate( 0, 0 );
        Graphics2D g2d = (Graphics2D) g;
        g2d.clip( owner.getPlotRegion().getRect() );

        int minX = Integer.MAX_VALUE;
        int maxX = - minX;
        int minY = minX;
        int maxY = maxX;
		for (int j = 0; j < numYpts; ++j) {
			double y = yValues[j];

			double ylow;
			double yhigh;
			if (j == 0) {
				ylow = y;
				yhigh = y + (yValues[j + 1] - y) / 2;
			} else if (j + 1 == numYpts) {
				ylow = y - (y - yValues[j - 1]) / 2;
				yhigh = y;
			} else {
				ylow = y - (y - yValues[j - 1]) / 2;
				yhigh = y + (yValues[j + 1] - y) / 2;
			}

           for ( int k = 0; k < numXpts; ++k ){
				double x = xValues[k];
				double xlow;
				double xhigh;

				if (k == 0) {
					xlow = x;
					xhigh = x + (xValues[k + 1] - x) / 2;
				} else if (k + 1 == numXpts) {
					xlow = x - (x - xValues[k - 1]) / 2;
					xhigh = x;
				} else {
					xlow = x - (x - xValues[k - 1]) / 2;
					xhigh = x + (xValues[k + 1] - x) / 2;
				}
                
                GeneralPath path = new GeneralPath();
                coord.setWorldC1( xlow );
                coord.setWorldC2( ylow );
                ct.WorldToPlot( coord );
                path.moveTo( (float) coord.getX(), (float) coord.getY() );
                if( coord.getX() < minX ) minX = (int)coord.getX();
                if( coord.getX() > maxX ) maxX = (int)coord.getX();
                if( coord.getY() < minY ) minY = (int)coord.getY();
                if( coord.getY() > maxY ) maxY = (int)coord.getY();
                coord.setWorldC2( yhigh );
                ct.WorldToPlot( coord );
                path.lineTo( (float) coord.getX(), (float) coord.getY() );
                if( coord.getX() < minX ) minX = (int)coord.getX();
                if( coord.getX() > maxX ) maxX = (int)coord.getX();
                if( coord.getY() < minY ) minY = (int)coord.getY();
                if( coord.getY() > maxY ) maxY = (int)coord.getY();
                coord.setWorldC1( xhigh );
                ct.WorldToPlot( coord );
                path.lineTo( (float) coord.getX(), (float) coord.getY() );
                if( coord.getX() < minX ) minX = (int)coord.getX();
                if( coord.getX() > maxX ) maxX = (int)coord.getX();
                if( coord.getY() < minY ) minY = (int)coord.getY();
                if( coord.getY() > maxY ) maxY = (int)coord.getY();
                coord.setWorldC2( ylow );
                ct.WorldToPlot( coord );
                path.lineTo( (float) coord.getX(), (float) coord.getY() );
                if( coord.getX() < minX ) minX = (int)coord.getX();
                if( coord.getX() > maxX ) maxX = (int)coord.getX();
                if( coord.getY() < minY ) minY = (int)coord.getY();
                if( coord.getY() > maxY ) maxY = (int)coord.getY();
                g2d.setColor( colors[j][k] );
                g2d.fill( path );
            }

        }
        this.addToRegion(new Rectangle(minX,minY,maxX-minX,maxY-minY));
    }
}
