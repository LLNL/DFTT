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
package llnl.gnem.dftt.core.gui.plotting.plotobject;


import llnl.gnem.dftt.core.gui.plotting.transforms.Coordinate;
import llnl.gnem.dftt.core.gui.plotting.transforms.CoordinateTransform;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * A class that draws a centered TriangleUp superimposed on an "I-beam" whose height is equal to the standard error
 * associated with the measurement being represented by the centered symbol. The symbol may have text associated
 * with it and can have its internal color set independently of its edge color. The symbol edge color is the
 * same color as the I-Beam representing the uncertainty. The positive uncertainty can be set independently
 * of the negative uncertainty.
 */
public class ErrorBarOnTriangleUp extends ErrorBar {


    /**
     * Constructor for ErrorBar that allows specification of both a plus and a minus value for the
     * standard error as well as setting all properties for the centered symbol.
     *
     * @param sigmaPlus  The positive standard error in data units
     * @param sigmaMinus The negative standard error in data units
     * @param halfWidth  The half-width of the error bar "I-Beam" in mm
     * @param X          X-coordinate of the center of the symbol
     * @param Y          Y-coordinate of the center of the symbol
     * @param size       Size of the symbol in millimeters
     * @param fillC      Fill color of the symbol
     * @param edgeC      Edge color of the symbol edge
     * @param textC      Color of the text
     * @param text       Optional text associated with the symbol.
     * @param visible    Controls whether the symbol is visible.
     * @param textVis    Controls whether the text associated with the symbol is visible.
     * @param fontsize   The fontsize of the associated text.
     */
    public ErrorBarOnTriangleUp(double X, double Y, double size, double sigmaPlus, double sigmaMinus, double halfWidth, Color fillC,
                    Color edgeC, Color textC, String text, boolean visible, boolean textVis, double fontsize) {
        super(X, Y, size, sigmaPlus, sigmaMinus, halfWidth, fillC, edgeC, textC, text, visible, textVis, fontsize);

    }

    /**
     * Constructor for ErrorBar that allows specification of a single standard error value for the
     * standard error as well as setting all properties for the centered symbol.
     *
     * @param X         X-coordinate of the center of the symbol
     * @param Y         Y-coordinate of the center of the symbol
     * @param size      Size of the symbol in millimeters
     * @param sigma     The standard error in data units
     * @param halfWidth The half-width of the error bar "I-Beam" in mm
     * @param fillC     Fill color of the symbol
     * @param edgeC     Edge color of the symbol edge
     * @param textC     Color of the text
     * @param text      Optional text associated with the symbol.
     * @param visible   Controls whether the symbol is visible.
     * @param textVis   Controls whether the text associated with the symbol is visible.
     * @param fontsize  The fontsize of the associated text.
     */
    public ErrorBarOnTriangleUp(double X, double Y, double size, double sigma, double halfWidth, Color fillC, Color edgeC,
                    Color textC, String text, boolean visible, boolean textVis, double fontsize) {
        super(X, Y, size, sigma, halfWidth, fillC, edgeC, textC, text, visible, textVis, fontsize);
    }


    /**
     * Constructor that allows setting independent Y-standard errors but uses default values for the centered symbol
     *
     * @param sigmaPlus  The positive standard error in data units
     * @param sigmaMinus The negative standard error in data units
     * @param halfWidth  The half-width of the error bar "I-Beam" in mm
     * @param X          X-coordinate of the center of the symbol
     * @param Y          Y-coordinate of the center of the symbol
     * @param size       Size of the symbol in millimeters
     */
    public ErrorBarOnTriangleUp( double X, double Y, double size, double sigmaPlus, double sigmaMinus, double halfWidth )
    {
        super( X, Y, size, sigmaPlus, sigmaMinus, halfWidth );
    }


    /**
     * Constructor that allows specification of a single-standard error value and that uses default values
     * for all properties related to drawing the centered symbol.
     *
     * @param X         X-coordinate of the center of the symbol
     * @param Y         Y-coordinate of the center of the symbol
     * @param size      Size of the symbol in millimeters
     * @param sigma     The standard error in data units
     * @param halfWidth The half-width of the error bar "I-Beam" in mm
     */
    public ErrorBarOnTriangleUp( double X, double Y, double size, double sigma, double halfWidth )
    {
        super( X, Y, size, sigma, halfWidth );

    }

    public ErrorBarOnTriangleUp() {
        super();
    }

    /**
     * render the symbol to the supplied graphics context. This method is called
     * by the base class render method. Text is rendered separately.
     *
     * @param g The graphics context
     * @param x The X-value ( in user space pixels )
     * @param y The Y-value ( in user space pixels )
     * @param h The height of the symbol in pixels
     */
    public void PaintSymbol(Graphics g, int x, int y, int h) {
        int du = (int) (0.530 * h);
        Graphics2D g2d = (Graphics2D) g;

        // for the symbol legend, there is no assigned owner
        GeneralPath p = new GeneralPath();
        if (owner != null) {
            // First draw the I-beam shaped error bars
            CoordinateTransform ct = owner.getCoordinateTransform();

            switch (getOrientation()) {
            case Vertical: {
                int half = owner.getUnitsMgr().getHorizUnitsToPixels(getHalfWidth());
                float leftEdge = (float) x - half;
                float rightEdge = leftEdge + 2 * half;
                Coordinate coord = new Coordinate(0.0, 0.0, 0.0, _Ycenter + getSigmaPlus());
                ct.WorldToPlot(coord);
                float top = (float) coord.getY();
                coord.setWorldC2(_Ycenter - getSigmaPlus());
                ct.WorldToPlot(coord);
                float bottom = (float) coord.getY();

                p.moveTo(leftEdge, top);
                p.lineTo(rightEdge, top);
                p.moveTo((float) x, top);
                p.lineTo((float) x, bottom);
                p.moveTo(leftEdge, bottom);
                p.lineTo(rightEdge, bottom);
            }
                break;

            case Horizontal: {
                int half = owner.getUnitsMgr().getVertUnitsToPixels(getHalfWidth());
                float topEdge = (float) y - half;
                float bottomEdge = topEdge + 2 * half;
                Coordinate coord = new Coordinate(0.0, 0.0, _Xcenter + getSigmaPlus(), 0.0);
                ct.WorldToPlot(coord);
                float right = (float) coord.getX();
                coord.setWorldC1(_Xcenter - getSigmaPlus());
                ct.WorldToPlot(coord);
                float left = (float) coord.getX();

                p.moveTo(left, topEdge);
                p.lineTo(left, bottomEdge);
                p.moveTo(left, (float) y);
                p.lineTo(right, (float) y);
                p.moveTo(right, topEdge);
                p.lineTo(right, bottomEdge);
            }
                break;
            }

            g2d.setColor(getErrorBarColor());
            g2d.draw(p);
        }

        // Now draw the centered symbol
        float h2 = h / 2.F;
        GeneralPath triangle = new GeneralPath();
        triangle.moveTo(x, y - h2);
        triangle.lineTo(x + h2, y + h2);
        triangle.lineTo(x - h2, y + h2);
        triangle.lineTo(x, y - h2);
        // Rectangle2D square = new Rectangle2D.Double( x - h2, y - h2, h, h );
        // Ellipse2D circle = new Ellipse2D.Double( x - du, y - du, 2 * du, 2 * du );
        g2d.setColor(getFillColor());
        g2d.fill(triangle);
        g2d.setColor(_EdgeColor);
        g2d.setStroke(new BasicStroke(1.0F));
        g2d.draw(triangle);
        addToRegion(triangle);
    }

    /**
     * Produce a String descriptor for this object
     *
     * @return The String descriptor
     */
    public String toString() {
        return "ErrorBarOnTriangleUp" + super.toString();
    }
}
