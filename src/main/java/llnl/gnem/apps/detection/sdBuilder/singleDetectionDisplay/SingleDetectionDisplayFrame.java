/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package llnl.gnem.apps.detection.sdBuilder.singleDetectionDisplay;

import java.awt.BorderLayout;
import javax.swing.JSplitPane;
import llnl.gnem.core.gui.util.PersistentPositionContainer;

/**
 *
 * @author dodge1
 */
public class SingleDetectionDisplayFrame extends PersistentPositionContainer{

    private static final long serialVersionUID = -7111707052083891640L;
    
    private SingleDetectionDisplayFrame() {
        super("detection/sdBuilder/singleDetectionDisplay", "Subspace Detector Builder" + ": Single Detection Display", 800, 800);
        setIconImage(null);
        SingleDetectionWaveformView view = new SingleDetectionWaveformView();
        SingleDetectionModel.getInstance().addView(view);
        
        FeatureHistogram histogram = new FeatureHistogram();
        SingleDetectionModel.getInstance().addView(histogram);
        JSplitPane split2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, view, histogram);
        split2.setOneTouchExpandable(true);
        split2.setDividerLocation(350);
        this.registerSplitter(split2, "hist_trace_splitter", 350);
        
        getContentPane().add(split2, BorderLayout.CENTER);
        FeatureTableHolder table = new FeatureTableHolder();
        SingleDetectionModel.getInstance().addView(table);
        getContentPane().add(table.getScrollPane(), BorderLayout.SOUTH);
    }
    
    public static SingleDetectionDisplayFrame getInstance() {
        return SingleDetectionDisplayFrameHolder.INSTANCE;
    }
    
    private static class SingleDetectionDisplayFrameHolder {

        private static final SingleDetectionDisplayFrame INSTANCE = new SingleDetectionDisplayFrame();
    }
}
