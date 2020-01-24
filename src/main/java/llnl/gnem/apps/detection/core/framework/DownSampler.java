package llnl.gnem.apps.detection.core.framework;

import com.oregondsp.signalProcessing.filter.iir.Butterworth;
import com.oregondsp.signalProcessing.filter.iir.IIRFilter;
import com.oregondsp.signalProcessing.filter.iir.PassbandType;
import java.util.ArrayList;
import java.util.List;
import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;

import llnl.gnem.core.signalprocessing.Sequence;
import llnl.gnem.apps.detection.core.dataObjects.StreamSegment;
import llnl.gnem.apps.detection.core.dataObjects.WaveformSegment;
import llnl.gnem.core.waveform.qc.DataDefect;

public class DownSampler {

    private final int numChannels;
//    private final int blockSize;

    private final IIRFilter[] filters;
    private final IIRFilter filter;
    private final int order;

    private final int decimationRate;
    private final int decimatedBlockSize;

    public DownSampler(PreprocessorParams params,
            int numChannels,
            double samplingRate) {

        this.numChannels = numChannels;
        this.decimatedBlockSize = params.getDecimatedDataBlockSize();
        this.decimationRate = params.getDecimationRate();
        this.order = params.getPreprocessorFilterOrder();

        //       blockSize = decimatedBlockSize * decimationRate;
        // anti-alias filters
        filters = new IIRFilter[numChannels];
        for (int ich = 0; ich < numChannels; ich++) {
            filters[ich] = new Butterworth(order,
                    PassbandType.BANDPASS,
                    params.getPassBandLowFrequency(),
                    params.getPassBandHighFrequency(),
                    1.0 / samplingRate);
        }

        // Single anti-alias filter for creating templates
        filter = new Butterworth(order,
                PassbandType.BANDPASS,
                params.getPassBandLowFrequency(),
                params.getPassBandHighFrequency(),
                1.0 / samplingRate);

    }

    public StreamSegment process(StreamSegment segment) {

        int nch = segment.getNumChannels();
        if (nch != numChannels) {
            throw new IllegalStateException(String.format("Expected segment with %d channels but got %d instead!", numChannels, nch));
        }

        ArrayList<WaveformSegment> result = new ArrayList<>();
        for (int ich = 0; ich < segment.getNumChannels(); ++ich) {
            WaveformSegment ws = segment.getWaveformSegment(ich);
            List<DataDefect> defects = new ArrayList<>(ws.getDefects());
            float[] tmp = ws.getData();
            filters[ich].filter(tmp);
            float[] decimated = new float[decimatedBlockSize];
            Sequence.decimate(tmp, decimated, decimationRate);
            WaveformSegment wsd = new WaveformSegment(ws.getSta(),
                    ws.getChan(),
                    ws.getTimeAsDouble(),
                    ws.getSamprate() / decimationRate,
                    decimated,
                    defects);
            result.add(wsd);
        }

        return new StreamSegment(result);
    }

    /**
     * method to preprocess master event segments one channel at a time
     *
     * @param x float[] containing one-channel segment
     * @return
     */
    public float[] downSample(float[] x) {

        int n = x.length;
        float[] tmp = new float[n];

        filter.initialize();
        filter.filter(x, tmp);

        int m = n / decimationRate;
        if (n - m * decimationRate > 0) {
            m++;
        }
        float[] retval = new float[m];
        Sequence.decimate(tmp, retval, decimationRate);

        return retval;
    }

}
