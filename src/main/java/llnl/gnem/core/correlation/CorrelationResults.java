package llnl.gnem.core.correlation;

import Jama.Matrix;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.List;

/**
 * Created by dodge1
 * Date: Apr 2, 2009
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class CorrelationResults implements Serializable{

  private static final long serialVersionUID = 1L;

    private final Matrix shifts;
    private final Matrix correlations;
    private final List<StationEventChannelData> data;

    public CorrelationResults(Matrix shifts, Matrix correlations, List<StationEventChannelData> data)
    {
        this.shifts = shifts;
        this.correlations = correlations;
        this.data = ImmutableList.copyOf(data);
    }

    public Matrix getShifts()
    {
        return shifts;
    }

    public Matrix getCorrelations()
    {
        return correlations;
    }

    public List<StationEventChannelData> getData() {
        return data;
    }
    
    public int size()
    {
        return shifts.getColumnDimension();
    }
}
