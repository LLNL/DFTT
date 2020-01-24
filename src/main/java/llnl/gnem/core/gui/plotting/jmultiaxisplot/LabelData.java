package llnl.gnem.core.gui.plotting.jmultiaxisplot;

import java.awt.Color;
import java.awt.Font;

public class LabelData {

    private String Text = "";
    private String FontName = "Arial";
    private Color color = Color.black;
    private int Size = 12;        // This is in units of points
    private int Style = Font.PLAIN;
    private boolean Visible = true;
    private double Offset = 6;  // Offset of label from axis line in physical units, e.g. mm

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Font getFont() {
        return new Font(FontName, Style, Size);
    }

    public void setFont(Font font) {
        FontName = font.getName();
        Size = font.getSize();
        Style = font.getStyle();
    }

    public double getOffset() {
        return Offset;
    }

    public void setOffset(double offset) {
        Offset = offset;
    }

    public int getSize() {
        return Size;
    }

    public void setSize(int size) {
        Size = size;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public boolean isVisible() {
        return Visible;
    }

    public void setVisible(boolean visible) {
        Visible = visible;
    }
}
