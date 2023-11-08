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
package llnl.gnem.dftt.core.dataAccess.database.javadb;

import java.util.ArrayList;
import java.util.Collection;
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
import llnl.gnem.dftt.core.waveform.merge.NamedIntWaveform;

import llnl.gnem.dftt.core.waveform.seismogram.CssSeismogram;

/**
 *
 * @author dodge1
 */
public class DerbyContinuousWaveformDAO implements ContinuousWaveformDAO{


    @Override
    public ArrayList<ChannelSegmentCatalog> getChannelSegments(StreamKey key, StationSelectionMode stationSelectionMode) throws DataAccessException{
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public ContinuousSeismogram getContinuousSeismogram(StreamKey name, Epoch epoch) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

 

    @Override
    public CssSeismogram getCssSeismogram(StreamKey key, Epoch epoch) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public StreamSummary getStreamAvailability(StreamKey key) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public StreamSupport getStreamSupport(StreamKey key, int minJate, int maxJdate) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NamedIntWaveform getNamedIntWaveform(StreamKey key, Epoch epoch) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<StreamAvailability> getContiguousEpochs(StreamKey key, Epoch epoch) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

  
    
}
