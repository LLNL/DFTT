/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package llnl.gnem.apps.detection.sdBuilder.singleDetectionDisplay;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import llnl.gnem.apps.detection.core.dataObjects.Detection;
import llnl.gnem.apps.detection.core.dataObjects.TriggerDataFeatures;
import llnl.gnem.apps.detection.database.DetectionDAO;
import llnl.gnem.core.util.ApplicationLogger;

/**
 *
 * @author dodge1
 */
public class SingleDetectionRetrievalWorker extends SwingWorker<Void,Void> {
    private final int detectionid;
    private Detection result;
    
    public SingleDetectionRetrievalWorker( int detectionid )
    {
        this.detectionid = detectionid;
    }

    @Override
    protected Void doInBackground() throws Exception {
            result = DetectionDAO.getInstance().getSingleDetection(detectionid);
        return null;
    }
    
    @Override
    public void done()
    {
        try{
            get();
            SingleDetectionModel.getInstance().setDetection(result);
        }
        catch (InterruptedException | ExecutionException ex){
            ApplicationLogger.getInstance().log(Level.WARNING, "Failed retrieving Detection for detectionid : " + detectionid, ex);
        }
    }
    
}
