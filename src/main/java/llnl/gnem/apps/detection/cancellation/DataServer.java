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
package llnl.gnem.apps.detection.cancellation;


import com.oregondsp.io.SACFileWriter;
import com.oregondsp.io.SACHeader;
import com.oregondsp.io.SACInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

import com.oregondsp.signalProcessing.filter.iir.Butterworth;
import com.oregondsp.signalProcessing.filter.iir.PassbandType;
import com.oregondsp.util.DirectoryListing;
import com.oregondsp.util.TimeStamp;
import llnl.gnem.apps.detection.cancellation.io.ChannelID;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.apps.detection.core.dataObjects.StreamSegment;
import llnl.gnem.apps.detection.core.dataObjects.WaveformSegment;


/**
 *
 * @author dbh
 */
public class DataServer implements Callable<Void> {
  
    private SegmentedCancellor           cancellor;
    
    private HashMap< String, Integer >   mapping;
    
    private int         nch;
    private int         N;
    private double      delta;
    private TimeT       T;
    private float[][]   data;
    private ChannelID[] chanids;
    private String[]    files;
    private int         ptr;
    private int         segmentLength;


    public DataServer( CancellationParameters parameters, ChannelID[] channelIDs ) {
      
        nch     = channelIDs.length;
        chanids = channelIDs;
      
        mapping = new HashMap< String, Integer >();
        for ( int ich = 0;  ich < nch;  ich++ ) mapping.put( chanids[ich].toString(), ich );
        
        String streamDirectory = parameters.getStreamDirectory();
        
        // read stream data
                  
        DirectoryListing D = new DirectoryListing( streamDirectory, parameters.getStreamFilePattern() );
        D.print( System.out );
        
        int nfiles = D.nFiles();
        
        files = new String[ D.nFiles() ];
        for ( int i = 0;  i < D.nFiles();  i++ ) files[i] = D.file( i );
                  
        if( nfiles != nch ) throw new IllegalStateException( "Number of files " + nfiles + " does not equal number of requested channels " + nch );
                  
        try {
          SACInputStream stream = new SACInputStream( streamDirectory + File.separator + files[0] );
          N     = stream.header.npts;
          delta = stream.header.delta;
          T     = new TimeT(stream.header.getStartTime().epochAsDouble());
          stream.close();
        } catch ( IOException e ) {
          e.printStackTrace();
        }
                  
        Butterworth F = new Butterworth( 5, PassbandType.BANDPASS, parameters.getFlow(), parameters.getFhigh(), delta );
                  
        data = new float[nch][N];
                  
        int ich = -1;
                  
        for ( int ifile = 0;  ifile < nch;  ifile++ ) {
          
          try {
            
            SACInputStream reader = new SACInputStream( streamDirectory + File.separator + D.file( ifile ) );
            
            String stachan = reader.header.kstnm.trim() + "." + reader.header.kcmpnm.trim();
            if ( mapping.get( stachan ) != null ) {
              ich = mapping.get( stachan );
            }
            else {
              reader.close();
              throw new IllegalStateException( "Channel id not found:  " + stachan );
            }
            
            reader.readData( data[ich] );
            F.initialize();
            F.filter( data[ich] );
            reader.close();
            
          } catch ( IOException e ) {
            e.printStackTrace();
          }
          
        }
        
        ptr = 0;
        segmentLength = (int) Math.round( parameters.getBlockLength() / delta );
              
        System.out.println( "  ... done\n" );
    }
    
    
    
    public StreamSegment getStreamSegment() {
      
      int n = Math.min( N-ptr, segmentLength );
        
      double start = T.getEpochTime() + ptr*delta;
      
      ArrayList< WaveformSegment > tmps = new ArrayList<  >();
      
      for ( int ich = 0;  ich < nch;  ich++ ) {
        float[] tmp = new float[ n ];
        System.arraycopy(  data[ich], ptr, tmp, 0, n );
        tmps.add( new WaveformSegment( chanids[ich].getStation(), chanids[ich].getComponent(), start, delta, tmp, null ) );
      }
      
      ptr += n;
      
      return new StreamSegment( tmps );
    }
    
    
    
    public void setCancellor( SegmentedCancellor cancellor ) {
      this.cancellor = cancellor;
    }
    
    
    
    @Override
    public Void call() throws Exception {
      
      while ( ptr < N ) {
        cancellor.put( getStreamSegment() );
      }

      cancellor.put( getStreamSegment() );                    // adds empty segment as flag that data have run out
        
      return null;
    }
    

    
    public double    getDelta() {
      return delta;
    }



    public TimeT getStartTime() {
        return T;
    }
    
    
    
    public int       getSegmentLength() {
      return segmentLength;
    }
    
    
    
    public ChannelID[] getChanIDs() {
      return chanids;
    }
    
    
    
    public void writeFilteredData( String targetDirectory, int offset, int length ) throws IOException {
        
      float[] tmp = new float[ length ];
        
      for ( int ich = 0;  ich < nch;  ich++ ) {
        SACFileWriter writer = new SACFileWriter( targetDirectory + File.separator + files[ich] + ".filtered" );
        SACHeader header = writer.getHeader();
        header.delta = (float) delta;
        header.b      = 0.0f;
        header.kstnm  = chanids[ich].getStation();
        header.kcmpnm = chanids[ich].getComponent();
        header.setStartTime( new TimeStamp(T.getEpochTime()) );
        System.arraycopy( data[ich], offset, tmp, 0, length );
        writer.writeFloatArray( tmp );
        writer.close();
      }
      
    }
    
    
    
    public void writeFilteredData( String targetDirectory ) throws IOException {
        writeFilteredData( targetDirectory, 0, N );
    }
    
    
    
    public String[]  getFiles() {
        return files;
    }
   
}