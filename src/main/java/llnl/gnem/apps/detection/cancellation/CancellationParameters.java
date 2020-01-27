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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;
import llnl.gnem.apps.detection.cancellation.dendrogram.LinkageType;
import llnl.gnem.apps.detection.cancellation.io.ChannelID;

public class CancellationParameters {

    private CancellorType  cancellorType;
    private String         streamDirectory;
    private String         streamFilePattern;
    private String         processedTracesDirectory;
    private double         flow;
    private double         fhigh;
    private String         templateName;
    private String         designEventsPath;
    private String         eventDirectoryPattern;
    private String         eventFilePattern;
    private double         templateStart;
    private double         templateLength;
    private double         maxOffset;
    private int            minNumEvents;
    private double         energyCapture;
    private float          detectionThreshold;
    private float          clusteringThreshold;
    private double         peakHalfWidth;
    private double         simultaneityThreshold;
    private int            numberOfIterations;
    private double         blockLength;
    private double         damping;
    private ChannelID[]    channelIDs;
    private LinkageType    linkageType;
	  
	  
    public CancellationParameters( String parfile ) {
		  
        try {
		        
            FileInputStream stream = new FileInputStream( parfile );
            Properties parameters = new Properties();
            parameters.load( stream );
            stream.close();
				  
            System.out.println( "Using parameters from:  " + parfile + "\n" );
            String type = parameters.getProperty( "cancellorType", "DISCRETE" );
            if ( type.equals( "DISCRETE" ) ) 
                cancellorType = CancellorType.DISCRETE;
            else if ( type.equals( "CONTINUOUS" ) ) 
                cancellorType = CancellorType.CONTINUOUS;
            
            String staString         =                     parameters.getProperty( "stations"                           );
            String chanString        =                     parameters.getProperty( "channels"                           );
            
            String[] stations = staString.split( "\\s+" );
            String[] channels = chanString.split( "\\s+" );
            if ( stations.length != channels.length ) throw new IllegalStateException( "Stations do not match channels" );
            channelIDs = new ChannelID[ stations.length ];
            for ( int i = 0;  i < stations.length;  i++ ) channelIDs[i] = new ChannelID( stations[i], channels[i] );
            
            streamDirectory          =                     parameters.getProperty( "streamDirectory"                    );
            streamFilePattern        =                     parameters.getProperty( "streamFilePattern"                  );
            processedTracesDirectory =                     parameters.getProperty( "processedTracesDirectory"           );
            flow                     = Double.parseDouble( parameters.getProperty( "lowCutoff",             "2.0"     ) );
            fhigh                    = Double.parseDouble( parameters.getProperty( "highCutoff",            "8.0"     ) );
            templateName             =                     parameters.getProperty( "templateName",          "cleaner"   );
            designEventsPath         =                     parameters.getProperty( "designEventsPath"                   );
            eventDirectoryPattern    =                     parameters.getProperty( "eventDirectoryPattern"              );
            eventFilePattern         =                     parameters.getProperty( "eventFilePattern"                   );
            templateStart            = Double.parseDouble( parameters.getProperty( "templateStart",         "0.0"     ) );
            templateLength           = Double.parseDouble( parameters.getProperty( "templateLength",        "5.0"     ) );
            maxOffset                = Double.parseDouble( parameters.getProperty( "maxOffset",             "0.5"     ) );
            minNumEvents             = Integer.parseInt(   parameters.getProperty( "minNumEvents",          "10"      ) );
            energyCapture            = Double.parseDouble( parameters.getProperty( "energyCapture",         "0.95"    ) );
            detectionThreshold       = Float.parseFloat(   parameters.getProperty( "detectionThreshold",    "0.6"     ) );
            clusteringThreshold      = Float.parseFloat(   parameters.getProperty( "clusteringThreshold",   "0.8"     ) );
            peakHalfWidth            = Double.parseDouble( parameters.getProperty( "peakHalfWidth",         "0.2"     ) );
            simultaneityThreshold    = Double.parseDouble( parameters.getProperty( "simultaneityThreshold", "2.0"     ) );
            numberOfIterations       = Integer.parseInt(   parameters.getProperty( "numberOfIterations",    "1"       ) );
            blockLength              = Double.parseDouble( parameters.getProperty( "blockLength",           "60.0"    ) );
            damping                  = Double.parseDouble( parameters.getProperty( "damping",               "0.001"   ) );
            
            String linker = parameters.getProperty( "linkage", "CompleteLink" );
            switch ( linker ) {
                case "SingleLink":
                    linkageType = LinkageType.SingleLink;
                    break;
                case "CompleteLink":
                    linkageType = LinkageType.CompleteLink;
                    break;
                default:
                    linkageType = LinkageType.CompleteLink;
            }
            
            File tmp = new File( processedTracesDirectory );
            if ( !tmp.exists() ) tmp.mkdir();
		  
				        
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        
    }
    
    
    
    public ChannelID[]   getChannelIDs() {
        return channelIDs;
    }
    
    
    
    public CancellorType getCancellorType() {
        return cancellorType;
    }

	  
	
    public String getStreamDirectory() {
        return streamDirectory;
    }


    public String getStreamFilePattern() {
        return streamFilePattern;
    }


    public String getProcessedTracesDirectory() {
        return processedTracesDirectory;
    }


    public double getFlow() {
        return flow;
    }


    public double getFhigh() {
        return fhigh;
    }


    public String getTemplateName() {
        return templateName;
    }


    public String getDesignEventsPath() {
        return designEventsPath;
    }


    public String getEventDirectoryPattern() {
        return eventDirectoryPattern;
    }


    public String getEventFilePattern() {
        return eventFilePattern;
    }


    public double getTemplateStart() {
        return templateStart;
    }


    public double getTemplateLength() {
        return templateLength;
    }


    public double getMaxOffset() {
        return maxOffset;
    }
	
	
    public int getMinNumEvents() {
        return minNumEvents;
    }


    public double getEnergyCapture() {
        return energyCapture;
    }


    public float getDetectionThreshold() {
        return detectionThreshold;
    }
	
	
    public float getClusteringThreshold() {
        return clusteringThreshold;
    }


    public double getPeakHalfWidth() {
        return peakHalfWidth;
    }
	
	
    public double getSimultaneityThreshold() {
        return simultaneityThreshold;
    }


    public int getNumberOfIterations() {
        return numberOfIterations;
    }
	
	
    public double getDamping() {
        return damping;
    }
	
	
    public double getBlockLength() {
        return blockLength;
    }
    
    
    public LinkageType getLinkageType() {
        return linkageType;
    }
	
	
    public static void writeParfileTemplate( String filename ) throws IOException {
		
        PrintStream ps = new PrintStream( new FileOutputStream( filename ) );
		
	ps.println( "! Parameters used by both cancellors" );
	ps.println( "!" );
        ps.println( "stations                 = <station list  [ SPA0 SPA0 SPA0 SPB1 SPB1 SPB1 ...] > " );
        ps.println( "channels                 = <station list> [ BHZ  BHN  BHE  BHZ  BHN  BHE  ...] > " );
        ps.println( "cancellorType            = <[DISCRETE] | CONTINUOUS>" );
	ps.println( "streamDirectory          = <path for directory containing stream SAC files>" );
	ps.println( "streamFilePattern        = <filename regular expression for stream SAC files, Java convention>" );
	ps.println( "processedTracesDirectory = <path for directory containing output SAC files>" );
	ps.println( "flow                     = <freq (Hz) [2.0]>" );
	ps.println( "fhigh                    = <freq (Hz) [8.0]>" );
        ps.println( "templateName             = <base filename [cleaner]>" );
	ps.println( "designEventsPath         = <directory path for event subdirectories> " );
	ps.println( "eventDirectoryPattern    = <event subdirectory regular expression>" ); 
	ps.println( "eventFilePattern         = <filename regular expression for event SAC files, single channel>" );
	ps.println( "templateStart            = <starting position of template segment in event SAC files (secs) [0.0]>" );
	ps.println( "templateLength           = <template length (sec) [5.0]>" );
	ps.println( "maxOffset                = <maximum permissible offset in correlation measurements (secs) [0.5]>" );
	ps.println( "clusteringThreshold      = <linkage correlation threshold for defining clusters [0.8]>" );
        ps.println( "linkageType              = <SingleLink|CompleteLink [CompleteLink]>" );
	ps.println( "minNumEvents             = <minimum cluster size for building a template [10]>" );
	ps.println( "energyCapture            = <minimum fraction of singular value energy, used to define template dimension [0.95]>" );
	ps.println( "blockLength              = <segment size in seconds [60.0]>" );
	ps.println( "!" );
	ps.println( "! Parameters used by detecting cancellor only" );
	ps.println( "!" );
	ps.println( "detectionThreshold       = <detection statistic threshold for declaring a trigger [0.6]>" );
	ps.println( "peakHalfWidth            = <minimum search distance (secs) for defining a peak for triggering [0.2]>" );
	ps.println( "simultaneityThreshold    = <time offset (secs) for declaring simultaneous triggers [2.0]>" );
	ps.println( "numberOfIterations       = <number of cancellation iterations [1]>" );
	ps.println( "!" );
	ps.println( "! Parameters used by continuous cancellor only" );
	ps.println( "!" );
	ps.println( "damping                  = <damping factor for least-squares continuous source estimation [0.001]>" );

	ps.close();
    }
}
