package llnl.gnem.core.gui.plotting;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * A type-safe enum class for interpretation of mouse actions
 *
 * @author Doug Dodge
 */
public enum MouseMode {
    SELECT_ZOOM("select_zoom"),  // Left button down marks a selection attempt or start of a zoom
    PAN("pan"),      // Left button down marks start of panning
    PAN2("pan2"), // two-D pan
    ZOOM_ONLY("zoomOnly"),      // Left button down marks start of zoom only
    CONTROL_SELECT("ControlSelect"),  // Indicates that the Control Key was depressed while this mouse action was made.
    SELECT_REGION("RegionSelect"),      /**
 * Indicates that the mouse can only be used for selecting a region. Region selection
 * causes no change to the plot. but any registered observers will be notified of
 * the action, and given the specifications of the selected region.
 */
CREATE_PICK("CreatePick"),
    /**
     * In this mode, the mouse listener will interpret left-mouse clicks as an attempt to create
     * a new pick.
     */
    CREATE_POLYGON("CreatePolygon" );

    private final String name;

    MouseMode(String name)
    {
        this.name = name;
    }

    /**
     * Return a String description of this type.
     *
     * @return The String description
     */
    @Override
    public String toString()
    {
        return name;
    }

}

