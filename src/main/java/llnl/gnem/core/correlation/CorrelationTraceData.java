package llnl.gnem.core.correlation;

import llnl.gnem.core.correlation.util.NominalArrival;
import llnl.gnem.core.util.SeriesMath;
import llnl.gnem.core.waveform.BaseTraceData;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;
import llnl.gnem.core.waveform.responseProcessing.WaveformDataType;
import llnl.gnem.core.waveform.responseProcessing.WaveformDataUnits;

/**
 *
 * @author dodge1
 */
public class CorrelationTraceData extends BaseTraceData {

    private NominalArrival referenceArrival;

    public CorrelationTraceData(CssSeismogram seismogram, WaveformDataType dataType,
            WaveformDataUnits dataUnits, NominalArrival arrival) {
        super(seismogram, dataType, dataUnits);
        referenceArrival = arrival;
    }

    public CorrelationTraceData(CorrelationTraceData other) {
        super(other);
        this.referenceArrival = other.referenceArrival;
    }

    public NominalArrival getNominalPick() {
        return referenceArrival;
    }
    
    public void replaceNominalPick(NominalArrival arrival){
        referenceArrival = new NominalArrival(arrival);
    }

    @Override
    public CorrelationTraceData newCopy() {
        return new CorrelationTraceData(this);
    }

    public float[] getScaledData(double rmin, double rmax) {
        float[] result = this.getBackupSeismogram().getData();
        scaleArray(rmax, rmin, result);

        return result;
    }

    private void scaleArray(double rmax, double rmin, float[] result) {
        double rrange = rmax - rmin;
        double max = SeriesMath.getMax(result);
        double min = SeriesMath.getMin(result);
        double range = max - min;
        range /= rrange;
        for (int j = 0; j < result.length; ++j) {
            result[j] /= range;
        }
    }

    public void addSeismogram(CssSeismogram seis) {
        this.getBackupSeismogram().addInPlace(seis);
    }

    public void scaleTo(double min, double max) {
        this.getBackupSeismogram().scaleTo(min,max);
    }
}
