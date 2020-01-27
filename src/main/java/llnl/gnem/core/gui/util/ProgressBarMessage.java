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
package llnl.gnem.core.gui.util;

import net.jcip.annotations.ThreadSafe;

/*
 *  COPYRIGHT NOTICE
 *  RBAP Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * A class that can be passed from an Observable to a StatusBar Observer to update
 * the state of a Progress Bar managed by the Status bar.
 *
 * @author Doug Dodge
 */
@ThreadSafe
public class ProgressBarMessage {
    /**
     * Constructor for the ProgressBarMessage object
     *
     * @param current       Current value of the process whose progress is being displayed
     *                      by the progress bar.
     * @param max           The maximum value of the process whose progress is being
     *                      displayed by the progress bar.
     * @param show          Controls whether the progress bar is visible or not.
     * @param indeterminate Controls whether the progress bar is in indeterminate mode
     *                      or not.
     */
    public ProgressBarMessage( int current, int max, boolean show, boolean indeterminate )
    {
        this.current = current;
        this.max = max > 0 ? max : 1;
        this.show = show;
        this.indeterminate = indeterminate;
    }

    /**
     * Gets the current attribute of the ProgressBarMessage object
     *
     * @return The current value
     */
    public int getCurrent()
    {
        return current;
    }

    /**
     * Gets the max attribute of the ProgressBarMessage object
     *
     * @return The max value
     */
    public int getMax()
    {
        return max;
    }

    /**
     * Gets the show attribute of the ProgressBarMessage object
     *
     * @return The show value
     */
    public boolean getShow()
    {
        return show;
    }

    /**
     * Gets the indeterminate attribute of the ProgressBarMessage object
     *
     * @return The indeterminate value
     */
    public boolean getIndeterminate()
    {
        return indeterminate;
    }

    private final int current;
    private final int max;
    private final boolean show;
    private final boolean indeterminate;
}

