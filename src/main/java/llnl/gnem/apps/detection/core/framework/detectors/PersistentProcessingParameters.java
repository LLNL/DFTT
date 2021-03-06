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
package llnl.gnem.apps.detection.core.framework.detectors;

import com.oregondsp.signalProcessing.filter.iir.PassbandType;
import java.io.Serializable;
import llnl.gnem.apps.detection.core.signalProcessing.IIRFilterType;

public class PersistentProcessingParameters implements Serializable {

    private static final double TOLERANCE = 1.0e-5;

    public IIRFilterType filterType;
    public PassbandType passbandType;
    public double f1;
    public double f2;
    public int npoles;
    public double eps;
    public double samplingRate;
    public int decrate;

    static final long serialVersionUID = 1311704418283952297L;

    public PersistentProcessingParameters(IIRFilterType filterType,
            PassbandType passbandType,
            double f1,
            double f2,
            int npoles,
            double eps,
            double samplingRate,
            int decrate) {

        this.filterType = filterType;
        this.passbandType = passbandType;
        this.f1 = f1;
        this.f2 = f2;
        this.npoles = npoles;
        this.eps = eps;
        this.samplingRate = samplingRate;
        this.decrate = decrate;
    }

    public PersistentProcessingParameters(PersistentProcessingParameters other) {
        filterType = other.filterType;
        passbandType = other.passbandType;
        f1 = other.f1;
        f2 = other.f2;
        npoles = other.npoles;
        eps = other.eps;
        samplingRate = other.samplingRate;
        decrate = other.decrate;
    }

    public double getDecimatedSampleRate() {
        return samplingRate / decrate;
    }

    public boolean consistentWith(PersistentProcessingParameters P) {

        boolean retval = true;

        if (filterType != P.filterType) {
            System.out.println("filter type mismatch!");
            retval = false;
        }
        if (passbandType != P.passbandType) {
            System.out.println("passband mismatch!");
            retval = false;
        }
        if (Math.abs(f1 - P.f1) / (f1 + P.f1) > TOLERANCE) {
            System.out.println("low pass mismatch!");
            retval = false;
        }
        if (Math.abs(f2 - P.f2) / (f2 + P.f2) > TOLERANCE) {
            System.out.println("high pass mismatch!");
            retval = false;
        }
        if (npoles - P.npoles != 0) {
            System.out.println("NPOLES mismatch!");
            retval = false;
        }
//        if (Math.abs(eps - P.eps) / (eps + P.eps) > TOLERANCE) {
//            System.out.println("EPS mismatch!");
//            retval = false;
//        }
        if (Math.abs(samplingRate - P.samplingRate) / (samplingRate + P.samplingRate) > TOLERANCE) {
            System.out.println("sampling rate mismatch!");
            retval = false;
        }
        if (decrate - P.decrate != 0) {
            System.out.println("decrate mismatch!");
            retval = false;
        }

        return retval;
    }

}
