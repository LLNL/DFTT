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
package llnl.gnem.core.gui.plotting.contour;

import java.util.ArrayList;
import llnl.gnem.core.gui.plotting.contour.Contour;
import llnl.gnem.core.gui.plotting.contour.ContourLine;
import llnl.gnem.core.gui.plotting.contour.Point;

/**
 *
 * @author dyer1
 */
/**
 * =========================================================================
 * Class MarchingSquaresContours implements the marching squares algorithm to find
 * plot contour lines.
 * One description of this algorithm is at
 *     http://en.wikipedia.org/wiki/Marching_squares
 */
public class MarchingSquaresContours {
    private enum Side
    {
        None,
        Top,
        Left,
        Bottom,
        Right
    }

    private MarchingSquaresGriddedData gridData;
    private ArrayList<Double> xGridVals;
    private ArrayList<Double> yGridVals;
    private double[][] zVals;

    int nxData;
    int nyData;

    byte[][] cells;
    int nxCells;
    int nyCells;

    /**
     * =========================================================================
     * Class constructor.
     *
     * @param gridData - 2D array of gridded data.
     */
    public MarchingSquaresContours(MarchingSquaresGriddedData gridData) {
        this.gridData = gridData;
        xGridVals = gridData.getGridValsX();
        yGridVals = gridData.getGridValsY();
        zVals = gridData.getGriddedData();

        nxData = gridData.getLenXGrid();
        nyData = gridData.getLenYGrid();
    }


    /**
     * =========================================================================
     * Return the contour lines for a given contour value
     *
     * @param contourLevel - A number giving the value to contour.
     * @param label - A label to be associated with the contour.
     * @return contourLines - An ArrayList of contour lines for this contour level.
     */
    public ArrayList<ContourLine> getContourLine(double contourLevel, String label) {

        // Convert z arr to byte or boolean array
        byte[][] shapes = getZeroOneArray(contourLevel);

        // At each grid intersection, create a cell and fill it with a value
        // from 0 to 15, depending on which of the four surrounding values are
        // 0's and 1's.
        createFillCells(shapes, contourLevel);


        ArrayList<ContourLine> contourLines = new ArrayList<ContourLine>();
        while (true) {
            Contour c = new Contour(contourLevel, label);
            MachingSquareIdx startCell = findStart();
            if (startCell == null) {
                break;
            }
            ArrayList<Point> points = walkPath(startCell, contourLevel);
            if (points.isEmpty()) {
                break;
            }
            for (Point pt : points) {
                c.addPoint(pt);
            }

            contourLines.add(new ContourLine(c));
        }


        return contourLines;
    }


    // ------------------------------------------------------------------------
    // Private methods
    // ------------------------------------------------------------------------

    /**
     * =========================================================================
     * Error check that the contour line is coming into the cell from the
     * expected direction.
     *
     */
    private void checkIncoming(Side incoming, Side nextIncoming) {
        if (nextIncoming == Side.None) {
            return;
        }
        if (incoming != nextIncoming) {
            System.err.println("Expected and actual incoming directions do not match.");
            System.exit(1);
        }
    }


    /**
     * =========================================================================
     * At each grid intersection, create a cell and fill it with a value
     * from 0 to 15, depending on which of the four surrounding values are
     * 0's (empty) and 1's (filled).
     *
     * @param shapes - A 2D byte array of indicating if the cells are empty or filled.
     * @param contourLevel - A number giving the value to contour.
     */
    private void createFillCells(byte[][] shapes, double contourLevel) {

        nxCells = nxData - 1;
        nyCells = nyData - 1;
        cells = new byte[nxCells][nyCells];
        for (int iy = 0; iy < nyCells; iy++) {
            for (int ix = 0; ix < nxCells; ix++) {
                boolean downLeft  = isEmpty(shapes, ix,     iy);
                boolean downRight = isEmpty(shapes, ix + 1, iy);
                boolean upRight   = isEmpty(shapes, ix + 1, iy + 1);
                boolean upLeft    = isEmpty(shapes, ix,     iy + 1);


                // Determine which state we are in
                byte state = 0;

                if (downLeft) {
                    state |= 1;
                }
                if (downRight) {
                    state |= 2;
                }
                if (upRight) {
                    state |= 4;
                }
                if (upLeft) {
                    state |= 8;
                }

                // Resolve saddle points
//                if (state == 5 || state == 10) {
//                    double valDL = zVals[ix][iy];
//                    double valDR = zVals[ix+1][iy];
//                    double valUR = zVals[ix+1][iy+1];
//                    double valUL = zVals[ix][iy+1];
//                    double valCentral = (valDL + valDR + valUR + valUL) / 4.0;
//
//                    if (valCentral < contourLevel && (state == 5)) {
//                        state = 10;
//                    }
//                    else if (valCentral < contourLevel && (state == 10)) {
//                        state = 5;
//                    }
//
//
//                }

                // Convert the case 0's (completely surrounded) to 15's (completely empty)
                if (state == 0) {
                    state = 15;
                }

                cells[ix][iy] = state;
            }
        }

    }


    /**
     * =========================================================================
     * Find a point on a contour line so the march around the contour may begin
     *
     * @return index - A MachingSquareIdx giving the index of a starting point.
     */
    // Finds the first pixel in the perimeter of the image
    private MachingSquareIdx findStart() {
        boolean foundASaddlePoint = false;

        //  What if 5 and 10 are all that are available?  ???
        for (int iy = 0; iy < nyCells; iy++) {
            for (int ix = 0; ix < nxCells; ix++) {
                byte state = cells[ix][iy];
                if (state == 5 || state == 10) {
                    foundASaddlePoint = true;
                    continue;
                }
                if (state < 15) {
                    return new MachingSquareIdx(ix, iy);
                }
            }
        }

        if (foundASaddlePoint) {
            System.err.println("Cannot find a suitable starting point.");
//            System.exit(1);
        }
        return null;

    }


    /**
     * =========================================================================
     * Given a direction to move and the current location, find the next
     * cell (point).
     *
     * @param outgoing - The side to exit from of the current cell.
     * @param ix - x index of current cell.
     * @param iy - y index of current cell.
     * @return index - A MachingSquareIdx giving the index of the next point.
     */
    private MachingSquareIdx getNextCell(Side outgoing, int ix, int iy) {
        int ixOut = ix;
        int iyOut = iy;
        if (outgoing == Side.Bottom) {
            ixOut = ix;
            iyOut = iy - 1;
        }
        else if (outgoing == Side.Left) {
            ixOut = ix - 1;
            iyOut = iy;
        }
        else if (outgoing == Side.Top) {
            ixOut = ix;
            iyOut = iy + 1;
        }
        else if (outgoing == Side.Right) {
            ixOut = ix + 1;
            iyOut = iy;
        }

        return new MachingSquareIdx(ixOut, iyOut);
    }


    /**
     * =========================================================================
     * Given a cell and its side of interest, determine the x and y value
     * and create a Point object.
     *
     * @param side - The side entering from or exiting to of the current cell
     * @param ix - x index of current cell.
     * @param iy - y index of current cell.
     * @param contourLevel - A number giving the value to contour.
     * @return point - A Point to be used in the contour line.
     */
    private Point getPoint(Side side, int ix, int iy, double contourLevel) {
        int igx1 = -1;
        int igx2 = -1;
        int igy1 = -1;
        int igy2 = -1;
        if (side == Side.Bottom) {
            igx1 = ix;
            igx2 = ix + 1;
            igy1 = iy;
            igy2 = iy;
        }
        else if (side == Side.Left) {
            igx1 = ix;
            igx2 = ix;
            igy1 = iy;
            igy2 = iy + 1;
        }
        else if (side == Side.Top) {
            igx1 = ix;
            igx2 = ix + 1;
            igy1 = iy + 1;
            igy2 = iy + 1;
        }
        else if (side == Side.Right) {
            igx1 = ix + 1;
            igx2 = ix + 1;
            igy1 = iy;
            igy2 = iy + 1;
        }


        // With the Marching Squares algorithm, either igx1 == igx2 or igy1 == igy2
        double x = 0.0;
        double y = 0.0;
        if (igx1 == igx2) {
            x = xGridVals.get(igx1);

            double y1 = yGridVals.get(igy1);
            double y2 = yGridVals.get(igy2);

            double v1 = zVals[igx1][igy1];
            double v2 = zVals[igx1][igy2];


            double diff = y2 - y1;
            double vmax = Math.max(v1, v2);
            double frac = 0.0;
            if (v1 < v2) {
                frac = contourLevel / vmax;
            }
            else if (v1 > v2) {
                frac = 1.0 - (contourLevel / vmax);
            }
            else {
                System.err.println("The values cannot be equal on both sides of a contour line.");
                System.exit(1);
            }
            y = y1 + (frac * diff);

        }
        else if (igy1 == igy2) {
            y = yGridVals.get(igy1);

            double x1 = xGridVals.get(igx1);
            double x2 = xGridVals.get(igx2);

            double v1 = zVals[igx1][igy1];
            double v2 = zVals[igx2][igy1];

            double diff = x2 - x1;
            double vmax = Math.max(v1, v2);
            double frac = 0.0;
            if (v1 < v2) {
                frac = contourLevel / vmax;
            }
            else if (v1 > v2) {
                frac = 1.0 - (contourLevel / vmax);
            }
            else {
                System.err.println("The values cannot be equal on both sides of a contour line.");
                System.exit(1);
            }
            x = x1 + (frac * diff);
        }
        else {
            System.err.println("Indices are incorrect.");
            System.exit(1);
        }


        return new Point(x, y);
    }


    /**
     * =========================================================================
     * For a given state value ranging from 0-15, determine which sides the
     * contour line enters from and exits to.
     *
     * @param state - Value stating which neighboring cells are empty or occupied.
     * @param thisIncoming - The incoming side.
     * @return sides - ArrayList giving the incoming, outgoing and next incoming sides.
     */
    private ArrayList<Side> getSides(byte state, Side thisIncoming) {
        Side incoming = Side.None;
        Side outgoing = Side.None;
        Side nextIncoming = Side.None;

        switch (state) {
            case 0:    // Completely surrounded
                break;
            case 1:
                incoming = Side.Bottom;
                checkIncoming(incoming, nextIncoming);
                outgoing = Side.Left;
                nextIncoming = Side.Right;

                break;
            case 2:
                incoming = Side.Right;
                checkIncoming(incoming, nextIncoming);
                outgoing = Side.Bottom;
                nextIncoming = Side.Top;
                break;
            case 3:
                incoming = Side.Right;
                checkIncoming(incoming, nextIncoming);
                outgoing = Side.Left;
                nextIncoming = Side.Right;
                break;
            case 4:
                incoming = Side.Top;
                checkIncoming(incoming, nextIncoming);
                outgoing = Side.Right;
                nextIncoming = Side.Left;
                break;
            case 5:
                if (thisIncoming == Side.Top) {
                    incoming = Side.Top;
                    outgoing = Side.Left;
                    nextIncoming = Side.Right;
                } else if (thisIncoming == Side.Bottom) {
                    incoming = Side.Bottom;
                    outgoing = Side.Right;
                    nextIncoming = Side.Left;
                } else {
                    System.err.println("getSides, case 5: Bad marching square direction. Received " + thisIncoming);
                    System.exit(1);
                }
                break;
            case 6:
                incoming = Side.Top;
                checkIncoming(incoming, nextIncoming);
                outgoing = Side.Bottom;
                nextIncoming = Side.Top;
                break;
            case 7:
                incoming = Side.Top;
                checkIncoming(incoming, nextIncoming);
                outgoing = Side.Left;
                nextIncoming = Side.Right;
                break;
            case 8:
                incoming = Side.Left;
                checkIncoming(incoming, nextIncoming);
                outgoing = Side.Top;
                nextIncoming = Side.Bottom;
                break;
            case 9:
                incoming = Side.Bottom;
                checkIncoming(incoming, nextIncoming);
                outgoing = Side.Top;
                nextIncoming = Side.Bottom;
                break;
            case 10:
                if (thisIncoming == Side.Right) {
                    incoming = Side.Right;
                    outgoing = Side.Top;
                    nextIncoming = Side.Bottom;
                } else if (thisIncoming == Side.Left) {
                    incoming = Side.Left;
                    outgoing = Side.Bottom;
                    nextIncoming = Side.Top;
                } else {
                    System.err.println("getSides, case 10: Bad marching square direction. Received " + thisIncoming);
                    System.exit(1);
                }
                break;
            case 11:
                incoming = Side.Right;
                checkIncoming(incoming, nextIncoming);
                outgoing = Side.Top;
                nextIncoming = Side.Bottom;
                break;
            case 12:
                incoming = Side.Left;
                checkIncoming(incoming, nextIncoming);
                outgoing = Side.Right;
                nextIncoming = Side.Left;
                break;
            case 13:
                incoming = Side.Bottom;
                checkIncoming(incoming, nextIncoming);
                outgoing = Side.Right;
                nextIncoming = Side.Left;
                break;
            case 14:
                incoming = Side.Left;
                checkIncoming(incoming, nextIncoming);
                outgoing = Side.Bottom;
                nextIncoming = Side.Top;
                break;
            case 15:    // Completely empty
                break;
            default:
                break;
        }

        ArrayList<Side> sides = new ArrayList<Side>();
        sides.add(incoming);
        sides.add(outgoing);
        sides.add(nextIncoming);
        return sides;
    }


    /**
     * =========================================================================
     * Given a 2D array of values, create a matching 2D byte array where
     * anything less than the contour value is set to 0 and anything greater
     * than or equal to is set to 1.
     *
     * @param contourLevel - A number giving the value to contour.
     * @return shapes - 2D byte array of 0's and 1's.
     */
    private byte[][] getZeroOneArray(double contourLevel) {
        int nx = gridData.getLenXGrid();
        int ny = gridData.getLenYGrid();

        // Convert z arr to byte or boolean array
        byte[][] shapes = new byte[nx][ny];
        for (int iy = 0; iy < ny; iy++) {
            for (int ix = 0; ix < nx; ix++) {
                if (zVals[ix][iy] >= contourLevel) {
                    shapes[ix][iy] = 1;
                }
                else {
                    shapes[ix][iy] = 0;
                }
            }
        }
        return shapes;
    }


    /**
     * =========================================================================
     * Determine if a cell is empty
     *
     * @param shapes - 2D byte array of 0's and 1's.
     * @param ix - x index of the cell.
     * @param iy - y index of the cell.
     * @return result - boolean. True if cell is empty.
     */
    private boolean isEmpty(byte[][] shapes, int ix, int iy) {
        if (shapes[ix][iy] == 0) {
            return true;
        }
        return false;
    }


    /**
     * =========================================================================
     * Wlak the path of a contour
     *
     * @param startCell - Index of the starting cell on the path.
     * @param contourLevel - A number giving the value to contour.
     * @return pointList - ArrayList of contour points.
     */
    private ArrayList<Point> walkPath(MachingSquareIdx startCell, double contourLevel) {
        byte state;
        Side incoming;
        Side outgoing;
        Side nextIncoming;
        Side thisIncoming = Side.None;
        ArrayList<Side> sides;
        ArrayList<Point> pointList = new ArrayList<Point>();

        // Starting point ----------------
        int startX = startCell.getXIndex();
        int startY = startCell.getYIndex();
        state = cells[startX][startY];
        if (state == 5 || state == 10) {
            System.err.println("Cannot start marching square walk on a saddle point.");
            System.exit(1);
        }
        sides = getSides(state, thisIncoming);
        incoming = sides.get(0);
        nextIncoming = incoming;
        Point firstPoint = getPoint(incoming, startX, startY, contourLevel); // Based on incoming side
        pointList.add(firstPoint);


        // Start walking -------
        int ix = startX;
        int iy = startY;
        while (true) {
            state = cells[ix][iy];

            thisIncoming = nextIncoming;
            sides = getSides(state, thisIncoming);

            outgoing = sides.get(1);
            nextIncoming = sides.get(2);



            if (outgoing == Side.None) {
                break;
            }

            Point pt = getPoint(outgoing, ix, iy, contourLevel);
            pointList.add(pt);


            // Empty out this cell, so we can do multiple calls to walkPath
            // to check for multiple shapes of this contour level without
            // getting into an infinite loop
            cells[ix][iy] = 15;

            // Get next ix, iy
            MachingSquareIdx nextIdx = getNextCell(outgoing, ix, iy);
            ix = nextIdx.getXIndex();
            iy = nextIdx.getYIndex();
            if (ix < 0 || iy < 0 || ix >= cells.length || iy >= cells[ix].length ) {
                break;
            }

        }

        return pointList;
    }

}
