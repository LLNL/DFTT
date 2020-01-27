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
package llnl.gnem.core.database.dao;

import llnl.gnem.core.waveform.Wfdisc;
import llnl.gnem.core.waveform.io.BinaryData;
import llnl.gnem.core.waveform.io.BinaryDataReader;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;

/**
 *
 * @author addair1
 */
public abstract class WaveformDAO {
     /**
     * Used heavily in Big Data Applications
     *
     * @param wfdisc
     * @param rawData
     * @return
     */
    public static CssSeismogram getSeismogram(Wfdisc wfdisc, byte[] rawData) {
        try {
            BinaryDataReader bdr = BinaryDataReader.getReader(wfdisc.getDatatype());
            BinaryData bdata = bdr.readFloatData(rawData, wfdisc.getFoff(), wfdisc.getNsamp());

            return new CssSeismogram(wfdisc, bdata);
        } catch (Exception ex) {
            String msg = String.format("Failed attempting to read %d samples "
                    + "of %s data at offset %d", wfdisc.getNsamp(), wfdisc.getDatatype(), wfdisc.getFoff());
            throw new IllegalStateException(msg, ex);
        }
    }

    public abstract CssSeismogram getSeismogram(Wfdisc wfdisc) throws InterruptedException;

    public BinaryData getBinaryData(String dir, String dfile, int foff, int nsamp, String datatype) throws InterruptedException {
        String fname = dir + '/' + dfile;

        BinaryDataReader bdr = BinaryDataReader.getReader(datatype);
        if (bdr != null) {
            try {
                return bdr.readFloatData(fname, foff, nsamp);
            } catch (Exception e) {

                if (e instanceof InterruptedException) {
                    throw new InterruptedException();
                }
                String msg = String.format("Failed attempting to read %d samples "
                        + "of %s data at offset %d for file: %s", nsamp, datatype, foff, fname);

                throw new IllegalStateException(msg, e);
            }
        } else {
            throw new IllegalStateException("No BinaryDataReader was instantiated. Could not read data.");
        }
    }

    public float[] getSeismogramData(String dir, String dfile, int foff, int nsamp, String datatype) throws InterruptedException {
        return getBinaryData(dir, dfile, foff, nsamp, datatype).getFloatData();
    }
}
