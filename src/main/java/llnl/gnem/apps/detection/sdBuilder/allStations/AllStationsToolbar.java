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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;
import javax.swing.*;
import llnl.gnem.apps.detection.sdBuilder.BuilderFilterContainer;
import llnl.gnem.apps.detection.sdBuilder.allStations.actions.DefineEventAction;
import llnl.gnem.apps.detection.sdBuilder.allStations.actions.ExitAction;
import llnl.gnem.apps.detection.sdBuilder.allStations.actions.ExportAction;
import llnl.gnem.apps.detection.sdBuilder.allStations.actions.MagnifyAction;
import llnl.gnem.apps.detection.sdBuilder.allStations.actions.PrintAction;
import llnl.gnem.apps.detection.sdBuilder.allStations.actions.ReduceAction;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.core.gui.filter.FilterToolbarControl;

import llnl.gnem.core.gui.plotting.ZoomType;
import llnl.gnem.core.gui.util.Utility;
import llnl.gnem.core.gui.waveform.factory.FilterComponentFactoryHolder;
import llnl.gnem.core.util.ButtonAction;

/**
 * Created by dodge1 Date: Feb 12, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class AllStationsToolbar extends JToolBar {

    private static final long serialVersionUID = -8766192001472128982L;

    private final MultiSeismogramPlot viewer;
    private ZoomType zoomType;
    private final FilterToolbarControl control;

    public AllStationsToolbar(MultiSeismogramPlot viewer) {
        super();
        this.viewer = viewer;
        JButton button = new JButton(ExitAction.getInstance(this));
        addButton(button);
        this.addSeparator();

        button = new JButton(ExportAction.getInstance(this));
        addButton(button);

        button = new JButton(PrintAction.getInstance(this));
        addButton(button);
        this.addSeparator();

        this.addSeparator();
        button = new JButton(MagnifyAction.getInstance(this));
        addButton(button);

        button = new JButton(ReduceAction.getInstance(this));
        addButton(button);
        ZoomTypeState state = new ZoomTypeState();
        button = new JButton(state);
        Object obj = state.getValue("LARGE_ICON_KEY");
        if (obj != null && obj instanceof ImageIcon) {
            ImageIcon io = (ImageIcon) obj;
            button.setIcon(io);
        }
        addButton(button);
        
        control = new BuilderFilterContainer(SeismogramModel.getInstance()).getFilterToolbarControl();
        add(control);
        
        button = new JButton(DefineEventAction.getInstance(this));
        addButton(button);
        
        
        
    }

    private void addButton(JButton button) {
        if (button.getIcon() != null) {
            button.setText(""); //an icon-only button
        }
        button.setPreferredSize(new Dimension(38, 36));
        button.setMaximumSize(new Dimension(38, 36));
        add(button);
    }

    public class ZoomTypeState extends ButtonAction {

        private final Preferences prefs;
        private String description;

        public ZoomTypeState() {
            super("Set Zoom-Type", Utility.getIcon(AllStationsToolbar.this, "miscIcons/boxzoom32.gif"));

            prefs = Preferences.userNodeForPackage(this.getClass());

            String type = prefs.get("ZOOM_TYPE", "Zoom_Box");
            ImageIcon icon;
            if (type.equals(ZoomType.ZOOM_BOX.toString())) {
                setZoomType(ZoomType.ZOOM_BOX);
                description = "Click to change to Zoom-All";
                icon = Utility.getIcon(AllStationsToolbar.this, "miscIcons/boxzoom32.gif");
            } else {
                setZoomType(ZoomType.ZOOM_ALL);
                description = "Click to change to Box-Zoom";
                icon = Utility.getIcon(AllStationsToolbar.this, "miscIcons/zoomall32.gif");
            }
            putValue(SHORT_DESCRIPTION, description);

            putValue("SMALL_ICON", icon);
            putValue("LARGE_ICON_KEY", icon);

            updateZoomTypes();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source instanceof JButton) {
                JButton button = (JButton) source;
                String type = prefs.get("ZOOM_TYPE", "Zoom_Box");
                ImageIcon icon;
                if (type.equals(ZoomType.ZOOM_BOX.toString())) {
                    setZoomType(ZoomType.ZOOM_ALL);
                    description = "Click to change to Box-Zoom";
                    icon = Utility.getIcon(this, "miscIcons/zoomall32.gif");
                } else {
                    setZoomType(ZoomType.ZOOM_BOX);
                    description = "Click to change to Zoom-All";
                    icon = Utility.getIcon(this, "miscIcons/boxzoom32.gif");
                }
                putValue(SHORT_DESCRIPTION, description);
                putValue("SMALL_ICON", icon);
                button.setIcon(icon);
                button.setToolTipText(description);
                prefs.put("ZOOM_TYPE", zoomType.toString());
                updateZoomTypes();
            }
        }
    }

    private void setZoomType(ZoomType zoomType) {
        this.zoomType = zoomType;
    }

    public void updateZoomTypes() {
        viewer.setZoomType(zoomType);

    }
    
    
    public void applyCurrentFilter() {
        control.applyCurrentFilter();
    }

}
