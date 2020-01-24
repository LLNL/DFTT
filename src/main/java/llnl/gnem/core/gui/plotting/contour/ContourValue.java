/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.plotting.contour;

/**
 *
 * @author dyer1
 */
public class ContourValue {

    private double z;
    private String label;

    public ContourValue(double z, String label) {

        this.z = z;
        this.label = label;

    }

     public String getLabel() {
        return label;
    }

    public double getZ() {
        return z;
    }

}
