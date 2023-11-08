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
package llnl.gnem.dftt.core.gui.waveform.plotPrefs;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;

/**
 *
 * @author dodge1
 */
public class DrawingRegionPrefs implements Serializable{
    boolean drawBox = true;
    Color lineColor = Color.black;
    int lineWidth = 1;
    boolean fillRegion = true;
    Color backgroundColor = Color.white;
    static final long serialVersionUID = 4967515221780333339L;

    private Font font = null;
    private Color fontColor = null;

    /**
     * @return the drawBox
     */
    public boolean isDrawBox() {
        return drawBox;
    }

    /**
     * @param drawBox the drawBox to set
     */
    public void setDrawBox(boolean drawBox) {
        this.drawBox = drawBox;
    }

    /**
     * @return the lineColor
     */
    public Color getLineColor() {
        return lineColor;
    }

    /**
     * @param lineColor the lineColor to set
     */
    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    /**
     * @return the lineWidth
     */
    public int getLineWidth() {
        return lineWidth;
    }

    /**
     * @param lineWidth the lineWidth to set
     */
    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    /**
     * @return the fillRegion
     */
    public boolean isFillRegion() {
        return fillRegion;
    }

    /**
     * @param fillRegion the fillRegion to set
     */
    public void setFillRegion(boolean fillRegion) {
        this.fillRegion = fillRegion;
    }

    /**
     * @return the backgroundColor
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * @param backgroundColor the backgroundColor to set
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }



    /**
     * @return the color
     */
    public Color getFontColor() {
        if (fontColor == null) {
            fontColor = Color.black;
        }
        return fontColor;
    }

    /**
     * @param color the color to set
     */
    public void setFontColor(Color color) {
        this.fontColor = color;
    }

    /**
     *
     * @return the font to set
     */
    public Font getFont() {
        if (font == null) {
            font = new Font("Arial", Font.PLAIN, 14);
        }
        return font;
    }

    /**
     *
     * @param font to set
     */
    public void setFont(Font font) {
        this.font = font;
    }

}
