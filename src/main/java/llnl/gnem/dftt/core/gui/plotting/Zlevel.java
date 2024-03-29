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
package llnl.gnem.dftt.core.gui.plotting;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Vector;

import llnl.gnem.dftt.core.gui.plotting.plotobject.BasicText;
import llnl.gnem.dftt.core.gui.plotting.plotobject.Line;
import llnl.gnem.dftt.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.dftt.core.gui.plotting.plotobject.Symbol;

/**
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2005 Lawrence Livermore National Laboratory.
 * User: dodge1
 * Date: Feb 14, 2006
 */
public class Zlevel {
    private ArrayList<PlotObject> objects;
    private boolean selectable = true;
    private boolean visible = true;

    public Zlevel()
    {
        objects = new ArrayList<>();
    }

    public synchronized void clear()
    {
        objects.clear();
    }

    public synchronized void add( PlotObject obj )
    {
        objects.add( obj );
    }

    public synchronized boolean remove( PlotObject obj )
    {
        return objects.remove( obj );
    }

    public synchronized ArrayList<PlotObject> getObjects()
    {
        return objects;
    }

    public boolean isSelectable()
    {
        return selectable;
    }

    public void setSelectable( boolean selectable )
    {
        this.selectable = selectable;
    }

    public boolean isVisible()
    {
        return visible;
    }

    public void setVisible( boolean visible )
    {
        this.visible = visible;
    }

    public synchronized PlotObject getHotObject( int x, int y )
    {
        if( isSelectable() ){  // If this level is selectable...
            for ( ListIterator<PlotObject> i = objects.listIterator( objects.size() ); i.hasPrevious(); ){
                PlotObject obj = i.previous();
                if( obj.isSelectable() && obj.PointInside( x, y ) )
                    return obj;
                else{
                    PlotObject po = obj.getSubObjectContainingPoint( x, y );
                    if( po != null )
                        return po;
                }
            }
            return null;
        }
        else
            return null;

    }

    public synchronized void setLevelSymbolAlpha( int alpha )
    {
        for ( ListIterator<PlotObject> i = objects.listIterator( objects.size() ); i.hasPrevious(); ){
            PlotObject obj = i.previous();
            if( obj instanceof Symbol ){
                Symbol s = (Symbol) obj;
                s.setAlpha( alpha );
            }
        }
    }


    public synchronized int getLineCount()
    {
        int count = 0;

        for (Object object : objects) {
            PlotObject obj = (PlotObject) object;
            if (obj instanceof Line)
                ++count;
        }
        return count;
    }

    public synchronized ArrayList<Line> getLines()
    {
        ArrayList<Line> result = new ArrayList<>();
        for (Object object : objects) {
            PlotObject obj = (PlotObject) object;
            if (obj instanceof Line)
                result.add((Line) obj);
        }
        return result;
    }

    public synchronized void setPolyLineUsage( boolean value )
    {
        for (Object object : objects) {
            PlotObject obj = (PlotObject) object;
            if (obj instanceof Line) {
                Line l = (Line) obj;
                l.setPolylineUsage(value);
            }
        }
    }


    public synchronized void clearSelectionRegions()
    {
        for (Object object : objects) {
            PlotObject obj = (PlotObject) object;
            obj.clearSelectionRegion();
        }
    }


    synchronized void renderVisiblePlotObjects( Graphics g, JBasicPlot owner )
    {
        for ( Object o : objects ){
            PlotObject obj = (PlotObject) o;
            obj.render( g, owner );
        }
    }

    public synchronized void clearText()
    {
        Iterator<PlotObject> it = objects.iterator();
        while( it.hasNext() ){
            PlotObject obj = it.next();
            if( obj instanceof BasicText ){
                it.remove();
            }
        }
    }

    public synchronized boolean contains( PlotObject po )
    {
        for ( Object o : objects ){
            PlotObject obj = (PlotObject) o;
            if( obj == po )
                return true;
        }
        return false;
    }

    public synchronized ArrayList<PlotObject> getVisiblePlotObjects()
    {
        ArrayList<PlotObject> result = new ArrayList<>();
        for ( Object o : objects ){
            PlotObject obj = (PlotObject) o;
            if( obj.isVisible() )
                //noinspection unchecked
                result.add( obj );
        }
        return result;
    }

}
