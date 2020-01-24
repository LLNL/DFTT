/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.waveform.qc;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.SeriesMath;
import llnl.gnem.core.waveform.seismogram.BasicSeismogram;

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
