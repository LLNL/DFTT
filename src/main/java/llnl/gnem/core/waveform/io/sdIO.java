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
package llnl.gnem.core.waveform.io;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import edu.iris.dmc.seedcodec.Codec;
import edu.iris.dmc.seedcodec.CodecException;
import edu.iris.dmc.seedcodec.DecompressedData;
import edu.sc.seis.seisFile.mseed.Btime;
import edu.sc.seis.seisFile.mseed.DataHeader;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.MiniSeedRead;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed.SeedRecord;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.merge.MergeException;
import llnl.gnem.core.waveform.merge.NamedIntWaveform;

/**
 * A class that reads binary waveform data encoded using miniseed format.
 */
public class sdIO extends BinaryDataReader {

    /**
     * Read a file containing miniseed data and return the result as a
     * BinaryData object.
     *
     * @param stream
     * @param byteOffset
     *            offset into file in words.
     * @param npts
     *            number of words to read.
     * @param buffer
     * @return The BinaryData object holding the uncompressed waveform data.
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Override
    public BinaryData readData(InputStream stream, int byteOffset, int npts, BinaryData buffer) throws Exception {
        Collection<NamedIntWaveform> waveforms = readMiniSeed(stream, byteOffset, buffer.size());
        if (waveforms.isEmpty()) {
            throw new IllegalStateException("No data was read from stream!");
        }
        return extractRequested(waveforms, buffer);
    }

    @Override
    public BinaryData getBuffer(int npts) {
        return new IntBinaryData(npts);
    }

    private static Collection<NamedIntWaveform> readMiniSeed(InputStream fis, int byteOffset, int requested) throws IOException, SeedFormatException, CodecException {

        DataInputStream ls = null;
        BufferedInputStream bis = null;
        Codec codec = new Codec();
        Collection<NamedIntWaveform> waveforms = new ArrayList<>();
        boolean endOfFile = false;

        try {
            bis = new BufferedInputStream(fis, 4096);
            ls = new DataInputStream(bis);
            TimeT predictedStart = null;
            String sta = null;
            String chan = null;
            String locid = null;
            TimeT segmentStart = null;
            Double rate = null;
            ArrayList<int[]> samples = new ArrayList<>();
            int sampleCount = 0;

            MiniSeedRead rf = new MiniSeedRead(ls);
            fis.skip(byteOffset);
            while (!endOfFile) {
                try {
                    SeedRecord sr;
                    sr = rf.getNextRecord();
                    if (sr instanceof DataRecord) {
                        DataRecord dr = (DataRecord) sr;
                        DataHeader dataHeader = dr.getHeader();
                        double sampleRate = dataHeader.calcSampleRateFromMultipilerFactor();

                        //                        TimeT start = dataHeader.getStart();
                        TimeT start = toTimeT(dataHeader.getStartBtime());

                        int nsamp = dataHeader.getNumSamples();
                        if (sta == null) {
                            sta = dataHeader.getStationIdentifier();
                        }
                        if (chan == null) {
                            chan = dataHeader.getChannelIdentifier();
                        }
                        if (locid == null) {
                            locid = dataHeader.getLocationIdentifier();
                        }
                        if (segmentStart == null) {
                            segmentStart = start;
                        }
                        if (rate == null) {
                            rate = sampleRate;
                        }

                        if (predictedStart != null) {
                            double discrepancy = Math.abs(predictedStart.getEpochTime() - start.getEpochTime()) * sampleRate;
                            if (discrepancy >= 1) {
                                if (!samples.isEmpty()) {
                                    waveforms.add(createWaveform(samples, sta, chan, segmentStart, rate));
                                }
                                samples.clear();
                                sampleCount = 0;
                                segmentStart = start;
                            }
                        }
                        predictedStart = new TimeT(start.getEpochTime() + nsamp / sampleRate);

                        {
                            DecompressedData dd = dr.decompress();
                            int[] values = dd.getAsInt();
                            samples.add(values);
                            sampleCount += values.length;
                        }

                        if (sampleCount >= requested) {
                            waveforms.add(createWaveform(samples, sta, chan, segmentStart, rate));
                            return waveforms;
                        }

                    }
                } catch (EOFException e) {
                    endOfFile = true;
                }

            }
            if (!samples.isEmpty()) {
                waveforms.add(createWaveform(samples, sta, chan, segmentStart, rate));
            }
            return waveforms;
        } finally {

            if (ls != null) {
                ls.close();
            }
            if (bis != null) {
                bis.close();
            }
            if (fis != null) {
                fis.close();
            }

        }
    }

    private static NamedIntWaveform createWaveform(ArrayList<int[]> samples, String sta, String chan, TimeT segmentStart, Double rate) {

        return new NamedIntWaveform(sta, chan, segmentStart.getEpochTime(), rate, samples);
    }

    private BinaryData extractRequested(Collection<NamedIntWaveform> waveforms, BinaryData buffer) throws MergeException {
        NamedIntWaveform result = null;
        int totalExtracted = 0;
        for (NamedIntWaveform nw : waveforms) {
            totalExtracted += nw.getNpts();
            if (result == null) {
                result = nw;
            } else {
                result = result.union(nw, true);
            }

            if (result.getNpts() >= buffer.size()) {
                int[] data = result.getData();
                for (int i = 0; i < buffer.size(); i++) {
                    buffer.setInt(i, data[i]);
                }
                return buffer;
            }
        }
        throw new IllegalStateException(String.format("Attempt to fill buffer of " + "size %d failed because only %d points were available!", buffer.size(), totalExtracted));
    }

    public static TimeT toTimeT(Btime time) {
        int tmilli = time.tenthMilli;
        int msec = tmilli / 10;

        GregorianCalendar d = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        d.clear();
        d.set(Calendar.MILLISECOND, msec);
        d.set(Calendar.SECOND, time.sec);
        d.set(Calendar.MINUTE, time.min);
        d.set(Calendar.HOUR, time.hour);
        d.set(Calendar.DAY_OF_YEAR, time.jday);
        d.set(Calendar.YEAR, time.year);
        double milliseconds = d.getTime().getTime();
        double microseconds = 0;

        double remainder = (tmilli - msec * 10) / 10000.0;
        double etime = Math.min((milliseconds / 1000.0) + microseconds / 1000000.0, TimeT.MAX_EPOCH_TIME);
        return new TimeT(etime + remainder);
    }
}
