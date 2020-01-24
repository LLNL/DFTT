package llnl.gnem.core.gui.plotting.jmultiaxisplot;

import java.awt.Color;
import java.awt.Font;
import llnl.gnem.core.gui.plotting.TickDir;

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
