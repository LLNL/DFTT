package llnl.gnem.core.waveform.merge;

import llnl.gnem.core.util.ApplicationLogger;

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
