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
package llnl.gnem.apps.detection.sdBuilder.allStations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.swing.SwingWorker;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.dataobjects.EventInfo;
import llnl.gnem.apps.detection.dataAccess.dataobjects.StationInfo;
import llnl.gnem.apps.detection.dataAccess.dataobjects.OriginInfo;
import llnl.gnem.apps.detection.dataAccess.dataobjects.PhasePick;
import llnl.gnem.apps.detection.dataAccess.dataobjects.ShortDetectionSummary;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.core.dataAccess.DAOFactory;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.gui.util.ExceptionDialog;
import llnl.gnem.core.gui.util.ProgressDialog;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class GetStationTimeDataWorker extends SwingWorker<Void, Void> {

    private final double paddingSeconds = 20.0;
    private final Collection<EventSeismogramData> results;
    private final Collection<OriginInfo> origins;
    private final Collection<EventInfo> events;
    private final Epoch timeWindow;
    private final long detectionid;

    public GetStationTimeDataWorker(long detectionid, Epoch timeWindow) {
        this.detectionid = detectionid;
        results = new ArrayList<>();
        this.timeWindow = new Epoch(timeWindow.getStart() - paddingSeconds, timeWindow.getEnd() + paddingSeconds);
        origins = new ArrayList<>();
        events = new ArrayList<>();
        ProgressDialog.getInstance().setTitle("Retrieving all station segments...");
        ProgressDialog.getInstance().setText("...");
        ProgressDialog.getInstance().setProgressStringPainted(true);
        ProgressDialog.getInstance().setProgressBarIndeterminate(true);
        ProgressDialog.getInstance().setReferenceFrame(ClusterBuilderFrame.getInstance());
        ProgressDialog.getInstance().setValue(0);
        ProgressDialog.getInstance().setVisible(true);

    }

    @Override
    protected Void doInBackground() throws Exception {
        ProgressDialog.getInstance().setText("Retrieving groupid...");
        int groupid = DetectionDAOFactory.getInstance().getStationDAO().getGroupForDetectionid(detectionid);
        if (groupid > 0) { // Detection belongs to group so it is possible to retrieve data for associated stations.
            ProgressDialog.getInstance().setText("Retrieving origins...");
            origins.addAll(DetectionDAOFactory.getInstance().getOriginDAO().getOriginsInTimeWindow(timeWindow));
            ProgressDialog.getInstance().setText("Retrieving events...");
            events.addAll(DetectionDAOFactory.getInstance().getEventDAO().getEventsInTimeWindow(timeWindow));
            ProgressDialog.getInstance().setText("Retrieving station list...");
            Collection<StationInfo> evstaInfo = DetectionDAOFactory.getInstance().getStationDAO().getGroupStations(groupid);
            ProgressDialog.getInstance().setProgressBarIndeterminate(false);
            ProgressDialog.getInstance().setMinMax(0, evstaInfo.size());
            AtomicLong counter = new AtomicLong();
            counter.getAndSet(0);
            Epoch epoch = new Epoch(new TimeT(timeWindow.getStart()), new TimeT(timeWindow.getEnd()));
            List<List<EventSeismogramData>> aResult = evstaInfo.parallelStream().map(t -> processStationInfo(t, epoch,counter)).filter(Objects::nonNull).collect(Collectors.toList());
            for (List<EventSeismogramData> aList : aResult) {
                for (EventSeismogramData esd : aList) {
                    results.add(esd);
                }
            }
        }
        return null;
    }

    private List<EventSeismogramData> processStationInfo(StationInfo si, Epoch epoch,AtomicLong counter) {
        List<EventSeismogramData> result = new ArrayList<>();
        try {
            ProgressDialog.getInstance().setText(si.getSta());
            Collection<ShortDetectionSummary> detections = DetectionDAOFactory.getInstance().getDetectionDAO().getDetectionsInTimeInterval(si.getConfigid(), epoch);
            Collection<PhasePick> picks = new ArrayList<>();
            picks.addAll(DetectionDAOFactory.getInstance().getPickDAO().getPicks(si.getConfigid(), epoch));
            try {
                List<StreamKey> keys = DetectionDAOFactory.getInstance().getDetectorDAO().getDetectorChannelsFromConfig(si.getConfigid());
                result.addAll(keys.stream().map(t -> buildEvSeisData(t, si, epoch, detections, picks)).filter(Objects::nonNull).collect(Collectors.toList()));
            } catch (DataAccessException ex) {
                ApplicationLogger.getInstance().log(Level.INFO, "Error retrieving seismograms!");
            }
        } catch (Exception ex) {
            ApplicationLogger.getInstance().log(Level.INFO, "Error producing EventSeismogramData!");

        }
        ProgressDialog.getInstance().setValue((int)counter.getAndIncrement()+1);
        return result;
    }

    private EventSeismogramData buildEvSeisData(StreamKey key, StationInfo si, Epoch epoch, Collection<ShortDetectionSummary> detections, Collection<PhasePick> picks) {
        try {
            CssSeismogram seis = DAOFactory.getInstance().getContinuousWaveformDAO().getCssSeismogram(key, epoch);
            if (seis != null) {
                return new EventSeismogramData(origins, si, seis, detections, picks);
            }
        } catch (Exception ex) {
            return null;
        }
        return null;
    }

    @Override
    public void done() {
        ProgressDialog.getInstance().setVisible(false);
        try {
            get();
            SeismogramModel.getInstance().setSeismograms(results, events);
        } catch (InterruptedException | ExecutionException e) {
            if (!(e instanceof CancellationException)) {
                ExceptionDialog.displayError(e);
            }
        }
    }
}
