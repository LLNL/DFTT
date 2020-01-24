/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package llnl.gnem.core.gui.plotting.epochTimePlot;

import llnl.gnem.core.gui.plotting.HorizAlignment;
import llnl.gnem.core.gui.plotting.TickLabel;

/**
 *
 * @author dodge
 */
public class TickValue {
    private final double offset;
    private final TickLabel label;
    private final boolean major;
    private final HorizAlignment horizAlignment;
    private final TickTimeType type;

    public TickValue(double offset, TickLabel label, boolean isMajor, HorizAlignment alignment, TickTimeType type) {
        this.offset = offset;
        this.label = label;
        this.major = isMajor;
        this.horizAlignment = alignment;
        this.type = type;
    }

    /**
     * @return the offset
     */
    public double getOffset() {
        return offset;
    }

    /**
     * @return the label
     */
    public TickLabel getLabel() {
        return label;
    }

    /**
     * @return the isMajor
     */
    public boolean isMajor() {
        return major;
    }

    /**
     * @return the horizAlignment
     */
    public HorizAlignment getHorizAlignment() {
        return horizAlignment;
    }

    /**
     * @return the type
     */
    public TickTimeType getType() {
        return type;
    }
    
}
