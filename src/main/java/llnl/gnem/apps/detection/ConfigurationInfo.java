/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import llnl.gnem.apps.detection.util.initialization.StreamsConfig;
import llnl.gnem.dftt.core.dataAccess.DAOFactory;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;
import llnl.gnem.dftt.core.dataAccess.SeismogramSourceInfo;
import llnl.gnem.dftt.core.dataAccess.dataObjects.continuous.StreamSupport;

import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.Epoch;
import llnl.gnem.dftt.core.util.StreamKey;
import llnl.gnem.dftt.core.util.TimeT;

/**
 *
 * @author dodge1
 */
public class ConfigurationInfo {

    private final Collection<Integer> streamids;
    private final Collection<StreamKey> keys;

    private String configName;

    private double commonRate;
    private Integer currentConfigid = null;

    private StreamSupport streamSupport = null;

    private ConfigurationInfo() {
        streamids = new ArrayList<>();
        keys = new ArrayList<>();
    }

    public static ConfigurationInfo getInstance() {
        return ConfigurationManagerHolder.INSTANCE;
    }

    private static class ConfigurationManagerHolder {

        private static final ConfigurationInfo INSTANCE = new ConfigurationInfo();
    }

    public StreamSupport getSupport() {
        return streamSupport;
    }

    public void initialize(String configName, Integer startingJdate) throws DataAccessException {
        this.configName = configName;
        setStreamKeys(configName);
        int minJdate = startingJdate == null ? ProcessingPrescription.getInstance().getMinJdateToProcess() : startingJdate;
        int maxJdate = ProcessingPrescription.getInstance().getMaxJdateToProcess();

        summarize(minJdate, maxJdate, startingJdate);

    }

    public Collection<StreamKey> getStreamKeys() {
        return new ArrayList<>(keys);
    }

    public void setStreamKeys(String configName) throws DataAccessException {
        keys.clear();
        keys.addAll(DetectionDAOFactory.getInstance().getStreamDAO().getStreamKeys(configName));
    }

    public void setCurrentConfigurationData(int configid) throws DataAccessException {
        if (currentConfigid != null && currentConfigid == configid) {
            return;
        }

        SeismogramSourceInfo sourceInfo = DetectionDAOFactory.getInstance().getConfigurationDAO().getConfigurationSeismogramSourceInfo(configid);
        if (sourceInfo != null) {
            DAOFactory.getInstance().setSeismogramSourceInfo(sourceInfo);
        }
        currentConfigid = configid;
        keys.clear();
        keys.addAll(DetectionDAOFactory.getInstance().getStreamDAO().getStreamKeys(configid));

        Epoch epoch = DetectionDAOFactory.getInstance().getStreamDAO().getAllTriggersEpoch(configid);
        Integer startingJdate = null;
        if (epoch == null || epoch.isEmpty()) {
            throw new IllegalStateException("Could not determine a non-null trigger range for configid = " + configid);
        }
        populateTimeRange(epoch.getOnJdate(), epoch.getOffJdate(), startingJdate);
    }

    public void summarize(int minJdate, int maxJdate, Integer startingJdate) throws DataAccessException {
        if (!DetectionDAOFactory.getInstance().getConfigurationDAO().configExists(configName)) {
            DetectionDAOFactory.getInstance().getConfigurationDAO().createConfiguration(configName);
        }
        streamids.addAll(DetectionDAOFactory.getInstance().getConfigurationDAO().getStreamids(configName));

        summarizeStreams();
        populateTimeRange(minJdate, maxJdate, startingJdate);

    }

    private void summarizeStreams() throws DataAccessException {

        if (streamids.isEmpty()) {
            throw new IllegalStateException(String.format("Configuration %s (specified in parameter file) has no streams!", configName));
        } else {
            for (int streamid : streamids) {
                listChannels(streamid);
            }
        }

    }

    private void populateTimeRange(int minJdate, int maxJdate, Integer startingJdate) throws DataAccessException {

        //If startingJdate is not null, then this is a continuation of prior run.
        if (startingJdate != null) {
            minJdate = startingJdate;
        }
        Double minTime = TimeT.jdateToEpoch(minJdate);
        Double maxTime = TimeT.jdateToEpoch(maxJdate);
        Long arate = null;
        for (StreamKey key : keys) {
            StreamSupport support = DAOFactory.getInstance().getContinuousWaveformDAO().getStreamSupport(key, minJdate, maxJdate);
            if (minTime < support.getTimeSpan().getStart()) {
                minTime = support.getTimeSpan().getStart();
            }
            if (maxTime > support.getTimeSpan().getEnd()) {
                maxTime = support.getTimeSpan().getEnd();
            }
            long theRate = Math.round(support.getRate());
            if (arate == null) {
                arate = theRate;
            } else if (arate != theRate) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Not all channels have the same sampleRate!");
            }
        }
        if (minJdate == maxJdate) {
            maxTime = minTime + TimeT.SECPERDAY;
        }

        commonRate = arate;

        double passbandUpperCorner = StreamsConfig.getInstance().getMaxPassbandUpperCorner();
        if (passbandUpperCorner >= commonRate / 2) {
            throw new IllegalStateException("Wideband passband upper corner is >= Nyquist frequency of data!");
        }
        streamSupport = new StreamSupport(new Epoch(minTime, maxTime), commonRate);
    }

    public void printSummary() {
        TimeT start = streamSupport.getTimeSpan().getTime();
        TimeT end = streamSupport.getTimeSpan().getEndtime();
        ApplicationLogger.getInstance().log(Level.FINE, String.format("Data extend from %s to %s", start, end));

        StringBuilder sb = new StringBuilder("\n");
        for (StreamKey key : keys) {
            sb.append(key.toString()).append("\n");
        }
        ApplicationLogger.getInstance().log(Level.FINER, String.format("Available sta-chan are: %s", sb.toString()));
    }

    private void listChannels(int streamid) throws DataAccessException {

        System.out.println(String.format("Station-channel combinations in stream %d are:", streamid));
        int count = 0;
        for (StreamKey key : keys) {
            System.out.println(String.format("\t%s", key.toString()));
            ++count;
        }
        System.out.println("======================================================\n");
        if (count < 1) {
            throw new IllegalStateException(String.format("Stream %d has no channels!", streamid));
        }

    }

}
