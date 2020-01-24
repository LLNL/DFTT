/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.core.dataObjects;

import java.util.ArrayList;

/**
 *
 * @author dodge
 */
public class CancellationData {

    private final int offset;
    private final ArrayList<float[]> corrections;
    private final int length;

    public CancellationData(int offset, ArrayList<float[]> corrections) {
       this.offset = offset;
        this.corrections = new ArrayList<>(corrections);
        int thisLength = -1;
        for (int j = 0; j < corrections.size(); ++j) {
            int aLength = corrections.get(j).length;
            if (thisLength < 0) {
                thisLength = aLength;
            } else if (thisLength != aLength) {
                throw new IllegalStateException("Not all correction arrays have the same length!");
            }
        }
        length = thisLength;
    }


    /**
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * @return the corrections
     */
    public ArrayList<float[]> getCorrections() {
        return new ArrayList<>(corrections);
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }
}
