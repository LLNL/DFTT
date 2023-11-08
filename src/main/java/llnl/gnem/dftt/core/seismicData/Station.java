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
package llnl.gnem.dftt.core.seismicData;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import llnl.gnem.dftt.core.geom.CartesianCoordinate;
import llnl.gnem.dftt.core.geom.Coordinate;
import llnl.gnem.dftt.core.geom.GeographicCoordinate;
import llnl.gnem.dftt.core.geom.Location;

/**
 *
 * @author addair1
 * @param <T>
 */
public class Station<T extends Coordinate> extends Location<T> {
    public Station(T coord) {
        this(coord, "-");
    }
    
    @JsonCreator
    public Station(@JsonProperty("coordinate") T coord, @JsonProperty("name") String name) {
        super(coord, name);
    }

    @JsonIgnore
    public String getSta() {
        return getName();
    }

    @JsonIgnore
    public double getElev() {
        return getCoordinate().getElevation();
    }
    
    public static Station<GeographicCoordinate> fromGeo(double lat, double lon) {
        return fromGeo(lat, lon, 0.0);
    }
    
    public static Station<GeographicCoordinate> fromGeo(double lat, double lon, double elev) {
        return fromGeo(lat, lon, elev, "-");
    }
    
    public static Station<GeographicCoordinate> fromGeo(double lat, double lon, Double elev, String name) {
        return new Station<>(new GeographicCoordinate(lat, lon, elev), name);
    }
    
    public static Station<CartesianCoordinate> fromCartesian(double x, double y) {
        return fromCartesian(x, y, 0.0);
    }
    
    public static Station<CartesianCoordinate> fromCartesian(double x, double y, double elev) {
        return fromCartesian(x, y, elev, "-");
    }
    
    public static Station<CartesianCoordinate> fromCartesian(double x, double y, double elev, String name) {
        return new Station<>(new CartesianCoordinate(x, y, elev), name);
    }
}
