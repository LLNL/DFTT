package llnl.gnem.apps.detection.core.framework.detectors;

import com.oregondsp.io.SACInputStream;
import com.oregondsp.util.DirectoryListing;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import llnl.gnem.apps.detection.core.dataObjects.DetectorSpecification;
import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;
import llnl.gnem.apps.detection.core.framework.DownSampler;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.StreamKey;

public class EmpiricalTemplate implements Serializable {

    protected     DetectorSpecification          specification;
    protected     int                            dimension;
    protected     PreprocessorParams             parameters;
    private final PersistentProcessingParameters persistentParams;
    
    static final long serialVersionUID = -117421882076591370L;


    protected EmpiricalTemplate( PreprocessorParams parameters, DetectorSpecification specification, int dimension ) {
        this.specification = specification;
        this.dimension     = dimension;
        this.parameters    = parameters;
        persistentParams   = parameters.getPreprocessorParams();
    }

    public Double getTemplateTBP()
    {
        return null;
    }
    
    public boolean consistent( EmpiricalTemplate other ) {
        
        boolean retval = true;
                    
        Collection< StreamKey > channels_o = other.getStaChanList();
        Collection< StreamKey > channels   = getStaChanList();
 
        for ( StreamKey sk : channels_o ) {
            if( !channels.contains(sk) ) 
                retval = false;
        }
        for ( StreamKey sk : channels ) {
            if( !channels_o.contains(sk) ) 
                retval = false;
        }
        
        if ( Math.abs( parameters.getSampleRate() - other.parameters.getSampleRate() ) > 1.0e-3 ) 
            retval = false;
        
        return retval;
    }
    
    

    protected final ArrayList< float[][]> readTrainingData( String[] trainingSegments, String filePattern ) throws IOException {

        ArrayList< float[][]> eventData = new ArrayList<>();

        int nsegments = trainingSegments.length;
        ApplicationLogger.getInstance().log( Level.INFO, "Constructing template from " + nsegments + " events" );
        if ( nsegments > 0 ) {

            String[] filenames = getFileList( trainingSegments[0], filePattern );

            SACInputStream reader = new SACInputStream( trainingSegments[0] + File.separator + filenames[0] );
            int            npts   = reader.header.npts;
            float          delta  = reader.header.delta;
            reader.close();

            if ( Math.abs( 1.0 / delta - persistentParams.samplingRate) > 0.0001 ) {
                throw new IllegalStateException( "Framework target sampling rate and flat file sampling rate are mismatched" );
            }

            int nch = specification.getNumChannels();

            for ( int iseg = 0;  iseg < nsegments;  iseg++ ) {

                ApplicationLogger.getInstance().log( Level.FINE, "   " + trainingSegments[iseg] );
                filenames = getFileList( trainingSegments[iseg], filePattern );

                int nfound = 0;
                float[][] data = new float[nch][];
                float[]   tmp  = new float[npts];
                
                DownSampler downSampler = new  DownSampler( parameters, nch, persistentParams.samplingRate );
                
                
                for ( String filename : filenames ) {
                    ApplicationLogger.getInstance().log(Level.INFO, "\t\tReading "+filename);
                    reader = new SACInputStream( trainingSegments[iseg] + File.separator + filename );
                    StreamKey fileid = new StreamKey( reader.header.kstnm.trim(), reader.header.kcmpnm.trim() );                  
                    
              // find proper position for data

                    int ich = -1;
                    for ( int jch = 0;  jch < nch;  jch++ ) {
                        if ( fileid.equals( specification.getStreamKey(jch) ) ) {
                            ich = jch;
                        }
                    }

                    if ( ich > -1 ) {
                        reader.readData( tmp );
                        data[ich] = downSampler.downSample(tmp);
                        nfound++;
                    }
                    reader.close();
                }

                if ( nfound != nch ) {
                    throw new IllegalStateException( "Missing channels for event " + trainingSegments[iseg] );
                }

                eventData.add(data);
            }

        }

        return eventData;
    }
    
    

    public String[] getFileList(String eventPath, String filePattern) {

        DirectoryListing D = new DirectoryListing(eventPath, filePattern);

        if (D.nFiles() < 1) {
            throw new IllegalStateException(String.format("Failed to find any files matching pattern %s in path %s!", filePattern, eventPath));
        }
        String[] retval = new String[D.nFiles()];

        for (int i = 0; i < D.nFiles(); i++) {
            retval[i] = D.file(i);
        }

        Logger.getLogger(this.getClass().getName()).log(Level.FINE,
                String.format("Found %d files in directory (%s) using pattern (%s)",
                        retval.length,
                        eventPath,
                        filePattern));
        return retval;
    }

    

    public int getnchannels() {
        return specification.getNumChannels();
    }

    

    public int getdimension() {
        return dimension;
    }

    
    
    public DetectorSpecification getSpecification() {
        return specification;
    }
    
    

    public ArrayList< StreamKey> getStaChanList() {
        return new ArrayList<>( specification.getStaChanList() );
    }

    
    
    public StreamKey getStaChanKey( int index ) {
        return specification.getStreamKey( index );
    }
    
    

    public PersistentProcessingParameters getProcessingParameters() {
        return persistentParams;
    }
    
    
    
    public PreprocessorParams getPreprocessingParameters() {
        return parameters;
    }

}
