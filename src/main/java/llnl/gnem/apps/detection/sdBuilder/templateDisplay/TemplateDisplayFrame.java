/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
