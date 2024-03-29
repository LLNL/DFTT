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
package llnl.gnem.dftt.core.traveltime.Ak135;

import java.io.Serializable;
import llnl.gnem.dftt.core.traveltime.Point3D;
import llnl.gnem.dftt.core.traveltime.SinglePhaseTraveltimeCalculator;
import llnl.gnem.dftt.core.util.Geometry.EModel;

public class AirTraveltimeCalculator implements SinglePhaseTraveltimeCalculator, Serializable{

    private static final long serialVersionUID = 672075859936562861L;
    
    
    private final double speedMpS;
    public AirTraveltimeCalculator()
    {
        double temp = 0;
        speedMpS = calculateSpeed(temp);
    }
    
    public AirTraveltimeCalculator( double tempCelcius )
    {
      
        speedMpS = calculateSpeed(tempCelcius);
        System.out.println(speedMpS);
    }

    private double calculateSpeed(double tempCelcius) {
        return 331.3 + 0.606 * tempCelcius; // From http://en.wikipedia.org/wiki/Speed_of_sound
    }
    
    public double getTraveltimeDistMeters(double distMeters)
    {
        if( distMeters < 0 ){
            throw new IllegalArgumentException( "Distance cannot be negative!");
            
        }
        return distMeters / speedMpS;
    }
    
    public double getTraveltimeDistKm( double dist )
    {
        return getTraveltimeDistMeters( dist * 1000);
    }
    
    public double getTraveltimeDistDeg( double delta )
    {
        double dist = EModel.getKilometersPerDegree() * delta;
        return getTraveltimeDistKm(dist);
    }

    @Override
    public double getTT(Point3D evLoc, Point3D stLoc) {
        double delta = evLoc.dist_geog(stLoc);
        return getTraveltimeDistDeg(delta);
    }

    @Override
    public double getTT1D(double delta, double depth) {
        return getTraveltimeDistDeg(delta);
    }

    @Override
    public String getPhase() {
        return "A";
    }

    @Override
    public double get1DHslowness(double delta, double depth) {
        return 1000 / speedMpS; // slowness must be in s / km
    }
    
}
