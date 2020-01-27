/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package llnl.gnem.apps.detection.streams;


import llnl.gnem.apps.detection.core.framework.StreamProcessor;
import llnl.gnem.apps.detection.source.SourceData;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import llnl.gnem.apps.detection.util.initialization.StreamsConfig;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.apps.detection.core.dataObjects.WaveformSegment;

/**
 * Created by dodge1 Date: Sep 27, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class StreamServer {

    private final Collection<StreamProcessor> processors;
    private final SourceData source;
    private final Epoch dataEpoch;
    private TimeT blockStartTime;
    private final Map<StreamKey, WaveformSegment> preRetrievedMap;
    private final Collection<StreamKey> channels;
    private final double requestedSeconds;
    private final int blockSize;
    private int lastJdate = -1;

    public StreamServer(SourceData source, double primaryBufferSize) {
        this.source = source;
        processors = new ArrayList<>();
        dataEpoch = source.getTimeRange();
        blockStartTime = dataEpoch.getTime();
        preRetrievedMap = new HashMap<>();
        channels = source.getStaChan();
        blockSize = StreamsConfig.getInstance().getUndecimatedBlockSize();
        requestedSeconds = (StreamsConfig.getInstance().getUndecimatedBlockSize() - 1) / source.getCommonSampleRate();
        
    }

    public void addStreamProcessor(StreamProcessor processor) {
        if (!source.supports(processor)) {
            throw new IllegalStateException(String.format("StreamProcessor (%s) is not applicable to current source!", processor));
        }

        processors.add(processor);
    }

    public boolean advance() throws Exception {

        if (preRetrievedMap.isEmpty()) {
            return false;
        }
        Epoch epoch = new Epoch(blockStartTime, blockStartTime.add(requestedSeconds));
        String activity =  "Processing";
        String msg = String.format("%s block : %s (length = %8.3f s)...", activity, epoch.toString(), epoch.duration());
        ApplicationLogger.getInstance().log(Level.FINE, msg);
        
        
        int jdate = epoch.getOnJdate();
        if( jdate > lastJdate){
            ApplicationLogger.getInstance().log(Level.INFO, String.format("Processing day(%d)...", jdate));
        }
        lastJdate = jdate;
        if (currentDataIncludesEpoch(epoch)) {
            processEpoch(epoch);
        } else {
            while (!currentDataIncludesEpoch(epoch)) {
                if (!extendPreRetrievedData()) {
                    return false;
                }
            }

            if (currentDataIncludesEpoch(epoch)) {
                processEpoch(epoch);
            } else {
                return false;
            }
        }
        blockStartTime = blockStartTime.add(requestedSeconds);
        return true;
    }

    private boolean extendPreRetrievedData() throws Exception {
        return appendDataBlock();
    }

    private void retrieveFirstDataBlock() throws Exception {

        Collection<WaveformSegment> results = source.getDataBlock();
        double retrievedSeconds = 0;
        for (WaveformSegment data : results) {
            blockStartTime = new TimeT(data.getTimeAsDouble());
            retrievedSeconds = getDuration(data);
            StreamKey sc = new StreamKey(data.getSta(), data.getChan());
            preRetrievedMap.put(sc, data);
        }
        ApplicationLogger.getInstance().log(Level.FINE, String.format("Retrieved datablock starting at %s and extending for %f seconds...",
                blockStartTime.toString(), retrievedSeconds));

    }

    private void processEpoch(Epoch epoch) {
        for (StreamKey cs : channels) {
            WaveformSegment data = preRetrievedMap.get(cs);

            WaveformSegment subset = data.getSubset(epoch, blockSize);
            for (StreamProcessor processor : processors) {
                processor.maybeAddChannel(subset);
            }
        }
    }

    private boolean currentDataIncludesEpoch(Epoch epoch) {
        for (StreamKey cs : channels) {
            WaveformSegment nw = preRetrievedMap.get(cs);
            if (!nw.includes(epoch)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasMoreData() {
        if (blockStartTime.getJdate() > ProcessingPrescription.getInstance().getMaxJdateToProcess()) {
            source.stopRetrieving();
            return false;
        }
        return source.isHasMoreData();
    }

    public void initialize() throws Exception {
        retrieveFirstDataBlock();
    }

    public void close() throws IOException, SQLException {
        source.close();
    }

    public double getCommonSampleRate() {
        return source.getCommonSampleRate();
    }

    public TimeT getBlockStartTime() {
        return blockStartTime;
    }

    public double getRequestedSeconds() {
        return requestedSeconds;
    }

    private double getDuration(WaveformSegment data) {
        double rate = data.getSamprate();
        int npts = data.getNsamp();
        return (npts - 1) / rate;
    }

    private boolean appendDataBlock() throws InterruptedException, IOException {
        Collection<WaveformSegment> results = source.getDataBlock();
        if (!results.isEmpty()) {
            double retrievedSeconds = 0;
            for (WaveformSegment newData : results) {
                String sta = newData.getSta();
                String chan = newData.getChan();
                StreamKey sc = new StreamKey(sta, chan);
                WaveformSegment existing = preRetrievedMap.get(sc);
                if (existing == null) {
                    throw new IllegalStateException("Attempt to extend non-existing segment");
                }
                WaveformSegment segment = existing.trimFrontAndAppend(newData, blockStartTime.getEpochTime());
                retrievedSeconds = (segment.getNsamp() - 1) / segment.getSamprate();
                preRetrievedMap.remove(sc);
                preRetrievedMap.put(sc, segment);
            }
            double endtime = blockStartTime.getEpochTime() + retrievedSeconds;
            ApplicationLogger.getInstance().log(Level.FINE, String.format("Updated datablock: Now starting at %s and extending to %s...",
                    blockStartTime.toString(), new TimeT(endtime)));
            return true;
        } else {
            ApplicationLogger.getInstance().log(Level.FINE, "No more segments available to StreamServer.");
            return false;
        }
    }

    public void shutdown() {
        source.stopRetrieving();
    }
}
