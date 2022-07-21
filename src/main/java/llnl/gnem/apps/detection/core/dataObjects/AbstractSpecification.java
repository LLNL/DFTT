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
package llnl.gnem.apps.detection.core.dataObjects;

import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
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
    public void setThreshold(float value) {
        this.threshold = value;
    }

    /*
     * Flat file constructor
     */
    public AbstractSpecification(InputStream stream) throws IOException {

        staChanList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();

        try (InputStreamReader isr = new InputStreamReader(stream)) {
            try (BufferedReader br = new BufferedReader(isr)) {
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
            String[] tokens = line.trim().split("\\s+");
            int numTokens = tokens.length;
            if (numTokens > 0 && tokens[numTokens - 1].equals("1")) { // old-style specification with weight specifier (no longer supported)
                --numTokens;
            }
            switch (numTokens) {
                case 2:
                    // Old-style sta-chan description
                    result.add(new StreamKey(tokens[0], tokens[1]));
                    break;
                case 4:
                    //net-sta-chan-locid
                    result.add(new StreamKey(tokens[0], tokens[1], tokens[2], tokens[3]));
                    break;
                case 5:
                    //agency-net-sta-chan-locid
                    result.add(new StreamKey(tokens[0], tokens[1], tokens[2], tokens[3], tokens[4]));
                    break;
                case 6:
                    //agency-net-net_date-sta-chan-locid
                    result.add(new StreamKey(tokens[0], tokens[1], Integer.parseInt(tokens[2]), tokens[3], tokens[4], tokens[4]));
                    break;
                default:
                    break;
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
            staChanList.add(sck);
        }

        if (staChanList.isEmpty()) {
            throw new IllegalStateException("Empty station-channel list");
        }
        freeformBytes = new byte[0];
    }

    @Override
    public Collection<StreamKey> getStreamKeys() {
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
