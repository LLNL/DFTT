package llnl.gnem.apps.detection.sdBuilder.waveformViewer;

import java.awt.BorderLayout;
import java.util.Collection;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import llnl.gnem.apps.detection.sdBuilder.ClusterDlgToolbar;
import llnl.gnem.apps.detection.sdBuilder.dataSelection.ConfigDataModel;
import llnl.gnem.apps.detection.sdBuilder.dataSelection.GetConfigurationsWorker;
import llnl.gnem.apps.detection.sdBuilder.dataSelection.TreePanel;
import llnl.gnem.apps.detection.sdBuilder.stackViewer.StackViewer;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.gui.plotting.Limits;
import llnl.gnem.core.gui.plotting.MouseMode;
import llnl.gnem.core.gui.util.PersistentPositionContainer;

/**
 * Created by dodge1 Date: Feb 4, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class ClusterBuilderFrame extends PersistentPositionContainer {

    private static ClusterBuilderFrame instance;
    private static final long serialVersionUID = 7841877980319845713L;
    private final ClusterViewer traceViewer;
    private final StackViewer stackViewer;
    private final TreePanel treePanel;

    public synchronized static ClusterBuilderFrame getInstance() {
        if (instance == null) {
            instance = new ClusterBuilderFrame();
        }
        return instance;
    }
    private final ClusterDlgToolbar toolbar;

    private ClusterBuilderFrame()  {
        super("detection/sdBuilder", "Subspace Detector Builder" + ": Detector Waveforms", 800, 800);
        setIconImage(null);

        treePanel = new TreePanel();
        ConfigDataModel.getInstance().setView(treePanel);
        traceViewer = new ClusterViewer();
        stackViewer = new StackViewer();
        new GetConfigurationsWorker().execute();
        
        JSplitPane waveformSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT,traceViewer,stackViewer);
        this.registerSplitter(waveformSplitter, "traceTraceSplitter", 500);
        
        JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,treePanel,waveformSplitter);
        this.registerSplitter(splitter, "treeTraceSplitter", 200);
      
        
        
        CorrelatedTracesModel.getInstance().setViewer(traceViewer);
        CorrelatedTracesModel.getInstance().setViewer(stackViewer);
        
        
        this.getContentPane().add(splitter, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        toolbar = new ClusterDlgToolbar(traceViewer);
        this.getContentPane().add(toolbar, BorderLayout.NORTH);


//        updateCaption();
    }

    @Override
    protected void updateCaption() {
        setTitle(shortTitle);
    }

    public void magnifyTraces() {
        traceViewer.magnifyTraces();
        stackViewer.magnifyTraces();
    }

    public void reduceTraces() {
        traceViewer.reduceTraces();
        stackViewer.reduceTraces();
    }


    public void autoScaleTraces() {
        traceViewer.scaleAllTraces(false);
        traceViewer.repaint();

    }

    public void unzoomAll() {
        traceViewer.unzoomAll();
    }

    public void exportPlot() {
        traceViewer.exportSVG();
    }

    public void printPlot() {
        traceViewer.print();
    }

    public void clearTree() {
        treePanel.clearTree();
    }
    
    public Limits getCurrentXLimits()
    {
        return traceViewer.getCurrentXLimits();
    }

    void setSelectedDetection(int detectionid) {
        treePanel.setSelectedDetection(detectionid);
    }
    
    public void applyCurrentFilter()
    {
        toolbar.applyCurrentFilter();
    }
    
    public void returnFocusToTree()
    {
        treePanel.returnFocusToTree();
    }
    
    public void setMouseMode(MouseMode mode){
        stackViewer.setMouseMode(mode);
        traceViewer.setMouseMode(mode);
    }
    
    public void loadDetectionWaveforms() {
        treePanel.loadDetections();
    }
    
    public void removeMultipleDetections(Collection<Integer> detectionIdValues){
        treePanel.removeMultipleDetections(detectionIdValues);
    }

    public Collection<CorrelationComponent> getVisibleTraces() {
        return traceViewer.getVisibleTraces();
    }
}
