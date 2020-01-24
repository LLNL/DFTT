package llnl.gnem.core.gui.plotting.jgeographicplot;

import llnl.gnem.core.gui.plotting.JBasicPlot;
import llnl.gnem.core.gui.plotting.plotobject.Line;

import java.awt.*;
import java.util.Vector;


/**
 * A class to display latitude and longitude grid lines on a world map.
 */
public final class GridLines {

    /**
     * Constructor that initializes the gridlines object to have a spacing of 10 degrees and
     * to render each line using 200 points.
     */
    public GridLines()
    {
        initialize( 10, 200 );
    }

    /**
     * Constructor for the GridLines object that allows specification of the spacing
     * and number of points per line.
     *
     * @param degreeSpacing The spacing in degrees between adjacent grid lines
     * @param pointsPerLine The number of points used in rendering each grid line
     */
    public GridLines( int degreeSpacing, int pointsPerLine )
    {
        initialize( degreeSpacing, pointsPerLine );
    }


    private void initialize( int degreeSpacing, int pointsPerLine )
    {
        lonlines = new Vector<LongitudeLine>();
        latlines = new Vector<LatitudeLine>();

        if( degreeSpacing < 1 )
            degreeSpacing = 1;
        if( pointsPerLine < 100 )
            pointsPerLine = 100;

        for ( int j = -180; j < 180; j += degreeSpacing )
            lonlines.add( new LongitudeLine( j, pointsPerLine ) );

        for ( int j = -90; j < 90; j += degreeSpacing ){
            latlines.add( new LatitudeLine( j, pointsPerLine ) );
        }
    }

    void setRenderLimits( double originLat, double originLon, double plotRadius )
    {
        this.originLat = originLat;
        this.originLon = originLon;
        this.plotRadius = plotRadius;
    }

    /**
     * Renders the set of grid lines to the supplied JBasicPlot using the supplied graphics context.
     *
     * @param g    The graphics context to use for rendering
     * @param plot The JBasicPlot on which to render the grid lines.
     */
    public final void Render( Graphics g, JBasicPlot plot )
    {
        for ( LatitudeLine l : latlines ){
            Line line = l.createLine( originLat, originLon, 2 * plotRadius );
            if( line != null )
                line.render( g, plot );
        }

        for ( LongitudeLine l : lonlines ){
            l.render( g, plot );
        }

    }


    private Vector<LatitudeLine> latlines;
    private Vector<LongitudeLine> lonlines;
    private double originLat;
    private double originLon;
    private double plotRadius;
}
