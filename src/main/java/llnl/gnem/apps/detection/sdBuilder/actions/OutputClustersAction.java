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
package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.apps.detection.sdBuilder.WriteClustersWorker;
import llnl.gnem.core.correlation.clustering.GroupData;
import llnl.gnem.core.gui.util.Utility;


public class OutputClustersAction extends AbstractAction {

    private static OutputClustersAction ourInstance;
   

    public static OutputClustersAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new OutputClustersAction(owner);
        }
        return ourInstance;
    }

    private OutputClustersAction(Object owner) {
        super("Template", Utility.getIcon(owner, "miscIcons/add2db32.gif"));
        putValue(SHORT_DESCRIPTION, "Write clusters to database.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_T);
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Collection<GroupData>groups = CorrelatedTracesModel.getInstance().getGroups();
        int detectorid = CorrelatedTracesModel.getInstance().getCurrentDetectorid();
        int runid = CorrelatedTracesModel.getInstance().getRunid();
        new WriteClustersWorker(groups,  detectorid,  runid).execute();

    }

}
