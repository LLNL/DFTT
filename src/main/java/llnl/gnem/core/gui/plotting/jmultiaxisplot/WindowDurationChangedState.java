package llnl.gnem.core.gui.plotting.jmultiaxisplot;

/**
 * Created by: dodge1 Date: Dec 3, 2004 COPYRIGHT NOTICE GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
public class WindowDurationChangedState {
    private final JSubplot sp;
    private final JWindowHandle handle;
    private final double startDuration;
    private double deltaD;

    public WindowDurationChangedState(JWindowHandle handle, JSubplot sp, double start) {
        this.sp = sp;
        this.handle = handle;
        this.startDuration = start;
        this.deltaD = 0.0;
    }

    public JWindowHandle getWindowHandle() {
        return handle;
    }

    public JSubplot getSubplot() {
        return sp;
    }

    public void finishChange() {
        deltaD = handle.getAssociatedPick().getWindow().getDuration() - startDuration;
    }

    public double getDeltaD() {
        return deltaD;
    }
}
