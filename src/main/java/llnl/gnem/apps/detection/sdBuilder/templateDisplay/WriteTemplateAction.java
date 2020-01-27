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
package llnl.gnem.apps.detection.sdBuilder.templateDisplay;

import llnl.gnem.apps.detection.sdBuilder.actions.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import llnl.gnem.apps.detection.core.framework.detectors.EmpiricalTemplate;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceTemplate;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.apps.detection.sdBuilder.WriteSACFilesWorker;
import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class WriteTemplateAction extends AbstractAction {

    private static WriteTemplateAction ourInstance;
    private static final long serialVersionUID = -5567288303125228667L;
    private final JFileChooser fc = new JFileChooser();

    public static WriteTemplateAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new WriteTemplateAction(owner);
        }
        return ourInstance;
    }
    private final Preferences prefs;
    private int detectorid;

    private WriteTemplateAction(Object owner) {
        super("SAC OUTPUT", Utility.getIcon(owner, "miscIcons/saveSac.gif"));
        putValue(SHORT_DESCRIPTION, "Create SAC files from current template.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_S);
        setEnabled(true);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        JLabel msgLabel = new JLabel("<html><b>SAC files will be written under chosen directory.</b></html>");
        JPanel msgPanel = new JPanel();
        msgPanel.setLayout(new BorderLayout());
        msgPanel.setBorder(BorderFactory.createTitledBorder("Please choose a directory."));
        msgPanel.add(msgLabel, BorderLayout.CENTER);
        msgPanel.setBackground(new Color(240, 240, 250));
        fc.setAccessory(msgPanel);
        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        prefs = Preferences.userNodeForPackage(this.getClass());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String targetDir = prefs.get("TEMPLATE_DIR", System.getProperty("user.dir"));
        File targetDirFile = new File(targetDir);
        if (targetDirFile.exists()) {
            fc.setCurrentDirectory(targetDirFile);
        }
        int returnVal = fc.showOpenDialog(ClusterBuilderFrame.getInstance());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            fc.setCurrentDirectory(file);
            prefs.put("TEMPLATE_DIR", file.getAbsolutePath());
            EmpiricalTemplate template = TemplateModel.getInstance().getCurrentTemplate();
            if(template instanceof SubspaceTemplate){
                SubspaceTemplate st = (SubspaceTemplate)template;
                try {
                    st.writeTemplateToSACFiles(file.getAbsolutePath(), detectorid);
                } catch (IOException ex) {
                    Logger.getLogger(WriteTemplateAction.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    void setDetectorid(int detectorid) {
        this.detectorid = detectorid;
    }

}
