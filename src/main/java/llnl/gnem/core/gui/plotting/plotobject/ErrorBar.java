package llnl.gnem.core.gui.plotting.plotobject;


import llnl.gnem.core.gui.plotting.transforms.Coordinate;
import llnl.gnem.core.gui.plotting.transforms.CoordinateTransform;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

/**
 * A class that draws a centered circle superimposed on an "I-beam" whose height is equal to the standard error
 * associated with the measurement being represented by the centered symbol. The symbol may have text associated
 * with it and can have its internal color set independently of its edge color. The symbol edge color is the
 * same color as the I-Beam representing the uncertainty. The positive uncertainty can be set independently
 * of the negative uncertainty.
 */
public class ErrorBar extends Symbol {

    public enum Orientation {
        Vertical, Horizontal
    };

    private final double sigmaPlus;
    // NOTE: currently the sigmaMinus value is not used when plotting.
    private final double sigmaMinus;
    private final double halfWidth; // width of error bars in mm.
    private Orientation ibeam = Orientation.Vertical;
    private Color errorBarColor = _EdgeColor;

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
    public ErrorBar( double X, double Y, double size, double sigmaPlus, double sigmaMinus,
                     double halfWidth, Color fillC, Color edgeC, Color textC, String text, boolean visible,
                     boolean textVis, double fontsize )
    {
        super( X, Y, size, fillC, edgeC, textC, text, visible, textVis, fontsize );
        this.sigmaMinus = Math.abs( sigmaMinus );
        this.sigmaPlus = Math.abs( sigmaPlus );
        this.halfWidth = halfWidth;
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
    public ErrorBar( double X, double Y, double size, double sigma, double halfWidth,
                     Color fillC, Color edgeC, Color textC, String text, boolean visible,
                     boolean textVis, double fontsize )
    {
        super( X, Y, size, fillC, edgeC, textC, text, visible, textVis, fontsize );
        this.sigmaMinus = Math.abs( sigma );
        this.sigmaPlus = Math.abs( sigma );
        this.halfWidth = halfWidth;
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
    public ErrorBar( double X, double Y, double size, double sigmaPlus, double sigmaMinus, double halfWidth )
    {
        super( X, Y, size );
        this.sigmaMinus = Math.abs( sigmaMinus );
        this.sigmaPlus = Math.abs( sigmaPlus );
        this.halfWidth = halfWidth;
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
    public ErrorBar( double X, double Y, double size, double sigma, double halfWidth )
    {
        super( X, Y, size );
        this.sigmaMinus = Math.abs( sigma );
        this.sigmaPlus = Math.abs( sigma );
        this.halfWidth = halfWidth;
    }


    public ErrorBar()
    {
        super();
        this.sigmaMinus = 0.0;
        this.sigmaPlus = 0.0;
        this.halfWidth = 1.0;
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

            switch (ibeam) {
            case Vertical: {
                int half = owner.getUnitsMgr().getHorizUnitsToPixels(halfWidth);
                float leftEdge = (float) x - half;
                float rightEdge = leftEdge + 2 * half;
                Coordinate coord = new Coordinate(0.0, 0.0, 0.0, _Ycenter + sigmaPlus);
                ct.WorldToPlot(coord);
                float top = (float) coord.getY();
                coord.setWorldC2(_Ycenter - sigmaPlus);
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
                int half = owner.getUnitsMgr().getVertUnitsToPixels(halfWidth);
                float topEdge = (float) y - half;
                float bottomEdge = topEdge + 2 * half;
                Coordinate coord = new Coordinate(0.0, 0.0, _Xcenter + sigmaPlus, 0.0);
                ct.WorldToPlot(coord);
                float right = (float) coord.getX();
                coord.setWorldC1(_Xcenter - sigmaPlus);
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
        Ellipse2D circle = new Ellipse2D.Double(x - du, y - du, 2 * du, 2 * du);
        g2d.setColor(getFillColor());
        g2d.fill(circle);
        g2d.setColor(_EdgeColor);
        g2d.setStroke(new BasicStroke(1.0F));
        g2d.draw(circle);
        addToRegion(circle);
    }

    /**
     * Produce a String descriptor for this object
     *
     * @return The String descriptor
     */
    public String toString()
    {
        return "ErrorBar" + super.toString();
    }

    public void setOrientation(Orientation value) {
        this.ibeam = value;
    }

    protected Orientation getOrientation() {
        return ibeam;
    }

    protected double getHalfWidth() {
        return halfWidth;
    }

    protected double getSigmaPlus() {
        return sigmaPlus;
    }

    protected double getSigmaMinus() {
        return sigmaMinus;
    }

    /**
     * Gets the fillColor attribute of the Symbol object
     *
     * @return The fillColor value
     */
    public Color getErrorBarColor() {
        return errorBarColor;
    }

    /**
     * Sets the fillColor attribute of the Symbol object
     *
     * @param v The new fillColor value
     */
    public void setErrorBarColor(Color v) {
        errorBarColor = new Color(v.getRed(), v.getGreen(), v.getBlue(), getAlpha());
    }

}
