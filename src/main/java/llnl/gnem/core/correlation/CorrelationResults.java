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
package llnl.gnem.core.correlation;

import java.io.Serializable;
import java.util.List;

import org.ojalgo.matrix.Primitive32Matrix;

import com.google.common.collect.ImmutableList;

/**
 * Created by dodge1 Date: Apr 2, 2009 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class CorrelationResults implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Primitive32Matrix shifts;
    private final Primitive32Matrix correlations;
    private final List<StationEventChannelData> data;

    public CorrelationResults(Primitive32Matrix shifts, Primitive32Matrix correlations, List<StationEventChannelData> data) {
        this.shifts = shifts;
        this.correlations = correlations;
        this.data = ImmutableList.copyOf(data);
    }

    public Primitive32Matrix getShifts() {
        return shifts;
    }

    public Primitive32Matrix getCorrelations() {
        return correlations;
    }

    public List<StationEventChannelData> getData() {
        return data;
    }

    public int size() {
        return shifts.getColDim();
    }
}
