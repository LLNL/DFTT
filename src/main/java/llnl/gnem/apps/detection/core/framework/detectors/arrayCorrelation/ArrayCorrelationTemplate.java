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
package llnl.gnem.apps.detection.core.framework.detectors.arrayCorrelation;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import com.oregondsp.io.SACFileWriter;
import llnl.gnem.apps.detection.core.framework.detectors.EmpiricalTemplate;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.FileSystemException;
import llnl.gnem.core.util.StreamKey;


/**
 *
 * @author harris2
 */
public class ArrayCorrelationTemplate extends EmpiricalTemplate implements Serializable {

    static final long serialVersionUID = 2502597330729162426L;
    private static final float FLOAT_TOL = 0.000001f;

    private ArrayList< float[][]> representation;
    private ArrayList< double[]> singularValues;
    private int templateLength;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.representation != null ? this.representation.hashCode() : 0);
        hash = 67 * hash + (this.singularValues != null ? this.singularValues.hashCode() : 0);
        hash = 67 * hash + this.templateLength;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ArrayCorrelationTemplate other = (ArrayCorrelationTemplate) obj;
        if ((this.representation != null && other.representation == null) || (other.representation != null && this.representation == null)) {
            return false;
        }
        if ((this.singularValues != null && other.singularValues == null) || (other.singularValues != null && this.singularValues == null)) {
            return false;
        }
        if (this.templateLength != other.templateLength) {
            return false;
        }
        if (this.singularValues != null && other.singularValues != null) {
            if (this.singularValues.size() != other.singularValues.size()) {
                return false;
            }
            for (int j = 0; j < singularValues.size(); ++j) {
                double[] a1 = singularValues.get(j);
                double[] a2 = other.singularValues.get(j);
                if (a1.length != a2.length) {
                    return false;
                }
                for (int k = 0; k < a1.length; ++k) {
                    if (Math.abs(a1[k] - a2[k]) > FLOAT_TOL) {
                        return false;
                    }
                }
            }
        }

        if (this.representation != null && other.representation != null) {
            if (representation.size() != other.representation.size()) {
                return false;
            }
            for (int j = 0; j < representation.size(); ++j) {
                float[][] a1 = representation.get(j);
                float[][] a2 = other.representation.get(j);
                if (a1.length != a2.length) {
                    return false;
                }
                for (int k = 0; k < a1.length; ++k) {
                    float[] aa1 = a1[k];
                    float[] aa2 = a2[k];
                    if (aa1.length != aa2.length) {
                        return false;
                    }
                    for (int m = 0; m < aa1.length; ++m) {
                        if (Math.abs(aa1[m] - aa2[m]) > FLOAT_TOL) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    

    public ArrayCorrelationTemplate( ArrayList< float[][]>         eventData,
                                     ArrayCorrelationSpecification spec,
                                     PreprocessorParams            parameters ) {

        super( parameters, spec, eventData.size() );                  // TODO:  Shouldn't there be a rank estimator?
        createRepresentationFromData( eventData, spec );
    }
    
    

    /**
     * ArrayCorrelationTemplate constructor intended to construct a template
     * from raw trace data from multiple events. Used in
     * ArrayCorrelationDetector constructor. The event multichannel data are
     * required in an ArrayList. Each ArrayList element is a float[][]
     * containing the data for one event. The first index of the float[][]
     * ranges over the channels of the network or array supplying the data and
     * the second index ranges over time.
     *
     * @param spec Specification object.
     * @param parameters
     * @throws IOException
     */
    public ArrayCorrelationTemplate( ArrayCorrelationSpecification spec, PreprocessorParams parameters ) throws IOException {

        super( parameters, spec, 1 );

        ArrayList< float[][]> eventData = readTrainingData( spec.getEventDirectoryList(), spec.getEventFilePattern() );
        createRepresentationFromData(eventData, spec);
    }
    
    

    /**
     * The guts of creating a representation from event data
     *
     * @param eventData ArrayList< float[][] > containing the trace data of
     * design events
     * @param spec Specification object containing parameters required to create
     * template
     */
    private void createRepresentationFromData( ArrayList< float[][] > eventData, ArrayCorrelationSpecification spec ) {

        int nevents   = eventData.size();
        int nchannels = getnchannels();

        int start = (int) (spec.getOffsetSecondsToWindowStart() * getProcessingParameters().samplingRate / getProcessingParameters().decrate);
        templateLength = (int) (spec.getWindowDurationSeconds() * getProcessingParameters().samplingRate / getProcessingParameters().decrate);

        // preprocess and assemble event data into data matrices
        double[][][] data = new double[nchannels][templateLength][nevents];

        for (int ie = 0; ie < nevents; ie++) {
            float[][] tmp = eventData.get(ie);
            for (int ich = 0; ich < nchannels; ich++) {
                for (int i = 0; i < templateLength; i++) {
                    data[ich][i][ie] = tmp[ich][i + start];
                }
            }
        }

        // scale event waveforms to prevent any one event from dominating the others
        Matrix[] U = new Matrix[nchannels];
        singularValues = new ArrayList< >();
        double[] s2 = new double[nevents];
        double E = 0.0;

        for (int ich = 0; ich < nchannels; ich++) {

            for (int ie = 0; ie < nevents; ie++) {
                double scale = 0.0;
                for (int i = 0; i < templateLength; i++) {
                    scale += data[ich][i][ie] * data[ich][i][ie];
                }
                scale = 1.0 / Math.sqrt(scale);
                for (int i = 0; i < templateLength; i++) {
                    data[ich][i][ie] *= scale;
                }
            }

            Matrix dataMatrix = new Matrix(data[ich]);

            // estimate dimension
            SingularValueDecomposition svd = new SingularValueDecomposition(dataMatrix);
            double[] s = svd.getSingularValues();
            singularValues.add(s);
            for (int i = 0; i < s.length; i++) {
                s2[i] += s[i] * s[i];
                E += s[i] * s[i];
            }

            U[ich] = svd.getU();
        }

        dimension = 0;
        double sum = 0.0;
        while (sum / E < spec.getEnergyCaptureThreshold()) {
            sum += s2[dimension++];
        }

        sum = 0.0;
        for (int i = 0; i < s2.length; i++) {
            sum += s2[i];
            if (i != dimension - 1) {
                String msg = String.format("%d  %f", (i + 1), (sum / E));
                ApplicationLogger.getInstance().log(Level.FINEST, msg);
            } else {
                String msg = String.format("%d  %f    <---", (i + 1), (sum / E));
                ApplicationLogger.getInstance().log(Level.FINEST, msg);
            }
        }
        ApplicationLogger.getInstance().log(Level.FINE, "Template dimension = " + dimension);

        // extract template
        representation = new ArrayList< >();
        for (int ich = 0; ich < nchannels; ich++) {

            float[][] tmp = new float[dimension][templateLength];
            for (int i = 0; i < templateLength; i++) {
                for (int id = 0; id < dimension; id++) {
                    tmp[id][i] = (float) U[ich].get(i, id);
                }
            }

            representation.add(tmp);
        }

    }

    
    
    /**
     * Array correlation template constructor intended for use with a single
     * event cut from a preprocessed data stream.
     *
     * @param parameters
     * @param preprocessedDataFromStream float[][] containing unnormalized,
     * decimated stream data. The first index ranges over channels and the
     * second over time.
     * @param channelIDs ArrayList< StaChan > containing channel identifiers
     */
//    public ArrayCorrelationTemplate( PreprocessorParams parameters, float[][] preprocessedDataFromStream, ArrayList< StaChanKey> channelIDs ) {
//
//        super( parameters, channelIDs, 1 );
//
//        nchannels = preprocessedDataFromStream.length;
//        if (nchannels != chanIDs.size()) {
//            throw new IllegalStateException("Channel ID list length not equal to number of channels in preprocessed data segment");
//        }
//
//        chanIDs = new ArrayList< >(channelIDs);
//        singularValues = new ArrayList< >();
//        templateLength = preprocessedDataFromStream[0].length;
//        dimension = 1;
//        representation = new ArrayList< >();
//
//        for (int ich = 0; ich < nchannels; ich++) {
//
//            float[] trace = preprocessedDataFromStream[ ich];
//
//            float scale = 0.0f;
//            for (int i = 0; i < templateLength; i++) {
//                scale += trace[i] * trace[i];
//            }
//            scale = (float) Math.sqrt(scale);
//
//            double[] s = new double[1];
//            s[0] = scale;
//            singularValues.add(s);
//
//            float[][] tmp = new float[1][templateLength];
//            for (int i = 0; i < templateLength; i++) {
//                tmp[0][i] = trace[i] / scale;
//            }
//            representation.add(tmp);
//        }
//
//    }

    
    
    /**
     * Copy constructor
     *
     * @param template SubspaceTemplate to be copied
     */
    public ArrayCorrelationTemplate( ArrayCorrelationTemplate template ) {

        super( template.parameters, template.specification, template.dimension );

        singularValues = new ArrayList< >();
        for (double[] sv : template.singularValues) {
            singularValues.add(sv.clone());
        }

        this.templateLength = template.templateLength;

        representation = new ArrayList< >();
        for (float[][] T : template.representation) {
            representation.add(T.clone());
        }
    }
    
    

    /**
     * Subspace template constructor that takes an archive template expressed as
     * an ArrayList< float[][] >.
     *
     * @param archiveTemplate The float[] contains the channel-multiplexed data.
     * @param channelIDs ArrayList of station channel IDs.
     * @param singularValues The array of singular values determined during
     * creation of the input template.
     */
//    public ArrayCorrelationTemplate( PreprocessorParams    parameters,
//                                     ArrayList< float[][]> archiveTemplate,
//                                     ArrayList<StaChanKey> channelIDs,
//                                     ArrayList< double[]>  singularValues) {
//
//        super( parameters, channelIDs, archiveTemplate.size() );
//
//        this.singularValues = new ArrayList< >();
//        for (double[] sv : singularValues) {
//            this.singularValues.add(sv.clone());
//        }
//        templateLength = archiveTemplate.get(0).length;
//
//        representation = new ArrayList< >();
//        for (float[][] T : archiveTemplate) {
//            representation.add(T.clone());
//        }
//
//    }

    
    
    public int getTemplateLength() {
        return templateLength;
    }

    
    
    public ArrayList< float[][]> getRepresentation() {
        return representation;
    }

    
    
    public ArrayList< double[]> getSingularValues() {
        return singularValues;
    }

    
    
    public void writeTemplateToSACFiles(String directory, String detectorID) throws IOException {

        ArrayList< float[][]> rep = getRepresentation();
        
        int nchannels = getnchannels();

        String path = directory + File.separator + detectorID;
        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new FileSystemException("Failed to create directory: " + dir.getAbsolutePath());
            }
        }

        for (int id = 0; id < rep.size(); id++) {

            String dimName = (new Integer(id)).toString().trim();
            float[][] traces = rep.get(id);

            for ( int ic = 0;  ic < nchannels;  ic++ ) {
                StreamKey stachan = getStaChanKey(ic);
                String filename = path + File.separator + stachan.getSta().trim() + "_" + stachan.getChan().trim() + "_" + dimName + ".sac";
                SACFileWriter writer = new SACFileWriter(filename);
                writer.getHeader().b = 0.0f;
                writer.getHeader().delta = (float) (getProcessingParameters().decrate / getProcessingParameters().samplingRate);
                writer.writeFloatArray(traces[ic]);
                writer.close();
            }
        }

    }

}
