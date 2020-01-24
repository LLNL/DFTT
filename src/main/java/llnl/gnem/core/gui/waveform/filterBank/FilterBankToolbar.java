/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform.filterBank;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import llnl.gnem.core.gui.filter.FilterGuiContainer;
import llnl.gnem.core.gui.waveform.factory.WaveformViewerFactoryHolder;
import llnl.gnem.core.gui.waveform.BasePickingStateManager;
import llnl.gnem.core.gui.waveform.WaveformViewer;
import llnl.gnem.core.gui.waveform.WaveformViewer;
import llnl.gnem.core.gui.waveform.WaveformViewerToolbar;
import llnl.gnem.core.gui.util.Utility;

/**
 *
 * @author dodge1
 */
public class FilterBankToolbar extends WaveformViewerToolbar {

    public FilterBankToolbar(WaveformViewer owner) {
        super(owner);
    }
}
