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
package llnl.gnem.apps.detection.sdBuilder.histogramDisplay;

import llnl.gnem.apps.detection.sdBuilder.templateDisplay.*;
import java.awt.BorderLayout;
import javax.swing.JButton;
import llnl.gnem.core.gui.util.PersistentPositionContainer;

/**
 *
 * @author dodge1
 */
public class HistogramDisplayFrame extends PersistentPositionContainer{

    private static final long serialVersionUID = 2204786536057767245L;

    private final HistogramView view;
    
    private HistogramDisplayFrame() {
        super("detection/sdBuilder/histogramDisplay", "Subspace Detector Builder" + ": Histogram Display", 800, 800);
        setIconImage(null);
         view = new HistogramView();
        HistogramModel.getInstance().setView(view);
        getContentPane().add(view, BorderLayout.CENTER);
        JButton button = new JButton(ExportHistogramPlotAction.getInstance(this));
        getContentPane().add(button, BorderLayout.NORTH);
    }
    
    public static HistogramDisplayFrame getInstance() {
        return TemplateDisplayFrameHolder.INSTANCE;
    }
    
    private static class TemplateDisplayFrameHolder {

        private static final HistogramDisplayFrame INSTANCE = new HistogramDisplayFrame();
    }
    
    
    public void exportPlot() {
        view.exportSVG();
    }

}
