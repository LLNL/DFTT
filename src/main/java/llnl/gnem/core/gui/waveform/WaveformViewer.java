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
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.Action;
import static javax.swing.Action.SHORT_DESCRIPTION;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import llnl.gnem.core.gui.plotting.JPlotContainer;
import llnl.gnem.core.gui.util.ExceptionDialog;
import llnl.gnem.core.gui.util.StatusBarPanel;
import llnl.gnem.core.gui.util.Utility;
import llnl.gnem.core.gui.waveform.phaseVisibility.PreferredPhaseDialogFrame;
import llnl.gnem.core.util.ButtonAction;
import llnl.gnem.core.util.Command;
import llnl.gnem.core.util.CommandManager;
import llnl.gnem.core.util.Invokable;
import org.apache.batik.svggen.SVGGraphics2DIOException;

public abstract class WaveformViewer<T extends WaveformDataModel> extends JPanel implements WaveformView, Invokable {

    private static final long serialVersionUID = 6372831087522593705L;

    private final T dataModel;
    private final String label;
    private final Collection<ButtonAction> orderedActions;
    private WaveformViewerContainer owner;
    private final RedoAction redoAction;
    private final UndoAction undoAction;

    public WaveformViewer(WaveformViewerContainer owner, String label, T dataModel) {
        super(new BorderLayout());

        this.owner = owner;
        this.label = label;
        this.dataModel = dataModel;
        orderedActions = new ArrayList<>();

        addToolbarAction(new ExitAction());
        addToolbarAction(new ExportAction());
        addToolbarAction(new PrintAction());
        addToolbarAction(new MagnifyAction());
        addToolbarAction(new ReduceAction());
        addToolbarAction(new UnzoomAllAction());

        undoAction = new UndoAction();
        redoAction = new RedoAction();

        addToolbarAction(undoAction);
        addToolbarAction(redoAction);
        addToolbarAction(new OpenPhaseDialogAction());

        dataModel.addView(this);
    }

    public void addToolbar(WaveformViewerToolbar toolbar) {
        add(toolbar, BorderLayout.NORTH);
    }

    public void clear() {
        if (getCommandManager() != null) {
            getCommandManager().clear();
        }
    }

    public class OpenPhaseDialogAction extends ButtonAction {

        private static final long serialVersionUID = 205555473662302906L;

        private OpenPhaseDialogAction() {
            super("Preferred Phase Preferences", Utility.getIcon(WaveformViewer.this, "miscIcons/editDlg32.gif"));
            putValue(SHORT_DESCRIPTION, "Open the preferred phase selection dialog.");
            setEnabled(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            PreferredPhaseDialogFrame.getInstance().setVisible(true);
        }

    }

    public void exportSVG() {
        FileFilter svgFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".svg");
            }

            @Override
            public String getDescription() {
                return "SVG Files";
            }
        };

        JFileChooser chooser = new JFileChooser();
        chooser.addChoosableFileFilter(svgFilter);
        chooser.setFileFilter(svgFilter);
        File saveFile = new File("plot.svg");
        chooser.setSelectedFile(saveFile);
        int rval = chooser.showSaveDialog(null);
        if (rval == JFileChooser.APPROVE_OPTION) {
            saveFile = chooser.getSelectedFile();

            try {
                Collection<? extends WaveformPlot> plots = getPlots();
                if (plots.size() == 1) {
                    for (WaveformPlot plot : plots) {
                        plot.exportSVG(saveFile);
                    }

                } else {
                    int i = 0;
                    for (WaveformPlot plot : plots) {
                        String filename = saveFile.getAbsolutePath().replaceFirst("\\.svg$", "");
                        filename += "." + i + ".svg";
                        plot.exportSVG(filename);
                        i++;
                    }
                }
            } catch (UnsupportedEncodingException | FileNotFoundException | SVGGraphics2DIOException e) {
                ExceptionDialog.displayError(e);
            }
        }
    }

    @Override
    public void setActive(boolean active) {
        if (active) {
            owner.select(this);
        } else {
            owner.close(this);
        }
    }

    public ButtonAction getButtonAction(Class<? extends ButtonAction> className) {
        Action localAction = getActionMap().get(className);
        ButtonAction result = null;
        if (localAction != null && localAction instanceof ButtonAction) {
            result = (ButtonAction) localAction;
        }

        if (result == null) {
            throw new IllegalStateException("No mapped action found for "
                    + className.toString() + " in " + getTitle() + " viewer");
        }

        return result;
    }

    public T getDataModel() {
        if (dataModel == null) {
            throw new IllegalStateException("No data model found for waveform viewer.");
        }
        return dataModel;
    }

    public abstract Collection<? extends WaveformPlot> getPlots();

    public void magnifyTraces() {
        for (WaveformPlot plot : getPlots()) {
            plot.magnify();
        }
    }

    public void print() {
        JPlotContainer.printAllPlots(getPlots());
    }

    public void reduceTraces() {
        for (WaveformPlot plot : getPlots()) {
            plot.reduce();
        }
    }

    public void unzoomAllTraces() {
        for (WaveformPlot plot : getPlots()) {
            plot.unzoomAll();
            plot.repaint();
        }
    }

    protected abstract CommandManager getCommandManager();

    public WaveformViewerContainer getOwner() {
        return owner;
    }

    public void setOwner(WaveformViewerContainer owner) {
        this.owner = owner;
    }

    public StatusBarPanel getStatusbar() {
        return owner.getStatusbar();
    }

    public String getTitle() {
        return label;
    }

    public Collection<ButtonAction> getToolbarActions() {
        return new ArrayList<>(orderedActions);
    }

    public void setUsable(boolean usable) {
        owner.setUsableState(usable, true);
        if (usable) {
            grabFocus();
        }
    }

    @Override
    public void invoke(Command command) {
        if (getCommandManager() != null) {
            getCommandManager().invokeCommand(command);
        }
    }

    @Override
    public String toString() {
        return getTitle();
    }

    protected final void addButtonAction(ButtonAction action) {
        getActionMap().put(action.getClass(), action);

        if (action.hasMnemonic()) {
            getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(action.getMnemonic(), 0, true), action.getClass());
        }
    }

    protected final void addToolbarAction(ButtonAction action) {
        orderedActions.add(action);
        addButtonAction(action);
    }

    protected final void addToolbarAction(ButtonAction action, int pos) {
        if (pos >= 0 && pos <= orderedActions.size()) {
            ArrayList<ButtonAction> tmp = new ArrayList<>(orderedActions);
            orderedActions.clear();
            for (int j = 0; j < tmp.size(); ++j) {
                if (j == pos) {
                    orderedActions.add(action);
                    addButtonAction(action);
                }
                orderedActions.add(tmp.get(j));
            }
        }
    }

    protected void exit() {
        owner.setVisible(false);
    }

    protected final RedoAction getRedoAction() {
        return redoAction;
    }

    protected final UndoAction getUndoAction() {
        return undoAction;
    }

    @SuppressWarnings(value = {"NonThreadSafeLazyInitialization"})
    public class ExitAction extends ButtonAction {

        private ExitAction() {
            super("Exit", Utility.getIcon(WaveformViewer.this, "miscIcons/exit32.gif"));
            putValue(SHORT_DESCRIPTION, "Click to close this dialog.");
            //putValue(MNEMONIC_KEY, KeyEvent.VK_ESCAPE);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            exit();
        }
    }

    @SuppressWarnings(value = {"NonThreadSafeLazyInitialization"})
    public class ExportAction extends ButtonAction {

        private ExportAction() {
            super("Export to SVG", Utility.getIcon(WaveformViewer.this, "miscIcons/export32.gif"));
            putValue(SHORT_DESCRIPTION, "Click to export this seismogram to SVG");
            //putValue(MNEMONIC_KEY, KeyEvent.VK_ESCAPE);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            exportSVG();
        }
    }

    @SuppressWarnings(value = {"NonThreadSafeLazyInitialization"})
    public class MagnifyAction extends ButtonAction {

        public MagnifyAction() {
            super("Magnify", Utility.getIcon(WaveformViewer.this, "miscIcons/pageup32.gif"));
            putValue(SHORT_DESCRIPTION, "Increase Magnification");
            putValue(MNEMONIC_KEY, KeyEvent.VK_UP);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            magnifyTraces();
        }
    }

    @SuppressWarnings(value = {"NonThreadSafeLazyInitialization"})
    public class PrintAction extends ButtonAction {

        private PrintAction() {
            super("Export to SVG", Utility.getIcon(WaveformViewer.this, "miscIcons/print32.gif"));
            putValue(SHORT_DESCRIPTION, "Click to print this seismogram");
            //putValue(MNEMONIC_KEY, KeyEvent.VK_ESCAPE);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            print();
        }
    }

    @SuppressWarnings(value = {"NonThreadSafeLazyInitialization"})
    public class RedoAction extends ButtonAction {

        private RedoAction() {
            super("Redo", Utility.getIcon(WaveformViewer.this, "miscIcons/redo32.gif"));
            putValue(SHORT_DESCRIPTION, "Redo last waveform operation");
            putValue(MNEMONIC_KEY, KeyEvent.VK_R);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (getCommandManager() != null) {
                getCommandManager().redo();
            }
        }
    }

    @SuppressWarnings(value = {"NonThreadSafeLazyInitialization"})
    public class ReduceAction extends ButtonAction {

        public ReduceAction() {
            super("Reduce", Utility.getIcon(WaveformViewer.this, "miscIcons/pagedown32.gif"));
            putValue(SHORT_DESCRIPTION, "Decrease Magnification");
            putValue(MNEMONIC_KEY, KeyEvent.VK_DOWN);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            reduceTraces();
        }
    }

    @SuppressWarnings(value = {"NonThreadSafeLazyInitialization"})
    public class UndoAction extends ButtonAction {

        private UndoAction() {
            super("Undo", Utility.getIcon(WaveformViewer.this, "miscIcons/undo32.gif"));
            putValue(SHORT_DESCRIPTION, "Undo last waveform operation");
            putValue(MNEMONIC_KEY, KeyEvent.VK_U);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (getCommandManager() != null) {
                getCommandManager().undo();
            }
        }
    }

    @SuppressWarnings(value = {"NonThreadSafeLazyInitialization"})
    public class UnzoomAllAction extends ButtonAction {

        public UnzoomAllAction() {
            super("Unzoom-All", Utility.getIcon(WaveformViewer.this, "miscIcons/unzoomall32.gif"));
            putValue(SHORT_DESCRIPTION, "Unzoom all axes to their initial state.");
            //putValue(MNEMONIC_KEY, KeyEvent.VK_Z);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            unzoomAllTraces();
        }
    }
}
