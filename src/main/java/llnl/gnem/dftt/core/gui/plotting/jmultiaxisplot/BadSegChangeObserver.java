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
package llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot;

import java.util.Observer;
import java.util.Observable;


/*
 *  COPYRIGHT NOTICE
 *  RBAP Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * A class that observes changes to a VPickLine representing a bad segment window.
 * When changes in position or duration occur because of user actions with the mouse,
 * this class is notified and it updates the data bridge linking the VPickLine object
 * with the database row object.
 *
 * @author Doug Dodge
 */
public class BadSegChangeObserver implements Observer {
    /**
     * Constructor for the BadSegChangeObserver object. Takes the VPickLine that is
     * to be listened to.
     *
     * @param windowObject The VPickLine to observe
     */
    public BadSegChangeObserver( VPickLine windowObject )
    {
        this.windowObject = windowObject;
        WindowBridge = windowObject.getDataBridge();
        WindowBridge.addObserver( this );
    }

    /**
     * A convenience method that returns the VPickLine object being observed
     *
     * @return The VPickLine object
     */
    public VPickLine getPick()
    {
        return windowObject;
    }

    /**
     * A convenience method that returns the Data bridge being updated by this observer.
     *
     * @return The dataBridge value
     */
    public PickDataBridge getDataBridge()
    {
        return WindowBridge;
    }

    /**
     * The method called in response to a change in the VPickLine object
     *
     * @param o   The bridge object owned by the VPickLine
     * @param arg The graphical component that initiated the transaction
     */
    public void update( Observable o, Object arg )
    {
        PickDataBridge pd = (PickDataBridge) o;
        pd.setDirty( true );
        WindowBridge.setEndTime( WindowBridge.getTime() + pd.getDuration() );
        if( arg instanceof JWindowHandle ) { // dragging, so update duration of JWindowRegion

            windowObject.getWindow().setDurationNoNotify( pd.getDuration() );
        }
    }

    private VPickLine windowObject = null;
    private PickDataBridge WindowBridge;
}



