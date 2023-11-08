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
package llnl.gnem.apps.detection.sdBuilder.singleDetectionDisplay;

import java.awt.BorderLayout;
import javax.swing.JSplitPane;
import llnl.gnem.dftt.core.gui.util.PersistentPositionContainer;

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
