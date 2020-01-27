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
package llnl.gnem.apps.detection.util.initialization;


import java.util.ArrayList;
import llnl.gnem.core.util.StreamKey;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author dodge1
 */
@ThreadSafe
public class BeamParams {

    public static final int DEGREES_PER_RADIAN = 180;
    private final double baz;
    private final double velocity;
    private final ArrayList<StreamKey> channels;

    BeamParams(ArrayList<String> beamLines) {
        if (beamLines.size() < 3) {
            throw new IllegalStateException("Not enough elements specified!");
        }
        channels = new ArrayList<>();
        String line = beamLines.get(0);
        String[] stearingVecStrings = line.trim().split("\\s+");
        if (stearingVecStrings.length != 2) {
            throw new IllegalStateException("Beam steering vector must have BAZ and velocity specified!");
        }
        baz = Double.parseDouble(stearingVecStrings[0]);
        velocity = Double.parseDouble(stearingVecStrings[1]);
        for (int j = 1; j < beamLines.size(); ++j) {
            line = beamLines.get(j);
            String[] stachanString = line.split("\\s+");
            if (stachanString.length == 2) {
                channels.add(new StreamKey(stachanString[0], stachanString[1]));
            }
        }
    }

    public BeamParams(BeamInfo bi, ArrayList<StreamKey> channels) {
        double sx = bi.getNorthSlowness();
        double sy = bi.getEastSlowness();
        double theta = Math.atan2(sy, sx);
        double tmp = theta * DEGREES_PER_RADIAN / Math.PI;
        baz = tmp < 0 ? 360 + tmp : tmp;
        velocity = 1.0 / (Math.sqrt(sx * sx + sy * sy));
        this.channels = new ArrayList<>(channels);
    }

    @Override
    public String toString() {
        return "BeamParams{" + "baz=" + baz + ", velocity=" + velocity + " with " + channels.size() + " channels" + '}';
    }

    public double getBaz() {
        return baz;
    }

    public double getVelocity() {
        return velocity;
    }

    public ArrayList<StreamKey> getChannels() {
        return new ArrayList<>(channels);
    }

    boolean elementsMatch(BeamParams other) {
        if (this.channels.size() != other.channels.size()) {
            return false;
        }
        for (StreamKey sc : channels) {
            if (!other.channels.contains(sc)) {
                return false;
            }
        }

        return true;
    }

    public Vector3D getSlownessVector() {
        BeamInfo bi = new BeamInfo(this);
        return bi.getSlownessVector();
    }
}
