package llnl.gnem.core.gui.plotting.jmultiaxisplot;

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



