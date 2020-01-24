package llnl.gnem.core.gui.plotting.jmultiaxisplot;

/**
 * Created by: dodge1
 * Date: Dec 3, 2004
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
public class PickMovedState {
    private final JSubplot sp;
    private final VPickLine vpl;
    private final double start;

    public PickMovedState( VPickLine vpl, JSubplot sp, double start )
    {
        this.sp = sp;
        this.vpl = vpl;
        this.start = start;
    }

    public VPickLine getPickLine()
    {
        return vpl;
    }

    public JSubplot getSubplot()
    {
        return sp;
    }

    public double getDeltaT()
    {
        return vpl.getXval() - start;
    }
}
