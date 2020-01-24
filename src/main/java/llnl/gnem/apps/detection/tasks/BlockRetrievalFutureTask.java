/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.tasks;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import llnl.gnem.core.util.ApplicationLogger;

/**
 *
 * @author dodge1
 */
public class BlockRetrievalFutureTask extends FutureTask<Void>{
    
    public BlockRetrievalFutureTask(Callable<Void> callable)
    {
        super(callable);
    }
    
    @Override
    protected void done()
    {
        try {
            this.get();
        } catch (InterruptedException ex) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "Task was interrupted!", ex);
            System.exit(1);
        } catch (Exception ex) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "Task failed!", ex);
            System.exit(1);
        }
        
    }
    
}
