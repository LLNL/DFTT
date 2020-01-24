package llnl.gnem.core.gui.plotting.jmultiaxisplot;

/**
 * Created by: dodge1
 * Date: Dec 10, 2004
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
public class PickErrorChangeState {
    private JSubplot sp;
    private VPickLine vpl;
    private double initialStd;

    public PickErrorChangeState( VPickLine vpl, JSubplot sp, double initialStd )
    {
        this.sp = sp;
        this.vpl = vpl;
        this.initialStd = initialStd;
    }

    public VPickLine getPickLine()
    {
        return vpl;
    }

    public JSubplot getSubplot()
    {
        return sp;
    }

    public double getDeltaStd()
    {
        return vpl.getStd() - initialStd;
    }
}
