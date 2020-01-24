package llnl.gnem.apps.detection.sdBuilder.multiStationStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.dataobjects.StationInfo;
import llnl.gnem.apps.detection.sdBuilder.multiStationStack.MultiStationStackModel;
import llnl.gnem.apps.detection.sdBuilder.multiStationStack.StackElement;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.gui.util.ExceptionDialog;
import llnl.gnem.core.gui.util.ProgressDialog;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class BuildMultiStationStackWorker extends SwingWorker<Void, Void> {

    private final long detectionid;
    private final ArrayList<Epoch> epochs;
    private final Collection<StackElement> stacks;

    public BuildMultiStationStackWorker(long detectionid, Collection<Epoch> epochs) {
        this.detectionid = detectionid;
        this.epochs = new ArrayList<>(epochs);
        stacks = new ArrayList<>();
        ProgressDialog.getInstance().setTitle("Building stack...");
        ProgressDialog.getInstance().setText("...");
        ProgressDialog.getInstance().setProgressStringPainted(true);
        ProgressDialog.getInstance().setProgressBarIndeterminate(true);
        ProgressDialog.getInstance().setReferenceFrame(ClusterBuilderFrame.getInstance());
        ProgressDialog.getInstance().setVisible(true);


    }

    @Override
    protected Void doInBackground() throws Exception {
        int groupid = DetectionDAOFactory.getInstance().getStationDAO().getGroupForDetectionid(detectionid);
        if (groupid > 0) { // Detection belongs to group so it is possible to retrieve data for associated stations.
            Collection<StationInfo> evstaInfo = DetectionDAOFactory.getInstance().getStationDAO().getGroupStations(groupid);
       ProgressDialog.getInstance().setMinMax(0, evstaInfo.size());
       ProgressDialog.getInstance().setProgressBarIndeterminate(false);
       int processedSoFar = 0;
       ProgressDialog.getInstance().setValue(processedSoFar);
            for (StationInfo si : evstaInfo) {
                ProgressDialog.getInstance().setText(si.getSta());
                try {
                    List<StreamKey> keys = DetectionDAOFactory.getInstance().getDetectorDAO().getDetectorChannelsFromConfig(si.getConfigid());
                    for (StreamKey key : keys) {
                        double[] stack = null;
                        int count = 0;
                        double delta = -1;
                        for (Epoch epoch : epochs) {
                            CssSeismogram seis = DetectionDAOFactory.getInstance().getSeismogramDAO().getSeismogram(key, epoch.getTime(), epoch.getEndtime());
                            if (seis != null) {
                                if(delta < 0){
                                    delta = seis.getDelta();
                                }
                                seis.RemoveMean();
                                seis.Taper(5.0);
                                float[] data = seis.getData();
                                if(stack == null){
                                    stack = new double[data.length];
                                }
                                else if(stack.length != data.length){
                                    System.out.println("retrieved data not same length!");
                                }
                                else{
                                    for(int j = 0; j < data.length; ++j){
                                        stack[j] += data[j];
                                    }
                                    ++count;
                                }
                            }
                        }
                        if(count > 1 && stack  != null){
                            double scale = 1.0/count;
                            float[] result = new float[stack.length];
                            for(int j = 0; j < result.length; ++j){
                                result[j] = (float)(stack[j] * scale);
                            }
                            stacks.add(new StackElement(key,result,delta));
                        }
                    }
                } catch (DataAccessException ex) {
                    ApplicationLogger.getInstance().log(Level.INFO, "Stack creation failed!");
                }
                ++processedSoFar;
                ProgressDialog.getInstance().setValue(processedSoFar);
            }
        }
        return null;
    }

    @Override
    public void done() {
        ProgressDialog.getInstance().setVisible(false);
        try {
            get();
            MultiStationStackModel.getInstance().setStackData(stacks);
        } catch (InterruptedException | ExecutionException e) {
            if (!(e instanceof CancellationException)) {
                ExceptionDialog.displayError(e);
            }
        }
    }
}
