/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.cancellation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletionService;
import java.util.logging.Level;
import llnl.gnem.apps.detection.cancellation.io.ChannelID;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceTemplate;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.TemplateRecluster;
import llnl.gnem.apps.detection.tasks.ComputationService;
import llnl.gnem.core.util.ApplicationLogger;

/**
 *
 * @author dodge1
 */
public class CancellorFactory {

    public static DiscreteSegmentedCancellor createCancellor( String cancellationParamFile, double streamDelta, int segmentLengthSamples ) throws IOException {
        
        CancellationParameters parameters = new CancellationParameters(cancellationParamFile);

        ChannelID[] chanids = parameters.getChannelIDs();
        TemplateBuilder builder = new TemplateBuilder( parameters, streamDelta, chanids );
        int ntemplates = builder.getNumTemplates();
        if( ntemplates < 1){
            throw new IllegalStateException("No cancellation templates were created!");
        }
        CancellationTemplate[] templates = new CancellationTemplate[ ntemplates ];
        ApplicationLogger.getInstance().log(Level.INFO, "Number of cancellation templates:  " + ntemplates);
        for (int i = 0; i < ntemplates; i++) {
            templates[i] = builder.getTemplate(i);
        }

        DiscreteSegmentedCancellor result = new DiscreteSegmentedCancellor( segmentLengthSamples,
                                                                            streamDelta,
                                                                            templates,
                                                                            parameters.getPeakHalfWidth(),
                                                                            parameters.getSimultaneityThreshold(),
                                                                            parameters.getDetectionThreshold(),
                                                                            parameters.getNumberOfIterations()  );
        
        CompletionService<Void> cancellorService = ComputationService.getInstance().getCancellationCompService();
        cancellorService.submit( result );

        return result;
    }
    
    
    
    public static DiscreteSegmentedCancellor createCancellor( String                        cancellationParamFile, 
                                                              double                        streamDelta, 
                                                              int                           segmentLengthSamples, 
                                                              ArrayList< SubspaceTemplate > originalSubspaceTemplates  ) throws IOException {
        
        CancellationParameters parameters = new CancellationParameters( cancellationParamFile );
        
        TemplateRecluster      recluster  = new TemplateRecluster( originalSubspaceTemplates, parameters.getLinkageType() );
        
        ArrayList< SubspaceTemplate > newTemplates = recluster.buildAllTemplates( parameters.getClusteringThreshold(), parameters.getEnergyCapture() );
         
        int ntemplates                   = newTemplates.size();
        CancellationTemplate[] templates = new CancellationTemplate[ ntemplates ];
        for ( int it = 0;  it < ntemplates;  it++ ) templates[ it ] = new CancellationTemplate( newTemplates.get(it) );

        DiscreteSegmentedCancellor result = new DiscreteSegmentedCancellor( segmentLengthSamples,
                                                                            streamDelta,
                                                                            templates,
                                                                            parameters.getPeakHalfWidth(),
                                                                            parameters.getSimultaneityThreshold(),
                                                                            parameters.getDetectionThreshold(),
                                                                            parameters.getNumberOfIterations()  );
        
        CompletionService<Void> cancellorService = ComputationService.getInstance().getCancellationCompService();
        cancellorService.submit( result );
        
        return result;
    }

}
