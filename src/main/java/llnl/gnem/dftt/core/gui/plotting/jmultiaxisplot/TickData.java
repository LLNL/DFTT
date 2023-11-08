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
package llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot;

import java.awt.Color;
import java.awt.Font;
import llnl.gnem.dftt.core.gui.plotting.TickDir;

public class TickData {

    private int NumMinor = 4;
    private double MajorLen = 3;  // In physical units, e.g. mm
    private double MinorLen = 2;  // In physical units, e.g. mm
    private TickDir dir = TickDir.IN;
    private boolean visible = true;
    private String FontName = "Arial";
    private int FontSize = 10;
    private int FontStyle = Font.PLAIN;
    private Color FontColor = Color.black;


    public TickDir getDir() {
        return dir;
    }

    public void setDir(TickDir dir) {
        this.dir = dir;
    }

    public Font getFont() {
        return new Font(FontName, FontStyle, FontSize);
    }

    public void setFont(Font font) {
        FontName = font.getName();
        FontSize = font.getSize();
        FontStyle = font.getStyle();
    }

    public Color getFontColor() {
        return FontColor;
    }

    public void setFontColor(Color fontColor) {
        FontColor = fontColor;
    }

    public String getFontName() {
        return FontName;
    }

    public void setFontName(String fontName) {
        FontName = fontName;
    }

    public int getFontSize() {
        return FontSize;
    }

    public void setFontSize(int fontSize) {
        FontSize = fontSize;
    }

    public double getMajorLen() {
        return MajorLen;
    }

    public void setMajorLen(double majorLen) {
        MajorLen = majorLen;
    }

    public double getMinorLen() {
        return MinorLen;
    }

    public void setMinorLen(double minorLen) {
        MinorLen = minorLen;
    }

    public int getNumMinor() {
        return NumMinor;
    }

    public void setNumMinor(int numMinor) {
        NumMinor = numMinor;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

}
