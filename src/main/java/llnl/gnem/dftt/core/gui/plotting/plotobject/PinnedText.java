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

import llnl.gnem.dftt.core.gui.plotting.VertAlignment;
import llnl.gnem.dftt.core.gui.plotting.VertPinEdge;
import llnl.gnem.dftt.core.gui.plotting.HorizPinEdge;
import llnl.gnem.dftt.core.gui.plotting.HorizAlignment;
import llnl.gnem.dftt.core.gui.plotting.JBasicPlot;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

/*
 * COPYRIGHT NOTICE GnemUtils Version 1.0 Copyright (C) 2002 Lawrence Livermore
 * National Laboratory.
 */
/**
 * Description of the Class
 *
 * @author Doug Dodge
 */
public class PinnedText extends BasicText {
    private final double xposition;
    private final double yPosition;
    private final HorizPinEdge horizontalPinEdge;
    private final VertPinEdge verticalPinEdge;

    /**
     * Constructor for the PinnedText object
     *
     * @param xPos distance in mm from the x-pin edge
     * @param yPos distance in mm from the y-pin edge
     * @param text The text to render
     * @param hp The horizontal edge to pin to
     * @param vp The vertical edge to pin to
     * @param fontName The name of the font used to render the text
     * @param fontSize The fontSize
     * @param textC The color of the text
     * @param hAlign The horizontal alignment type
     * @param vAlign The vertical alignment type
     */
    public PinnedText(double xPos,
            double yPos,
            String text,
            HorizPinEdge hp,
            VertPinEdge vp,
            String fontName,
            double fontSize,
            Color textC,
            HorizAlignment hAlign,
            VertAlignment vAlign) {
        super(text, fontName, fontSize, textC, hAlign, vAlign);
        xposition = xPos;
        yPosition = yPos;
        horizontalPinEdge = hp;
        verticalPinEdge = vp;
    }

    /**
     * Constructor for the PinnedText object
     *
     * @param x distance in mm from the x-pin edge
     * @param y distance in mm from the y-pin edge
     * @param text The text to render
     */
    public PinnedText(double x, double y, String text) {
        super(text);
        xposition = x;
        yPosition = y;
        horizontalPinEdge = HorizPinEdge.LEFT;
        verticalPinEdge = VertPinEdge.TOP;
    }

    /**
     * Description of the Method
     *
     * @param g Description of the Parameter
     * @param owner Description of the Parameter
     */
    @Override
    public void render(Graphics g, JBasicPlot owner) {
        if (!visible || _Text.length() < 1 || !owner.getCanDisplay()) {
            return;
        }

        // Remove any pre-existing regions before creating new...
        region.clear();
        int xOffset = owner.getUnitsMgr().getHorizUnitsToPixels(xposition);
        int yOffset = owner.getUnitsMgr().getVertUnitsToPixels(yPosition);
        int xval = horizontalPinEdge == HorizPinEdge.LEFT ? owner.getPlotLeft() + xOffset : owner.getPlotLeft() + owner.getPlotWidth() - xOffset;
        int yval = verticalPinEdge == VertPinEdge.TOP ? owner.getPlotTop() + yOffset : owner.getPlotTop() + owner.getPlotHeight() - yOffset;
        Graphics2D g2d = (Graphics2D) g;
        // Save old color
        Color oldColor = g2d.getColor();

        // Create new font and color
        g2d.setColor(_Color);

        // Layout and render text
        TextLayout textTl = new TextLayout(_Text, new Font(_FontName, Font.PLAIN, (int) _FontSize), new FontRenderContext(null, false, false));
        float xshift = getHorizontalAlignmentOffset(textTl);
        float yshift = getVerticalAlignmentOffset(textTl);
        textTl.draw(g2d, xval + xshift, yval + yshift);
        AffineTransform textAt = new AffineTransform();
        textAt.translate(xval + xshift, yval + yshift);
        Shape s = textTl.getOutline(textAt);
        addToRegion(s.getBounds2D());

        // restore old color
        g2d.setColor(oldColor);
    }
}
