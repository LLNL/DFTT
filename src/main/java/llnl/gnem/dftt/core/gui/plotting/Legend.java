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
package llnl.gnem.dftt.core.gui.plotting;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import llnl.gnem.dftt.core.gui.plotting.plotobject.*;

/*
 * COPYRIGHT NOTICE GnemUtils Version 1.0 Copyright (C) 2002 Lawrence Livermore
 * National Laboratory.
 */
/**
 * A Class that allows display of a legend associating a String with each line
 * displayed in a Subplot.
 *
 * @author Doug Dodge
 */
@SuppressWarnings({"MagicNumber"})
public class Legend extends PlotObject {
    private List<String> labels;
    private List<AbstractLine> lines;
    private String _FontName;
    private double _FontSize;
    private HorizPinEdge _HorAlign;
    private VertPinEdge _VertAlign;
    private double _Xoff;
    private double _Yoff;
    
    public Legend(String fontName, double fontSize, HorizPinEdge hAlign, VertPinEdge vAlign, double xoff, double yoff) {
        this(new ArrayList<String>(), new ArrayList<Line>(), fontName, fontSize, hAlign, vAlign, xoff, yoff);
    }

    /**
     * Constructor for the Legend object
     *
     * @param text A Vector of Strings, one for each Line in the Subplot,
     * ordered the same as the creation order of the Lines.
     * @param lines
     * @param fontName The name of the font used to display the Strings in the
     * legend
     * @param fontSize The size of the font used to display the legend strings.
     * @param hAlign The horizontal alignment of the legend ( LEFT, RIGHT )
     * @param vAlign The vertical alignment of the legend (TOP, BOTTOM)
     * @param xoff The horizontal distance in mm from the horizontal pin edge
     * @param yoff The vertical distance in mm from the vertical pin edge
     */
    public Legend(List<String> text, List<Line> lines, String fontName, double fontSize, HorizPinEdge hAlign, VertPinEdge vAlign, double xoff, double yoff) {
        labels = new ArrayList<String>(text);
        this.lines = new ArrayList<AbstractLine>(lines);
        _FontName = fontName;
        _FontSize = fontSize;
        _HorAlign = hAlign;
        _VertAlign = vAlign;
        _Xoff = xoff;
        _Yoff = yoff;
    }
    
    public void addLabeledLine(String label, AbstractLine line) {
        labels.add(label);
        lines.add(line);
    }
    
    public void clear() {
        labels.clear();
        lines.clear();
    }

    // For now Legend cannot be moved
    @Override
    public void ChangePosition(JBasicPlot owner, Graphics graphics, double dx, double dy) {
    }

    /**
     * render this Legend to the supplied graphics context
     *
     * @param g The graphics context
     * @param owner The JSubplot that owns this Legend
     */
    @Override
    public void render(Graphics g, JBasicPlot owner) {
        if (labels.size() < 1 || !owner.getCanDisplay()) {
            return;
        }

        // Remove any pre-existing regions before creating new...
        region.clear();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font(_FontName, Font.PLAIN, (int) _FontSize));
        FontMetrics fm = g2d.getFontMetrics();
        int legendwidth = LegendWidth(owner, fm);
        int legendheight = getLegendHeight(fm);
        int legendleft = getLegendLeft(owner, legendwidth);
        int legendtop = getLegendTop(owner, legendheight);

        // Fill and stroke the legend box
        Rectangle rect = new Rectangle(legendleft, legendtop, legendwidth, legendheight);
        g2d.setColor(Color.white);
        g2d.fill(rect);
        g2d.setColor(Color.black);
        g2d.setStroke(new BasicStroke(1.0F));
        g2d.draw(rect);
        addToRegion(rect);
        // Make this selectable

        int off = fm.getMaxAscent() + fm.getMaxDescent();
        int off2 = off / 2;
        for (int j = 0; j < lines.size(); ++j) {
            String label = labels.get(j);
            int xtext = legendleft + off2;
            int ytest = legendtop + j * off + off2;
            g2d.setColor(Color.black);
            g2d.drawString(label, xtext, ytest + fm.getMaxAscent());
            int advance = fm.stringWidth(label);
            int startX = legendleft + off + advance;
            int length = legendleft + legendwidth - startX - off2;
            int Y = legendtop + (j + 1) * off;
            drawLine(lines.get(j), g, startX, Y, length);
        }
    }

    private int LegendWidth(JBasicPlot owner, FontMetrics fm) {
        int maxlen = 0;
        for (Object a_Text : labels) {
            int advance = fm.stringWidth((String) a_Text);
            maxlen = Math.max(maxlen, advance);
        }
        double minLineLen = 15.0;
        // millimeters
        return maxlen + owner.getUnitsMgr().getHorizUnitsToPixels(minLineLen) + 5;
    }

    private int getLegendHeight(FontMetrics fm) {
        int height = fm.getMaxAscent() + fm.getMaxDescent();
        return (labels.size() + 1) * height;
    }

    private int getLegendLeft(JBasicPlot owner, int legendwidth) {
        int offset = owner.getUnitsMgr().getHorizUnitsToPixels(_Xoff);
        return _HorAlign == HorizPinEdge.LEFT ? owner.getPlotLeft() + offset : owner.getPlotLeft() + owner.getPlotWidth() - offset - legendwidth;
    }

    private int getLegendTop(JBasicPlot owner, int legendheight) {
        int offset = owner.getUnitsMgr().getVertUnitsToPixels(_Yoff);
        return _VertAlign == VertPinEdge.TOP ? owner.getPlotTop() + offset : owner.getPlotTop() + owner.getPlotHeight() - offset - legendheight;
    }

    private void drawLine(AbstractLine line, Graphics g, int startX, int Y, int length) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(line.getColor());
        PaintMode Mode = PaintMode.COPY;
        Mode.setGraphicsPaintMode(g2d);
        g2d.setStroke(line.getPenStyle().getStroke(line.getWidth()));
        if (line.getPenStyle() != PenStyle.NONE) {
            g2d.drawLine(startX, Y, startX + length, Y);
        }

        if (line.getSymbolStyle() != SymbolStyle.NONE) {
            Symbol s;
            if (line.getSymbolStyle() == SymbolStyle.ERROR_BAR) {
                s = new ErrorBar(0.0, 0.0, line.getSymbolSize(), 0.35, 0.35,
                        2.0, line.getSymbolFillColor(), line.getSymbolEdgeColor(),
                        Color.black, "", true, false, 10.0);
                s.setOwner(getOwner());

            } else {
                s = SymbolFactory.createSymbol(line.getSymbolStyle(), 0.0, 0.0, line.getSymbolSize(),
                        line.getSymbolFillColor(), line.getSymbolEdgeColor(),
                        Color.black, "", true, false, 10.0);
            }

            if (s != null) {
                s.setXcoordPinned(true);
                s.setYcoordPinned(true);
                s.setXcoordIntValue(startX);
                s.setYcoordIntValue(Y);
                s.render(g2d, owner);
                s.setXcoordIntValue(startX + length);
            }
        }
    }
}
