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
package llnl.gnem.dftt.core.gui.plotting.epochTimePlot;

import llnl.gnem.dftt.core.gui.plotting.HorizAlignment;
import llnl.gnem.dftt.core.gui.plotting.TickLabel;

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
