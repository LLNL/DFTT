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
package llnl.gnem.dftt.core.waveform.merge;

import llnl.gnem.dftt.core.util.ApplicationLogger;

import java.util.logging.Level;

/**
 * Created by dodge1 Date: Dec 8, 2009 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class WaveformMerger {

    public static IntWaveform mergeWaveforms(IntWaveform source,
            IntWaveform target,
            boolean replaceOnMergeError,
            boolean ignoreMismatchedSamples) throws MergeException {
        if (source.rateIsComparable(target)) {
            try {
                return target.union(source, ignoreMismatchedSamples);
            } catch (MergeException e) {
                if (replaceOnMergeError) {
                    if (source.getNpts() >= target.getNpts()) {
                        ApplicationLogger.getInstance().log(Level.WARNING, String.format("Merge failed for (%s) to (%s) Replacing target with source.",
                                source.toString(), target.toString()));
                        return new IntWaveform(target.getWfid(), source.getStart(), source.getRate(), source.getData());
                    } else {
                        return target;
                    }
                } else {
                    throw e;
                }
            }
        } else {
            ApplicationLogger.getInstance().log(Level.WARNING, String.format("Waveforms have incompatible sample rates! (%s), (%s) Replacing target with source.",
                    source.toString(), target.toString()));
            return new IntWaveform(target.getWfid(), source.getStart(), source.getRate(), source.getData());
        }
    }
}
