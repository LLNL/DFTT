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
package llnl.gnem.core.gui.map.internal.clustering;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.gui.map.ViewPort;
import llnl.gnem.core.gui.map.internal.Measurable;

/**
 *
 * @author dodge1
 */
public class Cluster<T extends Measurable> implements Measurable {

    private final Collection<T> items;

    public Cluster(T item) {
        items = new ArrayList<T>();
        items.add(item);
    }

    public void addItem(T item) {
        items.add(item);
    }

    @Override
    public double distanceFrom(Measurable other) {
        double avg = 0;
        for (Measurable item : items) {
            avg += item.distanceFrom(other);
        }
        return avg / items.size();
    }

    @Override
    public boolean isInside(ViewPort viewport) {
        return true;
    }

    @Override
    public boolean intersects(ViewPort viewport) {
        return true;
    }

    public T getRepresentative() {
        return items.iterator().next();
    }

    @Override
    public String toString()
    {
        return String.format( "Cluster with %d members.", items.size());
    }
}
