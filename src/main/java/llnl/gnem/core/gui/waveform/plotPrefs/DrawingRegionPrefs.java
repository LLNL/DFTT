/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform.plotPrefs;

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
