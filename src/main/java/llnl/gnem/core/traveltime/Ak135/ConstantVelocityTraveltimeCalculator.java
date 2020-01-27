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
package llnl.gnem.core.traveltime.Ak135;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import llnl.gnem.core.traveltime.Point3D;
import llnl.gnem.core.traveltime.SinglePhaseTraveltimeCalculator;
import llnl.gnem.core.util.Geometry.EModel;

public class ConstantVelocityTraveltimeCalculator implements SinglePhaseTraveltimeCalculator, Serializable{

    static boolean supportsPhase(String phase) {
        Pattern pattern = Pattern.compile("([^a-zA-Z])+");
        Matcher matcher = pattern.matcher(phase);
        return matcher.find();
    }
    
    private final String phase;
    private final double velocity;
    public ConstantVelocityTraveltimeCalculator( String phase )
    {
        this.phase = phase;
        Pattern pattern = Pattern.compile("([^a-zA-Z])+");
        Matcher matcher = pattern.matcher(phase);
        if( matcher.find()){
            String number = matcher.group();
            velocity = Double.parseDouble(number);
        }
        else {
            throw new IllegalArgumentException( "Input phase string does not contain a number!");
        }
    }

    @Override
    public double getTT(Point3D evLoc, Point3D stLoc) {
        double delta = evLoc.dist_geog(stLoc);
        double dist = EModel.getKilometersPerDegree() * delta;
        return dist / velocity;
    }

    @Override
    public double getTT1D(double delta, double depth) {
        double dist = EModel.getKilometersPerDegree() * delta;
        return dist /velocity;
    }

    @Override
    public String getPhase() {
        return phase;
    }

    @Override
    public double get1DHslowness(double delta, double depth) {
        return 1/velocity;
    }
    
}
