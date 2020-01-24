package llnl.gnem.apps.detection.cancellation;


import java.util.concurrent.ArrayBlockingQueue;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import llnl.gnem.apps.detection.cancellation.io.ChannelID;

import llnl.gnem.apps.detection.streams.StreamModifier;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.apps.detection.core.dataObjects.StreamSegment;
import llnl.gnem.apps.detection.core.dataObjects.WaveformSegment;

public abstract class AbstractSegmentedCancellor implements SegmentedCancellor, StreamModifier {
    
    protected static final int NSEGMENTS = 2;
	
    protected final ArrayBlockingQueue< StreamSegment > in;
    protected final ArrayBlockingQueue< StreamSegment > out;
	
    protected final CancellationTemplate[]                          templates;
    protected final int                                 segmentLength;
    protected final int                                 numChannels;
    protected final ChannelID[]                         chanids;
    protected final float[][]                           buffer;
    protected final float[][]                           residuals;
    protected final double                              delta;
    
    protected TimeT                                     T;
	
    protected StreamSegment                             segment;
	
	
	
    public AbstractSegmentedCancellor( int segmentLength, double delta, CancellationTemplate[] templates ) {
		
        in  = new ArrayBlockingQueue<>( 1 );
	out = new ArrayBlockingQueue<>( 1 );
		
	this.templates   = templates;
		
	this.segmentLength = segmentLength;
	this.delta       = delta;
		
	chanids     = templates[0].getChannelIDs();
		
	numChannels = templates[0].getNumChannels();
		
	buffer    = new float[ numChannels ][ NSEGMENTS*segmentLength ];
        residuals = new float[ numChannels ][ NSEGMENTS*segmentLength ];
    }

	
	
    protected int loadSegment() throws InterruptedException {
	  
	segment = in.take();
        T = segment.getStartTime();
	  
	int actualLength = segment.getWaveformSegment(0).getData().length;
	  
	if ( segment.getNumChannels()    != numChannels ) throw new IllegalStateException( "Mismatch in number of channels" );
	if ( segment.getSampleInterval() != delta )       throw new IllegalStateException( "Mismatch in sampling rate" );
	  
	for ( int ich = 0;  ich < numChannels;  ich++ ) {
	    
            WaveformSegment waveform = segment.getWaveformSegment( ich );
	    
            // left shift buffer
	    
            for ( int i = 0;  i < segmentLength;  i++ ) {
                buffer[ ich ][ i ] = buffer[ ich ][ i + segmentLength ];
            }
	    
            // copy data from segment into buffer
	    
            float[] tmp = waveform.getData();
            System.arraycopy(tmp, 0, buffer[ich], segmentLength, actualLength );
        }
	  
	return actualLength;
    }
    
    
    
    @Override
    public ChannelID[] getChannelIDs() {
        return chanids;
    }
	
	
	
    @Override
    public int  getNumTemplates() {
	return templates.length;
    }
	
	
	
    @Override
    public void put( StreamSegment segment ) throws InterruptedException {
	in.put( segment );
    }
	
	
	
    @Override
    public StreamSegment take() throws InterruptedException {
	return out.take();
    }
	
	
	
    @Override
    public Void call() throws InterruptedException {
	  
	while (true) {
	    
            int actualLength = loadSegment();
            if ( actualLength > 0 ) {
                processSegment();
                pushProcessedSegment();
            }
            else {
                pushEmptySegment();
                break;
            }
	    
	  }
	  
	  return null;
	}
        
        
        
    private void pushProcessedSegment() {
        
        ArrayList< WaveformSegment > tmps = new ArrayList<  >();
        for ( int ich = 0;  ich < numChannels;  ich++ ) {
            ChannelID chanid = chanids[ich];
            float[] tmp = new float[ segmentLength ];
            System.arraycopy( residuals[ich], segmentLength/2, tmp, 0, segmentLength );
            tmps.add( new WaveformSegment( chanid.getStation(), chanid.getComponent(), T.getEpochTime() - segmentLength/2*delta,1.0/ delta,  tmp) );
        }
        try {   
            out.put( new StreamSegment( tmps ) );
        } catch (InterruptedException ex) {
            Logger.getLogger(AbstractSegmentedCancellor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    private void pushEmptySegment() {
        
        ArrayList< WaveformSegment > tmps = new ArrayList<>();
        tmps.add( new WaveformSegment( null, null, 0.0, 0.0, new float[0], null ) );
        StreamSegment emptySegment = new StreamSegment( tmps );
        try {
            out.put( emptySegment );
        } catch (InterruptedException ex) {
            Logger.getLogger(AbstractSegmentedCancellor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	
}
