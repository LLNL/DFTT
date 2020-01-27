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
package llnl.gnem.core.gui.waveform.recsec;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import llnl.gnem.core.gui.filter.FilterGuiContainer;
import llnl.gnem.core.gui.waveform.factory.WaveformViewerFactoryHolder;
import llnl.gnem.core.gui.waveform.BasePickingStateManager;
import llnl.gnem.core.gui.waveform.WaveformViewer;
import llnl.gnem.core.gui.waveform.WaveformViewerToolbar;
import llnl.gnem.core.gui.util.Utility;

/**
 *
 * @author dodge1
 */
public class RsToolbar extends WaveformViewerToolbar {

    public static final String EQUALLY_SPACED_TEXT = " View equally spaced.";
    public static final String NO_REDUCTION_TEXT = "Do not reduce times.";
    public static final String RECORD_SECTION_TEXT = "View as Record Section.";
    private static final String REDUCTION_TEXT = "Reduce times by predicted P.";
    private static final long serialVersionUID = 7667055050296854890L;
    private final ImageIcon preductionIcon = Utility.getIcon(this, "miscIcons/preduc32.gif");
    private final ImageIcon noReductionIcon = Utility.getIcon(this, "miscIcons/noPreduc32.gif");
    private final ImageIcon equalSpaceIcon = Utility.getIcon(this, "miscIcons/equalSpace32.gif");
    private final ImageIcon recSecIcon = Utility.getIcon(this, "miscIcons/recsec232.gif");
    private JToggleButton scaleTypeButton;
    private JToggleButton reductionTypeButton;
    private JToggleButton renderPolicyButton;

    public RsToolbar(BasePickingStateManager psMgr, FilterGuiContainer container,
            WaveformViewer owner) {
        super(owner);
        addReductionTimeControlButton();
        addDistanceRenderPolicyButton();
    }

    private void addScaleControlButton() {
        RecordSectionViewerProperties props = RecordSectionViewerProperties.getInstance();
        scaleTypeButton = new JToggleButton(Utility.getIcon(this, "miscIcons/showSelected.gif"));
        scaleTypeButton.setSelected(props.getScalingType() == ScalingType.Relative);
        scaleTypeButton.setToolTipText("Select to set scaling type to relative.");

        scaleTypeButton.addActionListener(new ScaleTypeButtonActionListener());

        scaleTypeButton.setPreferredSize(new Dimension(22, 22));
        scaleTypeButton.setMaximumSize(new Dimension(22, 22));
        add(scaleTypeButton);
    }

    private void addReductionTimeControlButton() {
        RecordSectionViewerProperties props = RecordSectionViewerProperties.getInstance();
        boolean isSelected = props.getTimeReductionType() == TimeReductionType.Ptime;
        reductionTypeButton = new JToggleButton();

        setButtonState(isSelected);
        reductionTypeButton.addActionListener(new ReductionTypeActionListener());

        reductionTypeButton.setPreferredSize(new Dimension(38, 36));
        reductionTypeButton.setMaximumSize(new Dimension(38, 36));
        add(reductionTypeButton);

    }

    private void setButtonState(boolean isSelected) {
        reductionTypeButton.setSelected(isSelected);
        reductionTypeButton.setIcon(isSelected ? noReductionIcon : preductionIcon);
        reductionTypeButton.setToolTipText(isSelected ? NO_REDUCTION_TEXT : REDUCTION_TEXT);
    }

    private void addDistanceRenderPolicyButton() {
        DistanceRenderPolicyPrefs prefs = DistanceRenderPolicyPrefs.getInstance();
        boolean isSelected = prefs.getPolicy() == DistanceRenderPolicy.PRESERVE_EXACT_DISTANCE;
        renderPolicyButton = new JToggleButton();
        setDistanceRenderButtonState(isSelected);
        renderPolicyButton.addActionListener(new RenderPolicyActionListener());

        renderPolicyButton.setPreferredSize(new Dimension(38, 36));
        renderPolicyButton.setMaximumSize(new Dimension(38, 36));
        add(renderPolicyButton);

    }

    private void setDistanceRenderButtonState(boolean isSelected) {
        renderPolicyButton.setSelected(isSelected);
        renderPolicyButton.setIcon(isSelected ? equalSpaceIcon : recSecIcon);
        renderPolicyButton.setToolTipText(isSelected ? EQUALLY_SPACED_TEXT : RECORD_SECTION_TEXT);
    }

    class ScaleTypeButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            RecordSectionViewerProperties props = RecordSectionViewerProperties.getInstance();
            ScalingType type = scaleTypeButton.isSelected() ? ScalingType.Relative : ScalingType.Fixed;
            props.setScalingType(type);
            WaveformViewerFactoryHolder.getInstance().getMultiStationWaveformView().setWaveformScalingType(type);
        }
    }

    private class ReductionTypeActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            RecordSectionViewerProperties props = RecordSectionViewerProperties.getInstance();
            TimeReductionType type = reductionTypeButton.isSelected() ? TimeReductionType.Ptime : TimeReductionType.None;
            props.setTimeReductionType(type);
            boolean isSelected = props.getTimeReductionType() == TimeReductionType.Ptime;
            setButtonState(isSelected);
            WaveformViewerFactoryHolder.getInstance().getMultiStationWaveformView().setTimeReductionType(type);
        }
    }

    private class RenderPolicyActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            DistanceRenderPolicyPrefs prefs = DistanceRenderPolicyPrefs.getInstance();
            prefs.setPolicy(renderPolicyButton.isSelected() ? DistanceRenderPolicy.PRESERVE_EXACT_DISTANCE : DistanceRenderPolicy.ORDER_BY_DISTANCE);
            boolean isSelected = prefs.getPolicy() == DistanceRenderPolicy.PRESERVE_EXACT_DISTANCE;

            setDistanceRenderButtonState(isSelected);
            WaveformViewerFactoryHolder.getInstance().getMultiStationWaveformView().distanceRenderPolicyChanged(prefs.getPolicy());
        }
    }
}
