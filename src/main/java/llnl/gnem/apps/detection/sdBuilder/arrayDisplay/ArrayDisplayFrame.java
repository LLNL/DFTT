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
package llnl.gnem.apps.detection.sdBuilder.arrayDisplay;

import java.awt.BorderLayout;

import llnl.gnem.dftt.core.gui.util.PersistentPositionContainer;

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
