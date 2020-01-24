package llnl.gnem.apps.detection.cancellation;

import java.util.ArrayList;

import Jama.Matrix;
import com.oregondsp.io.SACFileWriter;
import java.io.File;
import java.io.IOException;
import llnl.gnem.apps.detection.cancellation.io.ChannelID;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceTemplate;
import llnl.gnem.core.util.StreamKey;

public class CancellationTemplate {

    private final ArrayList< float[][]> templateComponents;
    private final int dimension;
    private final int nchannels;
    private final int templateLength;
    private Matrix U;

    private ChannelID[] chanids;

    public CancellationTemplate(ArrayList< float[][]> componentRepresentation, ChannelID[] ids) {

        templateComponents = new ArrayList<>(componentRepresentation);
        dimension = templateComponents.size();
        float[][] tmp = templateComponents.get(0);
        nchannels = tmp.length;
        templateLength = tmp[0].length;

        this.chanids = ids;

        loadU();

    }

    private void loadU() {

        int N = templateLength * nchannels;
        U = new Matrix(N, dimension);
        double[][] Ua = U.getArray();
        for (int id = 0; id < dimension; id++) {
            float[][] tmp = templateComponents.get(id);
            for (int ich = 0; ich < nchannels; ich++) {
                for (int i = 0; i < templateLength; i++) {
                    int j = i * nchannels + ich;
                    Ua[j][id] = tmp[ich][i];
                }
            }
        }

    }

    public CancellationTemplate(Matrix U, ChannelID[] chanids) {

        this.U = U;
        nchannels = chanids.length;

        this.chanids = chanids;

        double[][] Ua = U.getArray();
        dimension = U.getColumnDimension();
        int nrows = U.getRowDimension();
        templateLength = nrows / nchannels;

        templateComponents = new ArrayList<>();

        for (int id = 0; id < dimension; id++) {

            float[][] tmp = new float[nchannels][templateLength];
            for (int i = 0; i < templateLength; i++) {
                for (int ich = 0; ich < nchannels; ich++) {
                    int j = i * nchannels + ich;
                    tmp[ich][i] = (float) Ua[j][id];
                }
            }

            templateComponents.add(tmp);
        }

    }

    // constructs a CancellationTemplate from a SubspaceTemplate
    public CancellationTemplate(SubspaceTemplate subspaceTemplate) {

        templateComponents = subspaceTemplate.getRepresentation();
        dimension = subspaceTemplate.getdimension();
        nchannels = subspaceTemplate.getnchannels();
        templateLength = subspaceTemplate.getTemplateLength();

        loadU();

        ArrayList< StreamKey> tmp = subspaceTemplate.getStaChanList();

        chanids = new ChannelID[nchannels];
        for (int ich = 0; ich < nchannels; ich++) {
            StreamKey sk = tmp.get(ich);
            chanids[ich] = new ChannelID(sk.getSta(), sk.getChan(), sk.getNet(), sk.getLocationCode());
        }

    }

    public int getDimension() {
        return dimension;
    }

    public int getNumChannels() {
        return nchannels;
    }

    public int getTemplateLength() {
        return templateLength;
    }

    public float[][] getComponent(int index) {
        return templateComponents.get(index);
    }

    public Matrix getU() {
        return U;
    }

    public ChannelID[] getChannelIDs() {
        return chanids;
    }

    public String toString() {
        return dimension + "  " + nchannels + "  " + templateLength;
    }

    public void dump(String path) throws IOException {

        File F = new File(path);

        if (!F.exists()) {
            F.mkdir();
        }

        for (int id = 0; id < dimension; id++) {
            float[][] component = templateComponents.get(id);
            for (int ich = 0; ich < nchannels; ich++) {
                ChannelID chid = chanids[ich];
                String file = path + File.separator + chid.getStation() + "." + chid.getComponent() + "_" + id;
                SACFileWriter writer = new SACFileWriter(file);
                writer.getHeader().kstnm = padTo(chid.getStation(), 8);
                writer.getHeader().kcmpnm = padTo(chid.getComponent(), 8);
                writer.getHeader().delta = 1.0f;
                writer.getHeader().b = 0.0f;
                writer.writeFloatArray(component[ich]);
                writer.close();
            }
        }

    }

    private String padTo(String S, int length) {

        String retval = S;
        while (retval.length() < length) {
            retval = retval + " ";
        }

        return retval;
    }

}
