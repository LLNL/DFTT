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
package llnl.gnem.core.gui.waveform;


import llnl.gnem.core.waveform.components.BaseSingleComponent;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.PickDataBridge;

/**
 *
 * @author dodge1
 */
public class PlotPickDataBridge extends PickDataBridge {

    private DisplayArrival arrival;
    private final BaseSingleComponent component;

    public PlotPickDataBridge(DisplayArrival arrival, BaseSingleComponent component) {
        super(arrival.getTime(), arrival.getDeltim(), 0);
        this.arrival = arrival;
        this.component = component;
    }

    @Override
    public String getInfoString() {
        return String.format("%s pick on %s by %s at %8.5f with deltim = %8.7f (Arid = %d)",
                arrival.getPhase(),
                component.getName(),
                arrival.getAuth(),
                arrival.getTime(),
                deltim,
                arrival.getArid());
    }
}
