package llnl.gnem.core.gui.waveform;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.*;
import llnl.gnem.core.util.ButtonAction;
import llnl.gnem.core.gui.util.WrapLayout;

/**
 * Created by dodge1 Date: Feb 12, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class WaveformViewerToolbar extends JToolBar {

    private static final long serialVersionUID = -6558189400337482476L;
    public WaveformViewerToolbar(WaveformViewer<? extends WaveformDataModel> owner) {
    	setLayout(new WrapLayout(FlowLayout.LEFT));

        for (ButtonAction action : owner.getToolbarActions()) {
            addButton(action.getButton());
        }
        setFloatable(false);
    }

    public void addSpacedButton(JButton button) {
        //addSeparator();
        add(button);
    }

    public void addRighthandComponent(Component c) {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.LINE_AXIS));
        rightPanel.add(Box.createHorizontalGlue());
        rightPanel.add(c);
        add(rightPanel);
    }

    protected final void addButton(AbstractButton button) {
        if (button.getIcon() != null) {
            button.setText(""); //an icon-only button
        }
        button.setPreferredSize(new Dimension(38, 36));
        button.setMaximumSize(new Dimension(38, 36));
        button.setMinimumSize(new Dimension(38, 36));
        add(button);
    }

}
