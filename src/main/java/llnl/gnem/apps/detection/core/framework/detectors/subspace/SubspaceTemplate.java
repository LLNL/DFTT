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
package llnl.gnem.apps.detection.core.framework.detectors.subspace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

import org.ojalgo.array.Array1D;
import org.ojalgo.matrix.Primitive32Matrix;
import org.ojalgo.matrix.Primitive32Matrix.DenseReceiver;
import org.ojalgo.matrix.decomposition.SingularValue;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore.Factory;
import org.ojalgo.matrix.store.Primitive64Store;
import org.ojalgo.matrix.store.RawStore;

import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;
import llnl.gnem.apps.detection.core.dataObjects.TemplateSerializationType;
import llnl.gnem.apps.detection.core.framework.detectors.EmpiricalTemplate;
import llnl.gnem.apps.detection.core.signalProcessing.SVDUpdate;
import llnl.gnem.apps.detection.util.io.SACFileWriter;
import llnl.gnem.dftt.core.dataAccess.dataObjects.ProgressMonitor;
import llnl.gnem.dftt.core.signalprocessing.statistics.SignalPairStats;
import llnl.gnem.dftt.core.signalprocessing.statistics.TimeBandwidthComponents;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.FileSystemException;
import llnl.gnem.dftt.core.util.StreamKey;

public class SubspaceTemplate extends EmpiricalTemplate implements Serializable {

    private static final Factory<Double, Primitive64Store> MATRIX_FACTORY = Primitive64Store.FACTORY;

    private ArrayList<float[]> representation; // trace sequential multiplexed form
    private double[] singularValues;
    private int templateLength;

    static final long serialVersionUID = -6216732028961623252L;
    private final double windowDurationSeconds;
    private final double templateTBP;

    @Override
    public Double getTemplateTBP() {
        return templateTBP;
    }

    public double getWindowDurationSeconds() {
        return windowDurationSeconds;
    }

    public SubspaceTemplate(ArrayList<float[][]> eventData, SubspaceSpecification spec, PreprocessorParams params, boolean fixToSpecifiedDimension, boolean capToSpecifiedDimension,
            int requiredDimension, ProgressMonitor monitor) {

        super(params, spec, eventData.size());

        createRepresentationFromData(eventData, spec, fixToSpecifiedDimension, capToSpecifiedDimension, requiredDimension, monitor);
        windowDurationSeconds = spec.getWindowDurationSeconds();
        templateTBP = computeTemplateTBP();
    }

    public SubspaceTemplate(SubspaceSpecification spec, PreprocessorParams params) throws IOException {

        super(params, spec, 1);

        ArrayList<float[][]> eventData = readTrainingData(spec.getEventDirectoryList(), spec.getEventFilePattern());
        boolean fixToSpecifiedDimension = false;
        boolean capToSpecifiedDimension = false;
        int requiredDimension = 0;
        createRepresentationFromData(eventData, spec, fixToSpecifiedDimension, capToSpecifiedDimension, requiredDimension, null);
        windowDurationSeconds = spec.getWindowDurationSeconds();
        templateTBP = computeTemplateTBP();
    }

    public SubspaceTemplate(SubspaceSpecification spec, PreprocessorParams params, ArrayList<float[]> representation, double[] singularValues) {

        super(params, spec, representation.size());

        this.representation = representation;
        this.singularValues = singularValues;
        windowDurationSeconds = spec.getWindowDurationSeconds();

        templateLength = representation.get(0).length / spec.getNumChannels();
        templateTBP = computeTemplateTBP();
    }

    /**
     * The guts of creating a representation from event data
     *
     * @param eventData
     *            ArrayList< float[][] > containing the trace data of design
     *            events
     * @param spec
     *            Specification object containing parameters required to create
     *            template
     */
    private void createRepresentationFromData(ArrayList<float[][]> eventData, SubspaceSpecification spec, boolean fixToSpecifiedDimension, boolean capToSpecifiedDimension, int requiredDimension,
            ProgressMonitor monitor) {

        int nevents = eventData.size();
        int start = (int) (spec.getOffsetSecondsToWindowStart() * parameters.getPreprocessorParams().samplingRate / parameters.getPreprocessorParams().decrate);
        templateLength = (int) (spec.getWindowDurationSeconds() * parameters.getPreprocessorParams().samplingRate / parameters.getPreprocessorParams().decrate);

        // preprocess and concatenate channels
        int nchannels = spec.getNumChannels();
        int nrows = nchannels * templateLength;

        double[][] data = new double[nrows][nevents];

        for (int ie = 0; ie < nevents; ie++) {
            float[][] tmp = eventData.get(ie);
            for (int ich = 0; ich < nchannels; ich++) {
                for (int i = 0; i < templateLength; i++) {
                    data[ich * templateLength + i][ie] = tmp[ich][i + start];
                }
            }
        }

        // scale event waveforms to prevent any one event from dominating the others
        for (int ie = 0; ie < nevents; ie++) {
            double scale = 0.0;
            for (int i = 0; i < nrows; i++) {
                scale += data[i][ie] * data[i][ie];
            }
            scale = 1.0 / Math.sqrt(scale);
            for (int i = 0; i < nrows; i++) {
                data[i][ie] *= scale;
            }
        }
        if (monitor != null) {
            monitor.setText("Computing SVD...");
        }
        Primitive64Store data2 = MATRIX_FACTORY.rows(data);
        SingularValue<Double> svd2 = SingularValue.PRIMITIVE.make(data2);
        svd2.decompose(data2);

        Array1D<Double> foo = svd2.getSingularValues();
        double[] s3 = new double[(int) foo.length];
        for (int i = 0; i < s3.length; ++i) {
            s3[i] = foo.get(i);
        }

        MatrixStore<Double> myU = svd2.getU();
        long rows = myU.countRows();
        long cols = myU.countColumns();
        DenseReceiver myMatrix = Primitive32Matrix.FACTORY.makeDense((int) rows, (int) cols);
        for (int j = 0; j < rows; ++j) {
            for (int k = 0; k < cols; ++k) {
                myMatrix.set(j, k, myU.get(j, k));
            }
        }
        double E = 0.0;
        for (int i = 0; i < s3.length; i++) {
            s3[i] *= s3[i];
            E += s3[i];
        }
        if (fixToSpecifiedDimension && requiredDimension > 0 && requiredDimension <= s3.length) {
            dimension = requiredDimension;
        } else {
            dimension = 0;
            double sum = 0.0;
            while (sum / E < spec.getEnergyCaptureThreshold()) {
                sum += s3[dimension++];
                if (capToSpecifiedDimension && requiredDimension > 0 && requiredDimension <= s3.length && dimension == requiredDimension) {
                    break;
                }
            }

        }

        ApplicationLogger.getInstance().log(Level.FINE, "Template dimension = " + dimension);
        if (monitor != null) {
            monitor.setText("Unpacking template...");
        }
        unpackTemplate(myMatrix.get(), s3);
    }

    public SubspaceTemplate(PreprocessorParams params, float[][] preprocessedDataFromStream, SubspaceSpecification spec) {

        super(params, spec, 1);

        singularValues = new double[1];
        singularValues[0] = 1.0;
        templateLength = preprocessedDataFromStream[0].length;
        windowDurationSeconds = spec.getWindowDurationSeconds();
        int nchannels = spec.getNumChannels();
        dimension = 1;

        representation = new ArrayList<>();
        float[] tmp = new float[templateLength * nchannels];
        float scale = 0.0f;
        for (int ich = 0; ich < nchannels; ich++) {
            float[] trace = preprocessedDataFromStream[ich];
            for (int i = 0; i < templateLength; i++) {
                tmp[ich * templateLength + i] = trace[i];
                scale += trace[i] * trace[i];
            }
        }

        scale = (float) Math.sqrt(scale);

        for (int i = 0; i < tmp.length; i++) {
            tmp[i] /= scale;
        }

        representation.add(tmp);
        templateTBP = computeTemplateTBP();
    }

    public SubspaceTemplate(SubspaceTemplate template, SubspaceSpecification newSpecification) {

        super(template.parameters, newSpecification, template.dimension);
        windowDurationSeconds = newSpecification.getWindowDurationSeconds();
        this.singularValues = template.singularValues.clone();
        this.templateLength = template.templateLength;

        representation = new ArrayList<>();
        for (int id = 0; id < dimension; id++) {
            float[] tmp = template.representation.get(id);
            representation.add(tmp.clone());
        }
        templateTBP = computeTemplateTBP();
    }

    public SubspaceTemplate(SubspaceTemplate template) {

        super(template.parameters, template.specification, template.dimension);
        windowDurationSeconds = template.windowDurationSeconds;
        this.singularValues = template.singularValues.clone();
        this.templateLength = template.templateLength;

        representation = new ArrayList<>();
        for (int id = 0; id < dimension; id++) {
            float[] tmp = template.representation.get(id);
            representation.add(tmp.clone());
        }
        templateTBP = computeTemplateTBP();
    }

    public int getTemplateLength() {
        return templateLength;
    }

    public ArrayList<float[][]> getRepresentation() {

        int nchannels = getnchannels();

        ArrayList<float[][]> retval = new ArrayList<>();
        for (int id = 0; id < dimension; id++) {

            float[] rep = representation.get(id);
            float[][] tmp = new float[nchannels][templateLength];
            for (int ich = 0; ich < nchannels; ich++) {
                for (int i = 0; i < templateLength; i++) {
                    tmp[ich][i] = rep[ich * templateLength + i];
                }
            }

            retval.add(tmp);
        }

        return retval;
    }

    public ArrayList<float[]> getMultiplexedRepresentation() {
        return representation;
    }

    public double[] getSingularValues() {
        return singularValues;
    }

    public void serialize(String directory, int detectorID, TemplateSerializationType type) throws IOException {
        switch (type) {
        case SACFILE:
            writeTemplateToSACFiles(directory, detectorID);
            return;
        case JAVA_OBJECT:
            serialize(directory, detectorID);
            return;
        default:
            throw new IllegalStateException("Unknown serializationType!");
        }
    }

    public void writeTemplateToSACFiles(String directory, int detectorID) throws IOException {

        ArrayList<float[][]> rep = getRepresentation();

        String path = directory + File.separator + detectorID;
        File dir = new File(path);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new FileSystemException("Failed to create directory: " + dir.getAbsolutePath());
        }

        for (int id = 0; id < rep.size(); id++) {

            String dimName = Integer.toString(id).trim();
            float[][] traces = rep.get(id);

            for (int ic = 0; ic < getnchannels(); ic++) {
                StreamKey stachan = getStaChanKey(ic);
                String filename = path + File.separator + stachan.getSta().trim() + "_" + stachan.getChan().trim() + "_" + dimName + ".sac";
                SACFileWriter writer = new SACFileWriter(filename);
                writer.getHeader().b = 0.0f;
                writer.getHeader().delta = (float) (parameters.getPreprocessorParams().decrate / parameters.getPreprocessorParams().samplingRate);
                writer.writeFloatArray(traces[ic]);
                writer.close();
            }
        }

    }

    public void serialize(String directory, int detectorID) throws IOException {

        String path = directory + File.separator + detectorID;
        File dir = new File(directory);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new FileSystemException("Failed to create directory: " + dir.getAbsolutePath());
        }
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(new File(path));
            oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
        } finally {
            if (oos != null) {
                oos.close();
            }
            if (fos != null) {
                fos.close();
            }
        }

    }

    public static Collection<SubspaceTemplate> deserialize(String directory) throws FileSystemException, IOException, FileNotFoundException, ClassNotFoundException {
        Collection<SubspaceTemplate> result = new ArrayList<>();
        File dir = new File(directory);
        if (!dir.exists()) {

            throw new FileSystemException("No such directory: " + dir.getAbsolutePath());

        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir.toPath())) {
            for (Path entry : stream) {
                SubspaceTemplate tmp = deserializeSingle(entry);
                if (tmp != null) {
                    result.add(tmp);
                }
            }
        }
        return result;
    }

    private static SubspaceTemplate deserializeSingle(Path entry) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fos = null;
        ObjectInputStream oos = null;
        try {
            fos = new FileInputStream(entry.toFile());
            oos = new ObjectInputStream(fos);
            return (SubspaceTemplate) oos.readObject();
        } finally {
            if (oos != null) {
                oos.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    /**
     * Method to update an existing template with data from a newly detected
     * event. The new data must be cut to have the same length as the existing
     * template and is assumed to be preprocessed data, presented in an
     * ArrayList< float[] > with one data channel per ArrayList element. The
     * elements are in standard real format.
     *
     * @param preprocessedDataFromStream
     *            ArrayList< float[] > containing new event data
     * @param energyCaptureThreshold
     *            double containing the energyCaptureThreshold, for future
     *            development
     * @param alpha
     *            double parameter used to scale the new data in the update 0 <
     *            alpha < 1
     * @param lambda
     *            double parameter age-weighting factor for existing template 0
     *            < lambda < 1
     * @throws JampackException
     */
    public void update(ArrayList<float[]> preprocessedDataFromStream, double energyCaptureThreshold, double lambda) {

        int nch = preprocessedDataFromStream.size();
        int n = preprocessedDataFromStream.get(0).length;

        if (n != templateLength) {
            throw new IllegalStateException("Length of data segment " + n + " does not match template length " + templateLength);
        }

        float[] x = repackPreprocessedData(preprocessedDataFromStream);

        int nrows = n * nch;
        double[][] data = new double[nrows][1];

        double scale = 0.0;
        for (int i = 0; i < nrows; i++) {
            data[i][0] = x[i];
            scale += x[i] * x[i];
        }
        scale = 1.0 / Math.sqrt(scale);
        float sc = (float) scale;
        for (int i = 0; i < nrows; i++) {
            data[i][0] *= sc;
        }

        DenseReceiver Y = Primitive32Matrix.FACTORY.makeWrapper(RawStore.wrap(data)).copy();

        // construct projection matrix from template
        DenseReceiver Ua = Primitive32Matrix.FACTORY.makeDense(nrows, dimension);
        for (int id = 0; id < dimension; id++) {
            float[] tmp = representation.get(id);
            for (int i = 0; i < nrows; i++) {
                Ua.set(i, id, tmp[i]);
            }
        }

        // construct matrix of singular values
        DenseReceiver Sa = Primitive32Matrix.FACTORY.makeDense(dimension, dimension);
        for (int id = 0; id < dimension; id++) {
            Sa.set(id, id, singularValues[id]);
        }

        // update matrices
        List<Primitive32Matrix> updates = SVDUpdate.evaluate(Ua.get(), Sa.get(), Y.get(), lambda);

        Primitive32Matrix U = updates.get(0);
        Primitive32Matrix S = updates.get(1);

        // logic to set new dimension
        int Sdim = S.getColDim();
        double Etot = 0.0;
        for (int id = 0; id < Sdim; id++) {
            double tmp = S.get(id, id);
            Etot += tmp * tmp;
        }

        int newDim = 1;
        double E = S.get(0, 0) * S.get(0, 0);
        while (E < Etot * energyCaptureThreshold) {
            newDim++;
            double tmp = S.get(newDim - 1, newDim - 1);
            E += tmp * tmp;
        }

        dimension = newDim;

        // unpack singular values and vectors
        double[] s = new double[dimension];
        for (int id = 0; id < dimension; id++) {
            s[id] = S.get(id, id);
        }

        unpackTemplate(U, s);
    }

    /**
     * Utility to pack preprocessed data into trace sequential order and scale
     *
     * @param preprocessedData
     *            ArrayList< float[] > containing preprocessed data, one channel
     *            per ArrayList element
     * @return float[] Contains packed and scaled data
     */
    private static float[] repackPreprocessedData(ArrayList<float[]> preprocessedData) {

        int nch = preprocessedData.size();
        int n = preprocessedData.get(0).length;

        float[] result = new float[n * nch];

        for (int ich = 0; ich < nch; ich++) {
            float[] tmp = preprocessedData.get(ich);
            System.arraycopy(tmp, 0, result, ich * n, n);
        }

        // normalize
        double scale = 0.0;
        for (float element : result) {
            scale += element * element;
        }
        scale = Math.sqrt(scale);
        for (int i = 0; i < result.length; i++) {
            result[i] /= scale;
        }

        return result;
    }

    private void unpackTemplate(Primitive32Matrix U, double[] s) {

        int nrows = getnchannels() * templateLength;

        // store singular vectors
        representation = new ArrayList<>();
        for (int id = 0; id < dimension; id++) {
            float[] tmp = new float[nrows];
            for (int i = 0; i < nrows; i++) {
                tmp[i] = U.get(i, id).floatValue();
            }
            representation.add(tmp);
        }

        // store singular values
        singularValues = new double[dimension];
        System.arraycopy(s, 0, singularValues, 0, dimension);

    }

    public Primitive32Matrix getU() {

        int nrows = representation.get(0).length;
        int ncols = representation.size();
        DenseReceiver U = Primitive32Matrix.FACTORY.makeDense(nrows, ncols);

        for (int ic = 0; ic < ncols; ic++) {
            float[] u = representation.get(ic);
            for (int ir = 0; ir < nrows; ir++) {
                U.set(ir, ic, u[ir]);
            }
        }

        return U.get();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.representation);
        hash = 71 * hash + Arrays.hashCode(this.singularValues);
        hash = 71 * hash + this.templateLength;
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.windowDurationSeconds) ^ (Double.doubleToLongBits(this.windowDurationSeconds) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SubspaceTemplate other = (SubspaceTemplate) obj;
        if (this.templateLength != other.templateLength) {
            return false;
        }
        if (Double.doubleToLongBits(this.windowDurationSeconds) != Double.doubleToLongBits(other.windowDurationSeconds)) {
            return false;
        }
        if (this.representation.size() != other.representation.size()) {
            return false;
        }
        for (int k = 0; k < this.representation.size(); ++k) {
            for (int j = 0; j < this.representation.get(k).length; ++j) {
                Float v1 = this.representation.get(k)[j];
                Float v2 = other.representation.get(k)[j];
                if (!v1.equals(v2)) {
                    return false;
                }
            }

        }

        if (!Arrays.equals(this.singularValues, other.singularValues)) {
            return false;
        }
        return true;
    }

    public final double computeTemplateTBP() {
        double rate = getProcessingParameters().samplingRate / getProcessingParameters().decrate;
        float[][] data = getRepresentation().get(0);
        double tbpSum = 0;
        for (float[] element : data) {
            TimeBandwidthComponents tbc1 = SignalPairStats.computeTimeBandwidthProduct(element, 1 / rate);
            tbpSum += tbc1.getTBP();
        }
        return tbpSum;
    }

    public TimeBandwidthComponents getTimeFreqStats(int dimension, int channel) {
        double rate = getProcessingParameters().samplingRate / getProcessingParameters().decrate;
        float[][] data = getRepresentation().get(dimension);
        return SignalPairStats.computeTimeBandwidthProduct(data[channel], 1 / rate);
    }

}
