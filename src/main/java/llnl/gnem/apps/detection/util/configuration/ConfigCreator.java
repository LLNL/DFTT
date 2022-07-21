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
package llnl.gnem.apps.detection.util.configuration;

import java.awt.Dimension;
import java.io.File;

import java.util.*;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.Util;
import llnl.gnem.apps.detection.dataAccess.dataobjects.ArrayConfiguration;
import llnl.gnem.apps.detection.dataAccess.dataobjects.ArrayElementInfo;
import llnl.gnem.apps.detection.util.ArrayInfoModel;
import llnl.gnem.core.dataAccess.DAOFactory;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.dataObjects.continuous.StreamAvailability;

import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author dodge
 */
public class ConfigCreator {

    private File configDirectory;

    private void run() throws Exception {
        DAOFactory.getInstance().setSeismogramSourceInfo(ConfigCreatorParameters.getInstance().getSeismogramSourceInfo());
        File pwd = new File(".");
        String configName = ConfigCreatorParameters.getInstance().getConfigName();
        configDirectory = new File(pwd, configName);

        Util.populateArrayInfoModel(ConfigCreatorParameters.getInstance().getMinDate());

        if (DetectionDAOFactory.getInstance().getConfigurationDAO().configExists(configName)) {
            throw new IllegalStateException(String.format("Configuration: %s already exists in database!", configName));
        }

        StreamOrganizer so = retrieveStreamSupportInformation();
        if(so.isEmpty()){
            ApplicationLogger.getInstance().log(Level.WARNING, "No stream support was found! Cannot continue.");
            System.exit(1);
        }
     
        Collection<StreamKey> selectedChannels = chooseChannels(so.getMergedSegments());


        File bulletinFile = null;
        new ConfigFileWriter(ConfigCreatorParameters.getInstance().getRefSta(),
                ConfigCreatorParameters.getInstance().getConfigName(),
                configDirectory,
                ConfigCreatorParameters.getInstance().getMinDate(),
                ConfigCreatorParameters.getInstance().getMaxDate(),
                selectedChannels,
                ConfigCreatorParameters.getInstance().getMinTemplateLength(),
                ConfigCreatorParameters.getInstance().getMaxTemplateLength(),
                ConfigCreatorParameters.getInstance().getMinFrequency(),
                ConfigCreatorParameters.getInstance().getMaxFrequency(),
                ConfigCreatorParameters.getInstance().getSsThresh(),
                ConfigCreatorParameters.getInstance().getStaLtaThresh(),
                ConfigCreatorParameters.getInstance().getNumThreads(),
                ConfigCreatorParameters.getInstance().getBootDetectorType(),
                ConfigCreatorParameters.getInstance().getBeamAzimuth(),
                ConfigCreatorParameters.getInstance().getBeamVelocity(),
                bulletinFile,
                ConfigCreatorParameters.getInstance().getSnrThreshold(),
                ConfigCreatorParameters.getInstance().getMinEventDuration(),
                ConfigCreatorParameters.getInstance().getBlockSizeSeconds(),
                ConfigCreatorParameters.getInstance().getDecimationRate(),
                ConfigCreatorParameters.getInstance().isSpawnCorrelationDetectors()).create();
    }

    private StreamOrganizer retrieveStreamSupportInformation() throws IllegalStateException, DataAccessException {
        StreamOrganizer so = new StreamOrganizer();
        Epoch requestEpoch = ConfigCreatorParameters.getInstance().getRequestEpoch();
        if (ConfigCreatorParameters.getInstance().isArrayBased()) {
            if (!ArrayInfoModel.getInstance().hasArray(ConfigCreatorParameters.getInstance().getRefSta())) {
                throw new IllegalStateException(String.format("REFSTA: %s not found in metadata!", ConfigCreatorParameters.getInstance().getRefSta()));
            }
            ArrayConfiguration geometry = ArrayInfoModel.getInstance().getGeometry(ConfigCreatorParameters.getInstance().getRefSta());
            
            
            Collection<ArrayElementInfo> elements = geometry.getElements(requestEpoch.getStart());
            for (ArrayElementInfo aei : elements) {
                StreamKey key = new StreamKey(aei.getStationCode(),null);
                Collection<StreamAvailability> csa = DAOFactory.getInstance().getContinuousWaveformDAO().getContiguousEpochs(key, requestEpoch);
                so.add(csa);
            }
        }
        else{
            for(String sta : ConfigCreatorParameters.getInstance().getStations()){
                StreamKey key = new StreamKey(sta,null);
                Collection<StreamAvailability> csa = DAOFactory.getInstance().getContinuousWaveformDAO().getContiguousEpochs(key, requestEpoch);
                so.add(csa);
            }
        }
        return so;
    }

    public ConfigCreator(String[] args) throws Exception {
        ConfigCreatorParameters.getInstance().getCommandLineInfo(args);
        ConfigCreatorParameters.getInstance().initializeConnection();

    }

    public static void main(String[] args) {

        ConfigCreator runner;
        try {
            ApplicationLogger.getInstance().setFileHandler("ConfigCreator", false);
            ApplicationLogger.getInstance().useConsoleHandler();
            runner = new ConfigCreator(args);

            runner.run();
        } catch (Exception e) {
            ApplicationLogger.getInstance().log(Level.SEVERE, e.getMessage(), e);
        }

    }

    private Collection<StreamKey> chooseChannels(Collection<StreamAvailability> streams) throws DataAccessException {
 
        ChannelDisplayPanel panel = new ChannelDisplayPanel(streams);
       
        JScrollPane scroll = new JScrollPane(panel);
         scroll.setPreferredSize(new Dimension(700,400));
        String[] options1 = {"Accept", "Cancel"};
        int answer = JOptionPane.showOptionDialog(null, scroll, "Choose channels to use with configuration", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, // do not use a
                // custom Icon
                options1, // the titles of buttons
                options1[0]);
        if (answer == JOptionPane.YES_OPTION) {
            return panel.getSelectedChannels();
        } else {
            System.exit(0);
        }
         return null;
    }

}
