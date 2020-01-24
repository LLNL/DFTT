/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
