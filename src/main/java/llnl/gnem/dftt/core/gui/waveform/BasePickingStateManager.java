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
package llnl.gnem.dftt.core.gui.waveform;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import llnl.gnem.dftt.core.dataAccess.dataObjects.SeismicPhase;
import llnl.gnem.dftt.core.gui.plotting.MouseMode;
import llnl.gnem.dftt.core.gui.plotting.PickCreationInfo;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.JMultiAxisPlot;
import llnl.gnem.dftt.core.gui.plotting.plotobject.Line;
import llnl.gnem.dftt.core.gui.util.SpringUtilities;
import llnl.gnem.dftt.core.gui.waveform.factory.WaveformViewerFactoryHolder;
import llnl.gnem.dftt.core.gui.waveform.phaseVisibility.PreferredPhaseListener;
import llnl.gnem.dftt.core.gui.waveform.phaseVisibility.UsablePhaseManager;
import llnl.gnem.dftt.core.util.ApplicationLogger;

public abstract class BasePickingStateManager extends JPanel implements KeyEventDispatcher, PreferredPhaseListener {

    private final Collection<JMultiAxisPlot> linkedPlots;
    private JComboBox phaseCombo;
    private final JToggleButton pickModeButton;
    private final JToggleButton normalModeButton;
    private JTextField phaseComboField;
    private static final Dimension CONTROL_SIZE = new Dimension(160, 25);
    private static final Dimension COMBO_SIZE = new Dimension(60, 18);
    private final String user;
    private KeyListener keyListener;

    public BasePickingStateManager(JMultiAxisPlot gui, UsablePhaseManager manager, String user) {
        super(new SpringLayout());
        this.user = user;
        linkedPlots = new ArrayList<>();
        linkedPlots.add(gui);
        JLabel label = new JLabel("Current ", JLabel.TRAILING);
        add(label);
        label.setFont(new Font("Arial", Font.PLAIN, 12));

        setUpPhaseCombo(manager.getUsablePhases(), label);

        // To allow the user to control this object while the keyboard focus is on the
        // picker, we need to be able to dispatch Keyboard events received by the picker
        // to this control. Therefore, we make this control a KeyEventDispatcher and
        // register it with the KeyboardFocusManager.
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);

        // Three Function keys are used by this control. Need to register a listener
        // to respond to the KeyPresses on F2, F3, F4.
        // Note this key listener also processes other key strokes for picking
        // changed it from anonymous instance so that children can replace it.
        setKeyListener(new FunctionKeyListener());

        // This button puts the picker into normal SELECT_ZOOM mode.
        String imageLoc = "miscIcons/selectMode16.gif";
        normalModeButton = addToggleButton(gui, imageLoc, "In this mode the mouse can be used to select, drag, zoom, etc. (F2 selects)");
        normalModeButton.setSelected(true);
        normalModeButton.addActionListener(new NormalModeChangeListener());

        // This button puts the picker into Pick mode.
        imageLoc = "miscIcons/pickMode16.gif";
        pickModeButton = addToggleButton(gui, imageLoc, "In this mode clicking on a trace sets a pick with the current phase on the selected trace. (F3 selects)");
        pickModeButton.addActionListener(new PickModeChangeListener());

        SpringUtilities.makeCompactGrid(this,
                1, 4, //rows, cols
                6, 2, //initX, initY
                2, 0);       //xPad, yPad

        setPreferredSize(CONTROL_SIZE);
        setMaximumSize(CONTROL_SIZE);
        this.setBorder(new LineBorder(Color.blue));
        this.setToolTipText("<html>Use this to control whether mouse clicks create new Picks.<br> <b>F2</b> puts mouse "
                + "in non-pick mode,<br> <b>F3</b> puts mouse in pick mode.</html>");

        manager.getPreferredPhaseManager().addListener(this);
    }

    private void setUpPhaseCombo(Collection<SeismicPhase> phases, JLabel label) {
        phaseCombo = new JComboBox(phases.toArray());
        phaseCombo.setMaximumSize(COMBO_SIZE);
        phaseCombo.setPreferredSize(COMBO_SIZE);
        phaseCombo.setToolTipText("The phase shown here will automatically be assigned to picks created using this control.");
        label.setLabelFor(phaseCombo);
        add(phaseCombo);
        phaseCombo.setEditable(true);

        // Set up the JTextField Combo editor with a custom document listener that will
        // implement auto-complete.
        Component comp = phaseCombo.getEditor().getEditorComponent();
        phaseComboField = (JTextField) comp;
        Document doc = phaseComboField.getDocument();
        doc.addDocumentListener(new MyDocumentListener());
    }

    @Override
    public void updatePhases() {
        updateForChangeInAllowablePhases();
    }

    public void updateForChangeInAllowablePhases() {
        UsablePhaseManager manager = WaveformViewerFactoryHolder.getInstance().getUsablePhaseManager();
        getPhaseCombo().removeAllItems();
        Collection<SeismicPhase> phases = manager.getUsablePhases();
        for (SeismicPhase phase : phases) {
            getPhaseCombo().addItem(phase.getName());
        }
    }

    public String getCurrentPhase() {
        return getPhaseComboField().getText();
    }

    public void setPhase(String phase) {
        getPhaseCombo().setSelectedItem(phase);
        getPhaseComboField().setText(phase);
    }

    private JToggleButton addToggleButton(Object owner, String imageLoc, String tooltip) {
        Dimension buttonSize = new Dimension(18, 18);
        ClassLoader cl = owner.getClass().getClassLoader();
        URL imageURL = cl.getResource(imageLoc);
        JToggleButton result;
        if (imageURL != null) {
            result = new JToggleButton(new ImageIcon(imageURL));
        } else {
            result = new JToggleButton("ImageNotFound");
        }

        result.setToolTipText(tooltip);
        result.setPreferredSize(buttonSize);
        result.setMaximumSize(buttonSize);
        add(result);
        return result;

    }

    public void exitPickMode() {
        getNormalModeButton().setSelected(true);
        getPickModeButton().setSelected(false);
        for (JMultiAxisPlot gui : getLinkedPlots()) {
            gui.setMouseMode(MouseMode.SELECT_ZOOM);
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        //In some applications the keyevent can result in the plots being replaced.
        //Without first copying to tmp, a ConcurrentModificationException may be thrown.
        List<JMultiAxisPlot> tmp = new ArrayList<>(getLinkedPlots());
        for (JMultiAxisPlot gui : tmp) {

            if (e.getSource() == gui) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_TAB) {
                    gui.maybeHandleTabKey(e);
                    return true;
                }
                if (isPsmControlKey(e)) {
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent(this, e);
                } else {
                    // If key is a character key send it off to the control for processing
                    // Within the control's KeyPressed handler, keystrokes for fast-mode
                    // handling will be interpreted.

                    KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent(this, e);

                    // When not in fast-mode, we will send characters directly to combo who's
                    // document listener will handle editing.
                    handlePickModeCharacters(e);
                }
            }
        }
        return false;
    }

    private void handlePickModeCharacters(KeyEvent e) {
        int code = e.getKeyCode();
        if (code != KeyEvent.VK_DELETE
                && code != KeyEvent.VK_CONTROL
                && code != KeyEvent.VK_ALT
                && code != KeyEvent.VK_ESCAPE) {
            if (!getNormalModeButton().isSelected()) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent(getPhaseComboField(),
                        e);
            }
        }
    }

    static boolean isPsmControlKey(KeyEvent e) {
        int code = e.getKeyCode();
        return code == KeyEvent.VK_F2 || code == KeyEvent.VK_F3 || code == KeyEvent.VK_F4
                || code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN;
    }

    protected abstract void maybeCreatePick(String phase, PickCreationInfo pci);

    public void replacePlots(Collection<? extends JMultiAxisPlot> plots) {
        getLinkedPlots().clear();
        getLinkedPlots().addAll(plots);
    }

    public void removePlots() {
        getLinkedPlots().clear();
    }

    public void addSinglePlot(JMultiAxisPlot plot) {
        getLinkedPlots().add(plot);
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * Expose the Normal Mode Button to subclasses.
     * @return
     */
    protected JToggleButton getNormalModeButton() {
        return normalModeButton;
    }

    /**
     *  Expose the Pick Mode Button to subclasses.
     * @return
     */
    protected JToggleButton getPickModeButton() {
        return pickModeButton;
    }

    /**
     *  Expose the Linked Plots to subclasses.
     * @return
     */
    protected Collection<JMultiAxisPlot> getLinkedPlots() {
        return linkedPlots;
    }

    /**
     *  Expose the Phase Combo Field to subclasses.
     * @return
     */
    protected JTextField getPhaseComboField() {
        return phaseComboField;
    }

    /**
     *  Expose the Phase Combo Box to subclasses.
     * @return
     */
    protected JComboBox getPhaseCombo() {
        return phaseCombo;
    }

    /**
     * Expose the key listener to subclasses.
     * @return KeyListener for the Picking State Manager
     */
    protected KeyListener getKeyListener() {
        return keyListener;
    }

    /**
     * Allow subclasses to change the key listener. Adds the new Key Listener to the AWT component handling keystrokes.
     *
     * Note this method does not remove the existing keyListener that was added to the AWT subsystem in our constructor.
     *
     * @param keyListener
     */
    protected void setKeyListener(KeyListener keyListener) {
        this.keyListener = keyListener;
        addKeyListener(keyListener);
    }

    class NormalModeChangeListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            getPickModeButton().setSelected(false);
            for (JMultiAxisPlot gui : getLinkedPlots()) {
                gui.setMouseMode(MouseMode.SELECT_ZOOM);
            }
        }
    }

    class PickModeChangeListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            getNormalModeButton().setSelected(false);
            for (JMultiAxisPlot gui : getLinkedPlots()) {
                gui.setMouseMode(MouseMode.CREATE_PICK);
            }
        }
    }

    class MyDocumentListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    displayEditInfo();
                }
            });
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
        }

        @Override
        public void changedUpdate(DocumentEvent e) {

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    displayEditInfo();
                }
            });
        }

        private void displayEditInfo() {
            try {
                String str = ((JTextComponent) getPhaseCombo().getEditor().getEditorComponent()).getText();
                if (str.length() < 1) {
                    return;
                }

                for (int j = 0; j < getPhaseCombo().getItemCount(); ++j) {
                    // First take care of exact matches...
                    Object o = getPhaseCombo().getItemAt(j);
                    String sTemp = o.toString();
                    if (sTemp.equals(str)) {
                        getPhaseCombo().setSelectedIndex(j);
                        return;
                    }

                    // Now handle input characters that form the start of a string in the combo box.
                    if (sTemp.startsWith(str)) {
                        getPhaseCombo().setSelectedIndex(j);
                        JTextComponent jtc = (JTextComponent) getPhaseCombo().getEditor().getEditorComponent();
                        jtc.setText(sTemp);
                        Caret c = jtc.getCaret();
                        c.setDot(sTemp.length());
                        c.moveDot(str.length());
                        break;
                    }
                }
            } catch (Exception ex) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Failed setting combo box from types characters!", ex);
            }
        }
    }

    class FunctionKeyListener implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_F2) {
                getNormalModeButton().setSelected(true);
                getPickModeButton().setSelected(false);
                for (JMultiAxisPlot gui : getLinkedPlots()) {
                    gui.setMouseMode(MouseMode.SELECT_ZOOM);
                }
            } else if (e.getKeyCode() == KeyEvent.VK_F3) {
                getNormalModeButton().setSelected(false);
                getPickModeButton().setSelected(true);
                for (JMultiAxisPlot gui : getLinkedPlots()) {
                    gui.setMouseMode(MouseMode.CREATE_PICK);
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

        @Override
        public void keyTyped(KeyEvent e) {
            String phase = ("" + e.getKeyChar()).toUpperCase();
            UsablePhaseManager manager = WaveformViewerFactoryHolder.getInstance().getUsablePhaseManager();
            if (manager.isAllowable(phase)
                    && getNormalModeButton().isSelected()) {
                getPhaseComboField().setText(phase);
                getPhaseCombo().setSelectedItem(phase);
                for (JMultiAxisPlot gui : getLinkedPlots()) {
                    PickCreationInfo pci = gui.getPickCreationInfo();

                    if (pci != null && pci.getOwningPlot() != null
                            && pci.getSelectedObject() != null) {
                        if (pci.getSelectedObject() instanceof Line) {
                            maybeCreatePick(phase, pci);
                        }
                    }
                }
            }
        }
    }
}
