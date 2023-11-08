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
package llnl.gnem.dftt.core.waveform.qc;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.dftt.core.util.Epoch;
import llnl.gnem.dftt.core.util.SeriesMath;
import llnl.gnem.dftt.core.waveform.seismogram.BasicSeismogram;

/**
 *
 * @author dodge1
 */
public class DropoutDetector {

    private final int minDropoutLengthSamples;

    public DropoutDetector() {
        minDropoutLengthSamples = 10;
    }

    public DropoutDetector(int minDropoutLengthSamples) {
        this.minDropoutLengthSamples = minDropoutLengthSamples;
    }

    public Collection<DropOut> scanForDropouts(BasicSeismogram seis) {
        Collection<DropOut> result = new ArrayList<>();
        float[] values = seis.getData();
        double delta = seis.getDelta();
        double startTime = seis.getTimeAsDouble();
        int i = 1;
        boolean inside = false;
        int startIndex = -1;
        int endIndex;
        int currentLength = 0;
        while (i < values.length) {
            if (values[i] == values[i - 1]) {
                if (inside) {
                    currentLength++;
                } else {
                    inside = true;
                    currentLength = 1;
                    startIndex = i - 1;
                }
            } else {
                endIndex = i - 1;
                float dropoutValue = values[endIndex];
                if (inside) {
                    if (currentLength > minDropoutLengthSamples) {
                        double dropoutBegin = startIndex * delta;
                        double dropoutEnd = endIndex * delta;
                        Epoch epoch = new Epoch(startTime + dropoutBegin, startTime + dropoutEnd);
                        result.add(new DropOut(epoch, dropoutValue, dropoutBegin, dropoutEnd, startTime));
                    }
                    currentLength = 0;
                    inside = false;
                }
            }
            ++i;
        }
        if (inside) {
            float dropoutValue = values[values.length - 1];
            if (currentLength > minDropoutLengthSamples) {
                double dropoutBegin = startIndex * delta;
                double dropoutEnd = (values.length - 1) * delta;
                Epoch epoch = new Epoch(startTime + dropoutBegin, startTime + dropoutEnd);
                result.add(new DropOut(epoch, dropoutValue, dropoutBegin, dropoutEnd, startTime));
            }
        }

        return result;
    }

}
