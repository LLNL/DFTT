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
import java.io.Serializable;
import llnl.gnem.dftt.core.gui.plotting.PenStyle;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.PickTextPosition;

/**
 *
 * @author dodge1
 */
public class PickPrefs implements Serializable{

    private double height;
    private double heightInMillimeters; // Used for picks in record-section views.
    private Color color;
    private int width;
    private int textSize;
    private PickTextPosition textPosition;
    private PenStyle penStyle;
    static final long serialVersionUID = -7088607112685498455L;

    public PickPrefs() {
        height = 0.8;
        heightInMillimeters = 5.0;
        color = Color.black;
        width = 2;
        textSize = 10;
        textPosition = PickTextPosition.BOTTOM;
        penStyle = PenStyle.SOLID;
    }

    public PickPrefs(double pickHeight,
            double pickHeightInMillimeters,
            Color pickColor,
            int pickWidth,
            int pickTextSize,
            PickTextPosition position,
            PenStyle penStyle) {
        this.height = pickHeight;
        this.heightInMillimeters = pickHeightInMillimeters;
        this.color = pickColor;
        this.width = pickWidth;
        this.textSize = pickTextSize;
        this.textPosition = position;
        this.penStyle = penStyle;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public PickTextPosition getTextPosition() {
        return textPosition;
    }

    public void setTextPosition(PickTextPosition textPosition) {
        this.textPosition = textPosition;
    }

    public PenStyle getPenStyle() {
        return penStyle;
    }

    public void setPenStyle(PenStyle penStyle) {
        this.penStyle = penStyle;
    }

    public double getHeightInMillimeters() {
        return heightInMillimeters;
    }

    public void setHeightInMillimeters(double heightInMillimeters) {
        this.heightInMillimeters = heightInMillimeters;
    }
}
