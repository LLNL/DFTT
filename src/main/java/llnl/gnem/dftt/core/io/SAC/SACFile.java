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
package llnl.gnem.dftt.core.io.SAC;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import llnl.gnem.dftt.core.signalprocessing.Sequence;
import llnl.gnem.dftt.core.util.TimeT;
import llnl.gnem.dftt.core.waveform.seismogram.BasicSeismogram;
import llnl.gnem.dftt.core.waveform.seismogram.CssSeismogram;

/**
 * User: matzel Date: May 30, 2006 Time: 3:13:49 PM
 */
public class SACFile {

    public SACFile(String filename) {
        this.file = new File(filename);

        if (file.exists()) {
            SACFileReader reader = null;

            try {
                reader = new SACFileReader(file.getAbsolutePath());
                this.header = reader.header;
                this.sequence = reader.readSequence(header.npts);
            } catch (Exception e) {
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } else {
            create();
        }

    }

    public SACFile(BasicSeismogram seis) {
        header = new SACHeader();
        header.setBeginTime(seis.getTime());
        header.kstnm = seis.getSta();
        header.kcmpnm = seis.getChan();
        header.delta = (float) (1 / seis.getSamprate());

        sequence = new Sequence(seis.getData());

    }

    public SACFile(InputStream stream) throws Exception {
        this.file = null;

        SACFileReader reader = null;
        try {
            reader = new SACFileReader(stream);
            this.header = reader.header;
            this.sequence = reader.readSequence(header.npts);
        } catch (Exception e) {
            throw new Exception("Unable to read sac file from stream!", e);
        }
    }

    /**
     * Open a SAC file and create the SACFile object
     *
     * @param file
     */
    public SACFile(File file) {
        this.file = file;

        if (file.exists()) {
            SACFileReader reader = null;
            try {
                reader = new SACFileReader(file.getAbsolutePath());
                this.header = reader.header;
                this.sequence = reader.readSequence(header.npts);
            } catch (Exception e) {
                System.err.println("Unable to read sac file: " + file.getName() + " e");
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } else {
            create();
        }
    }

    public SACFile(File file, TimeT startcutTime, TimeT endcutTime) {
        this.file = file;

        if (file.exists()) {
            SACFileReader reader = null;
            try {
                reader = new SACFileReader(file.getAbsolutePath());
                this.header = reader.header;

                double fileStartTime = reader.getEpochStartTime();
                long offset = (long) ((startcutTime.getEpochTime() - fileStartTime) / header.delta); // desired starttime - actual start time
                int nPtsRequested = (int) ((endcutTime.subtract(startcutTime)).getEpochTime() / header.delta);
                reader.skipSamples(offset);
                this.sequence = reader.readSequence(nPtsRequested);

                header.setBeginTime(startcutTime);

                reader.close();
            } catch (Exception e) {
                System.err.println("Unable to read sac file: " + e);
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } else {
            create();
        }
    }

    public SACFile(File file, TimeT startcutTime, int nPtsRequested) {
        this.file = file;

        if (file.exists()) {
            SACFileReader reader = null;
            try {
                reader = new SACFileReader(file.getAbsolutePath());
                this.header = reader.header;

                double fileStartTime = reader.getEpochStartTime();
                long offset = (long) ((startcutTime.getEpochTime() - fileStartTime) / header.delta); // desired starttime - actual start time
                reader.skipSamples(offset);
                this.sequence = reader.readSequence(nPtsRequested);

                header.setBeginTime(startcutTime);

                reader.close();
            } catch (Exception e) {
                System.err.println("Unable to read sac file: " + e);
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } else {
            create();
        }
    }

    /**
     * Assigns a specific header and sequence to a given File
     */
    public SACFile(File file, SACHeader header, Sequence sequence) {
        this.file = file;
        this.header = header;
        this.sequence = sequence;
        setSequenceHeaderValues(sequence);
    }

    private void create() {
        this.header = new SACHeader();
        this.sequence = new Sequence();
    }

    /**
     * overwrite the original file
     */
    public void write() {
        try {
            SACFileWriter writer = new SACFileWriter(file.getAbsolutePath(), header, sequence);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * write to a specific file
     *
     * @param file - the designated file
     */
    public void write(File file) {
        this.file = file;
        write();
    }

    public File getFile() {
        return file;
    }

    public SACHeader getHeader() {
        return header;
    }

    public float[] getData() {
        return sequence.getArray();
    }

    public Sequence getSequence() {
        return sequence;
    }

    public void setSequence(Sequence sequence) {
        this.sequence = sequence;

        setSequenceHeaderValues(sequence);
    }

    /**
     * Replace the data sequence with a CssSeismogram object change the header
     * values where necessary
     *
     * @param seis
     */
    public void setSequence(CssSeismogram seis) {
        header.kstnm = seis.getSta();
        header.kcmpnm = seis.getChan();
        header.delta = (float) (1. / seis.getSamprate());
        sequence = new Sequence(seis.getData());

        TimeT starttime = seis.getTime(); // this is the absolute time of the first point in the CssSeismogram

        header.setBeginTime(starttime);

        setSequenceHeaderValues(sequence);
    }

    /**
     * These variables are determined by the data sequence
     */
    private void setSequenceHeaderValues(Sequence S) {
        header.npts = S.length();
        header.depmax = S.max();
        header.depmin = S.min();
        header.depmen = S.mean();
        header.validate(); // this makes sure that all the dependent variables (e.g. b,e, dist, az ...) match up
    }

    /**
     * Create a CssSeismogram object from the SAC file
     *
     * @return
     */
    public CssSeismogram createSeismogram() {
        int wfid = -1;                   // default value - there is no wfid for a flat file
        String sta = header.kstnm.trim();
        String chan = header.kcmpnm.trim();
        double samprate = 1. / (double) header.delta;
        TimeT timet = new TimeT(header.nzyear,
                header.nzjday,
                header.nzhour,
                header.nzmin,
                header.nzsec,
                header.nzmsec);
        timet = timet.add(header.b); // add any offset listed in header b

        double calib = 1.;
        double calper = 1.;
        float[] data = sequence.getArray();

        return new CssSeismogram(wfid, sta, chan, data, samprate, timet, calib, calper);
    }

    /**
     * Cut the seismogram - this will change the b, e, npts header values and
     * the sequence.
     *
     * @param startcut the desired begin of the trace in seconds
     * @param endcut the desired end of the trace in seconds
     * @param type - identifies the time marker to cut relative to ('origin',
     * 'begin', 'reference', 'absolute')
     */
    public void cutTrace(float startcut, float endcut, String type) {
        TimeT reftime = header.getReferenceTime();
        if (reftime == null) {
            reftime = new TimeT(0.);
        }

        // by default start and end are in absolute time
        TimeT start = new TimeT(startcut);// the start of the cut in absolute time
        TimeT end = new TimeT(endcut);   // the end of the cut in absolute time

        // If the cut is a relative cut - change start and end appropriately
        if (type.equals("origin")) // cut the trace relative to the origin time
        {
            if (SACHeader.isDefault(header.o)) {
                //System.out.println("origin time not defined in SAC file");
                return;
            } else {
                double origin = header.o;
                start = reftime.add(startcut + origin);
                end = reftime.add(endcut + origin);
            }
        } else {
            if (type.equals("begin")) {
                if (SACHeader.isDefault(header.b)) {
                    //System.out.println("begin time not defined in SAC file");
                    return;
                } else {
                    double begin = header.b;
                    start = reftime.add(startcut + begin);
                    end = reftime.add(endcut + begin);
                }
            } else {
                if (type.equals("reference")) // cut the trace relative to the reference time (nzdate)
                {
                    if (SACHeader.isDefault(header.nzyear)) {
                        //System.out.println("reference time not defined in SAC file.");
                        return;
                    } else {
                        start = reftime.add(startcut);
                        end = reftime.add(endcut);
                    }
                }
            }
        }

        //Now cut the trace and modify all the headers
        CssSeismogram seis = this.createSeismogram();
        seis.cut(start, end);
        this.setSequence(seis);
    }
    private File file;
    private SACHeader header;
    private Sequence sequence;
}
