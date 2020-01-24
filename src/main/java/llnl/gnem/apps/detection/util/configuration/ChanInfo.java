/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.util.configuration;

/**
 *
 * @author dodge
 */
public class ChanInfo {

    private final String chan;
    private final String band;
    private final String instrument;
    private final String orientation;
    private final int count;

    public ChanInfo( String chan,
            String band,
            String instrument,
            String orientation, int count) {
        this.chan = chan;
        this.band = band;
        this.instrument = instrument;
        this.orientation = orientation;
        this.count = count;
    }

    @Override
    public String toString() {
        return String.format("%s (band = %s, instrument =  %s, orientation = %s) Count = %d", getChan(),band, instrument, orientation, count);
    }

    /**
     * @return the band
     */
    public String getBand() {
        return band;
    }

    /**
     * @return the instrument
     */
    public String getInstrument() {
        return instrument;
    }

    /**
     * @return the orientation
     */
    public String getOrientation() {
        return orientation;
    }

    /**
     * @return the chan
     */
    public String getChan() {
        return chan;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }
}
