/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.waveform.responseProcessing;

import java.io.IOException;
import java.util.StringTokenizer;
import llnl.gnem.core.util.FileInputArrayLoader;

/**
 *
 * @author dodge1
 */
public class Identifier {

    @Override
    public String toString() {
        return "Identifier{" + "sta=" + sta + ", chan=" + chan + ", locid=" + locid + ", net=" + net + '}';
    }

    private final String sta;
    private final String chan;
    private final String locid;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.sta != null ? this.sta.hashCode() : 0);
        hash = 71 * hash + (this.chan != null ? this.chan.hashCode() : 0);
        hash = 71 * hash + (this.locid != null ? this.locid.hashCode() : 0);
        hash = 71 * hash + (this.net != null ? this.net.hashCode() : 0);
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
        final Identifier other = (Identifier) obj;
        if ((this.sta == null) ? (other.sta != null) : !this.sta.equals(other.sta)) {
            return false;
        }
        if ((this.chan == null) ? (other.chan != null) : !this.chan.equals(other.chan)) {
            return false;
        }
        if ((this.locid == null) ? (other.locid != null) : !this.locid.equals(other.locid)) {
            return false;
        }
        if ((this.net == null) ? (other.net != null) : !this.net.equals(other.net)) {
            return false;
        }
        return true;
    }
    private final String net;

    Identifier(String sta,
            String chan,
            String locid,
            String net) {
        this.sta = sta;
        this.chan = chan;
        this.locid = locid;
        this.net = net;
    }

    /**
     * @return the sta
     */
    public String getSta() {
        return sta;
    }

    /**
     * @return the chan
     */
    public String getChan() {
        return chan;
    }

    /**
     * @return the locid
     */
    public String getLocid() {
        return locid;
    }

    /**
     * @return the net
     */
    public String getNet() {
        return net;
    }

    public static Identifier getIdentifier(String inFile) throws IOException {
        String[] lines = FileInputArrayLoader.fillStrings(inFile);
        String sta = null;
        String chan = null;
        String net = null;
        String locid = null;
        for (String line : lines) {
            if (line.contains("B050F03")) {
                sta = getThirdToken(line);
                if (allTokensParsed(sta, chan, net, locid)) {
                    return new Identifier(sta, chan, locid, net);
                }
            } else if (line.contains("B050F16")) {
                net = getThirdToken(line);
                if (allTokensParsed(sta, chan, net, locid)) {
                    return new Identifier(sta, chan, locid, net);
                }
            } else if (line.contains("B052F03")) {
                locid = getThirdToken(line);
                if (allTokensParsed(sta, chan, net, locid)) {
                    return new Identifier(sta, chan, locid, net);
                }
            } else if (line.contains("B052F04")) {
                chan = getThirdToken(line);
                if (allTokensParsed(sta, chan, net, locid)) {
                    return new Identifier(sta, chan, locid, net);
                }
            }
        }
        throw new IllegalStateException("Failed to find required ID blockettes in file: " + inFile);
    }

    private static boolean allTokensParsed(String sta, String chan, String net, String locid) {
        return sta != null && chan != null && net != null && locid != null;
    }

    private static String getThirdToken(String line) {
        StringTokenizer st = new StringTokenizer(line);
        if (st.countTokens() >= 3) {
            st.nextToken();
            st.nextToken();
            return st.nextToken();
        } else {
            return "*";
        }
    }
}