/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.stackViewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import llnl.gnem.apps.detection.sdBuilder.actions.BuildStackBeamAction;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.SeismogramViewer;
import llnl.gnem.dftt.core.correlation.CorrelationComponent;
import llnl.gnem.dftt.core.gui.plotting.MouseMode;
import llnl.gnem.dftt.core.gui.util.Utility;

/**
 *
 * @author dodge1
 */
public class StackViewerPanel extends JPanel implements SeismogramViewer {

    private final StackViewer stackViewer;

    public StackViewerPanel() {
        super(new BorderLayout());
        stackViewer = new StackViewer();
        JToolBar stackToolBar = new JToolBar();

        JButton button = new JButton(BuildStackBeamAction.getInstance(this));
        addButton(button, stackToolBar);

        ImageIcon imageIcon = new ImageIcon(Utility.getIcon(this, "miscIcons/showerror16.gif").getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        JCheckBox showWindowCheck = new JCheckBox(imageIcon);
        imageIcon = new ImageIcon(Utility.getIcon(this, "miscIcons/viewStack.gif").getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        showWindowCheck.setSelectedIcon(imageIcon);
        boolean showCorrelationWindow = FKWindowParams.getInstance().isShowFKWindow();
        showWindowCheck.setSelected(showCorrelationWindow);
        showWindowCheck.addActionListener(new WindowCheckActionListener());
        stackToolBar.add(showWindowCheck);

        add(stackToolBar, BorderLayout.NORTH);
        add(stackViewer, BorderLayout.CENTER);
    }

    public StackViewer getStackViewer() {
        return stackViewer;
    }

    private void addButton(JButton button, JToolBar toolbar) {
        if (button.getIcon() != null) {
            button.setText(""); //an icon-only button
        }
        button.setPreferredSize(new Dimension(38, 36));
        button.setMaximumSize(new Dimension(38, 36));
        toolbar.add(button);
    }

    public void setCorrelationWindowVisible(boolean value) {
        stackViewer.setCorrelationWindowVisible(value);
    }

    public void magnifyTraces() {
        stackViewer.magnifyTraces();
    }

    public void reduceTraces() {
        stackViewer.reduceTraces();
    }

    public void setCorrelationWindowLength(double newLength) {
        stackViewer.setCorrelationWindowLength(newLength);
    }

    public void setCorrelationWindowStart(double newStart) {
        stackViewer.setCorrelationWindowStart(newStart);
    }

    @Override
    public void setMouseMode(MouseMode mode) {
        stackViewer.setMouseMode(mode);
    }

    @Override
    public void clear() {
        stackViewer.clear();
    }

    @Override
    public void dataWereLoaded(boolean b) {
        stackViewer.dataWereLoaded(b);
    }

    @Override
    public void updateForFailedCorrelation() {
        stackViewer.updateForFailedCorrelation();
    }

    @Override
    public void loadClusterResult() {
        stackViewer.loadClusterResult();
    }

    @Override
    public void updateForChangedTrace() {
        stackViewer.updateForChangedTrace();
    }

    @Override
    public void maybeHighlightTrace(CorrelationComponent cc) {
        stackViewer.maybeHighlightTrace(cc);
    }

    @Override
    public void adjustWindow(double windowStart, double winLen) {
        stackViewer.adjustWindow(windowStart, winLen);
    }

    @Override
    public void displayAllPicks() {
        stackViewer.displayAllPicks();
    }

    @Override
    public void clearAllPicks() {
        stackViewer.clearAllPicks();
    }

    public void zoomToNewXLimits(double start, double end) {
        stackViewer.zoomToNewXLimits(start, end);
    }

    private class WindowCheckActionListener implements ActionListener {

        public WindowCheckActionListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source instanceof JCheckBox) {
                JCheckBox jcb = (JCheckBox) source;
                boolean selected = jcb.isSelected();
                FKWindowParams.getInstance().setShowFKWindow(selected);
                stackViewer.setfkWindowVisible(selected);
            }
        }
    }

}
