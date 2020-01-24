package llnl.gnem.apps.detection.cancellation;

import java.io.IOException;
import java.util.ArrayList;

import java.io.PrintStream;
import java.util.Collections;
import java.util.logging.Level;
import llnl.gnem.apps.detection.cancellation.subspace.CancellingSubspaceDetector;
import llnl.gnem.core.util.ApplicationLogger;

public class DiscreteSegmentedCancellor extends AbstractSegmentedCancellor {

    private final ArrayList< CancellingSubspaceDetector> detectors;
    private final float[][] detectionStatistics;
    private final int nchannels;
    private final ArrayList< Peak> peaks;
    private final int half;
    private final int close;
    private final float detectionThreshold;
    private final int numIterations;

    public DiscreteSegmentedCancellor(int segmentLength,
            double delta,
            CancellationTemplate[] templates,
            double peakHalfWidth,
            double simultaneityThreshold,
            float detectionThreshold,
            int numIterations) throws IOException {

        super(segmentLength, delta, templates);

        // create detectors from templates
        detectors = new ArrayList<>();

        int id = 0;
        for (CancellationTemplate template : templates) {
            detectors.add(new CancellingSubspaceDetector(template, segmentLength, id++));
        }

        detectionStatistics = new float[templates.length][2 * segmentLength];

        nchannels = templates[0].getNumChannels();

        this.detectionThreshold = detectionThreshold;
        half = (int) Math.round(peakHalfWidth / delta);
        close = (int) Math.round(simultaneityThreshold / delta);
        this.numIterations = numIterations;

        peaks = new ArrayList<>();

    }

    @Override
    public void print(PrintStream ps) {
        detectors.stream().forEach((detector) -> {
            ps.println(detector);
        });
    }

    @Override
    public void processSegment() {

        ApplicationLogger.getInstance().log(Level.FINE, "Cancellor got new segment New Segment.");

        peaks.clear();

        int iteration = 0;

        MultiCancellor cancellor = new MultiCancellor(templates, buffer);

        // initial condition:  residuals = original buffer data
        int n = buffer[0].length;

        for (int ich = 0; ich < numChannels; ich++) {
            System.arraycopy(buffer[ich], 0, residuals[ich], 0, n);
        }
        while (iteration < numIterations) {

            ApplicationLogger.getInstance().log(Level.FINEST, "Cancellation iteration: " + iteration);

            generateDetectionStatistics();
            int npeaks = findPeaks();
            if (npeaks > 0) {
                cancellor.cancel(peaks, residuals);

            } else {
                break;
            }
            iteration++;
        }

    }

    private void generateDetectionStatistics() {

        int ptr = 0;
        for (int iseg = 0; iseg < NSEGMENTS; iseg++) {
            for (int i = 0; i < templates.length; i++) {
                float[] detectionStatistic = detectors.get(i).produceStatistic(residuals, ptr);
                System.arraycopy(detectionStatistic, 0, detectionStatistics[i], ptr, segmentLength);
            }
            ptr += segmentLength;
        }

    }

    private int findPeaks() {

        ApplicationLogger.getInstance().log(Level.FINEST, "Finding peaks ...");

        ArrayList< Peak> newPeaks = new ArrayList<>();

        for (int it = 0; it < templates.length; it++) {

            float[] detstat = detectionStatistics[it];
            int templateLength = templates[it].getTemplateLength();

            for (int i = templateLength; i < detstat.length - templateLength; i++) {

                if (detstat[i] > detectionThreshold) {

                    boolean largest = true;
                    for (int j = -half; j <= half; j++) {
                        if (detstat[i + j] > detstat[i]) {
                            largest = false;
                        }
                    }

                    if (largest) {
                        newPeaks.add(new Peak(it, i - templateLength + 1, detstat[i]));
                    }
                }

            }

        }

        Peak.setPeakSortParameter(PeakSortParameter.DETSTAT);
        Collections.sort(newPeaks);
        Collections.reverse(newPeaks);

        // remove redundant peaks
        for (int i = 0; i < newPeaks.size(); i++) {

            Peak peak = newPeaks.get(i);
            if (!peak.shadowed) {

                float d = peak.detstat;
                int ptr = peak.index;
                for (int j = i + 1; j < newPeaks.size(); j++) {
                    Peak other = newPeaks.get(j);
                    if (Math.abs(ptr - other.index) <= close) {
                        if (d >= other.detstat) {
                            other.shadowed = true;
                        }
                    }
                }

            }

        }

        int npeaks = 0;

        ApplicationLogger.getInstance().log(Level.FINEST, "  ... done finding new peaks: ");
        for (Peak peak : newPeaks) {
            if (!peak.shadowed) {
                peaks.add(peak);
                ApplicationLogger.getInstance().log(Level.FINEST, peak.toString());
                npeaks++;
            }
        }

        Peak.setPeakSortParameter(PeakSortParameter.INDEX);
        Collections.sort(peaks);

        return npeaks;
    }

    @Override
    public void shutdown() {
    }

}
