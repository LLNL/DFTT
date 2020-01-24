package llnl.gnem.apps.detection.sdBuilder.allStations;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import llnl.gnem.core.gui.util.PersistentPositionContainer;

public class AllStationsFrame extends PersistentPositionContainer {

    private static AllStationsFrame instance;

    public static synchronized AllStationsFrame getInstance() {
        if (instance == null) {
            instance = new AllStationsFrame();
        }
        return instance;
    }

    private final AllStationsToolbar toolbar;
    private final SeismogramDisplayView seismogramView;
    private final MultiSeismogramPlot viewer;

    private AllStationsFrame() {
        super("detection/sdBuilder/allStations", "Origins" + ": Viewer", 800, 800);
        setIconImage(null);

        seismogramView = new SeismogramDisplayView();
        SeismogramModel.getInstance().addView(seismogramView);
        viewer = seismogramView.getSeismogramPlot();

        this.getContentPane().add(viewer, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        toolbar = new AllStationsToolbar(viewer);
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

    public void savePicks() {
        viewer.savePicks();
    }

    public void defineEventWindow() {
        viewer.defineEventWindow();
    }

}
