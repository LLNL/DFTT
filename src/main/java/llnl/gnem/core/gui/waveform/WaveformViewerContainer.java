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
package llnl.gnem.core.gui.waveform;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

import llnl.gnem.core.gui.util.ClosableTabComponent;
import llnl.gnem.core.gui.util.CrossDnDTabbedPane;
import llnl.gnem.core.gui.util.CustomGlassPane;
import llnl.gnem.core.gui.util.PersistentPositionContainer;

/**
 *
 * @author addair1
 */
public class WaveformViewerContainer extends PersistentPositionContainer {

    private final JTabbedPane tabbedPane;
    protected String fullTitle;

    public WaveformViewerContainer(String packageName, String titleBase,
            int width, int height, Image icon) {

        super(packageName, titleBase, width, height);
        fullTitle = shortTitle;

        setGlassPane(new CustomGlassPane());
        setIconImage(icon);
        updateCaption();
        statusbar.setVisible(true);

        tabbedPane = new CrossDnDTabbedPane() {
            @Override
            protected void handleNoTarget(CrossDnDTabbedPane source, int sourceIndex) {
                moveTab(sourceIndex);
            }

            @Override
            public void insertTab(String title, Icon icon, Component component, String tip, int index) {
                WaveformViewer viewer = (WaveformViewer) component;
                takeOwnership(viewer);
                super.insertTab(viewer.getTitle(), icon, viewer, tip, index);
                setTabComponentAt(index, new ClosableTabComponent(this));
            }

            @Override
            public void remove(int index) {
                // Hide containers without any tabs, the unused container should then be
                // garbage collected automatically
                super.remove(index);
                if (getTabCount() <= 0) {
                    WaveformViewerContainer.this.setVisible(false);
                }
            }
        };
        tabbedPane.addMouseListener(new TabPopupListener(tabbedPane));
        add(tabbedPane, BorderLayout.CENTER);

        getContentPane().add(tabbedPane, BorderLayout.CENTER);

    }
    
    public Collection<? extends WaveformPlot> getSelectedPlots()
    {
        Component component = tabbedPane.getSelectedComponent();
        WaveformViewer viewer = (WaveformViewer) component;
        return viewer.getPlots();
    }

    public WaveformViewerContainer(WaveformViewerContainer other) {
        this(other.getPreferencePath(), other.getTitle(), other.getWidth(), other.getHeight(), other.getIconImage());
    }

    public void moveTab(int tabIndex) {
        WaveformViewerContainer container = new WaveformViewerContainer(WaveformViewerContainer.this);
        moveTab(tabIndex, container);
        container.setLocation(MouseInfo.getPointerInfo().getLocation());
    }

    public void moveTab(int tabIndex, WaveformViewerContainer container) {
        if (tabIndex != -1) {
            WaveformViewer viewer = (WaveformViewer) tabbedPane.getComponentAt(tabIndex);
            tabbedPane.remove(tabIndex);
            container.takeOwnership(viewer);
            viewer.setActive(true);
        }
    }

    protected void takeOwnership(WaveformViewer viewer) {
        viewer.setOwner(this);
    }

    public void select(WaveformViewer viewer) {
        setVisible(true);
        int index = tabbedPane.indexOfTab(viewer.getTitle());
        if (index == -1) {
            tabbedPane.add(viewer.getTitle(), viewer);
            index = tabbedPane.getTabCount() - 1;
        }
        tabbedPane.setSelectedIndex(index);
        updateTitle(viewer.getTitle());
    }

    public void close(WaveformViewer viewer) {
        tabbedPane.remove(viewer);
    }

    @Override
    public void setUsableState(boolean usable, boolean blurBuffer) {
        Component component = getGlassPane();
        if (component instanceof CustomGlassPane) {
            CustomGlassPane pane = (CustomGlassPane) component;
            pane.setBlurDisplay(blurBuffer);
            pane.setVisible(!usable);
        }
    }

    protected void updateTitleBase(String newBase) {
        shortTitle = newBase;
        fullTitle = shortTitle;
        setTitle(fullTitle);
    }

    private void updateTitle(String label) {
        setTitle(shortTitle + " : " + label + " View");
    }

    @Override
    protected final void updateCaption() {
        setTitle(fullTitle);
    }

    public void addMenu(WaveformViewMenu menu) {
        this.setJMenuBar(menu);
    }

    public class TabPopupListener implements MouseListener, ActionListener {

        private final JTabbedPane pane;
        private final JPopupMenu popup;
        private int tabIndex;
        private final JMenuItem menuItem;

        public TabPopupListener(JTabbedPane pane) {
            this.pane = pane;

            popup = new JPopupMenu();
            menuItem = new JMenuItem("Move Tab into New Window");
            menuItem.addActionListener(this);
            popup.add(menuItem);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            showPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            showPopup(e);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            moveTab(tabIndex);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        private void showPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                tabIndex = pane.getUI().tabForCoordinate(pane, e.getX(), e.getY());
                if (tabIndex != -1) {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }
    }

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}
}
