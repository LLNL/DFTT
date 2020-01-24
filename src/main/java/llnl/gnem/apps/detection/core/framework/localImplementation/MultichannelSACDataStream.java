package llnl.gnem.apps.detection.core.framework.localImplementation;

import java.io.IOException;
import java.util.ArrayList;

import com.oregondsp.io.SACInputStream;
import com.oregondsp.util.TimeStamp;
import java.util.Collection;

import llnl.gnem.apps.detection.core.dataObjects.TransformedStreamSegment;

import llnl.gnem.apps.detection.core.framework.DownSampler;
import llnl.gnem.apps.detection.core.signalProcessing.RFFTdp;
import llnl.gnem.core.util.PairT;

import llnl.gnem.core.util.StreamKey;
import llnl.gnem.apps.detection.core.dataObjects.StreamSegment;
import llnl.gnem.apps.detection.core.dataObjects.WaveformSegment;



/**
 * Copyright (c) 2009  Lawrence Livermore National Laboratory
 * All rights reserved
 * Author:  Dave Harris
 * Created: Mar 17, 2009
 * Time: 6:20:01 PM
 * Last Modified: Mar 17, 2009
 */


public class MultichannelSACDataStream implements ContinuousDataStream {

    private static final double MAX_ALLOWABLE_DELTA_DIFFERENCE = 0.000001;

    private final SACInputStream[] readers;
    private final StreamKey[]     channels;
    private final int              nchannels;
    
    private final DownSampler      downSampler;
    
    private final TimeStamp        currentTime;
    private int                    samplesRemaining;
    private double                 delta;
    private int                    decrate;
    private int                    maxTemplateLength;
    
    private int                    nfft;
    private final RFFTdp           fft;


    public MultichannelSACDataStream( StreamPrescription streamPrescription, ProcessingPrescription prescription ) throws IOException {
        
        String[] SACFiles = streamPrescription.getFileList();

        nchannels = SACFiles.length;

        readers = new SACInputStream[ nchannels ];
        for (int i = 0; i < nchannels; i++) {
            readers[i] = new SACInputStream(SACFiles[i]);
        }

        // find latest time

        double T0 = readers[0].header.getStartTime().epochAsDouble();
        for (int i = 1; i < nchannels; i++)
          T0 = Math.max( T0, readers[i].header.getStartTime().epochAsDouble() );

        currentTime = new TimeStamp( T0 );

        // skip samples as appropriate

        for (int i = 0; i < nchannels; i++) {
            double T = readers[i].header.getStartTime().epochAsDouble();
            double difference = T0 - T;
            int samplesToSkip = (int) Math.round( difference / readers[0].header.delta );
            readers[i].skipSamples( samplesToSkip );
        }

        // find number of samples available

        samplesRemaining = Integer.MAX_VALUE;
        for (int i = 0; i < nchannels; i++)
            samplesRemaining = Math.min( samplesRemaining, readers[i].numPtsAvailable() );


        setDelta();
        prescription.setSamplingRate( 1.0/delta );
        
        decrate = prescription.getDecimationRate();

        channels = new StreamKey[ nchannels ];
        for ( int ich = 0;  ich < nchannels;  ich++ ) {
          channels[ich] = new StreamKey( readers[ich].header.kstnm.trim(), readers[ich].header.kcmpnm.trim() );
        }
        
        downSampler = new DownSampler( prescription, nchannels, 1.0/delta );
        
        maxTemplateLength = (int) Math.round( prescription.getMaxTemplateLength() / ( delta * decrate ) );
        
        int n = prescription.getDataBlockSize()/decrate + maxTemplateLength - 1;
        int log2N = 0;
        nfft  = 1;
        while ( nfft < n ) {
            nfft *= 2;
            log2N++;
        }
        fft = new RFFTdp( log2N );
        
    }

    
    
    private void setDelta() {
        delta = -1;
        for( int j = 0; j < nchannels; ++j ){
            double thisDelta = readers[j].header.delta;
            if( delta > 0  &&  Math.abs(delta - thisDelta) > MAX_ALLOWABLE_DELTA_DIFFERENCE )
                throw new IllegalStateException( "Not all SAC files in stream have compatible sample rates!");
            delta = thisDelta;
        }
    }


    
    @Override
    public TransformedStreamSegment getSegment( int segmentLength ) throws IOException {
      
      if ( numSamplesAvailable() == 0 ) return null;

      Collection< WaveformSegment > segs = new ArrayList<>();

      double T = currentTime.epochAsDouble();
      
      double samplingRate = getSamplingRate();
      
      int nread = 999999999;
      for ( int ich = 0;  ich < nchannels;  ich++ ) {
        float[] data = new float[ segmentLength ];
        nread = Math.min( nread, readers[ich].readData( data ) );
        segs.add( new WaveformSegment( channels[ich].getSta(), channels[ich].getChan(), T, samplingRate, data, null ) );
      }
      
      StreamSegment tmp = downSampler.process( new StreamSegment( segs ) );
      segs = tmp.getWaveforms();
      
      ArrayList< PairT< WaveformSegment, double[] > > tmp1 = new ArrayList<>();
      
      for ( WaveformSegment ws : segs ) {
          float[] x = ws.getData();
          double[] y = new double[ nfft ];
          for ( int i = 0;  i < x.length;  i++ ) y[i] = x[i];
          fft.dft( y );
          tmp1.add( new PairT( ws, y ) );
      }
      
      samplesRemaining -= nread;
      currentTime.plus( segmentLength * delta );

      return new TransformedStreamSegment( tmp1 );
    }
    
    
    
    public int          getFFTSize() {
        return nfft;
    }

    
    @Override
    public int          getNumChannels() { return nchannels; }
    
    
    @Override
    public StreamKey[] getStaChanArray() {
      return channels;
    }

    
    @Override
    public double       getSamplingRate() { return Math.round(1.0 / delta); }


    @Override
    public int          numSamplesAvailable() { return samplesRemaining; }


    @Override
    public TimeStamp    getTimeStamp()  { return new TimeStamp( currentTime ); }
    

    @Override
    public void         close() throws IOException {
        for( int j = 0; j < nchannels; ++j ){
            readers[j].close();
        }
    }

}
