/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.plotting.contour;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import llnl.gnem.core.gui.plotting.PenStyle;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JMultiAxisPlot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.core.gui.plotting.plotobject.Circle;

//----------------------------------------------------------
public class Contours {

    // Below, constant data members:
    // private final static boolean SHOW_NUMBERS = true;
    private double zMaxMin = 1.0E+10;
    //private double zMinMin = -1.0E+10;
    // Below, data members which store the grid steps,
    // the z values, the interpolation flag, the dimensions
    // of the contour plot and the increments in the grid:
    private int xSteps, ySteps;
    private double z[][];
    // boolean logInterpolation = false; // not implemented
    private double xmin, ymin, deltaX, deltaY;
    private List<Contour> contours;
    // Below, data members, most of which are adapted from
    // Fortran variables in Snyder's code:
    private int ncv;
    private double contourLevels[] = new double[2];
    private String contourLabels[] = new String[2];
    private double contourStep = 0.0;
    private int l1[] = new int[4];
    private int l2[] = new int[4];
    private int ij[] = new int[2];
    private int i1[] = new int[2];
    private int i2[] = new int[2];
    private int i3[] = new int[6];
    private int ibkey, icur, jcur, ii, jj, elle, ix, iedge, iflag, ni, ks;
    private int cntrIndex, prevIndex;
    private int idir, nxidir, k;
    private double z1, z2, cval, zMax, zMin;
    private double intersect[] = new double[4];
    private double xy[] = new double[2];
    private double prevXY[] = new double[2];
    private boolean jump;

    // -------------------------------------------------------
    // The constructor methods.
    // -------------------------------------------------------

    // Contour step given. Contour labels set to contour levels.
    public Contours(double[][] z, int nx, int ny, double xmin, double ymin,
            double xstep, double ystep, double contourStep, double zMaxMin) {

        setXYZ(z, nx, ny, xmin, ymin, xstep, ystep, zMaxMin);

        this.contourStep = contourStep;

    }

    // Contour levels given. Contour labels set to contour levels.
    public Contours(double[][] z, int nx, int ny, double xmin, double ymin,
            double xstep, double ystep, double[] contourLevels, double zMaxMin) {

        setXYZ(z, nx, ny, xmin, ymin, xstep, ystep, zMaxMin);

        this.contourLevels = contourLevels;
        ncv = contourLevels.length;

        contourLabels = new String[ncv];
        for (int i = 0; i < ncv; i++) {
            contourLabels[i] = Double.toString(contourLevels[i]);
        }


    }


    // Contour levels and contour labels given.
    public Contours(double[][] z, int nx, int ny, double xmin, double ymin,
            double xstep, double ystep, double[] contourLevels, String[] contourLabels, double zMaxMin) {

        setXYZ(z, nx, ny, xmin, ymin, xstep, ystep, zMaxMin);

        this.contourLevels = contourLevels;
        ncv = contourLevels.length;

        this.contourLabels = contourLabels;
        if (contourLabels.length != ncv) {
            System.out.println("Error: The number of contour labels does not equal the number of contour levels.");
            invalidData();
        }

    }

    // -------------------------------------------------------
    // "AssignContourValues" interpolates between "zMin" and
    // "zMax", either logarithmically or linearly, in order
    // to assign contour values to the array "contourLevels".
    // -------------------------------------------------------
    private void assignContourValues() {
        if (contourStep <= 0d) {
            // Contour steps were assigned by caller
            if (contourLevels[0] != contourLevels[1]) { // Valid data
                return;
            }

            invalidData();
        }
        else {
            int i;
            getExtremes();
            /*
             * if ((logInterpolation) && (zMin <= 0.0)) { invalidData(); } if
             * (logInterpolation) { double temp = Math.log(zMin);
             *
             * delta = (Math.log(zMax) - temp) / ncv; for (i = 0; i < ncv; i++)
             * { contourLevels[i] = (double) Math.exp(temp + (i + 1) * delta); } } else {
             */
            double firstContour = Math.floor(zMin / contourStep) * contourStep + contourStep;
            ncv = (int) Math.floor((zMax - firstContour) / contourStep) + 1;
            if (ncv <= 1) {
                invalidData();
            } else {
                contourLevels = new double[ncv];
                contourLabels = new String[ncv];
                for (i = 0; i < ncv; i++) {
                    contourLevels[i] = firstContour + i * contourStep;
                    contourLabels[i] = Double.toString(contourLevels[i]);
                }

            }
        }
    }

    // -------------------------------------------------------
    // "continueContour" continues tracing a contour. Edges
    // are numbered clockwise, the bottom edge being # 1.
    // -------------------------------------------------------
    private void continueContour() {
        int local_k;

        ni = 1;
        if (iedge >= 3) {
            ij[0] = ij[0] - i3[iedge - 1];
            ij[1] = ij[1] - i3[iedge + 1];
        }
        for (local_k = 1; local_k < 5; local_k++) {
            if (local_k != iedge) {
                ii = ij[0] + i3[local_k - 1];
                jj = ij[1] + i3[local_k];
                z1 = z[ii - 1][jj - 1];
                ii = ij[0] + i3[local_k];
                jj = ij[1] + i3[local_k + 1];
                z2 = z[ii - 1][jj - 1];
                if ((cval > Math.min(z1, z2) && (cval <= Math.max(z1, z2)))) {
                    if ((local_k == 1) || (local_k == 4)) {
                        double zz = z2;

                        z2 = z1;
                        z1 = zz;
                    }
                    intersect[local_k - 1] = (cval - z1) / (z2 - z1);
                    ni++;
                    ks = local_k;
                }
            }
        }
        if (ni != 2) {
            // -------------------------------------------------
            // The contour crosses all 4 edges of cell being
            // examined. Choose lines top-to-left & bottom-to-
            // right if interpolation point on top edge is
            // less than interpolation point on bottom edge.
            // Otherwise, choose the other pair. This method
            // produces the same results if axes are reversed.
            // The contour may close at any edge, but must not
            // cross itself inside any cell.
            // -------------------------------------------------
            ks = 5 - iedge;
            if (intersect[2] >= intersect[0]) {
                ks = 3 - iedge;
                if (ks <= 0) {
                    ks = ks + 4;
                }
            }
        }
        // ----------------------------------------------------
        // Determine whether the contour will close or run
        // into a boundary at edge ks of the current cell.
        // ----------------------------------------------------
        elle = ks - 1;
        iflag = 1; // 1. Continue a contour
        jump = true;
        if (ks >= 3) {
            ij[0] = ij[0] + i3[ks - 1];
            ij[1] = ij[1] + i3[ks + 1];
            elle = ks - 3;
        }
    }

    // -------------------------------------------------------
    // "contourPlotKernel" is the guts of this class and
    // corresponds to Synder's subroutine "GCONTR".
    // -------------------------------------------------------
    private void contourPlotKernel(boolean workSpace[]) {
        int val_label;
        boolean bool_label;

        l1[0] = xSteps;
        l1[1] = ySteps;
        l1[2] = -1;
        l1[3] = -1;
        i1[0] = 1;
        i1[1] = 0;
        i2[0] = 1;
        i2[1] = -1;
        i3[0] = 1;
        i3[1] = 0;
        i3[2] = 0;
        i3[3] = 1;
        i3[4] = 1;
        i3[5] = 0;
        prevXY[0] = 0.0;
        prevXY[1] = 0.0;
        xy[0] = 1.0;
        xy[1] = 1.0;
        cntrIndex = 0;
        prevIndex = -1;
        iflag = 6;
        drawKernel();
        icur = Math.max(1, Math.min((int) Math.floor(xy[0]), xSteps));
        jcur = Math.max(1, Math.min((int) Math.floor(xy[1]), ySteps));
        ibkey = 0;
        ij[0] = icur;
        ij[1] = jcur;
        if (routineLabel020() && routineLabel150()) {
            return;
        }
        if (routineLabel050()) {
            return;
        }
        while (true) {
            detectBoundary();
            if (jump) {
                if (ix != 0) {
                    iflag = 4; // Finish contour at boundary
                }
                iedge = ks + 2;
                if (iedge > 4) {
                    iedge = iedge - 4;
                }
                intersect[iedge - 1] = intersect[ks - 1];
                val_label = routineLabel200(workSpace);
                if (val_label == 1) {
                    if (routineLabel020() && routineLabel150()) {
                        return;
                    }
                    if (routineLabel050()) {
                        return;
                    }
                    continue;
                }
                else if (val_label == 2) {
                    continue;
                }
                else {
                    return;
                }
            }
            if ((ix != 3) && (ix + ibkey != 0) && crossedByContour(workSpace)) {
                //
                // An acceptable line segment has been found.
                // Follow contour until it hits a
                // boundary or closes.
                //
                iedge = elle + 1;
                cval = contourLevels[cntrIndex];
                if (ix != 1) {
                    iedge = iedge + 2;
                }
                iflag = 2 + ibkey;
                intersect[iedge - 1] = (cval - z1) / (z2 - z1);
                val_label = routineLabel200(workSpace);
                if (val_label == 1) {
                    if (routineLabel020() && routineLabel150()) {
                        return;
                    }
                    if (routineLabel050()) {
                        return;
                    }
                    continue;
                }
                else if (val_label == 2) {
                    continue;
                }
                else {
                    return;
                }
            }
            if (++elle > 1) {
                elle = idir % 2;
                ij[elle] = sign(ij[elle], l1[k - 1]);
                bool_label = routineLabel150();
                if (bool_label) {
                    return;
                }
            }
            if (routineLabel050()) {
                return;
            }
        }
    }

    /*
     * attach the segment to the existing one or create a new segment
     *
     * iflag == 1 means Continue a contour iflag == 2 means Start a contour at a
     * boundary iflag == 3 means Start a contour not at a boundary iflag == 4
     * means Finish contour at a boundary iflag == 5 means Finish closed contour
     * not at boundary iflag == 6 means Set pen position
     */
    private void contourSegment(double prevU, double prevV, double u, double v,
            double z, String label) {
        int i = 0;
        for (i = 0; i < contours.size(); i++) {
            Contour c = contours.get(i);
            if (!c.isFinished()) {
                Point p = c.getLastPoint();
                if (p.getX() == prevU && p.getY() == prevV) {
                    c.addPoint(new Point(u, v));
                    if (iflag == 4) {
                        c.setFinished(true);
                    } else if (iflag == 5) {
                        c.setFinished(true);
                        c.setClosed(true);
                    }
                    break;
                }
            }
        }
        if (i == contours.size()) { // start a new contour
            Contour c = new Contour(z, label);
            c.addPoint(new Point(prevU, prevV));
            c.addPoint(new Point(u, v));

            contours.add(c);
        }
    }

    // -------------------------------------------------------
    // "crossedByContour" is true iff the current segment in
    // the grid is crossed by one of the contour values and
    // has not already been processed for that value.
    // -------------------------------------------------------
    private boolean crossedByContour(boolean workSpace[]) {
        ii = ij[0] + i1[elle];
        jj = ij[1] + i1[1 - elle];
        z1 = z[ij[0] - 1][ij[1] - 1];
        z2 = z[ii - 1][jj - 1];
        for (cntrIndex = 0; cntrIndex < ncv; cntrIndex++) {
            int i = 2 * (xSteps * (ySteps * cntrIndex + ij[1] - 1) + ij[0] - 1) + elle;

            if (!workSpace[i]) {
                double x = contourLevels[cntrIndex];
                if ((x > Math.min(z1, z2)) && (x <= Math.max(z1, z2))) {
                    workSpace[i] = true;
                    return true;
                }
            }
        }
        return false;
    }

    // -------------------------------------------------------
    // "detectBoundary"
    // -------------------------------------------------------
    private void detectBoundary() {
        ix = 1;
        if (ij[1 - elle] != 1) {
            ii = ij[0] - i1[1 - elle];
            jj = ij[1] - i1[elle];
            if (z[ii - 1][jj - 1] <= zMaxMin) {
                ii = ij[0] + i2[elle];
                jj = ij[1] + i2[1 - elle];
                if (z[ii - 1][jj - 1] < zMaxMin) {
                    ix = 0;
                }
            }
            if (ij[1 - elle] >= l1[1 - elle]) {
                ix = ix + 2;
                return;
            }
        }
        ii = ij[0] + i1[1 - elle];
        jj = ij[1] + i1[elle];
        if (z[ii - 1][jj - 1] > zMaxMin) {
            ix = ix + 2;
            return;
        }
        if (z[ij[0]][ij[1]] >= zMaxMin) {
            ix = ix + 2;
        }
    }

    // -------------------------------------------------------
    // "drawKernel" is the guts of drawing and is called
    // directly or indirectly by "contourPlotKernel" in order
    // to draw a segment of a contour or to set the pen
    // position "prevXY". Its action depends on "iflag":
    //
    // iflag == 1 means Continue a contour
    // iflag == 2 means Start a contour at a boundary
    // iflag == 3 means Start a contour not at a boundary
    // iflag == 4 means Finish contour at a boundary
    // iflag == 5 means Finish closed contour not at boundary
    // iflag == 6 means Set pen position
    //
    // If the constant "SHOW_NUMBERS" is true then when
    // completing a contour ("iflag" == 4 or 5) the contour
    // index is drawn adjacent to where the contour ends.
    // -------------------------------------------------------
    private void drawKernel() {
        double prevU, prevV, u, v;

        if ((iflag == 1) || (iflag == 4) || (iflag == 5)) {
            if (cntrIndex != prevIndex) { // Must change colour
                // newContour();
                prevIndex = cntrIndex;
            }
            prevU = (prevXY[0] - 1.0) * deltaX + xmin;
            prevV = (prevXY[1] - 1.0) * deltaY + ymin;
            u = (xy[0] - 1.0) * deltaX + xmin;
            v = (xy[1] - 1.0) * deltaY + ymin;

            if (z1 < zMaxMin && z2 < zMaxMin) {
                contourSegment(prevU, prevV, u, v, contourLevels[cntrIndex], contourLabels[cntrIndex]);
            }
        }
        prevXY[0] = xy[0];
        prevXY[1] = xy[1];
    }

    // -------------------------------------------------------
    // "GetExtremes" scans the data in "z" in order
    // to assign values to "zMin" and "zMax".
    // -------------------------------------------------------
    private void getExtremes() {
        int i, j;
        double here;
        boolean dataSet = false;
        for (i = 0; i < xSteps; i++) {
            for (j = 0; j < ySteps; j++) {
                here = z[i][j];
                if (here < zMaxMin) {
                    if (dataSet) {
                        if (zMin > here) {
                            zMin = here;
                        }
                        if (zMax < here) {
                            zMax = here;
                        }
                    } else {
                        zMin = here;
                        zMax = here;
                        dataSet = true;
                    }
                }
            }
        }
        if (zMin == zMax) {
            invalidData();
        }
    }

    // -------------------------------------------------------
    // "invalidData" sets the first two components of the
    // contour value array to equal values, thus preventing
    // subsequent drawing of the contour plot.
    // -------------------------------------------------------
    private void invalidData() {
        contourLevels[0] = (double) 0.0;
        contourLevels[1] = (double) 0.0;

        contourLabels[0] = Double.toString(contourLevels[0]);
        contourLabels[1] = Double.toString(contourLevels[1]);

    }


    // -------------------------------------------------------
   public List<Contour> makeContours() {
        assignContourValues();

        int workLength = 2 * xSteps * ySteps * ncv;
        boolean workSpace[]; // Allocate below if data valid
        contours = new ArrayList<Contour>();
        /*
         * try {
         */
        if (contourLevels[0] != contourLevels[1]) { // Valid data
            workSpace = new boolean[workLength];
            contourPlotKernel(workSpace);
        }
        /*
         * } catch(Exception ex) { ex.printStackTrace(); return null; }
         */
        return contours;
    }

    // -------------------------------------------------------
    // "routineLabel020" corresponds to a block of code
    // starting at label 20 in Synder's subroutine "GCONTR".
    // -------------------------------------------------------
    private boolean routineLabel020() {
        l2[0] = ij[0];
        l2[1] = ij[1];
        l2[2] = -ij[0];
        l2[3] = -ij[1];
        idir = 0;
        nxidir = 1;
        k = 1;
        ij[0] = Math.abs(ij[0]);
        ij[1] = Math.abs(ij[1]);
        if (z[ij[0] - 1][ij[1] - 1] > zMaxMin) {
            elle = idir % 2;
            ij[elle] = sign(ij[elle], l1[k - 1]);
            return true;
        }
        elle = 0;
        return false;
    }

    // -------------------------------------------------------
    // "routineLabel050" corresponds to a block of code
    // starting at label 50 in Synder's subroutine "GCONTR".
    // -------------------------------------------------------
    private boolean routineLabel050() {
        while (true) {
            if (ij[elle] >= l1[elle]) {
                if (++elle <= 1) {
                    continue;
                }
                elle = idir % 2;
                ij[elle] = sign(ij[elle], l1[k - 1]);
                if (routineLabel150()) {
                    return true;
                }
                continue;
            }
            ii = ij[0] + i1[elle];
            jj = ij[1] + i1[1 - elle];
            if (z[ii - 1][jj - 1] > zMaxMin) {
                if (++elle <= 1) {
                    continue;
                }
                elle = idir % 2;
                ij[elle] = sign(ij[elle], l1[k - 1]);
                if (routineLabel150()) {
                    return true;
                }
                continue;
            }
            break;
        }
        jump = false;
        return false;
    }

    // -------------------------------------------------------
    // "routineLabel150" corresponds to a block of code
    // starting at label 150 in Synder's subroutine "GCONTR".
    // -------------------------------------------------------
    private boolean routineLabel150() {
        while (true) {
            // ------------------------------------------------
            // Lines from z[ij[0]-1][ij[1]-1]
            // to z[ij[0] ][ij[1]-1]
            // and z[ij[0]-1][ij[1]]
            // are not satisfactory. Continue the spiral.
            // ------------------------------------------------
            if (ij[elle] < l1[k - 1]) {
                ij[elle]++;
                if (ij[elle] > l2[k - 1]) {
                    l2[k - 1] = ij[elle];
                    idir = nxidir;
                    nxidir = idir + 1;
                    k = nxidir;
                    if (nxidir > 3) {
                        nxidir = 0;
                    }
                }
                ij[0] = Math.abs(ij[0]);
                ij[1] = Math.abs(ij[1]);
                if (z[ij[0] - 1][ij[1] - 1] > zMaxMin) {
                    elle = idir % 2;
                    ij[elle] = sign(ij[elle], l1[k - 1]);
                    continue;
                }
                elle = 0;
                return false;
            }
            if (idir != nxidir) {
                nxidir++;
                ij[elle] = l1[k - 1];
                k = nxidir;
                elle = 1 - elle;
                ij[elle] = l2[k - 1];
                if (nxidir > 3) {
                    nxidir = 0;
                }
                continue;
            }

            if (ibkey != 0) {
                return true;
            }
            ibkey = 1;
            ij[0] = icur;
            ij[1] = jcur;
            if (routineLabel020()) {
                continue;
            }
            return false;
        }
    }

    // -------------------------------------------------------
    // "routineLabel200" corresponds to a block of code
    // starting at label 200 in Synder's subroutine "GCONTR".
    // It has return values 0, 1 or 2.
    // -------------------------------------------------------
    private int routineLabel200(boolean workSpace[]) {
        while (true) {
            xy[elle] = 1.0 * ij[elle] + intersect[iedge - 1];
            xy[1 - elle] = 1.0 * ij[1 - elle];
            workSpace[2 * (xSteps * (ySteps * cntrIndex + ij[1] - 1) + ij[0] - 1) + elle] = true;
            drawKernel();
            if (iflag >= 4) {
                icur = ij[0];
                jcur = ij[1];
                return 1;
            }
            continueContour();
            if (!workSpace[2 * (xSteps * (ySteps * cntrIndex + ij[1] - 1) + ij[0] - 1) + elle]) {
                return 2;
            }
            iflag = 5; // 5. Finish a closed contour
            iedge = ks + 2;
            if (iedge > 4) {
                iedge = iedge - 4;
            }
            intersect[iedge - 1] = intersect[ks - 1];
        }
    }

    // -------------------------------------------------------
    private void setXYZ(double[][] z, int nx, int ny, double xmin, double ymin,
            double xstep, double ystep, double zMaxMin) {

        this.z = z;
        xSteps = nx;
        ySteps = ny;
        this.xmin = xmin;
        this.ymin = ymin;
        deltaX = xstep;
        deltaY = ystep;
        this.zMaxMin = zMaxMin;
    }

    // -------------------------------------------------------
    private int sign(final int a, final int b) {
        int c = Math.abs(a);
        if (b < 0) {
            c = -c;
        }
        return c;
    }

}
