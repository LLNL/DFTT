package llnl.gnem.apps.detection.core.dataObjects;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.StringTokenizer;
import llnl.gnem.core.util.StreamKey;

/*
 * AbstractSpecification is the base specification class for all detectors, i.e. it contains parameter common to all
 * detectors.  There are two constructors, one for detectors specified from a flat file and another for detectors
 * built from the database.
 */
public abstract class AbstractSpecification implements DetectorSpecification, Serializable {

    protected DetectorType detectorType;      // instance of enumeration denoting type of detector
    private float threshold;         // threshold on detection statistic for declaring triggers
    private float blackoutPeriod;    // blackout period (seconds) is period over which triggers 
    //   are suppressed following a declared trigger
    protected Properties parameterList;     // Properties object from which detector parameters are read
    protected ArrayList< StreamKey> staChanList;
    protected TriggerPositionType triggerPositionType;
    private final byte[] freeformBytes;

    static final long serialVersionUID = 1573775494678462610L;
    
    @Override
    public void setThreshold( float value){
        this.threshold = value;
    }

    /*
     * Flat file constructor
     */
    public AbstractSpecification(InputStream stream) throws IOException {

        InputStreamReader isr = null;
        BufferedReader br = null;

        staChanList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        try {
            isr = new InputStreamReader(stream);
            br = new BufferedReader(isr);
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                line = removeComments(line);
                if (line.trim().isEmpty()) {
                    continue;
                }
                if (line.indexOf("=") > 0) {
                    sb.append(line);
                    sb.append("\n");
                } else {
                    sb2.append(line);
                    sb2.append("\n");

                }
            }
            byte[] bytes = sb.toString().getBytes();
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            parameterList = new Properties();
            parameterList.load(bais);
            freeformBytes = sb2.toString().getBytes();
        } finally {
            if (br != null) {
                br.close();

            }
            if (isr != null) {
                isr.close();
            }
        }
        staChanList.addAll(parseStaChanList());
        if (staChanList.isEmpty()) {
            throw new IllegalStateException("No stations specified in specification file");
        }
        threshold = Float.parseFloat(parameterList.getProperty("threshold", "0.2"));
        blackoutPeriod = Float.parseFloat(parameterList.getProperty("blackoutPeriod", "3.0"));

    }

    private ArrayList< StreamKey> parseStaChanList() throws IOException {

        ArrayList<String> lines = this.getStringsForBlock(".StaChanList", ".EndList");
        ArrayList< StreamKey> result = new ArrayList<>();
        for (String line : lines) {
            StringTokenizer st = new StringTokenizer(line);
            int tokenCount = st.countTokens();
            if (tokenCount >= 2) {
                String sta = st.nextToken();
                String chan = st.nextToken();
               result.add(new StreamKey(sta, chan ));
            }
        }

        return result;
    }

    @Override
    public StreamKey getStreamKey(int index) {

        StreamKey retval = null;

        if (index < staChanList.size()) {
            return staChanList.get(index);
        }

        return retval;
    }


    /*
     * Constructor to support instantiation of detectors stored in the database.
     */
    public AbstractSpecification(float threshold,
            float blackoutPeriod,
            Collection< StreamKey> scList) {

        this.threshold = threshold;
        this.blackoutPeriod = blackoutPeriod;
        this.staChanList = new ArrayList<>();
        for (StreamKey sck : scList) {
            staChanList.add(new StreamKey(sck.getSta(), sck.getChan() ));
        }

        if (staChanList.isEmpty()) {
            throw new IllegalStateException("Empty station-channel list");
        }
        freeformBytes = new byte[0];
    }

    @Override
    public Collection< ? extends StreamKey> getStaChanList() {
        return staChanList;
    }

    @Override
    public int getNumChannels() {
        return staChanList.size();
    }

    @Override
    public DetectorType getDetectorType() {
        return detectorType;
    }

    @Override
    public float getThreshold() {
        return threshold;
    }

    @Override
    public float getBlackoutPeriod() {
        return blackoutPeriod;
    }

    @Override
    public TriggerPositionType getTriggerPositionType() {
        return triggerPositionType;
    }

    public static void printSpecificationTemplate(PrintStream ps) {

        ps.println("detectionStatisticPath = <pathname>");
        ps.println("threshold              = <threshold value>");
        ps.println("blackoutPeriod         = <period (sec)>");
        ps.println(".StaChanList");
        ps.println("  <station1> <channel1>");
        ps.println("  <station2> <channel2>");
        ps.println(".EndList");

    }

    @Override
    public void printSpecification(PrintStream ps) {

        ps.println("detector type = " + detectorType);
        ps.println();
        ps.println("threshold                  = " + threshold);
        ps.println("blackoutPeriod             = " + blackoutPeriod);
        ps.println();

        ps.println(" Using stations and channels: ");
        for (StreamKey key : staChanList) {
            ps.println("  " + key);
        }
    }

    private String removeComments(String line) {
        int idx = line.indexOf("#");
        if (idx >= 0) {
            line = line.substring(0, idx).trim();
        }
        return line;
    }

    private InputStream getFreeformStream() {
        return new ByteArrayInputStream(freeformBytes);
    }

    protected ArrayList< String> getStringsForBlock(String blockHeader, String blockFooter) throws IOException {
        InputStream stream = getFreeformStream();
        InputStreamReader isr = null;
        BufferedReader br = null;
        boolean inMySection = false;
        ArrayList< String> result = new ArrayList<>();
        try {
            isr = new InputStreamReader(stream);
            br = new BufferedReader(isr);
            while (true) {

                String line = br.readLine();
                if (line == null) {
                    break;
                }
                if (line.contains(blockHeader)) {
                    inMySection = true;
                    continue;
                }
                if (line.contains(blockFooter)) {
                    break;
                }
                if (inMySection) {
                    result.add(line);
                }

            }
            return result;
        } finally {
            if (br != null) {
                br.close();

            }
            if (isr != null) {
                isr.close();
            }
        }

    }
}
