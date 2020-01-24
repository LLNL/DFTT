package llnl.gnem.apps.detection.sdBuilder.allStations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.dataobjects.EventInfo;
import llnl.gnem.apps.detection.dataAccess.dataobjects.StationInfo;
import llnl.gnem.apps.detection.dataAccess.dataobjects.OriginInfo;
import llnl.gnem.apps.detection.dataAccess.dataobjects.PhasePick;
import llnl.gnem.apps.detection.dataAccess.dataobjects.ShortDetectionSummary;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.gui.util.ExceptionDialog;
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
        this.timeWindow = new Epoch(timeWindow.getStart()-paddingSeconds, timeWindow.getEnd() + paddingSeconds);
        origins = new ArrayList<>();
        events = new ArrayList<>();
    }

    @Override
    protected Void doInBackground() throws Exception {
        int groupid = DetectionDAOFactory.getInstance().getStationDAO().getGroupForDetectionid(detectionid);
        if (groupid > 0) { // Detection belongs to group so it is possible to retrieve data for associated stations.
            origins.addAll(DetectionDAOFactory.getInstance().getOriginDAO().getOriginsInTimeWindow(timeWindow));
            events.addAll(DetectionDAOFactory.getInstance().getEventDAO().getEventsInTimeWindow(timeWindow));
            Collection<StationInfo> evstaInfo = DetectionDAOFactory.getInstance().getStationDAO().getGroupStations(groupid);
            for (StationInfo si : evstaInfo) {
                try {
                    List<StreamKey> keys = DetectionDAOFactory.getInstance().getDetectorDAO().getDetectorChannelsFromConfig(si.getConfigid());
                    for (StreamKey key : keys) {
                        Epoch epoch = new Epoch(new TimeT(timeWindow.getStart()), new TimeT(timeWindow.getEnd()));
                        CssSeismogram seis = DetectionDAOFactory.getInstance().getSeismogramDAO().getSeismogram(key, epoch.getTime(), epoch.getEndtime());
                        if (seis != null) {
                            Collection<ShortDetectionSummary> detections = DetectionDAOFactory.getInstance().getDetectionDAO().getDetectionsInTimeInterval(si.getConfigid(), epoch);
                            Collection<PhasePick> picks = new ArrayList<>();
                            picks.addAll(DetectionDAOFactory.getInstance().getPickDAO().getPicks(si.getConfigid(), epoch));
                            results.add(new EventSeismogramData(origins, si, seis, detections, picks));
                        }
                    }
                } catch (DataAccessException ex) {
                    ApplicationLogger.getInstance().log(Level.INFO, "No seismograms retrieved!");
                }
            }
        }
        return null;
    }

    @Override
    public void done() {
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
