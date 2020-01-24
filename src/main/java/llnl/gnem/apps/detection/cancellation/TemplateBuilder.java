package llnl.gnem.apps.detection.cancellation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import com.oregondsp.io.SACFileWriter;

import com.oregondsp.signalProcessing.Sequence;
import com.oregondsp.signalProcessing.fft.RDFT;
import com.oregondsp.util.DirectoryListing;
import java.util.logging.Level;
import llnl.gnem.apps.detection.cancellation.dendrogram.SimilarityMeasure;
import llnl.gnem.apps.detection.cancellation.dendrogram.SingleLink;
import llnl.gnem.apps.detection.cancellation.io.ChannelID;
import llnl.gnem.apps.detection.cancellation.io.EventDataStructure;
import llnl.gnem.core.util.ApplicationLogger;

public class TemplateBuilder {

    private final ArrayList< EventDataStructure> eventData;
    private final int nch;
    private final int templateLength;    // template length
    private final ArrayList<Integer> dim;               // template dimension
    private final CancellationParameters parameters;
    private final SingleLink linker;
    private final ArrayList< CancellationTemplate> templates;

    public TemplateBuilder(CancellationParameters parameters, double streamDelta, ChannelID[] chanids) throws IOException {

        ApplicationLogger.getInstance().log(Level.INFO, "Building cancellation templates from supplied SAC file collection...");
        this.parameters = parameters;

        nch = chanids.length;

        String ensembleDirectory = parameters.getDesignEventsPath();
        String eventRegEx = parameters.getEventDirectoryPattern();
        String filePattern = parameters.getEventFilePattern();

        ApplicationLogger.getInstance().log(Level.INFO, ensembleDirectory + "  " + eventRegEx + "  " + filePattern);


        DirectoryListing D = new DirectoryListing(ensembleDirectory, eventRegEx);

        eventData = new ArrayList<>();

        for (int i = 0; i < D.nSubdirectories(); i++) {
            EventDataStructure EDS = new EventDataStructure(chanids, ensembleDirectory + File.separator + D.subDirectory(i), filePattern, parameters, streamDelta);
            eventData.add(EDS);
        }

        ApplicationLogger.getInstance().log(Level.FINE, "Number of events: " + eventData.size());

        int tstart = (int) Math.round(parameters.getTemplateStart() / streamDelta);
        int tlength = (int) Math.round(parameters.getTemplateLength() / streamDelta);

        int maxLag = (int) Math.round(parameters.getMaxOffset() / streamDelta);

        int initialOffset = tstart;
        int initialLength = tlength;

        int offset = tstart;
        templateLength = tlength;

        linker = correlateEvents(initialOffset, initialLength, maxLag);

        float clusteringThreshold = parameters.getClusteringThreshold();

     //   linker.print(System.out, clusteringThreshold);
        ArrayList< Object[]> clusters = linker.getClusters(clusteringThreshold);

        ApplicationLogger.getInstance().log(Level.FINEST, "Number of clusters:  " + clusters.size());
        ApplicationLogger.getInstance().log(Level.FINEST, "  Cluster sizes: ");
        clusters.stream().forEach((cluster) -> {
            ApplicationLogger.getInstance().log(Level.FINEST, "" + cluster.length);
        });

        templates = new ArrayList<>();
        dim = new ArrayList<>();

        int templateID = 0;
        for (Object[] cluster : clusters) {

            if (cluster.length >= parameters.getMinNumEvents()) {
                templates.add(new CancellationTemplate(constructRepresentation(cluster, templateID++, offset, templateLength), chanids));
            }
        }

    }

    private Matrix constructRepresentation(Object[] elements, int templateID, int offset, int length) {

        ApplicationLogger.getInstance().log(Level.FINEST, "Constructing template from cluster of " + elements.length + " events");

        int nevents = elements.length;

        // shift waveforms and pack into a data matrix
        Matrix X = new Matrix(nch * length, nevents);

        for (int ie = 0; ie < elements.length; ie++) {

            int eventIndex = (Integer) elements[ie];
            int delay = (int) linker.getDelay(eventIndex);
            float[][] waveforms = eventData.get(eventIndex).waveforms;

            for (int ich = 0; ich < nch; ich++) {

                Sequence.zeroShift(waveforms[ich], -delay);

                for (int i = 0; i < length; i++) {
                    X.set(i * nch + ich, ie, waveforms[ich][i + offset]);
                }

            }
        }

        // scale events
        Matrix P = X.transpose().times(X);
        Matrix S = new Matrix(nevents, nevents);
        for (int i = 0; i < nevents; i++) {
            S.set(i, i, 1.0 / Math.sqrt(P.get(i, i)));
        }
        X = X.times(S);

        // calculate svd and estimate dimension of template
        SingularValueDecomposition svd = new SingularValueDecomposition(X);

        double[] s = svd.getSingularValues();

        ApplicationLogger.getInstance().log(Level.FINEST, "Top singular values: ");

        int d = 0;

        double threshold = parameters.getEnergyCapture();

        double E = 0.0;
        for (int i = 0; i < s.length; i++) {
            s[i] *= s[i];
            E += s[i];
        }
        double C = 0.0;
        for (int i = 0; i < Math.min(25, s.length); i++) {

            C += s[i];
            if (C > threshold * E && d == 0) {
                d = i + 1;
                ApplicationLogger.getInstance().log(Level.FINEST, i + "  " + C / E + "  <--");
            } else {
                ApplicationLogger.getInstance().log(Level.FINEST, i + "  " + C / E);
            }
        }

        ApplicationLogger.getInstance().log(Level.FINEST, "... done,  dim:  " + d);

        Matrix U = svd.getU();

        dim.add(d);

        return U.getMatrix(0, length * nch - 1, 0, d - 1);
    }

    public int getNumTemplates() {
        return templates.size();
    }

    public CancellationTemplate getTemplate(int index) {
        return templates.get(index);
    }

    public int getTemplateLength() {
        return templateLength;
    }

    public int getTemplateDimension(int index) {
        return dim.get(index);
    }

    public final SingleLink correlateEvents(int offset, int n, int maxLag) throws IOException {

        ApplicationLogger.getInstance().log(Level.FINE, "\nCorrelating events ...");

        int nEvents = eventData.size();
        ArrayList< SimilarityMeasure> M = new ArrayList<>();

        ArrayList< Object> proxies = new ArrayList<>();
        for (int i = 0; i < nEvents; i++) {
            proxies.add(i);
        }

        int log2nfft = 2;
        int nfft = 4;
        while (nfft < 2 * n - 1) {
            nfft *= 2;
            log2nfft++;
        }
        RDFT fft = new RDFT(log2nfft);

        float[] tmp = new float[nfft];
        float[][] X = new float[nch][nfft];
        float[] Y = new float[nfft];
        float[] accum = new float[nfft];

        for (int ie = 0; ie < nEvents; ie++) {
            ApplicationLogger.getInstance().log(Level.FINEST, "\tProcessing event " + ie + " ...");
            // transform of first event
            float[][] x = eventData.get(ie).waveforms;
            float xe = 0.0f;
            for (int ich = 0; ich < nch; ich++) {
                Arrays.fill(tmp, 0.0f);
                System.arraycopy(x[ich], offset, tmp, 0, n);
                for (int k = 0; k < n; k++) {
                    xe += tmp[k] * tmp[k];
                }
                fft.evaluate(tmp, X[ich]);
            }

            for (int je = ie + 1; je < nEvents; je++) {

                // transform of second event
                Arrays.fill(accum, 0.0f);
                float[][] y = eventData.get(je).waveforms;
                float ye = 0.0f;
                for (int ich = 0; ich < nch; ich++) {

                    Arrays.fill(tmp, 0.0f);
                    System.arraycopy(y[ich], offset, tmp, 0, n);
                    for (int k = 0; k < n; k++) {
                        ye += tmp[k] * tmp[k];
                    }
                    fft.evaluate(tmp, Y);
                    RDFT.dftProduct(X[ich], Y, -1.0f);

                    // correlation function calculation
                    fft.evaluateInverse(Y, tmp);
                    for (int k = 0; k < nfft; k++) {
                        accum[k] += tmp[k];
                    }
                }

                // search for maximum
                float Cmax = 0.0f;
                int indexMax = 0;
                for (int k = 0; k < nfft; k++) {
                    if (Cmax < Math.abs(accum[k])) {
                        indexMax = k;
                        Cmax = Math.abs(accum[k]);
                    }
                }
                Cmax /= ((float) Math.sqrt(xe * ye));
                if (indexMax > nfft / 2) {
                    indexMax -= nfft;
                }

                if (Math.abs(indexMax) <= maxLag) {
                    M.add(new SimilarityMeasure(proxies.get(ie), proxies.get(je), Cmax, (float) indexMax));
                }
            }
        }

        ApplicationLogger.getInstance().log(Level.FINEST, "... done with correlation, linking ...");
        SingleLink retval = new SingleLink(M, 0.0f, proxies);
        ApplicationLogger.getInstance().log(Level.FINEST, "... done linking");

        return retval;
    }

    public static void dumpSACFile(float[] x, String SACFile) throws IOException {
        SACFileWriter writer = new SACFileWriter(SACFile);
        writer.getHeader().b = 0.0f;
        writer.getHeader().delta = 0.05f;
        writer.writeFloatArray(x);
        writer.close();
    }

}
