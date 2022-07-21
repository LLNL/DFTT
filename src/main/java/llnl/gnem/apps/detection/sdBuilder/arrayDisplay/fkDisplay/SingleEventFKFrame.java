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
package llnl.gnem.apps.detection.sdBuilder.arrayDisplay.fkDisplay;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import llnl.gnem.core.gui.plotting.fkPlot.FKPlot;
import llnl.gnem.core.gui.util.PersistentPositionContainer;
import llnl.gnem.core.signalprocessing.arrayProcessing.FKResult;

public class SingleEventFKFrame extends PersistentPositionContainer {

    private static SingleEventFKFrame instance;
    private final FKPlot viewer;

    public static synchronized SingleEventFKFrame getInstance() {
        if (instance == null) {
            instance = new SingleEventFKFrame();
        }
        return instance;
    }

    private SingleEventFKFrame() {
        super("detection/sdBuilder/arrayDisplay/FK", "FK" + ": Viewer", 800, 800);
        setIconImage(null);
        viewer = new FKPlot();
        SingleEventFKModel.getInstance().addView(this);

        this.getContentPane().add(viewer, BorderLayout.CENTER);
 //       this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    }

    @Override
    protected void updateCaption() {
        setTitle(shortTitle);
    }

    void updateForChangedData() {
        FKResult result = SingleEventFKModel.getInstance().getFkResult();
        viewer.plotFK(result);
    }
}