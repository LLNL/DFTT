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
package llnl.gnem.apps.detection.sdBuilder.templateDisplay;

import java.awt.BorderLayout;
import javax.swing.JSplitPane;
import llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections.HistogramProjectionView;
import llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections.ProjectionModel;
import llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections.ProjectionTableHolder;
import llnl.gnem.core.gui.util.PersistentPositionContainer;

/**
 *
 * @author dodge1
 */
public class TemplateDisplayFrame extends PersistentPositionContainer {


    private TemplateDisplayFrame() {
        super("detection/sdBuilder/templateDisplay", "Subspace Detector Builder" + ": Template Display", 1200, 1200);
        setIconImage(null);
        TemplateView view = new TemplateView();
        TemplateModel.getInstance().setView(view);

        HistogramProjectionView histView = new HistogramProjectionView();
        ProjectionModel.getInstance().setView(histView);
        ProjectionTableHolder holder = new ProjectionTableHolder();
        ProjectionModel.getInstance().setView(holder);
        JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, histView, holder.getScrollPane());
        splitter.setOneTouchExpandable(true);
        splitter.setDividerLocation(350);
        this.registerSplitter(splitter, "hist_table_splitter", 350);

        JSplitPane horizSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, view, splitter);
        horizSplit.setOneTouchExpandable(true);
        horizSplit.setDividerLocation(600);
        this.registerSplitter(horizSplit, "horiz_splitter", 600);

        getContentPane().add(horizSplit, BorderLayout.CENTER);

        TemplateDlgToolbar toolbar = new TemplateDlgToolbar();

        getContentPane().add(toolbar, BorderLayout.NORTH);
        view.setSelector(toolbar.getSelector());
        toolbar.getSelector().setView(view);
    }

    public static TemplateDisplayFrame getInstance() {
        return TemplateDisplayFrameHolder.INSTANCE;
    }

    private static class TemplateDisplayFrameHolder {

        private static final TemplateDisplayFrame INSTANCE = new TemplateDisplayFrame();
    }
}
