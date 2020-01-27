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
package llnl.gnem.core.gui.plotting.colormap;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Created by dodge1
 * Date: Jan 15, 2008
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 * Will not inspect for: MagicNumber
 */
public class ColorBar extends JPanel {
    
    private static final double EPSILON = 1e-24;

    private Colormap colormap;
    private int numBlocks = 50;
    private final int leftMargin = 10;
    private final int topMargin = 10;
    private final int barWidth = 25;
    private final int dashLength = 10;
    private FormatType formatType = FormatType.FIXED;

    /**
     * @return the formatType
     */
    public FormatType getFormatType() {
        return formatType;
    }

    /**
     * @param formatType the formatType to set
     */
    public void setFormatType(FormatType formatType) {
        this.formatType = formatType;
    }


    public enum FormatType { FIXED, SCIENTIFIC}


    public ColorBar(Colormap colormap)
    {
        this.setPreferredSize(new Dimension(100, 200));
        this.colormap = colormap;
        this.setBorder(BorderFactory.createLineBorder(Color.black));
    }

    public void setColormap( Colormap newMap )
    {
        colormap = newMap;
    }


    @Override
    public void paintComponent(Graphics g )
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        Dimension size = this.getSize();

        double barHeight = size.height - leftMargin - topMargin;
        double blockHeight = barHeight / numBlocks;
        double valueRange = colormap.getMax() - colormap.getMin();
        double step = valueRange / ( numBlocks-1);
        double blockTop = topMargin;
        drawBarLabel(g2d, blockTop, colormap.getMax());
        for( int j = 0; j < numBlocks; ++j ){
            double value = colormap.getMax() - j * step;
            Color color = colormap.getColor(value);
            g2d.setColor(color);
            g2d.fillRect(leftMargin,(int)Math.round(blockTop), barWidth,(int)Math.round(blockHeight));
            blockTop += blockHeight;
        }
        g2d.setColor(Color.black);
        g2d.draw(new Rectangle(leftMargin, topMargin, barWidth, (int) Math.round(barHeight)));

        if (FormatType.SCIENTIFIC == formatType) {
            // deal with very small positive values
            double min = colormap.getMin();
            if (min > 0.0 && min < EPSILON && step > EPSILON*1000.0) {
                drawBarLabel(g2d, blockTop, 0);
            } else {
                drawBarLabel(g2d, blockTop, colormap.getMin());
            }
        } else {
            drawBarLabel(g2d, blockTop, colormap.getMin());
        }
        
        drawBarLabel(g2d, topMargin + (numBlocks * blockHeight) / 2, (colormap.getMax() + colormap.getMin()) / 2);
    }

    private void drawBarLabel(Graphics2D g2d, double yPixelValue, double value)
    {
        g2d.setColor(Color.black);
        g2d.setStroke( new BasicStroke(1.5f));
        g2d.drawLine(leftMargin + barWidth, (int)Math.round(yPixelValue),
                leftMargin + barWidth +dashLength, (int)Math.round(yPixelValue));
        String text = formatType == FormatType.FIXED ? String.format("%6.0f", value ) : String.format("%.2g", value );
        FontMetrics fm = g2d.getFontMetrics();
        int ascent = fm.getAscent();
        int descent = fm.getDescent();
        int offset = (ascent-descent) /2;// - descent;
        g2d.drawString(text, leftMargin + barWidth +dashLength+1, (int)Math.round(yPixelValue)+ offset);
    }
}
