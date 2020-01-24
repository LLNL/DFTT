package llnl.gnem.apps.detection.sdBuilder.arrayDisplay;

import java.awt.BorderLayout;

import llnl.gnem.core.gui.util.PersistentPositionContainer;

/**
 * Created by dodge1 Date: Feb 4, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class ArrayDisplayFrame extends PersistentPositionContainer {

    private static ArrayDisplayFrame instance;
    private final ArrayDisplayViewer viewer;

    public synchronized static ArrayDisplayFrame getInstance() {
        if (instance == null) {
            instance = new ArrayDisplayFrame();
        }
        return instance;
    }

    private ArrayDisplayFrame()  {
        super("llnl/gnem/apps/detection/sdBuilder/arrayDisplay", "Builder" + ": Array Display", 800, 800);
  //      setIconImage(Builder.getApplicationIcon());

        viewer = new ArrayDisplayViewer();
        ArrayDisplayModel.getInstance().setViewer(viewer);
        this.getContentPane().add(viewer, BorderLayout.CENTER);

        this.getContentPane().add(new ArrayDlgToolbar(viewer), BorderLayout.NORTH);

        updateCaption();
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

    public void saveAsSVG() {
        viewer.exportSVG();
    }

    public void autoScaleTraces() {
        viewer.scaleAllTraces(false);
        viewer.repaint();

    }

    public void exportPlot() {
        viewer.exportSVG();
    }

    public void printPlot() {
        viewer.print();
    }

    public void unzoomAll() {
        viewer.unzoomAll();
    }
}
