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
package llnl.gnem.core.gui.waveform.plotPrefs;

import java.awt.Color;
import java.io.Serializable;

/**
 *
 * @author dodge1
 */
public class AxisPrefs implements Serializable {
    private LabelPrefs labelPrefs;
    private TickPrefs tickPrefs;
    private Color color;
    private int penWidth;
    private boolean visible;
    static final long serialVersionUID = 7471394890458400754L;


    public AxisPrefs()
    {
        labelPrefs = new LabelPrefs();
        tickPrefs = new TickPrefs();
        color = Color.black;
        penWidth = 1;
        visible = true;
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * @return the labelPrefs
     */
    public LabelPrefs getLabelPrefs() {
        return labelPrefs;
    }

    /**
     * @return the penWidth
     */
    public int getPenWidth() {
        return penWidth;
    }

    /**
     * @param penWidth the penWidth to set
     */
    public void setPenWidth(int penWidth) {
        this.penWidth = penWidth;
    }

    /**
     * @return the tickPrefs
     */
    public TickPrefs getTickPrefs() {
        return tickPrefs;
    }

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
