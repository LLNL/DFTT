package llnl.gnem.core.traveltime;

/**
 * Created by: dodge1
 * Date: Dec 13, 2004
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
public class PhaseTime {
    public String phase;
    public double time;

    public PhaseTime( String phase, double time)
    {
        this.phase = phase;
        this.time = time;
    }
}
