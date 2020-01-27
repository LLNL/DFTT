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
package llnl.gnem.core.geom;

import net.jcip.annotations.Immutable;

/**
 *
 * @author addair1
 * @param <T>
 */
@Immutable
public class Location<T extends Coordinate> implements Comparable<Location> {
    private final T coord;
    private final String name;
    
    public Location(T coord) {
        this(coord, coord.toString());
    }
    
    public Location(T coord, String name) {
        this.coord = coord;
        this.name = name;
    }
    
    public T getCoordinate() {
        return coord;
    }
    
    public String getName() {
        return name;
    }
    
    public double getDistance(Location<T> other) {
        return coord.getDistance(other.coord);
    }

    @Override
    public int compareTo(Location other) {
        return name.compareTo(other.name);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Location other = (Location) obj;
        if (this.coord != other.coord && (this.coord == null || !this.coord.equals(other.coord))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + (this.coord != null ? this.coord.hashCode() : 0);
        hash = 73 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}
