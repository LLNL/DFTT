package llnl.gnem.core.correlation;

import java.io.Serializable;
import llnl.gnem.core.correlation.util.EventStaInfo;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;

/**
 *
 * @author dodge1
 */
public strictfp class SeismogramData implements Serializable{

    private final CssSeismogram seismogram;
    private final PhaseArrivalWindow window;
    private final EventStaInfo evidWfidDelta;
    

    public SeismogramData(CssSeismogram seismogram,
            PhaseArrivalWindow window,
            EventStaInfo evidWfidDelta) {
        this.seismogram = seismogram;
        this.window = window;
        this.evidWfidDelta = evidWfidDelta;
     
    }

    public CssSeismogram getSeismogram() {
        return seismogram;
    }

    public PhaseArrivalWindow getWindow() {
        return window;
    }

    public EventStaInfo getEvidWfidDelta() {
        return evidWfidDelta;
    }

}
