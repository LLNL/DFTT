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
public class LabelPrefs implements Serializable {
    private String fontName = "Arial";
    private Color color = Color.black;
    private int fontSize = 12;
    private int fontStyle = Font.PLAIN;
    private double offset = 6;
    static final long serialVersionUID = 7757692269404713358L;


    /**
     *
     * @return the font to set
     */
    public Font getFont() {
        return new Font(fontName, fontStyle, fontSize);
    }

    /**
     *
     * @param font to set
     */
    public void setFont(Font font) {
        fontName = font.getName();
        fontSize = font.getSize();
        fontStyle = font.getStyle();
    }

    /**
     * @return the color
     */
    public Color getFontColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setFontColor(Color color) {
        this.color = color;
    }

    /**
     * @return the offset
     */
    public double getOffset() {
        return offset;
    }

    /**
     * @param offset the offset to set
     */
    public void setOffset(double offset) {
        this.offset = offset;
    }


}
