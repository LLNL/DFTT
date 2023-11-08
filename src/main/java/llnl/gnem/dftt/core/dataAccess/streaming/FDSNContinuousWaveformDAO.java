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
package llnl.gnem.dftt.core.dataAccess.streaming;

import edu.iris.dmc.criteria.CriteriaException;
import edu.iris.dmc.criteria.WaveformCriteria;
import edu.iris.dmc.service.NoDataFoundException;
import edu.iris.dmc.service.ServiceNotSupportedException;
import edu.iris.dmc.service.WaveformService;
import edu.iris.dmc.timeseries.model.Segment;
import edu.iris.dmc.timeseries.model.Timeseries;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import llnl.gnem.dftt.core.dataAccess.DataAccessException;
import llnl.gnem.dftt.core.dataAccess.dataObjects.continuous.ChannelSegmentCatalog;
import llnl.gnem.dftt.core.dataAccess.dataObjects.continuous.ContinuousSeismogram;
import llnl.gnem.dftt.core.dataAccess.dataObjects.continuous.StationSelectionMode;
import llnl.gnem.dftt.core.dataAccess.dataObjects.continuous.StreamAvailability;
import llnl.gnem.dftt.core.dataAccess.dataObjects.continuous.StreamSummary;
import llnl.gnem.dftt.core.dataAccess.dataObjects.continuous.StreamSupport;
import llnl.gnem.dftt.core.dataAccess.interfaces.ContinuousWaveformDAO;
import llnl.gnem.dftt.core.util.Epoch;
import llnl.gnem.dftt.core.util.StreamKey;
import llnl.gnem.dftt.core.waveform.seismogram.CssSeismogram;
import llnl.gnem.dftt.core.fdsn.DataCenter;
import llnl.gnem.dftt.core.fdsn.FedCatalog;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.TimeT;
import llnl.gnem.dftt.core.waveform.merge.IntWaveform;
import llnl.gnem.dftt.core.waveform.merge.MergeException;
import llnl.gnem.dftt.core.waveform.merge.NamedIntWaveform;
import llnl.gnem.dftt.core.waveform.merge.WaveformMerger;
import llnl.gnem.dftt.core.waveform.qc.DataDefect;
import llnl.gnem.dftt.core.waveform.qc.DataGap;

/**
 *
 * @author dodge1
 */
public class FDSNContinuousWaveformDAO implements ContinuousWaveformDAO {

    private String agency = "IRISDMC";
    private final Map<String, String> agencyAvailabilityUrlMap;
    private static final int CAPACITY = 3;
    private final ArrayBlockingQueue<WaveformService> serviceQueue;

    private FDSNContinuousWaveformDAO() {
        serviceQueue = new ArrayBlockingQueue<>(CAPACITY);
        agencyAvailabilityUrlMap = new HashMap<>();

        // retrieving these Strings should be added to FedCatalog, but for now...
        agencyAvailabilityUrlMap.put("IRISDMC", "http://service.iris.edu/fdsnws/availability/1/");
        agencyAvailabilityUrlMap.put("ORFEUS", "http://www.orfeus-eu.org/fdsnws/availability/1/");
        agencyAvailabilityUrlMap.put("RESIF", "http://ws.resif.fr/fdsnws/availability/1/");
        agencyAvailabilityUrlMap.put("NOA", "http://eida.gein.noa.gr/fdsnws/availability/1/");
        agencyAvailabilityUrlMap.put("SCEDC", "http://service.scedc.caltech.edu/fdsnws/availability/1/");
        agencyAvailabilityUrlMap.put("TEXNET", "http://rtserve.beg.utexas.edu/fdsnws/availability/1/");

        setWaveformService();

    }

    private void setWaveformService() {
        serviceQueue.clear();
        FedCatalog cat;
        try {
            cat = new FedCatalog();
            DataCenter dc = cat.getDataCenter(agency);
            String url = dc.getWaveformServiceUrl();
            for (int j = 0; j < CAPACITY; ++j) {
                serviceQueue.put(new WaveformService(url, "2.0.17", "1.1", "SeismogramDAO"));
            }
        } catch (Exception ex) {
            Logger.getLogger(FDSNContinuousWaveformDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static FDSNContinuousWaveformDAO getInstance() {
        return FDSNSeismogramSourceHolder.INSTANCE;
    }

    @Override
    public CssSeismogram getCssSeismogram(StreamKey key, Epoch epoch) throws DataAccessException {
        try {
            return getSeismogramP(key, epoch);
       } catch (ParseException | CriteriaException | MergeException | InterruptedException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public NamedIntWaveform getNamedIntWaveform(StreamKey key, Epoch epoch) throws DataAccessException {
        try {
            return getNamedIntWaveformP(key, epoch);
        } catch (ParseException | CriteriaException | MergeException | InterruptedException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Collection<StreamAvailability> getContiguousEpochs(StreamKey key, Epoch epoch) throws DataAccessException {
        try {
            return getContiguousEpochsP(key, epoch);
        } catch (IOException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private CssSeismogram getSeismogramP(StreamKey key, Epoch epoch) throws ParseException, CriteriaException, MergeException, InterruptedException {
        NamedIntWaveform intWaveform = getNamedIntWaveformP(key, epoch);
        if (intWaveform != null) {
            return new CssSeismogram(intWaveform);
        } else {
            return null;
        }
    }

    private NamedIntWaveform getNamedIntWaveformP(StreamKey key, Epoch epoch) throws ParseException, CriteriaException, MergeException, InterruptedException {
        boolean ignoreMergeError = true;
        boolean ignoreMismatchedSamples = true;

        Double calib = null;
        Double calper = null;
        // Retrieve waveforms in time order to facilitate identifying gaps between segments.
        Map<Double, NamedIntWaveform> waveformMap = getWaveformsInTimeOrder(key, epoch);
        NamedIntWaveform result = null;
        Double lastSample = null;
        Collection<DataDefect> defects = new ArrayList<>();

        //Iterate through the returned segments merging them into a single result waveform.
        for (Double startTime : waveformMap.keySet()) {
            NamedIntWaveform waveform = waveformMap.get(startTime);
            if (result == null) {
                result = waveform;
                lastSample = result.getEnd();
            } else {
                double firstSample = waveform.getStart();
                double gap = (firstSample - lastSample) * waveform.getRate();
                long nsamplesInGap = Math.round(gap);
                if (nsamplesInGap > 1) {
                    DataGap dataGap = new DataGap(new Epoch(lastSample, firstSample));
                    defects.add(dataGap);
                }

                IntWaveform wf = WaveformMerger.mergeWaveforms(waveform, result, ignoreMergeError, ignoreMismatchedSamples);
                result = new NamedIntWaveform(wf.getWfid(), waveform.getSta(), waveform.getChan(), wf.getStart(), wf.getRate(), wf.getData());
                lastSample = result.getEnd();
            }

        }
        return result != null ? new NamedIntWaveform(key,
                -1L,
                result.getData(),
                result.getStart(),
                result.getRate(),
                calib,
                calper,
                defects) : null;

    }

    private Map<Double, NamedIntWaveform> getWaveformsInTimeOrder(StreamKey key,
            Epoch segmentEpoch) throws ParseException, CriteriaException, InterruptedException {
        WaveformService waveformService = null;
        String[] channels = {key.getChan()};
        Date startDate = convertToDate(segmentEpoch.getStart());
        Date endDate = convertToDate(segmentEpoch.getEnd());
        WaveformCriteria criteria = new WaveformCriteria();
        criteria.add(key.getNet(), key.getSta(), key.getLocationCode(), channels[0], startDate, endDate);
        Map<Double, NamedIntWaveform> waveformMap = new TreeMap<>();
        try {
            waveformService = serviceQueue.take();
            List<Timeseries> timeseriesList = waveformService.fetch(criteria);
            for (Timeseries timeseries : timeseriesList) {
                for (Segment segment : timeseries.getSegments()) {
                    NamedIntWaveform waveform = new NamedIntWaveform(key, segment);
                    if (!waveform.isEmpty()) {
                        waveformMap.put(waveform.getStart(), waveform);
                    }
                }
            }
        } catch (CriteriaException | ServiceNotSupportedException | IOException ex) {
            ApplicationLogger.getInstance().log(Level.FINE, "Failed to retrieve waveform.", ex);
        } catch (NoDataFoundException ex) {
            ApplicationLogger.getInstance().log(Level.FINE, "No data found for: " + key);
        } finally {
            if (waveformService != null) {
                this.serviceQueue.put(waveformService);
            }
        }
        return waveformMap;
    }

    public void setAgency(String agency) {
        if (!this.agency.equals(agency)) {
            this.agency = agency;
            setWaveformService();
        }
    }

    @Override
    public ArrayList<ChannelSegmentCatalog> getChannelSegments(StreamKey key, StationSelectionMode stationSelectionMode) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public ContinuousSeismogram getContinuousSeismogram(StreamKey key, Epoch epoch) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public StreamSummary getStreamAvailability(StreamKey key) throws DataAccessException {

        try {
            return new StreamSummary(getStreamAvailabilityP(key));
        } catch (IOException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }


    private Collection<StreamAvailability> getStreamAvailabilityP(StreamKey key) throws MalformedURLException, IOException {
        Collection<StreamAvailability> result = new ArrayList<>();
        String baseUrl = this.agencyAvailabilityUrlMap.get(agency);
        if (baseUrl == null) {
            return result;
        }
        String urlString = String.format("%sextent?network=%s&station=%s&channel=%s&location=%s",
                baseUrl,
                key.getNet(),
                key.getSta(),
                key.getChan(),
                key.getLocationCode());
        URL url = new URL(urlString);
        // test for redirect
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setInstanceFollowRedirects(true); //you still need to handle redirect manully.
        HttpURLConnection.setFollowRedirects(true);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                if (count > 0) {
                    StreamAvailability sa = parseLine(line);
                    if (sa != null && sa.getSampleRate() > 0) {
                        result.add(sa);
                    }
                }

                ++count;
            }
        } catch (FileNotFoundException ex) {
            ApplicationLogger.getInstance().log(Level.WARNING, "No Data for: " + urlString);
        }
        return result;
    }

    private Collection<StreamAvailability> getContiguousEpochsP(StreamKey key, Epoch epoch) throws MalformedURLException, IOException {
        if (key.getSta() == null || key.getSta().isEmpty()) {
            throw new IllegalStateException("Station code not set!");
        }
        Collection<StreamAvailability> result = new ArrayList<>();
        String baseUrl = this.agencyAvailabilityUrlMap.get(agency);
        if (baseUrl == null) {
            return result;
        }
        String format = "yyyy-MM-dd'T'HH:mm:ss";
        TimeT t1 = epoch.getbeginning();
        String start = String.format("&start=%s", t1.toString(format));
        TimeT t2 = epoch.getEndtime();
        String end = String.format("&end=%s", t2.toString(format));

        boolean hasNetwork = key.getNet() != null && !key.getNet().isEmpty();
        String networkClause = hasNetwork ? "network=" + key.getNet() + "&" : "";

        boolean hasChan = key.getChan() != null && !key.getChan().isEmpty();
        String chanClause = hasChan ? "channel=" + key.getChan() + "&" : "";

        boolean hasLocid = key.getLocationCode() != null && !key.getLocationCode().isEmpty();
        String locidClause = hasLocid ? "location=" + key.getLocationCode() + "&" : "";

        String urlString = String.format("%squery?%sstation=%s&%s%s%s%s",
                baseUrl,
                networkClause,
                key.getSta(),
                chanClause,
                locidClause,
                start,
                end);
        URL url = new URL(urlString);
        // test for redirect
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setInstanceFollowRedirects(true); //you still need to handle redirect manully.
        HttpURLConnection.setFollowRedirects(true);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                if (count > 0) {
                    StreamAvailability sa = parseLine2(line);
                    if (sa != null && sa.getSampleRate() > 0) {
                        result.add(sa);
                    }
                }

                ++count;
            }
        } catch (FileNotFoundException ex) {
            ApplicationLogger.getInstance().log(Level.WARNING, "No Data for: " + urlString);
        }
        return result;
    }

    private StreamAvailability parseLine(String line) {
        String[] tokens = line.split("\\s+");
        if (tokens.length != 11) {
            return null;
        }
        String net = tokens[0];
        String sta = tokens[1];
        String locid = tokens[2];
        String chan = tokens[3];
        StreamKey key = new StreamKey(agency, net, sta, chan, locid);
        double rate = Double.parseDouble(tokens[5]);
        TimeT begin = parseTime(tokens[6]);
        TimeT end = parseTime(tokens[7]);
        int timeSpans = Integer.parseInt(tokens[9]);
        return new StreamAvailability(key, new Epoch(begin, end), rate, timeSpans);
    }

    private StreamAvailability parseLine2(String line) {
        String[] tokens = line.split("\\s+");
        if (tokens.length != 8) {
            return null;
        }
        String net = tokens[0];
        String sta = tokens[1];
        String locid = tokens[2];
        String chan = tokens[3];
        StreamKey key = new StreamKey(agency, net, sta, chan, locid);
        double rate = Double.parseDouble(tokens[5]);
        TimeT begin = parseTime(tokens[6]);
        TimeT end = parseTime(tokens[7]);
        int timeSpans = 1;
        return new StreamAvailability(key, new Epoch(begin, end), rate, timeSpans);
    }

    private TimeT parseTime(String line) {
        int year = Integer.parseInt(line.substring(0, 4));
        int month = Integer.parseInt(line.substring(5, 7));
        int day = Integer.parseInt(line.substring(8, 10));
        int hour = Integer.parseInt(line.substring(11, 13));
        int minute = Integer.parseInt(line.substring(14, 16));
        double second = Double.parseDouble(line.substring(17, 26));

        return new TimeT(year, month, day, hour, minute, second);
    }

    @Override
    public StreamSupport getStreamSupport(StreamKey key, int minJdate, int maxJdate) throws DataAccessException {
        StreamSummary summary = getStreamAvailability(key);
        return summary.getSupport(key, minJdate, maxJdate);
    }

    private static class FDSNSeismogramSourceHolder {

        private static final FDSNContinuousWaveformDAO INSTANCE = new FDSNContinuousWaveformDAO();
    }

    private Date convertToDate(double anEpochTime) throws ParseException {

        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        dfm.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        TimeT tmp = new TimeT(anEpochTime);

        return dfm.parse(tmp.toString("yyyy-MM-dd'T'HH:mm:ss.SSS"));
    }
}
