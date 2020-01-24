/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
