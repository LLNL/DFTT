/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder;

import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import Jampack.JampackParameters;
import java.io.IOException;
import java.util.logging.Level;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;

import llnl.gnem.core.gui.util.ExceptionDialog;
import llnl.gnem.core.gui.util.MessageDialog;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.JavaPrefObjectManager;

/**
 *
 * @author dodge1
 */
public class Builder {

    public Builder(String[] args) throws Exception {
        DetectionDAOFactory.getInstance();
        String appName = "Builder";
        configureLogging(appName);
        JampackParameters.setBaseIndex(0);

        new FilterRetrievalWorker().execute();
        JavaPrefObjectManager.getInstance().setAppName(appName);
    }

    public void run() throws Exception {

        SwingUtilities.invokeLater(() -> {
            ClusterBuilderFrame.getInstance().setVisible(true);
        });
    }

    public static void main(String[] args) {

        Builder runner;
        try {
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());
            runner = new Builder(args);

            runner.run();
        } catch (Exception e) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "Application error!", e);
        }

    }

    private static void configureLogging(String appName) throws IOException {
        ApplicationLogger.getInstance().setFileHandler(appName, false);
        ApplicationLogger.getInstance().useConsoleHandler();
        ApplicationLogger.getInstance().setLevel(Level.INFO);
        ExceptionDialog.setPostDisplayAction(MessageDialog.PostDisplayAction.LOG_MESSAGE);
    }
}
