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
package llnl.gnem.apps.detection.cancellation.io;

import com.oregondsp.io.SACInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import com.oregondsp.signalProcessing.Sequence;
import com.oregondsp.signalProcessing.filter.iir.Butterworth;
import com.oregondsp.signalProcessing.filter.iir.IIRFilter;
import com.oregondsp.signalProcessing.filter.iir.PassbandType;

import com.oregondsp.util.DirectoryListing;
import com.oregondsp.util.TimeStamp;
import llnl.gnem.apps.detection.cancellation.CancellationParameters;

public class EventDataStructure {

    public int nch;
    public double dt;
    public int npts;

    public TimeStamp startTime;

    public float[][] waveforms;
    public ChannelID[] chanids;

    private final int[] window;

    public EventDataStructure(String eventDirectoryPath, String filePattern) throws IOException {

        DirectoryListing D = new DirectoryListing(eventDirectoryPath, filePattern);

        double Ts = 0.0;
        double Te = 0.0;

        npts = 0;
        nch = 0;

        for (int i = 0; i < D.nFiles(); i++) {

            try (SACInputStream stream = new SACInputStream(eventDirectoryPath + File.separator + D.file(i))) {

                if (i == 0) {
                    Ts = stream.header.getStartTime().epochAsDouble();
                    Ts += stream.header.b;
                    dt = stream.header.delta;
                    Te = Ts + (stream.header.npts - 1) * dt;
                } else {
                    double T = stream.header.getStartTime().epochAsDouble();
                    T += stream.header.b;
                    Ts = Math.max(T, Ts);
                    T += (stream.header.npts - 1) * stream.header.delta;
                    Te = Math.min(T, Te);
                    if ((dt - stream.header.delta) / (dt + stream.header.delta) > 1.0e-5) {
                        stream.close();
                        throw new IllegalStateException("Sampling interval mismatch:  " + dt + "   " + stream.header.delta);
                    }
                }

                nch++;
            }
        }

        npts = (int) Math.round((Te - Ts) / dt) + 1;

        waveforms = new float[nch][npts];
        chanids = new ChannelID[nch];

        for (int ich = 0; ich < nch; ich++) {
            try (SACInputStream stream = new SACInputStream(eventDirectoryPath + File.separator + D.file(ich))) {

                double T = stream.header.getStartTime().epochAsDouble();
                T += stream.header.b;
                int ndiscard = (int) Math.round((Ts - T) / dt);
                float[] tmp = new float[ndiscard];
                stream.readData(tmp);
                stream.readData(waveforms[ich]);
                Sequence.rmean(waveforms[ich]);
                chanids[ich] = new ChannelID(stream.header);
            }
        }

        startTime = new TimeStamp(Ts);

        window = new int[2];
        window[0] = 0;
        window[1] = npts - 1;
    }

    public EventDataStructure(String eventDirectoryPath, String filePattern, IIRFilter F, int decrate) throws IOException {

        DirectoryListing D = new DirectoryListing(eventDirectoryPath, filePattern);

        double Ts = 0.0;
        double Te = 0.0;

        npts = 0;
        nch = 0;

        for (int i = 0; i < D.nFiles(); i++) {

            try (SACInputStream stream = new SACInputStream(eventDirectoryPath + File.separator + D.file(i))) {

                if (i == 0) {
                    Ts = stream.header.getStartTime().epochAsDouble();
                    Ts += stream.header.b;
                    dt = stream.header.delta;
                    Te = Ts + (stream.header.npts - 1) * dt;
                } else {
                    double T = stream.header.getStartTime().epochAsDouble();
                    T += stream.header.b;
                    Ts = Math.max(T, Ts);
                    T += (stream.header.npts - 1) * stream.header.delta;
                    Te = Math.min(T, Te);
                    if ((dt - stream.header.delta) / (dt + stream.header.delta) > 1.0e-5) {
                        throw new IllegalStateException("Sampling interval mismatch:  " + dt + "   " + stream.header.delta);
                    }
                }

                nch++;
            }
        }

        int n = (int) Math.round((Te - Ts) / dt) + 1;
        npts = n / decrate;
        n = npts * decrate;

        float[] x = new float[n];

        waveforms = new float[nch][npts];
        chanids = new ChannelID[nch];

        for (int ich = 0; ich < nch; ich++) {
            try (SACInputStream stream = new SACInputStream(eventDirectoryPath + File.separator + D.file(ich))) {

                double T = stream.header.getStartTime().epochAsDouble();
                T += stream.header.b;
                int ndiscard = (int) Math.round((Ts - T) / dt);
                float[] tmp = new float[ndiscard];
                stream.readData(tmp);
                stream.readData(x);
                Sequence.rmean(x);
                F.initialize();
                F.filter(x);
                Sequence.decimate(x, decrate, waveforms[ich]);
                chanids[ich] = new ChannelID(stream.header);
            }
        }

        startTime = new TimeStamp(Ts);

        window = new int[2];
        window[0] = 0;
        window[1] = npts - 1;
    }

    public EventDataStructure(ChannelID[] requestedIDs, String eventDirectoryPath, String filePattern, CancellationParameters parameters, double streamDelta) throws IOException {

        nch = requestedIDs.length;

        HashMap< String, Integer> mapping = new HashMap<>();
        for (int ich = 0; ich < nch; ich++) {
            mapping.put(requestedIDs[ich].toString(), ich);
        }

        DirectoryListing D = new DirectoryListing(eventDirectoryPath, filePattern);

        double Ts = 0.0;
        double Te = 0.0;

        npts = 0;

        for (int i = 0; i < D.nFiles(); i++) {
            try (SACInputStream stream = new SACInputStream(eventDirectoryPath + File.separator + D.file(i))) {

                if (i == 0) {
                    Ts = stream.header.getStartTime().epochAsDouble();
                    Ts += stream.header.b;
                    dt = stream.header.delta;
                    Te = Ts + (stream.header.npts - 1) * dt;
                } else {
                    double T = stream.header.getStartTime().epochAsDouble();
                    T += stream.header.b;
                    Ts = Math.max(T, Ts);
                    T += (stream.header.npts - 1) * stream.header.delta;
                    Te = Math.min(T, Te);
                    if ((dt - stream.header.delta) / (dt + stream.header.delta) > 1.0e-5) {
                        throw new IllegalStateException("Sampling interval mismatch:  " + dt + "   " + stream.header.delta);
                    }
                }
            }
        }

        int decrate = (int) Math.round(streamDelta/dt);
        IIRFilter prefilter = new Butterworth(5, PassbandType.BANDPASS, parameters.getFlow(), parameters.getFhigh(), dt);

        int n = (int) Math.round((Te - Ts) / dt) + 1;
        npts = n / decrate;
        n = npts * decrate;

        float[] x = new float[n];

        waveforms = new float[nch][npts];
        chanids = new ChannelID[nch];

        for (int isch = 0; isch < nch; isch++) {

            try (SACInputStream stream = new SACInputStream(eventDirectoryPath + File.separator + D.file(isch))) {
                String key = stream.header.kstnm.trim() + "." + stream.header.kcmpnm.trim();
                int ich = -1;
                if (mapping.get(key) != null) {
                    ich = mapping.get(key);
                } else {
                    stream.close();
                    throw new IllegalStateException("Channel id not found:  " + key);
                }

                double T = stream.header.getStartTime().epochAsDouble();
                T += stream.header.b;
                int ndiscard = (int) Math.round((Ts - T) / dt);
                float[] tmp = new float[ndiscard];
                stream.readData(tmp);
                stream.readData(x);
                Sequence.rmean(x);
                prefilter.initialize();
                prefilter.filter(x);
                Sequence.decimate(x, decrate, waveforms[ich]);
                chanids[ich] = new ChannelID(stream.header);
            }
        }

        startTime = new TimeStamp(Ts);

        window = new int[2];
        window[0] = 0;
        window[1] = npts - 1;
    }

    public int[] getWindow() {
        return window;
    }

    public void setWindow(int[] w) {
        window[0] = Math.max(w[0], window[0]);
        window[1] = Math.min(w[1], window[1]);
    }

    public int getNumChannels() {
        return nch;
    }

    public ChannelID[] getChannelIDs() {
        return chanids;
    }

    public float[] getWaveform(ChannelID chanid) {

        float[] retval = null;

        for (int ich = 0; ich < chanids.length; ich++) {
            if (chanid.equals(chanids[ich])) {
                retval = waveforms[ich];
                break;
            }
        }

        return retval;
    }

    public float[][] getWaveforms() {
        return waveforms;
    }

    public void print(PrintStream ps) {
        ps.println(startTime);
        ps.println("  nch:   " + nch);
        ps.println("  npts:  " + npts);
        ps.println("  dt:    " + dt);
        ps.println();
        for (int i = 0; i < nch; i++) {
            ps.println("  " + chanids[i]);
        }
    }

    public float[] getChannelSequentialForm() {

        int n = window[1] - window[0] + 1;

        float[] retval = new float[n * nch];
        int ptr = 0;
        for (int i = window[0]; i <= window[1]; i++) {
            for (int ich = 0; ich < nch; ich++) {
                retval[ptr] = waveforms[ich][i];
                ptr++;
            }
        }

        return retval;
    }

    public float[] getTraceSequentialForm() {

        int n = window[1] - window[0] + 1;

        float[] retval = new float[n * nch];
        int ptr = 0;
        for (int ich = 0; ich < nch; ich++) {
            for (int i = window[0]; i <= window[1]; i++) {
                retval[ptr] = waveforms[ich][i];
                ptr++;
            }
        }

        return retval;
    }

}
