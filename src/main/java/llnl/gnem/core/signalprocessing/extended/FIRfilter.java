/*
 * Created on Jan 7, 2005
 *
 */
package llnl.gnem.core.signalprocessing.extended;

/**
 * @author Paik6
 */
public interface FIRfilter {
    /**
     * Re-initializes this FIRfilter.
     */
    public abstract void init();

    /**
     * Used by master ComplexFIRfilters to filter the multiplexed data from the
     * preprocessing. The main difference from this filter function and the
     * other filter function is the calculation of the dataDFT. The filter function
     * requires another ComplexSequence to hold the new values.
     *
     * @param returnSeq   - the ComplexSequence to hold the new filtered values
     * @param dataSegment - the ComplexSequence representing the multiplexed data
     */
    public abstract void filter(ComplexSequence returnSeq, ComplexSequence dataSegment);

    /**
     * Used by slave ComplexFIRfilters to filter the multiplexed data from the
     * preprocessing. This function does not take in multiplexed data: rather,
     * it copies the dataDFT from its master.
     *
     * @param returnSeq - the ComplexSequence to hold the new filtered values
     */
    public abstract void filter(ComplexSequence returnSeq);
}