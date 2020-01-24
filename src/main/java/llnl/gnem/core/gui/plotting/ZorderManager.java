package llnl.gnem.core.gui.plotting;

import java.awt.Graphics;
import java.util.ArrayList;

import llnl.gnem.core.gui.plotting.plotobject.Line;
import llnl.gnem.core.gui.plotting.plotobject.PlotObject;

/**
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2005 Lawrence Livermore National Laboratory.
 * User: dodge1
 * Date: Feb 14, 2006
 */
public class ZorderManager {
    private final ArrayList<Zlevel> levels;

    public ZorderManager()
    {
        levels = new ArrayList<>();
        levels.add( new Zlevel() );
    }

    public void clear()
    {
        for ( Zlevel level : levels )
            level.clear();
        levels.clear();
    }

    public void setLevelVisible( boolean visible, int level )
    {
        if( level < levels.size() )
            levels.get( level ).setVisible( visible );
    }

    public boolean isLevelVisible( int level )
    {
        if( level < levels.size() )
            return levels.get( level ).isVisible();
        else
            return false;
    }

    public void setLevelSymbolAlpha( int alpha, int level )
    {
        if( level < levels.size() )
            levels.get( level ).setLevelSymbolAlpha( alpha );
    }


    public void add( PlotObject obj )
    {
        levels.get( 0 ).add( obj );
    }

    public void add( PlotObject obj, int level )
    {
        while( levels.size() < level + 1 )
            levels.add( new Zlevel() );
        levels.get( level ).add( obj );
    }

    public boolean remove( PlotObject obj )
    {
        for ( Zlevel level : levels ){
            if( level.remove( obj ) )
                return true;
        }
        return false;
    }

    public PlotObject getHotObject( int x, int y )
    {
        // Traverse list from highest zorder to lowest...
        for( int j = levels.size()-1; j >= 0; --j ){
            Zlevel t = levels.get(j);
            if( t.isVisible() ){
                PlotObject po = t.getHotObject( x, y );
                if( po != null )
                    return po;
            }
        }
        return null;
    }


    public PlotObject getHotObject( int x, int y, int level )
    {
        if( level >= 0 && level < levels.size() ){
            return levels.get( level ).getHotObject( x, y );
        }
        else
            return null;
    }

    public int getLineCount()
    {
        int count = 0;
        for ( Zlevel level : levels )
            count += level.getLineCount();
        return count;
    }

    public ArrayList<Line> getLines()
    {
        ArrayList<Line> result = new ArrayList<>();
        for ( Zlevel level : levels )
            result.addAll( level.getLines() );
        return result;
    }

    public void setPolyLineUsage( boolean value )
    {
        for ( Zlevel level : levels )
            level.setPolyLineUsage( value );
    }

    public void clearSelectionRegions()
    {
        for ( Zlevel level : levels )
            level.clearSelectionRegions();
    }

    void renderVisiblePlotObjects( Graphics g, JBasicPlot owner )
    {
        for ( Zlevel level : levels ){
            if( level.isVisible() )
                level.renderVisiblePlotObjects( g, owner );
        }
    }

    public void clearText()
    {
        for ( Zlevel level : levels )
            level.clearText();
    }

    public boolean contains( PlotObject po )
    {
        for ( Zlevel level : levels )
            if( level.contains( po ) )
                return true;
        return false;
    }

    public ArrayList<PlotObject> getVisiblePlotObjects()
    {
        ArrayList<PlotObject> result = new ArrayList<>();
        for ( Zlevel level : levels ){
            if( level.isVisible() )
                //noinspection unchecked
                result.addAll( level.getVisiblePlotObjects() );
        }
        return result;
    }
}
