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
package llnl.gnem.core.gui.plotting.jmultiaxisplot;

import java.util.Observable;

import llnl.gnem.core.util.TimeT;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * The PickDataBridge class provides a means of signaling changes in pick data
 * to interested observers. Changes in pick position, window duration, and pick
 * std are all made accessible to observers through this class. Pick times
 * within a PickDataBridge are stored in two parts; a time and a reference
 * time. The time plus the reference time are assumed to be equal to the epoch
 * time of the pick. For plots in which the X-axis has units of epoch time, the
 * reference time is zero. For those plots in which the time has been reduced
 * by some reference time, the PickDataBridge class should be constructed with
 * the appropriate reference time.
 *
 * @author Doug Dodge
 */
public class PickDataBridge extends Observable {
    /**
     * Constructor for the PickDataBridge objectzero. assuming a reference time of
     * zero.
     *
     * @param time     The epoch time of the pick
     * @param deltim   The uncertainty of the time in seconds
     * @param duration The duration of any associated window in seconds
     */
    public PickDataBridge( double time, double deltim, double duration )
    {
        super();
        this.time = time;
        this.deltim = deltim;
        this.duration = duration;
        reftime = 0.0;
        dirty = false;
    }

    /**
     * Constructor for the PickDataBridge object assuming a non-zero reference
     * time.
     *
     * @param time     The epoch time of the pick
     * @param deltim   The uncertainty of the time in seconds
     * @param duration The duration of any associated window in seconds
     * @param reftime  The pick reference time in seconds.
     */
    public PickDataBridge( double time, double deltim, double duration, double reftime )
    {
        super();
        this.time = time - reftime;
        this.deltim = deltim;
        this.duration = duration;
        this.reftime = reftime;
        dirty = false;
    }


    /**
     * Sets the dirty attribute of the PickDataBridge object. When read in from the
     * database dirty is false until the window is adjusted. Newly-created picks have
     * dirty set to true.
     *
     * @param v The new dirty value
     */
    public void setDirty( boolean v )
    {
        dirty = v;
    }

    /**
     * Gets the dirty attribute of the PickDataBridge object. When read in from the
     * database dirty is false until the window is adjusted. Newly-created picks have
     * dirty set to true.
     *
     * @return The dirty value
     */
    public boolean getDirty()
    {
        return dirty;
    }


    /**
     * Converts a PickDataBridge from a zero reference time to a non-zero
     * reference time.
     *
     * @param reftime The new reference time to assign to this PickDataBridge
     *                object
     */
    public void makeTimeRelative( double reftime )
    {
        time = getTime() - reftime;
        this.reftime = reftime;
        endTime = time;
    }

    /**
     * Gets the time as an epoch time (time plus reference time).
     *
     * @return The epoch time value
     */
    public double getTime()
    {
        return time + reftime;
    }

    /**
     * Gets the time relative to the reference time. This is the time of the pick
     * as it is displayed in the plot.
     *
     * @return The relative Time value
     */
    public double getRelativeTime()
    {
        return time;
    }

    public void changeReferenceTime( double newRefTime )
    {
        double dt = newRefTime - reftime;
        time -= dt;
        endTime -= dt;
        reftime = newRefTime;
    }

    /**
     * Gets the reference Time of te PickDataBridge object
     *
     * @return The reference Time value
     */
    public double getReferenceTime()
    {
        return reftime;
    }

    /**
     * Gets the deltim ( time uncertainty ) of the PickDataBridge object
     *
     * @return The deltim value
     */
    public double getDeltim()
    {
        return deltim;
    }

    /**
     * Gets the duration in seconds of a window associated with this pick
     *
     * @return The duration value
     */
    public double getDuration()
    {
        return duration;
    }

    public String getInfoString()
    {
        double epochtime = time + reftime;
        StringBuffer sb = new StringBuffer( "Pick at: " );
        sb.append( new TimeT( epochtime ).toString() );
        sb.append( " with std = " + deltim );
        sb.append( " and window duration = " + duration );
        return sb.toString();
    }

    public String toString()
    {
        return getInfoString();
    }

    /**
     * Sets the time of the PickDataBridge object. This method is expected to be
     * called by a JmultiaxisPlot's mouse motion listener in response to dragging
     * of a pick or its associated window.
     *
     * @param time      The new time value
     * @param initiator A reference to the object that was being dragged.
     */
    protected void setTime( double time, Object initiator )
    {
        this.time = time;
        setChanged();
        notifyObservers( initiator );
    }

    /**
     * Sets the deltim of the PickDataBridge object. This method is expected to be
     * called by a JmultiaxisPlot's mouse motion listener in response to dragging
     * of a pick's error bars.
     *
     * @param deltim    The new deltim value
     * @param initiator A reference to the object that was being dragged.
     */
    protected void setDeltim( double deltim, Object initiator )
    {
        this.deltim = deltim;
        setChanged();
        notifyObservers( initiator );
    }

    /**
     * Sets the duration value of the PickDataBridge object. This method is
     * expected to be called by a JmultiaxisPlot's mouse motion listener in
     * response to dragging of a pick or a JWindowHandle.
     *
     * @param duration  The new duration value
     * @param initiator A reference to the object that was being dragged.
     */
    void setDuration( double duration, Object initiator )
    {
        this.duration = duration;
        endTime = time + duration;
        setChanged();
        notifyObservers( initiator );
    }

    public void setTimeNoNotify( double time )
    {
        this.time = time;
    }

    public void setDurationNoNotify( double duration )
    {
        this.duration = duration;
    }

    public void setDeltimNoNotify( double deltim )
    {
        this.deltim = deltim;
    }


    public void setEndTime( double v )
    {
        endTime = v;
    }


    protected double time;
    protected double reftime;
    protected double duration;
    protected double deltim;
    protected boolean dirty;
    protected double endTime;
}

