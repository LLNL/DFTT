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
