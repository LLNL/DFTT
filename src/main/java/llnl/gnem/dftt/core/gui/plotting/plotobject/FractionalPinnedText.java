/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2023 Lawrence Livermore National Laboratory (LLNL)
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


import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import llnl.gnem.dftt.core.gui.plotting.HorizAlignment;
import llnl.gnem.dftt.core.gui.plotting.HorizPinEdge;
import llnl.gnem.dftt.core.gui.plotting.JBasicPlot;
import llnl.gnem.dftt.core.gui.plotting.VertAlignment;
import llnl.gnem.dftt.core.gui.plotting.VertPinEdge;

public class FractionalPinnedText extends BasicText {

    private static Integer nextYvalue = null;
    private final double xFraction; // Should be between 0 and 1 or text will not be contained within plot boundaries
    private final double yFraction; // Should be between 0 and 1 or text will not be contained within plot boundaries
    private final HorizPinEdge horizontalPinEdge;
    private final VertPinEdge verticalPinEdge;

    /**
     * Constructor for the FractionalPinnedText object
     *
     * @param xFraction fraction of plot width from the x-pin edge
     * @param yFraction fraction of plot height from the y-pin edge
     * @param text The text to render
     * @param hp The horizontal edge to pin to
     * @param vp The vertical edge to pin to
     * @param fontName The name of the font used to render the text
     * @param fontSize The fontSize
     * @param textC The color of the text
     * @param hAlign The horizontal alignment type
     * @param vAlign The vertical alignment type
     */
    public FractionalPinnedText(double xFraction,
            double yFraction,
            String text,
            HorizPinEdge hp,
            VertPinEdge vp,
            String fontName,
            double fontSize,
            Color textC,
            HorizAlignment hAlign,
            VertAlignment vAlign) {
        super(text, fontName, fontSize, textC, hAlign, vAlign);
        this.xFraction = xFraction;
        this.yFraction = yFraction;
        horizontalPinEdge = hp;
        verticalPinEdge = vp;
    }

    /**
     * Constructor for the PinnedText object
     *
     * @param xFraction fraction of plot width from the x-pin edge
     * @param yFraction fraction of plot height from the y-pin edge
     * @param text The text to render
     */
    public FractionalPinnedText(double xFraction, double yFraction, String text) {
        super(text);
        this.xFraction = xFraction;
        this.yFraction = yFraction;
        horizontalPinEdge = HorizPinEdge.LEFT;
        verticalPinEdge = VertPinEdge.TOP;
    }

    /**
     * Description of the Method
     *
     * @param g The graphics context
     * @param owner The plot in which this text is to be displayed
     */
    @Override
    public void render(Graphics g, JBasicPlot owner) {
        if (!visible || _Text.length() < 1 || !owner.getCanDisplay()) {
            return;
        }

        // Remove any pre-existing regions before creating new...
        region.clear();

        int xOffset = (int) Math.round(xFraction * owner.getPlotWidth());
        int xval = horizontalPinEdge == HorizPinEdge.LEFT ? owner.getPlotLeft() + xOffset : owner.getPlotLeft() + owner.getPlotWidth() - xOffset;

        if (yFraction < 0 && nextYvalue == null) {
            return;
        }
        int yval = nextYvalue != null ? nextYvalue : -1;
        if (yFraction >= 0) {
            int yOffset = (int) Math.round(yFraction * owner.getPlotHeight());
            yval = verticalPinEdge == VertPinEdge.TOP ? owner.getPlotTop() + yOffset : owner.getPlotTop() + owner.getPlotHeight() - yOffset;
        }
        Graphics2D g2d = (Graphics2D) g;
        // Save old color
        Color oldColor = g2d.getColor();

        // Create new font and color
        g2d.setColor(_Color);

        // Layout and render text
        Font font = new Font(getFontName(), Font.PLAIN, (int) getFontSize());
        g2d.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int stringHeight = fm.getHeight();
        nextYvalue = yval + stringHeight;

        FontRenderContext frc = g2d.getFontRenderContext();
        TextLayout textTl = new TextLayout(_Text, font, frc);
        float xshift = getHorizontalAlignmentOffset(textTl);
        float yshift = getVerticalAlignmentOffset(textTl);
        g2d.drawString(_Text, xval + xshift, yval + yshift);
        AffineTransform textAt = new AffineTransform();
        textAt.translate(xval + xshift, yval + yshift);
        Shape s = textTl.getOutline(textAt);
        addToRegion(s.getBounds2D());

        // restore old color
        g2d.setColor(oldColor);
    }
}
