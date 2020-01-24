package llnl.gnem.apps.detection.sdBuilder.multiStationStack;

import llnl.gnem.apps.detection.sdBuilder.allStations.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;
import javax.swing.*;
import llnl.gnem.apps.detection.sdBuilder.BuilderFilterContainer;
import llnl.gnem.apps.detection.sdBuilder.multiStationStack.actions.ExitAction;
import llnl.gnem.apps.detection.sdBuilder.multiStationStack.actions.ExportAction;
import llnl.gnem.apps.detection.sdBuilder.multiStationStack.actions.MagnifyAction;
import llnl.gnem.apps.detection.sdBuilder.multiStationStack.actions.PrintAction;
import llnl.gnem.apps.detection.sdBuilder.multiStationStack.actions.ReduceAction;
import llnl.gnem.core.gui.filter.FilterToolbarControl;

import llnl.gnem.core.gui.plotting.ZoomType;
import llnl.gnem.core.gui.util.Utility;
import llnl.gnem.core.util.ButtonAction;

/**
 * Created by dodge1 Date: Feb 12, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class MultiStationStackToolbar extends JToolBar {

    private static final long serialVersionUID = -8766192001472128982L;

    private final MultiStationStackPlot viewer;
    private ZoomType zoomType;
    private final FilterToolbarControl control;

    public MultiStationStackToolbar(MultiStationStackPlot viewer) {
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
        
        control = new BuilderFilterContainer(MultiStationStackModel.getInstance()).getFilterToolbarControl();
        add(control);
        
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
            super("Set Zoom-Type", Utility.getIcon(MultiStationStackToolbar.this, "miscIcons/boxzoom32.gif"));

            prefs = Preferences.userNodeForPackage(this.getClass());

            String type = prefs.get("ZOOM_TYPE", "Zoom_Box");
            ImageIcon icon;
            if (type.equals(ZoomType.ZOOM_BOX.toString())) {
                setZoomType(ZoomType.ZOOM_BOX);
                description = "Click to change to Zoom-All";
                icon = Utility.getIcon(MultiStationStackToolbar.this, "miscIcons/boxzoom32.gif");
            } else {
                setZoomType(ZoomType.ZOOM_ALL);
                description = "Click to change to Box-Zoom";
                icon = Utility.getIcon(MultiStationStackToolbar.this, "miscIcons/zoomall32.gif");
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
