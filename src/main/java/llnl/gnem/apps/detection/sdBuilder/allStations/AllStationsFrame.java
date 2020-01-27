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
