package llnl.gnem.core.gui.waveform;

import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import llnl.gnem.core.gui.waveform.StationNavigationModel.NextAction;
import llnl.gnem.core.gui.waveform.StationNavigationModel.PreviousAction;
import llnl.gnem.core.gui.waveform.factory.actions.SaveAction;
import llnl.gnem.core.gui.waveform.ThreeComponentViewer.ApplyTaperAction;
import llnl.gnem.core.gui.waveform.ThreeComponentViewer.AutoScaleAction;
import llnl.gnem.core.gui.waveform.ThreeComponentViewer.DifferentiateAction;
import llnl.gnem.core.gui.waveform.ThreeComponentViewer.IntegrateAction;
import llnl.gnem.core.gui.waveform.ThreeComponentViewer.RemoveTrendAction;
import llnl.gnem.core.gui.waveform.ThreeComponentViewer.RotateToGcpAction;
import llnl.gnem.core.gui.waveform.ThreeComponentViewer.TransferAction;
import llnl.gnem.core.gui.waveform.WaveformViewer.MagnifyAction;
import llnl.gnem.core.gui.waveform.WaveformViewer.ReduceAction;
import llnl.gnem.core.gui.waveform.WaveformViewer.UnzoomAllAction;
import llnl.gnem.core.gui.util.ViewLogAction;

/**
 * Created by dodge1 Date: Feb 16, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class WaveformViewMenu extends JMenuBar {
    public WaveformViewMenu(WaveformViewerContainer container) {
        //makeFileMenu(viewer);
        //makeEditMenu();
        //makeViewMenu(viewer);
    }

    private void makeFileMenu(WaveformViewer viewer) {
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);

        menu.add(new JMenuItem(viewer.getButtonAction(PreviousAction.class)));
        menu.add(new JMenuItem(viewer.getButtonAction(NextAction.class)));

        JMenuItem item = new JMenuItem(SaveAction.getInstance(this));
        menu.add(item);

        menu.addSeparator();
        menu.addSeparator();
        item = new JMenuItem(viewer.getButtonAction(WaveformViewer.ExitAction.class));
        menu.add(item);
        add(menu);
    }

    private void makeEditMenu(WaveformViewer viewer) {
        JMenu menu = new JMenu("Edit");
        menu.setMnemonic(KeyEvent.VK_E);
        menu.add(new JMenuItem(viewer.getButtonAction(TransferAction.class)));
        menu.add(new JMenuItem(viewer.getButtonAction(RotateToGcpAction.class)));
        menu.add(new JMenuItem(viewer.getButtonAction(IntegrateAction.class)));
        menu.add(new JMenuItem(viewer.getButtonAction(DifferentiateAction.class)));
        menu.add(new JMenuItem(viewer.getButtonAction(ApplyTaperAction.class)));
        menu.add(new JMenuItem(viewer.getButtonAction(RemoveTrendAction.class)));

        menu.addSeparator();
        //menu.add(new JMenuItem(UndoAction.getInstance(this)));
        //menu.add(new JMenuItem(RedoAction.getInstance(this)));

        add(menu);
    }

    private void makeViewMenu(WaveformViewer viewer) {
        JMenu menu = new JMenu("View");
        menu.setMnemonic(KeyEvent.VK_V);

        menu.add(new JMenuItem(viewer.getActionMap().get(MagnifyAction.class)));
        menu.add(new JMenuItem(viewer.getActionMap().get(ReduceAction.class)));
        menu.add(new JMenuItem(viewer.getActionMap().get(AutoScaleAction.class)));
        menu.add(new JMenuItem(viewer.getActionMap().get(UnzoomAllAction.class)));

        menu.addSeparator();
        menu.add(new JMenuItem(ViewLogAction.getInstance(this)));

        add(menu);
    }
}