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
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.*;
import llnl.gnem.apps.detection.sdBuilder.dataSelection.DeleteDetectionsWorker;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.core.correlation.CorrelationComponent;

import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Mar 22, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class DeleteDisplayedDetectionsAction extends AbstractAction {

    private static DeleteDisplayedDetectionsAction ourInstance;
    private static final long serialVersionUID = 5632724532961671250L;

    public static DeleteDisplayedDetectionsAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new DeleteDisplayedDetectionsAction(owner);
        }
        return ourInstance;
    }

    private DeleteDisplayedDetectionsAction(Object owner) {
        super("Delete", Utility.getIcon(owner, "miscIcons/deleteSite32.gif"));
        putValue(SHORT_DESCRIPTION, "Delete all displayed detections.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_D);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Collection<CorrelationComponent>  selected = ClusterBuilderFrame.getInstance().getVisibleTraces();
        CorrelatedTracesModel.getInstance().detectionsWereDeleted(selected);
        
        ArrayList<Integer> detectionIdValues = new ArrayList<>();
        for(CorrelationComponent cc : selected){
            int detectionid = (int)cc.getEvent().getEvid();
            detectionIdValues.add(detectionid);
            
        }
        new DeleteDetectionsWorker(detectionIdValues).execute();
        ClusterBuilderFrame.getInstance().removeMultipleDetections(detectionIdValues);
    }

}
