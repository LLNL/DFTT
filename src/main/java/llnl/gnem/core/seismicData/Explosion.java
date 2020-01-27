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
package llnl.gnem.core.seismicData;

import llnl.gnem.core.geom.CartesianCoordinate;
import llnl.gnem.core.geom.Coordinate;
import llnl.gnem.core.geom.GeographicCoordinate;
import net.jcip.annotations.Immutable;

/**
 *
 * @author addair1
 */
@Immutable
public class Explosion<T extends Coordinate> extends Event<T> {
    public Explosion(T coord, double yield) {
        super(coord, Energy.fromYield(yield));
    }
    
    public double getHOB() {
        return getCoordinate().getElevation();
    }
    
    public double getYield() {
        return getEnergy().getYield();
    }
    
    public static Explosion<GeographicCoordinate> fromGeo(double lat, double lon, double hob, double yield) {
        return new Explosion<GeographicCoordinate>(new GeographicCoordinate(lat, lon, hob), yield);
    }
    
    public static Explosion<CartesianCoordinate> fromCartesian(double lat, double lon, double hob, double yield) {
        return new Explosion<CartesianCoordinate>(new CartesianCoordinate(lat, lon, hob), yield);
    }
}
