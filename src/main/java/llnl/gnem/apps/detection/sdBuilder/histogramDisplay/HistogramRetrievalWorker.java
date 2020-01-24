/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package llnl.gnem.apps.detection.sdBuilder.histogramDisplay;

import llnl.gnem.apps.detection.statistics.HistogramData;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import llnl.gnem.apps.detection.database.SubspaceDetectorDAO;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.PairT;

/**
 *
 * @author dodge1
 */
public class HistogramRetrievalWorker extends SwingWorker<Void,Void> {
    private final int detectorid;
    private final int runid;
    private HistogramData result;
    
    public HistogramRetrievalWorker( int detectorid, int runid )
    {
        this.detectorid = detectorid;
        this.runid = runid;
    }

    @Override
    protected Void doInBackground() throws Exception {
            result = SubspaceDetectorDAO.getInstance().getHistogramData(detectorid, runid);
        return null;
    }
    
    @Override
    public void done()
    {
        try{
            get();
            HistogramModel.getInstance().setHistogram(result,detectorid, runid);
        }
        catch (InterruptedException | ExecutionException ex){
            ApplicationLogger.getInstance().log(Level.WARNING, "Failed retrieving histogram for detectorid : " + detectorid, ex);
        }
    }
    
}
