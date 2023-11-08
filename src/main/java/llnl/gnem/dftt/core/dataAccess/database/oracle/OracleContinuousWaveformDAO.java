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
package llnl.gnem.dftt.core.dataAccess.database.oracle;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import llnl.gnem.dftt.core.dataAccess.DAOFactory;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;
import llnl.gnem.dftt.core.dataAccess.SeismogramSourceInfo.SourceType;
import llnl.gnem.dftt.core.dataAccess.dataObjects.continuous.ChannelSegmentCatalog;
import llnl.gnem.dftt.core.dataAccess.dataObjects.continuous.ContiguousSegmentCollection;
import llnl.gnem.dftt.core.dataAccess.dataObjects.continuous.ContinuousSeismogram;
import llnl.gnem.dftt.core.dataAccess.dataObjects.continuous.StationSelectionMode;
import llnl.gnem.dftt.core.dataAccess.dataObjects.continuous.StreamAvailability;
import llnl.gnem.dftt.core.dataAccess.dataObjects.continuous.StreamSummary;
import llnl.gnem.dftt.core.dataAccess.dataObjects.continuous.StreamSupport;
import llnl.gnem.dftt.core.dataAccess.database.oracle.waveformUtil.CssUtil;
import llnl.gnem.dftt.core.dataAccess.database.oracle.waveformUtil.DatabaseUtil;
import llnl.gnem.dftt.core.dataAccess.interfaces.ContinuousWaveformDAO;
import llnl.gnem.dftt.core.util.Epoch;
import llnl.gnem.dftt.core.util.StreamKey;
import llnl.gnem.dftt.core.waveform.merge.IntWaveform;
import llnl.gnem.dftt.core.waveform.merge.MergeException;
import llnl.gnem.dftt.core.waveform.merge.NamedIntWaveform;
import llnl.gnem.dftt.core.waveform.merge.WaveformMerger;
import llnl.gnem.dftt.core.waveform.qc.DataDefect;
import llnl.gnem.dftt.core.waveform.qc.DataGap;
import llnl.gnem.dftt.core.waveform.seismogram.CssSeismogram;

/**
 *
 * @author dodge1
 */
public class OracleContinuousWaveformDAO implements ContinuousWaveformDAO {

    @Override
    public ArrayList<ChannelSegmentCatalog> getChannelSegments(StreamKey key, StationSelectionMode stationSelectionMode) throws DataAccessException {
        try {
            return getChannelSegmentsP(key, stationSelectionMode);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }


    @Override
    public ContinuousSeismogram getContinuousSeismogram(StreamKey name, Epoch epoch) throws DataAccessException {
        try {
            return getContinuousSeismogramP(name, epoch);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }



    @Override
    public CssSeismogram getCssSeismogram(StreamKey key, Epoch epoch) throws DataAccessException {
        try {
            return getCssSeismogramP(key, epoch);
        } catch (MergeException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public NamedIntWaveform getNamedIntWaveform(StreamKey key, Epoch epoch) throws DataAccessException {
        try {
            return getNamedIntWaveformP(key, epoch);
        } catch (MergeException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public StreamSummary getStreamAvailability(StreamKey key) throws DataAccessException {
        return getStreamAvailabilityP(key);
    }


    @Override
    public StreamSupport getStreamSupport(StreamKey key, int minJdate, int maxJdate) throws DataAccessException {
        try {
            return getStreamSupportP(key, minJdate, maxJdate);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Collection<StreamAvailability> getContiguousEpochs(StreamKey key, Epoch epoch) throws DataAccessException {
        ArrayList<ChannelSegmentCatalog> tmp = new ArrayList<>();
        SourceType type = DAOFactory.getInstance().getSeismogramSourceInfo().getSourceType();
        try {
            if (null == type) {
                throw new IllegalStateException("Null source type for datrabase DAO!");
            } else {
                switch (type) {
                    case CssDatabase:
                        tmp.addAll(CssUtil.getChannelSegments(key, epoch));
                        return convertToStreamAvailability(tmp);
                case Type2Database:
                    tmp.addAll(DatabaseUtil.getChannelSegments(key, epoch));
                        return convertToStreamAvailability(tmp);
                    default:
                        throw new IllegalStateException("Unexpected source type for datrabase DAO: " + type);
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private ArrayList<ChannelSegmentCatalog> getChannelSegmentsP(StreamKey channel, StationSelectionMode mode) throws Exception {
        SourceType type = DAOFactory.getInstance().getSeismogramSourceInfo().getSourceType();
        try {
            if (null == type) {
                throw new IllegalStateException("Null source type for datrabase DAO!");
            } else {
                switch (type) {
                    case CssDatabase:
                        return CssUtil.getChannelSegments(channel, mode);
                case Type2Database:
                    return DatabaseUtil.getChannelSegments(channel, mode);
                    default:
                        throw new IllegalStateException("Unexpected source type for datrabase DAO: " + type);
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }


    private ContinuousSeismogram getContinuousSeismogramP(StreamKey name, Epoch epoch) throws Exception {
        SourceType type = DAOFactory.getInstance().getSeismogramSourceInfo().getSourceType();

        if (null == type) {
            throw new IllegalStateException("Null source type for datrabase DAO!");
        } else {
            switch (type) {
                case CssDatabase:
                    return CssUtil.getContinuousSeismogramFromCssExtTable(name, epoch);
            case Type2Database:
                return DatabaseUtil.getContinuousSeismogramFromDB(name, epoch);
                default:
                    throw new IllegalStateException("Unexpected source type for datrabase DAO: " + type);
            }
        }

    }


    private CssSeismogram getCssSeismogramP(StreamKey key, Epoch epoch) throws DataAccessException, MergeException {
        NamedIntWaveform tmp = getNamedIntWaveformP(key, epoch);
        return tmp != null ? new CssSeismogram(tmp) : null;
    }

    private NamedIntWaveform getNamedIntWaveformP(StreamKey key, Epoch epoch) throws DataAccessException, MergeException {
        // Method works for both Css and dftt continuous sesimogram tables.
        boolean ignoreMergeError = true;
        boolean ignoreMismatchedSamples = true;
        NamedIntWaveform result = null;
        Collection<DataDefect> defects = new ArrayList<>();
        ContinuousSeismogram seis = getContinuousSeismogram(key, epoch);
        if (seis != null) {
            Map<Double, NamedIntWaveform> waveformMap = seis.getNamedIntWaveformMap();
            Double lastSample = null;

            Double calib = null;
            Double calper = null;
            //Iterate through the returned segments merging them into a single result waveform.
            for (Double startTime : waveformMap.keySet()) {
                NamedIntWaveform waveform = waveformMap.get(startTime);
                calib = waveform.getCalib();
                calper = waveform.getCalper();
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
                    result = new NamedIntWaveform(key, wf.getWfid(), wf.getData(), wf.getStart(), wf.getRate(), calib, calper);

                    lastSample = result.getEnd();
                }

            }
        }
        if (result != null) {
            return new NamedIntWaveform(key, -1L, result.getData(), result.getStart(), result.getRate(), result.getCalib(), result.getCalper(), defects);
        } else {
            return null;
        }
    }

    private StreamSummary getStreamAvailabilityP(StreamKey key) throws DataAccessException {
        ArrayList<ChannelSegmentCatalog> segments = DAOFactory.getInstance().getContinuousWaveformDAO().getChannelSegments(key, StationSelectionMode.SINGLE_STATION);
        Collection<StreamAvailability> tmp = convertFrom(segments);
        return new StreamSummary(tmp);
    }

    private StreamSupport getStreamSupportP(StreamKey key, int minJdate, int maxJdate) throws SQLException {
        SourceType type = DAOFactory.getInstance().getSeismogramSourceInfo().getSourceType();
        if (null == type) {
            throw new IllegalStateException("Null source type for datrabase DAO!");
        } else {
            switch (type) {
                case CssDatabase:
                    return CssUtil.getStreamSupportFromCssExtTable(key, minJdate, maxJdate);
            case Type2Database:
                return DatabaseUtil.getStreamSupport(key, minJdate, maxJdate);
                default:
                    throw new IllegalStateException("Unexpected source type for datrabase DAO: " + type);
            }
        }

    }

    private Collection<StreamAvailability> convertFrom(ArrayList<ChannelSegmentCatalog> segments) {
        Collection<StreamAvailability> result = new ArrayList<>();
        for (ChannelSegmentCatalog csc : segments) {
            StreamKey key = csc.getName();
            double sampleRate = csc.getSampleRate();
            Epoch range = csc.getEpoch();
            int timeSpans = csc.getTotalSegmentCount();
            result.add(new StreamAvailability(key, range, sampleRate, timeSpans));
        }
        return result;
    }

    private Collection<StreamAvailability> convertToStreamAvailability(ArrayList<ChannelSegmentCatalog> tmp) {
        Collection<StreamAvailability> result = new ArrayList<>();
        for (ChannelSegmentCatalog csc : tmp) {
            for (ContiguousSegmentCollection cs : csc.getContiguousSegments()) {
                result.add(new StreamAvailability(csc.getName(), cs.getEpoch(), csc.getSampleRate(), cs.getSize()));
            }

        }

        return result;
    }

}
