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
package llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import llnl.gnem.dftt.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class GetProjectionsAction extends AbstractAction {

    private static GetProjectionsAction ourInstance;
    private static final long serialVersionUID = 5120574418825972506L;
   

    public static GetProjectionsAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new GetProjectionsAction(owner);
        }
        return ourInstance;
    }
    private int detectorid;
    private int runid;

    private GetProjectionsAction(Object owner) {
        super("Projections", Utility.getIcon(owner, "miscIcons/projector32.gif"));
        putValue(SHORT_DESCRIPTION, "Get projections on other templates used in current run.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_P);
        setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new ComputeProjectionsWorker(detectorid,runid ).execute();
    }

    public void setDetectorid(int detectorid) {
        this.detectorid = detectorid;
    }
    
    public void setRunid( int runid ){
        this.runid = runid;
    }

}
