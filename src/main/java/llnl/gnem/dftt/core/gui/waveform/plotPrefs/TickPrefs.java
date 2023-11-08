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
import llnl.gnem.dftt.core.gui.plotting.TickDir;

/**
 *
 * @author dodge1
 */
public class TickPrefs implements Serializable{
    private boolean visible = true;
    private TickDir direction = TickDir.IN;
    private String fontName = "Arial";
    private int fontSize = 10;
    private int fontStyle = Font.PLAIN;
    private Color color = Color.black;
    static final long serialVersionUID = -6254132982942263566L;


    /**
     * @return the direction
     */
    public TickDir getDirection() {
        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(TickDir direction) {
        this.direction = direction;
    }

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

    public Color getFontColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setFontColor(Color color) {
        this.color = color;
    }

//    /**
//     * @return the fontName
//     */
//    public String getFontName() {
//        return fontName;
//    }

//    /**
//     * @param fontName the fontName to set
//     */
//    public void setFontName(String fontName) {
//        this.fontName = fontName;
//    }

//    /**
//     * @return the fontSize
//     */
//    public int getFontSize() {
//        return fontSize;
//    }

//    /**
//     * @param fontSize the fontSize to set
//     */
//    public void setFontSize(int fontSize) {
//        this.fontSize = fontSize;
//    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

}
