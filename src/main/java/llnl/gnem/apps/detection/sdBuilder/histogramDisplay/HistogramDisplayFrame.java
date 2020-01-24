/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
