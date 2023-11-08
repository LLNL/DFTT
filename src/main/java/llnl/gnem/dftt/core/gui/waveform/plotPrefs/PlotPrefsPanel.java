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
package llnl.gnem.dftt.core.gui.waveform.plotPrefs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author dodge1
 */
public class PlotPrefsPanel extends JPanel {

    private final PlotPresentationPrefs prefs;
    private final BorderRegionPanel borderPanel;
    private final DrawingRegionPanel centerPanel;
    private final AxisPrefsPanel xPrefsPanel;
    private final AxisPrefsPanel yPrefsPanel;
    private final PickPrefsPanel pickPrefsPanel;
    private final PickPrefsPanel predPickPrefsPanel;
    private final MiscPropertiesPanel miscPropertiesPanel;

    public PlotPrefsPanel(PlotPresentationPrefs prefs) {
        super(new BorderLayout());
        this.prefs = prefs;

        JTabbedPane tabbedPane = new JTabbedPane();

        borderPanel = new BorderRegionPanel(prefs.getBorderPrefs());
        tabbedPane.addTab("Border", null, borderPanel);

        centerPanel = new DrawingRegionPanel(prefs.getPlotRegionPrefs());
        tabbedPane.addTab("Interior", null, centerPanel);

        xPrefsPanel = new AxisPrefsPanel(prefs.getxAxisPrefs());
        tabbedPane.addTab("X-Axis", null, xPrefsPanel);

        yPrefsPanel = new AxisPrefsPanel(prefs.getyAxisPrefs());
        tabbedPane.addTab("Y-Axis", null, yPrefsPanel);

        pickPrefsPanel = new PickPrefsPanel(prefs.getPickPrefs());
        tabbedPane.addTab("Pick", null, pickPrefsPanel);

        predPickPrefsPanel = new PickPrefsPanel(prefs.getPredPickPrefs());
        tabbedPane.addTab("Pred-Pick", null, predPickPrefsPanel);

        miscPropertiesPanel = new MiscPropertiesPanel(prefs);
        tabbedPane.addTab("Miscellaneous", null, miscPropertiesPanel);

        add(tabbedPane, BorderLayout.CENTER);
        this.setBorder(BorderFactory.createLineBorder(Color.blue));
        setPreferredSize(new Dimension(500, 350));
    }

    void updatePrefsFromControls() {
        borderPanel.updatePrefsFromControls();
        centerPanel.updatePrefsFromControls();
        xPrefsPanel.updatePrefsFromControls();
        yPrefsPanel.updatePrefsFromControls();
        pickPrefsPanel.updatePrefsFromControls();
        predPickPrefsPanel.updatePrefsFromControls();

        miscPropertiesPanel.updatePrefsFromControls();
    }
}
