package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.apps.detection.sdBuilder.WriteSACFilesWorker;
import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class CreateSacfilesAction extends AbstractAction {

    private static CreateSacfilesAction ourInstance;
    private final JFileChooser fc = new JFileChooser();

    public static CreateSacfilesAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new CreateSacfilesAction(owner);
        }
        return ourInstance;
    }
    private final Preferences prefs;

    private CreateSacfilesAction(Object owner) {
        super("SAC", Utility.getIcon(owner, "miscIcons/saveSac.gif"));
        putValue(SHORT_DESCRIPTION, "Create SAC files from current traces.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_S);
        setEnabled(false);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        JLabel msgLabel = new JLabel("<html><b>SAC files will be written in sub-<br>directories under chosen directory;<br>"
                + "one sub-directory per detection.<br><br>The time extent of the SAC files will be<br> "
                + "set to no more than the axis limits.</b></html>");
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
        String targetDir = prefs.get("SACFILE_DIR", System.getProperty("user.dir"));
        File targetDirFile = new File(targetDir);
        if (targetDirFile.exists()) {
            fc.setCurrentDirectory(targetDirFile);
        }
        int returnVal = fc.showOpenDialog(ClusterBuilderFrame.getInstance());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            fc.setCurrentDirectory(file);
            prefs.put("SACFILE_DIR", file.getAbsolutePath());
            new WriteSACFilesWorker(file).execute();
        }

    }

}
