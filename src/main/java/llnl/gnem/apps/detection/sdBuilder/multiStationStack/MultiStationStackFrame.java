package llnl.gnem.apps.detection.sdBuilder.multiStationStack;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import llnl.gnem.core.gui.util.PersistentPositionContainer;

public class MultiStationStackFrame extends PersistentPositionContainer {

    private static MultiStationStackFrame instance;
    private static final long serialVersionUID = -1186421003183786937L;

    public static synchronized MultiStationStackFrame getInstance() {
        if (instance == null) {
            instance = new MultiStationStackFrame();
        }
        return instance;
    }

    private final MultiStationStackToolbar toolbar;
    private final MultiStationStackPlot viewer;

    private MultiStationStackFrame() {
        super("detection/sdBuilder/multiStationStack", "Stacks" + ": Viewer", 800, 800);
        setIconImage(null);
        viewer = new MultiStationStackPlot();
        MultiStationStackModel.getInstance().addView(viewer);
        
        this.getContentPane().add(viewer, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        toolbar = new MultiStationStackToolbar(viewer);
        this.getContentPane().add(toolbar, BorderLayout.NORTH);
    }

    @Override
    protected void updateCaption() {
        setTitle(shortTitle);
    }

    public void magnifyTraces() {
        viewer.magnifyTraces();
    }

    public void reduceTraces() {
        viewer.reduceTraces();
    }

    public void autoScaleTraces() {
        viewer.scaleAllTraces(false);
        viewer.repaint();

    }

    public void unzoomAll() {
        viewer.unzoomAll();
    }

    public void exportPlot() {
        viewer.exportSVG();
    }

    public void printPlot() {
        viewer.print();
    }

    private void loadData() {
        // seismogramView.loadData();
    }

    public void writeCurrent() {
        // Classifications result = controlPanel.getClassifications();
        // FeatureSet features = seismogramView.getCurrentFeatureSet();
        // new WriteClassificationWorker(result, features).execute();

        loadData();
    }

    public void returnFocusToPlot() {
        viewer.requestFocusInWindow();
    }
}
