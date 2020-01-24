package llnl.gnem.core.waveform;

import llnl.gnem.core.waveform.responseProcessing.WaveformDataType;
import llnl.gnem.core.waveform.responseProcessing.WaveformDataUnits;
import llnl.gnem.core.waveform.filter.StoredFilter;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;
import java.io.IOException;
import java.sql.SQLException;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.Passband;
import llnl.gnem.core.util.SeriesMath;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.responseProcessing.ResponseType;
import llnl.gnem.core.waveform.responseProcessing.TransferFunctionService;

/**
 *
 * @author dodge1
 */
public class BaseTraceData {

    private static double PICK_STD_ESTIMATION_WINDOW_LENGTH = 0.1;
    private static final int PICK_STD_ESTIMATION_THRESHOLD = 5;
    private CssSeismogram data;
    private CssSeismogram filterBackup;
    private StoredFilter currentFilter;
    private WaveformDataType dataType;
    private WaveformDataUnits dataUnits;
    private static final double NANOMETER_TO_METER = 1.0e-9;

    public static void setPickEstimationWindowLength(double value) {
        PICK_STD_ESTIMATION_WINDOW_LENGTH = value;
    }

    public StreamKey getStreamKey() {
        return data.getStreamKey();
    }

    @Override
    public String toString() {
        return String.format("TraceData with seis: (%s).", data.toString());
    }

    public BaseTraceData(CssSeismogram seismogram,
            WaveformDataType dataType,
            WaveformDataUnits dataUnits) {

        data = new CssSeismogram(seismogram);
        filterBackup = new CssSeismogram(seismogram);
        this.dataType = dataType;
        this.dataUnits = dataUnits;
        currentFilter = null;
    }

    public BaseTraceData(BaseTraceData other) {
        data = new CssSeismogram(other.data);
        filterBackup = new CssSeismogram(other.filterBackup);

        currentFilter = other.currentFilter;
        dataType = other.dataType;
        dataUnits = other.dataUnits;
    }

    public BaseTraceData(CssSeismogram seis, CssSeismogram seisBack,
            WaveformDataType dataType,
            WaveformDataUnits dataUnits,
            StoredFilter currentFilter) {
        data = new CssSeismogram(seis);
        filterBackup = new CssSeismogram(seisBack);

        this.currentFilter = currentFilter;
        this.dataType = dataType;
        this.dataUnits = dataUnits;
    }

    public WaveformDataType getDataType() {
        return dataType;
    }

    public WaveformDataUnits getDataUnits() {
        return dataUnits;
    }

    public void setDataUnits(WaveformDataUnits units) {
        this.dataUnits = units;
    }

    public void setDataType(WaveformDataType type) {
        this.dataType = type;
    }

    public float[] getSegment(double start, double duration) {
        return data.getSubSection(start, duration);
    }

    public double getDelta() {
        return data.getDelta();
    }

    public float[] getPlotData() {
        return data.getData();
    }

    public String getName() {
        return data.getStreamKey().toString();
    }

    public TimeT getTime() {
        return data.getTime();
    }

    public double getSampleRate() {
        return data.getSamprate();
    }

    public int getNsamp() {
        return data.getNsamp();
    }

    /**
     * Apply a cosine taper to the time series of this seismogram
     *
     * @param TaperPercent The (one-sided) percent of the time series to which a
     * taper will be applied. The value ranges from 0 (no taper) to 50 ( The
     * taper extends half the length of the Seismogram ). Since the taper is
     * symmetric, a 50% taper means that all but the center value of the
     * Seismogram will be scaled by some value less than 1.0.
     */
    public void taper(double TaperPercent) {
        data.Taper(TaperPercent);
        filterBackup.Taper(TaperPercent);
    }

    /**
     * Remove a linear trend from the time series data of this Seismogram.
     */
    public void removeTrend() {
        data.removeTrend();
        filterBackup.removeTrend();
    }

    public void undoChangesUsingBackup(BaseTraceData other) {
        data = new CssSeismogram(other.data);
        filterBackup = new CssSeismogram(other.filterBackup);
    }

    /**
     * Gets a float array which is a subsection of the Seismogram's time series.
     * The subsection starts at time start (presumed to be within the
     * Seismogram's time series) and has a length of duration.
     *
     * @param start The starting time relative to event reference time.
     * @param duration The duration in seconds of the subsection.
     * @return The subSection array
     */
    public float[] getSubSection(double start, double duration) {
        return data.getSubSection(start, duration);
    }

    public double estimatePickStdErr(double pickTime) {
        double time = data.getTimeAsDouble();

        if (pickTime <= time) {
            return 1.0;
        }
        double maxWindowLength = PICK_STD_ESTIMATION_WINDOW_LENGTH;
        double preWindowStart = pickTime - maxWindowLength;
        if (preWindowStart < time) {
            preWindowStart = time;
        }

        double duration = pickTime - preWindowStart;

        float[] preWindow = getSubSection(preWindowStart, duration);
        if (preWindow.length < 10) {
            return 1.0;
        }
        double mean = SeriesMath.getMean(preWindow);
        double rms = SeriesMath.getRMS(preWindow);
        double samprate = data.getSamprate();
        int j = (int) Math.round((pickTime - time) * samprate);
        double t = pickTime;
        double dt = 1 / samprate;
        double snr = 0;
        int nsamp = data.getNsamp();
        while (snr < PICK_STD_ESTIMATION_THRESHOLD && t <= pickTime + maxWindowLength && j < nsamp - 1) {
            double value = Math.abs(data.getValueAt(j++) - mean);
            snr = rms > 0 ? value / rms : value;
            t += dt;
        }
        return t - pickTime;
    }

    /**
     * Apply a Butterworth filter to the time series data of this Seismogram.
     *
     * @param order The order of the filter to be applied
     * @param passband The passband of the filter. Passband is one of
     * Passband.LOW_PASS, Passband.HIGH_PASS, Passband.BAND_PASS,
     * Passband.BAND_REJECT
     * @param cutoff1 For BAND_PASS and BAND_REJECT filters this is the low
     * corner of the filter. For HIGH_PASS and LOW_PASS filters, this is the
     * single corner frequency
     * @param cutoff2 For BAND_PASS and BAND_REJECT filters, this is the
     * high-frequency corner. For other filters, this argument is ignored.
     * @param two_pass When true, the filter is applied in both forward and
     * reverse directions to achieve zero-phase.
     */
    public void filter(int order, Passband passband, double cutoff1, double cutoff2, boolean two_pass) {
        data = new CssSeismogram(filterBackup);
        data.filter(order, passband, cutoff1, cutoff2, two_pass);

    }

    public boolean applyFilter(StoredFilter filter) {
        data = new CssSeismogram(filterBackup);
        currentFilter = filter;
        return data.applyFilter(filter);

    }

    public void unApplyFilter() {
        data = new CssSeismogram(filterBackup);
    }

    public long getWfid() {
        return data.getWaveformID();
    }

    public Epoch getEpoch() {
        return data.getEpoch();
    }

    public void trimTo(Epoch epoch) {
        try {
            data.trimTo(epoch);
            filterBackup.trimTo(epoch);
        } catch (Exception ex) {
            String msg = String.format("Failed trimming TraceData (%s) to epoch (%s)", this.toString(), epoch.toString());
            throw new IllegalStateException(ex.getMessage() + ":   " + msg);
        }
    }

    public String getSta() {
        return data.getSta();
    }

    public String getChan() {
        return data.getChan();
    }

    public Double getCalib() {
        return data.getCalib();
    }

    public Double getCalper() {
        return data.getCalper();
    }

    public CssSeismogram getSeismogram() {
        return data;
    }

    public CssSeismogram getBackupSeismogram() {
        return filterBackup;
    }

    public void integrate() {
        data.integrate();
        filterBackup.integrate();
        resetUnitsAndTypeForIntegration();
    }

    private void resetUnitsAndTypeForIntegration() throws IllegalStateException {
        if (null == dataType) {
            throw new IllegalStateException("Cannot integrate data of type: " + dataType);
        } else {
            switch (dataType) {
                case velocity:
                    dataType = WaveformDataType.displacement;
                    if (null == dataUnits) {
                        throw new IllegalStateException("No compatible units available for integrated data!");
                    } else {
                        switch (dataUnits) {
                            case cmpersec:
                                dataUnits = WaveformDataUnits.cm;
                                break;
                            case mpersec:
                                dataUnits = WaveformDataUnits.m;
                                break;
                            default:
                                throw new IllegalStateException("No compatible units available for integrated data!");
                        }
                    }
                    break;
                case acceleration:
                    dataType = WaveformDataType.velocity;
                    if (null == dataUnits) {
                        throw new IllegalStateException("No compatible units available for integrated data!");
                    } else {
                        switch (dataUnits) {
                            case cmpersec2:
                                dataUnits = WaveformDataUnits.cmpersec;
                                break;
                            case mpersec2:
                                dataUnits = WaveformDataUnits.mpersec;
                                break;
                            default:
                                throw new IllegalStateException("No compatible units available for integrated data!");
                        }
                    }
                    break;
                case counts:
                    break;
                default:
                    throw new IllegalStateException("Cannot integrate data of type: " + dataType);
            }
        }
    }

    public void differentiate() {
        data.differentiate();
        filterBackup.differentiate();
        resetUnitsAndTypeForDifferentiation();
    }

    private void resetUnitsAndTypeForDifferentiation() {
        if (null == dataType) {
            throw new IllegalStateException("Cannot differentiate data of type: " + dataType);
        } else {
            switch (dataType) {
                case velocity:
                    dataType = WaveformDataType.acceleration;
                    if (null == dataUnits) {
                        throw new IllegalStateException("No compatible units available for differentiated data!");
                    } else {
                        switch (dataUnits) {
                            case cmpersec:
                                dataUnits = WaveformDataUnits.cmpersec2;
                                break;
                            case mpersec:
                                dataUnits = WaveformDataUnits.mpersec2;
                                break;
                            default:
                                throw new IllegalStateException("No compatible units available for differentiated data!");
                        }
                    }
                    break;
                case displacement:
                    dataType = WaveformDataType.velocity;
                    if (null == dataUnits) {
                        throw new IllegalStateException("No compatible units available for differentiated data!");
                    } else {
                        switch (dataUnits) {
                            case cm:
                                dataUnits = WaveformDataUnits.cmpersec;
                                break;
                            case m:
                                dataUnits = WaveformDataUnits.mpersec;
                                break;
                            default:
                                throw new IllegalStateException("No compatible units available for differentiated data!");
                        }
                    }
                    break;
                case counts:
                    break;
                default:
                    throw new IllegalStateException("Cannot differentiate data of type: " + dataType);
            }
        }
    }

    public boolean canIntegrate() {
        return dataUnits == WaveformDataUnits.unknown
                || dataType == WaveformDataType.acceleration || dataType == WaveformDataType.velocity;
    }

    public boolean canDifferentiate() {
        return dataUnits == WaveformDataUnits.unknown
                || dataType == WaveformDataType.velocity || dataType == WaveformDataType.displacement;
    }

    public StoredFilter getCurrentFilter() {
        return currentFilter;
    }

    public boolean canRemoveInstrumentResponse() {
        return dataType == WaveformDataType.counts;
    }

    public BaseTraceData newCopy() {
        return new BaseTraceData(this);
    }

    public boolean isBad() {
        return false;
    }

    public long getIdentifier() {
        return data.getWaveformID();
    }

    public void resample(double newRate) {
        data.resample(newRate);
        filterBackup.resample(newRate);
    }

    public void convertToType(WaveformDataType newType) throws SQLException, IOException {
        if (dataType == newType) {
            return;
        }
        switch (dataType) {
            case counts:
                transformFromCounts(newType);
                break;
            case velocity: {
                transformFromVelocity(newType);
                break;
            }
            case acceleration:
                transformFromAcceleration(newType);
                break;
            case displacement:
                transformFromDisplacement(newType);
                break;
            default: {
                throw new IllegalStateException("Do not know how to convert to " + newType);
            }
        }
        dataType = newType;
    }

    private void transformFromVelocity(WaveformDataType newType) {
        switch (newType) {
            case acceleration: {
                data.differentiate();
                filterBackup.differentiate();
                dataUnits = WaveformDataUnits.changeForDifferentiation(dataUnits);
                break;
            }
            case displacement: {
                data.integrate();
                filterBackup.integrate();
                dataUnits = WaveformDataUnits.changeForIntegration(dataUnits);
                break;
            }
            case velocity:
                break;
            default:
                throw new IllegalStateException("Unable to transform from velocity to " + newType + "!");
        }

        dataType = newType;
    }

    private void transformFromAcceleration(WaveformDataType newType) {
        switch (newType) {
            case velocity: {
                data.integrate();
                filterBackup.integrate();
                dataUnits = WaveformDataUnits.changeForIntegration(dataUnits);
                break;
            }
            case displacement: {
                data.integrate();
                filterBackup.integrate();
                dataUnits = WaveformDataUnits.changeForIntegration(dataUnits);
                data.integrate();
                filterBackup.integrate();
                dataUnits = WaveformDataUnits.changeForIntegration(dataUnits);
                break;
            }
            case acceleration:
                break;
            default:
                throw new IllegalStateException("Unable to transform from acceleration to " + newType + "!");
        }
        dataType = newType;
    }

    private void transformFromDisplacement(WaveformDataType newType) {
        switch (newType) {
            case velocity: {
                data.differentiate();
                filterBackup.differentiate();
                dataUnits = WaveformDataUnits.changeForDifferentiation(dataUnits);
                break;
            }
            case acceleration: {
                data.differentiate();
                filterBackup.differentiate();
                dataUnits = WaveformDataUnits.changeForDifferentiation(dataUnits);
                data.differentiate();
                filterBackup.differentiate();
                dataUnits = WaveformDataUnits.changeForDifferentiation(dataUnits);
                break;
            }
            case displacement:
                break;
            default:
                throw new IllegalStateException("Unable to transform from displacement to " + newType + "!");
        }
        dataType = newType;
    }

    private void transformFromCounts(WaveformDataType newType) throws SQLException, IOException {
        switch (newType) {
            case velocity:
                TransferFunctionService.getInstance().transfer(data, ResponseType.DIS);
                data.MultiplyScalar(NANOMETER_TO_METER);
                data.differentiate();
                TransferFunctionService.getInstance().transfer(filterBackup, ResponseType.DIS);
                filterBackup.MultiplyScalar(NANOMETER_TO_METER);
                filterBackup.differentiate();
                dataUnits = WaveformDataUnits.mpersec;
                break;
            case acceleration:
                TransferFunctionService.getInstance().transfer(data, ResponseType.DIS);
                data.MultiplyScalar(NANOMETER_TO_METER);
                data.differentiate();
                data.differentiate();
                TransferFunctionService.getInstance().transfer(filterBackup, ResponseType.DIS);
                filterBackup.MultiplyScalar(NANOMETER_TO_METER);
                filterBackup.differentiate();
                filterBackup.differentiate();
                dataUnits = WaveformDataUnits.mpersec2;
                break;
            case displacement:
                TransferFunctionService.getInstance().transfer(data, ResponseType.DIS);
                data.MultiplyScalar(NANOMETER_TO_METER);
                TransferFunctionService.getInstance().transfer(filterBackup, ResponseType.DIS);
                filterBackup.MultiplyScalar(NANOMETER_TO_METER);
                dataUnits = WaveformDataUnits.m;
                break;
            case pressure:
                TransferFunctionService.getInstance().transfer(data, ResponseType.DIS);
                TransferFunctionService.getInstance().transfer(filterBackup, ResponseType.DIS);
                dataUnits = WaveformDataUnits.Pa;
        }

    }

    public void removeMean() {
        data.RemoveMean();
        filterBackup.RemoveMean();
    }

    public String getShortName() {
        return data.getStreamKey().getShortName();
    }
}
