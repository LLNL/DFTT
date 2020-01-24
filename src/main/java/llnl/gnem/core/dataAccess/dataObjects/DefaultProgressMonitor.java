/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.dataObjects;

import java.util.logging.Level;
import llnl.gnem.core.util.ApplicationLogger;

/**
 *
 * @author dodge1
 */
public class DefaultProgressMonitor implements ProgressMonitor{

    @Override
    public void setText(String textString) {
        ApplicationLogger.getInstance().log(Level.INFO, textString);
    }

    @Override
    public void setProgressStateIndeterminate(boolean state) {
        // do nothing
    }

    @Override
    public void setRange(int minValue, int maxValue) {
        // do nothing
    }

    @Override
    public void setValue(int value) {
        // do nothing
    }
    
}
