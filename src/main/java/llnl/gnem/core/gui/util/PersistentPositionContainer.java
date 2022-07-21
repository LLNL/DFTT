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
package llnl.gnem.core.gui.util;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * User: dodge1
 * Date: Jun 12, 2007
 * Time: 4:27:28 PM
 */

public class PersistentPositionContainer extends JFrame implements Observer {

    protected StatusBarPanel statusbar;

    private static final int RIGHT_PANEL_WIDTH = 100;
    private static final int CONTROL_LEFT = RIGHT_PANEL_WIDTH;
    @SuppressWarnings({"SuspiciousNameCombination"})
    private static final int CONTROL_TOP = RIGHT_PANEL_WIDTH;
    private final String preferencePath;
    protected String shortTitle = "";
    private static final int LEFT_PANEL_WIDTH = 200;
    private FramePositionManager positionManager;


    public PersistentPositionContainer(String preferencePath, String title, int width, int height) {
        this.preferencePath = preferencePath;
        SetUpFrameManagement(preferencePath, width, height);
        setGlassPane(new CustomGlassPane());
        shortTitle = title;

        statusbar = new StatusBarPanel(LEFT_PANEL_WIDTH, RIGHT_PANEL_WIDTH);
        getContentPane().add(statusbar, BorderLayout.SOUTH);
        statusbar.setVisible(false);

    }

    public String getPreferencePath() {
        return preferencePath;
    }

    public void setUsableState(boolean usable, boolean blurBuffer) {
        CustomGlassPane pane = (CustomGlassPane)getGlassPane();
        pane.setBlurDisplay(blurBuffer);
        pane.setVisible(!usable);
    }

    /**
     * This method is called by the CaptionManager Observable when the caption changes.
     * In response, the JFrame caption is updated here.
     *
     * @param o   Unused.
     * @param arg Unused.
     */
    @Override
    public void update(Observable o, Object arg) {
        updateCaption();
    }

    protected void updateCaption() {
        setTitle(shortTitle);
    }

    protected void SetUpFrameManagement(String preferencePath, int width, int height) {
        int initialLeft = CONTROL_LEFT;
        int initialTop = CONTROL_TOP;
        positionManager = new FramePositionManager(preferencePath, this,
                initialLeft, initialTop, width, height);
        this.addComponentListener(positionManager);
    }

    public StatusBarPanel getStatusbar() {
        return statusbar;
    }

    public void setStatusBar(StatusBarPanel statusbar) {
        this.statusbar = statusbar;
        getContentPane().add(this.statusbar, BorderLayout.SOUTH);
    }

    public final void registerSplitter(JSplitPane splitter, String name, int initialPosition) {
        positionManager.registerSplitter(splitter, name, initialPosition);
    }
}
