/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.plotting.contour;

/**
 *
 * @author dyer1
 */
/**
 * =========================================================================
 * Class MachingSquareIdx sotes an x index and a y index.
 */
public class MachingSquareIdx {
    private int XIndex;
    private int YIndex;

   /**
    * =========================================================================
    * Class constructor.
    *
    * @param XIndex - x index into a grid
    * @param YIndex - y index into a grid
    */
    public MachingSquareIdx(int XIndex, int YIndex) {
        this.XIndex = XIndex;
        this.YIndex = YIndex;

    }


   /**
    * =========================================================================
    * Return the x index into a grid
    *
    * @return XIndex - the x index into a grid
    */
    public int getXIndex() {
        return XIndex;
    }


   /**
    * =========================================================================
    * Return the y index into a grid
    *
    * @return YIndex - the y index into a grid
    */
    public int getYIndex() {
        return YIndex;
    }


   /**
    * =========================================================================
    * Return a String representation of the x and y indices.
    *
    * @return string - String representation of the x and y indices.
    */
    @Override
    public String toString() {
        return String.format("MachingSquareIdx: %d %d", XIndex, YIndex);
    }
}
