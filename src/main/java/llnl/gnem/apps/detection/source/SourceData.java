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
package llnl.gnem.apps.detection.source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import llnl.gnem.apps.detection.ConfigurationInfo;
import llnl.gnem.apps.detection.core.dataObjects.WaveformSegment;
import llnl.gnem.apps.detection.core.framework.StreamProcessor;
import llnl.gnem.apps.detection.gaps.GapManager;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import llnl.gnem.dftt.core.dataAccess.DAOFactory;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.Epoch;
import llnl.gnem.dftt.core.util.StreamKey;
import llnl.gnem.dftt.core.util.TimeT;
import llnl.gnem.dftt.core.waveform.merge.NamedIntWaveform;

/**
 * Created by dodge1 Date: Jul 14, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class SourceData {

    public static final int QUEUE_DEPTH = 2;
    public static final int SLEEP_MILLIS = 10000;

    private final int BLOCK_SIZE = 7200;
    private double commonRate;

    private long currentStartTime;
    private final ArrayBlockingQueue<Collection<WaveformSegment>> retrievedSegments;
    private boolean hasMoreData;
    private boolean stopRetrieving;
    private final String threadName;
    private final boolean scaleByCalib;

    private TimeT maxTimeToRetrieve;

    public SourceData(String streamGroup, boolean scaleByCalib) {
        retrievedSegments = new ArrayBlockingQueue<>(QUEUE_DEPTH);
        hasMoreData = true;
        stopRetrieving = false;
        threadName = "RetrieveAllBlocksThread";
        this.scaleByCalib = scaleByCalib;
        maxTimeToRetrieve = TimeT.getTimeFromJulianDate(ProcessingPrescription.getInstance().getMaxJdateToProcess());
        maxTimeToRetrieve.add(TimeT.SECPERDAY);

    }

    public Epoch getTimeRange() {
        return ConfigurationInfo.getInstance().getSupport().getTimeSpan();
    }

    public Collection<StreamKey> getStreamKeys() {
        return new ArrayList<>(ConfigurationInfo.getInstance().getStreamKeys());
    }

    public void retrieveAllBlocks() throws InterruptedException, DataAccessException {

        Epoch epoch = ConfigurationInfo.getInstance().getSupport().getTimeSpan();
        currentStartTime = Math.round(epoch.getStart());
        maxTimeToRetrieve = epoch.getEndtime();
        commonRate = ConfigurationInfo.getInstance().getSupport().getRate();
        Thread.currentThread().setName(threadName);
        TimeT blockStartTime = new TimeT(currentStartTime);
        if (blockStartTime.gt(maxTimeToRetrieve)) {
            retrievedSegments.put(new ArrayList<>());// poison pill to be used by streamServer to shut down
            hasMoreData = false;
            return;
        }
        while (currentStartTime + BLOCK_SIZE < maxTimeToRetrieve.getEpochTime()) {
            if (!getDataBlockForCurrentTime()) {
                break;
            }
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
        }
        ApplicationLogger.getInstance().log(Level.FINEST, "No more blocks to retrieve in (retrieveAllBlocks)");
        retrievedSegments.offer(new ArrayList<>(), 10l, TimeUnit.SECONDS);// poison pill to be used by streamServer to
                                                                          // shut down
        ApplicationLogger.getInstance().log(Level.FINEST,
                "Inserted termination block into queue in (retrieveAllBlocks)");
        hasMoreData = false;
    }

    public boolean isHasMoreData() {
        return hasMoreData || !retrievedSegments.isEmpty();
    }

    private boolean getDataBlockForCurrentTime() throws DataAccessException, InterruptedException {

        if (stopRetrieving) {
            return false;
        }
        double start = currentStartTime;
        double end = start + BLOCK_SIZE;

        ApplicationLogger.getInstance().log(Level.FINEST, String
                .format("Retrieving trimmed waveform collection for epoch (%s)...", new Epoch(start, end).toString()));
        Collection<WaveformSegment> trimmed = createTrimmedWaveformCollection(end, start, scaleByCalib);
        String msg = String.format("Inserting block into queue of size %d in (getDataBlockForCurrentTime)",
                retrievedSegments.size());
        ApplicationLogger.getInstance().log(Level.FINEST, msg);
        retrievedSegments.put(trimmed);
        ApplicationLogger.getInstance().log(Level.FINEST,
                "Completed inserting block into queue in (getDataBlockForCurrentTime)");
        currentStartTime += BLOCK_SIZE;
        return true;
    }

    private Collection<WaveformSegment> createTrimmedWaveformCollection(double end, double start, boolean scaleByCalib)
            throws DataAccessException {
        Collection<WaveformSegment> result = new ArrayList<>();
        Collection<NamedIntWaveform> namedWaveforms = getNamedWaveformCollection(start, end);
        for (NamedIntWaveform waveform : namedWaveforms) {
            if (scaleByCalib) {
                waveform.scaleByCalib();
            }
            WaveformSegment aSeg = new WaveformSegment(waveform, commonRate);
            result.add(aSeg);
        }
        return result;

    }

    public Collection<NamedIntWaveform> getNamedWaveformCollection(double start, double end)
            throws DataAccessException {
        Collection<NamedIntWaveform> namedWaveforms = new ArrayList<>();
        // Next block will start exactly at current end so end this block one sample
        // earlier.
        if (commonRate == 0.0) {
            commonRate = ConfigurationInfo.getInstance().getSupport().getRate();
        }
        if(commonRate <= 0){
            throw new IllegalStateException("Sample rate is not set in configuration info!");
        }
        double deltaT = 1.0 / commonRate;
        Epoch epoch = new Epoch(start, end - deltaT);
        Collection<StreamKey> keys = ConfigurationInfo.getInstance().getStreamKeys();
        for (StreamKey key : keys) {
            NamedIntWaveform waveform = null;
            try {
                waveform = DAOFactory.getInstance().getContinuousWaveformDAO().getNamedIntWaveform(key, epoch);
            } catch (DataAccessException ex) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Failed retrieving data for:" + key, ex);
            }
            if (waveform == null) {
                waveform = createEmptySegment(start, end, key, commonRate);
            }
            waveform = waveform.ensureCompleteEpoch(epoch);
            double samprate = waveform.getRate();
            if (samprate != commonRate) {
                waveform = waveform.interpolateTo(commonRate);
            }
            waveform = GapManager.getInstance().maybeFillGaps(waveform);

            namedWaveforms.add(waveform);
        }

        return namedWaveforms;
    }

    public Collection<WaveformSegment> retrieveDataBlock(TimeT startTime, double duration, boolean scaleByCalib)
            throws Exception {

        ApplicationLogger.getInstance().log(Level.FINE, String.format(
                "Retrieving new primary data block starting at %s and extending for %9.3f s", startTime, duration));
        double start = startTime.getEpochTime();
        double end = start + duration;
        Collection<WaveformSegment> trimmed = createTrimmedWaveformCollection(end, start, scaleByCalib);
        return trimmed;
    }

    public Collection<WaveformSegment> getDataBlock() throws InterruptedException {
        ApplicationLogger.getInstance().log(Level.FINEST, String
                .format(" SourceData.getDataBlock() will take block from queue of size %d.", retrievedSegments.size()));

        return retrievedSegments.take();

    }

    public double getCommonSampleRate() {
        return commonRate;
    }

    public boolean supports(StreamProcessor processor) {
        Collection<StreamKey> requiredChannels = processor.getChannels();
        Collection<StreamKey> myChannels = getStreamKeys();
        if (!requiredChannels.stream().noneMatch(sc -> (!myChannels.contains(sc)))) {
            return false;
        }
        return true;
    }

    public synchronized void stopRetrieving() {
        stopRetrieving = true;
    }

    private NamedIntWaveform createEmptySegment(double requestedStart, double requestedEnd, StreamKey key,
            double expectedRate) {

        double duration = requestedEnd - requestedStart;
        long npts = Math.round(duration * expectedRate);// Want the waveform to end 1 sample before requested end since
                                                        // requested end = requested start of next data block.
        int[] data = new int[(int) npts];
        Arrays.fill(data, 0);
        return new NamedIntWaveform(key, -1L, data, requestedStart, expectedRate, null, null);
    }

}
