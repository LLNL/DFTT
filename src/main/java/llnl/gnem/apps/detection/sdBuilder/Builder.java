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
