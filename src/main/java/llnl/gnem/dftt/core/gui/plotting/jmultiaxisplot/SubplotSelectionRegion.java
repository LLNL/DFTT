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
package llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot;

import llnl.gnem.dftt.core.gui.plotting.ZoomLimits;

import java.util.*;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * Class containing a JSubplot and a subregion within the JSubplot. Used for passing
 * selection messages from the JMultiAxisPlot MouseListener to interested observers.
 *
 * @author Doug Dodge
 */
public class SubplotSelectionRegion {
    /**
     * Constructor for the SubplotSelectionRegion object
     *
     * @param p      The JSubplot
     * @param region The selected region within this subplot
     */
    public SubplotSelectionRegion( JSubplot p, ZoomLimits region )
    {
        this.p = p;
        this.region = region;
    }

    /**
     * Gets the JSubplot
     *
     * @return The subplot value
     */
    public JSubplot getSubplot()
    {
        return p;
    }

    /**
     * Gets the selectedRegion for the contained JSubplot
     *
     * @return The selected Region
     */
    public ZoomLimits getSelectedRegion()
    {
        return region;
    }

    private JSubplot p;
    private ZoomLimits region;
}


