package llnl.gnem.core.gui.plotting;

import llnl.gnem.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.core.gui.plotting.plotobject.SymbolDef;
import llnl.gnem.core.gui.plotting.plotobject.Symbol;
import llnl.gnem.core.gui.plotting.plotobject.SymbolFactory;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by dodge1
 * Date: Feb 6, 2008
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 * Will not inspect for: MagicNumber
 */
public class SymbolLegend extends PlotObject {


    private ArrayList<SymbolTextPair> legendEntries;
    private String fontName;
    private double fontSize;
    private HorizPinEdge horAlign;
    private VertPinEdge vertAlign;
    private double xOffset;
    private double yOffset;

    public SymbolLegend(ArrayList<SymbolTextPair> entries,
                        String fontName,
                        double fontSize,
                        HorizPinEdge hAlign,
                        VertPinEdge vAlign,
                        double xOff,
                        double yOff )
    {
        this.legendEntries = entries;
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.horAlign = hAlign;
        this.vertAlign = vAlign;
        this.xOffset = xOff;
        this.yOffset = yOff;
    }

    public void render(Graphics g, JBasicPlot owner)
    {
        if (legendEntries.size() < 1 || !owner.getCanDisplay() || !isVisible())
            return;

        // Remove any pre-existing regions before creating new...
        region.clear();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font(fontName, Font.PLAIN, (int) fontSize));
        FontMetrics fm = g2d.getFontMetrics();
        int legendwidth = getLegendWidth(owner, fm);
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


        int off = fm.getMaxAscent() + fm.getMaxDescent();
        int off2 = off / 2;
        for (int j = 0; j < legendEntries.size(); ++j) {
            String tmp = legendEntries.get(j).getText();
            int Y = legendtop + (j + 1) * off;
            drawSymbol(legendEntries.get(j).getSymbolDef(), g, legendleft + 10, Y);
            int xtext = legendleft + 20;
            int ytest = legendtop + j * off + off2;
            g2d.setColor(Color.black);
            g2d.drawString(tmp, xtext, ytest + fm.getMaxAscent());
        }

    }


    private void drawSymbol(SymbolDef line, Graphics g, int x, int y)
    {
        Graphics2D g2d = (Graphics2D) g;
        try {
            Symbol s = SymbolFactory.createSymbol(line);
            s.setXcoordPinned(true);
            s.setYcoordPinned(true);
            s.setXcoordIntValue(x);
            s.setYcoordIntValue(y);
            s.render(g2d, owner);
            s.setXcoordIntValue(x);

        } catch (Exception e) {
            // give up on this symbol
        }

    }

    private int getLegendWidth(JBasicPlot owner, FontMetrics fm)
    {
        int maxlen = 0;
        for (SymbolTextPair a_Text : legendEntries) {
            int advance = fm.stringWidth(a_Text.getText());
            maxlen = Math.max(maxlen, advance);
        }
        double minLineLen = 15.0;
        // millimeters
        return maxlen + owner.getUnitsMgr().getHorizUnitsToPixels(minLineLen) + 5;
    }

    private int getLegendHeight(FontMetrics fm)
    {
        int height = fm.getMaxAscent() + fm.getMaxDescent();
        return (legendEntries.size() + 1) * height;
    }

    private int getLegendLeft(JBasicPlot owner, int legendwidth)
    {
        int offset = owner.getUnitsMgr().getHorizUnitsToPixels(xOffset);
        return horAlign == HorizPinEdge.LEFT ? owner.getPlotLeft() + offset : owner.getPlotLeft() + owner.getPlotWidth() - offset - legendwidth;
    }

    private int getLegendTop(JBasicPlot owner, int legendheight)
    {
        int offset = owner.getUnitsMgr().getVertUnitsToPixels(yOffset);
        return vertAlign == VertPinEdge.TOP ? owner.getPlotTop() + offset : owner.getPlotTop() + owner.getPlotHeight() - offset - legendheight;
    }


    public void ChangePosition(JBasicPlot owner, Graphics graphics, double dx, double dy)
    {
        // Movement not allowed.
    }

    public static class SymbolTextPair {
        private final String text;
        private final SymbolDef symbolDef;

        public SymbolTextPair(String text, SymbolDef symbolDef)
        {
            this.text = text;
            this.symbolDef = symbolDef;
        }

        public String getText()
        {
            return text;
        }

        public SymbolDef getSymbolDef()
        {
            return symbolDef;
        }
    }
}
