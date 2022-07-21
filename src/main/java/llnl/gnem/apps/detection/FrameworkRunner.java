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
package llnl.gnem.apps.detection;

import java.util.logging.Level;

import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.source.SourceData;
import llnl.gnem.apps.detection.tasks.BlockRetrievalFutureTask;
import llnl.gnem.apps.detection.tasks.ComputationService;
import llnl.gnem.apps.detection.tasks.RetrieveAllBlocksTask;
import llnl.gnem.apps.detection.util.ArrayInfoModel;
import llnl.gnem.apps.detection.util.RunInfo;
import llnl.gnem.apps.detection.util.SourceDataHolder;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import llnl.gnem.core.dataAccess.SeismogramSourceInfo;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.BuildInfo;
import llnl.gnem.core.util.FileUtil.DriveMapper;

public class FrameworkRunner {

    private SourceData source;
    private final DetectionFramework framework;

    public FrameworkRunner(String[] args) throws Exception {

        DriveMapper.setupWindowsNFSDriveMap();
        String buf = "usage: DetectionFramework login/password  \n{Required Arguments:}\n\t";

        CommandLineInfo.getInstance().getCommandLineInfo(args, buf);
        String commandLineArgs = getCommandLineString(args);

        String configName = ProcessingPrescription.getInstance().getConfigName();
        if ((configName == null || configName.isEmpty())) {
            System.err.println("No configuration was specified in the parameter file!");
            System.exit(1);
        }
        source = new SourceData(configName, CommandLineInfo.getInstance().isScaleByCalib());
        SourceDataHolder.getInstance().setSource(source);

        ArrayInfoModel.getInstance().setCurrentDate(ProcessingPrescription.getInstance().getMinJdateToProcess());

        if (ProcessingPrescription.getInstance().isCreateConfiguration() && CommandLineInfo.getInstance().getRunidToResume() == null) {
            SeismogramSourceInfo sourceInfo = ProcessingPrescription.getInstance().getSeismogramSourceInfo();
            DetectionDAOFactory.getInstance().getConfigurationDAO().createOrReplaceConfigurationUsingInputFiles(configName, commandLineArgs, sourceInfo);
        }
        RunInfo.getInstance().initialize(CommandLineInfo.getInstance().getRunidToResume(), commandLineArgs);

        Integer startingJdate = null;
        if (CommandLineInfo.getInstance().getRunidToResume() != null) {
            startingJdate = DetectionDAOFactory.getInstance().getFrameworkRunDAO().getJdateOfLastTrigger(CommandLineInfo.getInstance().getRunidToResume());
        }

        ConfigurationInfo.getInstance().initialize(configName, startingJdate);

        ProcessingPrescription.getInstance().validateStreamParams(ConfigurationInfo.getInstance().getSupport().getRate());
        ApplicationLogger.getInstance().log(Level.INFO, "Processing data from source: " + ProcessingPrescription.getInstance().getSeismogramSourceInfo());

        RetrieveAllBlocksTask task = new RetrieveAllBlocksTask(source);
        BlockRetrievalFutureTask mft = new BlockRetrievalFutureTask(task);
        ComputationService.getInstance().getBlockRetrievalExecutorService().execute(mft);

        framework = new DetectionFramework(source, CommandLineInfo.getInstance().getPrimaryBufferSize());
    }

    public void run() throws Exception {
        framework.initialize();
        framework.run();
    }

    public void close() throws Exception {

        if (framework != null) {
            framework.close();
        }
    }

    public static void main(String[] args) {

        FrameworkRunner runner = null;
        try {
            BuildInfo buildInfo = new BuildInfo(FrameworkRunner.class);

            ApplicationLogger.getInstance().setFileHandler("DetectionFramework", false);
            ApplicationLogger.getInstance().useConsoleHandler();
            ApplicationLogger.getInstance().setLevel(CommandLineInfo.getLogLevel());
            if (buildInfo.isFromJar()) {
                ApplicationLogger.getInstance().log(Level.INFO, buildInfo.getBuildInfoString());
            }
            runner = new FrameworkRunner(args);
            runner.summarizeSource();
            runner.run();
        } catch (Exception e) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "General Failure", e);
        } catch (OutOfMemoryError ome) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "Out of memory!");
        } finally {
            if (runner != null) {
                try {
                    runner.close();
                } catch (Exception e) {
                    ApplicationLogger.getInstance().log(Level.WARNING, "General Failure", e);
                }
            }
        }

    }

    private void summarizeSource() {
        ConfigurationInfo.getInstance().printSummary();
    }

    private String getCommandLineString(String[] args) {
        StringBuilder buf = new StringBuilder();
        for (String arg : args) {
            buf.append(arg);
            buf.append(" ");
        }
        return buf.toString().trim();
    }

}
