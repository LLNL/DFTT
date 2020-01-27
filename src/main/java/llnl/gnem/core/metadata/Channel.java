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
package llnl.gnem.core.metadata;

import java.util.logging.Level;
import llnl.gnem.core.util.ApplicationLogger;

/**
 *
 * @author dodge1
 */
public class Channel {

    private final BandCode band;
    private final InstrumentCode instrument;
    private final OrientationCode orientation;

    public Channel(BandCode band,
            InstrumentCode instrument,
            OrientationCode code) {
        this.band = band;
        this.instrument = instrument;
        this.orientation = code;
    }

    public Channel(String chan) {
        if (chan.length() < 3) {
            String msg = String.format("Failed constructing Channel. Channel string must be at least 3-characters long! (Supplied = %s)", chan);
            ApplicationLogger.getInstance().log(Level.WARNING, msg);
            throw new IllegalArgumentException(msg);
        }
        try {
            band = BandCode.valueOf(chan.substring(0, 1).toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BandCodeException("Failed constructing channel because of invalid BAND code.", ex);
        }

        try {
            instrument = InstrumentCode.valueOf(chan.substring(1, 2).toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InstrumentCodeException("Failed constructing channel because of invalid Instrument code.", ex);
        }
        String ocode = chan.substring(2, 3).toUpperCase();
        try {
            orientation = OrientationCode.getEnumValue(ocode);
        } catch (IllegalArgumentException ex) {
            throw new OrientationCodeException("Failed constructing channel because of invalid Orientation code.", ex);
        }

    }

    public BandCode getBandCode() {
        return band;
    }

    public InstrumentCode getInstrumentCode() {
        return instrument;
    }

    /**
     * @return the code
     */
    public OrientationCode getOrientationCode() {
        return orientation;
    }

    public String getFDSNChannelString() {
        return band.toString() + instrument.toString() + orientation.toString();
    }

    static class BandCodeException extends IllegalArgumentException {

        public BandCodeException(String msg) {
            super(msg);
        }

        public BandCodeException(String msg, Throwable throwable) {
            super(msg, throwable);
        }
    }

    static class InstrumentCodeException extends IllegalArgumentException {

        public InstrumentCodeException(String msg) {
            super(msg);
        }

        public InstrumentCodeException(String msg, Throwable throwable) {
            super(msg, throwable);
        }
    }

    static class OrientationCodeException extends IllegalArgumentException {

        public OrientationCodeException(String msg) {
            super(msg);
        }

        public OrientationCodeException(String msg, Throwable throwable) {
            super(msg, throwable);
        }
    }
}
