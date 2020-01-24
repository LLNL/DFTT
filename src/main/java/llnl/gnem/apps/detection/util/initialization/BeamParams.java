/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
