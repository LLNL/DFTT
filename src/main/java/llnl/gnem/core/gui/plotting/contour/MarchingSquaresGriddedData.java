/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.plotting.contour;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author dyer1
 */
public class MarchingSquaresGriddedData {

    ArrayList<Double> xVals = new ArrayList<Double>();
    ArrayList<Double> yVals = new ArrayList<Double>();
    ArrayList<Double> zVals = new ArrayList<Double>();

    ArrayList<Double> xGridVals = new ArrayList<Double>();
    ArrayList<Double> yGridVals = new ArrayList<Double>();

    /**
     * =========================================================================
     * Class constructor.
     *
     * @param xVals - ArrayList of x values for each point
     * @param yVals - ArrayList of y values for each point
     * @param zVals - ArrayList of measured or predicted values for each point
     */
    public MarchingSquaresGriddedData(ArrayList<Double> xVals,
                ArrayList<Double> yVals,
                ArrayList<Double> zVals) {


        if ((xVals.size() != yVals.size()) || (xVals.size() != zVals.size())) {
            System.err.println("Gridded data size mismatch");
            System.exit(1);
        }
        this.xVals = xVals;
        this.yVals = yVals;
        this.zVals = zVals;

        xGridVals = getGridVals(xVals);
        yGridVals = getGridVals(yVals);

    }


    /**
     * =========================================================================
     * Return an ArrayList of the grid values
     *
     * @ param vals - Double ArrayList of x or y values
     * @return grid - ArrayList of the grid values
     */
    private ArrayList<Double> getGridVals(ArrayList<Double> vals) {
        TreeMap<Double, Integer> tm = new TreeMap<Double, Integer>();
        for (Double val : vals) {
            tm.put(val, 1);
        }
        Set<Double> keys = tm.keySet();
        return new ArrayList<Double>(keys);
    }


    /**
     * =========================================================================
     * Return an ArrayList of the xgrid values
     *
     * @return xgrid - ArrayList of the xgrid values
     */
    public ArrayList<Double> getGridValsX() {
        return xGridVals;
    }


    /**
     * =========================================================================
     * Return an ArrayList of the ygrid values
     *
     * @return ygrid - ArrayList of the ygrid values
     */
    public ArrayList<Double> getGridValsY() {
        return yGridVals;
    }


    /**
     * =========================================================================
     * Return the number of items in the x grid.
     *
     * @return nx - Length/size the xgrid.
     */
    public int getLenXGrid() {
        return xGridVals.size();
    }


    /**
     * =========================================================================
     * Return the number of items in the y grid.
     *
     * @return ny - Length/size the ygrid.
     */
    public int getLenYGrid() {
        return yGridVals.size();
    }


    /**
     * =========================================================================
     * Return the 2D z values as a 2D array, with values starting in the
     * LOWER LEFT corner of the 2D array.
     *
     * @return zValues - 2D array of zvalues[nx][ny], starting in the upper left
     */
    public double[][] getGriddedData() {
        int nx = xGridVals.size();
        int ny = yGridVals.size();

        double[][] zValues = new double[nx][ny];

        for (int ix = 0; ix < nx; ix++) {
            for (int iy = 0; iy < ny; iy++) {
                zValues[ix][iy] = 0.0;
            }
        }

        for (int i = 0; i < xVals.size(); i++) {
            double x = xVals.get(i);
            double y = yVals.get(i);
            double v = zVals.get(i);

            int ix = xGridVals.indexOf(x);
            if (ix == -1) {
                System.err.println("Unknown x grid value: " + x);
                System.exit(1);
            }
            int iy = yGridVals.indexOf(y);
            if (iy == -1) {
                System.err.println("Unknown y grid value: " + y);
                System.exit(1);
            }
            zValues[ix][iy] = v;
        }

        return zValues;
    }

}
