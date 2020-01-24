package llnl.gnem.apps.detection.core.dataObjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import llnl.gnem.core.waveform.qc.DataDefect;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.qc.DataSpike;
import llnl.gnem.core.waveform.qc.DropOut;
import llnl.gnem.core.waveform.qc.DropoutDetector;
import llnl.gnem.core.waveform.qc.SpikeProcessor;

public class StreamSegment {

    private final ArrayList<WaveformSegment> waveforms;
    private final Map<StreamKey, WaveformSegment> data;
    private final int size;
    private final double sampleRate;

    public StreamSegment(Collection<WaveformSegment> data) {
        waveforms = new ArrayList<>(data);
        validate(waveforms);
        this.data = new ConcurrentHashMap<>();
        for (WaveformSegment seg : waveforms) {
            this.data.put(seg.getStreamKey(), seg);
        }
        size = waveforms.get(0).getLength();
        sampleRate = waveforms.get(0).getSamprate();
    }

    public ArrayList<StreamKey> getChannelKeys() {
        return new ArrayList<>(data.keySet());
    }

    public static void validate(ArrayList<WaveformSegment> data) {
        int nsamp = -1;
        TimeT astart = null;
        for (WaveformSegment chanData : data) {
            if (nsamp < 1) {
                nsamp = chanData.getNsamp();
            } else if (nsamp != chanData.getNsamp()) {
                throw new IllegalStateException("Number of points not the same for each channel!");
            }
            if (astart == null) {
                astart = new TimeT(chanData.getTimeAsDouble());
            } else if (Math.abs(astart.getEpochTime() - chanData.getTimeAsDouble()) > chanData.getDelta()) {
                throw new IllegalStateException("Not all channel start times are equal!");
            }
        }
    }

    public StreamSegment(Map<StreamKey, WaveformSegment> data) {
        this.data = new ConcurrentHashMap<>(data);
        waveforms = new ArrayList<>();
        for (StreamKey sc : data.keySet()) {
            WaveformSegment chanData = data.get(sc);
            waveforms.add(chanData);
        }
        validate(waveforms);
        size = waveforms.get(0).getLength();
        sampleRate = waveforms.get(0).getSamprate();
    }

    public StreamSegment(StreamSegment seg0, StreamSegment seg1) {

        int nch = seg0.getNumChannels();
        if (nch != seg1.getNumChannels()) {
            throw new IllegalArgumentException("Segments to be concatenated do not have the same number of channels!");
        }

        waveforms = new ArrayList<>();
        for (int j = 0; j < nch; ++j) {
            WaveformSegment data0 = seg0.waveforms.get(j);
            WaveformSegment data1 = seg1.waveforms.get(j);
            Collection<DataDefect> defects = data0.getDefects();
            defects.addAll(data1.getDefects());
            if( !data0.getStreamKey().equals(data1.getStreamKey())){
                throw new IllegalStateException("Segments to be merged are for different channels!");
            }
//            if (!data0.getPosition().equals(data1.getPosition())) {
//                throw new IllegalStateException("Segments to be merged have differing positions!");
//            }

            float[] float0 = data0.getData();
            float[] float1 = data1.getData();
            float[] concatenated = new float[float0.length + float1.length];
            System.arraycopy(float0, 0, concatenated, 0, float0.length);
            System.arraycopy(float1, 0, concatenated, float0.length, float1.length);
            WaveformSegment nwResult = new WaveformSegment(data0.getSta(),
                    data0.getChan(),
                    data0.getTimeAsDouble(),
                    data0.getSamprate(),
                    concatenated,
                    new ArrayList<>(defects));
            waveforms.add(nwResult);
        }
        data = new ConcurrentHashMap<>();
        for (WaveformSegment seg : waveforms) {
            this.data.put(seg.getStreamKey(), seg);
        }
        size = waveforms.get(0).getLength();
        sampleRate = waveforms.get(0).getSamprate();
    }

    public Collection<WaveformSegment> getWaveforms() {
        return new ArrayList<>(waveforms);
    }

    public StreamSegment getSubsegment(Epoch epoch) {
        ArrayList<WaveformSegment> subsetData = new ArrayList<>();
        for (WaveformSegment seg : waveforms) {
            WaveformSegment subset = seg.getSubset(epoch);
            subsetData.add(subset);
        }
        return new StreamSegment(subsetData);
    }

    public float[] getChannelData(int channel) {
        return waveforms.get(channel).getData();
    }

    public float[] getChannelData(StreamKey staChan) {
        return data.get(staChan).getData();
    }

    public WaveformSegment getWaveformSegment(int channel) {
        return waveforms.get(channel);
    }

    public WaveformSegment getWaveformSegment(StreamKey staChan) {
        return data.get(staChan);
    }

    public TimeT getStartTime() {
        return new TimeT(waveforms.get(0).getTimeAsDouble());
    }

    public int getNumChannels() {
        return waveforms.size();
    }

    public int size() {
        return size;
    }

    public double getSamplerate() {
        return sampleRate;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.waveforms);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StreamSegment other = (StreamSegment) obj;
        if (!Objects.equals(this.waveforms, other.waveforms)) {
            return false;
        }
        return true;
    }

    public ArrayList<WaveformSegment> getWaveforms(Collection<? extends StreamKey> staChanList) {
        ArrayList<WaveformSegment> result = new ArrayList<>();
        for (StreamKey sck : staChanList) {
            WaveformSegment ws = this.getWaveformSegment(sck);
            if (ws == null) {
                throw new IllegalStateException("Now waveform segment found in stream for " + sck);
            }
            result.add(ws);
        }
        return result;
    }

    public double getDuration() {
        return waveforms.get(0).getLengthInSeconds();
    }

    public boolean includes(Epoch required) {
        return waveforms.get(0).includes(required);
    }

    public Epoch getEpoch() {
        return waveforms.get(0).getEpoch();
    }

    public double getSampleInterval() {
        return waveforms.get(0).getDelta();
    }

    public boolean isCompatible(StreamSegment seg2) {
        int nchans = this.getNumChannels();
        if (seg2.getNumChannels() != nchans) {
            return false;
        }
        for (int j = 0; j < nchans; ++j) {
            if (!waveforms.get(j).getStreamKey().equals(seg2.waveforms.get(j).getStreamKey())) {
                return false;
            }
        }
        double dt1 = getSampleInterval();
        double dt2 = seg2.getSampleInterval();
        double diff = (dt1 - dt2) / dt1 * 100;
        return diff < 1;
    }

    public int getLengthInSamples() {
        return waveforms.get(0).getNsamp();
    }

    public boolean hasDefects() {
        for(WaveformSegment seg : waveforms){
            if(seg.hasDefects())
                return true;
        }
        return false;
    }

    public void performDefectScan(double minGlobalSpikeZScore, 
            double minLocalSpikeZScore, 
            double maxSpikeDurationSeconds) {
        waveforms.parallelStream().forEach((segment) -> {
            scanSingleSegment(segment, minGlobalSpikeZScore, maxSpikeDurationSeconds, minLocalSpikeZScore);
        });
    }

    private void scanSingleSegment(WaveformSegment segment, double minGlobalSpikeZScore, double maxSpikeDurationSeconds, double minLocalSpikeZScore) {
        Collection<DropOut>dropouts =  new DropoutDetector().scanForDropouts(segment);
        segment.appendToDefectCollection(dropouts);
        Collection<DataSpike> spikes = new SpikeProcessor(minGlobalSpikeZScore,
                maxSpikeDurationSeconds, minLocalSpikeZScore).scanForSpikes(segment);
        segment.appendToDefectCollection(spikes);
    }

}
