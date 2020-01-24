/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.dataObjects;

/**
 *
 * @author dodge1
 */
public interface ProgressMonitor {

    void setText(String textString);

    void setProgressStateIndeterminate(boolean state);

    void setRange(int minValue, int maxValue);

    void setValue(int value);
}
